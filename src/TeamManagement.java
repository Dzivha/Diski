package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class TeamManagement extends JPanel {
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private JFrame parentFrame;
    private Map<String, int[][]> formations;

    public TeamManagement(JFrame frame) {
        this.parentFrame = frame;
        setLayout(null);
        setBackground(new Color(34, 139, 34));
        initializeFormations();
        createUI();
    }

    private void initializeFormations() {
        formations = new HashMap<>();
        // Format: {{x1,y1}, {x2,y2}, ...} for 5 players (GK, and 4 outfield)
        // x and y are percentages of field width/height
        formations.put("2-1-1", new int[][] {
                { 25, 50 }, // GK
                { 35, 30 }, // Defender
                { 35, 70 }, // Defender
                { 50, 50 }, // Midfielder
                { 65, 50 } // Striker
        });

        formations.put("1-2-1", new int[][] {
                { 25, 50 }, // GK
                { 35, 50 }, // Defender
                { 50, 30 }, // Midfielder
                { 50, 70 }, // Midfielder
                { 65, 50 } // Striker
        });

        formations.put("2-2", new int[][] {
                { 25, 50 }, // GK
                { 40, 30 }, // Defender
                { 40, 70 }, // Defender
                { 60, 30 }, // Forward
                { 60, 70 } // Forward
        });
    }

    private void createUI() {
        // Title
        JLabel titleLabel = new JLabel("Team Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(SCREEN_WIDTH / 2 - 200, 50, 400, 60);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel);

        // Formation selection label
        JLabel formationLabel = new JLabel("Select Formation:");
        formationLabel.setFont(new Font("Arial", Font.BOLD, 24));
        formationLabel.setForeground(Color.WHITE);
        formationLabel.setBounds(SCREEN_WIDTH / 2 - 150, 150, 300, 30);
        formationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(formationLabel);

        // Create formation buttons
        int buttonY = 200;
        for (String formation : formations.keySet()) {
            JButton formationButton = createStyledButton(formation, SCREEN_WIDTH / 2 - 100, buttonY);
            formationButton.addActionListener(e -> startGame(formation));
            add(formationButton);
            buttonY += 70;
        }

        // Back button
        JButton backButton = createStyledButton("Back", SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT - 100);
        backButton.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.add(new HomeScreen(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        add(backButton);
    }

    private JButton createStyledButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 200, 50);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(34, 139, 34));
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 220, 220));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    private void startGame(String formation) {
        parentFrame.getContentPane().removeAll();
        Game game = new Game(formations.get(formation));
        parentFrame.add(game);
        parentFrame.revalidate();
        parentFrame.repaint();

        game.requestFocusInWindow();

        new Thread(() -> {
            final int TICKS_PER_SECOND = 60;
            final long TICK_TIME = 1000000000 / TICKS_PER_SECOND;
            long lastTime = System.nanoTime();
            long accumulator = 0;

            while (true) {
                long currentTime = System.nanoTime();
                long deltaTime = currentTime - lastTime;
                lastTime = currentTime;
                accumulator += deltaTime;

                while (accumulator >= TICK_TIME) {
                    game.update();
                    accumulator -= TICK_TIME;
                }

                game.repaint();

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
