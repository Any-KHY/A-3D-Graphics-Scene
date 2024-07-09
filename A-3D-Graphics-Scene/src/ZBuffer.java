/*
    Class to represent a "z-buffer" for dealing with the visibility issue.

    All done for you, but make sure you understand how it works
 */

public class ZBuffer {

    // Maximum possible depth - some ridiculously large value
    private static final double zMax = 1e10;

    // Store the pixel depths in a floating point double 2d array
    private double[][] buffer;

    private int numPixX, numPixY;

    // Constructor initializes the buffer with each value being the maximum depth
    ZBuffer(int nX, int nY) {
        buffer = new double[nX][nY];
        for (int x = 0; x < nX; ++x) {
            for (int y = 0; y < nY; ++y) buffer[x][y] = zMax;
        }
        numPixX = nX;
        numPixY = nY;
    }

    // Compare z with current depth and update if necessary
    public boolean update(int x, int y, double zValue) {
        if (zValue < buffer[x][y]) {
            buffer[x][y] = zValue;
            return true;
        }
        else
            return false;
    }

}
