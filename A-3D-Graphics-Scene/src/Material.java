/*
    This class handles the reflective properties of the surface.

    Makes sense to do it this way. How the shading is done, does depend on the material property
 */

// Ray tracing Lecture - Phong Reflection model

public class Material {

    // Parameters of the Phong reflection model as described in the lectures
    private double alpha, beta, nShiny;

    Material(double a, double b, double n) {
        alpha = a; // ambient light fraction
        beta = b;   // fraction of specular reflection
        nShiny = n; // shining value
    }

    public double calculate(Point4 N, Point4 L, Point4 V, double ff) {
        // N = normal vector (i.e. hitrecord.vNormal ), L = Light source vector, V = view poit vector, ff = shadow effect
        N.normalize();
        L.normalize();
        V.normalize();

        // Diffuse reflection
        double diffuse = (Point4.dot(N, L)>0) ? Point4.dot(N, L) : 0;

        // Specular reflection
        // Reflection direction(R) = (2*(N.L))N-L (Lecture I & S P.24)
        double dotProduct = 2 * Point4.dot(N, L);
        double Rx = N.x * dotProduct;
        double Ry = N.y * dotProduct;
        double Rz = N.z * dotProduct;
        Point4 R = Point4.createVector(Rx,Ry,Rz).minus(L); //reflection direction

        double dotProVR = (Point4.dot(V, R)>0) ? Point4.dot(V, R) : 0;

        double fReflect = (1-beta)*diffuse + beta*(Math.pow(dotProVR,nShiny));

        double fShading = (alpha + ((1 - alpha) * fReflect)) * ff;

        // f = Shading factor which should in btw 0 to 1, i.e. 0 <= f <=1
        // Math.max returns 0 if fShading<0, Math.min returns 1 if fShading>1
        return Math.min(1.0, Math.max(0.0, fShading));
    }
}
