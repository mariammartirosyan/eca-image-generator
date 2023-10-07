package BLL;

import java.util.concurrent.BlockingQueue;

public class CellCalculationJob<N,T> implements Runnable {
    T currentGeneration;
    T nextGeneration;
    IRuleApplier<N,T> ruleApplier;
    BlockingQueue<N> inputQueue;
    BlockingQueue<N> outputQueue;

    public CellCalculationJob(T currentGeneration, T nextGeneration, IRuleApplier<N,T> ruleApplier, BlockingQueue<N> inputQueue, BlockingQueue<N> outputQueue){
        this.currentGeneration = currentGeneration;
        this.nextGeneration = nextGeneration;
        this.ruleApplier = ruleApplier;
        this.inputQueue = inputQueue;
        this.outputQueue = outputQueue;
    }

   @Override
    public void run() {
        try {
            calculate();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void calculate()throws InterruptedException {
        while(!inputQueue.isEmpty()){
            var cellCoordinates = inputQueue.poll();
            if(cellCoordinates!=null){
                var cellState = ruleApplier.calculateCell(cellCoordinates,currentGeneration);
                ruleApplier.addCellToNextGeneration(cellCoordinates,cellState,nextGeneration);
                outputQueue.put(cellCoordinates);
            }

        }
    }

}

