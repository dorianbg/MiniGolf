package AI;

import AI.utils.Position;
import AI.utils.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dorian on 26-Apr-16.
 * MiniGolf
 */
public class MoveGenerator {
    private VectorField vectorFieldGenerator;
    private int[][] terrain;
    private Vector[][] vectorField;
    private int[][] degreeVectorField;

    private Position currentPosition;
    private Position goalPosition;
    private Position startingPosition;
    private List<Position> movePath;


    private int xGoalPos;
    private int yGoalPos;

    public MoveGenerator(VectorField vectorField){
        this.vectorFieldGenerator = vectorField;
        this.terrain = vectorFieldGenerator.getTerrain();
        this.vectorField = vectorFieldGenerator.getVectorField();
        this.degreeVectorField = vectorField.convertVectorField();
        goalPosition = vectorField.findGoalPosition();
    }


    public void setInitialPosition(int x, int y){
        if (x < 0 || x > terrain.length || y < 0 || y > terrain[0].length){
            throw new IllegalArgumentException("Illegal arguments");
        }
        startingPosition = new Position(x,y,0);
        setCurrentPosition(x,y);
    }

    public void setCurrentPosition(int x, int y){
        if (x < 0 || x > terrain.length || y < 0 || y > terrain[0].length){
            throw new IllegalArgumentException("Illegal arguments");
        }
        if(currentPosition == null){
            currentPosition = new Position(x,y,0);
        }
        else{
            currentPosition.xCoord = x;
            currentPosition.yCoord = y;
        }
    }

    public Position getCurrentPosition(){
        return currentPosition;
    }
    public void printCurrentPosition(){
        System.out.println(currentPosition.xCoord + " "+ currentPosition.yCoord);
    }



    private Position move(){
        Position tempPosition = new Position(currentPosition.xCoord, currentPosition.yCoord, 0);

        Vector currentVector = vectorField[currentPosition.xCoord][currentPosition.yCoord];
        Vector previousVector = vectorField[currentPosition.xCoord][currentPosition.yCoord];

        while(currentVector.equals(previousVector)){
            // x dir actually is y direction;s movement, and y direction is actually x directions natural movement
            tempPosition.xCoord += currentVector.yDir;
            tempPosition.yCoord += currentVector.xDir;

            previousVector.xDir = currentVector.xDir;
            previousVector.yDir = currentVector.yDir;

            currentVector = vectorField[tempPosition.xCoord][tempPosition.yCoord];

        }
        Position positionChange =
                new Position(tempPosition.xCoord - currentPosition.xCoord,
                        tempPosition.yCoord - currentPosition.yCoord,0);
        setCurrentPosition(tempPosition.xCoord,tempPosition.yCoord);
        return positionChange;
    }



    public List<Position> movePath(){
        Position startingPosition = getCurrentPosition();
        System.out.println("Starting position  " + startingPosition);
        Position positionChange;
        List<Position> positionChanges = new ArrayList<>(6);
//        System.out.println(currentPosition);
//        System.out.println(goalPosition);
        while(!(currentPosition.equals(goalPosition))){
//            System.out.println(currentPosition);
            positionChanges.add(move());
        }
        setCurrentPosition(startingPosition.xCoord,startingPosition.yCoord);
        System.out.println("Ending position  " + getCurrentPosition());

        return positionChanges;
    }

    public List<Position> compressMovePath(){
        List<Position> positionChanges = movePath();
        System.out.println(positionChanges.size());
        List<Position> newPath = new ArrayList<>();
        Position currentPosition = new Position(this.startingPosition.xCoord,
                this.startingPosition.yCoord,0);
        boolean changedLastPosition = false;

        for (int i = 0; i < positionChanges.size()-1; i++){

            Position change1 = positionChanges.get(i);
            Position change2 = positionChanges.get(i+1);

            int changeInHeight = change1.xCoord + change2.xCoord;
            int changeInLength = change1.yCoord + change2.yCoord;
//            System.out.println("Change in height" + changeInHeight);
//            System.out.println("Change in length" + changeInLength);

            if(!findObstaclesInArea(currentPosition.yCoord, currentPosition.yCoord + changeInLength,
                    currentPosition.xCoord, currentPosition.xCoord + changeInHeight)) {
                Position compressedPositionChange = new Position(changeInHeight, changeInLength, 0);
                currentPosition.updatePosition(changeInHeight,changeInLength);
                newPath.add(compressedPositionChange);
                changedLastPosition = true;
            }
            else{
                newPath.add(change1);
                currentPosition.updatePosition(change1.xCoord,change1.yCoord);
                changedLastPosition = false;
            }
        }

        if(changedLastPosition == false){
            newPath.add(positionChanges.get(positionChanges.size()-1));
        }
//        if (newPath.size()==positionChanges.size()-1 &&  positionChanges.size()!=2 &&  positionChanges.size()!=3){
//            newPath.add(positionChanges.get(positionChanges.size()-1));
//        }

        System.out.println("old path");
        System.out.println(positionChanges);

        System.out.println("new path");
        System.out.println(newPath);
        return newPath;
    }





    public boolean findObstaclesInArea( int fromX, int toX, int fromY, int toY){
        System.out.println("fromX = [" + fromX + "], toX = [" + toX + "], fromY = [" + fromY + "], toY = [" + toY + "]");
        if(fromY < toY){
            for (int i = fromY; i <=  toY; i++){
                if(fromX < toX){
                    for(int j = fromX; j <= toX; j++){
//                        System.out.println(i + " " + j);
                        try {
                            if (terrain[i][j] == -1) {
//                            System.out.println("failed");
                                return true;
                            }
                        }
                        catch (ArrayIndexOutOfBoundsException e){

                        }
                    }
                }
                else {
                    for(int j = fromX; j >= toX; j--){
//                        System.out.println(i + " " + j);
                        try {
                            if (terrain[i][j] == -1) {
//                            System.out.println("failed");
                                return true;
                            }
                        }
                        catch (ArrayIndexOutOfBoundsException e){

                        }
                    }

                }
            }
        }
        else{
            for (int i = fromY; i >= toY; i--){
                if(fromX < toX){
                    for(int j = fromX; j <= toX; j++){
//                        System.out.println(i + " " + j);
                        try {
                            if (terrain[i][j] == -1) {
//                            System.out.println("failed");
                                return true;
                            }
                        }
                        catch (ArrayIndexOutOfBoundsException e){

                        }
                    }
                }
                else {
                    for(int j = fromX; j >= toX; j--){
//                        System.out.println(i + " " + j);
                        try {
                            if (terrain[i][j] == -1) {
//                            System.out.println("failed");
                                return true;
                            }
                        }
                        catch (ArrayIndexOutOfBoundsException e){

                        }
                    }

                }
            }
        }
        return false;
    }
}