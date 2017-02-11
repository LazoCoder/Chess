package Chess.Pieces;

import Chess.Board;
import Chess.Position;
import Chess.Team;

import java.util.HashSet;

/**
 * A representation of a Pawn piece. Contains the logic of where the piece can move to.
 */
public class Pawn extends Piece {

    private boolean directionNorth;

    /**
     * Constructs the Pawn.
     * @param board         the game board that the Pawn was placed on
     * @param team              the team the Pawn is on
     * @param directionNorth    the direction the Pawn is facing
     */
    public Pawn(Board board, Team team, boolean directionNorth) {
        super(board, team);
        this.directionNorth = directionNorth;
    }

    @Override
    public HashSet<Position> getAllPossibleMoves(Position position) {

        HashSet<Position> hashSet = new HashSet<>();

        if (directionNorth) {
            movingNorth(hashSet, position);
        } else {
            movingSouth(hashSet, position);
        }

        return hashSet;
    }

    /**
     * Loads the possible positions the Pawn can move to if it is facing North.
     * @param hashSet           the add that the positions are loaded to
     * @param position          the starting position
     */
    private void movingNorth (HashSet<Position> hashSet, Position position) {

        Position oneNorth   = new Position(position.getX(), position.getY()-1);
        Position twoNorth   = new Position(position.getX(), position.getY()-2);
        Position eatLeft    = new Position(position.getX()-1, position.getY()-1);
        Position eatRight   = new Position(position.getX()+1, position.getY()-1);

        boolean secondRow = position.getY() == 6;

        moveForward(hashSet, position, oneNorth);

        if (secondRow) {
            moveForward(hashSet, position, twoNorth);
        }

        eat(hashSet, position, eatLeft);
        eat(hashSet, position, eatRight);
    }

    /**
     * Loads the possible positions the Pawn can move to if it is facing South.
     * @param hashSet           the add that the positions are loaded to
     * @param position          the starting position
     */
    private void movingSouth (HashSet<Position> hashSet, Position position) {

        Position oneSouth   = new Position(position.getX(), position.getY()+1);
        Position twoSouth   = new Position(position.getX(), position.getY()+2);
        Position eatLeft    = new Position(position.getX()-1, position.getY()+1);
        Position eatRight   = new Position(position.getX()+1, position.getY()+1);

        boolean seventhRow = position.getY() == 1;

        moveForward(hashSet, position, oneSouth);

        if (seventhRow) {
            moveForward(hashSet, position, twoSouth);
        }

        eat(hashSet, position, eatLeft);
        eat(hashSet, position, eatRight);
    }

    /**
     * Loads positions to the add after checking if they are valid.
     * @param hashSet           the add that the positions are loaded to
     * @param position          the position of the Pawn
     * @param destination       the position that is being validated
     */
    private void moveForward (HashSet<Position> hashSet, Position position, Position destination) {

        if (!isInBounds(destination)) return;

        // Pawn cannot jump over a piece when moving forward two squares.
        if (Math.abs(position.getY() - destination.getY()) > 1) {

            int middle = (position.getY() + destination.getY()) / 2;
            Position pos = new Position(position.getX(), middle);

            if (!isEmptySpot(pos)) {
                return;
            }
        }

        if (isEmptySpot(destination)) {
            add(hashSet, position, destination);
        }
    }

    /**
     * Checks to see if it is possible for the Pawn to eat.
     * Then loads those positions.
     * @param hashSet       the add that the positions are loaded to
     * @param position      the position of the Knight
     * @param destination   the position that is being validated
     */
    private void eat (HashSet<Position> hashSet, Position position, Position destination) {

        if (Board.isInBounds(destination)
                && !isEmptySpot(destination)
                && !isSameTeam(destination)) {
            add(hashSet, position, destination);
        } else if (Board.isInBounds(destination)
                && destination.equals(board.getEnPassant())) {
            add(hashSet, position, destination);
        }

    }

    /**
     * Checks to see the direction the Pawn is facing.
     * @return  true if the Pawn is facing North
     */
    public boolean goingNorth () {
        return directionNorth;
    }

    /**
     * Create a new identical object.
     * @param board     the new game board for the piece to be on
     * @return              a new Pawn object
     */
    public Pawn clone (Board board) {
        Pawn pawn = new Pawn(board, getTeam(), this.directionNorth);
        pawn.moved = this.moved;
        pawn.directionNorth = this.directionNorth;
        return pawn;
    }

    @Override
    public String toString () {
        return super.toString() + " Pawn";
    }

}
