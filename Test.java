package gomoku;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        Seed theSeed = Seed.BLACK;
        
        Seed[][] board = new Seed[15][15];
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                board[row][col] = Seed.EMPTY;
            }
        }
        Evaluator evaluator = new Evaluator(board, Seed.WHITE);
        
        board[7][7] = Seed.BLACK;
        evaluator.updatePiece(Seed.BLACK, 7, 7);
        
        board[7][8] = Seed.WHITE;
        evaluator.updatePiece(Seed.WHITE, 7, 8);
        
        board[6][6] = Seed.BLACK;
        evaluator.updatePiece(Seed.BLACK, 6, 6);
        
        board[7][9] = Seed.WHITE;
        evaluator.updatePiece(Seed.WHITE, 7, 9);

        board[5][5] = Seed.BLACK;
        evaluator.updatePiece(Seed.BLACK, 5, 5);
        
        board[7][10] = Seed.WHITE;
        evaluator.updatePiece(Seed.WHITE, 7, 10);
        
        board[4][4] = Seed.BLACK;
        evaluator.updatePiece(Seed.BLACK, 4, 4);
        
        board[3][3] = Seed.WHITE;
        evaluator.updatePiece(Seed.WHITE, 3, 3);
        
        int[] a = evaluator.evaluate(2, Seed.BLACK, Seed.WHITE, Integer.MIN_VALUE + 1, Integer.MAX_VALUE);
        System.out.println(a[0] + ", " + a[1] + ", " + a[2]);
        
        int b = evaluator.evaluatePosition(Seed.BLACK);
        System.out.println(b);
        
        int c = evaluator.evaluatePosition(Seed.WHITE);
        System.out.println(c);
        
        board[8][8] = Seed.BLACK;
        evaluator.updatePiece(Seed.BLACK, 8, 8);
        
        System.out.println(evaluator.evaluatePosition(Seed.BLACK));
        System.out.println(evaluator.evaluatePosition(Seed.WHITE));
//        Set<List<Integer>> bbbb = evaluator.generateMoves();
//        System.out.println(bbbb.size() + " size");
//        for (List<Integer> aaaa: bbbb) {
//            System.out.println(aaaa.get(0) + ", " + aaaa.get(1) + " coordinate");
//        }
//        
//        for (ArrayList<Integer> a : evaluator.whitePieces) {
//            System.out.println(a.get(0) + ", " + a.get(1) + " w");
//        }
//        
//        for (ArrayList<Integer> a : evaluator.blackPieces) {
//            System.out.println(a.get(0) + ", " + a.get(1) + " b");
//        }
//        
//        Set<ArrayList<Integer>> ok = (theSeed == Seed.WHITE ? evaluator.whitePieces : evaluator.blackPieces);
//        for (ArrayList<Integer> a : ok) {
//            System.out.println(a.get(0) + ", " + a.get(1) + " xxx");
//        }
//        
//        int result = evaluator.evalPos(Seed.BLACK);
//        
//        int[] results = evaluator.diagonalXInARow(4, Seed.BLACK);
//        System.out.println(results[0] + ", " + results[1]);
//        
//        System.out.println(result);
//        
//        int[] bleh = evaluator.evaluate(4, Seed.WHITE, Seed.BLACK, Integer.MIN_VALUE + 1, Integer.MAX_VALUE);
//        
//        System.out.println(bleh[0] + ", " + bleh[1] + ", " + bleh[2]);
        
    }
}
