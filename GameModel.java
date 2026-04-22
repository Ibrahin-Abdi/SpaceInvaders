import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;

public class GameModel {
    public final int WIDTH = 800, HEIGHT = 600;
    private int playerX = WIDTH / 2, playerY = HEIGHT - 50;
    private int lives = 3, score = 0;
    private List<Rectangle> playerBullets = new ArrayList<>();
    private List<Rectangle> alienBullets = new ArrayList<>();
    private List<Alien> aliens = new ArrayList<>();
    private List<BlueOrb> blueOrbs = new ArrayList<>();
    private Boss boss = null;
    private int alienDirection = 1;
    private int alienMoveTimer = 0;
    private boolean gameOver = false, isPaused = false, victory = false;
    private boolean tripleShot = false;
    private int tripleShotTimer = 0;
    private static final int TRIPLE_SHOT_DURATION = 625;

    public static class Alien extends Rectangle {
        public int health, type;
        public Alien(int x, int y, int w, int h, int type) {
            super(x, y, w, h);
            this.type = type;
            this.health = (type == 4) ? 5 : (type == 3) ? 3 : (type == 2) ? 2 : 1;
        }
    }

    public static class BlueOrb extends Rectangle {
        public BlueOrb(int x, int y) { super(x, y, 15, 15); }
    }

    public static class Boss extends Rectangle {
        public int health = 50;
        public int vx = 4, vy = 2;
        public Boss(int x, int y) { super(x, y, 80, 40); }
    }

    public GameModel() { reset(); }

    public void reset() {
        playerX = WIDTH / 2; lives = 3; score = 0;
        playerBullets.clear(); alienBullets.clear(); aliens.clear();
        blueOrbs.clear(); tripleShot = false; tripleShotTimer = 0;
        gameOver = false; isPaused = false; victory = false;
        boss = null;
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 11; col++) {
                int type = (row == 0) ? 4 : (row < 2) ? 3 : (row < 4) ? 2 : 1;
                aliens.add(new Alien(col * 60 + 50, row * 40 + 50, 30, 20, type));
            }
        }
    }

    public void togglePause() { isPaused = !isPaused; }
    public boolean isPaused() { return isPaused; }
    public void movePlayer(int dx) { if (!isPaused) playerX = Math.max(0, Math.min(WIDTH - 40, playerX + dx)); }

    public void fireBullet() {
        if (!isPaused && playerBullets.isEmpty()) {
            if (tripleShot) {
                playerBullets.add(new Rectangle(playerX + 5,  playerY, 5, 10));
                playerBullets.add(new Rectangle(playerX + 18, playerY, 5, 10));
                playerBullets.add(new Rectangle(playerX + 31, playerY, 5, 10));
            } else {
                playerBullets.add(new Rectangle(playerX + 18, playerY, 5, 10));
            }
        }
    }

    public void update() {
        if (gameOver || isPaused || victory) return;

        for (int i = 0; i < playerBullets.size(); i++) {
            Rectangle b = playerBullets.get(i);
            b.y -= 10;
            if (b.y < 0) { playerBullets.remove(i); i--; }
        }

        for (int i = 0; i < alienBullets.size(); i++) {
            Rectangle b = alienBullets.get(i);
            b.y += 5;
            if (b.y > HEIGHT) { alienBullets.remove(i); i--; }
        }

        for (int i = 0; i < blueOrbs.size(); i++) {
            BlueOrb orb = blueOrbs.get(i);
            orb.y += 3;
            if (orb.y > HEIGHT) { blueOrbs.remove(i); i--; }
        }

        if (tripleShot) {
            tripleShotTimer--;
            if (tripleShotTimer <= 0) { tripleShot = false; tripleShotTimer = 0; }
        }

        if (aliens.isEmpty() && boss == null) {
            boss = new Boss(WIDTH / 2 - 40, 40);
        }

        if (boss != null) {
            boss.x += boss.vx;
            boss.y += boss.vy;
            if (boss.x <= 0 || boss.x >= WIDTH - boss.width) boss.vx *= -1;
            if (boss.y <= 20 || boss.y >= HEIGHT / 2 - boss.height) boss.vy *= -1;
            if (Math.random() < 0.04) {
                alienBullets.add(new Rectangle(boss.x + boss.width / 2, boss.y + boss.height, 5, 10));
            }
        }

        if (!aliens.isEmpty()) {
            alienMoveTimer++;
            if (alienMoveTimer >= 25) {
                boolean shiftDown = false;
                for (Alien a : aliens) {
                    a.x += (10 * alienDirection);
                    if (a.x > WIDTH - a.width || a.x < 0) shiftDown = true;
                    if (Math.random() < 0.02) {
                        alienBullets.add(new Rectangle(a.x + a.width / 2, a.y + a.height, 5, 10));
                    }
                }
                if (shiftDown) {
                    alienDirection *= -1;
                    for (Alien a : aliens) {
                        a.y += 20;
                        if (a.y > playerY) gameOver = true;
                    }
                }
                alienMoveTimer = 0;
            }
        }

        checkCollisions();
    }

    private void checkCollisions() {
        Rectangle playerRect = new Rectangle(playerX, playerY, 40, 20);

        for (int bi = 0; bi < playerBullets.size(); bi++) {
            Rectangle bullet = playerBullets.get(bi);
            for (int i = 0; i < aliens.size(); i++) {
                Alien a = aliens.get(i);
                if (bullet.intersects(a)) {
                    a.health--;
                    playerBullets.remove(bi); bi--;
                    if (a.health <= 0) {
                        score += a.type * 10;
                        if (a.type == 4) { lives += 2; }
                        else if (a.type == 3) { lives += 1; }
                        else if (a.type == 2 && Math.random() < 0.30) {
                            blueOrbs.add(new BlueOrb(a.x, a.y));
                        }
                        aliens.remove(i);
                    }
                    break;
                }
            }
        }

        if (boss != null) {
            for (int bi = 0; bi < playerBullets.size(); bi++) {
                Rectangle bullet = playerBullets.get(bi);
                if (bullet.intersects(boss)) {
                    boss.health--;
                    playerBullets.remove(bi); bi--;
                    // 40% chance to drop a Blue Orb on every hit
                    if (Math.random() < 0.40) {
                        blueOrbs.add(new BlueOrb(boss.x + boss.width / 2, boss.y + boss.height));
                    }
                    if (boss.health <= 0) {
                        score += 500;
                        victory = true;
                        boss = null;
                    }
                    break;
                }
            }
        }

        for (int i = 0; i < alienBullets.size(); i++) {
            if (alienBullets.get(i).intersects(playerRect)) {
                alienBullets.remove(i);
                lives--;
                if (lives <= 0) gameOver = true;
                break;
            }
        }

        for (int i = 0; i < blueOrbs.size(); i++) {
            if (blueOrbs.get(i).intersects(playerRect)) {
                blueOrbs.remove(i);
                tripleShot = true;
                tripleShotTimer = TRIPLE_SHOT_DURATION;
                break;
            }
        }
    }

    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }
    public List<Alien> getAliens() { return aliens; }
    public List<Rectangle> getPlayerBullets() { return playerBullets; }
    public List<Rectangle> getAlienBullets() { return alienBullets; }
    public List<BlueOrb> getBlueOrbs() { return blueOrbs; }
    public Boss getBoss() { return boss; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public boolean isGameOver() { return gameOver; }
    public boolean isVictory() { return victory; }
    public boolean isTripleShot() { return tripleShot; }
    public int getTripleShotTimer() { return tripleShotTimer; }
    public int getTripleShotDuration() { return TRIPLE_SHOT_DURATION; }
}