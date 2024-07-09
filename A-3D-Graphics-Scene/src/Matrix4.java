/*
    Class to represent a 4-D matrix for transformations working in homogeneous coordinates. // no need to touch
 */
public class Matrix4 {

    static private final int SIZE = 4;  //x, y, z, w
    private final double[][] mx = new double[SIZE][SIZE];

    // Construct a 4X4 matrix with all 16 inputs arranged row major
    public Matrix4(double m00, double m01, double m02, double m03,
                   double m10, double m11, double m12, double m13,
                   double m20, double m21, double m22, double m23,
                   double m30, double m31, double m32, double m33) {
        mx[0][0] = m00;
        mx[0][1] = m01;
        mx[0][2] = m02;
        mx[0][3] = m03;
        mx[1][0] = m10;
        mx[1][1] = m11;
        mx[1][2] = m12;
        mx[1][3] = m13;
        mx[2][0] = m20;
        mx[2][1] = m21;
        mx[2][2] = m22;
        mx[2][3] = m23;
        mx[3][0] = m30;
        mx[3][1] = m31;
        mx[3][2] = m32;
        mx[3][3] = m33;
    }

    // Constructor with no parameters initializes with zero matrix
    public Matrix4() {
        this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    // Get a string representation so that we can use it with System.out.println
    public String toString() {
        String br = System.lineSeparator();
        String str = "";
        for (int i = 0; i < SIZE; ++i) {
            str += mx[i][0] + " " + mx[i][1] + " " + mx[i][2] + " " + mx[i][3] + br;
        }
        return str;
    }

    // Overloaded methods to carry out multiplications in various forms. This construct also allows
    // for the chaining of several multiplications
    public Point4 times(Point4 rhs) {
        return times(rhs, new Point4());
    }

    public Point4 times(Point4 rhs, Point4 res) {
        res.x = mx[0][0] * rhs.x + mx[0][1] * rhs.y + mx[0][2] * rhs.z + mx[0][3] * rhs.w;
        res.y = mx[1][0] * rhs.x + mx[1][1] * rhs.y + mx[1][2] * rhs.z + mx[1][3] * rhs.w;
        res.z = mx[2][0] * rhs.x + mx[2][1] * rhs.y + mx[2][2] * rhs.z + mx[2][3] * rhs.w;
        res.w = mx[3][0] * rhs.x + mx[3][1] * rhs.y + mx[3][2] * rhs.z + mx[3][3] * rhs.w;
        return res;
    }

    public Matrix4 times(Matrix4 rhs) {
        return times(rhs, new Matrix4());
    }

    public Matrix4 times(Matrix4 rhs, Matrix4 res) {
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                double sum = 0;
                for (int k = 0; k < SIZE; ++k) {
                    sum += mx[i][k] * rhs.mx[k][j];
                }
                res.mx[i][j] = sum;
            }
        }
        return res;
    }

    // Some static methods to work as convenient factory functions

    // Identity matrix
    public static Matrix4 createIdentity() {
        return new Matrix4(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);
    }

    // Get a scaling matrix with scale factors in x, y, and z
    public static Matrix4 createScale(double sx, double sy, double sz) {
        return new Matrix4(
                sx, 0, 0, 0,
                0, sy, 0, 0,
                0, 0, sz, 0,
                0, 0,  0, 1);
    }

    // Get a rotation matrix around x-axis (or in yz plane)
    public static Matrix4 createRotationX(double ang) {
        double cos = Math.cos(ang);
        double sin = Math.sin(ang);
        return new Matrix4(
                1, 0, 0, 0,
                0, cos, -sin, 0,
                0, sin, cos, 0,
                0, 0, 0, 1);
    }

    // Get a rotation matrix around y-axis (or in xz plane)
    public static Matrix4 createRotationY(double ang) {
        double cos = Math.cos(ang);
        double sin = Math.sin(ang);
        return new Matrix4(
                cos, 0, sin, 0,
                0, 1, 0, 0,
                -sin, 0, cos, 0,
                0, 0, 0, 1);
    }

    // Get a rotation matrix around z-axis (or in xy plane)
    public static Matrix4 createRotationZ(double ang) {
        double cos = Math.cos(ang);
        double sin = Math.sin(ang);
        return new Matrix4(
                cos, -sin, 0, 0,
                sin, cos, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);
    }

    // Get a displacement matrix for translations in x, y, and z directions
    public static Matrix4 createDisplacement(double dx, double dy, double dz) {
        return new Matrix4(
                1, 0, 0, dx,
                0, 1, 0, dy,
                0, 0, 1, dz,
                0, 0, 0, 1);
    }

    // One-point perspective transformation with projection plane parallel
    // to the xy plane and located at z=d
    public static Matrix4 createPerspective1pt(double d){
        return new Matrix4(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 1/d, 0);
    }

}
