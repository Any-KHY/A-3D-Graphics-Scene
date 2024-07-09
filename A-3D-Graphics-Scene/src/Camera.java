import java.awt.image.BufferedImage;

/*
    Class to represent a virtual "camera". Quite a lot of components we need to provide
 */
public class Camera {

    // Actual image formed by the camera - this is just the viewplane extent divided into pixels
    public BufferedImage image;
    // Need a zBuffer to build the image
    public ZBuffer zBuffer;
    // Placement of camera within the scene
    public Placement place;
    // Camera projection plane
    public ViewPlane viewPlane;
    // Use this as the "viewpoint"
    public Point4 p0World;
    // Project from view plane coordinate frame to pixel locations
    public Matrix4 tProj;

    private Camera() {}

    /*
    Get a projection matrix to allow conversion from local coordinates of a
    given surface to viewplane/image coordinates
     */
    Matrix4 getProjection(Placement placeModel) {
         return tProj.times(placeModel.tLW);
    }

    /*
    Set up camera placement for camera position, target position, and orientation
     */
    void rePoint(Point4 pC, Point4 pT, Point4 vU) {
        this.place = Placement.placeCamera(pC, pT, vU);
        this.p0World = place.tLW.times(viewPlane.pOrigin);
        tProj = viewPlane.tProj.times(place.tWL);
    }

    /*
    Get a ray object from camera viewpoint through the given pixel location
     */
    Ray makeRay(double xpix, double ypix) {
        Point4 p = viewPlane.toScreen(xpix, ypix);
        Point4 pWorld = place.tLW.times(p);
        return new Ray(p0World, pWorld);
    }

    /*
    Set the image pixel - need to check with the z-buffer
     */
    void setPixel(int x, int y, ShadeRecord sr) {
        if (sr.isShaded) {
            Point4 pS = place.toLocal(sr.pSurface);
            if (zBuffer.update(x, y, pS.z))
                image.setRGB(x, y, sr.colour.getRGB());
        }
    }

    /*
    Factory function to get a "standard" camera model
     */
    public static Camera standardCamera(double fovy, int npixx, int npixy) {
        Camera camera = new Camera();
        camera.image = new BufferedImage(npixx, npixy, BufferedImage.TYPE_INT_ARGB);
        camera.viewPlane = new ViewPlane(fovy, npixx, npixx);
        camera.zBuffer = new ZBuffer(npixx, npixy);
        return camera;
    }
}
