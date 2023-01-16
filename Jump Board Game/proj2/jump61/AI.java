package jump61;
import java.util.Random;

import static jump61.Side.*;

/** An automated Player.
 *  @author P. N. Hilfinger
 */
class AI extends Player {

    /** A new player of GAME initially COLOR that chooses moves automatically.
     *  SEED provides a random-number seed used for choosing moves.
     */
    AI(Game game, Side color, long seed) {
        super(game, color);
        _random = new Random(seed);
    }

    @Override
    String getMove() {
        Board board = getGame().getBoard();

        assert getSide() == board.whoseMove();
        int choice = searchForMove();
        getGame().reportMove(board.row(choice), board.col(choice));
        return String.format("%d %d", board.row(choice), board.col(choice));
    }

    /** Return a move after searching the game tree to DEPTH>0 moves
     *  from the current position. Assumes the game is not over. */
    private int searchForMove() {
        Board work = new Board(getBoard());
        int value;
        assert getSide() == work.whoseMove();
        _foundMove = 0;
        if (getSide() == RED) {
            value = minMax(work, 4,  true, 1,
                    -INFTY, INFTY);
        } else {
            value = minMax(work, 4, true, -1,
                    -INFTY, INFTY);
        }
        return _foundMove;
    }


    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    private int minMax(Board board, int depth, boolean saveMove,
                       int sense, int alpha, int beta) {
        if (depth == 0) {
            return staticEval(board, 1000);
        }
        int bestScore = -sense * INFTY;
        int bestSquare = 0;
        for (int i = 0; i < board.size() * board.size(); i++) {
            if (sense == 1) {
                if (!board.get(i).getSide().equals(BLUE)) {
                    board.addSpot(RED, i);
                    int value = minMax(board, depth - 1,
                            false, -sense, alpha, beta);
                    board.undo();
                    if (sense * value > sense * bestScore) {
                        bestSquare = i;
                        bestScore = value;
                    }
                    if (sense == 1) {
                        alpha = Math.max(alpha, sense * value);
                    } else if (sense == -1) {
                        beta = Math.min(beta, sense * value);
                    }
                    if (alpha > beta) {
                        break;
                    }
                }
            } else {
                if (!board.get(i).getSide().equals(RED)) {
                    board.addSpot(BLUE, i);
                    int value = minMax(board,  depth - 1,
                            false, -sense, alpha, beta);
                    board.undo();
                    if (sense * value > sense * bestScore) {
                        bestSquare = i;
                        bestScore = value;
                    }
                    if (sense == 1) {
                        alpha = Math.max(alpha, sense * value);
                    } else if (sense == -1) {
                        beta = Math.min(beta, sense * value);
                    }
                    if (alpha > beta) {
                        break;
                    }
                }
            }

        }
        if (saveMove && getBoard().getWinner() == null) {
            _foundMove = bestSquare;
        }
        return bestScore;
    }

    /** Return a heuristic estimate of the value of board position B.
     *  Use WINNINGVALUE to indicate a win for Red and -WINNINGVALUE to
     *  indicate a win for Blue. */
    private int staticEval(Board b, int winningValue) {
        double totalScore = 0;
        int blue = 0;
        int red = 0;
        if (b.getWinner() != null) {
            if (b.getWinner() == RED) {
                return winningValue;
            } else {
                return -winningValue;
            }
        }
        blue = b.numOfSide(BLUE);
        red = b.numOfSide(RED);
        totalScore = blue + red;
        return (int) totalScore;
    }

    /** A random-number generator used for move selection. */
    private Random _random;

    /** Used to convey moves discovered by minMax. */
    private int _foundMove;

    /** A number greater than any given integer. */
    private static final int INFTY = Integer.MAX_VALUE;
}
