package BLL;

import BLL.CellCalculationJob;

import java.awt.image.BufferedImage;
import java.util.concurrent.*;

public class ColorfulImageGenerator extends ImageGenerator {
    private int liveCellStatesNumber=3;
    private int threadCount;
    private BlockingQueue<int[]> inputQueue;
    private BlockingQueue<int[]> outputQueue;
    private ExecutorService executorService;
    public ColorfulImageGenerator (int width, int height, int generationCount, int threadCount, IRuleApplier<int[],int[][]> ruleApplier, IImageService<int[][]> imageService){
        super(width,height,generationCount,ruleApplier,imageService);
        this.threadCount = threadCount;
        inputQueue = new LinkedBlockingQueue<int[]>((width - 2)*(height-2));
        outputQueue = new LinkedBlockingQueue<int[]>((width - 2)*(height-2));
    }
    @Override
    public BufferedImage generate() throws InterruptedException {
        var finalGeneration = computeGenerations();
        var image = imageService.getImage(width, height,finalGeneration);
        return image;
    }
    private int[][] computeGenerations() throws InterruptedException {
        int[][] currentGeneration = new int[height][width];
        int[][] nextGeneration = null;

        for(int i=1;i<height-1;i++){
            for(int j=1;j<width-1;j++){
                //add random states to cells for the initial generation, ignore edges
                currentGeneration[i][j]= HelperService.getRandomNumber(0,liveCellStatesNumber);
                //fill inputQueue with coordinates
                inputQueue.put(new int[]{i,j});
            }
        }

        for(int n=0;n<generationCount-1;n++){
            nextGeneration = getNextGeneration(currentGeneration);
            currentGeneration = nextGeneration;
        }
        return nextGeneration;
    }

    private int[][] getNextGeneration(int[][] currentGeneration) {
        executorService = Executors.newFixedThreadPool(threadCount);
        int[][] nextGeneration = new int[height][width];
        for (int k = 0; k < threadCount; k++) {
            executorService.execute(new CellCalculationJob<int[],int[][]>(currentGeneration,nextGeneration,ruleApplier,inputQueue,outputQueue));
        }
        HelperService.awaitTerminationAfterShutdown(executorService);
        var temp = inputQueue;
        inputQueue = outputQueue;
        outputQueue = temp;
        return nextGeneration;
    }

}
