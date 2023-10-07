package BLL;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GeometricImageGenerator extends ImageGenerator {
    private int threadCount;
    private ExecutorService executorService;
    private BlockingQueue<Integer> inputQueue;
    private BlockingQueue<Integer> outputQueue;

    public GeometricImageGenerator(int width, int height, int generationCount, int threadCount, IRuleApplier<Integer, int[]> ruleApplier, IImageService<List<int[]>> imageService){
        super(width, height, generationCount, ruleApplier, imageService);
        this.threadCount = threadCount;
        this.executorService = Executors.newFixedThreadPool(threadCount);

        inputQueue = new LinkedBlockingQueue<Integer>(width - 2);
        outputQueue = new LinkedBlockingQueue<Integer>(width - 2);
        for (int i = 1; i < width - 1; i++) {
            inputQueue.add(i);
            //test
        }

    }

    @Override
    public BufferedImage generate() throws InterruptedException {
        var generations = computeGenerations();
        var image = imageService.getImage(width, height, generations);
        return image;
    }

    private List<int[]> computeGenerations() throws InterruptedException {
        List<int[]> generations = new ArrayList<int[]>();
        int[] currentGeneration = new int[width];
        currentGeneration[width / 2] = 1;
        generations.add(currentGeneration);

        for (int i = 0; i < generationCount - 1; i++) {
            int[] nextGeneration = getNextGeneration(currentGeneration);
            generations.add(nextGeneration);
            currentGeneration = nextGeneration;
        }
        return generations;
    }

    private int[] getNextGeneration(int[] currentGeneration) throws InterruptedException {
        executorService = Executors.newFixedThreadPool(threadCount);
        int[] nextGeneration = new int[currentGeneration.length];

        try {
            for (int i = 0; i < threadCount; i++) {
                executorService.execute(new CellCalculationJob<Integer,int[]>(currentGeneration,nextGeneration,ruleApplier,inputQueue,outputQueue));
            }
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
        }

        HelperService.awaitTerminationAfterShutdown(executorService);

        var temp = inputQueue;
        inputQueue = outputQueue;
        outputQueue = temp;
        return nextGeneration;

    }



}
