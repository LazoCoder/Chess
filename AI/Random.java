package AI;

import Chess.Board;
import Chess.Position;

import java.util.ArrayList;

/**
 * Selects a random piece and moves it to a random legal position.
 */
public class Random {

    /**
     * Random cannot be instantiated.
     */
    private Random () {}

    /**
     * Move a random piece to a random location depending on who's turn it is.
     * @param board     the chess board to perform the move on
     */
    public static void playMove (Board board) {
        if (!board.isGameOver()) {
            Position piece = getRandomPiece(board);
            moveToRandomLocation(board, piece);
        }
    }

    private static Position getRandomPiece (Board board) {

        ArrayList<Position> pieces = getPieces(board);

        java.util.Random r = new java.util.Random();
        Position piece = pieces.get(r.nextInt(pieces.size()));

        while (board.get(piece).getAllPossibleMoves(piece).isEmpty()) {
            piece = pieces.get(r.nextInt(pieces.size()));
        }

        return piece;
    }

    private static ArrayList<Position> getPieces (Board board) {
        ArrayList<Position> pieces = new ArrayList<>();

        for (Position p : board) {
            if (board.get(p).getTeam().equals(board.getTurn())) {
                pieces.add(p);
            }
        }

        return pieces;
    }

    private static void moveToRandomLocation (Board board, Position piece) {

        ArrayList<Position> moveList =
                new ArrayList<>(board.get(piece).getAllPossibleMoves(piece));

        java.util.Random r = new java.util.Random(1L);
        Position pos = moveList.get(r.nextInt(moveList.size()));
        board.move(piece, pos.getX(), pos.getY());
    }

}
