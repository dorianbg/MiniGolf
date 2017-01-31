package AI;

import Game.Gameplay.AIPlayer;
import GameplayObjects.GolfBall;
import GameplayObjects.Hole;
import GameplayObjects.Obstacle;
import GameplayObjects.Terrain;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dorian on 04-May-16.
 * MiniGolf
 */
public class WorldStateConverter {
    private List<ModelInstance> world;
    private GolfBall aiGolfBall;
    private GolfBall playerGolfBall;
    private AIPlayer game;
    private Terrain terrainModel;
    private Hole hole;
    private ArrayList<Obstacle> obstacles = new ArrayList<>(10);
    private int[][] terrain = new int[100][100];

    public WorldStateConverter(AIPlayer game){
        this.game = game;
        this.world = game.getModelWorld();
        this.aiGolfBall = game.getAIball();
        this.playerGolfBall = game.getPlayerBall();
        System.out.println("here");

        sortModels();
        generate2Darray();
        System.out.println("here");
    }

    public void sortModels(){
        for(ModelInstance modelInstance : world){
            if (modelInstance instanceof Terrain){
                this.terrainModel = (Terrain) modelInstance;
            }
            else if (modelInstance instanceof Hole){
                this.hole = (Hole) modelInstance;
            }
            else if(modelInstance instanceof Obstacle){
                Obstacle obstacle = (Obstacle) modelInstance;
                if ((obstacle.getxLength() * obstacle.getzLength()) != 0.01f){
                    this.obstacles.add(obstacle);
                }
            }
            else {
                int x = 5;
            }
        }
    }

    public void generate2Darray(){
        BoundingBox box = new BoundingBox();
        System.out.println(terrainModel.calculateBoundingBox(box));

        for(Obstacle obstacle : obstacles){
            System.out.println(obstacle.getBoundingBox());
            System.out.println(obstacle.getxPosition());
            System.out.println(obstacle.getzPosition());
            System.out.println(obstacle.getxLength());
            System.out.println(obstacle.getzLength());

            int topX = (int) (convertPosition(obstacle.getxPosition()) - obstacle.getxLength()/2 * 100);
            int topZ = (int) (convertPosition(obstacle.getzPosition()) - obstacle.getzLength()/2 * 100);
            for (int i = topX; i < topX + obstacle.getxLength()*100;i++){
                for(int j = topZ; j < topZ + obstacle.getzLength()*100; j++){
                    terrain[i][j] = -1;
                }
            }


            // now make an rectangle from xLength and zLength and fill up everything inside
        }


        // a position is: xPosition * 100 + 50, zPosition *100 + 50

        // first make the boundary
        for (int i = 0; i < 100; i++){
            terrain[i][0] = -1;
            terrain[0][i] = -1;
            terrain[99][i] = -1;
            terrain[i][99] = -1;
        }

        int holeX = (int) (hole.getxPosition() * 100 + 50);
        int holeZ = (int) (hole.getzPosition() * 100 + 50);
        terrain[holeX][holeZ] = -2;

        printTerrain();

    }


    public int convertPosition(double x){
        return (int)(x*100 + 50);
    }

    public void printTerrain(){
        for (int i = 0; i < terrain.length; i++){
            for(int j = 0; j < terrain[0].length; j++){
                System.out.print(terrain[i][j]);
            }
            System.out.println();
        }
    }



    public int[][] get2Dworld(){
        return this.terrain;
    }

}
