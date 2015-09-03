package gomoku;
import java.util.Set;
import java.util.Random;
import java.util.HashSet;
//import java.util.Map;
//import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class Evaluator {
    private Seed[][] board;
//    private Board testBoard;
    private Seed compPlayer;
    private Seed oppPlayer;
//    private Map<Board, Integer> tTable = new HashMap<Board, Integer>();
    
    final int ROWS = DrawCanvas.ROWS;
    final int COLS = DrawCanvas.COLS;
    private final int EVAL_DEPTH = 5;
    private int nodesExamined = 0;
    public Set<ArrayList<Integer>> whitePieces = new HashSet<ArrayList<Integer>>();
    public Set<ArrayList<Integer>> blackPieces = new HashSet<ArrayList<Integer>>();
    private int[][] zobrist;
    private HashMap<Integer, Integer> zmap = new HashMap<Integer, Integer>();
    private int hash = 0;
    
    public Evaluator(Seed[][] board, Seed compPlayer) {
        this.board = board;
//        this.testBoard = new Board(board);
        this.compPlayer = compPlayer;
        this.oppPlayer = ((compPlayer == Seed.BLACK) ? Seed.WHITE : Seed.BLACK);
        initZobrist();
    }
    
    public void initZobrist() {
        zobrist = new int[226][3];
        Random rand = new Random();
        for (int i = 1; i <= 225; i++) {
            for (int k = 1; k <= 2; k++) {
                zobrist[i][k] = rand.nextInt();
            }
        }
        hash();
    }
    
    public void hash() {
        int h = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] != Seed.EMPTY) {
                    int j = 0;
                    if (board[row][col] == Seed.BLACK) j =1;
                    else j = 2;
                    h = h ^ zobrist[15 * row + col + 1][j];
                }
            }
        }
        hash = h;
        
    }
    
    
    public void updatePiece(Seed seed, int row, int col) {
        if (seed == Seed.WHITE) {
            whitePieces.add(new ArrayList<Integer>(Arrays.asList(new Integer[] {row, col})));
            hash = hash ^ zobrist[15 * row + col + 1][2];
        } else if (seed == Seed.BLACK) {
            blackPieces.add(new ArrayList<Integer>(Arrays.asList(new Integer[] {row, col})));
            hash = hash ^ zobrist[15 * row + col + 1][1];
        }
    }
    
    public void deletePiece(Seed seed, int row, int col) {
        if (seed == Seed.WHITE) {
            whitePieces.remove(new ArrayList<Integer>(Arrays.asList(new Integer[] {row, col})));
            hash = hash ^ zobrist[15 * row + col + 1][2];
        } else if (seed == Seed.BLACK) {
            blackPieces.remove(new ArrayList<Integer>(Arrays.asList(new Integer[] {row, col})));
            hash = hash ^ zobrist[15 * row + col + 1][1];
        }
    }
    
    public int[] evaluate() {
        nodesExamined = 0;
        long startTime = System.nanoTime();
        int[] result = evaluate(EVAL_DEPTH, compPlayer, oppPlayer, Integer.MIN_VALUE + 1, Integer.MAX_VALUE);
        long elapsedTime = System.nanoTime() - startTime;
        System.out.println(elapsedTime / 1000000000.0 + ", " + nodesExamined +", " + (nodesExamined * 1000000000.0) / elapsedTime);
//        System.out.println(elapsedTime / 1000000000.0 + " s elapsed");
//        System.out.println(nodesExamined + " nodes examined.");
        updatePiece(compPlayer, result[1], result[2]);
        
        return result;
    }
    
    public int[] evaluate(int depth, Seed p1, Seed p2, int alpha, int beta) {
        Set<List<Integer>> nextMoves = generateMoves();
        
        int score;
        int bestScore = Integer.MIN_VALUE;
        int bestRow = -1;
        int bestCol = -1;
            
        if (depth == 0 || nextMoves.isEmpty()) {
            score = evalPos(p1);
            nodesExamined += 1;
            return new int[] {score, bestRow, bestCol};
        }
        
        nodesExamined += 1;
        if (hasWon(p2)) return new int[] {-99999999, bestRow, bestCol};
        
        for (List<Integer> move : nextMoves) {
            int tempRow = move.get(0);
            int tempCol = move.get(1);
//            System.out.println(p1);
            board[tempRow][tempCol] = p1;
            updatePiece(p1, tempRow, tempCol);
            score = -evaluate(depth - 1, p2, p1, -beta, -alpha)[0];
//            System.out.println("evaluating " + tempRow + ", " + tempCol + " for player " + p1 +", got " + score);
            if (score > bestScore) {
                bestScore = score;
                bestRow = tempRow;
                bestCol = tempCol;
            }
            alpha = Math.max(alpha, score);
            board[tempRow][tempCol] = Seed.EMPTY;
            deletePiece(p1, tempRow, tempCol);
            if (alpha >= beta) break;
        }
//        System.out.println("went with move " + bestRow + ", " + bestCol + " for player " + p1);
        return new int[] {alpha, bestRow, bestCol};
    }
    
    public int evalPos(Seed player) {
        if (zmap.containsKey(hash)) {
            return zmap.get(hash);
        }
        int result = evaluatePosition(player);
        zmap.put(hash, result);
        return result;
    }
    
    public int evaluatePosition(Seed currentPlayer) {
        
        
        String endMsg = " on " + currentPlayer + "'s turn.";
        String foundMsg = " found to have ";
//        if (tTable.containsKey(testBoard)) {
//            System.out.println("match found!");
//            return tTable.get(testBoard);
//        }
        
        Seed oppPlayer = (currentPlayer == Seed.BLACK) ? Seed.WHITE : Seed.BLACK;
        int[] counts;
        int temp4;
        
        int score = 0;
        counts = xInARow(5, currentPlayer);
        if (counts[0] > 10000) {
//            System.out.println(currentPlayer + foundMsg + ">=1 U5" + endMsg); 
            return 99999999;}
        if (counts[1] > 10000) {
//            System.out.println(currentPlayer + foundMsg + ">=1 O5" + endMsg);
            return 99000000;}

        counts = xInARow(5, oppPlayer);
        if (counts[0] > 10000) {
//            System.out.println(oppPlayer + foundMsg + ">=1 U5" + endMsg);
            return -99999999;}
        if (counts[1] > 10000) {
//            System.out.println(oppPlayer + foundMsg + ">=1 O5" + endMsg);
            return -99000000;}
        
        
        counts = xInARow(4, currentPlayer);
        if (counts[0] != 0 || counts[1] != 0) {
//            System.out.println(currentPlayer + foundMsg + "1 U/O 4" + endMsg); 
            return 10000000;
        }
//        
        counts = xInARow(4, oppPlayer);
        if (counts[0] != 0) {
//            System.out.println(oppPlayer + foundMsg + "1 U4" + endMsg); 
            return -10000000; 
        }
        temp4 = counts[1];
        if (temp4 > 1) {
//            System.out.println(oppPlayer + foundMsg + ">1 O4" + endMsg); 
            return -10000000;
        }
//        
//        counts = xInARow(3, currentPlayer);
//        if (counts[0] >= 1) {
//            System.out.println(currentPlayer + foundMsg + ">=1 U3" + endMsg); return 10000000;
//        }
//        score += counts[1] * counts[1] * 5000;
//        
//        counts = xInARow(3, oppPlayer);
//        if (counts[0] >= 2) {
//            System.out.println(oppPlayer + foundMsg + ">1 U3" + endMsg); return -10000000;
//        }
//        if (temp4 == 1 && counts[0] == 1) {
//            System.out.println(oppPlayer + foundMsg + "1 U3 + 1 O4" + endMsg); return -10000000;
//        }
//        
//        score -= (counts[0] * 10000 + counts[1] * counts[1] * 1000);
        
//        counts = xInARow(2, currentPlayer);
//        score += counts[0] * counts[0] * 1000;
//        score += counts[1] * counts[1] * 500;
//        
//        counts = xInARow(2, oppPlayer);
//        score -= counts[0] * counts[0] * 1000;
//        score -= counts[1] * counts[1] * 500;
//
        score += centerCount(currentPlayer, oppPlayer);
//        tTable.put(new Board(Arrays.copyOf(board, board.length)), score);
        return score;
    }
    
    public Set<List<Integer>> generateMoves() {
        Set<List<Integer>> nextMoves= new HashSet<List<Integer>>(); 
        
        // If gameover, i.e., no next move
//        if (hasWon(Seed.WHITE) || hasWon(Seed.BLACK)) {
//           return nextMoves;   // return empty list
//        }
   
        // Search for empty cells and add to the List
        
        for (ArrayList<Integer> a : whitePieces) {
            int row = a.get(0);
            int col = a.get(1);
            
            for (int i = Math.max(0, row - 1); i <= Math.min(14, row + 1); i++) {
                for (int k = Math.max(0, col - 1); k <= Math.min(14, col + 1); k++) {
                    if (board[i][k] == Seed.EMPTY) {
                        nextMoves.add(new ArrayList<Integer>(Arrays.asList(new Integer[] {i, k})));
                    }
                }
            }
        }
        
        for (ArrayList<Integer> a : blackPieces) {
            int row = a.get(0);
            int col = a.get(1);
            
            for (int i = Math.max(0, row - 1); i <= Math.min(14, row + 1); i++) {
                for (int k = Math.max(0, col - 1); k <= Math.min(14, col + 1); k++) {
                    if (board[i][k] == Seed.EMPTY) {
                        nextMoves.add(new ArrayList<Integer>(Arrays.asList(new Integer[] {i, k})));
                    }
                }
            }
        }
        
        return nextMoves;
    }
    
    private int centerCount(Seed p1, Seed p2) {
        int count = 0;
        
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Seed temp = board[row][col];
                if (temp == p1) count += (100 - (Math.pow(7 - row, 2) + Math.pow(7 - col, 2))); 
                else if (temp == p2) count -= (100 - (Math.pow(7 - row, 2) + Math.pow(7 - col, 2))); 
            }
        }
        return count;
    }
    
    public boolean hasWon(Seed player) {
        
        int[] result = xInARow(5, player);
        if (result[0] > 10000) return true;
        if (result[1] > 10000) return true;
        return false;
    }
    
    public int[] xInARow(int x, Seed theSeed) {
        
        int unblocked = 0;
        int oneBlock = 0;
        
        int[] horizontal = horizontalXInARow(x, theSeed);
        int[] vertical = verticalXInARow(x, theSeed);
        int[] diagonal = diagonalXInARow(x, theSeed);
        
        unblocked = horizontal[0] + vertical[0] + diagonal[0];
        oneBlock = horizontal[1] + vertical[1] + diagonal[1];
        
        return new int[] {unblocked, oneBlock};
    }
    
    
    private int[] horizontalXInARow(int x, Seed theSeed) {
        
        int unblocked = 0;
        int oneBlock = 0;
        boolean found = false;
        
        for (ArrayList<Integer> a : (theSeed == Seed.WHITE ? whitePieces : blackPieces)) {
            int row = a.get(0);
            int col = a.get(1);
            
            if (col < COLS - x + 1) {
                boolean temp = true;
                for (int i = 0; i < x; i++) {
                    if (board[row][col + i] != theSeed) {
                        temp = false;
                        break;
                    }
                }
                if (temp) {
                    int val = 0;
                    
                    if (col > 0 && board[row][col - 1] == Seed.EMPTY) val += 1;
                    if (col + x < COLS && board[row][col + x] == Seed.EMPTY) val += 1;
                    if (val == 2) unblocked += 1;
                    if (val == 1) oneBlock += 1;
                    found = true;
                    
                }
            }
        }
        
        if (x == 5 && found) return new int[] {unblocked + 10000, oneBlock + 10000};
        return new int[] {unblocked, oneBlock};
    }
    
    private int[] verticalXInARow(int x, Seed theSeed) {
        
        int unblocked = 0;
        int oneBlock = 0;
        boolean found = false;
        
        for (ArrayList<Integer> a : (theSeed == Seed.WHITE ? whitePieces : blackPieces)) {
            int row = a.get(0);
            int col = a.get(1);
            
            if (row < ROWS - x + 1) {
                boolean temp = true;
                for (int i = 0; i < x; i++) {
                    if (board[row + i][col] != theSeed) {
                        temp = false;
                        break;
                    }
                }
                if (temp) {
                    int val = 0;
                    if (row > 0 && board[row - 1][col] == Seed.EMPTY) val += 1;
                    if (row + x < ROWS && board[row + x][col] == Seed.EMPTY) val += 1;
                    if (val == 2) unblocked += 1;
                    if (val == 1) oneBlock += 1;
                    found = true;
                    
                }
                
            }
        }
        
        if (x == 5 && found) return new int[] {unblocked + 10000, oneBlock + 10000};
        return new int[] {unblocked, oneBlock};
    }
    
    public int[] diagonalXInARow(int x, Seed theSeed) {
        
        
        int unblocked = 0;
        int oneBlock = 0;
        boolean found = false;
        
        for (ArrayList<Integer> a : (theSeed == Seed.WHITE ? whitePieces : blackPieces)) {
            int row = a.get(0);
            int col = a.get(1);
            
            if (col < COLS - x + 1) {

                if (row < ROWS - x + 1) {
                    int forwardTemp = ForwardSlashCheck(board, x, theSeed, row, col);
                    if (forwardTemp !=0) {
                        found = true;
                    }
                    if (forwardTemp == 2) {unblocked += 1;}
                    if (forwardTemp == 1) {oneBlock += 1;}
                    
                }
                if (row > x - 2) {
                    int backTemp = BackSlashCheck(board, x, theSeed, row, col);
                    if (backTemp != 0) {
                        found = true;
                    }
                    if (backTemp == 2) {unblocked += 1;}
                    if (backTemp == 1) {oneBlock += 1;}
                }
            } 
        }
        
        if (x == 5 && found) {return new int[] {unblocked + 10000, oneBlock + 10000};}
        return new int[] {unblocked, oneBlock};
    }
    
    public int ForwardSlashCheck(Seed[][] board, int x, Seed theSeed, int row, int col) {
        
        int val = 0;
        
        for (int i = 1; i < x; i++) {
            if (board[row + i][col + i] != theSeed) {
                return 0; 
            }
        }

        if (row > 0 && col > 0 && board[row - 1][col - 1] == Seed.EMPTY) val += 1;
        if (row + x < ROWS && col + x < COLS && board[row + x][col + x] == Seed.EMPTY) val += 1;
        
        return val;
    }
    
    public int BackSlashCheck(Seed[][] board, int x, Seed theSeed, int row, int col) {
        
        int val = 0;

        for (int i = 1; i < x; i++) {
            if (board[row - i][col + i] != theSeed) return 0;
        }
        
        if (row + 1 < ROWS && col > 0 && board[row + 1][col - 1] == Seed.EMPTY) val += 1;
        if (row > x - 1 && col + x < COLS && board[row - x][col + x] == Seed.EMPTY) val += 1;
        
        return val;
    }    
    
}
