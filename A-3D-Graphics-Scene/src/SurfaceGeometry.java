/*
    Deal with Ray intersection geometry for various surface types

    Work in local coordinates of the surface
 */

/*
    Use this to record all the necessary information from the ray shooting
    and ray-surface intersection calculations
 */
class HitRecord {
    public Point4 pSurface;// x,y,z of intersection
    public Point4 vNormal; // surface normal(direction vector)
    public double u, v; // Texture mapping coordinates
    public double tHit; // t value for position on ray(distance)
    public boolean isHit;
    public HitRecord() {
        pSurface = Point4.createPoint(0,0,0);
        vNormal = Point4.createVector(0,0,0);
    }
}

public abstract class SurfaceGeometry {

    protected final static double TINY = 0.01;

    /*
    Implementation details of these abstract methods will depend upon the
    type geometry of the surface
     */

    // Shoot a ray onto the surface
    public abstract boolean shoot(Ray ray, HitRecord hit);

    // Get the smallest box that completely surrounds the surface
    public abstract BoundingBox getBB();
 }

// Shooting to an infinite plane
class Plane extends SurfaceGeometry {

    // a(x-xc) + b(y-yc) + c(z-zc) = 0 =? N(P-Pc) = 0
    private final Point4 pOrigin = Point4.createPoint(0,0,0);

    public Plane() {
    }

    @Override
    public boolean shoot(Ray ray, HitRecord hit) {

        // thit = N(Pc-P0)/N(P1-P0) => P surface = P0+thit(P1-P0)
        // Local coordinates
        Point4 p0 = ray.pOrigin;
        Point4 p1 = ray.pDest; // P(corner)

        Point4 U = p1.minus(p0); // (Pc-P0)
        Point4 V = pOrigin.minus(p0); //(P1-P0)

        double t = V.z / U.z; //dot product of points and Normals

        hit.vNormal = Point4.createVector(0, 0, 1);
        hit.pSurface = ray.calculate(t); // intersect
        hit.u = hit.pSurface.x + 0.5;
        hit.v = 0.5 - hit.pSurface.y;
        hit.tHit = t;
        hit.isHit = true;
        return true;
    }

    @Override
    public BoundingBox getBB() {
        return null;
    }
}

// Shooting onto a square - one type of bounded planar region
class Square extends Plane {
    private final static double h = 0.5;    // half width
    public boolean shoot(Ray ray, HitRecord hit) {
        super.shoot(ray, hit);
        hit.isHit = hit.tHit > 0 && hit.u >= 0 && hit.u <= 1 && hit.v >= 0 && hit.v <= 1;
        return hit.isHit;
    }
    public BoundingBox getBB() {
        return new BoundingBox(-h, h,-h, h,-TINY,TINY);
    }   // i.e. sth like hit box in game!!!!
}

/*
Provide an implementation for ray shooting onto a Spherical surface
 */

class Sphere extends SurfaceGeometry {

    private Point4 center = Point4.createPoint(0,0,0);
    private final static double radius = 1;

    @Override
    public boolean shoot(Ray ray, HitRecord hit) {

        // Local coordinates
        Point4 p0 = ray.pOrigin;
        Point4 p1 = ray.pDest;

        Point4 U = p1.minus(p0);
        Point4 V = p0.minus(center);

        // surface : (P-Pc)^2 = r^2 => (P-Pc)^2 - r^2 = 0 => (U.U)t^2 + 2(U.V)t + V*V - r^2 = 0
        double a = Point4.dot(U,U);
        double b = 2*Point4.dot(U,V);
        double c = Point4.dot(V,V) - (radius*radius);

        // D = (b^2-4ac)
        double D = (b*b) - (4*a*c);

        // If the D<0, no real solutions (no intersection), D = 0 (1 solution, i.e grazes), D >0 (2 solutions, i.e in and out)
        if (D < 0) {
            hit.isHit = false;
        } else {
            // intersection (possible solutions for t)
            double t1 = (-b + Math.sqrt(D)) / (2 * a);
            double t2 = (-b - Math.sqrt(D)) / (2 * a);

            // the closer intersection
            double t = (t1 < t2) ? t1 : t2; // should return t1 even if t1 == t2

            hit.pSurface = ray.calculate(t);
            hit.vNormal = hit.pSurface.minus(center);
            hit.vNormal.normalize();
            hit.tHit = t;

            // Lecture Mapping P.13
            hit.u = 0.5 * (1.0+ (Math.atan2(hit.pSurface.y,hit.pSurface.x) / Math.PI) );
            hit.v = 0.5 + (Math.asin(hit.pSurface.z) / Math.PI);

            hit.isHit = true;
        }

        return hit.isHit;
    }

    @Override
    public BoundingBox getBB() {
        return new BoundingBox(-radius, radius,-radius, radius,-radius, radius);
    }
}
