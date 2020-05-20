package background;

import java.awt.*;
import java.awt.image.BufferedImage;

public class filter {
    public BufferedImage convertToBW(BufferedImage image){
        BufferedImage result = new BufferedImage(image.getWidth(),image.getHeight(),image.getType());
        Graphics2D G = result.createGraphics();
        G.drawImage(image, 0, 0, null);
        G.dispose();
        int width = image.getWidth();
        int height = image.getHeight();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int p = image.getRGB(j,i);

                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;

                int avg = (r+g+b)/3;
                p = (a<<24) | (avg<<16) | (avg<<8) | avg;

                result.setRGB(j,i,p);
            }
        }
        return result;
    }
}
