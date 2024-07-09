import java.awt.*;

/*
    Class hierarchy  to deal with the two ways of getting a surface colour.
 */

abstract class SurfaceColour {
    // Get colour for given "texture" coordinates
    abstract Color pickColour(double u, double v);
}

// Use if the colour is the same across the surface. Just need to specify the colour
class UniformColour extends SurfaceColour {

    private Color colour;
    UniformColour(Color c) {this.colour = c;}

    @Override
    Color pickColour(double u, double v) {
        return colour;
    }
}

class TextureColour extends SurfaceColour {

    private TextureMap texMap;
    TextureColour(TextureMap t) {
        texMap = t;
    }
    @Override
    Color pickColour(double u, double v) {
        return texMap.pickColour(u, v);
    }
}
