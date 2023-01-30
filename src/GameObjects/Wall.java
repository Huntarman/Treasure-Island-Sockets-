package GameObjects;

import java.awt.*;

public class Wall extends GameObject{
    public Wall(){
        this.color = Color.black;
        this.canWalkThrough = false;
    }
}
