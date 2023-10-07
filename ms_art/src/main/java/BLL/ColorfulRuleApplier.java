package BLL;

public class ColorfulRuleApplier implements IRuleApplier<int[],int[][]> {

    @Override
    public int calculateCell(int[] cellCoordinates, int[][] currentGeneration) {
        int state1Count = 0;
        int state2Count = 0;
        int state3Count = 0;

        //start indexes for columns and rows
        int x = cellCoordinates[0]-1;
        int y = cellCoordinates[1]-1;

        //indexes of the cell to be computed
        int cellX = cellCoordinates[0];
        int cellY = cellCoordinates[1];
        int cellState = currentGeneration[cellX][cellY];

        for(int i = x;i<x+3;i++){
            for(int j = y;j<y+3;j++){
                if(i!=cellX && j!=cellY){
                    if(currentGeneration[i][j]==1){
                        state1Count++;
                    }
                    else if(currentGeneration[i][j]==2){
                        state2Count++;
                    }
                    else if(currentGeneration[i][j]==3){
                        state3Count++;
                    }
                }
            }
        }

        int liveNeighborsCount = state1Count + state2Count + state3Count;
        //Any live cell with fewer than two or with more than three live neighbours dies
        if(cellState!=0 && (liveNeighborsCount<2 || liveNeighborsCount > 3)){
            return 0;
        }
        //Any dead cell with exactly three live neighbours comes to life.
        else if(cellState==0 && liveNeighborsCount==3){
            return Math.max(state1Count, Math.max(state2Count, state3Count));
        }
        //otherwise remains unchanged
        return cellState;
    }

    @Override
    public void addCellToNextGeneration(int[] cellCoordinates, Integer cellState, int[][] nextGeneration) {
        nextGeneration[cellCoordinates[0]][cellCoordinates[1]] = cellState;
    }

}
