package gomoku;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class DrawCanvas extends JPanel{
    
    public static final int ROWS = 15;
    public static final int COLS = 15;
    public static final int CELL_SIZE = 40;
    public static final int CANVAS_WIDTH = CELL_SIZE * (COLS + 1);
    public static final int CANVAS_HEIGHT = CELL_SIZE * (ROWS + 1);
    public static final int GRID_WIDTH = 2;
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;
    public static final int CELL_PADDING = CELL_SIZE / 8;
    public static final int CIRCLE_SIZE = CELL_SIZE - CELL_PADDING * 2;
    private int hoverX;
    private int hoverY;
    private Seed[][] board;
    
    public DrawCanvas() {
        super();
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
    }
    
    public void update(Seed[][] board) {
        this.board = board;
    }
    
    public void updateHoverInfo(int hoverX, int hoverY) {
        this.hoverX = hoverX;
        this.hoverY = hoverY;
    }
    
    public void paintComponent(Graphics g) {  // invoke via repaint()
        super.paintComponent(g);    // fill background
        setBackground(new Color(166, 93, 82)); // set its background color

        // Draw the grid-lines
        g.setColor(Color.BLACK);
        for (int row = 1; row <= ROWS; ++row) {
            g.fillRect(CELL_SIZE, CELL_SIZE * row - GRID_WIDTH_HALF,
                    (ROWS - 1) * CELL_SIZE, GRID_WIDTH);
        }
        for (int col = 1; col <= COLS; ++col) {
           g.fillRect(CELL_SIZE * col - GRID_WIDTH_HALF, CELL_SIZE,
                 GRID_WIDTH, (COLS - 1) * CELL_SIZE);
        }
        
        Graphics2D g2d = (Graphics2D)g;
       
        // Setup the 9 star points
        g2d.setColor(Color.BLACK);
        g2d.fillOval(4 * CELL_SIZE - CELL_PADDING, 4 * CELL_SIZE - CELL_PADDING,
                CIRCLE_SIZE / 3, CIRCLE_SIZE / 3);
        g2d.fillOval(4 * CELL_SIZE - CELL_PADDING, 12 * CELL_SIZE - CELL_PADDING,
                CIRCLE_SIZE / 3, CIRCLE_SIZE / 3);
        g2d.fillOval(8 * CELL_SIZE - CELL_PADDING, 8 * CELL_SIZE - CELL_PADDING,
                CIRCLE_SIZE / 3, CIRCLE_SIZE / 3);
        g2d.fillOval(12 * CELL_SIZE - CELL_PADDING, 4 * CELL_SIZE - CELL_PADDING,
                CIRCLE_SIZE / 3, CIRCLE_SIZE / 3);
        g2d.fillOval(12 * CELL_SIZE - CELL_PADDING, 12 * CELL_SIZE - CELL_PADDING,
                CIRCLE_SIZE / 3, CIRCLE_SIZE / 3);
        
        // Display mouse hover preview circle over board
        if (hoverX > CELL_SIZE / 2 && hoverX < CELL_SIZE * 16
                && hoverY > CELL_SIZE / 2 && hoverY < CELL_SIZE * 16)
        g2d.drawOval(hoverX - CIRCLE_SIZE / 2, hoverY - CIRCLE_SIZE / 2,
                CIRCLE_SIZE, CIRCLE_SIZE);

        // Display all of the pieces
        for (int row = 0; row < ROWS; ++row) {
           for (int col = 0; col < COLS; ++col) {
              int x1 = (int) ((col + 0.5) * CELL_SIZE + CELL_PADDING);
              int y1 = (int) ((row + 0.5) * CELL_SIZE + CELL_PADDING);
              if (board[row][col] == Seed.BLACK) {
                 g2d.setColor(Color.BLACK);
                 g2d.fillOval(x1, y1, CIRCLE_SIZE, CIRCLE_SIZE);
              } else if (board[row][col] == Seed.WHITE) {
                 g2d.setColor(Color.WHITE);
                 g2d.fillOval(x1, y1, CIRCLE_SIZE, CIRCLE_SIZE);
              }
           }
        }
        
     }
}

