package Chess.Pieces;

import Chess.Board;
import Chess.Position;
import Chess.Team;

import java.util.HashSet;

/**
 * A representation of a Knight piece. Contains the logic of where the piece can move to.
 */
public class Knight extends Piece {

    /**
     * Constructs the Knight.
     * @param board     the game board that the Knight was placed on
     * @param team          the team the Knight is on
     */
    public Knight(Board board, Team team) {
        super(board, team);
    }

    @Override
    public HashSet<Position> getAllPossibleMoves(Position position) {
        HashSet<Position> hashSet = new HashSet<>();
        loadNorthMoves(hashSet, position);
        loadSouthMoves(hashSet, position);
        loadWestMoves(hashSet, position);
        loadEastMoves(hashSet, position);
        return hashSet;
    }

    /**
     * Loads the moves above the Knight.
     * @param hashSet       the add to load to
     * @param position      the position of the Knight
     */
    private void loadNorthMoves (HashSet<Position> hashSet, Position position) {
        Position moveUpLeft     = new Position(position.getX()-1, position.getY()-2);
        Position moveUpRight    = new Position(position.getX()+1, position.getY()-2);

        if (isValidSpot(this, moveUpLeft)) {
            add(hashSet, position, moveUpLeft);
        }
        if (isValidSpot(this, moveUpRight)) {
            add(hashSet, position, moveUpRight);
        }
    }

    /**
     * Loads the moves under the Knight.
     * @param hashSet       the add to load to
     * @param position      the position of the Knight
     */
    private void loadSouthMoves (HashSet<Position> hashSet, Position position) {
        Position moveDownLeft   = new Position(position.getX()-1, position.getY()+2);
        Position moveDownRight  = new Position(position.getX()+1, position.getY()+2);

        if (isValidSpot(this, moveDownLeft)) {
            add(hashSet, position, moveDownLeft);
        }
        if (isValidSpot(this, moveDownRight)) {
            add(hashSet, position, moveDownRight);
        }
    }

    /**
     * Loads the moves to the left of the Knight.
     * @param hashSet       the add to load to
     * @param position      the position of the Knight
     */
    private void loadWestMoves (HashSet<Position> hashSet, Position position) {
        Position moveLeftDown   = new Position(position.getX()-2, position.getY()+1);
        Position moveLeftUp     = new Position(position.getX()-2, position.getY()-1);

        if (isValidSpot(this, moveLeftDown)) {
            add(hashSet, position, moveLeftDown);
        }
        if (isValidSpot(this, moveLeftUp)) {
            add(hashSet, position, moveLeftUp);
        }
    }

    /**
     * Loads the moves to the right of the Knight.
     * @param hashSet       the add to load to
     * @param position      the position of the Knight
     */
    private void loadEastMoves (HashSet<Position> hashSet, Position position) {
        Position moveRightDown  = new Position(position.getX()+2, position.getY()+1);
        Position moveRightUp    = new Position(position.getX()+2, position.getY()-1);

        if (isValidSpot(this, moveRightDown)) {
            add(hashSet, position, moveRightDown);
        }
        if (isValidSpot(this, moveRightUp)) {
            add(hashSet, position, moveRightUp);
        }
    }

    /**
     * Create a new identical object.
     * @param board     the new game board for the piece to be on
     * @return              a new Knight object
     */
    public Knight clone (Board board) {
        Knight knight = new Knight(board, getTeam());
        knight.moved = this.moved;
        return knight;
    }

    @Override
    public String toString () {
        return super.toString() + " Knight";
    }

}
