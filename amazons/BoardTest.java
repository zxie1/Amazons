package amazons;

import org.junit.Test;

import static amazons.Piece.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import static amazons.Board.*;


/** Tests of the Board class.
 *  @author Yuan Xie.
 */

public class BoardTest {

    private Board b;

    @Test
    public void testIsUnblockedMove() {
        b = new Board();
        makeBoard(b);
        assertTrue(b.isUnblockedMove(Square.sq(32), Square.sq(22), null));
        assertFalse(b.isUnblockedMove(Square.sq(32), Square.sq(37), null));
    }

    @Test
    public void testMakeMoveAndUndo() {
        b = new Board();
        makeBoard(b);
        b.makeMove(Move.mv(Square.sq(32), Square.sq(33), Square.sq(0)));
        assertEquals(SMILE1, b.toString());
        b.undo();
        assertEquals(SMILE, b.toString());
        assertEquals(0, b.numMoves());
    }

    @Test
    public void testReachableFrom() {
        b = new Board();
        makeBoard(b);
        Iterator<Square> a = b.reachableFrom(Square.sq(88), null);
        assertEquals(Square.sq(98), a.next());
        assertEquals(Square.sq(99), a.next());
        assertEquals(Square.sq(89), a.next());
        assertEquals(Square.sq(79), a.next());
        assertEquals(Square.sq(77), a.next());
        assertEquals(Square.sq(97), a.next());

        b = new Board();
        a = b.reachableFrom(Square.sq(3), null);
        ArrayList<Square> result = new ArrayList<>();
        Square c = a.next();
        while (c != null) {
            result.add(c);
            c = a.next();
        }
        assertEquals(20, result.size());

        b = new Board();
        makeBoard(b);
        a = b.reachableFrom(Square.sq(77), null);
        assertEquals(null, a.next());

    }

    @Test
    public void testLegalMoves() {
        b = new Board();
        Iterator<Move> a = b.legalMoves(WHITE);
        ArrayList<Move> result = new ArrayList<>();
        Move c = a.next();
        while (c != null) {
            result.add(c);
            c = a.next();
        }
        assertEquals(2176, result.size());

        b = new Board();
        for (int i = 0; i < 97; i += 1) {
            b.put(SPEAR, Square.sq(i));
        }
        b.put(WHITE, Square.sq(99));
        a = b.legalMoves(WHITE);
        result = new ArrayList<>();
        c = a.next();
        while (c != null) {
            result.add(c);
            c = a.next();
        }
        assertEquals(4, result.size());

        b = new Board();
        makeboard1(b);
        a = b.legalMoves(WHITE);
        result = new ArrayList<>();
        c = a.next();
        while (c != null) {
            result.add(c);
            c = a.next();
        }
        assertEquals(4, result.size());

        b = new Board();
        makeBoard(b);
        b.put(BLACK, Square.sq(77));
        a = b.legalMoves(BLACK);
        assertEquals(null, a.next());
    }

    @Test
    public void testSquare() {
        assertEquals("j2", Square.sq(19).toString());
    }

    private void makeboard1(Board board) {
        for (int i = 0; i < 100; i += 1) {
            board.put(BLACK, Square.sq(i));
        }
        board.put(EMPTY, Square.sq(99));
        board.put(EMPTY, Square.sq(89));
        board.put(WHITE, Square.sq(79));
    }

    private void makeBoard(Board board) {
        board.put(EMPTY, Square.sq(0, 3));
        board.put(EMPTY, Square.sq(0, 6));
        board.put(EMPTY, Square.sq(9, 3));
        board.put(EMPTY, Square.sq(9, 6));
        board.put(EMPTY, Square.sq(3, 0));
        board.put(EMPTY, Square.sq(3, 9));
        board.put(EMPTY, Square.sq(6, 0));
        board.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        board.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                board.put(SPEAR, Square.sq(col, row));
            }
        }
        board.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            board.put(WHITE, Square.sq(lip, 2));
        }
        board.put(WHITE, Square.sq(2, 3));
        board.put(WHITE, Square.sq(7, 3));
    }

    static final String SMILE =
            "   - - - - - - - - - -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - S - S - - S - S -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - W - - - - W - -\n"
                    + "   - - - W W W W - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n";

    static final String SMILE1 =
            "   - - - - - - - - - -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - S - S - - S - S -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - W - - - W - -\n"
                    + "   - - - W W W W - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   S - - - - - - - - -\n";
}
