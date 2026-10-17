import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Map extends JPanel {

    // Debug Program for Map editor with row and col customization and file loading
    static int[][] defaultMap = new int[][] {
            { 1, 1, 1 },
            { 1, 0, 1 },
            { 1, 1, 1 }
    };
    static int[][] map = null;
    static int cols;
    static int rows;
    static int IconWidth;
    static int IconHeight;
    static int MapWidth;
    static int MapHeight;
    static int MapX;
    static int MapY;
    static int StrokeSize;
    static int startBox;
    static JLabel[] box;
    static Scanner write;
    static Scanner read;
    JFrame frame = Frame.frame;
    String FileName = "Map";

    public static void main(String[] args) {
        Frame.main(args);
    }

    void setPanel() {
        // Panel Stuff
        // setSize(frame.getContentPane().getWidth(),
        // frame.getContentPane().getHeight());
        setSize(frame.getContentPane().getWidth(), frame.getContentPane().getHeight());

        setLocation(0, 0);
        setLayout(null);
        setOpaque(true);
        System.out.println("MapPanel set with the Sizes: " + getWidth() + " " + getHeight());
        System.out.println("MapPanel set with the Positions: " + getX() + " " + getY());

    }

    void refreshVariables() {
        // Map Stuff
        MapWidth = getWidth();
        MapHeight = getHeight();
        MapX = getX();
        MapY = getY();
        IconWidth = MapWidth / cols;
        IconHeight = MapHeight / rows;
    }

    void handleMap() {
        if (map == null) {
            map = createMap();
        }
    }

    Color[] boxColor = new Color[] {
            new Color(57, 255, 20),
            new Color(255, 36, 0),
            new Color(255, 20, 147),
            new Color(255, 255, 0),
            new Color(191, 0, 255),
            new Color(0, 255, 255),
            new Color(0, 245, 255),
            new Color(255, 0, 255),
            new Color(57, 255, 20),
            new Color(57, 255, 20),
    };
    Color[] strokeColor = new Color[] {
            new Color(40, 30, 20),
            new Color(0, 0, 0),
            new Color(0, 0, 0),
            new Color(0, 0, 0),
            new Color(0, 0, 0),
            new Color(0, 0, 0),
            new Color(0, 0, 0),
            new Color(0, 0, 0),
            new Color(0, 0, 0),
            new Color(0, 0, 0),
    };

    void load() {
        System.out.println("Loading Map!");
        removeAll();

        handleMap();
        if (box == null) {
            box = new JLabel[rows * cols];
        }
        refreshVariables();

        for (int i = 0; i < box.length; i++) {
            int rowIndex = i / cols;
            int colIndex = i % cols;
            int strokeSize = 0;

            if (box[i] == null) {
                box[i] = new JLabel();
                add(box[i]);
            }
            int mapNumber = map[rowIndex][colIndex];
            box[i].setSize(IconWidth, IconHeight);
            if (mapNumber == 1) {
                Player.Collisions.add(Integer.valueOf(i));
            }
            if (mapNumber == 0) {
                Player.Collisions.remove(Integer.valueOf(i));
            }

            int nextBoxNumber = i + 1;
            boolean bR = false;

            if (nextBoxNumber < box.length) {
                int nextRowIndex = nextBoxNumber / cols;
                int nextColIndex = nextBoxNumber % cols;
                int nextMapNumber = map[nextRowIndex][nextColIndex];

                if (mapNumber == nextMapNumber) {
                    bR = true;
                }
            }
            ImageIcon icon = drawIcon(IconWidth, IconHeight, boxColor[mapNumber], strokeColor[mapNumber],
                    strokeSize, bR);
            if (mapNumber == 9) {
                startBox = i;
                icon = drawSpawnIcon(IconWidth, IconHeight, 5, mapNumber, icon);
            }
            box[i].setIcon(icon);
            positionLabel(box[i], rowIndex, colIndex);
        }
        if (startBox == 0) {
            startBox = map[0].length + 1;
        }

    }

    ImageIcon drawSpawnIcon(int width, int height, int strokesize, int mapNumber, ImageIcon icon) {
        Image image = icon.getImage();
        Graphics gr = image.getGraphics();
        Graphics2D g2d = (Graphics2D) gr;

        g2d.setColor(Color.white);
        g2d.fillArc(width / 4, height / 4, width / 2, height / 2, 360, 360);
        g2d.setStroke(new BasicStroke(strokesize));
        g2d.setColor(Color.black);
        g2d.drawArc(width / 4, height / 4, width / 2, height / 2, 360, 360);
        return new ImageIcon(image);
    }

    void positionLabel(JLabel label, int rowIndex, int colIndex) {
        label.setLocation(colIndex * IconWidth, rowIndex * IconHeight);
    }

    int[][] createMap() {
        System.out.println("Creating Map!");
        write = new Scanner(System.in);
        cols = 0;
        rows = 0;

        if (!FileName.startsWith("Maps/")) {
            FileName = "Maps/" + FileName;
        }

        if (!FileName.endsWith(".map")) {
            FileName = FileName + ".map";
        }
        File file = new File(FileName);

        try {
            if (file.exists()) {
                // File exists!
                System.out.println("File is existing!");
            } else {
                System.out.println("File: " + file + " does not Exist!");
                throw new Exception();
            }
            // Read...
            read = new Scanner(file);
            ArrayList<Integer> FileContent = new ArrayList<Integer>();

            while (read.hasNextLine()) {
                rows++;

                String line = read.nextLine();
                for (int i = 0; i < line.length(); i++) {
                    char currentChar = line.charAt(i);

                    // If Array is a Number
                    if (Character.isDigit(currentChar)) {
                        // Add to Array
                        FileContent.add(Character.getNumericValue(currentChar));

                        if (rows == 1) {
                            cols++;
                        }
                    }
                }
            }

            int[][] finalMap = new int[rows][cols];

            for (int i = 0; i < FileContent.size(); i++) {
                int rowIndex = i / cols;
                int colIndex = i % cols;
                finalMap[rowIndex][colIndex] = FileContent.get(i);
            }

            if (finalMap.length == 0) {
                System.out.println("File is empty!");
                cols = defaultMap[0].length;
                rows = defaultMap.length;
                return defaultMap;
            }
            read.close();
            write.close();
            System.out.println("Created Map!");
            return finalMap;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while creating Map!");
            return defaultMap;
        }
    }

    ImageIcon drawIcon(int width, int height, Color BoxColor, Color StrokeColor, int StrokeSize, boolean bR) {
        Map.StrokeSize = StrokeSize;
        BufferedImage image = new BufferedImage(width * 2, height, BufferedImage.TYPE_INT_RGB);
        Graphics gr = image.getGraphics();
        Graphics2D g2d = (Graphics2D) gr;
        g2d.setColor(BoxColor);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(StrokeColor);
        g2d.setStroke(new BasicStroke(StrokeSize));

        if (!bR) {
            g2d.drawRect(StrokeSize / 2, StrokeSize / 2, width - StrokeSize, height - StrokeSize);
        } else if (bR) {
            g2d.drawRect(StrokeSize / 2, StrokeSize / 2, width * 2, height - StrokeSize);
            // System.out.println("Right borders with pair");
        }
        return new ImageIcon(image);
    }

    public void paint(Graphics g) {
        super.paint(g);
    }
}