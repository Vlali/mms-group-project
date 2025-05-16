package src.tetris;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Board extends JPanel implements ActionListener {

    private final int boardWidth = 10;
    private final int boardHeight = 19;   //its working with 19, otherwise the element is going out of the grid
    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int currentX = 0;
    private int currentY = 0;
    private Tetromino currentPiece;
    private Tetromino.Shape[][] board;
    private int score = 0;

    public Board() {
        setFocusable(true);
        setPreferredSize(new Dimension(300, 600));
        currentPiece = new Tetromino();
        board = new Tetromino.Shape[boardHeight][boardWidth];
        clearBoard();

        addKeyListener(new TAdapter());

        timer = new Timer(400, this);
        timer.start();

        start();
    }

    public void start() {
        isStarted = true;
        isFallingFinished = false;
        score = 0;
        clearBoard();
        spawnNewPiece();
        timer.start();
    }

    private void clearBoard() {
        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                board[i][j] = Tetromino.Shape.NoShape;
            }
        }
    }

    private void spawnNewPiece() {
        currentPiece.setShape(randomShape());
        currentX = boardWidth / 2 - 1;
        currentY = -2;  // 👈 omogucava spawn izvan vidljivog dela

        if (!isValidMove(currentPiece, currentX, currentY)) {
            currentPiece.setShape(Tetromino.Shape.NoShape);
            timer.stop();
            isStarted = false;
            JOptionPane.showMessageDialog(this, "Game Over. Score: " + score);
        }
    }

    private Tetromino.Shape randomShape() {
        Tetromino.Shape[] shapes = Tetromino.Shape.values();
        return shapes[(int)(Math.random() * (shapes.length - 1)) + 1];
    }

    private boolean isValidMove(Tetromino shape, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + shape.getX(i);
            int y = newY + shape.getY(i);

            if (x < 0 || x >= boardWidth || y >= boardHeight) {
                return false;
            }

            if (y >= 0 && board[y][x] != Tetromino.Shape.NoShape) {
                return false;
            }
        }
        return true;
    }

    private void freezePiece() {
        for (int i = 0; i < 4; i++) {
            int x = currentX + currentPiece.getX(i);
            int y = currentY + currentPiece.getY(i);
            if (y >= 0) {
                board[y][x] = currentPiece.getShape();      //in order of this not going below the grid
            }
        }
    }

    private void clearFullLines() {
        int linesCleared = 0;

        for (int row = boardHeight - 1; row >= 0; row--) {
            boolean fullLine = true;

            for (int col = 0; col < boardWidth; col++) {
                if (board[row][col] == Tetromino.Shape.NoShape) {
                    fullLine = false;
                    break;
                }
            }

            if (fullLine) {
                linesCleared++;

                for (int r = row; r > 0; r--) {
                    for (int c = 0; c < boardWidth; c++) {
                        board[r][c] = board[r - 1][c];
                    }
                }

                for (int c = 0; c < boardWidth; c++) {
                    board[0][c] = Tetromino.Shape.NoShape;
                }

                row++;
            }
        }

        if (linesCleared > 0) {
            score += linesCleared * 100;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            spawnNewPiece();
        } else {
            oneLineDown();
        }
    }

    private void oneLineDown() {
        if (isValidMove(currentPiece, currentX, currentY + 1)) {
            currentY++;
        } else {
            freezePiece();
            clearFullLines();
            isFallingFinished = true;
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }

    private void drawBoard(Graphics g) {
        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                Tetromino.Shape shape = board[i][j];
                if (shape != Tetromino.Shape.NoShape) {
                    drawSquare(g, j * 30, i * 30, shape);
                }
            }
        }

        if (currentPiece.getShape() != Tetromino.Shape.NoShape) {
            for (int i = 0; i < 4; i++) {
                int x = currentX + currentPiece.getX(i);
                int y = currentY + currentPiece.getY(i);
                if (y >= 0) {
                    drawSquare(g, x * 30, y * 30, currentPiece.getShape());
                }
            }
        }

        setBackground(Color.BLACK);
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);

        g.setColor(Color.DARK_GRAY);

        for (int i = 0; i <= boardHeight; i++) {

            g.drawLine(0, i * 30, boardWidth * 30, i * 30);

        }

        for (int j = 0; j <= boardWidth; j++) {

            g.drawLine(j * 30, 0, j * 30, boardHeight * 30);

        }

        
    }

    private void drawSquare(Graphics g, int x, int y, Tetromino.Shape shape) {
        g.setColor(Tetromino.getColor(shape));
        g.fillRect(x, y, 30, 30);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, 30, 30);
    }

    class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!isStarted || currentPiece.getShape() == Tetromino.Shape.NoShape) return;

            int keycode = e.getKeyCode();

            switch (keycode) {
                case KeyEvent.VK_LEFT:
                    if (isValidMove(currentPiece, currentX - 1, currentY)) currentX--;
                    break;
                case KeyEvent.VK_RIGHT:
                    if (isValidMove(currentPiece, currentX + 1, currentY)) currentX++;
                    break;
                case KeyEvent.VK_DOWN:
                    oneLineDown();
                    break;
                case KeyEvent.VK_UP:
                    Tetromino rotated = currentPiece.rotateRight();
                    if (isValidMove(rotated, currentX, currentY)) currentPiece = rotated;
                    break;
                case KeyEvent.VK_SPACE:
                    while (isValidMove(currentPiece, currentX, currentY + 1)) currentY++;
                    oneLineDown();
                    break;
            }

            repaint();
        }
    }
}

