package BLL;

public class GeometricRuleApplier implements IRuleApplier<Integer,int[]> {
    int rule;
    int[] ruleset;
    public GeometricRuleApplier(int rule) throws Exception {
        if(rule>255 || rule<0){
         System.out.println("The rule should be a number in range 0-255. As default rule 90 will be chosen");
         rule = 90;
        }
        this.rule = rule;
        this.ruleset = getRuleset(rule);
    }

    int[] getRuleset(int rule){
        int [] ruleset = new int[8];
        var binaryNumArr = Integer.toBinaryString(rule).toCharArray();
        var lengthDiff =  ruleset.length - binaryNumArr.length;
        for(int i = lengthDiff; i < ruleset.length; i++){
            ruleset[i] = Character.getNumericValue(binaryNumArr[i-lengthDiff]);
        }
        return ruleset;
    }

    @Override
    public int calculateCell(Integer cellCoordinates, int[] currentGeneration) {
        int leftNeighbor = currentGeneration[cellCoordinates-1];
        int cell = currentGeneration[cellCoordinates];
        int rightNeighbor = currentGeneration[cellCoordinates+1];

        if(leftNeighbor==1 && cell==1 && rightNeighbor==1){
            return ruleset[0];
        }
        if(leftNeighbor==1 && cell==1 && rightNeighbor==0){
            return ruleset[1];
        }
        if(leftNeighbor==1 && cell==0 && rightNeighbor==1){
            return ruleset[2];
        }
        if(leftNeighbor==1 && cell==0 && rightNeighbor==0){
            return ruleset[3];
        }
        if(leftNeighbor==0 && cell==1 && rightNeighbor==1){
            return ruleset[4];
        }
        if(leftNeighbor==0 && cell==1 && rightNeighbor==0){
            return ruleset[5];
        }
        if(leftNeighbor==0 && cell==0 && rightNeighbor==1){
            return ruleset[6];
        }
        return ruleset[7];
    }

    @Override
    public void addCellToNextGeneration(Integer cellCoordinates, Integer cellState, int[] nextGeneration) {
        nextGeneration[cellCoordinates] = cellState;
    }
}
