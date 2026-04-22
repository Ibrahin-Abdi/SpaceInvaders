import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameController extends KeyAdapter {
    private GameModel model;
    private GameView view;
    private JFrame window;
    private Timer gameTimer;
    private boolean leftHeld = false, rightHeld = false, spaceHeld = false;

    public GameController() {
        model = new GameModel();
        view = new GameView(model);
        window = new JFrame("Space Invaders");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(view);
        window.pack();
        window.setLocationRelativeTo(null);
        window.addKeyListener(this);
        window.setVisible(true);

        gameTimer = new Timer(16, e -> {
            if (leftHeld) model.movePlayer(-5);
            if (rightHeld) model.movePlayer(5);
            if (spaceHeld && !model.isPaused() && !model.isGameOver()) model.fireBullet();
            model.update();
            view.repaint();
        });
        gameTimer.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_P) model.togglePause();
        if (key == KeyEvent.VK_R) model.reset();

        if (key == KeyEvent.VK_LEFT) leftHeld = true;
        if (key == KeyEvent.VK_RIGHT) rightHeld = true;
        if (key == KeyEvent.VK_SPACE) spaceHeld = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) leftHeld = false;
        if (key == KeyEvent.VK_RIGHT) rightHeld = false;
        if (key == KeyEvent.VK_SPACE) spaceHeld = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameController());
    }
}