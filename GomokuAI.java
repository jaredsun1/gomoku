package gomoku;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class GomokuAI extends Gomoku2P {
    
    public static Seed compPlayer = Seed.WHITE;
    private int[] evaluation = new int[3];
    private Evaluator evaluator;
    private JLabel evalLabel;
    
    @Override
    public void setupContainer() {
        super.setupContainer();
        
        evalLabel = new JLabel("  ");
        evalLabel.setFont(new Font("Helvetica", Font.BOLD, 10));
        statusBar.setFont(new Font("Helvetica", Font.BOLD, 10));
        
        infoPanel.setLayout(new BorderLayout(180, 0));
        infoPanel.add(statusBar, BorderLayout.WEST);
        infoPanel.add(evalLabel, BorderLayout.CENTER);
        infoPanel.add(coordinates, BorderLayout.EAST);
        
    }
    
    @Override
    public void initGame() {
        super.initGame();
        evaluator = new Evaluator(board, compPlayer);
    }
    
    @Override
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
                  if (currentPlayer != compPlayer) { 
                      if (rowSelected >= 0 && rowSelected < ROWS && colSelected >= 0
                            && colSelected < COLS && board[rowSelected][colSelected] == Seed.EMPTY) {
                         board[rowSelected][colSelected] = currentPlayer; // Make a move
                         updateGame(currentPlayer); // update state
                         evaluator.updatePiece(currentPlayer, rowSelected, colSelected);
                         // Switch player
                         currentPlayer = (currentPlayer == Seed.BLACK) ? Seed.WHITE : Seed.BLACK;
                         updateStatusBar();
                         
                         repaint();
                         makeAIMove();
                      }
                  } else {
                      makeAIMove();
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
    
    public void makeAIMove() {
        if (currentState == GameState.PLAYING) {
            evaluation = evaluator.evaluate();
            int row = evaluation[1];
            int col = evaluation[2];
            int eval = evaluation[0] * (compPlayer == Seed.BLACK ? 1: -1);
            if (eval == 99999999) evalLabel.setText("Black victory");
            else if (eval == -99999999) evalLabel.setText("White victory");
            else evalLabel.setText("" + eval);
            board[row][col] = currentPlayer;
            updateGame(currentPlayer);
            evaluator.updatePiece(currentPlayer, row, col);
            currentPlayer = (currentPlayer == Seed.BLACK) ? Seed.WHITE : Seed.BLACK;
        }
    }
    
    public static void main(String[] args) {
        // Run GUI codes in the Event-Dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
              new GomokuAI(); // Let the constructor do the job
           }
        });
     }
    
}
