import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Frame implements Runnable {

    // Game Loop
    double delta = 1;
    Thread GameThread;
    long drawCount = 0;
    int FPS = 144;
    static String Title = "Lost Place Together | By MrByte";

    public static void main(String[] args) {
        Frame f = new Frame();
        frame = new JFrame(Title);
        Player p = new Player();
        Map m = new Map();
        Ray r = new Ray();
        f.createFrame();
        f.startGameThread();

        JLabel waitscreen = new JLabel();
        waitscreen.setSize(frame.getWidth(), frame.getHeight());
        ImageIcon waitIcon = new ImageIcon("Assets/LoadingSceen.png");
        waitscreen.setIcon(new ImageIcon(
                waitIcon.getImage().getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_SMOOTH)));
        frame.add(waitscreen);

        m.setPanel();
        m.load();
        r.setPanel();
        r.setSliders();
        r.addSliders();
        p.setPanel();
        // frame.add(p);
        frame.add(r);
        // frame.add(m);
        Ray.raycast = true;
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.remove(waitscreen);
    }

    void startGameThread() {
        GameThread = new Thread(this);
        GameThread.start();

    }

    final long OPTIMAL_TIME = 1000000000 / FPS;

    long lastLoopTime = System.nanoTime();
    long lastFpsTime = 0;
    int fps = 0;

    @Override
    public void run() {
        while (true) {
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            delta = updateLength / ((double) OPTIMAL_TIME);

            lastFpsTime += updateLength;
            fps++;

            if (lastFpsTime >= 1000000000) {
                frame.setTitle(Title + " | FPS: " + fps);
                lastFpsTime = 0;
                fps = 0;
            }

            // Simulate game update and rendering
            update(delta);
            repaint();

            // Sleep to maintain target FPS
            try {
                long sleepTime = (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000;
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    Player p = new Player();

    void update(double delta) {
        // Player.moveSpeed = 1 + ((Player.height * 5) / 50);
        // Player.rotSpeed = 1 + (((Player.height / 50) * 5) / 2);
        // double rotSpeed = 1;

        if (Listener.W) {
            Player.x = p.move(Player.x, Player.y, Player.dir, Player.height, Player.moveSpeed)[0];
            Player.y = p.move(Player.x, Player.y, Player.dir, Player.height, Player.moveSpeed)[1];
        }

        if (Listener.S) {
            Player.x = p.move(Player.x, Player.y, Player.dir, Player.height, -Player.moveSpeed)[0];
            Player.y = p.move(Player.x, Player.y, Player.dir, Player.height, -Player.moveSpeed)[1];
        }

        if (Listener.Left) {
            Player.dir = rotate(Player.dir - Player.rotSpeed);
        }

        if (Listener.Right) {
            Player.dir = rotate(Player.dir + Player.rotSpeed);
        }
        if (Listener.Up) {
            Player.pitch += Player.pitchspeed;
            if (Player.pitch > Player.maxpitch) {
                Player.pitch = Player.maxpitch;
            }
        }
        if (Listener.Down) {
            Player.pitch -= Player.pitchspeed;
            if (Player.pitch < -Player.maxpitch) {
                Player.pitch = -Player.maxpitch;
            }
        }
        if (Listener.A) {
            Player.x = p.move(Player.x, Player.y, Player.dir - 90, Player.height, Player.moveSpeed)[0];
            Player.y = p.move(Player.x, Player.y, Player.dir - 90, Player.height, Player.moveSpeed)[1];
        }

        if (Listener.D) {
            Player.x = p.move(Player.x, Player.y, Player.dir + 90, Player.height, Player.moveSpeed)[0];
            Player.y = p.move(Player.x, Player.y, Player.dir + 90, Player.height, Player.moveSpeed)[1];
        }

        if (Listener.Sprint) {
            Player.moveSpeed = 10;
        } else {
            Player.moveSpeed = Player.defMoveSpeed;
        }
    }

    int rotate(int direction) {
        if (direction < 0) {
            direction += 360;
        }
        if (direction > 360) {
            direction -= 360;
        }
        return direction;
    }

    static JFrame frame;
    boolean resizable = true;
    boolean fullscreen = false;

    void createFrame() {
        frame.setLayout(null);
        frame.setUndecorated(fullscreen);
        frame.pack();
        frame.setSize(getScreenSize());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(resizable);
        frame.setLocation(getScreenPosition());
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon("Assets/LostPlaceTogether.png").getImage());
        frame.addKeyListener(new Listener());
        frame.setVisible(true);
    }

    KeyAdapter keyAdapter = new KeyAdapter() {

        public void keyPressed(KeyEvent e) {
        }
    };

    GraphicsEnvironment getGraphicsEnviroment() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment();
    }

    GraphicsDevice getDefaultDevice() {
        return getGraphicsEnviroment().getDefaultScreenDevice();

    }

    GraphicsDevice currentDevice() {
        return frame.getGraphicsConfiguration().getDevice();
    }

    int currentDeviceNumber() {
        GraphicsDevice currentDevice = frame.getGraphicsConfiguration().getDevice();
        for (int i = 0; i < getDevices().length; i++) {
            if (getDevices()[i] == currentDevice) {
                return i;
            }
        }
        return -1;
    }

    GraphicsDevice[] getDevices() {
        return getGraphicsEnviroment().getScreenDevices();
    }

    Dimension getScreenSize() {
        return new Dimension(getDefaultDevice().getDefaultConfiguration().getBounds().getSize());
    }

    Point getScreenPosition() {
        return new Point(getDefaultDevice().getDefaultConfiguration().getBounds().getLocation());
    }

    void repaint() {
        frame.repaint();
    }
}
