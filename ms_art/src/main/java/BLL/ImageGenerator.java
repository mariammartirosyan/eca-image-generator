package BLL;

import BLL.IImageService;

import java.awt.image.BufferedImage;

public abstract class ImageGenerator {
    protected int width;
    protected int height;
    protected int generationCount;
    protected IRuleApplier ruleApplier;
    protected IImageService imageService;

    public ImageGenerator (int width, int height, int generationCount, IRuleApplier ruleApplier, IImageService imageService){
        this.width = width;
        this.height = height;
        this.generationCount = generationCount;
        this.ruleApplier = ruleApplier;
        this.imageService = imageService;
    }

    public abstract BufferedImage generate() throws InterruptedException;
}
