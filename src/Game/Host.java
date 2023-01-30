package Game;

import GameObjects.EmptySpace;
import GameObjects.GameObject;
import GameObjects.Treasure;
import GameObjects.Wall;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static java.lang.Thread.sleep;
//
//
//todo Refractor, make code easy to see through add comments, add more classes, make code 'prettier' in general
//
//  michalzajdel
//
//  cd lab06
//  javac -sourcepath src/ -d build/production/Lab06 src/Game/Host.java
//  java.exe -p "build/production/Lab06" -m Lab06/Game.Host
//  java.exe -p "build/production/Lab06" -m Lab06/Game.PlayerInterface
//  jar cfv Lab06_pop.jar -C build/production/Lab06 .
//  java -p Lab06_pop.jar -m Lab06/Game.Host
//  java -p Lab06_pop.jar -m Lab06/Game.PlayerInterface
//
//
//
public class Host extends JFrame {
    Random r = new Random();JLabel time = new JLabel("15:00");
    boolean gamestart = false; boolean gamestop = false;
    private JPanel contentPane;
    private int n = 128;
    String[][] map = new String[n][n];
    GamePanel game;
    GameObject[][] ObjectMap = new GameObject[n][n];
    public Host() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 650, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 || i == n-1 || j == 0 || j == n-1){
                    map[i][j] = "b";
                }
                else map[i][j] = "e";
            }
        }
        for (int i = 0; i < 3; i++) {
            map[r.nextInt(n-2)+1][r.nextInt(n-2)+1] = "t";
        }

        this.map = readMap();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                switch(map[i][j]){
                    case "t":
                        ObjectMap[i][j] = new Treasure();
                        break;
                    case "e":
                        ObjectMap[i][j] = new EmptySpace();
                        break;
                    case "b":
                        ObjectMap[i][j] = new Wall();
                        break;
                }
            }
        }

        game = new GamePanel(map);
        game.setBounds(100,20,600,600);
        game.repaint();
        contentPane.add(game);

        JButton button = new JButton("Refresh");
        button.setBounds(10,20,85,200);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                game.setMap(map);
                game.repaint();
             }
            });
        contentPane.add(button);

        time.setBounds(10 ,250,85,30);
        contentPane.add(time);
        contentPane.setVisible(true);
        }

        public String[][] getMap(){
        return map;
        }
        public void repaintMap(){
        game.setMap(map);
        game.repaint();
        }
    private ServerSocket serverSocket;

    public void start(int port) {
        ArrayList<ClientHandler> Players = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        synchronized (ClientHandler.class){
            int i =0;
        while (i<4) {
            try {
                Players.add(new ClientHandler(serverSocket.accept()));
                Players.get(i).start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            i++;
        }
        gamestart = true;
            System.out.println("Game can start now.\n 5 players have connected.");
        }
        long start = System.currentTimeMillis();
        long curr = System.currentTimeMillis();
        System.out.println("15 minutes");
        while (curr - start < 900000){
            int sekundy = (int) ((900000 - (curr - start))/1000)%60;
            int minuty = (int) (900000 - (curr - start))/1000/60;
            System.out.println(minuty + ":" + sekundy);
            time.setText(minuty + ":" + sekundy);
            curr = System.currentTimeMillis();
            repaintMap();
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        gamestart = false;
        gamestop = true;
        for (ClientHandler player : Players){
            player.stop();
        }
    }
    public void setMap(String[][] map) {
        this.map = map;
    }
    public class ClientHandler extends Thread{
        Socket soc = null;
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            long curTime = System.currentTimeMillis();
            while (true) {
                if (gamestart){
                try {
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String msg = in.readLine();
                    String[] msg2 = msg.split(";");
                    switch (msg2[0]) {
                        case "map":
                            int x = Integer.parseInt(msg2[1]);
                            int y = Integer.parseInt(msg2[2]);
                            map[x][y] = "p";
                            out.println(map[x - 1][y - 1] + ";" + map[x - 1][y] + ";" + map[x - 1][y + 1] + ";"
                                    + map[x][y - 1] + ";" + map[x][y + 1] + ";"
                                    + map[x + 1][y - 1] + ";" + map[x + 1][y] + ";" + map[x + 1][y + 1]);
                            break;

                        case "ruch":
                            int xruch = Integer.parseInt(msg2[1]);
                            int yruch = Integer.parseInt(msg2[2]);
                            int x2ruch = Integer.parseInt(msg2[3]);
                            int y2ruch = Integer.parseInt(msg2[4]);
                                if (!Objects.equals(map[x2ruch][y2ruch], "p") && !Objects.equals(map[x2ruch][y2ruch], "b")) {
                                    out.println("y");
                                    map[xruch][yruch] = "e";
                                    map[x2ruch][y2ruch] = "p";
                                } else {
                                    out.println("n");
                                }
                            break;

                        case "skarb":
                            switch (msg2.length) {
                                case 2:
                                    out.println("whar?");
                                    break;
                                case 4:
                                    out.println(ObjectMap[Integer.parseInt(msg2[2])][Integer.parseInt(msg2[3])].getCooldown());
                                    break;
                                case 6:
                                    out.println(ObjectMap[Integer.parseInt(msg2[2])][Integer.parseInt(msg2[3])].getCooldown() + ";" + ObjectMap[Integer.parseInt(msg2[4])][Integer.parseInt(msg2[5])].getCooldown());
                                    break;
                                case 8:
                                    out.println(ObjectMap[Integer.parseInt(msg2[2])][Integer.parseInt(msg2[3])].getCooldown() + ";" + ObjectMap[Integer.parseInt(msg2[4])][Integer.parseInt(msg2[5])].getCooldown() + ";" + ObjectMap[Integer.parseInt(msg2[6])][Integer.parseInt(msg2[7])].getCooldown());
                                    break;
                            }
                            break;
                        case "taken":
                            map[Integer.parseInt(msg2[1])][Integer.parseInt(msg2[2])] = "e";break;

                        case "start":
                            int r1;
                            int r2;
                            while(true){
                                r1 = r.nextInt(n);
                                r2 = r.nextInt(n);
                                if (Objects.equals(map[r1][r2], "e")) break;
                            }
                            out.println(r1 + ";" + r2);
                            break;

                        default:
                            break;
                    }

                    curTime = System.currentTimeMillis();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else if(gamestop){
                break;
                }else{
                    try {
                        out = new PrintWriter(clientSocket.getOutputStream(), true);
                        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String msg = in.readLine();
                        out.println("1");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        }
    }

    public static void main(String[] args){
        Host host = new Host();
        host.setVisible(true);
        host.start(1234);

    }

    public String[][] readMap() {
        BufferedReader czytacz = null;
        String[][] cMap=  new String[128][128];
        try {
            czytacz = new BufferedReader(new FileReader("darkest.txt"));
        } catch (IOException e) {
            System.out.println("Błąd inicjacji bufora.");
        }

        String line = "";
        int wiersz = 0;

        try {
            while (czytacz.ready()) {
                try {
                    line = czytacz.readLine();
                } catch (IOException e) {
                    System.out.println("Błąd odczytu pliku");
                }
                for (int kolumna = 0; kolumna < line.length(); kolumna++) {
                     switch (line.charAt(kolumna)){
                         case 'b':
                             cMap[kolumna][wiersz] = "b";
                             break;
                         case 'a':
                             cMap[kolumna][wiersz] = "e";
                             break;
                         case 'c':
                             cMap[kolumna][wiersz] = "t";
                             break;
                     }
                }
                wiersz++;
            }
        } catch (IOException e) {
            System.out.println("Unable to read from buffer");
        }
        return cMap;
    }
}

