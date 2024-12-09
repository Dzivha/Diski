package src;
public class GameState {
    private static final long HALF_LENGTH = 300000; // 5 minutes in milliseconds
    private long gameStartTime;
    private long currentTime;
    private boolean isFirstHalf;
    private boolean isKickOff;
    private boolean isGameStarted;
    private int homeScore;
    private int awayScore;
    private Team kickingTeam;

    public GameState() {
        this.isFirstHalf = true;
        this.isKickOff = true;
        this.isGameStarted = false;
        this.homeScore = 0;
        this.awayScore = 0;
    }

    public void startGame() {
        gameStartTime = System.currentTimeMillis();
        isGameStarted = true;
        setupKickOff();
    }

    public void setupKickOff() {
        isKickOff = true;
        // Randomly select kicking team
        kickingTeam = Math.random() < 0.5 ? Game.getHomeTeam() : Game.getAwayTeam();
    }

    public void update() {
        if (!isGameStarted)
            return;

        currentTime = System.currentTimeMillis() - gameStartTime;

        // Check for half time
        if (currentTime >= HALF_LENGTH && isFirstHalf) {
            isFirstHalf = false;
            isKickOff = true;
            setupKickOff();
            gameStartTime = System.currentTimeMillis(); // Reset timer for second half
        }

        // Check for game end
        if (currentTime >= HALF_LENGTH && !isFirstHalf) {
            isGameStarted = false;
        }
    }

    public void checkForGoal(Ball ball) {
        // Define goal posts
        final int GOAL_HEIGHT = 100;
        final int GOAL_Y_START = (Game.GAME_HEIGHT - GOAL_HEIGHT) / 2;
        final int GOAL_Y_END = GOAL_Y_START + GOAL_HEIGHT;
        final int GOAL_X_MARGIN = 50;

        // Check left goal (Away team scores)
        if (ball.getX() <= GOAL_X_MARGIN &&
                ball.getY() >= GOAL_Y_START &&
                ball.getY() <= GOAL_Y_END) {
            awayScore++;
            resetAfterGoal();
        }

        // Check right goal (Home team scores)
        if (ball.getX() >= Game.GAME_WIDTH - GOAL_X_MARGIN - ball.getSize() &&
                ball.getY() >= GOAL_Y_START &&
                ball.getY() <= GOAL_Y_END) {
            homeScore++;
            resetAfterGoal();
        }
    }

    private void resetAfterGoal() {
        setupKickOff();
        isKickOff = true;
    }

    // Getters
    public boolean isFirstHalf() {
        return isFirstHalf;
    }

    public boolean isKickOff() {
        return isKickOff;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public Team getKickingTeam() {
        return kickingTeam;
    }

    public long getRemainingTime() {
        return Math.max(0, HALF_LENGTH - currentTime);
    }

    public void setKickOffComplete() {
        isKickOff = false;
    }
}
