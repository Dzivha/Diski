package src;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;

public class Game extends JPanel implements KeyListener {
    private ControllerInput controller;
    private Team playerTeam;
    private boolean[] keys;
    static final int GAME_WIDTH = 800;
    static final int GAME_HEIGHT = 600;
    private static final int FIELD_MARGIN = 50;
    private static Team homeTeam;
    private static Team awayTeam;
    private Ball ball;
    private GameState gameState;
    private static final int GOAL_POST_THICKNESS = 5;
    private int[][] selectedFormation;
    public static int[][] defaultFormation = {
            { 25, 50 }, // GK
            { 35, 30 }, // Defender
            { 35, 70 }, // Defender
            { 50, 50 }, // Midfielder
            { 65, 50 } // Striker
    };

    public Game(int[][] formation) {
        setFocusable(true);
        requestFocusInWindow(); // new
        addKeyListener(this);
        keys = new boolean[256];
        controller = new ControllerInput();

        // Check if formation is null or empty
        if (formation == null || formation.length == 0) {
            // Use a default formation
            formation = new int[][] {
                    { 25, 50 }, // GK
                    { 35, 30 }, // Defender
                    { 35, 70 }, // Defender
                    { 50, 50 }, // Midfielder
                    { 65, 50 } // Striker
            };
        }

        this.selectedFormation = formation;

        homeTeam = new Team(Color.BLUE);
        awayTeam = new Team(Color.RED);
        ball = new Ball(GAME_WIDTH / 2, GAME_HEIGHT / 2);

        initializeTeams();
        Game.homeTeam = homeTeam;
        Game.awayTeam = awayTeam;

        initializeGameState();
        gameState.startGame();
    }

    private void initializeGameState() {
        gameState = new GameState();
    }

    private void initializeTeams() {

        // Initialize home team based on selected formation
        for (int i = 0; i < selectedFormation.length; i++) {
            int x = (selectedFormation[i][0] * GAME_WIDTH) / 200;
            int y = (selectedFormation[i][1] * GAME_HEIGHT) / 100;

            String position = i == 0 ? "GK" : i <= 2 ? "DEF" : i == 3 ? "MID" : "ST";

            homeTeam.addPlayer(new Player(this, x, y, position, Color.BLUE,
                    position.equals("GK") ? 70 : 75, // speed
                    position.equals("GK") ? 30 : 65, // passing
                    position.equals("ST") ? 90 : 60, // shooting
                    position.equals("DEF") ? 85 : 70, // tackling
                    position.equals("GK") ? 80 : 70, // goalkeeping
                    75 // stamina
            ));
        }
        // Away team (mirror of home team formation)
        for (int i = 0; i < selectedFormation.length; i++) {
            int x = GAME_WIDTH - ((selectedFormation[i][0] * GAME_WIDTH) / 200);
            int y = (selectedFormation[i][1] * GAME_HEIGHT) / 100;

            String position = i == 0 ? "GK" : i <= 2 ? "DEF" : i == 3 ? "MID" : "ST";

            awayTeam.addPlayer(new Player(this, x, y, position, Color.RED,
                    position.equals("GK") ? 70 : 75, // speed
                    position.equals("GK") ? 30 : 65, // passing
                    position.equals("ST") ? 90 : 60, // shooting
                    position.equals("DEF") ? 85 : 70, // tackling
                    position.equals("GK") ? 80 : 70, // goalkeeping
                    75 // stamina
            ));
        }

        // OLD INITIALISATION. STATIC
        // // Home team initialization (5 players)
        // homeTeam.addPlayer(new Player(this, GAME_WIDTH / 4, GAME_HEIGHT / 2, "GK",
        // Color.BLUE,
        // 70, 30, 20, 60, 80, 75));
        // homeTeam.addPlayer(new Player(this, GAME_WIDTH / 3, GAME_HEIGHT / 3, "DEF",
        // Color.BLUE,
        // 75, 65, 60, 70, 85, 80));
        // homeTeam.addPlayer(new Player(this, GAME_WIDTH / 3, 2 * GAME_HEIGHT / 3,
        // "DEF", Color.BLUE,
        // 75, 65, 60, 70, 85, 80));
        // homeTeam.addPlayer(new Player(this, GAME_WIDTH / 2.5, GAME_HEIGHT / 3, "MID",
        // Color.BLUE,
        // 80, 85, 75, 85, 70, 70));
        // homeTeam.addPlayer(new Player(this, GAME_WIDTH / 2.5, 2 * GAME_HEIGHT / 3,
        // "ST", Color.BLUE,
        // 85, 85, 90, 75, 50, 75));

        // // Away team initialization (static positions)
        // awayTeam.addPlayer(new Player(this, GAME_WIDTH - 100, GAME_HEIGHT / 2, "GK",
        // Color.RED,
        // 70, 30, 20, 60, 80, 75)); // Goalkeeper
        // awayTeam.addPlayer(new Player(this, GAME_WIDTH - 200, GAME_HEIGHT / 3, "DEF",
        // Color.RED,
        // 75, 65, 60, 70, 85, 80)); // Defender
        // awayTeam.addPlayer(new Player(this, GAME_WIDTH - 200, 2 * GAME_HEIGHT / 3,
        // "DEF", Color.RED,
        // 75, 65, 60, 70, 85, 80)); // Defender
        // awayTeam.addPlayer(new Player(this, GAME_WIDTH - 300, GAME_HEIGHT / 3, "MID",
        // Color.RED,
        // 80, 85, 75, 85, 70, 70)); // Midfielder
        // awayTeam.addPlayer(new Player(this, GAME_WIDTH - 300, 2 * GAME_HEIGHT / 3,
        // "ST", Color.RED,
        // 85, 85, 90, 75, 50, 75)); // Striker

    }

    public static Team getHomeTeam() {
        return homeTeam;
    }

    public static Team getAwayTeam() {
        return awayTeam;
    }

    private void drawField(Graphics g) {
        // Draw grass
        g.setColor(new Color(34, 139, 34));
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Draw field lines
        g.setColor(Color.WHITE);
        // Outer boundary
        g.drawRect(FIELD_MARGIN, FIELD_MARGIN,
                GAME_WIDTH - 2 * FIELD_MARGIN, GAME_HEIGHT - 2 * FIELD_MARGIN);

        // Center line
        g.drawLine(GAME_WIDTH / 2, FIELD_MARGIN, GAME_WIDTH / 2, GAME_HEIGHT - FIELD_MARGIN);

        // Center circle
        int centerCircleRadius = 50;
        g.drawOval(GAME_WIDTH / 2 - centerCircleRadius, GAME_HEIGHT / 2 - centerCircleRadius,
                centerCircleRadius * 2, centerCircleRadius * 2);

        // Penalty areas
        int penaltyWidth = 100;
        int penaltyHeight = 200;
        // Left penalty area
        g.drawRect(FIELD_MARGIN, GAME_HEIGHT / 2 - penaltyHeight / 2,
                penaltyWidth, penaltyHeight);
        // Right penalty area
        g.drawRect(GAME_WIDTH - FIELD_MARGIN - penaltyWidth, GAME_HEIGHT / 2 - penaltyHeight / 2,
                penaltyWidth, penaltyHeight);

        // Draw goals
        int goalHeight = 100;
        int goalY = (GAME_HEIGHT - goalHeight) / 2;

        // Left goal
        g.setColor(Color.WHITE);
        g.fillRect(FIELD_MARGIN - GOAL_POST_THICKNESS, goalY, GOAL_POST_THICKNESS, goalHeight); // Post
        g.fillRect(FIELD_MARGIN - GOAL_POST_THICKNESS, goalY, 20, GOAL_POST_THICKNESS); // Top
        g.fillRect(FIELD_MARGIN - GOAL_POST_THICKNESS, goalY + goalHeight - GOAL_POST_THICKNESS, 20,
                GOAL_POST_THICKNESS); // Bottom

        // Right goal
        g.fillRect(GAME_WIDTH - FIELD_MARGIN, goalY, GOAL_POST_THICKNESS, goalHeight); // Post
        g.fillRect(GAME_WIDTH - FIELD_MARGIN - 20, goalY, 20, GOAL_POST_THICKNESS); // Top
        g.fillRect(GAME_WIDTH - FIELD_MARGIN - 20, goalY + goalHeight - GOAL_POST_THICKNESS, 20, GOAL_POST_THICKNESS); // Bottom
    }

    public static boolean isKeyPressed(int keyCode) {
        if (Team.class.isInstance(homeTeam) && homeTeam.getSelectedPlayer() != null) {
            return ((Game) homeTeam.getSelectedPlayer().getGame()).keys[keyCode];
        }
        return false;
    }

    public static boolean isAwayKeyPressed(int keyCode) {
        if (Team.class.isInstance(awayTeam) && awayTeam.getSelectedPlayer() != null) {
            return ((Game) awayTeam.getSelectedPlayer().getGame()).keys[keyCode];
        }
        return false;
    }

    public synchronized void update() {

        gameState.update();

        if (!gameState.isGameStarted()) {
            return;
        }

        // Auto-switch
        Player possessingPlayer = ball.getPossessingPlayer();
        if (possessingPlayer != null && possessingPlayer.getTeamColor() == Color.BLUE) {
            // If the possessing player is on the home team but not selected, switch to them
            if (homeTeam.getSelectedPlayer() != possessingPlayer) {
                if (homeTeam.getSelectedPlayer() != null) {
                    homeTeam.getSelectedPlayer().setSelected(false);
                }
                possessingPlayer.setSelected(true);
                homeTeam.selectPlayer(possessingPlayer);
            }
        }

        if (gameState.isKickOff()) {
            // Reset positions for kick-off
            ball.setPosition(GAME_WIDTH / 2, GAME_HEIGHT / 2);
            ball.setPossessingPlayer(null);

            // If ball is moved, end kick-off
            // if (ball.getX() != GAME_WIDTH / 2 || ball.getY() != GAME_HEIGHT / 2) {
            gameState.setKickOffComplete();
            // }
        }

        if (controller.isControllerPresent()) {
            ControllerInput.ControllerState state = controller.pollController();

            // Handle home team movement with left analog stick
            if (homeTeam.getSelectedPlayer() != null) {
                double dx = state.leftStickX * 5; // Scale the movement
                double dy = state.leftStickY * 5;

                homeTeam.moveSelectedPlayer(dx, dy);

                // Handle controller buttons
                if (state.shootButton) {
                    Player selectedPlayer = homeTeam.getSelectedPlayer();
                    if (ball.getPossessingPlayer() == selectedPlayer) {
                        selectedPlayer.shoot(ball);
                    }
                }

                if (state.passButton) {
                    Player selectedPlayer = homeTeam.getSelectedPlayer();
                    if (ball.getPossessingPlayer() == selectedPlayer) {
                        selectedPlayer.pass(ball);
                    }
                }

                if (state.tackleButton) {
                    Player selectedPlayer = homeTeam.getSelectedPlayer();
                    if (selectedPlayer != null) {
                        selectedPlayer.tackle(ball);
                    }
                }

                if (state.switchPlayerButton) {
                    homeTeam.selectNextPlayer();
                }
            }
        }

        // Handle home team player movement
        if (homeTeam.getSelectedPlayer() != null) {
            double dx = 0, dy = 0;
            if (keys[KeyEvent.VK_LEFT])
                dx = -5;
            if (keys[KeyEvent.VK_RIGHT])
                dx = 5;
            if (keys[KeyEvent.VK_UP])
                dy = -5;
            if (keys[KeyEvent.VK_DOWN])
                dy = 5;

            homeTeam.moveSelectedPlayer(dx, dy);

            // Handle shooting (S key)
            if (keys[KeyEvent.VK_S]) {
                Player selectedPlayer = homeTeam.getSelectedPlayer();
                if (ball.getPossessingPlayer() == selectedPlayer) {
                    selectedPlayer.shoot(ball);
                    repaint();
                }
                keys[KeyEvent.VK_S] = false; // Reset key state

            }

            // Handle passing (A key)
            if (keys[KeyEvent.VK_A]) {
                Player selectedPlayer = homeTeam.getSelectedPlayer();
                if (ball.getPossessingPlayer() == selectedPlayer) {
                    selectedPlayer.pass(ball);
                    repaint();
                }
                keys[KeyEvent.VK_A] = false; // Reset key state

            }

            // Handle tackling (D key)
            if (keys[KeyEvent.VK_D]) {
                Player selectedPlayer = homeTeam.getSelectedPlayer();
                if (selectedPlayer != null) {
                    selectedPlayer.tackle(ball);
                    repaint();
                }
                keys[KeyEvent.VK_D] = false; // Reset key state
            }

            // Switch player when SPACE is pressed
            if (keys[KeyEvent.VK_SPACE]) {
                homeTeam.selectNextPlayer();
                keys[KeyEvent.VK_SPACE] = false;
            }
        }

        if (awayTeam.getSelectedPlayer() != null) {
            double dx = 0, dy = 0;
            if (keys[KeyEvent.VK_NUMPAD4])
                dx = -5;
            if (keys[KeyEvent.VK_NUMPAD6])
                dx = 5;
            if (keys[KeyEvent.VK_NUMPAD8])
                dy = -5;
            if (keys[KeyEvent.VK_NUMPAD2])
                dy = 5;

            awayTeam.moveSelectedPlayer(dx, dy);

            // Handle shooting (* key)
            if (keys[KeyEvent.VK_MULTIPLY]) {
                Player selectedPlayer = awayTeam.getSelectedPlayer();
                if (ball.getPossessingPlayer() == selectedPlayer) {
                    selectedPlayer.shoot(ball);
                }
                keys[KeyEvent.VK_MULTIPLY] = false;
            }

            // Handle passing (/ key)
            if (keys[KeyEvent.VK_DIVIDE]) {
                Player selectedPlayer = awayTeam.getSelectedPlayer();
                if (ball.getPossessingPlayer() == selectedPlayer) {
                    selectedPlayer.pass(ball);
                }
                keys[KeyEvent.VK_DIVIDE] = false;
            }

            // Handle tackling (- key)
            if (keys[KeyEvent.VK_SUBTRACT]) {
                Player selectedPlayer = awayTeam.getSelectedPlayer();
                if (selectedPlayer != null) {
                    selectedPlayer.tackle(ball);
                }
                keys[KeyEvent.VK_SUBTRACT] = false;
            }

            // Switch player when is pressed
            if (keys[KeyEvent.VK_NUMPAD5]) {
                awayTeam.selectNextPlayer();
                keys[KeyEvent.VK_NUMPAD5] = false;
            }
        }

        // // Switch player when is pressed
        // if (keys[KeyEvent.VK_PLUS]) {
        // awayTeam.selectNextPlayer();
        // keys[KeyEvent.VK_PLUS] = false;
        // }

        for (Player player : homeTeam.getPlayers()) {
            player.update(ball);
        }
        for (Player player : awayTeam.getPlayers()) {
            player.update(ball);
        }

        ball.update();
        gameState.checkForGoal(ball);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawField(g);
        homeTeam.draw(g);
        awayTeam.draw(g);
        ball.draw(g);

        // Draw scores and time
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));

        // Draw scores
        String scoreText = homeTeam.getTeamColor() == Color.BLUE
                ? "Blue " + gameState.getHomeScore() + " - " + gameState.getAwayScore() + " Red"
                : "Red " + gameState.getHomeScore() + " - " + gameState.getAwayScore() + " Blue";
        g.drawString(scoreText, GAME_WIDTH / 2 - 80, 30);

        // Draw time
        long remainingSeconds = gameState.getRemainingTime() / 1000;
        String timeText = String.format("%02d:%02d %s",
                remainingSeconds / 60,
                remainingSeconds % 60,
                gameState.isFirstHalf() ? "1H" : "2H");
        g.drawString(timeText, GAME_WIDTH / 2 - 40, 60);

        // Draw kickoff indicator if needed
        if (gameState.isKickOff()) {
            g.setColor(Color.YELLOW);
            g.drawString("KICK OFF!", GAME_WIDTH / 2 - 50, GAME_HEIGHT / 2 - 50);
        }
    }

    // KeyListener implementation
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public void keyTyped(KeyEvent e) {
    }

    // Main method to start the game
    // public static void main(String[] args) {
    // JFrame frame = new JFrame("5-a-side Football");
    // Game game = new Game();
    // frame.add(game);
    // frame.setSize(GAME_WIDTH, GAME_HEIGHT);
    // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // frame.setVisible(true);

    // // Game loop
    // while (true) {
    // game.update();
    // game.repaint();
    // try {
    // Thread.sleep(16); // Approximately 60 FPS
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // }
    // }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("5-a-side Football");
        Game game = new Game(defaultFormation);
        frame.add(game);
        frame.setSize(GAME_WIDTH, GAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(false);

        // Start with home screen
        HomeScreen homeScreen = new HomeScreen(frame);
        frame.add(homeScreen);

        // Center the window on the screen
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // // Game loop variables
        // final int TICKS_PER_SECOND = 60;
        // final long TICK_TIME = 1000000000 / TICKS_PER_SECOND; // Time in nanoseconds
        // long lastTime = System.nanoTime();
        // long accumulator = 0;

        // Game loop
        // while (true) {
        // long currentTime = System.nanoTime();
        // long deltaTime = currentTime - lastTime;
        // lastTime = currentTime;
        // accumulator += deltaTime;

        // // Update game state at a fixed time step
        // while (accumulator >= TICK_TIME) {
        // game.update();
        // accumulator -= TICK_TIME;
        // }

        // // Render as fast as possible
        // game.repaint();

        // // Add a small sleep to prevent excessive CPU usage
        // try {
        // Thread.sleep(1);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
    }
}
