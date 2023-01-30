package Game;

import GameObjects.GameObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

class Player extends GameObject {
    private String[][] map = new String[128][128];
    private int score;
    private int direction;
    private int random1, random2;
    public int cooldown;
    public boolean firstMove = true;
    public boolean spawned = false;

    public Player() {

        for (int i = 0; i < 128; i++) {
            for (int j = 0; j < 128; j++) {
                this.map[i][j] = "f";
            }
        }
        this.direction = r.nextInt(7);
    }

    public boolean isSpawned() {
        return spawned;
    }

    public void setSpawned() {
        this.spawned = true;
    }

    public void changeDirection() {
        this.direction = r.nextInt(7);
    }

    public String[][] getMap() {
        return map;
    }

    public boolean firstmove() {
        return firstMove;
    }

    public void setMap(String[][] map) {
        this.map = map;
    }

    public void setCoordinates(int newX, int newY) {
        this.map[x][y] = "e";
        this.x = newX;
        this.y = newY;
        this.map[x][y] = "p";
    }

    public void setCoordinates2(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    public int getDirection() {
        return direction;
    }

    public void isFirstMove() {
        this.firstMove = false;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void cooldownDown() {
        this.cooldown = Math.max(--this.cooldown, 0);
    }
    //public void printMap(){
    //    for (int i = 0; i < 128; i++) {
    //        for (int j = 0; j < 128; j++) {
    //            System.out.print("["+map[i][j]+"]");
    //        }
    //        System.out.println();
    //    }
    //}

    public int getScore() {
        return score;
    }

    public void addscore() {
        this.score++;
    }
}

public class PlayerInterface extends JFrame implements Runnable {
    private JPanel contentPane;
    private Socket socket;

    static Player player = new Player();
    static String[][] map = player.getMap();
    static int x;
    static int y;
    Random r = new Random();
    private boolean Run = true;

    {
        try {
            socket = new Socket("localhost", 1234);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    GamePanel game = new GamePanel(map);
    PrintWriter out;

    {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    printMap a = new printMap() {
        @Override
        public void getmap(GamePanel game) {
            game.setMap(player.getMap());
            game.repaint();
        }
    };


    public static void main(String[] args) {
        PlayerInterface frame = null;
        try {
            frame = new PlayerInterface();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frame.setVisible(true);

    }

    public PlayerInterface() throws IOException {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 650, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        //JTextField text = new JTextField("Score: " + player.getScore());
        //text.setBounds(10,140, 85, 21);
        //contentPane.add(text);
        game.setBounds(100, 20, 600, 600);
        game.repaint();
        contentPane.add(game);


        JToggleButton btnToggle = new JToggleButton("AI");
        btnToggle.setBounds(10, 140, 85, 21);
        contentPane.add(btnToggle);
        btnToggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (btnToggle.isSelected()) {
                    Run = true;
                    run();
                } else Run = false;
            }
        });
        JButton btnNewButton = new JButton("Mapa");
        btnNewButton.setBounds(10, 80, 85, 21);
        contentPane.add(btnNewButton);

        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (player.isSpawned()) {
                    out.println("map;" + x + ";" + y);
                    try {
                        String msg = in.readLine();
                        String[] msg2 = msg.split(";");
                        map[x - 1][y - 1] = msg2[0];
                        map[x - 1][y] = msg2[1];
                        map[x - 1][y + 1] = msg2[2];
                        map[x][y - 1] = msg2[3];
                        map[x][y + 1] = msg2[4];
                        map[x + 1][y - 1] = msg2[5];
                        map[x + 1][y] = msg2[6];
                        map[x + 1][y + 1] = msg2[7];
                        player.setMap(map);
                        game.setMap(player.getMap());
                        game.repaint();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        JButton btnNewButton_1 = new JButton("Ruch");
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (player.isSpawned()) {
                    int rX = 0, rY = 0;
                    try {
                        switch (player.getDirection()) {
                            case 0:
                                if (Objects.equals(map[x - 1][y - 1], "b")) {
                                    player.changeDirection();
                                } else {
                                    rX = -1;
                                    rY = -1;
                                }
                                break;
                            case 1:
                                if (Objects.equals(map[x - 1][y], "b")) {
                                    player.changeDirection();
                                } else {
                                    rX = -1;
                                    rY = 0;
                                }
                                break;
                            case 2:
                                if (Objects.equals(map[x - 1][y + 1], "b")) {
                                    player.changeDirection();
                                } else {
                                    rX = -1;
                                    rY = 1;
                                }
                                break;
                            case 3:
                                if (Objects.equals(map[x][y - 1], "b")) {
                                    player.changeDirection();
                                } else {
                                    rX = 0;
                                    rY = -1;
                                }
                                break;
                            case 4:
                                if (Objects.equals(map[x][y + 1], "b")) {
                                    player.changeDirection();
                                } else {
                                    rX = 0;
                                    rY = 1;
                                }
                                break;
                            case 5:
                                if (Objects.equals(map[x + 1][y - 1], "b")) {
                                    player.changeDirection();
                                } else {
                                    rX = 1;
                                    rY = -1;
                                }
                                break;
                            case 6:
                                if (Objects.equals(map[x + 1][y], "b")) {
                                    player.changeDirection();
                                } else {
                                    rX = 1;
                                    rY = 0;
                                }
                                break;
                            case 7:
                                if (Objects.equals(map[x + 1][y + 1], "b")) {
                                    player.changeDirection();
                                } else {
                                    rX = 1;
                                    rY = 1;
                                }
                                break;
                        }
                        out.println("ruch" + ";" + x + ";" + y + ";" + (x + rX) + ";" + (y + rY));
                        String msg = in.readLine();
                        switch (msg) {
                            case "y":
                                x += rX;
                                y += rY;
                                player.setCoordinates(x, y);
                                game.setMap(player.getMap());
                                game.repaint();
                                break;

                            case "n":
                                System.out.println("Nie mozna wykonac takiego ruchu");
                                player.changeDirection();
                                break;
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        btnNewButton_1.setBounds(10, 50, 85, 21);
        contentPane.add(btnNewButton_1);

        JButton btnNewButton_2 = new JButton("Branie");
        btnNewButton_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String msg = in.readLine();
                        if (player.isSpawned()) {
                            switch (treasureLocation().size()) {
                                case 2:
                                    out.println("skarb;" + nearTile("t") + ";" + treasureLocation().get(0) + ";" + treasureLocation().get(1));
                                    break;

                                case 4:
                                    out.println("skarb;" + nearTile("t") + ";" + treasureLocation().get(0) + ";" + treasureLocation().get(1) + ";" + treasureLocation().get(2) + ";" + treasureLocation().get(3));
                                    break;

                                case 6:
                                    out.println("skarb;" + nearTile("t") + ";" + treasureLocation().get(0) + ";" + treasureLocation().get(1) + ";" + treasureLocation().get(2) + ";" + treasureLocation().get(3) + ";" + treasureLocation().get(4) + ";" + treasureLocation().get(5));
                                    break;

                                default:
                                    out.println("skarb;" + nearTile("t"));
                                    break;
                            }
                                String[] msg2 = msg.split(";");
                                int[] cooldowns = new int[msg2.length];
                                int min = 10, ite = 0;//ite should be the lowest tresure numba
                                for (int i = 0; i < msg2.length; i++) {
                                    cooldowns[i] = Integer.parseInt(msg2[i]);
                                    if (cooldowns[i] < min) {
                                        min = cooldowns[i];
                                        ite = i;
                                    }
                                }
                                switch (ite) {
                                    case 0:
                                        int x = treasureLocation().get(0);
                                        int y = treasureLocation().get(1);
                                        player.setCooldown(cooldowns[0]);
                                        player.addscore();
                                        map[x][y] = "e";
                                        out.println("taken;" + x + ";" + y);
                                        break;
                                    case 1:
                                        x = treasureLocation().get(2);
                                        y = treasureLocation().get(3);
                                        player.setCooldown(cooldowns[1]);
                                        player.addscore();
                                        map[x][y] = "e";
                                        out.println("taken;" + x + ";" + y);
                                        break;
                                    case 2:
                                        x = treasureLocation().get(4);
                                        y = treasureLocation().get(5);
                                        player.setCooldown(cooldowns[2]);
                                        player.addscore();
                                        map[x][y] = "e";
                                        out.println("taken;" + x + ";" + y);
                                        break;
                                }
                                //text.setText("Score: " + player.getScore());
                                int n = player.getCooldown();
                                for (int i = 0; i < n; i++) {
                                    player.cooldownDown();
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                                player.setMap(map);
                                game.setMap(player.getMap());
                                game.repaint();
                            }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        btnNewButton_2.setBounds(10, 110, 85, 21);
        contentPane.add(btnNewButton_2);

        JButton btnNewButton_3 = new JButton("Spawn");
        btnNewButton_3.setBounds(10, 20, 85, 21);
        btnNewButton_3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (player.firstmove()) {
                    out.println("start");
                    try {
                        String msg = in.readLine();
                            String[] msg2 = msg.split(";");
                            x = Integer.parseInt(msg2[0]);
                            y = Integer.parseInt(msg2[1]);
                            player.setCoordinates2(x, y);
                            player.setCoordinates(x, y);
                            player.setSpawned();
                            map[x][y] = "p";
                            player.setMap(map);
                            player.isFirstMove();
                            game.setMap(player.getMap());
                            game.repaint();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        contentPane.add(btnNewButton_3);

        JButton button4 = new JButton("Change direction");
        button4.setBounds(10, 170, 85, 21);
        button4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.changeDirection();
            }
        });
        contentPane.add(button4);
        contentPane.setVisible(true);
    }

    public boolean nearTile(String tile) {
        boolean boooool = false;
        do {
            if (Objects.equals(map[x - 1][y - 1], tile)) {
                boooool = true;
                break;
            } else if (Objects.equals(map[x - 1][y], tile)) {
                boooool = true;
                break;
            } else if (Objects.equals(map[x - 1][y + 1], tile)) {
                boooool = true;
                break;
            } else if (Objects.equals(map[x][y - 1], tile)) {
                boooool = true;
                break;
            } else if (Objects.equals(map[x][y + 1], tile)) {
                boooool = true;
                break;
            } else if (Objects.equals(map[x + 1][y - 1], tile)) {
                boooool = true;
                break;
            } else if (Objects.equals(map[x + 1][y], tile)) {
                boooool = true;
                break;
            } else if (Objects.equals(map[x + 1][y + 1], tile)) {
                boooool = true;
                break;
            }
        } while (false);
        return boooool;
    }

    public ArrayList<Integer> treasureLocation() {
        ArrayList<Integer> locations = new ArrayList<>();

        if (Objects.equals(map[x - 1][y - 1], "t")) {
            locations.add(x - 1);
            locations.add(y - 1);
        }
        if (Objects.equals(map[x - 1][y], "t")) {
            locations.add(x - 1);
            locations.add(y);
        }
        if (Objects.equals(map[x - 1][y + 1], "t")) {
            locations.add(x - 1);
            locations.add(y + 1);
        }
        if (Objects.equals(map[x][y - 1], "t")) {
            locations.add(x);
            locations.add(y - 1);
        }
        if (Objects.equals(map[x][y + 1], "t")) {
            locations.add(x);
            locations.add(y + 1);
        }
        if (Objects.equals(map[x + 1][y - 1], "t")) {
            locations.add(x + 1);
            locations.add(y - 1);
        }
        if (Objects.equals(map[x + 1][y], "t")) {
            locations.add(x + 1);
            locations.add(y);
        }
        if (Objects.equals(map[x + 1][y + 1], "t")) {
            locations.add(x + 1);
            locations.add(y + 1);
        }
        return locations;
    }

    @Override
    public void run() {
        while (Run) {
            if (player.firstmove()) {
                out.println("start");
                try {
                    String msg = in.readLine();
                    if (Objects.equals(msg, "1")) {
                        System.out.println("Game has not started yet!");
                    } else {
                        String[] msg2 = msg.split(";");
                        x = Integer.parseInt(msg2[0]);
                        y = Integer.parseInt(msg2[1]);
                        player.setCoordinates2(x, y);
                        player.setCoordinates(x, y);
                        map[x][y] = "p";
                        player.setMap(map);
                        player.isFirstMove();
                        a.getmap(game);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else if (nearTile("f")) {
                out.println("map;" + x + ";" + y);
                try {
                    String msg = in.readLine();
                    String[] msg2 = msg.split(";");
                    map[x - 1][y - 1] = msg2[0];
                    map[x - 1][y] = msg2[1];
                    map[x - 1][y + 1] = msg2[2];
                    map[x][y - 1] = msg2[3];
                    map[x][y + 1] = msg2[4];
                    map[x + 1][y - 1] = msg2[5];
                    map[x + 1][y] = msg2[6];
                    map[x + 1][y + 1] = msg2[7];
                    player.setMap(map);
                    a.getmap(game);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else if (nearTile("t")) {
                switch (treasureLocation().size()) {
                    case 2:
                        out.println("skarb;" + nearTile("t") + ";" + treasureLocation().get(0) + ";" + treasureLocation().get(1));
                        break;

                    case 4:
                        out.println("skarb;" + nearTile("t") + ";" + treasureLocation().get(0) + ";" + treasureLocation().get(1) + ";" + treasureLocation().get(2) + ";" + treasureLocation().get(3));
                        break;

                    case 6:
                        out.println("skarb;" + nearTile("t") + ";" + treasureLocation().get(0) + ";" + treasureLocation().get(1) + ";" + treasureLocation().get(2) + ";" + treasureLocation().get(3) + ";" + treasureLocation().get(4) + ";" + treasureLocation().get(5));
                        break;

                    default:
                        out.println("skarb;" + nearTile("t"));
                        break;
                }
                try {
                    String msg = in.readLine();
                    String[] msg2 = msg.split(";");
                    int[] cooldowns = new int[msg2.length];
                    int min = 10, ite = 0;//ite should be the lowest tresure numba
                    for (int i = 0; i < msg2.length; i++) {
                        cooldowns[i] = Integer.parseInt(msg2[i]);
                        if (cooldowns[i] < min) {
                            min = cooldowns[i];
                            ite = i;
                        }
                    }
                    switch (ite) {
                        case 0:
                            int x = treasureLocation().get(0);
                            int y = treasureLocation().get(1);
                            player.setCooldown(cooldowns[0]);
                            player.addscore();
                            map[x][y] = "e";
                            out.println("taken;" + x + ";" + y);
                            break;
                        case 1:
                            x = treasureLocation().get(2);
                            y = treasureLocation().get(3);
                            player.setCooldown(cooldowns[1]);
                            player.addscore();
                            map[x][y] = "e";
                            out.println("taken;" + x + ";" + y);
                            break;
                        case 2:
                            x = treasureLocation().get(4);
                            y = treasureLocation().get(5);
                            player.setCooldown(cooldowns[2]);
                            player.addscore();
                            map[x][y] = "e";
                            out.println("taken;" + x + ";" + y);
                            break;
                    }
                    //text.setText("Score: " + player.getScore());
                    int n = player.getCooldown();
                    for (int i = 0; i < n; i++) {
                        player.cooldownDown();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    player.setMap(map);
                    a.getmap(game);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                int rX = 0, rY = 0;
                try {
                    switch (player.getDirection()) {
                        case 0:
                            if (Objects.equals(map[x - 1][y - 1], "b")) {
                                player.changeDirection();
                            } else {
                                rX = -1;
                                rY = -1;
                            }
                            break;
                        case 1:
                            if (Objects.equals(map[x - 1][y], "b")) {
                                player.changeDirection();
                            } else {
                                rX = -1;
                                rY = 0;
                            }
                            break;
                        case 2:
                            if (Objects.equals(map[x - 1][y + 1], "b")) {
                                player.changeDirection();
                            } else {
                                rX = -1;
                                rY = 1;
                            }
                            break;
                        case 3:
                            if (Objects.equals(map[x][y - 1], "b")) {
                                player.changeDirection();
                            } else {
                                rX = 0;
                                rY = -1;
                            }
                            break;
                        case 4:
                            if (Objects.equals(map[x][y + 1], "b")) {
                                player.changeDirection();
                            } else {
                                rX = 0;
                                rY = 1;
                            }
                            break;
                        case 5:
                            if (Objects.equals(map[x + 1][y - 1], "b")) {
                                player.changeDirection();
                            } else {
                                rX = 1;
                                rY = -1;
                            }
                            break;
                        case 6:
                            if (Objects.equals(map[x + 1][y], "b")) {
                                player.changeDirection();
                            } else {
                                rX = 1;
                                rY = 0;
                            }
                            break;
                        case 7:
                            if (Objects.equals(map[x + 1][y + 1], "b")) {
                                player.changeDirection();
                            } else {
                                rX = 1;
                                rY = 1;
                            }
                            break;
                    }
                    out.println("ruch" + ";" + x + ";" + y + ";" + (x + rX) + ";" + (y + rY));
                    String msg = in.readLine();
                    switch (msg) {
                        case "y":
                            x += rX;
                            y += rY;
                            player.setCoordinates(x, y);
                            a.getmap(game);
                            break;

                        case "n":
                            System.out.println("Nie mozna wykonac takiego ruchu");
                            player.changeDirection();
                            break;
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}