package jump61;

import java.util.ArrayDeque;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.function.Consumer;

import static jump61.Side.*;

/**
 * Represents the state of a Jump61 game.  Squares are indexed either by
 * row and column (between 1 and size()), or by square number, numbering
 * squares by rows, with squares in row 1 numbered from 0 to size()-1, in
 * row 2 numbered from size() to 2*size() - 1, etc. (i.e., row-major order).
 * <p>
 * A Board may be given a notifier---a Consumer<Board> whose
 * .accept method is called whenever the Board's contents are changed.
 *
 * @author Zheng, Peng
 */
class Board {


    /**
     * A notifier that does nothing.
     */
    private static final Consumer<Board> NOP = (s) -> {
    };
    /**
     * Used in jump to keep track of squares needing processing.  Allocated
     * here to cut down on allocations.
     */
    private final ArrayDeque<Integer> _workQueue = new ArrayDeque<>();

    /**
     * A Board in form of two-dimensional array.
     */
    private Square[][] _content;
    /**
     * An integer that represents the number of moves so far.
     */
    private int _moves;
    /**
     * A LinkedList that stores the past state of board.
     */
    private LinkedList<Board> _history = new LinkedList<>();
    /**
     * A read-only version of this Board.
     */
    private ConstantBoard _readonlyBoard;
    /**
     * Use _notifier.accept(B) to announce changes to this board.
     */
    private Consumer<Board> _notifier;

    /**
     * An uninitialized Board.  Only for use by subtypes.
     */
    protected Board() {
        _notifier = NOP;
    }

    /**
     * An N x N board in initial configuration.
     */
    Board(int N) {
        Square[][] newBoard = new Square[N][N];
        for (int i = 0; i < newBoard.length; i++) {
            for (int j = 0; j < newBoard[i].length; j++) {
                newBoard[i][j] = Square.INITIAL;
            }
        }
        _content = newBoard;
        _notifier = NOP;
        _moves = 0;
    }

    /**
     * A board whose initial contents are copied from BOARD0, but whose
     * undo history is clear, and whose notifier does nothing.
     */
    Board(Board board0) {
        this(board0.size());
        _history = new LinkedList<>();
        copy(board0);
        _notifier = NOP;
        _readonlyBoard = new ConstantBoard(this);

    }

    /**
     * Return the content of the board.
     */
    Square[][] getContent() {
        return _content;
    }

    /**
     * Returns a readonly version of this board.
     */
    Board readonlyBoard() {
        return _readonlyBoard;
    }

    /**
     * (Re)initialize me to a cleared board with N squares on a side. Clears
     * the undo history and sets the number of moves to 0.
     */
    void clear(int N) {
        Board clear = new Board(N);
        _content = clear._content;
        _moves = 0;
        _history = new LinkedList<>();
        announce();
    }

    /**
     * Copy the contents of BOARD into me.
     */
    void copy(Board board) {
        internalCopy(board);
        this._history = board._history;
        this._moves = 0;
    }

    /**
     * Copy the contents of BOARD into me, without modifying my undo
     * history. Assumes BOARD and I have the same size.
     */
    private void internalCopy(Board board) {
        assert size() == board.size();
        for (int i = 0; i < size(); i++) {
            for (int k = 0; k < size(); k++) {
                _content[i][k] = board.get(i + 1, k + 1);
            }
        }
    }

    /**
     * Return the number of rows and of columns of THIS.
     */
    int size() {
        return _content.length;
    }

    /**
     * Returns the contents of the square at row R, column C
     * 1 <= R, C <= size ().
     */
    Square get(int r, int c) {
        return get(sqNum(r, c));
    }

    /**
     * Returns the contents of square #N, numbering squares by rows, with
     * squares in row 1 number 0 - size()-1, in row 2 numbered
     * size() - 2*size() - 1, etc.
     */
    Square get(int n) {
        int row = n / size();
        int col = n % size();
        return _content[row][col];
    }

    /**
     * Returns the total number of spots on the board.
     */
    int numPieces() {
        int sum = 0;
        for (int i = 0; i < size() * size(); i++) {
            sum += get(i).getSpots();
        }
        return sum;
    }

    /**
     * Returns the Side of the player who would be next to move.  If the
     * game is won, this will return the loser (assuming legal position).
     */
    Side whoseMove() {
        return ((numPieces() + size()) & 1) == 0 ? RED : BLUE;
    }

    /**
     * Return true iff row R and column C denotes a valid square.
     */
    final boolean exists(int r, int c) {
        return 1 <= r && r <= size() && 1 <= c && c <= size();
    }

    /**
     * Return true iff S is a valid square number.
     */
    final boolean exists(int s) {
        int N = size();
        return 0 <= s && s < N * N;
    }

    /**
     * Return the row number for square #N.
     */
    final int row(int n) {
        return n / size() + 1;
    }

    /**
     * Return the column number for square #N.
     */
    final int col(int n) {
        return n % size() + 1;
    }

    /**
     * Return the square number of row R, column C.
     */
    final int sqNum(int r, int c) {
        return (c - 1) + (r - 1) * size();
    }

    /**
     * Return a string denoting move (ROW, COL)N.
     */
    String moveString(int row, int col) {
        return String.format("%d %d", row, col);
    }

    /**
     * Return a string denoting move N.
     */
    String moveString(int n) {
        return String.format("%d %d", row(n), col(n));
    }

    /**
     * Returns true iff it would currently be legal for PLAYER to add a spot
     * to square at row R, column C.
     */
    boolean isLegal(Side player, int r, int c) {
        return isLegal(player, sqNum(r, c));
    }

    /**
     * Returns true iff it would currently be legal for PLAYER to add a spot
     * to square #N.
     */
    boolean isLegal(Side player, int n) {
        Side targetSide = get(n).getSide();
        return isLegal(targetSide) && whoseMove().equals(player)
                && getWinner() == null;
    }

    /**
     * Returns true iff PLAYER is allowed to move at this point.
     */
    boolean isLegal(Side player) {
        return player.playableSquare(player);
    }

    /**
     * Returns the winner of the current position, if the game is over,
     * and otherwise null.
     */
    final Side getWinner() {
        if (numOfSide(WHITE) != 0) {
            return null;
        }
        if (numOfSide(RED) == 0) {
            return BLUE;
        }
        if (numOfSide(BLUE) == 0) {
            return RED;
        }
        return null;
    }

    /**
     * Return the number of squares of given SIDE.
     */
    int numOfSide(Side side) {
        int total = 0;
        for (int i = 0; i < size() * size(); i++) {
            if (get(i).getSide().equals(side)) {
                total += 1;
            }
        }
        return total;
    }

    /**
     * return the number of neighbors of square at row R,
     * column C.
     */
    int numNeighbors(int r, int c) {
        if ((r == 1 && (c == 1 || c == size()))
                || r == size() && (c == 1 || c == size())) {
            return 2;
        } else if (r == 1 || r == size() || c == 1 || c == size()) {
            return 3;
        }
        return 4;
    }


    /**
     * Add a spot from PLAYER at row R, column C.  Assumes
     * isLegal(PLAYER, R, C).
     */
    void addSpot(Side player, int r, int c) {
        if (isLegal(player, r, c)) {
            markUndo();
            int neighbors = numNeighbors(r, c);
            int spot = get(r, c).getSpots() + 1;
            internalSet(r, c, spot, player);
            _moves += 1;
            if (get(r, c).getSpots() > neighbors) {
                jump(r, c);
            }
        }
    }

    /**
     * Add a spot from PLAYER at square #N.  Assumes isLegal(PLAYER, N).
     */
    void addSpot(Side player, int n) {
        int row = row(n);
        int col = col(n);
        addSpot(player, row, col);
    }

    /**
     * Set the square at row R, column C to NUM spots (0 <= NUM), and give
     * it color PLAYER if NUM > 0 (otherwise, white).
     */
    void set(int r, int c, int num, Side player) {
        internalSet(r, c, num, player);
        announce();
    }

    /**
     * Set the square at row R, column C to NUM spots (0 <= NUM), and give
     * it color PLAYER if NUM > 0 (otherwise, white).  Does not announce
     * changes.
     */
    private void internalSet(int r, int c, int num, Side player) {
        internalSet(sqNum(r, c), num, player);
    }

    /**
     * Set the square #N to NUM spots (0 <= NUM), and give it color PLAYER
     * if NUM > 0 (otherwise, white). Does not announce changes.
     */
    private void internalSet(int n, int num, Side player) {
        if (num > 0) {
            int row = row(n) - 1;
            int col = col(n) - 1;
            _content[row][col] = Square.square(player, num);
        } else {
            int row = row(n) - 1;
            int col = col(n) - 1;
            _content[row][col] = Square.square(WHITE, num);
        }

    }

    /**
     * Undo the effects of one move (that is, one addSpot command).  One
     * can only undo back to the last point at which the undo history
     * was cleared, or the construction of this Board.
     */
    void undo() {
        if (!_history.isEmpty()) {
            this.internalCopy(_history.removeLast());
        }
    }

    /**
     * Record the beginning of a move in the undo history.
     */
    private void markUndo() {
        this._history.add(new Board(this));
    }

    /**
     * Add DELTASPOTS spots of side PLAYER to row R, column C,
     * updating counts of numbers of squares of each color.
     */
    private void simpleAdd(Side player, int r, int c, int deltaSpots) {
        internalSet(r, c, deltaSpots + get(r, c).getSpots(), player);
    }

    /**
     * Add DELTASPOTS spots of color PLAYER to square #N,
     * updating counts of numbers of squares of each color.
     */
    private void simpleAdd(Side player, int n, int deltaSpots) {
        internalSet(n, deltaSpots + get(n).getSpots(), player);
    }

    /**
     * Do all jumping on this board, assuming that initially, S is the only
     * square that might be over-full.
     * @param row for the row of the board.
     * @param  col for col of the board.
     */
    private void jump(int row, int col) {
        int neighbors = numNeighbors(row, col);
        Side thiSide = get(row, col).getSide();
        int realRow = row - 1;
        int realCol = col - 1;
        if (getWinner() == null) {
            Square now = get(row, col);
            internalSet(row, col, now.getSpots() - neighbors, now.getSide());
            if (row > 1) {
                simpleAdd(thiSide, row - 1, col, 1);
                if (get(row - 1, col).getSpots() > numNeighbors(row - 1, col)
                        && getWinner() == null) {
                    jump(row - 1, col);
                }
            }
            if (row < size()) {
                simpleAdd(thiSide, row + 1, col, 1);
                if (get(row + 1, col).getSpots() > numNeighbors(row + 1, col)
                        && getWinner() == null) {
                    jump(row + 1, col);
                }
            }
            if (col > 1) {
                simpleAdd(thiSide, row, col - 1, 1);
                if (get(row, col - 1).getSpots() > numNeighbors(row, col - 1)
                        && getWinner() == null) {
                    jump(row, col - 1);
                }
            }
            if (col < size()) {
                simpleAdd(thiSide, row, col + 1, 1);
                if (get(row, col + 1).getSpots() > numNeighbors(row, col + 1)
                        && getWinner() == null) {
                    jump(row, col + 1);
                }
            }
        }
    }

    /**
     * Returns my dumped representation.
     */
    @Override
    public String toString() {
        String topDown = "===\n";
        for (int i = 0; i < size(); i++) {
            topDown += "   ";
            for (int k = 0; k < size(); k++) {
                String color = "-";
                if (_content[i][k].getSide().equals(RED)) {
                    color = "r";
                }
                if (_content[i][k].getSide().equals(BLUE)) {
                    color = "b";
                }

                topDown += " " + _content[i][k].getSpots() + "" + color + "";
            }
            topDown += "\n";
        }
        topDown += "===";
        return topDown;
    }

    /**
     * Returns an external rendition of me, suitable for human-readable
     * textual display, with row and column numbers.  This is distinct
     * from the dumped representation (returned by toString).
     */
    public String toDisplayString() {
        String[] lines = toString().trim().split("\\R");
        Formatter out = new Formatter();
        for (int i = 1; i + 1 < lines.length; i += 1) {
            out.format("%2d %s%n", i, lines[i].trim());
        }
        out.format("  ");
        for (int i = 1; i <= size(); i += 1) {
            out.format("%3d", i);
        }
        return out.toString();
    }

    /**
     * Returns the number of neighbors of the square at row R, column C.
     */
    int neighbors(int r, int c) {
        int size = size();
        int n;
        n = 0;
        if (r > 1) {
            n += 1;
        }
        if (c > 1) {
            n += 1;
        }
        if (r < size) {
            n += 1;
        }
        if (c < size) {
            n += 1;
        }
        return n;
    }

    /**
     * Returns the number of neighbors of square #N.
     */
    int neighbors(int n) {
        return neighbors(row(n), col(n));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Board)) {
            return false;
        } else {
            Board B = (Board) obj;
            return this.equals(B);
        }
    }

    @Override
    public int hashCode() {
        return numPieces();
    }

    /**
     * Set my notifier to NOTIFY.
     */
    public void setNotifier(Consumer<Board> notify) {
        _notifier = notify;
        announce();
    }

    /**
     * Take any action that has been set for a change in my state.
     */
    private void announce() {
        _notifier.accept(this);
    }
}
