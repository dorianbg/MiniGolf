package GameplayObjects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

/**
 * Created by Dorian on 14-Mar-16.
 * MiniGolf
 */
public class Terrain extends ModelInstance {


    public Terrain(Model model, float x, float y, float z) {
        super(model, x, y, z);
    }
}
