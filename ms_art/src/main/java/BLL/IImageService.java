package BLL;

import java.awt.image.BufferedImage;

public interface IImageService<T>{
    BufferedImage getImage(int width, int height, T generation);
}
