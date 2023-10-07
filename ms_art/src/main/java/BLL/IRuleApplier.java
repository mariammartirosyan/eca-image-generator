package BLL;

public interface IRuleApplier<N,T>{
    int calculateCell(N cellCoordinates, T currentGeneration);
    void addCellToNextGeneration(N cellCoordinates, Integer cellState, T nextGeneration);

}
