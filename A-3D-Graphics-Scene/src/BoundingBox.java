/*
    Use this to represent a two dimensional sub raster for an image.

    The idea is that we calculate this for a given surface to get a 2D pixel
    bounding box. Then loop over all pixels within the subraster only (instead of
    the whole image)
 */
class RasterMap {

    // Store the integer pixel locations of the corners
    int x1, x2, y1, y2;

    public String toString() {
        return x1 + " " + x2 + " " + y1 + " " + y2;
    }

    private static int clipFloor(double v) {
        return Math.max((int) Math.floor(v), 0);
    }

    private static int clipCeil(double v, int N) {
        return Math.min((int) Math.ceil(v), N);
    }

    /*
    Get a 2D raster map from a 3d bounding box. This will give us the maximum extent of the 3d
    surface scanned onto the image
     */
    public static RasterMap fromBB(BoundingBox bb, int npixx, int npixy) {
        RasterMap r = new RasterMap();
        r.x1 = clipFloor(bb.x1);
        r.x2 = clipCeil(bb.x2, npixx);
        r.y1 = clipFloor(bb.y1);
        r.y2 = clipCeil(bb.y2, npixy);
        return r;
    }

}

/*
    Three-dimensional bounding box providing the corners of the smallest box that
    completely surrounds a surface
 */
class BoundingBox {

    static private double min8(double v0, double v1, double v2, double v3, double v4, double v5, double v6, double v7) {
        return Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(Math.min(v0, v1), v2), v3), v4), v5), v6), v7);
    }

    static private double max8(double v0, double v1, double v2, double v3, double v4, double v5, double v6, double v7) {
        return Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(v0, v1), v2), v3), v4), v5), v6), v7);
    }

    public double x1, x2, y1, y2, z1, z2;

    // Use this to flag if any points are behind the camera
    public boolean anyNegW;

    public BoundingBox() {
        anyNegW = false;
    }

    public BoundingBox(double x1, double x2, double y1, double y2, double z1, double z2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.z1 = z1;
        this.z2 = z2;
    }

    // Helper function to apply a projection on an (x,y,z) triplet
    private Point4 calc(Matrix4 pmx, double x, double y, double z) {
        Point4 p = pmx.times(Point4.createPoint(x, y, z));
        if (p.w < 0) anyNegW = true;
        p.homogenize();  // Must homogenize
        return p;
    }

    /*
    Use the given projection matrix to transform the bounding box corners to a position on the view plane
     */
    public BoundingBox transform(Matrix4 pmx) {
        Point4 p0 = calc(pmx, x1, y1, z1);
        Point4 p1 = calc(pmx, x1, y1, z2);
        Point4 p2 = calc(pmx, x1, y2, z1);
        Point4 p3 = calc(pmx, x1, y2, z2);
        Point4 p4 = calc(pmx, x2, y1, z1);
        Point4 p5 = calc(pmx, x2, y1, z2);
        Point4 p6 = calc(pmx, x2, y2, z1);
        Point4 p7 = calc(pmx, x2, y2, z2);

        BoundingBox bb = new BoundingBox();
        bb.anyNegW = this.anyNegW;
        bb.x1 = min8(p0.x, p1.x, p2.x, p3.x, p4.x, p5.x, p6.x, p7.x);
        bb.x2 = max8(p0.x, p1.x, p2.x, p3.x, p4.x, p5.x, p6.x, p7.x);
        bb.y1 = min8(p0.y, p1.y, p2.y, p3.y, p4.y, p5.y, p6.y, p7.y);
        bb.y2 = max8(p0.y, p1.y, p2.y, p3.y, p4.y, p5.y, p6.y, p7.y);
        bb.z1 = min8(p0.z, p1.z, p2.z, p3.z, p4.z, p5.z, p6.z, p7.z);
        bb.z2 = max8(p0.z, p1.z, p2.z, p3.z, p4.z, p5.z, p6.z, p7.z);
        return bb;
    }

    public String toString() {
        return x1 + " " + x2 + " " + y1 + " " + y2 + " " + z1 + " " + z2;
    }
}
