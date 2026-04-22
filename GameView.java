import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GameView extends JPanel {
    private GameModel model;
    private int[] starX, starY;
    private static final int STAR_COUNT = 100;

    public GameView(GameModel model) {
        this.model = model;
        this.setBackground(Color.BLACK);
        this.setPreferredSize(new Dimension(800, 600));
        Random rand = new Random();
        starX = new int[STAR_COUNT];
        starY = new int[STAR_COUNT];
        for (int i = 0; i < STAR_COUNT; i++) {
            starX[i] = rand.nextInt(800);
            starY[i] = rand.nextInt(600);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int cw = getWidth();
        int ch = getHeight();

        // 0. Starfield
        for (int i = 0; i < STAR_COUNT; i++) {
            g2.setColor(i % 3 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
            int size = (i % 5 == 0) ? 2 : 1;
            g2.fillOval(starX[i], starY[i], size, size);
        }

        // 1. Draw Player Ship
        drawPlayerShip(g2, model.getPlayerX(), model.getPlayerY());

        // 2. Draw Aliens & Health Bars
        for (GameModel.Alien a : model.getAliens()) {
            drawAlien(g2, a);
            if (a.health > 1) {
                int maxH = (a.type == 4) ? 5 : (a.type == 3) ? 3 : 2;
                g2.setColor(Color.GRAY);
                g2.fillRect(a.x, a.y - 8, a.width, 5);
                g2.setColor(Color.GREEN);
                int hWidth = (int)((a.width * a.health) / (double)maxH);
                g2.fillRect(a.x, a.y - 8, hWidth, 5);
            }
        }

        // 3. Draw Boss
        GameModel.Boss boss = model.getBoss();
        if (boss != null) {
            drawBoss(g2, boss);
            g2.setColor(Color.GRAY);
            g2.fillRect(boss.x, boss.y - 12, boss.width, 8);
            g2.setColor(Color.RED);
            int hWidth = (int)((boss.width * boss.health) / 50.0);
            g2.fillRect(boss.x, boss.y - 12, hWidth, 8);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.drawString("BOSS  " + boss.health + "/50", boss.x, boss.y - 14);
        }

        // 4. Draw Player Bullets
        g2.setColor(model.isTripleShot() ? Color.CYAN : Color.YELLOW);
        for (Rectangle b : model.getPlayerBullets()) {
            g2.fillRect(b.x, b.y, b.width, b.height);
        }

        // 5. Draw Alien Bullets
        g2.setColor(Color.RED);
        for (Rectangle b : model.getAlienBullets()) {
            g2.fillRect(b.x, b.y, b.width, b.height);
        }

        // 6. Draw Blue Orbs
        g2.setColor(Color.CYAN);
        for (GameModel.BlueOrb orb : model.getBlueOrbs()) {
            g2.fillOval(orb.x, orb.y, orb.width, orb.height);
        }

        // 7. HUD
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Score: " + model.getScore(), 10, 25);
        g2.drawString("Lives: " + model.getLives(), 10, 45);

        if (model.isTripleShot()) {
            g2.setColor(Color.CYAN);
            g2.drawString("TRIPLE SHOT!", cw - 140, 25);
            int barWidth = 120;
            int filled = (int)((barWidth * model.getTripleShotTimer()) / (double)model.getTripleShotDuration());
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(cw - 140, 30, barWidth, 8);
            g2.setColor(Color.CYAN);
            g2.fillRect(cw - 140, 30, filled, 8);
        }

        // 8. Overlays
        if (model.isPaused()) {
            drawCenteredText(g2, "PAUSED", Color.YELLOW, 36, cw, ch);
        } else if (model.isVictory()) {
            drawCenteredText(g2, "VICTORY!", Color.GREEN, 64, cw, ch);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fm = g2.getFontMetrics();
            String sub = "Final Score: " + model.getScore();
            g2.drawString(sub, (cw - fm.stringWidth(sub)) / 2, ch / 2 + 60);
        } else if (model.isGameOver()) {
            drawCenteredText(g2, "GAME OVER", Color.RED, 48, cw, ch);
        }
    }

    private void drawPlayerShip(Graphics2D g, int x, int y) {
        // Main body triangle pointing up
        int[] bx = {x + 20, x, x + 40};
        int[] by = {y, y + 20, y + 20};
        g.setColor(Color.GREEN);
        g.fillPolygon(bx, by, 3);
        // Cockpit
        g.setColor(Color.CYAN);
        g.fillOval(x + 13, y + 6, 14, 9);
        // Left wing
        int[] lx = {x, x - 8, x + 5};
        int[] ly = {y + 20, y + 20, y + 10};
        g.setColor(new Color(0, 180, 0));
        g.fillPolygon(lx, ly, 3);
        // Right wing
        int[] rx = {x + 40, x + 48, x + 35};
        int[] ry = {y + 20, y + 20, y + 10};
        g.fillPolygon(rx, ry, 3);
        // Engine glow
        g.setColor(Color.ORANGE);
        g.fillOval(x + 14, y + 18, 12, 6);
    }

    private void drawAlien(Graphics2D g, GameModel.Alien a) {
        int x = a.x, y = a.y, w = a.width, h = a.height;

        if (a.type == 4) {
            // Red - big menacing alien with horns
            g.setColor(Color.RED);
            g.fillOval(x + 2, y + 4, w - 4, h - 4);
            // Horns
            g.fillRect(x + 5, y, 5, 7);
            g.fillRect(x + w - 10, y, 5, 7);
            // Angry eyes
            g.setColor(Color.YELLOW);
            g.fillOval(x + 5, y + 7, 8, 6);
            g.fillOval(x + w - 13, y + 7, 8, 6);
            g.setColor(Color.BLACK);
            g.fillOval(x + 7, y + 9, 4, 3);
            g.fillOval(x + w - 11, y + 9, 4, 3);
            // Tentacles
            g.setColor(Color.RED);
            g.drawLine(x + 5,      y + h - 2, x + 2,      y + h + 5);
            g.drawLine(x + 12,     y + h - 2, x + 11,     y + h + 5);
            g.drawLine(x + w - 12, y + h - 2, x + w - 11, y + h + 5);
            g.drawLine(x + w - 5,  y + h - 2, x + w - 2,  y + h + 5);

        } else if (a.type == 3) {
            // Magenta - round alien with antennae
            g.setColor(Color.MAGENTA);
            g.fillOval(x + 2, y + 4, w - 4, h - 4);
            // Antennae
            g.drawLine(x + 8, y + 4, x + 5, y - 3);
            g.drawLine(x + w - 8, y + 4, x + w - 5, y - 3);
            g.fillOval(x + 3,     y - 5, 5, 5);
            g.fillOval(x + w - 8, y - 5, 5, 5);
            // Round eyes
            g.setColor(Color.WHITE);
            g.fillOval(x + 5, y + 7, 8, 7);
            g.fillOval(x + w - 13, y + 7, 8, 7);
            g.setColor(Color.BLACK);
            g.fillOval(x + 7, y + 9, 4, 4);
            g.fillOval(x + w - 11, y + 9, 4, 4);
            // Smile
            g.setColor(Color.WHITE);
            g.drawArc(x + 8, y + h - 8, w - 16, 6, 0, -180);

        } else if (a.type == 2) {
            // Blue - small alien with tentacles
            g.setColor(Color.BLUE);
            g.fillOval(x + 3, y + 3, w - 6, h - 6);
            // Eyes
            g.setColor(Color.WHITE);
            g.fillOval(x + 6,      y + 6, 6, 6);
            g.fillOval(x + w - 12, y + 6, 6, 6);
            g.setColor(Color.CYAN);
            g.fillOval(x + 7,      y + 7, 3, 3);
            g.fillOval(x + w - 11, y + 7, 3, 3);
            // Tentacles
            g.setColor(Color.BLUE);
            g.drawLine(x + 6,      y + h - 3, x + 3,      y + h + 4);
            g.drawLine(x + w / 2,  y + h - 3, x + w / 2,  y + h + 4);
            g.drawLine(x + w - 6,  y + h - 3, x + w - 3,  y + h + 4);

        } else {
            // Green - simple small alien
            g.setColor(Color.GREEN);
            g.fillRect(x + 4, y + 4, w - 8, h - 8);
            // Eyes
            g.setColor(Color.BLACK);
            g.fillOval(x + 6,      y + 6, 4, 4);
            g.fillOval(x + w - 10, y + 6, 4, 4);
            // Legs
            g.setColor(Color.GREEN);
            g.drawLine(x + 6,     y + h - 4, x + 4,     y + h + 3);
            g.drawLine(x + w - 6, y + h - 4, x + w - 4, y + h + 3);
        }
    }

    private void drawBoss(Graphics2D g, GameModel.Boss boss) {
        int x = boss.x, y = boss.y, w = boss.width, h = boss.height;
        // Body
        g.setColor(Color.ORANGE);
        g.fillOval(x + 5, y + 5, w - 10, h - 10);
        // Spikes
        g.setColor(new Color(200, 100, 0));
        g.fillRect(x,           y + h/2 - 5, 10, 10);
        g.fillRect(x + w - 10,  y + h/2 - 5, 10, 10);
        g.fillRect(x + w/2 - 5, y,           10, 10);
        // Eyes
        g.setColor(Color.RED);
        g.fillOval(x + 15,      y + 12, 14, 12);
        g.fillOval(x + w - 29,  y + 12, 14, 12);
        g.setColor(Color.BLACK);
        g.fillOval(x + 19,      y + 15, 6, 6);
        g.fillOval(x + w - 25,  y + 15, 6, 6);
        // Mouth
        g.setColor(Color.BLACK);
        g.fillArc(x + 20, y + h - 18, w - 40, 10, 0, -180);
    }

    private void drawCenteredText(Graphics2D g, String text, Color color, int size, int cw, int ch) {
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, size));
        FontMetrics fm = g.getFontMetrics();
        int x = (cw - fm.stringWidth(text)) / 2;
        int y = (ch / 2) + fm.getAscent() / 2;
        g.drawString(text, x, y);
    }
}