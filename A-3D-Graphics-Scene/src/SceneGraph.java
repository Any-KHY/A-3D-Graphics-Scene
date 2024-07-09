import java.util.ArrayList;
import java.util.Collections;

/*
    This class manages the list of surfaces to be rendered
 */

public class SceneGraph {

    // ALL surfaces in the scene end up in this list
    public ArrayList<ThreeDSurface> surfaces;

    SceneGraph() {surfaces = new ArrayList<>();}

    // Three overloaded methods to add new surfaces to the current list
    void add(ThreeDSurface s) {  //single surface
        surfaces.add(s);
    }
    void add(ThreeDSurface[] s) {
        Collections.addAll(surfaces, s);
    }
    void add(ArrayList<ThreeDSurface> s) {  //object
        surfaces.addAll(s);
    }

    /*
    Render the surfaces to the point of view of the camera as illuminated by a point
    source at the given position
     */
    void render (Camera camera, Point4 pLightW) {
        int npixx = camera.image.getWidth();
        int npixy = camera.image.getHeight();

        for (ThreeDSurface surface : surfaces) {
            Matrix4 pmx = camera.getProjection(surface.placement);
            RasterMap rm = surface.getRasterMap(pmx, npixx, npixy);
            System.out.println("Sub-raster: " + rm);

            for (int y = rm.y1; y < rm.y2; ++y) {
                for (int x = rm.x1; x < rm.x2; ++x) {
                    Ray rayW = camera.makeRay(x, y);
                    ShadeRecord sr = surface.shadeIt(rayW, pLightW, surfaces);  // ray shooting
                    camera.setPixel(x, y, sr);
                }
            }
        }

    }

}
