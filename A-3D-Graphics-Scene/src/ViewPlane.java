/*
    Class to represent the view plane for the virtual camera. Here the view plane is
    modelled as a planar surface parallel to the xy plane (in camera coordinates) and located
    at a distance on the optical axis (ie z-axis) of the camera. The positive optical axis points
    towards the scene. The projection employs a 1-point perspective.

    The view plane is divided into pixels
 */
public class ViewPlane {

    private static final double dScreen = 1.0;

    // Projection matrix from camera coordinates onto the view plane
    public Matrix4 tProj;

    private final double pixScale;
    private final double x1, y1;

    public final Point4 pOrigin;

    /*
    Constructor sets up view plane according to field of view in y-direction and the numbers
    of pixels in the x and y axes
     */
    ViewPlane(double fovy, int numPixX, int numPixY) {
        pOrigin = Point4.createPoint(0,0,0);
        double aspectr = (double) (numPixX - 1) / (double) (numPixY - 1);

        // Projection plane divided as follows
        double halfy = dScreen * Math.tan(0.5 * fovy);
        double halfx = aspectr * halfy;
        x1 = -halfx;
        y1 = -halfy;
        pixScale = 2 * halfy / (numPixY - 1);
        double invScale = 1.0 / pixScale;

        // Projection matrix converts from camera to view plane with the result in pixel location
        Matrix4 P = Matrix4.createPerspective1pt(dScreen);
        Matrix4 S = Matrix4.createScale(invScale, invScale, invScale);
        Matrix4 D = Matrix4.createDisplacement(halfx, halfy, -dScreen);
        tProj = S.times(D).times(P);
    }

    // Given pixel location, get a 3d point in camera coordinates
    Point4 toScreen(double xpix, double ypix) {
        return Point4.createPoint(x1 + xpix * pixScale, y1 + ypix * pixScale, dScreen);
    }
}