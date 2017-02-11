package AI;

import Chess.Board;
import Chess.Position;
import Chess.Team;

/**
 * Uses the Alpha-Beta Pruning algorithm to play a move in a game of Chess.
 * This algorithm does not create deep copies for each state in the tree.
 * Instead, moves can be "undone". This is how it gets from child to parent.
 */
public class AlphaBetaPruning {

    private static double maxPly;

    /**
     * When the algorithm is done, the best move is saved here and then
     * executed at last line of code in the playMove(...) method.
     */
    private static Position bestPiece, bestPosition;

    /**
     * AlphaBetaAdvanced cannot be instantiated.
     */
    private AlphaBetaPruning() {}

    /**
     * Execute the algorithm.
     * @param team          the team that the AI will identify as
     * @param board         the chess board to play on
     * @param maxPly        the maximum depth
     */
    public static void playMove (Team team, Board board, double maxPly) {
        if (maxPly < 1) {
            throw new IllegalArgumentException("Maximum depth must be greater than 0.");
        }
        AlphaBetaPruning.maxPly = maxPly;
        alphaBetaPruning(team, board, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
        board.move(bestPiece, bestPosition.getX(), bestPosition.getY());
    }

    /**
     * The meat of the algorithm.
     * @param team          the team that the AI will identify as
     * @param board         the chess board to play on
     * @param alpha         the alpha value
     * @param beta          the beta value
     * @param currentPly    the current depth
     * @return              the score of the board
     */
    private static int alphaBetaPruning (Team team, Board board, double alpha, double beta, int currentPly) {
        if (currentPly++ == maxPly || board.isGameOver()) {
            return Scoring.score(team, board, currentPly);
        }

        if (board.getTurn() == team) {
            return getMax(team, board, alpha, beta, currentPly);
        } else {
            return getMin(team, board, alpha, beta, currentPly);
        }
    }

    /**
     * Play the move with the highest score.
     * @param team          the team that the AI will identify as
     * @param board         the chess board to play on
     * @param alpha         the alpha value
     * @param beta          the beta value
     * @param currentPly    the current depth
     * @return              the score of the board
     */
    private static int getMax (Team team, Board board, double alpha, double beta, int currentPly) {
        Position pieceWithBestMove = null;
        Position bestPosition = null;

        int totalMovesMade = board.getMoveCount();

        for (Position piece : board) {

            if (board.get(piece).getTeam() != board.getTurn()) continue;

            for (Position pos : board.get(piece).getAllPossibleMoves(piece)) {
                board.move(piece, pos.getX(), pos.getY());
                int score = alphaBetaPruning(team, board, alpha, beta, currentPly);
                if (totalMovesMade != board.getMoveCount()) {
                    board.undo();
                }

                if (score > alpha) {
                    alpha = score;
                    pieceWithBestMove = piece;
                    bestPosition = pos;
                }

                if (alpha >= beta) { // Pruning.
                    break;
                }

            }
        }

        if (bestPosition != null) {
            AlphaBetaPruning.bestPiece = pieceWithBestMove;
            AlphaBetaPruning.bestPosition = bestPosition;
        }

        if (totalMovesMade != board.getMoveCount()) {
            throw new AssertionError("The number of moves made is not the expected value.\n"
                    + "It should be " + totalMovesMade + " but it is "
                    + board.getMoveCount() + "."
                    + board.getMoves()
                    + "\nBest Piece: " + pieceWithBestMove
                    + "\nBest Position: " + bestPosition);
        }

        return (int)alpha;
    }

    /**
     * Play the move with the lowest score.
     * @param team          the player that the AI will identify as
     * @param board         the chess board to play on
     * @param alpha         the alpha value
     * @param beta          the beta value
     * @param currentPly    the current depth
     * @return              the score of the board
     */
    private static int getMin (Team team, Board board, double alpha, double beta, int currentPly) {

        int totalMovesMade = board.getMoveCount();

        for (Position piece : board) {

            if (board.get(piece).getTeam() != board.getTurn()) continue;

            for (Position pos : board.get(piece).getAllPossibleMoves(piece)) {

                board.move(piece, pos.getX(), pos.getY());
                int score = alphaBetaPruning(team, board, alpha, beta, currentPly);
                board.undo();

                if (score < beta) {
                    beta = score;
                }

                if (alpha >= beta) { // Pruning.
                    break;
                }
            }
        }

        if (totalMovesMade != board.getMoveCount()) {
            throw new AssertionError("The number of moves made is not the expected value.\n"
                    + "It should be " + totalMovesMade
                    + " but it is " + board.getMoveCount() + "."
                    + board.getMoves());
        }

        return (int)beta;
    }

}
