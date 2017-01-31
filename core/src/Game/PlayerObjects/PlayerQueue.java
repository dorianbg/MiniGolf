package Game.PlayerObjects;

import java.util.ArrayList;

/**
 * Created by Daniel on 19.03.2016.
 */
public class PlayerQueue  {

    private ArrayList<Player> players;
    private int current;
    private int size;

    public PlayerQueue(int size){
        players = new ArrayList<>(size);
        this.size = size;
        current = -1;
        initializePlayers();
    }

    public Player next(){
        current+=1;
        return players.get(current%size);
    }

    public Player first(){
        return players.get(0);
    }

    public int getSize(){
        return size;
    }

    public void initializePlayers(){
        for (int i = 0; i < size; i++){
            Player player = new Player();
            player.setName("Player " + (i + 1));
            player.setScore(0);
            players.add(player);
        }
    }

    public ArrayList<Player> getAllPlayers(){
        return players;
    }

    public void print(){
        for(int i = 0; i < size; i++){
            System.out.println(players.get(i).getName());
        }
    }

}
