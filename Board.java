package gomoku;
import java.util.Arrays;

public class Board {
    public Seed[][] board;
    
    public Board(Seed[][] board) {
        this.board = board;
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Board)) {
            return false;
        }
        
        Board otherBoard = (Board) object;
        return this.hashCode() == otherBoard.hashCode();
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }
}
