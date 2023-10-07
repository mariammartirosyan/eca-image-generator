package BLL;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GeometricImageService implements IImageService<List<int[]>> {
    @Override
    public BufferedImage getImage(int width, int height, List<int[]> generation) {
        BufferedImage image = new BufferedImage(
                width, height, BufferedImage.TYPE_INT_RGB );
        File file = new File("geometric.png");

        for(int i= 0; i<height;i++){
            for(int j=0; j<width;j++){
                if(i<generation.size()){
                    if(generation.get(i)[j]==1){
                        image.setRGB(j, i, Color.black.getRGB());
                    }
                    else{
                        image.setRGB(j, i, Color.white.getRGB());
                    }
                }
                else{
                    image.setRGB(j, i, Color.white.getRGB());
                }
            }
        }
        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }
}
