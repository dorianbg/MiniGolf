package AI;

import AI.utils.MoveCommand;
import AI.utils.Position;
import Game.Gameplay.AIPlayer;
import GameplayObjects.GolfBall;
import GameplayObjects.Hole;
import PhysicsEngine.CollisionDetector;
import com.badlogic.gdx.math.Vector3;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Dorian on 12-Apr-16.
 * MiniGolf
 */
public class AiEngine {

    private AIPlayer aiGame;
    private GolfBall aiGolfBall;
    private Hole hole;
    private ExecutorService executorService;
    private volatile boolean cancelled;
    private long computationTime;

    /**
     */
    protected int simulationLimit = 250;
    protected int roundLimit = 5;
    public float  directionNoisePercent = 10f;
    public float forceNoisePercent = 10f;
    protected boolean SKIPPING = true;
    /////
    public boolean NOISE_ON = false;
    private Vector3 wind = new Vector3(0f,0f,0f);
    /**
     */

    Vector3 holePosition;
    Vector3 ballPosition;
    GolfBall aiTempBall;
    CollisionDetector collisionDetector;

    public void initialize(){
        holePosition = hole.getPosition();
        ballPosition = aiGolfBall.getPosition().cpy();
        aiTempBall = new GolfBall(aiGolfBall.model,ballPosition.x,ballPosition.y,ballPosition.z);
        float directionNoise = 360f/100f * directionNoisePercent;
        float forceNoise = forceNoisePercent/100f;
        aiTempBall.directionNoise = directionNoise;
        aiTempBall.forceNoise = forceNoise;
        collisionDetector = new CollisionDetector(aiGame.getModelWorld());
    }


    public MoveCommand findNextMove(){
        initialize();
        float differenceX = holePosition.x - ballPosition.x;
        float differenceZ = holePosition.z - ballPosition.z;

        // you can replace with this when you don't want to show
        ArrayList<MoveCommand> commands = new ArrayList<>(2000);

        int incrementX = 5;
        int incrementZ = 5;
        System.out.println(computationTime);
        if(computationTime == 1){
            incrementX = 9;
            incrementZ = 18;
        }
        else if(computationTime == 4){
            incrementX = 4;
            incrementZ = 8;
        }
        else if(computationTime == 10){
            incrementX = 2;
            incrementZ = 6;
        }

        executorService = Executors.newCachedThreadPool();
        for (int i = -300; i < 300; i += incrementX) {
            for (int j = -300; j < 300 ; j += incrementZ) {
//                System.out.println("I" + i + "J" + j);
                if(cancelled) {
                    break;
                }
//                System.out.println("Ball position" + ballPosition);
//              this is a simpler version
                aiTempBall.setPosition(ballPosition);
                ShotSimulator simulator = new ShotSimulator(aiTempBall,collisionDetector,false);
                Vector3 newPosition = simulator.simulateShot(i,j);
                /*
                final int inti = i;
                final int intj = j;
//                System.out.println("Golf ball position" + aiGolfBall.getPosition());
                Future<Vector3> future = executorService.submit(new Callable<Vector3>() {
                    @Override
                    public Vector3 call() throws Exception {
                        aiTempBall.setPosition(ballPosition);
//                        aiGolfBall.setPosition(ballPosition);
                        ShotSimulator shotSimulator = new ShotSimulator(
//                                aiGolfBall,
                                aiTempBall,
                                collisionDetector,
                                NOISE_ON);
                        return shotSimulator.simulateShot(inti, intj);
                    }
                });
                Vector3 newPosition = null;
                try {
                    newPosition = future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
//                System.out.println("New Golf ball position" + newPosition);
//                System.out.println("Hole position" + holePosition);
                */

                double distance = 0;

                if (newPosition != null) {
                    distance = Math.sqrt(Math.pow(holePosition.x - newPosition.x, 2)
                            + Math.pow(holePosition.z - newPosition.z, 2));
                }
                if(SKIPPING) {
                    if (distance > 0.5) {
                        i += 1;
                        j += 4;
                    }
                    if (distance > 0.6) {
                        i += 1;
                        j += 8;
                    }
                    if (distance > 0.7) {
                        i += 2;
                        j += 16;
                    }
                }
          //      aiTempBall.setPosition(ballPosition);
                commands.add(new MoveCommand(i, j, distance));
            }
        }

        commands.sort(new Comparator<MoveCommand>() {
            @Override
            public int compare(MoveCommand o1, MoveCommand o2) {
                return o1.getScore()>=o2.getScore() ? 1 : -1;
            }
        });

   //     MoveCommand minCommand = cleanShots(commands,simulationLimit,roundLimit).get(0);
   //     aiTempBall.setPosition(ballPosition);

        return commands.get(0);
    }

    public List<MoveCommand> cleanShots(ArrayList<MoveCommand> allCommands, int selectionLimit, int roundLimit){
        System.out.println("all simulated commands " + allCommands.size());
        List<MoveCommand> selectedCommands = allCommands.subList(0,selectionLimit);
        for (int i = 0; i < roundLimit; i++){
            for(MoveCommand moveCommand : selectedCommands){
                aiTempBall.setPosition(ballPosition);
                ShotSimulator simulator = new ShotSimulator(aiTempBall,collisionDetector,NOISE_ON);
                Vector3 newPosition = simulator.simulateShot(moveCommand.xDifference,moveCommand.yDifference);
                double distance = Math.sqrt(Math.pow(holePosition.x - newPosition.x, 2)
                        + Math.pow(holePosition.z - newPosition.z, 2));
                moveCommand.score += distance;
            }
        }

        for(MoveCommand moveCommand : selectedCommands) {
            moveCommand.score = moveCommand.score/((double)(roundLimit + 1));
        }

        selectedCommands.sort(new Comparator<MoveCommand>() {
            @Override
            public int compare(MoveCommand o1, MoveCommand o2) {
                return o1.getScore()>=o2.getScore() ? 1 : -1;
            }
        });

        System.out.println("Final commands score");
        for(MoveCommand moveCommand : selectedCommands) {
            System.out.println(moveCommand.score);
        }
        return selectedCommands;
    }





    public void cancel(){
        cancelled = true;
    }
    public void unCancel(){
        cancelled = false;
    }

    public AiEngine(AIPlayer aiGame){
        this.aiGame = aiGame;
        this.aiGolfBall = aiGame.getAIball();
        hole = aiGame.getHole();
    }




    int[][] degreeMap;
    double[][] euclideanDistanceMap;

    public Vector3 convertDegreeToDirection(double degree){
        double radianValue = (degree/180f) * 3.14;
        float x = (float) ( (Math.cos(radianValue) * (-1f)) * Math.pow(10,-3));
        float z = (float) (Math.sin(radianValue) * Math.pow(10,-3));
//        System.out.println("Vector" + new Vector3(x,0,z));
        return new Vector3(x,0f,z);
    }

    public int convertPosition(double x){
        return (int)(x*100 + 50);
    }

    public void buildStrat(){
//        System.out.println("Started");
        WorldStateConverter converter = new WorldStateConverter(aiGame);

        int[][] terrain2d = converter.get2Dworld();
        int holeX = convertPosition(hole.getxPosition());
        int holeZ = convertPosition(hole.getzPosition());
        terrain2d[holeX][holeZ] = -2;
        VectorField vectorField = new VectorField(terrain2d);
        vectorField.generateVectorField();
        vectorField.generateHeatMapForVectorField();
//        vectorField.printPiDegreeVectorField();
        MoveGenerator moveGenerator = new MoveGenerator(vectorField);
        moveGenerator.setInitialPosition(convertPosition(aiGolfBall.getPosition().x),
                convertPosition(aiGolfBall.getPosition().z));
        List<Position> positions = moveGenerator.compressMovePath();
        System.out.println("Positions length " + positions.size());
    }

    public void getDegree(double x, double y){
        System.out.println(
                degreeMap[convertPosition(x)]
                        [convertPosition(y)]
        );
        System.out.println(
                euclideanDistanceMap[convertPosition(x)]
                        [convertPosition(y)]
        );
    }

    public void setComputationTime(long time){
        this.computationTime = time;
    }
}