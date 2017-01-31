package AI;

import org.junit.Test;

/**
 * Created by Dorian on 25-Apr-16.
 * MiniGolf
 */
public class test {
    @Test
    public void test1(){
        int[][] terrain = new int[6][6];
        terrain[1][4] = -1;
        terrain[2][4] = -1;
        terrain[3][4] = -1;

        terrain[4][0] = -1;
        terrain[4][1] = -1;
        terrain[4][2] = -1;

        terrain[1][1] = -2;

        VectorField vectorField = new VectorField();
        vectorField.setTerrain(terrain);
        vectorField.generateHeatMapForVectorField();
        vectorField.printTerrain();
        vectorField.generateVectorField();
        System.out.println();
        vectorField.printVectorField();
        vectorField.generateEuclideanMap();
        System.out.println();
        vectorField.printEuclideanMap();
        vectorField.printDegreeVectorField();


        MoveGenerator moveGenerator = new MoveGenerator(vectorField);

    }

    @Test
    public void test3(){
        int[][] terrain = new int[6][6];
        terrain[1][4] = -1;
        terrain[2][4] = -1;
        terrain[3][4] = -1;

        terrain[4][0] = -1;
        terrain[4][1] = -1;
        terrain[4][2] = -1;

        terrain[1][1] = -2;

        VectorField vectorField = new VectorField();
        vectorField.setTerrain(terrain);
        vectorField.generateHeatMapForVectorField();
        vectorField.printTerrain();
        vectorField.generateVectorField();
        System.out.println();
        vectorField.printVectorField();
        vectorField.generateEuclideanMap();
        System.out.println();
        vectorField.printEuclideanMap();

    }

    @Test
    public void test2(){
        int[][] terrain = new int[12][12];
        terrain[1][4] = -1;
        terrain[2][4] = -1;
        terrain[3][4] = -1;

        terrain[4][0] = -1;
        terrain[4][1] = -1;
        terrain[4][2] = -1;


        terrain[2][8] = -1;
        terrain[2][9] = -1;
        terrain[2][10] = -1;
        terrain[7][5] = -1;
        terrain[7][6] = -1;
        terrain[7][7] = -1;

        terrain[6][7] = -2;

        VectorField vectorField = new VectorField();
        vectorField.setTerrain(terrain);
        vectorField.generateHeatMapForVectorField();
        vectorField.printTerrain();
        vectorField.generateVectorField();
        System.out.println();
        vectorField.printVectorField();
        vectorField.generateEuclideanMap();
        System.out.println();
        vectorField.printDegreeVectorField();

    }

    @Test
    public void moveTest(){
        int[][] terrain = new int[6][6];
        terrain[1][4] = -1;
        terrain[2][4] = -1;
        terrain[3][4] = -1;

        terrain[4][0] = -1;
        terrain[4][1] = -1;
        terrain[4][2] = -1;

        terrain[1][1] = -2;

        VectorField vectorField = new VectorField();
        vectorField.setTerrain(terrain);
        vectorField.printTerrain();
        System.out.println();
        vectorField.generateHeatMapForVectorField();
        vectorField.printTerrain();
        vectorField.generateVectorField();
        System.out.println();
        vectorField.printVectorField();
        vectorField.generateEuclideanMap();
        System.out.println();
        vectorField.printEuclideanMap();
        vectorField.printDegreeVectorField();

        MoveGenerator moveGenerator = new MoveGenerator(vectorField);
        moveGenerator.setInitialPosition(5,0);
        moveGenerator.printCurrentPosition();
//        moveGenerator.movePath();
        moveGenerator.compressMovePath();
    }

    @Test
    public void moveTest2(){
        int[][] terrain = new int[25][25];
        terrain[1][4] = -1;
        terrain[2][4] = -1;
        terrain[3][4] = -1;

        terrain[4][0] = -1;
        terrain[4][1] = -1;
        terrain[4][2] = -1;


        terrain[2][8] = -1;
        terrain[2][9] = -1;
        terrain[2][10] = -1;
        terrain[7][5] = -1;
        terrain[7][6] = -1;
        terrain[7][7] = -1;


        terrain[14][8] = -1;
        terrain[14][9] = -1;
        terrain[14][10] = -1;
        terrain[14][5] = -1;
        terrain[14][6] = -1;
        terrain[14][7] = -1;


        terrain[14][22] = -2;

        VectorField vectorField = new VectorField();
        vectorField.setTerrain(terrain);
        vectorField.printTerrain();
        vectorField.generateHeatMapForVectorField();
        vectorField.printTerrain();
        vectorField.generateVectorField();
        System.out.println();
        vectorField.printVectorField();
        vectorField.generateEuclideanMap();
        System.out.println();
        vectorField.printEuclideanMap();
        vectorField.printDegreeVectorField();

        MoveGenerator moveGenerator = new MoveGenerator(vectorField);
        moveGenerator.setInitialPosition(3,0);
        moveGenerator.printCurrentPosition();
//        moveGenerator.movePath();
        moveGenerator.compressMovePath();

    }



    @Test
    public void moveTest3(){
        int[][] terrain = new int[6][6];
        terrain[1][4] = -1;
        terrain[2][4] = -1;
        terrain[3][4] = -1;

        terrain[4][0] = -1;
        terrain[4][1] = -1;
        terrain[4][2] = -1;

        terrain[1][1] = -2;

        VectorField vectorField = new VectorField();
        vectorField.setTerrain(terrain);
        vectorField.printTerrain();
        System.out.println();
        vectorField.generateHeatMapForVectorField();
        vectorField.printTerrain();
        vectorField.generateVectorField();
        System.out.println();
//        vectorField.printVectorField();
        vectorField.generateEuclideanMap();
        System.out.println();
//        vectorField.printEuclideanMap();
        vectorField.printDegreeVectorField();

        MoveGenerator moveGenerator = new MoveGenerator(vectorField);
        moveGenerator.setInitialPosition(3,0);
        moveGenerator.printCurrentPosition();
//        moveGenerator.movePath();
        moveGenerator.compressMovePath();
    }
    @Test
    public void moveTest4(){
        int[][] terrain = new int[6][6];
        terrain[1][4] = -1;
        terrain[2][4] = -1;
        terrain[3][4] = -1;

        terrain[4][0] = -1;
        terrain[4][1] = -1;
        terrain[4][2] = -1;

        terrain[1][1] = -2;

        VectorField vectorField = new VectorField();
        vectorField.setTerrain(terrain);
        vectorField.printTerrain();
        System.out.println();
        vectorField.generateHeatMapForVectorField();
        vectorField.printTerrain();
        vectorField.generateVectorField();
        System.out.println();
//        vectorField.printVectorField();
        vectorField.generateEuclideanMap();
        System.out.println();
        vectorField.printDegreeVectorField();

        MoveGenerator moveGenerator = new MoveGenerator(vectorField);
        moveGenerator.setInitialPosition(3,3);
        moveGenerator.printCurrentPosition();
//        moveGenerator.movePath();
        moveGenerator.compressMovePath();
    }
    @Test
    public void moveTest5(){
        int[][] terrain = new int[6][6];
        terrain[1][4] = -1;
        terrain[2][4] = -1;
        terrain[3][4] = -1;

        terrain[4][0] = -1;
        terrain[4][1] = -1;
        terrain[4][2] = -1;

        terrain[1][1] = -2;

        VectorField vectorField = new VectorField();
        vectorField.setTerrain(terrain);
        vectorField.printTerrain();
        System.out.println();
        vectorField.generateHeatMapForVectorField();
        vectorField.printTerrain();
        vectorField.generateVectorField();
        vectorField.generateEuclideanMap();
        System.out.println();
        vectorField.printDegreeVectorField();

        MoveGenerator moveGenerator = new MoveGenerator(vectorField);
        moveGenerator.setInitialPosition(1,5);
//        moveGenerator.movePath();
        moveGenerator.compressMovePath();
    }

}
