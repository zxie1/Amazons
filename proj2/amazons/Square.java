package amazons;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static amazons.Utils.*;

/** Represents a position on an Amazons board.  Positions are numbered
 *  from 0 (lower-left corner) to 99 (upper-right corner).  Squares
 *  are immutable and unique: there is precisely one square created for
 *  each distinct position.  Clients create squares using the factory method
 *  sq, not the constructor.  Because there is a unique Square object for each
 *  position, you can freely use the cheap == operator (rather than the
 *  .equals method) to compare Squares, and the program does not waste time
 *  creating the same square over and over again.
 *  @author Yuan Xie.
 */
final class Square {

    /** The regular expression for a square designation (e.g.,
     *  a3). For convenience, it is in parentheses to make it a
     *  group.  This subpattern is intended to be incorporated into
     *  other pattern that contain square designations (such as
     *  patterns for moves). */
    static final String SQ = "([a-j](?:[1-9]|10))";

    /** Return my row position, where 0 is the bottom row. */
    int row() {
        return _row;
    }

    /** Return my column position, where 0 is the leftmost column. */
    int col() {
        return _col;
    }

    /** Return my index position (0-99).  0 represents square a1, and 99
     *  is square j10. */
    int index() {
        return _index;
    }

    /** Return true iff THIS - TO is a valid queen move. */
    boolean isQueenMove(Square to) {
        if (this == to || to == null || to.index() < 0
                || to.index() > (100 - 1)) {
            return false;
        }
        int colDiff = to._col - _col;
        int rowDiff = to._row - _row;
        int[] direction = {colDiff, rowDiff};
        for (int i = 0; i < DIR.length; i += 1) {
            for (int j = 0; j < 10; j += 1) {
                int[] a = {DIR[i][0] * j, DIR[i][1] * j};
                if (Arrays.equals(a, direction)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Definitions of direction for queenMove.  DIR[k] = (dcol, drow)
     *  means that to going one step from (col, row) in direction k,
     *  brings us to (col + dcol, row + drow). */
    private static final int[][] DIR = {
        { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 },
        { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }
    };

    /** Return the Square that is STEPS>0 squares away from me in direction
     *  DIR, or null if there is no such square. DIR = 0 for north, 1 for
     *  northeast, 2 for east, etc., up to 7 for northwest.
     *  If DIR has another value, return null. Thus, unless the result
     *  is null the resulting square is a queen move away rom me. */
    Square queenMove(int dir, int steps) {
        int col = _col + steps * DIR[dir][0];
        int row = _row + steps * DIR[dir][1];
        if (col > 9 || row > 9 || col < 0 || row < 0) {
            return null;
        }
        int index = row * 10 + col;
        if (index > (100 - 1) || index < 0) {
            return null;
        } else {
            return SQUARES[index];
        }
    }

    /** Return the direction (an int as defined in the documentation
     *  for queenMove) of the queen move THIS-TO. */
    int direction(Square to) {
        assert isQueenMove(to);
        int colDiff = to._col - _col;
        int rowDiff = to._row - _row;
        int[] direction = {colDiff, rowDiff};
        int result = 0;
        for (int i = 0; i < DIR.length; i += 1) {
            for (int j = 0; j < 10; j += 1) {
                int[] a = {DIR[i][0] * j, DIR[i][1] * j};
                if (Arrays.equals(a, direction)) {
                    result = i;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return _str;
    }

    /** Return true iff COL ROW is a legal square. */
    static boolean exists(int col, int row) {
        return row >= 0 && col >= 0 && row < Board.SIZE && col < Board.SIZE;
    }

    /** Return the (unique) Square denoting COL ROW. */
    static Square sq(int col, int row) {
        if (!exists(row, col)) {
            throw error("row or column out of bounds");
        }
        return sq((row * 10) + col);
    }

    /** Return the (unique) Square denoting the position with index INDEX. */
    static Square sq(int index) {
        return SQUARES[index];
    }

    /** Return the (unique) Square denoting the position COL ROW, where
     *  COL ROW is the standard text format for a square (e.g., a4). */
    static Square sq(String col, String row) {
        int thisColumn = col.charAt(0) - 'a';
        int thisRow = Integer.parseInt(row) - 1;
        return sq((thisRow * 10) + thisColumn);
    }

    /** Return the (unique) Square denoting the position in POSN, in the
     *  standard text format for a square (e.g. a4). POSN must be a
     *  valid square designation. */
    static Square sq(String posn) {
        assert posn.matches(SQ);
        int thisColumn = posn.charAt(0) - 'a';
        int thisRow = Integer.parseInt(posn.substring(1)) - 1;
        return sq((thisRow * 10) + thisColumn);
    }

    /** Return an iterator over all Squares. */
    static Iterator<Square> iterator() {
        return SQUARE_LIST.iterator();
    }

    /** Return the Square with index INDEX. */
    private Square(int index) {
        _index = index;
        _row = (index / 10) % 10;
        _col = index % 10;
        char column = (char) (_col + 'a');
        _str = String.format(Character.toString(column)
                + Integer.toString(_row + 1));
    }

    /** The cache of all created squares, by index. */
    private static final Square[] SQUARES =
        new Square[Board.SIZE * Board.SIZE];

    /** SQUARES viewed as a List. */
    private static final List<Square> SQUARE_LIST = Arrays.asList(SQUARES);

    static {
        for (int i = Board.SIZE * Board.SIZE - 1; i >= 0; i -= 1) {
            SQUARES[i] = new Square(i);
        }
    }

    /** My index position. */
    private final int _index;

    /** My row and column (redundant, since these are determined by _index). */
    private final int _row, _col;

    /** My String denotation. */
    private final String _str;

}
