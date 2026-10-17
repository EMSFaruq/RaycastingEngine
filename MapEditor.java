import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MapEditor extends JPanel {

    // Frame
    String Title;
    static JFrame frame = new JFrame();
    int MinimalWidth = 425;
    int MinimalHeight = 425;
    static int width;
    static int height;
    static int PosX;
    static int PosY;
    static boolean Fullscreen = false;
    static boolean Resizeable = true;
    static GraphicsEnvironment ge;
    static GraphicsDevice[] gd;
    static int defaultDevice;
    static int output = 2;
    static GraphicsConfiguration gDimension;
    static Rectangle gSize;
    static Point gPos;

    // Console
    static Scanner write;
    static Scanner read;

    // Map
    static int[][] defaultMap = new int[][] {
            { 1, 1, 1 },
            { 1, 0, 1 },
            { 1, 1, 1 },
    };

    static int defaultRows = 3;
    static int defaultCols = 3;

    static String FileName = "Map";
    static int[][] map;
    static int rows = defaultRows;
    static int cols = defaultCols;
    static int IconWidth;
    static int IconHeight;
    static int MapWidth;
    static int MapHeight;
    static int StrokeSize;
    static JLabel[] box;
    static MapEditor editor = new MapEditor();

    public static void main(String[] args) {
        map = editor.createMap();

        editor.refreshAllVariables();
        new MapEditor().openEditor();
    }

    void refreshAllVariables() {
        updateScreenVariables();
    }

    void updateScreenVariables() {
        try {
            gd[output].getDefaultConfiguration();
        } catch (Exception e) {
            output = defaultDevice;
        }
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gd = ge.getScreenDevices();
        defaultDevice = getObject((Object) ge.getDefaultScreenDevice(), (Object[]) gd);
        gDimension = gd[output].getDefaultConfiguration();
        gSize = gDimension.getBounds();
        gPos = gSize.getLocation();
        width = (int) gSize.getWidth();
        height = (int) gSize.getHeight();
        PosX = (int) gPos.getX();
        PosY = (int) gPos.getY();
    }

    int getObject(Object object, Object[] objectArray) {
        for (int i = 0; i < objectArray.length; i++) {
            if (objectArray[i] == object) {
                return i;
            }
        }
        return -1;
    }

    int[][] createMap() {
        write = new Scanner(System.in);
        System.out.println("Enter the Name of the Map you want to Load!");
        // String FileName = scanner.nextLine();
        rows = 0;
        cols = 0;

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
                throw new Exception();
            }
            // Read...

            read = new Scanner(file);
            ArrayList<Integer> FileContent = new ArrayList<Integer>();

            while (read.hasNextLine()) {
                String line = read.nextLine();
                rows++;
                for (int i = 0; i < line.length(); i++) {
                    char currentChar = line.charAt(i);

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
                // get rows with identifying the amount rows and cols
                finalMap[rowIndex][colIndex] = FileContent.get(i);
            }

            if (finalMap.length == 0) {
                System.out.println("File is Empty!");
                return defaultMap;
            }
            read.close();
            write.close();
            return finalMap;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("File: " + file + " does not Exist!");
            return defaultMap;
        }

    }

    // Generates a map for example if no map found in file
    void generateMap() {
        System.out.println("What Width should the Map be?");

        int IntInput;

        IntInput = 0;
        int newRows = defaultRows;
        String input = write.nextLine();

        while (IntInput == 0) {
            input = write.nextLine();
            try {
                IntInput = Integer.parseInt(input);
                System.out.println("Map-Width set to: " + IntInput);
            } catch (Exception e1) {
                System.out.println("Please insert a valid Width!");
            }
        }
        System.out.println("What Height should the Map be?");

        IntInput = 0;
        int newCols = defaultCols;

        while (IntInput == 0) {
            input = write.nextLine();
            try {
                IntInput = Integer.parseInt(input);
                newCols = IntInput;
                System.out.println("Map-Height set to: " + IntInput);
            } catch (Exception e1) {
                System.out.println("Please insert a valid Height!");
            }
        }

        int[][] generatedMap = new int[newRows][newCols];
        for (int i = 0; i < newRows * newCols; i++) {
            int rowIndex = i / cols;
            int colIndex = i % cols;
            if (colIndex <= generatedMap[0][generatedMap[0].length]) {
                generatedMap[rowIndex][colIndex] = 1;
            }
        }
        map = generatedMap;
    }

    void openEditor() {
        frame.setUndecorated(Fullscreen);

        MapEditor m = new MapEditor();
        frame.pack();
        createGrids();
        frame.add(m);

        if (Fullscreen) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        frame.setTitle("Map Editor | By EMSFaruq");
        Title = frame.getTitle();
        frame.setResizable(Resizeable);
        frame.setLocation(PosX, PosY);
        frame.setMinimumSize(new Dimension(425, 425));
        frame.setSize(width, height);
        frame.setVisible(true);
        frame.setIconImage(drawIcon(16, 16, new int[] { 200, 100, 100 }, new int[] { 0, 0, 0 }, 5).getImage());
        frame.addWindowListener(windowListener);
    }

    void setPanel() {
        cols = map[0].length;
        rows = map.length;
        MapWidth = width;
        MapHeight = height;
        setSize(MapWidth, MapHeight);
    }

    void createGrids() {
        setPanel();

        box = new JLabel[rows * cols];
        for (int i = 0; i < box.length; i++) {

            int currentI = i;

            box[i] = new JLabel();
            box[i].addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    int rowIndex = currentI / cols;
                    int colIndex = currentI % cols;

                    // Add current map as an label for every click so no direct accessing
                    int CurrentArray = map[rowIndex][colIndex];

                    if (CurrentArray == 0) {
                        CurrentArray = 1;
                    } else if (CurrentArray == 1) {
                        CurrentArray = 7;
                    } else if (CurrentArray == 7) {
                        CurrentArray = 0;
                    }
                    map[rowIndex][colIndex] = CurrentArray;
                    saveMap();
                }
            });
        }
        load();
    }

    void refreshVariables() {
        IconWidth = getWidth() / cols;
        IconHeight = getHeight() / rows;
    }

    void saveMap() {
        File file = new File(FileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);

            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    writer.write(map[y][x] + " ");
                }
                writer.write(System.lineSeparator());
            }
            writer.close();
        } catch (Exception e) {

        }
    }

    ImageIcon drawIcon(int width, int height, int[] BoxColor, int[] StrokeColor, int StrokeSize) {
        Map.StrokeSize = StrokeSize;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics gr = image.getGraphics();
        Graphics2D g2d = (Graphics2D) gr;
        g2d.setColor(new Color(BoxColor[0], BoxColor[1], BoxColor[2]));
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(new Color(StrokeColor[0], StrokeColor[1], StrokeColor[2]));
        g2d.setStroke(new BasicStroke(StrokeSize));
        g2d.drawRect(StrokeSize / 2, StrokeSize / 2, width - StrokeSize, height - StrokeSize);
        return new ImageIcon(image);
    }

    void load() {
        removeAll();
        refreshVariables();
        setPanel();

        for (int i = 0; i < box.length; i++) {
            int rowIndex = i / cols;
            int colIndex = i % cols;

            int StrokeSize = 5;
            int[] BoxColor = new int[] { 0, 0, 0 };
            int[] StrokeColor = new int[] { 0, 0, 0 };

            if (map[rowIndex][colIndex] == 0) {
                BoxColor = new int[] { 0, 0, 0 };
                StrokeColor = new int[] { 40, 30, 20 };
            } else if (map[rowIndex][colIndex] == 1) {
                BoxColor = new int[] { 200, 50, 50 };
                StrokeColor = new int[] { 0, 0, 0 };
            } else if (map[rowIndex][colIndex] == 7) {
                BoxColor = new int[] { 50, 200, 50 };
                StrokeColor = new int[] { 0, 0, 0 };
            }

            box[i].setSize(IconWidth, IconHeight);
            box[i].setIcon(drawIcon(IconWidth, IconHeight, BoxColor, StrokeColor, StrokeSize));
            add(box[i]);

            for (int x = 0; x < cols; x++) {
                for (int y = 0; y < rows; y++) {
                    box[y * cols + x].setLocation(x * IconWidth, y * IconHeight);
                }
            }
        }
    }

    WindowListener windowListener = new WindowAdapter() {

        public void windowClosing(WindowEvent e) {
            super.windowClosed(e);
            e.getWindow().dispose();
        }
    };

    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        load();
        frame.repaint();
    }

    ImageIcon drawIconImage() {
        int width = 128;
        int height = 128;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics gr = image.getGraphics();
        Graphics2D g2d = (Graphics2D) gr;

        int IconStroke = 5;
        g2d.setStroke(new BasicStroke(5));

        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.black);
        g2d.drawRect(IconStroke / 2, IconStroke / 2, width - IconStroke / 2, height - IconStroke / 2);

        g2d.setColor(new Color(200, 50, 50));
        g2d.fillRect(width / 3, 0, width / 3, height * 3);

        g2d.setColor(Color.black);
        g2d.drawRect(width / 3 - IconStroke / 2, IconStroke / 2, width / 3 - IconStroke / 2, height - IconStroke / 2);

        g2d.setColor(new Color(200, 50, 50));
        g2d.fillRect(0, height / 3, width, height / 3);

        g2d.setColor(Color.black);
        g2d.drawRect(IconStroke / 2, height / 3 - IconStroke / 2, width - IconStroke / 2, height / 3 - IconStroke / 2);
        g2d.setColor(Color.black);
        g2d.drawRect(width / 3 - IconStroke / 2, height / 3 - IconStroke / 2, width / 3 - IconStroke / 2,
                height / 3 - IconStroke / 2);
        return new ImageIcon(image);
    }
}
