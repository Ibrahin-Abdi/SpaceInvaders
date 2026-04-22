public class ModelTester {
    public static void main(String[] args) {
        System.out.println("--- Starting GameModel Tests ---");

        GameModel testModel = new GameModel();

        // 1. Test Left Edge
        testModel.movePlayer(-1000);
        if (testModel.getPlayerX() >= 0) {
            System.out.println("PASS: Player cannot move past left edge.");
        } else {
            System.out.println("FAIL: Player moved past left edge.");
        }

        // 2. Test Right Edge
        testModel.movePlayer(2000);
        if (testModel.getPlayerX() <= 800 - 40) {
            System.out.println("PASS: Player cannot move past right edge.");
        } else {
            System.out.println("FAIL: Player moved past right edge.");
        }

        // 3. Test Bullet Limit
        testModel.fireBullet();
        int bulletCountAfterFirst = testModel.getPlayerBullets().size();
        testModel.fireBullet();
        if (testModel.getPlayerBullets().size() == bulletCountAfterFirst) {
            System.out.println("PASS: Cannot fire while bullets are already in flight.");
        } else {
            System.out.println("FAIL: Fired again while bullets were active.");
        }

        // 4. Test Score Starts at 0
        if (testModel.getScore() == 0) {
            System.out.println("PASS: Score starts at 0.");
        } else {
            System.out.println("FAIL: Score did not start at 0.");
        }

        // 5. Test Starting Lives
        if (testModel.getLives() == 3) {
            System.out.println("PASS: Game starts with 3 lives.");
        } else {
            System.out.println("FAIL: Incorrect starting lives.");
        }

        // 6. Test Triple Shot starts inactive
        if (!testModel.isTripleShot()) {
            System.out.println("PASS: Triple shot is inactive at start.");
        } else {
            System.out.println("FAIL: Triple shot should not be active at start.");
        }

        // 7. Test Reset restores lives and score
        testModel.reset();
        if (testModel.getLives() == 3 && testModel.getScore() == 0) {
            System.out.println("PASS: Reset restores lives and score.");
        } else {
            System.out.println("FAIL: Reset did not restore lives and score.");
        }

        // 8. Test Boss does not spawn at start
        if (testModel.getBoss() == null) {
            System.out.println("PASS: Boss does not spawn until all aliens are cleared.");
        } else {
            System.out.println("FAIL: Boss spawned too early.");
        }

        // 9. Test Victory starts false
        if (!testModel.isVictory()) {
            System.out.println("PASS: Victory state is false at start.");
        } else {
            System.out.println("FAIL: Victory should not be true at start.");
        }

        System.out.println("--- Tests Complete ---");
    }
}