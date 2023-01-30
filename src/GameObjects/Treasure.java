package GameObjects;

import java.awt.*;

public class Treasure extends GameObject{
    public Treasure(){
        this.color = Color.yellow;
        this.cooldown = 5 + r.nextInt(10);
        this.canWalkThrough = true;
    }

}
