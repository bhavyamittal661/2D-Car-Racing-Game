import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class AppPanel extends JPanel {

    static BufferedImage playerCarImage;
    static BufferedImage bgImage;
    static BufferedImage obstacleCarImage;

    Timer timer;
    int playerX = 200;
    int playerY = 350;
    int obstacleX = 200;
    int obstacleY = -150;

    int score = 0;
    int obstacleSpeed = 5;

    int playerSpeed = 0; // Current speed of the player's car
    final int MAX_SPEED = 20; // Maximum speed
    final int ACCELERATION = 1; // Acceleration rate
    final int DECELERATION = 1; // Deceleration rate

    boolean gameOver = false;
    boolean accelerating = false; // Track if the player is accelerating

    AppPanel() {
        setSize(500, 500);
        showBgImage();
        showPlayerCarImage();
        showObstacleCarImage();
        startGameLoop();
        setupKeyboardControls();
        setFocusable(true);
    }

    static void showBgImage() {
        try {
            bgImage = ImageIO.read(AppPanel.class.getResource("road1.png"));
        } catch (IOException e) {
            System.out.println("No background image found. Ensure 'road1.png' is in the correct location.");
            e.printStackTrace();
        }
    }

    static void showPlayerCarImage() {
        try {
            playerCarImage = ImageIO.read(AppPanel.class.getResource("car.png")); 
        } catch (IOException e) {
            System.out.println("No player car image found. Ensure 'car.png' is in the correct location.");
            e.printStackTrace();
        }
    }

    static void showObstacleCarImage() {
        try {
            obstacleCarImage = ImageIO.read(AppPanel.class.getResource("car1.png")); 
        } catch (IOException e) {
            System.out.println("No obstacle car image found. Ensure 'car1.png' is in the correct location.");
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
        }

        if (playerCarImage != null) {
            g.drawImage(playerCarImage, playerX, playerY, 90, 100, null);
        }

        if (obstacleCarImage != null) {
            g.drawImage(obstacleCarImage, obstacleX, obstacleY, 100, 105, null);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Obstacle Speed: " + obstacleSpeed, 10, 40);
        g.drawString("Player Speed: " + playerSpeed, 10, 60); // Display player speed

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Game Over! Press SPACEBAR to Restart", 100, 250);
        }
    }

    void startGameLoop() {
        timer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    obstacleY += obstacleSpeed;

                    if (obstacleY > getHeight()) {
                        obstacleY = -100;
                        obstacleX = (int) (Math.random() * (getWidth() - 100));
                        score++;

                        if (score % 5 == 0) {
                            obstacleSpeed++;
                        }
                    }

                    // Adjust player speed for acceleration or deceleration
                    if (accelerating) {
                        playerSpeed = Math.min(playerSpeed + ACCELERATION, MAX_SPEED);
                    } else if (playerSpeed > 0) {
                        playerSpeed = Math.max(playerSpeed - DECELERATION, 0);
                    }

                    playerY -= playerSpeed; // Move player upwards based on speed

                    if (checkCollision()) {
                        gameOver = true;
                        timer.stop();
                    }

                    repaint();
                }
            }
        });
        timer.start();
    }

    void setupKeyboardControls() {
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameOver) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_RIGHT:
                            playerX += 10;
                            break;
                        case KeyEvent.VK_LEFT:
                            playerX -= 10;
                            break;
                        case KeyEvent.VK_UP:
                            accelerating = true; // Start accelerating when UP key is pressed
                            break;
                        case KeyEvent.VK_DOWN:
                            playerY += 10;
                            break;
                    }
                }

                if (gameOver && e.getKeyCode() == KeyEvent.VK_SPACE) {
                    restartGame();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    accelerating = false; // Stop accelerating when UP key is released
                }
            }
        });
    }

    boolean checkCollision() {
        int playerWidth = 90;
        int playerHeight = 100;
        int obstacleWidth = 100;
        int obstacleHeight = 105;

        return playerX < obstacleX + obstacleWidth &&
               playerX + playerWidth > obstacleX &&
               playerY < obstacleY + obstacleHeight &&
               playerY + playerHeight > obstacleY;
    }

    void restartGame() {
        gameOver = false;
        playerX = 200;
        playerY = 350;
        obstacleX = 200;
        obstacleY = -150;
        score = 0;
        obstacleSpeed = 5;
        playerSpeed = 0;
        accelerating = false;
        timer.start();
        repaint();
    }
}