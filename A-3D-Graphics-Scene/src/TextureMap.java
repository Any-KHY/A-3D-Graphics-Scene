/*
    Class to implement a texture mapping procedure
 */

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TextureMap {
    private BufferedImage textureImg = null;

    public TextureMap(String imgPath) throws IOException { // constructor with a path
        try {
            textureImg = ImageIO.read(new File(imgPath));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /*
    Sample the texture image at the given texel values (u,v)

    (u, v) are NORMALIZED, i.e. 0<=u<=1, 0<=v<=1

    Complete the implementation of pickColour().

    Here it just returns a default colour

     */

    public Color pickColour(double u, double v) {

        // Simple sampling - get pixel coordinates using the nearest-neighbor
        int x = (int) Math.round(u * (textureImg.getWidth()-1));
        int y = (int) Math.round(v * (textureImg.getHeight()-1));
        Color sampledColour = new Color(textureImg.getRGB(x, y));

        return sampledColour;
    }

}
