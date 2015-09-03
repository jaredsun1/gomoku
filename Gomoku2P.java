package gomoku;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class Gomoku2P extends JFrame{
    protected int ROWS = DrawCanvas.ROWS;
    protected int COLS = DrawCanvas.COLS;
    protected int CELL_SIZE = DrawCanvas.CELL_SIZE;
    
    protected int hoverX; // X position of cursor hover
    protected int hoverY; // Y position of cursor hover
    
    protected GameState currentState;
    protected Seed currentPlayer;
    protected Seed[][] board;
    
    protected DrawCanvas canvas;
    protected JLabel statusBar;
    protected JLabel coordinates;
    protected JPanel infoPanel;
    
    public Gomoku2P() {
        canvas = new DrawCanvas();  // Construct a drawing canvas (a JPanel)
        setResizable(false);
   
        // The canvas (JPanel) fires a MouseEvent upon mouse-click
        mouseClickEvent();
        
        // Keep track of mouse position on the board
        trackMouse();
   
        // Setup the game status
        setupContainer();
        
   
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();  // pack all the components in this JFrame
        setTitle("Gomoku");
        setVisible(true);  // show this JFrame
   
        board = new Seed[ROWS][COLS]; // allocate array
        initGame(); // initialize the game board contents and game variables
        
    }
    
    public void trackMouse() {
        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int xCo = (e.getX() + CELL_SIZE / 2) / CELL_SIZE;
                int yCo = 16 - (e.getY() + CELL_SIZE / 2) / CELL_SIZE;
                if (xCo > 0 && xCo < 16 && yCo > 0 && yCo < 16) {
                    coordinates.setText("(" + xCo + ", " + yCo + ")");
                } else {
                    coordinates.setText("Out of bounds");
                }
                
                hoverX = xCo * CELL_SIZE;
                hoverY = (16 - yCo) * CELL_SIZE;
                canvas.updateHoverInfo(hoverX, hoverY);
                repaint();
            }
         });
    }
    
    public void mouseClickEvent() {
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
               int mouseX = e.getX();
               int mouseY = e.getY();
               // Get the row and column clicked
               int rowSelected = (mouseY + CELL_SIZE / 2) / CELL_SIZE - 1;
               int colSelected = (mouseX + CELL_SIZE / 2) / CELL_SIZE - 1;
    
               if (currentState == GameState.PLAYING) {
                  if (rowSelected >= 0 && rowSelected < ROWS && colSelected >= 0
                        && colSelected < COLS && board[rowSelected][colSelected] == Seed.EMPTY) {
                     board[rowSelected][colSelected] = currentPlayer; // Make a move
                     updateGame(currentPlayer); // update state
                     // Switch player
                     currentPlayer = (currentPlayer == Seed.BLACK) ? Seed.WHITE : Seed.BLACK;
                  }
               } else {       // game over
                  initGame(); // restart the game
               }
               // Refresh the drawing canvas
               updateStatusBar();
               repaint();  // Call-back paintComponent().
            }
         });
    }
    
    public void setupContainer() {
        statusBar = new JLabel("  ");
        statusBar.setFont(new Font("Helvetica", Font.BOLD, 20));
        
        // Setup the mouse coordinates
        coordinates = new JLabel("  ");
        coordinates.setFont(new Font("Helvetica", Font.BOLD, 20));
        
        // Setup bottom bar with status on left, coordinates on right
        infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(statusBar, BorderLayout.WEST);
        infoPanel.add(coordinates, BorderLayout.EAST);
   
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(canvas, BorderLayout.CENTER);
        cp.add(infoPanel, BorderLayout.SOUTH);
    }
    
    // initialize the game with all board elements as empty. Black goes first.
    public void initGame() {
        for (int i = 0; i < ROWS; i++) {
            for (int k = 0; k < COLS; k++) {
                board[i][k] = Seed.EMPTY;
            }
        }
        currentState = GameState.PLAYING;
        currentPlayer = Seed.BLACK;
        canvas.update(board);
        updateStatusBar();
    }
    
    // update currentState if win or draw condition is met
    public void updateGame(Seed theSeed) {
        if (hasWon(theSeed)) {
            currentState = (theSeed == Seed.BLACK) ? GameState.BLACK_WON : GameState.WHITE_WON;
        } else if (isDraw()) {
            currentState = GameState.DRAW;
        }
    }
    
    public boolean hasWon(Seed theSeed) {
        return (horizontalCheck(theSeed) 
                || verticalCheck(theSeed) 
                || DiagonalCheck(theSeed));
    }
    
    private boolean horizontalCheck(Seed theSeed) {
        for (int row = 0; row < ROWS - 4; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == theSeed
                    && theSeed == board[row + 1][col]
                    && theSeed == board[row + 2][col]
                    && theSeed == board[row + 3][col]
                    && theSeed == board[row + 4][col]) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean verticalCheck(Seed theSeed) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS - 4; col++) {
                if (board[row][col] == theSeed
                    && theSeed == board[row][col + 1]
                    && theSeed == board[row][col + 2]
                    && theSeed == board[row][col + 3]
                    && theSeed == board[row][col + 4]) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean DiagonalCheck(Seed theSeed) {
        boolean result = false;
        
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS - 4; col++) {
                if (board[row][col] == theSeed) {
                    if (row < ROWS - 4)
                        result = result || ForwardSlashCheck(theSeed, row, col);
                    if (row > 3)
                        result = result || BackSlashCheck(theSeed, row, col);
                    if (result) return true;
                }
            }
        }
        return result;
    }
    
    // check for a '/' 5-in-a-row
    private boolean ForwardSlashCheck(Seed theSeed, int row, int col) {
        if (board[row + 1][col + 1] == theSeed
            && board[row + 2][col + 2] == theSeed
            && board[row + 3][col + 3] == theSeed
            && board[row + 4][col + 4] == theSeed) {
            return true;
        }
        return false;
    }
    
    // check for a '\' 5-in-a-row
    private boolean BackSlashCheck(Seed theSeed, int row, int col) {
        if (board[row - 1][col + 1] == theSeed
            && board[row - 2][col + 2] == theSeed
            && board[row - 3][col + 3] == theSeed
            && board[row - 4][col + 4] == theSeed) {
            return true;
        }
        return false;
    }
    
    // game is a draw if every board position is filled
    public boolean isDraw() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] != Seed.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void updateStatusBar() {
        if (currentState == GameState.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            if (currentPlayer == Seed.BLACK) {
               statusBar.setText("Black's Turn");
            } else {
               statusBar.setText("White's Turn");
            }
         } else if (currentState == GameState.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
         } else if (currentState == GameState.BLACK_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("Black Wins. Click to play again.");
         } else if (currentState == GameState.WHITE_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("White Wins. Click to play again.");
         }
    }
    
    public static void main(String[] args) {
        // Run GUI codes in the Event-Dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
              new Gomoku2P(); // Let the constructor do the job
           }
        });
     }
    
}
