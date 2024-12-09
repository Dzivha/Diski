package src;
import java.awt.Color;
import java.awt.Graphics;

public class Ball {
    private double x, y;
    private double velocityX = 0;
    private double velocityY = 0;
    private static final int SIZE = 10;
    private static final double FRICTION = 0.98;
    private static final double MAX_SPEED = 15;
    private Player possessingPlayer = null;

    public Ball(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public synchronized void update() {
        if (possessingPlayer != null) {

            // // Check if player is moving too fast to maintain control
            // double playerSpeed = Math.sqrt(
            // possessingPlayer.getLastDx() * possessingPlayer.getLastDx() +
            // possessingPlayer.getLastDy() * possessingPlayer.getLastDy());

            // // Lose ball if moving too fast relative to dribbling stat
            // double controlThreshold = possessingPlayer.getDribbling() / 10.0;
            // if (playerSpeed > controlThreshold) {
            // double loseControl = Math.random();
            // if (loseControl > possessingPlayer.getDribbling() / 100.0) {
            // possessingPlayer = null;
            // // Add some momentum to the ball based on player's movement
            // velocityX = possessingPlayer.getLastDx() * 0.8;
            // velocityY = possessingPlayer.getLastDy() * 0.8;
            // return;
            // }
            // }

            // Position ball slightly ahead of player based on their facing direction
            double offsetX = 0;
            double offsetY = 0;

            if (possessingPlayer.getLastDx() != 0 || possessingPlayer.getLastDy() != 0) {
                // Use player's last movement to determine ball position
                double magnitude = Math.sqrt(
                        possessingPlayer.getLastDx() * possessingPlayer.getLastDx() +
                                possessingPlayer.getLastDy() * possessingPlayer.getLastDy());
                double normalizedDx = possessingPlayer.getLastDx() / magnitude;
                double normalizedDy = possessingPlayer.getLastDy() / magnitude;
                offsetX = normalizedDx * 20; // 20 pixels ahead
                offsetY = normalizedDy * 20;
            } else {
                // Default offset if player isn't moving
                offsetX = 20; // Default to right side
            }

            // Ball follows the player with slight offset
            x = possessingPlayer.getX() + possessingPlayer.getSize() / 2 + offsetX;
            y = possessingPlayer.getY() + possessingPlayer.getSize() / 2 + offsetY;
            velocityX = 0;
            velocityY = 0;
        } else {

            for (Player player : Game.getHomeTeam().getPlayers()) {
                checkCollisionWithPlayer(player);
            }
            for (Player player : Game.getAwayTeam().getPlayers()) {
                checkCollisionWithPlayer(player);
            }

            // Normal ball physics when not possessed
            x += velocityX;
            y += velocityY;

            // Apply friction
            velocityX *= FRICTION;
            velocityY *= FRICTION;

            // Stop ball if moving very slowly
            if (Math.abs(velocityX) < 0.1)
                velocityX = 0;
            if (Math.abs(velocityY) < 0.1)
                velocityY = 0;

            // Keep ball in bounds
            if (x < 0 || x > Game.GAME_WIDTH - SIZE) {
                velocityX *= -0.8;
                x = x < 0 ? 0 : Game.GAME_WIDTH - SIZE;
            }
            if (y < 0 || y > Game.GAME_HEIGHT - SIZE) {
                velocityY *= -0.8;
                y = y < 0 ? 0 : Game.GAME_HEIGHT - SIZE;
            }
        }
    }

    private void checkCollisionWithPlayer(Player player) {
        double dx = (x + SIZE / 2) - (player.getX() + player.getSize() / 2);
        double dy = (y + SIZE / 2) - (player.getY() + player.getSize() / 2);
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < (SIZE / 2 + player.getSize() / 2)) {
            // Transfer some momentum to the ball
            double playerSpeedX = player.getLastDx();
            double playerSpeedY = player.getLastDy();

            velocityX = (velocityX * 0.2) + (playerSpeedX * 0.8);
            velocityY = (velocityY * 0.2) + (playerSpeedY * 0.8);
        }
    }

    public synchronized void kick(double power, double angle) {
        // Release ball from possession when kicked
        possessingPlayer = null;

        velocityX = Math.cos(angle) * power;
        velocityY = Math.sin(angle) * power;

        // Limit maximum speed
        double speed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (speed > MAX_SPEED) {
            velocityX = (velocityX / speed) * MAX_SPEED;
            velocityY = (velocityY / speed) * MAX_SPEED;
        }

        // Move ball slightly away from player to prevent immediate recapture
        x += velocityX;
        y += velocityY;
    }

    public void setPossessingPlayer(Player player) {
        this.possessingPlayer = player;
    }

    public Player getPossessingPlayer() {
        return possessingPlayer;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillOval((int) x, (int) y, SIZE, SIZE);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getSize() {
        return SIZE;
    }
}
