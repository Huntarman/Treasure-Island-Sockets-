package Game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GameMap{
    final int pixel_SIZE = 4;
    final int maxScreenCol = 128;
    final int maxScreenRow = 128;
    final int WIDTH = maxScreenCol * pixel_SIZE;
    final int HEIGHT = maxScreenRow * pixel_SIZE;

    private BufferedImage buffer;
    private Graphics2D buferGraphics;
    private String[][] cMap = new String[maxScreenCol][maxScreenRow];
    public GameMap(String[][] map){
        this.buffer = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
        this.buferGraphics = this.buffer.createGraphics();
        this.cMap = map;
    }
    public void drawMe(Graphics2D g2){
        Color color = null;
        for (int i = 0; i < maxScreenCol; i++) {
            for (int j = 0; j < maxScreenRow; j++) {
                switch (this.cMap[i][j]){
                    case "f","b":
                        color = Color.black;
                        break;
                    case "e":
                        color = Color.lightGray;
                        break;
                    case "t":
                        color = Color.yellow;
                        break;
                    case "p":
                        color = Color.blue;
                        break;
                    default:
                        color = new Color(255, 255, 255);
                        break;
                }
                buferGraphics.setColor(color);
                buferGraphics.fillRect(i * pixel_SIZE, j * pixel_SIZE, pixel_SIZE, pixel_SIZE);
            }
        }
        g2.drawImage(this.buffer, 0, 0, null);
    }
    public void changeMap(String[][] changedMap){
        this.cMap = changedMap;
    }
}

