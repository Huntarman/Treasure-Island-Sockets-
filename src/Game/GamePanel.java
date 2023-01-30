package Game;

import Game.GameMap;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    public String[][] map = new String[128][128];
    public GameMap mapImage = new GameMap(map);

    public GamePanel(String[][] map){
        this.map = map;
        this.mapImage = new GameMap(map);
    }
    @Override
    protected void paintComponent(Graphics arg){
        super.paintComponent(arg);
        if(mapImage!=null){
            mapImage.drawMe((Graphics2D) arg);
        }
    }
    public void setMap(String[][] map) {
        this.map = map;
        this.mapImage.changeMap(map);
    }
}
