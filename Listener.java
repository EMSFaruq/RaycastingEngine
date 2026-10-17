import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;

class Listener implements KeyListener {

    Graphics2D g2d;
    JFrame frame = Frame.frame;
    Player p = new Player();
    static boolean Up, Down, Left, Right, W, S, A, D, Sprint;

    public static void main(String[] args) {
        Frame.main(args);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                Up = true;
                break;
            case KeyEvent.VK_DOWN:
                Down = true;
                break;
            case KeyEvent.VK_LEFT:
                Left = true;
                break;
            case KeyEvent.VK_RIGHT:
                Right = true;
                break;
            case KeyEvent.VK_W:
                W = true;
                break;
            case KeyEvent.VK_S:
                S = true;
                break;
            case KeyEvent.VK_A:
                A = true;
                break;
            case KeyEvent.VK_D:
                D = true;
                break;
            case KeyEvent.VK_SHIFT:
                Sprint = true;
                break;
            case KeyEvent.VK_0:
                Player.x = p.getStartPoint()[0];
                Player.y = p.getStartPoint()[1];
                Player.dir = 0;
                Player.pitch = 0;
                break;
            case KeyEvent.VK_9:
                Player.doCollision = !Player.doCollision;
                break;
            case KeyEvent.VK_F:
                Ray.raycast = !Ray.raycast;
                break;
            case KeyEvent.VK_G:
                Ray.doLines = !Ray.doLines;
                System.out.println(Ray.doLines);
                break;
            case KeyEvent.VK_I:
                System.out.println("PlayerHeight: " + Player.height);
                System.out.println("PlayerPosition: " + Player.x + " " + Player.y);
                System.out.println("PlayerMoveSpeed: " + Player.moveSpeed);
                System.out.println("PlayerRotSpeed: " + Player.rotSpeed);
                break;

            case KeyEvent.VK_H:
                Map m = new Map();
                frame.remove(m);
                frame.repaint();
                break;

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                Up = false;
                break;
            case KeyEvent.VK_DOWN:
                Down = false;
                break;
            case KeyEvent.VK_LEFT:
                Left = false;
                break;
            case KeyEvent.VK_RIGHT:
                Right = false;
                break;
            case KeyEvent.VK_W:
                W = false;
                break;
            case KeyEvent.VK_S:
                S = false;
            case KeyEvent.VK_A:
                A = false;
                break;
            case KeyEvent.VK_D:
                D = false;
                break;
            case KeyEvent.VK_SHIFT:
                Sprint = false;
                break;
        }
    }
}