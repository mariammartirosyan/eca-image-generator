package BLL;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ColorfulImageService implements IImageService<int[][]> {
    Map<Integer,Color> colorStates = new HashMap<Integer, Color>() {{
        put(1, Color.red);
        put(2, Color.green);
        put(3, Color.blue);
    }};

    @Override
    public BufferedImage getImage(int width, int height, int[][] generation) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB );

        //File file = new File("colorful.png"); //for testing

        for(int i= 0; i<height;i++){
            for(int j=0; j<width;j++){
                //for every live cell get two random cells from neighborhood that have one of the live states: 1, 2, 3
                if(generation[i][j]!=0 && i!=0 && j!=0 && i!=height-1 && j!=width-1){
                    var state1 = getRandomLiveCellState(i-1,i+1,j-1,j+1,generation);
                    var state2 =  getRandomLiveCellState(i-1,i+1,j-1,j+1,generation);
                    image.setRGB(j, i, mix(colorStates.get(state1),colorStates.get(state2)).getRGB());
                }
                //set white for every dead cell
                else{
                    image.setRGB(j, i, Color.white.getRGB());
                }
            }
        }
//        try {
//            ImageIO.write(image, "PNG", file); //for testing
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return image;
    }
    //source:moodle
    public Color mix(Color firstParent, Color secondParent) {
        final double relation = 0.5;
        return new Color((int) (firstParent.getRed() * relation + secondParent.getRed() * (1.0 - relation)),
                (int) (firstParent.getGreen() * relation + secondParent.getGreen() * (1.0 - relation)),
                (int) (firstParent.getBlue() * relation + secondParent.getBlue() * (1.0 - relation)));
    }
    public int getRandomLiveCellState(int xMin,int xMax, int yMin,int yMax, int[][] array){
        var isDead = true;
        var cellState = -1;
        while(isDead){
            cellState = array[HelperService.getRandomNumber(xMin,xMax)][HelperService.getRandomNumber(yMin,yMax)];
            if(cellState!=0){
                isDead= false;
            }
        }
        return cellState;
    }
}
