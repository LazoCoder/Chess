package Chess.Pieces;

import Chess.Board;
import Chess.Position;
import Chess.Team;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;

/**
 * A representation of a King piece. Contains the logic of where the piece can move to.
 */
public class King extends Piece {

    /**
     * Construct the King.
     * @param board     the game board that the King was placed on
     * @param team          the team the King is on
     */
    public King(Board board, Team team) {
        super(board, team);
    }

    @Override
    public HashSet<Position> getAllPossibleMoves(Position position) {
        HashSet<Position> hashSet = new HashSet<>();
        getSurroundingSquares(position).forEach((pos) -> addMove(hashSet, position, pos));
        castling(hashSet);
        return hashSet;
    }

    /**
     * Adds a position to the add if the position does not result in a check
     * to the player making the move.
     * @param hashSet       the add that the position will be added to
     * @param position      the position of the King
     * @param destination   the position that is being validated for safety
     */
    private void addMove (HashSet<Position> hashSet, Position position, Position destination) {
        if (!board.wouldBeCheck(position, destination)) {
            hashSet.add(destination);
        }
    }

    /**
     * Loads the castling positions to the add if castling is possible.
     * @param hashSet       the add to load the positions to
     */
    private void castling (HashSet<Position> hashSet) {

        if (canCastleToTheBottomRight()) {
            hashSet.add(new Position(6, 7));
        }
        if (canCastleToTheTopRight()) {
            hashSet.add(new Position(6, 0));
        }
        if (canCastleToTheBottomLeft()) {
            hashSet.add(new Position(2, 7));
        }
        if (canCastleToTheTopLeft()) {
            hashSet.add(new Position(2, 0));
        }

    }

    /**
     * Checks to see if it is possible to castle with the Rook on the bottom right.
     * @return  true if castling is possible
     */
    private boolean canCastleToTheBottomRight () {
        Position p = new Position(7, 7);
        return !hasMoved()
                && isValidRook(p)
                && isEmptyToTheRightOf(new Position(5, 7), 2)
                && isSafeToTheRightOf(new Position(4, 7), 3);
    }

    /**
     * Checks to see if it is possible to castle with the Rook on the top right.
     * @return  true if castling is possible
     */
    private boolean canCastleToTheTopRight () {
        Position p = new Position(7, 0);
        return !hasMoved()
                && isValidRook(p)
                && isEmptyToTheRightOf(new Position(5, 0), 2)
                && isSafeToTheRightOf(new Position(4, 0), 3);
    }

    /**
     * Checks to see if it is possible to castle with the Rook on the bottom left.
     * @return  true if castling is possible
     */
    private boolean canCastleToTheBottomLeft () {
        Position p = new Position(0, 7);
        return !hasMoved()
                && isValidRook(p)
                && isEmptyToTheRightOf(new Position(1, 7), 2)
                && isSafeToTheRightOf(new Position(2, 7), 3);
    }

    /**
     * Checks to see if it is possible to castle with the Rook on the top left.
     * @return  true if castling is possible
     */
    private boolean canCastleToTheTopLeft () {
        Position p = new Position(0, 0);
        return !hasMoved()
                && isValidRook(p)
                && isEmptyToTheRightOf(new Position(1, 0), 2)
                && isSafeToTheRightOf(new Position(2, 0), 3);
    }

    /**
     * Checks to see if a particular position on the chess board contains
     * a Piece that is a Rook.
     * @param position  the position on the chess board to examine
     * @return          true if there is a Rook located at the position
     */
    private boolean isValidRook (Position position) {
        return !isEmptySpot(position)
                && isRook(position)
                && !isRookMoved(position);
    }

    /**
     * Checks to see if a piece at a particular position is a Rook.
     * The piece at the position cannot be null.
     * @param position  the position on the chess board to examine
     * @return          true if there is a Rook located at the position
     */
    private boolean isRook (Position position) {
        return board.get(position) instanceof Rook;
    }

    /**
     * Checks to see if the Rook at a particular position has been moved.
     * The piece at the position cannot be null.
     * @param position  the position that the Rook is located at
     * @return          true if the Rook has been moved
     */
    private boolean isRookMoved (Position position) {
        Piece piece = board.get(position);

        if (!(piece instanceof Rook)) {
            throw new InputMismatchException();
        }

        return (board.get(position)).hasMoved();
    }

    /**
     * Checks to see if a certain number of squares to the right of a
     * particular position (inclusive) contain no pieces.
     * @param position          the starting position
     * @param squaresToCheck    the amount of squares to wouldBeCheck
     *                          (including the starting position)
     * @return                  true if all the positions are empty
     */
    private boolean isEmptyToTheRightOf (Position position, int squaresToCheck) {
        ArrayList<Position> list = new ArrayList<>();

        for (int i = 0; i < squaresToCheck; i++) {
            list.add(new Position(position.getX() + i, position.getY()));
        }


        for (Position p : list) {
            if (!isEmptySpot(p)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks to see if a certain number of squares to the right of a
     * particular position (inclusive) are safe to move to.
     * @param position          the starting position
     * @param squaresToCheck    the amount of squares to wouldBeCheck
     *                          (including the starting position)
     * @return                  true if all the positions are safe
     */
    private boolean isSafeToTheRightOf (Position position, int squaresToCheck) {

        ArrayList<Position> list = new ArrayList<>();
        Team t = getTeam();

        for (int i = 0; i < squaresToCheck; i++) {
            list.add(new Position(position.getX() + i, position.getY()));
        }

        for (Position p : list) {
            if (!safeSpot(t, p)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the immediate surrounding positions of the King.
     * @param position          the position of the King
     * @return                  surrounding positions
     */
    public ArrayList<Position> getSurroundingSquares (Position position) {
        ArrayList<Position> squares = new ArrayList<>();
        loadHorizontalAndVerticalSquares(position, squares);
        loadDiagonalSquares(position, squares);
        return squares;
    }

    /**
     * Loads the neighboring horizontal and vertical squares.
     * @param position          the position of the King
     * @param squares           the list to load the squares to
     */
    private void loadHorizontalAndVerticalSquares (Position position, ArrayList<Position> squares) {
        Position up         = new Position(position.getX(), position.getY()-1);
        Position down       = new Position(position.getX(), position.getY()+1);
        Position left       = new Position(position.getX()+1, position.getY());
        Position right      = new Position(position.getX()-1, position.getY());

        if (isValidSpot(this, up)) {
            squares.add(up);
        }
        if (isValidSpot(this, down)) {
            squares.add(down);
        }
        if (isValidSpot(this, left)) {
            squares.add(left);
        }
        if (isValidSpot(this, right)) {
            squares.add(right);
        }
    }

    /**
     * Loads the neighboring diagonal squares.
     * @param position  the position of the King
     * @param squares   the list to load the squares to
     */
    private void loadDiagonalSquares (Position position, ArrayList<Position> squares) {
        Position upLeft     = new Position(position.getX()-1, position.getY()-1);
        Position upRight    = new Position(position.getX()+1, position.getY()-1);
        Position downLeft   = new Position(position.getX()-1, position.getY()+1);
        Position downRight  = new Position(position.getX()+1, position.getY()+1);

        if (isValidSpot(this, upLeft)) {
            squares.add(upLeft);
        }
        if (isValidSpot(this, upRight)) {
            squares.add(upRight);
        }
        if (isValidSpot(this, downLeft)) {
            squares.add(downLeft);
        }
        if (isValidSpot(this, downRight)) {
            squares.add(downRight);
        }
    }

    /**
     * Create a new identical object.
     * @param board     the new game board for the piece to be on
     * @return              a new King object
     */
    public King clone (Board board) {
        King king = new King(board, getTeam());
        king.moved = this.moved;
        return king;
    }

    @Override
    public String toString () {
        return super.toString() + " King";
    }

}
