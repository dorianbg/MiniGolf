package AI;

import AI.utils.Position;
import AI.utils.Vector;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

/**
 * Created by Dorian on 25-Apr-16.
 * MiniGolf
 */
public class VectorField {
    private int[][] terrain;
    private double[][] euclideanMap;
    private int[][] heatMap;
    private Vector[][] vectorField;
    private Queue<Position> queue;


    /*
     the methods makes sure the inputted terrain is up to standard
     rules for marking are:
      1) goal = -2
      2) obstacle = - 1
      3) terrain - 0
      4) ball - not relevant, same as terrain - 0
    */
    public VectorField(){

    }

    public VectorField(int[][] terrain){
        setTerrain(terrain);
    }

    private boolean checkTerrain(){
        for (int[] row : terrain){
            for (int i : row){
                if (i <= -3 || i >= 1){
                    return false;
                }
            }
        }
        return true;
    }



    public void setTerrain(int[][] terrain){
        this.terrain = terrain;
        if (checkTerrain() == false){
            throw new IllegalArgumentException("Terrain is not standardized");
        }
        queue = new ArrayDeque<>(terrain.length*terrain[0].length);
    }

    public int[][] getTerrain() {
        return terrain;
    }

    public void generateHeatMapForVectorField(){
        Position currentPosition = findGoalPosition();
        queue.add(currentPosition);
        int[][] tempTerrain = terrain.clone();
        while (queue.isEmpty() == false){
            currentPosition = queue.poll();
            markNeighbours2(currentPosition);
        }
        heatMap = terrain.clone();
        terrain = tempTerrain.clone();
    }
    public void generateHeatMapWithShortestPath(){
        Position currentPosition = findGoalPosition();
        queue.add(currentPosition);
        int[][] tempTerrain = terrain.clone();
        while (queue.isEmpty() == false){
            currentPosition = queue.poll();
            markNeighbours(currentPosition);
        }
        heatMap = terrain.clone();
        terrain = tempTerrain.clone();
    }

    public int[][] getHeatMap() {
        return heatMap;
    }

    public void generateEuclideanMap(){
        Position currentPosition = findGoalPosition();
        double xCentre = currentPosition.xCoord;
        double yCentre = currentPosition.yCoord;
        euclideanMap = new double[terrain.length][terrain[0].length];

        for (int i = 0; i < terrain.length; i++){
            for (int j = 0; j < terrain[i].length; j++){
                if (terrain[i][j] >= 1){
                    euclideanMap[i][j] = Math.sqrt(Math.pow(i-xCentre,2) + Math.pow(j-yCentre,2));
                }
                else if(terrain[i][j] == -2){
                    euclideanMap[i][j] = 0;
                }
                else{
                    euclideanMap[i][j] = 1000;
                }
            }
        }
    }

    public double[][] getEuclideanMap() {
        return euclideanMap;
    }

    public void generateVectorField(){
        vectorField = new Vector[terrain.length][terrain[0].length];
        for(int i = 0; i < terrain.length; i++){
            for (int j = 0; j < terrain[0].length; j++) {
                if (terrain[i][j] == -1) {
                }
                else if (terrain[i][j] == -2){
                    vectorField[i][j] = new Vector(0,0);
                }
                /*
                GENERALLY:
                A) HORIZONTAL
                    1) +1 - right
                    2) -1 - left
                B) VERTICAL
                    1) +1 - down
                    2) -1 - up
                 */
                else {
                    int verticalDirection = -10;
                    int horizontalDirection = -10;
                    int up = -1;
                    int down = 1;
                    int left = -1;
                    int right = 1;
                    int stay = 0;
                    // check obstacle returns true if there is a empty field or an obstacle to left
                    if (checkBelowObstacle(i,j) && checkAboveObstacle(i,j)){
                        verticalDirection = stay;
                    }
                    if (checkLeftObstacle(i,j) && checkRightObstacle(i,j)){
                        horizontalDirection = stay;
                    }

                    if(horizontalDirection != stay){
                        if (checkLeftObstacle(i, j)) {
                            if (terrain[i][j+right] < terrain[i][j]) {
                                horizontalDirection = right;
                            } else {
                                horizontalDirection = stay;
                            }
                        }
                        else if (checkRightObstacle(i, j)) {
                            if (terrain[i][j+left] < terrain[i][j]) {
                                horizontalDirection = left;
                            } else {
                                horizontalDirection = stay;
                            }
                        }
                        else{
                            if (terrain[i][j + left] < terrain[i][j + right]) {
                                horizontalDirection = left;
                            }
                            else if (terrain[i][j + left] == terrain[i][j + right] ) {
                                horizontalDirection = stay;
                            }
                            else{
                                horizontalDirection = right;
                            }
                        }
                    }

                    if(verticalDirection != stay){
                        if (checkAboveObstacle(i, j)) {
                            if (terrain[i+down][j] < terrain[i][j]) {
                                verticalDirection = down;
                            } else {
                                verticalDirection = stay;
                            }
                        }

                        else if (checkBelowObstacle(i, j)) {
                            if (terrain[i+up][j] < terrain[i][j]) {
                                verticalDirection = up;
                            } else {
                                verticalDirection = stay;
                            }
                        }
                        else{
                            if (terrain[i + up][j] < terrain[i + down][j] ) {
                                verticalDirection = up;
                            }
                            else if (terrain[i + up][j] == terrain[i + down][j]) {
                                verticalDirection = stay;
                            }
                            else {
                                verticalDirection = down;
                            }
                        }
                    }

                    if (horizontalDirection == 0 && verticalDirection == 0){
                        if (checkAboveObstacle(i,j) && checkBelowObstacle(i,j)){
                            horizontalDirection = left;
                        }
                        else if (checkLeftObstacle(i,j) && checkRightObstacle(i,j)){
                            verticalDirection = up;
                        }
                    }

                    vectorField[i][j] = new Vector(horizontalDirection, verticalDirection);
                }
            }
            // this is the case when we are near borders
        }
    }

    public void setInitialPosition(int x, int y){

    }

    public int[][] convertVectorField(){
        int[][] vectorDegreeField = new int[vectorField.length][vectorField[0].length];
        for (int i = 0; i <  vectorField.length; i++) {
            for (int j = 0; j <  vectorField[0].length; j++) {
                Vector currentVector = vectorField[i][j];
                if(currentVector == null){
                    vectorDegreeField[i][j] = -100;
                }
                else{
                    if(currentVector.xDir == 0 && currentVector.yDir == -1){
                        vectorDegreeField[i][j] = 360;
                    }
                    else if(currentVector.xDir == 1 && currentVector.yDir == -1){
                        vectorDegreeField[i][j] = 45;
                    }
                    else if(currentVector.xDir == 1 && currentVector.yDir == 0){
                        vectorDegreeField[i][j] = 90;
                    }
                    else if(currentVector.xDir == 1 && currentVector.yDir == 1){
                        vectorDegreeField[i][j] = 135;
                    }
                    else if(currentVector.xDir == 0 && currentVector.yDir == 1){
                        vectorDegreeField[i][j] = 180;
                    }
                    else if(currentVector.xDir == -1 && currentVector.yDir == 1){
                        vectorDegreeField[i][j] = 225;
                    }
                    else if(currentVector.xDir == -1 && currentVector.yDir == 0){
                        vectorDegreeField[i][j] = 270;
                    }
                    else if(currentVector.xDir == -1 && currentVector.yDir == -1){
                        vectorDegreeField[i][j] = 315;
                    }
                    else{
                        vectorDegreeField[i][j] = -50;
                    }
                }
            }
        }
        return vectorDegreeField;
    }

    public int[][] convertVectorFieldToPiStandard(){
        int[][] vectorDegreeField = new int[vectorField.length][vectorField[0].length];
        for (int i = 0; i <  vectorField.length; i++) {
            for (int j = 0; j <  vectorField[0].length; j++) {
                Vector currentVector = vectorField[i][j];
                if(currentVector == null){
                    vectorDegreeField[i][j] = -100;
                }
                else{
                    if(currentVector.xDir == 0 && currentVector.yDir == -1){
                        vectorDegreeField[i][j] = 270;
                    }
                    else if(currentVector.xDir == 1 && currentVector.yDir == -1){
                        vectorDegreeField[i][j] = 315;
                    }
                    else if(currentVector.xDir == 1 && currentVector.yDir == 0){
                        vectorDegreeField[i][j] = 360;
                    }
                    else if(currentVector.xDir == 1 && currentVector.yDir == 1){
                        vectorDegreeField[i][j] = 45;
                    }
                    else if(currentVector.xDir == 0 && currentVector.yDir == 1){
                        vectorDegreeField[i][j] = 90;
                    }
                    else if(currentVector.xDir == -1 && currentVector.yDir == 1){
                        vectorDegreeField[i][j] = 135;
                    }
                    else if(currentVector.xDir == -1 && currentVector.yDir == 0){
                        vectorDegreeField[i][j] = 180;
                    }
                    else if(currentVector.xDir == -1 && currentVector.yDir == -1){
                        vectorDegreeField[i][j] = 225;
                    }
                    else{
                        vectorDegreeField[i][j] = -50;
                    }
                }
            }
        }
        return vectorDegreeField;
    }

    public Vector[][] getVectorField() {
        return vectorField;
    }

    public int[][] getDegreeVectorField(){
        return convertVectorField();

    }

    private boolean checkLeftObstacle(int i, int j) {
        try{
            if(terrain[i][j-1] == -1){
                return true;
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            return true;
        }
        return false;
    }

    private boolean checkRightObstacle(int i, int j) {
        try{
            if(terrain[i][j+1] == -1){
                return true;
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            return true;
        }
        return false;
    }
    private boolean checkAboveObstacle(int i, int j) {
        try{
            if(terrain[i-1][j] == -1){
                return true;
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            return true;
        }
        return false;
    }
    private boolean checkBelowObstacle(int i, int j) {
        try{
            if(terrain[i+1][j] == -1){
                return true;
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            return true;
        }
        return false;
    }

    private void markNeighbours(Position position) {
        int pathValue = position.pathValue;
        int xPosition = position.xCoord;
        int yPosition = position.yCoord;

        for (int i = xPosition -1 ; i <= xPosition+1; i++) {
            for (int j = yPosition - 1; j <= yPosition + 1; j++) {
                // this is the case where we are on the original position
                if ((i == xPosition) && (j == yPosition)) {
                    // don't use break and continue statements
                    // just do nothing in this case
                }
                // the if statement makes sure we are operating within the range
                else {
                    if (i >= 0 && j >= 0 && i < terrain.length && i < terrain[0].length && j < terrain.length && j < terrain[0].length) {
                        // it is not a part of terrain then ignore
                        if (terrain[i][j] != 0) {
                        }
                        else {
                            terrain[i][j] = pathValue + 1;
                            queue.add(new Position(i, j, pathValue + 1));
                        }
                    }
                }
            }
        }
    }

    private void markNeighbours2(Position position) {
        int pathValue = position.pathValue;
        int xPosition = position.xCoord;
        int yPosition = position.yCoord;

        ArrayList<Position> positions = new ArrayList<>(4);
        positions.add(new Position(-1,0,0));
        positions.add(new Position(1,0,0));
        positions.add(new Position(0,-1,0));
        positions.add(new Position(0,1,0));

        for (Position pos : positions) {
            int i = xPosition + pos.xCoord;
            int j = yPosition + pos.yCoord;
            if (i >= 0 && j >= 0 && i < terrain.length && i < terrain[0].length && j < terrain.length && j < terrain[0].length) {
                // it is not a part of terrain then ignore
                if (terrain[i][j] != 0) {
                }
                else {
                    terrain[i][j] = pathValue + 1;
                    queue.add(new Position(i, j, pathValue + 1));
                }
            }
                }
    }


    public Position findGoalPosition(){
        for (int i = 0; i < terrain.length; i++){
            for(int j = 0; j < terrain[0].length; j++){
                if (terrain[i][j] == -2){
                    return new Position(i,j,0);
                }
            }
        }
        return new Position(-Integer.MAX_VALUE,-Integer.MAX_VALUE,-Integer.MAX_VALUE);
    }

    public void printTerrain(){
        for (int[] row : terrain){
            System.out.println(Arrays.toString(row));
        }
    }
    public void printHeatMap(){
        for (int[] row : heatMap){
            System.out.println(Arrays.toString(row));
        }
    }
    public void printEuclideanMap(){
        for (double[] row : euclideanMap){
            System.out.println(Arrays.toString(row));
        }
    }

    public void printVectorField(){
        for (Vector[] row : vectorField){
            for(Vector i : row) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    public void printDegreeVectorField(){
        int[][] degreeVectorField = convertVectorField();
        for (int[] row : degreeVectorField){
            for(int i : row) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    public void printPiDegreeVectorField(){
        int[][] degreeVectorField = convertVectorFieldToPiStandard();
        for (int[] row : degreeVectorField){
            for(int i : row) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }


}
