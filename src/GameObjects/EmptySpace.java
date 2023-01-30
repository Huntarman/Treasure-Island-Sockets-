package GameObjects;

import java.awt.*;

public class EmptySpace extends GameObject{
    public EmptySpace(){
        this.color = Color.gray;
        this.canWalkThrough = true;
    }
}
