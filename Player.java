import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Player
 */

public class Player extends JPanel {

    static double x;
    static double y;
    static int dir = 0;
    static int pitch = 0;
    static int width = 100;
    static int height = 100;
    static int defMoveSpeed = 3;
    static int moveSpeed = defMoveSpeed;
    static int rotSpeed = 1;
    static int pitchspeed = 10;
    static int maxpitch = 270;

    JFrame frame = Frame.frame;
    Map m = new Map();

    public static void main(String[] args) {
        Frame.main(args);
    }

    static int screenWidth;
    static int screenHeight;

    void setPanel() {
        // Panel Stuff
        setSize(Map.MapWidth, Map.MapHeight);
        screenWidth = getWidth();
        screenHeight = getHeight();
        setLocation(Map.MapX, Map.MapY);
        setLayout(null);
        setOpaque(false);
        width = (Map.box[Map.startBox].getHeight() / 2) / 12;
        height = (Map.box[Map.startBox].getHeight() / 2) / 12;
        x = getStartPoint()[0];
        y = getStartPoint()[1];
        System.out.println("PlayerPanel set with the Sizes: " + getWidth() + " " + getHeight());
        System.out.println("PlayerPanel set with the Positions: " + getX() + " " + getY());

    }

    int StrokeSize = 5;

    void drawPlayer(Graphics2D g2d) {
        g2d.setColor(Color.white);
        g2d.rotate(Math.toRadians(dir), x, y);
        g2d.setStroke(new BasicStroke(1));
        g2d.fillPolygon(new int[] { (int) x - width / 2, (int) x - width / 2, (int) x + width / 2 },
                new int[] { (int) y - height / 2, (int) y + height / 2, (int) y }, 3);
        g2d.setStroke(new BasicStroke(5 * (height / 50)));
        g2d.setColor(Color.black);
        g2d.drawPolygon(new int[] { (int) x - width / 2, (int) x - width / 2, (int) x + width / 2 },
                new int[] { (int) y - height / 2, (int) y + height / 2, (int) y }, 3);
        g2d.dispose();

    }

    int[] move(double posX, double posY, double direction, int playerheight, double moveSpeed) {
        // Berechne genauere Schritte als double
        double stepX = moveSpeed * Math.cos(Math.toRadians(direction));
        double stepY = moveSpeed * Math.sin(Math.toRadians(direction));

        // Erst X verschieben, prüfen
        int newX = tryMove(posX, posY, playerheight, (int) Math.round(stepX), 0)[0];
        // Dann Y verschieben, prüfen
        int newY = tryMove(newX, posY, playerheight, 0, (int) Math.round(stepY))[1];

        return new int[] { newX, newY };
    }

    static boolean doCollision = true;

    int[] tryMove(double posX, double posY, int height, int stepsX, int stepsY) {

        posX += stepsX;
        posY += stepsY;
        if (doCollision) {
            // Check if zouched Border
            if (touchBorder(posX, posY, height)) {
                posX -= stepsX;
                posY -= stepsY;
            }
        }

        return new int[] { (int) posX, (int) posY };
    }

    static ArrayList<Integer> Collisions = new ArrayList<>();

    boolean touchBorder(double PosX, double PosY, int height) {

        boolean collision = false;

        for (int i = 0; i < Collisions.size(); i++) {
            JLabel label = Map.box[Collisions.get(Integer.valueOf(i))];
            collision = PosX < 0 + height / 2 || PosX > screenWidth - height / 2 ||
                    PosX > label.getX() - height / 2 &&
                            PosX < label.getX() + label.getWidth() + height / 2 &&
                            PosY > label.getY() - height / 2 &&
                            PosY < label.getY() + label.getHeight() + height / 2
                    || PosY < 0 + height / 2 || PosY > screenHeight - height / 2;

            if (collision) {
                return true;

            }
        }
        return false;

    }

    int touchedBox(int PosX, int PosY, int height) {

        boolean collision = false;

        for (int i = 0; i < Collisions.size(); i++) {
            JLabel label = Map.box[Collisions.get(Integer.valueOf(i))];
            collision = PosX < 0 + height / 2 || PosX > screenWidth - height / 2 ||
                    PosX > label.getX() - height / 2 &&
                            PosX < label.getX() + label.getWidth() + height / 2 &&
                            PosY > label.getY() - height / 2 &&
                            PosY < label.getY() + label.getHeight() + height / 2
                    || PosY < 0 + height / 2 || PosY > screenHeight - height / 2;

            if (collision) {
                return i;
            }

        }

        return -1;
    }

    int[] getStartPoint() {
        JLabel currentBox = Map.box[Map.startBox];

        return new int[] {
                currentBox.getX() + currentBox.getWidth() / 2,
                currentBox.getY() + currentBox.getHeight() / 2 };
    }

    public void paint(Graphics g) {
        super.paint(g);
        drawPlayer((Graphics2D) g);
    }
}