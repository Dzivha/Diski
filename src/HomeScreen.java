package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class HomeScreen extends JPanel {
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;
    private JFrame parentFrame;

    public HomeScreen(JFrame frame) {
        this.parentFrame = frame;
        setLayout(null); // Using absolute positioning for this example
        setBackground(new Color(34, 139, 34)); // Same green as the football field

        // Create title
        JLabel titleLabel = new JLabel("5-a-side Football");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(SCREEN_WIDTH / 2 - 200, 100, 400, 60);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel);

        // Create buttons
        JButton playButton = createStyledButton("Play", SCREEN_WIDTH / 2 - 100, 300);
        JButton quitButton = createStyledButton("Quit", SCREEN_WIDTH / 2 - 100, 400);

        // Add action listeners
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        add(playButton);
        add(quitButton);
    }

    private JButton createStyledButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 200, 50);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(34, 139, 34));
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        // Add hover effect
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

    private void startGame() {
        parentFrame.getContentPane().removeAll();
        parentFrame.add(new TeamManagement(parentFrame));
        parentFrame.revalidate();
        parentFrame.repaint();
    }

    // private void startGame() {
    // parentFrame.getContentPane().removeAll();
    // Game game = new Game();
    // parentFrame.add(game);
    // parentFrame.revalidate();
    // parentFrame.repaint();

    // // Request focus for the game panel
    // game.requestFocusInWindow();

    // // Start the game loop in a separate thread
    // new Thread(() -> {
    // // Game loop variables
    // final int TICKS_PER_SECOND = 60;
    // final long TICK_TIME = 1000000000 / TICKS_PER_SECOND;
    // long lastTime = System.nanoTime();
    // long accumulator = 0;

    // while (true) {
    // long currentTime = System.nanoTime();
    // long deltaTime = currentTime - lastTime;
    // lastTime = currentTime;
    // accumulator += deltaTime;

    // while (accumulator >= TICK_TIME) {
    // game.update();
    // accumulator -= TICK_TIME;
    // }

    // game.repaint();

    // try {
    // Thread.sleep(1);
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // }
    // }).start();
    // }
}