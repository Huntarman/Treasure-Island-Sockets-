package GameObjects;

import java.awt.*;
import java.util.Random;

public class GameObject {
    protected int x;
    protected int y;
    protected Color color;
    protected boolean canWalkThrough;
    protected Random r = new Random();
    public int cooldown;

    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
        this.color = Color.red;
    }
    public GameObject() {
        this.x = r.nextInt(256);
        this.y = r.nextInt(256);
        this.color = Color.red;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCooldown() {
        return cooldown;
    }
}
