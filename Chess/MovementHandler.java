package Chess;

import Chess.Pieces.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Stack;

/**
 * Handles the special moves, including en passant, castling and pawn promotion.
 * All pieces that are moved come through this class to be evaluated.
 */
public class MovementHandler implements Serializable {

    private Board board;
    private Position enPassant;
    Stack<Move> moves;

    /**
     * Construct the MovementHandler.
     * @param board the game board of the pieces to be moved
     */
    MovementHandler(Board board) {
        this.board = board;
        enPassant = null;
        moves = new Stack<>();
    }

    /**
     * Moves the selected piece to a particular position.
     * @param position  the starting position of the piece
     * @param x         the x coordinate of the destination
     * @param y         the y coordinate of the destination
     */
    void move (Position position, int x, int y) {

        HashSet<Position> hashSet = board.get(position).getAllPossibleMoves(position);
        Position destination = new Position(x, y);

        if (!hashSet.contains(destination)) {
            return;
        }

        Move move = new Move(board, position, destination);
        move.setEnPassant(enPassant);
        moves.push(move);

        markEnPassantPosition(position, destination);
        performEnPassant(position, destination);
        castling(position, destination);
        markAsMoved(position);
        board.move(position, destination);

        pawnPromotion(destination);
    }

    /**
     * Undo the last move that was made.
     */
    boolean undo () {
        if (moves.isEmpty()) {
            return false;
        }

        Move move = moves.pop();

        Piece p1 = move.getPiece1();
        Piece p2 = move.getPiece2();
        Piece p3 = move.getPiece3();
        Piece p4 = move.getPiece4();

        Position pos1 = move.getPosition1();
        Position pos2 = move.getPosition2();
        Position pos3 = move.getPosition3();
        Position pos4 = move.getPosition4();

        Position enPassant = move.getEnPassant();
        setEnPassant(enPassant);

        board.add(p1, pos1);
        board.updateKings(p1, pos1);

        if (p2 != null) {
            board.add(p2, pos2);
            board.updateKings(p2, pos2);
        } else {
            board.add(null, pos2);
        }

        if (p3 != null) {
            board.add(p3, pos3);
            board.updateKings(p3, pos3);
        }

        if (p4 != null) {
            board.add(p4, pos4);
            board.updateKings(p4, pos4);
        } else {
            if (pos4 != null) {
                board.add(null, pos4);
            }
        }

        return true;
    }

    int getMoveCount () {
        return moves.size();
    }

    /**
     * Check to see if an en passant occurred. If so, removed the piece that was eaten.
     * @param position      the piece that will perform the en passant
     * @param destination   the destination of the piece
     */
    private Position performEnPassant (Position position, Position destination) {

        if (enPassantOccurredGoingNorth(position, destination)) {
            Position pos = new Position(destination.getX(), destination.getY() + 1);
            moves.peek().setPosition3(pos);
            board.delete(pos);
            return pos;
        }

        if (enPassantOccurredGoingSouth(position, destination)) {
            Position pos = new Position(destination.getX(), destination.getY() - 1);
            moves.peek().setPosition3(pos);
            board.delete(pos);
            return pos;
        }

        return null;
    }

    /**
     * Check to see if en passant occurred with a pawn facing North.
     * @param position      the starting position of the piece
     * @param destination   the destination of the piece
     * @return              true if an en passant was performed
     */
    private boolean enPassantOccurredGoingNorth (Position position, Position destination) {
        return pawnIsGoingNorth(position)
                && destinationIsDiagonal(position, destination)
                && board.isEmptySpot(destination);
    }

    /**
     * Check to see if en passant occurred with a pawn facing South.
     * @param position      the starting position of the piece
     * @param destination   the destination of the piece
     * @return              true if an en passant was performed
     */
    private boolean enPassantOccurredGoingSouth (Position position, Position destination) {
        return pawnIsGoingSouth(position)
                && destinationIsDiagonal(position, destination)
                && board.isEmptySpot(destination);
    }

    /**
     * Check to see if a pawn is going North.
     * @param p         the position of the Pawn
     * @return          true if it is going North.
     */
    private boolean pawnIsGoingNorth (Position p) {
        return board.get(p) instanceof Pawn && ((Pawn) board.get(p)).goingNorth();
    }

    /**
     * Check to see if a pawn is going South.
     * @param p         the position of the Pawn
     * @return          true if it is going South
     */
    private boolean pawnIsGoingSouth (Position p) {
        return board.get(p) instanceof Pawn && !((Pawn) board.get(p)).goingNorth();
    }

    /**
     * Check to see if a pawn moved diagonally.
     * This mean an en passant occurred.
     * @param position      the starting position of the piece
     * @param destination   the position of the piece
     * @return              true if the piece moved diagonally
     */
    private boolean destinationIsDiagonal (Position position, Position destination) {
        return Math.abs(destination.getX() - position.getX()) > 0;
    }

    /**
     * Check to see if castling occurred.
     * If it did, it moves the pieces to the appropriate positions.
     * @param position      the starting position of the piece
     * @param destination   the position of where the piece moved to
     */
    private void castling (Position position, Position destination) {
        Piece piece = board.get(position);

        // Castling to the right.
        if (piece instanceof King && !piece.hasMoved() && destination.getX() == 6) {
            Position pos1 = new Position(7, destination.getY());
            Position pos2 = new Position(5, position.getY());
            moves.peek().setPosition3(pos1);
            moves.peek().setPosition4(pos2);
            board.move(pos1, pos2);
            board.toggleTurn();
        }

        // Castling to the left.
        if (piece instanceof King && !piece.hasMoved() && destination.getX() == 2) {
            Position pos1 = new Position(0, destination.getY());
            Position pos2 = new Position(3, position.getY());
            moves.peek().setPosition3(pos1);
            moves.peek().setPosition4(pos2);
            board.move(pos1, pos2);
            board.toggleTurn();
        }

    }

    /**
     * Checks to see if a pawn has made it to the other end.
     * If so, promote it to Queen.
     * @param position  the position of th Pawn
     */
    private void pawnPromotion (Position position) {
        if (!(board.get(position) instanceof Pawn)) {
            return;
        }

        if (board.get(position).getTeam().equals(Team.WHITE) && isOnTopRow(position)) {
            Queen queen = new Queen(board, Team.WHITE);
            board.delete(position);
            board.add(queen, position);
        }

        if (board.get(position).getTeam().equals(Team.BLACK) && isOnBottomRow(position)) {
            Queen queen = new Queen(board, Team.BLACK);
            board.delete(position);
            board.add(queen, position);
        }

    }

    /**
     * Mark a pawn as having moved two squares if that occurred.
     * @param position      the starting position of the piece
     * @param destination   the position of where the pawn moved to
     */
    private void markEnPassantPosition(Position position, Position destination) {
        if (pawnMovedTwoSquares(position, destination)) {
            enPassant = new Position(position.getX(), (position.getY()+destination.getY())/2);
        } else {
            enPassant = null;
        }
    }

    /**
     * Check to see if a pawn moved two squares.
     * Useful for checking for en passant.
     * @param position      the starting position of the piece
     * @param destination   the position of where it moved to
     * @return              true if it moved two squares
     */
    private boolean pawnMovedTwoSquares (Position position, Position destination) {
        return board.get(position) instanceof Pawn
                && Math.abs(position.getY() - destination.getY()) > 1;
    }

    /**
     * Marks a piece as moved.
     */
    private void markAsMoved (Position position) {

        Piece piece = board.get(position);

        if (piece instanceof Rook) {
            piece.markAsMoved();
        } else if (piece instanceof King) {
            piece.markAsMoved();
        }
    }

    /**
     * Checks to see if a piece is on the top row.
     * @param position  the position of the piece
     * @return          true if it is on the top row
     */
    private boolean isOnTopRow (Position position) {
        return position.getY() == 0;
    }

    /**
     * Checks to see if a piece is on the bottom row.
     * @param position  the position of the piece
     * @return          true if it is on the bottom row
     */
    private boolean isOnBottomRow (Position position) {
        return position.getY() == 7;
    }

    /**
     * Set the position that would result in an en passant if a Pawn moved there.
     * @param enPassant  the position of the en passant
     */
    void setEnPassant (Position enPassant) {
        this.enPassant = enPassant;
    }

    /**
     * Get the position that would result in an en passant if a Pawn moved there.
     * @return          the position of the en passant
     */
    Position getEnPassant () {
        return enPassant;
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder();
        for (Move move : moves) {
            sb.append("\n");
            sb.append(move);
        }
        return new String(sb);
    }

}
