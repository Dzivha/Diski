package src;

import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.event.KeyEvent;

public class Player {
    private double x, y;
    private double lastDx = 0;
    private double lastDy = 0;
    private int width = 32; // Adjust based on your sprite size
    private int height = 32;
    private BufferedImage sprite;
    private boolean isSelected;
    private String position;
    private Color teamColor;
    private static final int PLAYER_SIZE = 20;
    private static final int POSSESSION_DISTANCE = 25;
    private static final double MAX_SHOT_POWER = 25;
    private static final double MAX_PASS_POWER = 15;
    private double facingAngle = 0; // in radians
    private Game game;

    // Stats
    private int pace;
    private int dribbling;
    private int shooting;
    private int passing;
    private int defending;
    private int physicality;

    public Player(Game game, double x, double y, String position, Color teamColor,
            int pace, int dribbling, int shooting, int passing, int defending, int physicality) {
        this.game = game;
        this.x = x;
        this.y = y;
        this.position = position;
        this.teamColor = teamColor;
        this.pace = pace;
        this.dribbling = dribbling;
        this.shooting = shooting;
        this.passing = passing;
        this.defending = defending;
        this.physicality = physicality;
        this.isSelected = false;
    }

    private boolean canReachBall(Ball ball) {
        // First check if ball is already possessed by another player
        if (ball.getPossessingPlayer() != null && ball.getPossessingPlayer() != this) {
            return false;
        }

        double dx = (ball.getX() + ball.getSize() / 2) - (x + PLAYER_SIZE / 2);
        double dy = (ball.getY() + ball.getSize() / 2) - (y + PLAYER_SIZE / 2);
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Base reach distance on player's physicality stat
        double reachDistance = POSSESSION_DISTANCE * (0.8 + (physicality / 100.0) * 0.4);
        return distance < reachDistance;
    }

    public void update(Ball ball) {
        if (canReachBall(ball)) {
            // Add chance to gain possession based on dribbling stat
            if (ball.getPossessingPlayer() == null) {
                double possessionChance = dribbling / 100.0;
                if (Math.random() < possessionChance) {
                    ball.setPossessingPlayer(this);

                    Team myTeam = this.teamColor == Color.BLUE ? Game.getHomeTeam() : Game.getAwayTeam();

                    // Only auto-switch for the human-controlled team (home team)
                    if (myTeam == Game.getHomeTeam()) {
                        // Deselect current player if there is one
                        if (myTeam.getSelectedPlayer() != null) {
                            myTeam.getSelectedPlayer().setSelected(false);
                        }
                        // Select this player
                        this.setSelected(true);
                        myTeam.selectPlayer(this);
                    }
                }
            }
        }
    }

    public void move(double dx, double dy) {
        if (isSelected) {
            x += dx * (pace / 50.0);
            y += dy * (pace / 50.0);

            // Track last movement direction if actually moving
            if (dx != 0 || dy != 0) {
                facingAngle = Math.atan2(dy, dx);
                lastDx = dx;
                lastDy = dy;
            }

            // Keep player in bounds
            x = Math.max(0, Math.min(x, Game.GAME_WIDTH - PLAYER_SIZE));
            y = Math.max(0, Math.min(y, Game.GAME_HEIGHT - PLAYER_SIZE));
        }
    }

    // public void shoot(Ball ball) {
    // if (canReachBall(ball)) {
    // // Calculate base shooting angle towards goal
    // double targetX = Game.GAME_WIDTH - 50; // Right goal
    // double targetY = Game.GAME_HEIGHT / 2;

    // // If player is on right side of field, shoot towards left goal
    // if (x > Game.GAME_WIDTH / 2) {
    // targetX = 50; // Left goal
    // }

    // // Calculate angle to goal
    // double angleToGoal = Math.atan2(targetY - y, targetX - x);

    // // Add inaccuracy based on shooting stat and facing direction
    // double accuracy = shooting / 100.0; // 0 to 1
    // double facingBonus = Math.cos(facingAngle - angleToGoal); // -1 to 1

    // // Calculate final angle with some randomness
    // double randomness = (1 - accuracy) * (Math.random() - 0.5);
    // double finalAngle = angleToGoal + randomness + (1 - facingBonus) * 0.5;

    // // Calculate power based on shooting stat and distance to goal
    // double distanceToGoal = Math.sqrt(Math.pow(targetX - x, 2) + Math.pow(targetY
    // - y, 2));
    // double power = MAX_SHOT_POWER * (shooting / 100.0) * (distanceToGoal /
    // Game.GAME_WIDTH);

    // // Release ball and kick
    // ball.setPossessingPlayer(null);
    // ball.kick(power, finalAngle);
    // }
    // }

    public void shoot(Ball ball) {
        if (ball.getPossessingPlayer() == this) { // Changed from canReachBall(ball)
            // Calculate base shooting angle towards goal
            double targetX = Game.GAME_WIDTH - 50; // Right goal
            double targetY = Game.GAME_HEIGHT / 2;

            // If player is on right side of field, shoot towards left goal
            // if (x > Game.GAME_WIDTH / 2) {
            // targetX = 50; // Left goal
            // }

            // Calculate angle to goal
            double angleToGoal = Math.atan2(targetY - y, targetX - x);

            // Add inaccuracy based on shooting stat and facing direction
            double accuracy = shooting / 100.0; // 0 to 1
            double facingBonus = Math.cos(facingAngle - angleToGoal); // -1 to 1

            // Calculate final angle with some randomness
            double randomness = (1 - accuracy) * (Math.random() - 0.5);
            double finalAngle = angleToGoal + randomness + (1 - facingBonus) * 0.5;

            // Calculate power based on shooting stat and distance to goal
            double distanceToGoal = Math.sqrt(Math.pow(targetX - x, 2) + Math.pow(targetY - y, 2));
            double power = MAX_SHOT_POWER * (shooting / 100.0) * (distanceToGoal / Game.GAME_WIDTH);

            // Release ball and kick
            ball.setPossessingPlayer(null);
            ball.kick(power, finalAngle);

        }

    }

    // public void pass(Ball ball) {
    // if (ball.getPossessingPlayer() == this) {
    // Player target = findTargetPlayer();
    // double passAngle;
    // double power;
    // if (target != null) {
    // // Calculate angle to target player
    // double dx = target.getX() - x;
    // double dy = target.getY() - y;
    // double distance = Math.sqrt(dx * dx + dy * dy);
    // double angleToTarget = Math.atan2(dy, dx);

    // // Normalize facing angle and angleToTarget to ensure consistent comparison
    // double normalizedFacingAngle = normalizeAngle(facingAngle);
    // angleToTarget = normalizeAngle(angleToTarget);

    // // Calculate facing bonus using normalized angles
    // double angleDiff = Math.abs(normalizeAngle(angleToTarget -
    // normalizedFacingAngle));
    // double facingBonus = Math.cos(angleDiff); // -1 to 1

    // // Add inaccuracy based on passing stat and facing direction
    // double accuracy = passing / 100.0; // 0 to 1

    // // Adjust randomness based on accuracy and facing direction
    // double maxRandomness = (1 - accuracy) * Math.PI / 4; // Maximum 45 degrees
    // deviation
    // double randomness = maxRandomness * (Math.random() * 2 - 1);

    // // Calculate final pass angle
    // // Reduce the impact of facing direction on pass accuracy
    // passAngle = angleToTarget + randomness * (1 - Math.abs(facingBonus));

    // // Adjust power based on distance, passing stat, and facing direction
    // double facingPowerMultiplier = 0.5 + (facingBonus + 1) / 2; // 0.5 to 1.0
    // power = Math.min(MAX_PASS_POWER * (distance / 300) *
    // (passing / 100.0) * facingPowerMultiplier,
    // MAX_PASS_POWER);
    // } else {
    // // No target found, pass in facing direction
    // passAngle = normalizeAngle(facingAngle);
    // power = MAX_PASS_POWER * 0.6;
    // }

    // // Release ball and kick
    // ball.setPossessingPlayer(null);
    // ball.kick(power, passAngle);
    // }
    // }

    public void pass(Ball ball) {
        if (ball.getPossessingPlayer() == this) {
            // Find closest teammate in the general direction we're facing
            Player target = findTargetPlayer();
            double passAngle;
            double power;

            // Get mouse/input position or desired pass direction
            double desiredPassAngle = getDesiredPassDirection(); // You'll need to implement this based on your input
                                                                 // system

            if (target != null) {
                // Calculate angle to target player
                double dx = target.getX() - x;
                double dy = target.getY() - y;
                double distance = Math.sqrt(dx * dx + dy * dy);
                double angleToTarget = Math.atan2(dy, dx);

                // Check if target is roughly in the desired direction
                double angleDifference = Math.abs(normalizeAngle(angleToTarget - desiredPassAngle));

                if (angleDifference < Math.PI / 4) { // If target is within 45 degrees of desired direction
                    // Use target-based passing
                    passAngle = angleToTarget;

                    // Normalize facing angle and angleToTarget
                    double normalizedFacingAngle = normalizeAngle(facingAngle);
                    angleToTarget = normalizeAngle(angleToTarget);

                    // Calculate facing bonus using normalized angles
                    double facingBonus = Math.cos(Math.abs(normalizeAngle(angleToTarget - normalizedFacingAngle)));

                    // Add inaccuracy based on passing stat and facing direction
                    double accuracy = passing / 100.0;
                    double maxRandomness = (1 - accuracy) * Math.PI / 4;
                    double randomness = maxRandomness * (Math.random() * 2 - 1);

                    // Calculate final pass angle with reduced randomness when targeting player
                    passAngle = angleToTarget + randomness * (1 - Math.abs(facingBonus));

                    // Adjust power based on distance, passing stat, and facing direction
                    double facingPowerMultiplier = 0.5 + (facingBonus + 1) / 2;
                    power = Math.min(MAX_PASS_POWER * (distance / 300) *
                            (passing / 100.0) * facingPowerMultiplier,
                            MAX_PASS_POWER);
                } else {
                    // Target exists but not in desired direction, use directional passing
                    passAngle = desiredPassAngle;
                    power = calculateDirectionalPassPower(desiredPassAngle);
                }
            } else {
                // No target found, pass in desired direction
                passAngle = desiredPassAngle;
                power = calculateDirectionalPassPower(desiredPassAngle);
            }

            // Add some randomness to untargeted passes
            if (target == null || Math.abs(normalizeAngle(passAngle - desiredPassAngle)) >= Math.PI / 4) {
                double accuracy = passing / 100.0;
                double maxRandomness = (1 - accuracy) * Math.PI / 3; // More randomness for untargeted passes
                passAngle += maxRandomness * (Math.random() * 2 - 1);
            }

            // Release ball and kick
            ball.setPossessingPlayer(null);
            ball.kick(power, passAngle);
        }
    }

    private double calculateDirectionalPassPower(double passAngle) {
        // Calculate power for directional passing
        double normalizedFacingAngle = normalizeAngle(facingAngle);
        double angleDiff = Math.abs(normalizeAngle(passAngle - normalizedFacingAngle));
        double facingBonus = Math.cos(angleDiff);
        double facingPowerMultiplier = 0.5 + (facingBonus + 1) / 2;

        // Base power for directional passes
        return MAX_PASS_POWER * 0.7 * facingPowerMultiplier * (passing / 100.0);
    }

    private Player findTargetPlayer() {
        Team myTeam = this.teamColor == Color.BLUE ? Game.getHomeTeam() : Game.getAwayTeam();
        Player bestTarget = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        // Normalize facing angle once for all comparisons
        double normalizedFacingAngle = normalizeAngle(facingAngle);
        double desiredPassAngle = getDesiredPassDirection();

        for (Player player : myTeam.getPlayers()) {
            if (player != this) {
                double dx = player.getX() - x;
                double dy = player.getY() - y;
                double distance = Math.sqrt(dx * dx + dy * dy);

                // Calculate and normalize angle to player
                double angleToPlayer = normalizeAngle(Math.atan2(dy, dx));

                // Calculate angle difference using normalized angles
                double angleDifference = Math.abs(normalizeAngle(angleToPlayer - normalizedFacingAngle));

                // Adjust scoring system to better handle all directions
                double angleScore = (Math.PI - angleDifference) / Math.PI; // 0 to 1
                double distanceScore = Math.max(0, 1 - (distance / 1000.0)); // 0 to 1

                // Weighted scoring system
                double score = (angleScore * 0.7) + (distanceScore * 0.3);

                // Increased angle threshold to 135 degrees for more natural passing
                if (score > bestScore && angleDifference < Math.PI * 0.75) {
                    bestScore = score;
                    bestTarget = player;
                }
            }
        }
        return bestTarget;
    }

    private double getDesiredPassDirection() {
        // Get the current movement direction from keyboard input
        double dx = 0, dy = 0;

        // Using the same key checks as in the Game class
        if (Game.getHomeTeam().getSelectedPlayer() == this) {
            if (Game.isKeyPressed(KeyEvent.VK_LEFT))
                dx -= 1;
            if (Game.isKeyPressed(KeyEvent.VK_RIGHT))
                dx += 1;
            if (Game.isKeyPressed(KeyEvent.VK_UP))
                dy -= 1;
            if (Game.isKeyPressed(KeyEvent.VK_DOWN))
                dy += 1;
        }

        // If no direction keys are pressed, use the player's facing direction
        if (dx == 0 && dy == 0) {
            return facingAngle;
        }

        // Calculate the desired angle based on movement keys
        return Math.atan2(dy, dx);
    }

    private double normalizeAngle(double angle) {
        angle = angle % (2 * Math.PI);
        if (angle > Math.PI) {
            angle -= 2 * Math.PI;
        } else if (angle < -Math.PI) {
            angle += 2 * Math.PI;
        }
        return angle;
    }

    public void tackle(Ball ball) {
        Player ballCarrier = ball.getPossessingPlayer();
        if (ballCarrier == null || ballCarrier == this) {
            return; // Can't tackle if no one has the ball or if we have it
        }

        // Calculate distance to ball carrier
        double dx = ballCarrier.getX() - x;
        double dy = ballCarrier.getY() - y;
        double distanceToBall = Math.sqrt(dx * dx + dy * dy);

        // Tackle range based on player stats (higher tackling stat = slightly longer
        // reach)
        double tackleRange = 30 + (defending / 100.0) * 10; // Range from 30 to 40 pixels

        if (distanceToBall <= tackleRange) {
            // Calculate angle to ball carrier
            double angleToTarget = Math.atan2(dy, dx);
            double angleDiff = Math.abs(normalizeAngle(angleToTarget - facingAngle));

            // Must be somewhat facing the ball carrier to tackle (180 degree arc)
            if (angleDiff <= Math.PI) {
                // Calculate tackle success chance based on:
                // - Tackling stat
                // - Distance (closer is better)
                // - Facing direction (better when directly facing)
                // - Ball carrier's ball control stat
                double tackleAccuracy = defending / 100.0;
                double distanceFactor = 1 - (distanceToBall / tackleRange); // 0 to 1, closer is better
                double facingBonus = (Math.PI - angleDiff) / Math.PI; // 0 to 1, better when facing
                double ballControlFactor = ballCarrier.getDribbling() / 100.0;

                // Calculate final success chance
                double successChance = (tackleAccuracy * 0.4 + // 40% based on tackle stat
                        distanceFactor * 0.3 + // 30% based on distance
                        facingBonus * 0.3) // 30% based on facing direction
                        * (1 - ballControlFactor * 0.5); // Reduce by up to 50% based on carrier's ball control

                // Add some randomness to make it interesting
                if (Math.random() < successChance) {
                    // Successful tackle
                    ball.setPossessingPlayer(null);

                    // Add some momentum to the ball based on tackle direction
                    double tackleForce = 5 + (defending / 100.0) * 5; // Force between 5 and 10
                    ball.kick(tackleForce, angleToTarget);

                    // Small chance to directly gain possession if tackle is very accurate
                    if (Math.random() < successChance * 0.3) { // 30% chance of clean tackle if success chance was high
                        ball.setPossessingPlayer(this);
                    }
                } else {
                    // Failed tackle - add some random ball movement
                    double deflectionAngle = angleToTarget + (Math.random() - 0.5) * Math.PI;
                    double deflectionForce = 3 + Math.random() * 3; // Small random force
                    ball.kick(deflectionForce, deflectionAngle);
                }
            }
        }
    }

    public void draw(Graphics g) {
        g.setColor(teamColor);
        g.fillOval((int) x, (int) y, PLAYER_SIZE, PLAYER_SIZE);
        if (isSelected) {
            g.setColor(Color.YELLOW);
            g.drawOval((int) x - 2, (int) y - 2, PLAYER_SIZE + 4, PLAYER_SIZE + 4);
        }
        // Draw player position text
        g.setColor(Color.WHITE);
        g.drawString(position, (int) x + 5, (int) y + PLAYER_SIZE / 2);
    }

    public Game getGame() {
        return game;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    // Getters and setters

    public String getPosition() {
        return position;
    }

    public int getPace() {
        return pace;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getSize() {
        return PLAYER_SIZE;
    }

    public int getDribbling() {
        return dribbling;
    }

    public int getShooting() {
        return shooting;
    }

    public int getPassing() {
        return passing;
    }

    public int getDefending() {
        return defending;
    }

    public int getPhysicality() {
        return physicality;
    }

    public double getLastDx() {
        return lastDx;
    }

    public double getLastDy() {
        return lastDy;
    }

    public Color getTeamColor() {
        return teamColor;
    }
}
