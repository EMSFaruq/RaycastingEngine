import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Ray extends JPanel {

    // Raycast values
    static int resolution = 20;
    static int fov = 90;

    // Ray
    static int res;
    static int rayHeight;
    static int raySpeed;
    static boolean raycast;
    static boolean doLines = false;
    static int renderDistance;

    // 3d Visualization
    static double x;
    static double distances;
    static double height;

    static JSlider fovSlider = new JSlider();
    static JSlider resSlider = new JSlider();
    static JLabel fovText = new JLabel();
    static JLabel resText = new JLabel();

    public static void main(String[] args) {
        Frame.main(args);
    }

    JFrame frame = Frame.frame;
    Map m = new Map();

    void setPanel() {
        // Panel Stuff
        // setSize(Map.MapWidth, Map.MapHeight);
        setSize(frame.getContentPane().getWidth(), frame.getContentPane().getHeight());
        setLocation(Map.MapX, Map.MapY);
        setLayout(null);
        setOpaque(false);
        System.out.println("RayPanel set with the Sizes: " + getWidth() + " " + getHeight());
        System.out.println("RayPanel set with the Positions: " + getX() + " " + getY());

    }

    void setSliders() {
        if (fov > 0) {
            fovSlider.setValue(fov);
        }
        fovSlider.setSize(150, 75);
        resSlider.setLocation(0, 25);
        fovSlider.setMinimum(45);
        fovSlider.setMaximum(135);
        fovSlider.setFocusable(false);
        fovSlider.setBackground(Color.white);
        fovSlider.setForeground(Color.black);

        fovText.setSize(100, 60);
        fovText.setText("FOV: " + fov);
        fovText.setLocation(fovSlider.getX(), fovText.getHeight() / 2);

        if (resolution > 1) {
            resSlider.setValue(resolution);
        }

        resSlider.setSize(150, 75);
        resSlider.setLocation(getWidth() - resSlider.getWidth(), 0);
        resSlider.setMinimum(1);
        resSlider.setMaximum(60);
        resSlider.setFocusable(false);
        resSlider.setBackground(Color.white);
        resSlider.setForeground(Color.black);
        resText.setSize(100, 60);
        resText.setText("Resolution: " + resolution);
        resText.setLocation(resSlider.getX(), resText.getHeight() / 2);

        fovSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fov = fovSlider.getValue();
                fovText.setText("FOV: " + fov);
                repaint(); // neu zeichnen
            }
        });

        resSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                resolution = resSlider.getValue();
                resText.setText("Resolution: " + resolution);
                repaint(); // neu zeichnen
            }
        });

    }

    void addSliders() {
        frame.add(fovText);
        frame.add(resText);
        frame.add(fovSlider);
        frame.add(resSlider);
    }

    void drawRays(Graphics2D g2d) {
        int repeats = getWidth() / resolution;
        rayHeight = 3;
        raySpeed = 4;
        res = resolution / 10;
        renderDistance = 10;

        x = (resolution / 2);

        for (int i = 0; i < repeats; i++) {
            // x von -1 (links) bis +1 (rechts)
            double cameraX = 2.0 * i / repeats - 1;
            double rayAngle = Math.toDegrees(Math.atan(cameraX * Math.tan(Math.toRadians(fov / 2.0)))) + Player.dir;

            drawRay(g2d, rayAngle);
            x += resolution;
        }
    }

    void drawRay(Graphics2D g2d, double rayDirection) {
        g2d.setStroke(new BasicStroke(res));

        g2d.setColor(Color.black);
        int[] newPos = move((int) Player.x, (int) Player.y, rayDirection, rayHeight, raySpeed);
        int newX = newPos[0];
        int newY = newPos[1];

        if (doLines) {
            g2d.drawLine((int) Player.x, (int) Player.y, newX, newY);
        }

        int rectWidth = (int) Math.abs(rayHeight * Math.cos(Math.toRadians(rayDirection)));
        int rectHeight = (int) Math.abs(rayHeight * Math.sin(Math.toRadians(rayDirection)));

        g2d.setColor(Color.white);

        if (doLines) {
            g2d.drawRect(newX, newY, rectWidth, rectHeight);
        }

        // Distances calculation - corrected to use radians
        distances = Point2D.distance(Player.x, Player.y, newX, newY);
        distances = distances * Math.cos(Math.toRadians(rayDirection - Player.dir));

        // Height calculation - Clamping the distance to avoid extremely large values
        g2d.setColor(Color.black);

        double projectionPlaneDistance = getWidth() / (2 * Math.tan(Math.toRadians(fov / 2)));
        height = Math.max(64 / distances * projectionPlaneDistance, 0);
        int currentBox = newPos[2];
        int rowIndex = currentBox / Map.cols;
        int colIndex = currentBox % Map.cols;
        int mapNumber = Map.map[rowIndex][colIndex];
        Color color = m.boxColor[mapNumber];

        g2d.setColor(color);
        int horizon = getHeight() / 2 + Player.pitch;

        int wallTop = horizon - (int) height;
        int wallHeight = (int) height * 2;
        g2d.fillRect((int) x - resolution / 2, wallTop, resolution, wallHeight);
    }

    Player p = new Player();

    // Returns x, y, touchedBox
    int[] move(int posX, int posY, double direction, int height, double moveSpeed) {
        double newX = posX;
        double newY = posY;

        while (!p.touchBorder((int) newX, (int) newY, height)) {
            newX += moveSpeed * Math.cos(Math.toRadians(direction));
            newY += moveSpeed * Math.sin(Math.toRadians(direction));
        }
        int touchedBox = p.touchedBox((int) newX, (int) newY, height);

        while (p.touchBorder((int) newX, (int) newY, height)) {
            newX += -0.5 * Math.cos(Math.toRadians(direction));
            newY += -0.5 * Math.sin(Math.toRadians(direction));
        }

        return new int[] { (int) newX, (int) newY, touchedBox };
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (raycast) {
            Graphics2D g2d = (Graphics2D) g;
            // Decke bis Horizon
            int horizon = getHeight() / 2 + Player.pitch;

            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, 0, getWidth(), horizon);

            // Boden ab Horizon
            g2d.setColor(Color.GRAY);
            g2d.fillRect(0, horizon, getWidth(), getHeight() - horizon);

            if (Player.doCollision && !p.touchBorder(Player.x, Player.y, Player.height)) {
                drawRays((Graphics2D) g);
            }

        }
    }
}