/*
    Class to represent a Ray
 */
public class Ray {
    public Point4 pOrigin; //P0
    public Point4 pDest; //P1

    public Ray() {}
    public Ray(Point4 p0, Point4 p1) {
        pOrigin = p0;
        pDest = p1;
    }

    public Point4 calculate(double t) {
        double t1 = 1 - t;
        return Point4.createPoint(
                t1 * pOrigin.x + t * pDest.x,
                t1 * pOrigin.y + t * pDest.y,
                t1 * pOrigin.z + t * pDest.z);
    }

    // Use this to transform the ray into another coordinate system
    public static Ray transform(Ray ray, Matrix4 tmx) {
        Point4 p0 = tmx.times(ray.pOrigin);
        Point4 p1 = tmx.times(ray.pDest);
        Ray rayT = new Ray(p0, p1);
        return rayT;
    }
}
