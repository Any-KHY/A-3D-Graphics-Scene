import java.awt.*;
import java.util.ArrayList;

/*
    Encapsulate information that is not when shading a point on the surface
 */
class ShadeRecord {
    Point4 pSurface;  // Surface position - need this for z-buffering
    Color colour;     // Final colour at that surface point  after applying shading
    boolean isShaded;
}

/*
    Class to encapsulate the properties of a surface that is placed in a 3D scene
 */
public class ThreeDSurface {

    private static final boolean SHADING_ON = true;
    private static final boolean FEEL_SHADOWS = true;
    // The type of geometry
    SurfaceGeometry surfaceGeometry;

    // The material reflective properties of the surface
    Material material;

    // The surface colour model: either uniform or from a texture map
    SurfaceColour surfaceColour;

    // The placement of the surface within the scene
    Placement placement;

    public ThreeDSurface(SurfaceGeometry sg, Material m, SurfaceColour sc, Placement p) {
        this.surfaceGeometry = sg;
        this.surfaceColour = sc;
        this.material = m;
        this.placement = p;
    }

    /*
    Given a Ray in world coordinates, calculate final shaded colour
     */
    public static int clip01(int c, double f) {
        return (int) Math.round(f * c);
    }

    public static Color rescaleColour(Color c, double f) {
        int r = clip01(c.getRed(), f);
        int g = clip01(c.getGreen(), f);
        int b = clip01(c.getBlue(), f);
        return new Color(r, g, b);
    }

    public RasterMap getRasterMap(Matrix4 pmx, int npixx, int npixy) {
        BoundingBox bb = surfaceGeometry.getBB().transform(pmx);
        RasterMap rm = RasterMap.fromBB(bb, npixx, npixy);
        if (bb.anyNegW) {
            rm.x1 = 0;
            rm.x2 = npixx;
            rm.y1 = 0;
            rm.y2 = npixy;
        }
        return rm;
    }


    class Shadows {

        static Boolean inShadow(Ray ray, HitRecord hitPt){
            // Distance of the ray from light source to target surface(Shadow ray)
            double dx = ray.pDest.x - ray.pOrigin.x;
            double dy = ray.pDest.y - ray.pOrigin.y;
            double dz = ray.pDest.z - ray.pOrigin.z;
            double dMax = Math.sqrt(dx*dx + dy*dy + dz*dz);

            return hitPt.tHit>=0.000001 && hitPt.tHit<dMax; // a tiny value to avoid noise
        }

        public static double feel(ArrayList<ThreeDSurface> surfaces, Point4 pSurfaceW, Point4 pLightW) {

            for (ThreeDSurface surface : surfaces) {
                HitRecord hit = new HitRecord();
                // shadow Ray in local coordinates of the surface
                Ray ray = Ray.transform(new Ray(pSurfaceW, pLightW), surface.placement.tWL);
                 if(surface.surfaceGeometry.shoot(ray,hit)){ // it hits sth
                     if(inShadow(ray,hit)) { // check if it's in between target object and light source
                        return 0.4;
                    }
                }
            }
            return 1.0; // no shadow
        }
    }

    public ShadeRecord shadeIt(Ray rayW, Point4 pLightW, ArrayList<ThreeDSurface> surfaces) {

        ShadeRecord sr = new ShadeRecord();

        // Ray in local coordinates of the surface
        Ray ray = Ray.transform(rayW, placement.tWL);

        HitRecord hit = new HitRecord();

        surfaceGeometry.shoot(ray, hit); // update of hit record
        sr.isShaded = hit.isHit;

        if (hit.isHit) {
            Point4 pSurfaceW = placement.toWorld(hit.pSurface);
            Point4 vNormalW  = placement.toWorld(hit.vNormal);
            Point4 vLightW   = pLightW.minus(pSurfaceW);
            Point4 vViewW    = rayW.pOrigin.minus(pSurfaceW);

            // Shadow form factor
            double ff = FEEL_SHADOWS ? Shadows.feel(surfaces, pSurfaceW, pLightW) : 1.0;

            // Shading factor
            double fShade = SHADING_ON ? material.calculate(vNormalW, vLightW, vViewW, ff) : 1.0;

            Color c1 = surfaceColour.pickColour(hit.u, hit.v);
            sr.colour = rescaleColour(c1, fShade);
            sr.pSurface = placement.toWorld(hit.pSurface);
            //System.out.println(fShade + " " + sr.isShaded + " " + sr.pSurface);
        }
        return sr;
    }
}
