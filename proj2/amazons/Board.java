package amazons;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;

import static amazons.Piece.*;
import static amazons.Move.mv;


/** The state of an Amazons Game.
 *  @author Yuan Xie.
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 10;

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        _turn = model._turn;
        _winner = model._winner;
        _numMoves = model._numMoves;
        _moveList = model._moveList;
        _board = new Piece[10][10];
        for (int i = 0; i < 10; i += 1) {
            for (int j = 0; j < 10; j += 1) {
                _board[i][j] = model._board[i][j];
            }
        }
    }

    /** Clears the board to the initial position. */
    void init() {
        _turn = WHITE;
        _winner = null;
        _numMoves = 0;
        _board = new Piece[SIZE][SIZE];
        for (int i = 0; i < SIZE; i += 1) {
            for (int j = 0; j < SIZE; j += 1) {
                _board[i][j] = EMPTY;
            }
        }
        _board[9][3] = BLACK;
        _board[9][6] = BLACK;
        _board[6][0] = BLACK;
        _board[6][9] = BLACK;
        _board[3][0] = WHITE;
        _board[3][9] = WHITE;
        _board[0][3] = WHITE;
        _board[0][6] = WHITE;

    }

    /** Return the Piece whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the number of moves (that have not been undone) for this
     *  board. */
    int numMoves() {
        return _numMoves;
    }

    /** Return the winner in the current position, or null if the game is
     *  not yet finished. */
    Piece winner() {
        Iterator<Move> a = legalMoves();
        if (a.next() == null) {
            if (_turn == WHITE) {
                return BLACK;
            } else {
                return WHITE;
            }
        }
        return null;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        int col = s.index() % 10;
        int row = (s.index() / 10) % 10;
        return _board[row][col];
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return _board[row][col];
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        _board[s.row()][s.col()] = p;
    }

    /** Set square (COL, ROW) to P. */
    final void put(Piece p, int col, int row) {
        _winner = EMPTY;
        _board[row][col] = p;
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /** Return true iff FROM - TO is an unblocked queen move on the current
     *  board, ignoring the contents of ASEMPTY, if it is encountered.
     *  For this to be true, FROM-TO must be a queen move and the
     *  squares along it, other than FROM and ASEMPTY, must be
     *  empty. ASEMPTY may be null, in which case it has no effect. */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (!from.isQueenMove(to) || to == null) {
            return false;
        }
        int dir = from.direction(to);
        int colDiff = Math.abs(to.col() - from.col());
        int rowDiff = Math.abs(to.row() - from.row());
        for (int i = 1; i <= Math.max(colDiff, rowDiff); i += 1) {
            if (this.get(from.queenMove(dir, i)) != EMPTY
                    && from.queenMove(dir, i) != asEmpty) {
                return false;
            }
        }
        return true;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        Piece p = this.get(from);
        return (!p.equals(EMPTY) && !p.equals(SPEAR) && p.equals(_turn));
    }

    /** Return true iff FROM-TO is a valid first part of move, ignoring
     *  spear throwing. */
    boolean isLegal(Square from, Square to) {
        return isUnblockedMove(from, to, null);
    }

    /** Return true iff FROM-TO(SPEAR) is a legal move in the current
     *  position. */
    boolean isLegal(Square from, Square to, Square spear) {
        return isUnblockedMove(to, spear, from)
                && isUnblockedMove(from, to, null);

    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /** Move FROM-TO(SPEAR), assuming this is a legal move. */
    void makeMove(Square from, Square to, Square spear) {
        Piece p = this.get(from);
        put(EMPTY, from);
        put(p, to);
        put(SPEAR, spear);
        _numMoves += 1;
        if (_turn == WHITE) {
            _turn = BLACK;
        } else {
            _turn = WHITE;
        }
        _moveList.add(mv(from, to, spear));
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_numMoves > 0) {
            _numMoves -= 2;
            Move mv1 = _moveList.get(_moveList.size() - 1);
            makeMove(mv1.to(), mv1.from(), mv1.spear());
            _moveList.remove(_moveList.size() - 1);
            _moveList.trimToSize();
            this.put(EMPTY, mv1.spear());
            _moveList.remove(_moveList.size() - 1);
            _moveList.trimToSize();
        }
    }

    /** Return an Iterator over the Squares that are reachable by an
     *  unblocked queen move from FROM. Does not pay attention to what
     *  piece (if any) is on FROM, nor to whether the game is finished.
     *  Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     *  feature is useful when looking for Moves, because after moving a
     *  piece, one wants to treat the Square it came from as empty for
     *  purposes of spear throwing.) */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /** Return an Iterator over all legal moves on the current board. */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /** Return an Iterator over all legal moves on the current board for
     *  SIDE (regardless of whose turn it is). */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /** An iterator used by reachableFrom. */
    private class ReachableFromIterator implements Iterator<Square> {

        /** Iterator of all squares reachable by queen move from FROM,
         *  treating ASEMPTY as empty. */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = -1;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            if (!hasNext()) {
                return null;
            } else {
                Square to = _from.queenMove(_dir, _steps);
                toNext();
                if (isUnblockedMove(_from, to, _asEmpty)) {
                    return to;
                }
            }
            return next();
        }

        /** Advance _dir and _steps, so that the next valid Square is
         *  _steps steps in direction _dir from _from. */
        private void toNext() {
            if (_steps == 9 || _steps == 0) {
                _dir += 1;
                _steps = 1;
            } else {
                _steps += 1;
            }

        }

        /** Starting square. */
        private Square _from;
        /** Current direction. */
        private int _dir;
        /** Current distance. */
        private int _steps;
        /** Square treated as empty. */
        private Square _asEmpty;
    }

    /** An iterator used by legalMoves. */
    private class LegalMoveIterator implements Iterator<Move> {

        /** All legal moves for SIDE (WHITE or BLACK). */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _startingSquares.hasNext();
        }

        @Override
        public Move next() {
            if (_start == Square.sq(100 - 1) && get(_start) == _fromPiece) {
                if (_nextSquare != null) {
                    Square s = _spearThrows.next();
                    if (s != null) {
                        return mv(_start, _nextSquare, s);
                    } else {
                        _nextSquare = _pieceMoves.next();
                        _spearThrows = reachableFrom(_nextSquare, _start);
                        return next();
                    }
                }
            }
            if (!hasNext()) {
                return null;
            }
            if (_nextSquare == null) {
                toNext();
                return next();
            }
            Square s = _spearThrows.next();
            if (s != null) {
                return mv(_start, _nextSquare, s);
            } else {
                _nextSquare = _pieceMoves.next();
                _spearThrows = reachableFrom(_nextSquare, _start);
                return next();
            }
        }

        /** Advance so that the next valid Move is
         *  _start-_nextSquare(sp), where sp is the next value of
         *  _spearThrows. */
        private void toNext() {
            _start = _startingSquares.next();
            while (get(_start) != _fromPiece && _startingSquares.hasNext()) {
                _start = _startingSquares.next();
            }
            if (get(_start) == _fromPiece) {
                _pieceMoves = reachableFrom(_start, null);
                _nextSquare = _pieceMoves.next();
                if (_nextSquare != null) {
                    _spearThrows = reachableFrom(_nextSquare, _start);
                }
            }
        }

        /** Color of side whose moves we are iterating. */
        private Piece _fromPiece;
        /** Current starting square. */
        private Square _start;
        /** Remaining starting squares to consider. */
        private Iterator<Square> _startingSquares;
        /** Current piece's new position. */
        private Square _nextSquare;
        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;
        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 9; i >= 0; i -= 1) {
            for (int j = 0; j <= 9; j += 1) {
                if (j == 0) {
                    result += "   " + _board[i][j].toString();
                } else {
                    result += " " + _board[i][j].toString();
                }
            }
            result += "\n";
        }
        return result;
    }

    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
        Collections.emptyIterator();

    /** Piece whose turn it is (BLACK or WHITE). */
    private Piece _turn;

    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;

    /** Representation of the board as a 2D array. */
    private Piece[][] _board;

    /** Number of moves that have been made. */
    private int _numMoves;

    /** ArrayList containing the state of the board. */
    private ArrayList<Move> _moveList = new ArrayList<>(0);
}
