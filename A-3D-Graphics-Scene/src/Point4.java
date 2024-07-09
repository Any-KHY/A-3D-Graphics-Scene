/*
    Class to represent a 4-D location in projective space, i.e. homogeneous coordinates // no need to touch
 */
public class Point4 {

    public double x;
    public double y;
    public double z;
    public double w;

    public Point4() {
        x = y = z = w = 0.0;
    }

    public Point4(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    // to ensure that the point remains in homogeneous coordinates, positions: w = 1, direction : w,=,0
    public void homogenize() {
        double wa = this.w;
        this.x /= wa;
        this.y /= wa;
        this.z /= wa;
        this.w /= wa;
    }

    // String representation
    public String toString() { // just for debugs
        String br = System.lineSeparator();
        return x + " " + y + " " + z + " " + w;
    }

    public void normalize() {
        double length = Math.sqrt(x * x + y * y + z * z);
        x /= length;
        y /= length;
        z /= length;
    }

    public Point4 addVector(Point4 rhs, double t) { // t : scaling factor
        return new Point4(
                this.x + t * rhs.x,
                this.y + t * rhs.y,
                this.z + t * rhs.z,
                this.w + t * rhs.w
        );
    }

    public Point4 minus(Point4 rhs) {
        return minus(rhs, new Point4());
    }

    public Point4 minus(Point4 rhs, Point4 res) { /// res = result
        res.x = this.x - rhs.x;
        res.y = this.y - rhs.y;
        res.z = this.z - rhs.z;
        res.w = this.w - rhs.w;
        return res;
    }

    // Only makes sense if p1 and p2 are vectors, ie w=0 (as when w = 1, it's a position)
    public static double dot(Point4 p1, Point4 p2) {
        return p1.x * p2.x + p1.y * p2.y + p1.z * p2.z;
    }

    public static Point4 cross(Point4 p1, Point4 p2) {
        return new Point4(
                p1.y * p2.z - p1.z * p2.y,
                p1.z * p2.x - p1.x * p2.z,
                p1.x * p2.y - p1.y * p2.x,
                0
        );
    }

    public static Point4 times(Point4 p, double multiplier) {
        return new Point4( p.x * multiplier, p.y * multiplier, p.z * multiplier, 0);
    }

    public static Point4 createPoint(double x, double y, double z) {
        return new Point4(x, y, z, 1);
    } // for position

    public static Point4 createVector(double x, double y, double z) {
        return new Point4(x, y, z, 0);
    }
}
