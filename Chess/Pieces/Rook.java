package Chess.Pieces;

import Chess.Board;
import Chess.Position;
import Chess.Team;

import java.util.HashSet;

/**
 * A representation of a Rook piece. Contains the logic of where the piece can move to.
 */
public class Rook extends Piece {

    /**
     * Constructs the Rook.
     * @param board     the game board that the Rook was placed on
     * @param team          the team the Rook is on
     */
    public Rook(Board board, Team team) {
        super(board, team);
    }

    @Override
    public HashSet<Position> getAllPossibleMoves(Position position) {
        HashSet<Position> hashSet = new HashSet<>();
        loadNorth(hashSet, position);
        loadSouth(hashSet, position);
        loadWest(hashSet, position);
        loadEast(hashSet, position);
        return hashSet;
    }

    /**
     * Loads all the Northward positions that the Rook can move to.
     * @param hashSet   the add that the positions are loaded to
     * @param position      the position of the Rook
     */
    private void loadNorth (HashSet<Position> hashSet, Position position) {
        for (int i = 1; i < 8; i++) {
            Position p = new Position(position.getX(), position.getY()-i);
            if (blocked(hashSet, position, p)) {
                return;
            }
        }
    }

    /**
     * Loads all the Southward positions that the Rook can move to.
     * @param hashSet   the add that the positions are loaded to
     * @param position      the position of the Rook
     */
    private void loadSouth (HashSet<Position> hashSet, Position position) {
        for (int i = 1; i < 8; i++) {
            Position p = new Position(position.getX(), position.getY()+i);
            if (blocked(hashSet, position, p)) {
                return;
            }
        }
    }

    /**
     * Loads all of the Westward positions that the Rook can move to.
     * @param hashSet   the add that the positions are loaded to
     * @param position      the position of the Rook
     */
    private void loadWest (HashSet<Position> hashSet, Position position) {
        for (int i = 1; i < 8; i++) {
            Position p = new Position(position.getX()-i, position.getY());
            if (blocked(hashSet, position, p)) {
                return;
            }
        }
    }

    /**
     * Loads all of the Eastward positions that the Rook can move to.
     * @param hashSet   the add that the positions are loaded to
     * @param position      the position of the Rook
     */
    private void loadEast (HashSet<Position> hashSet, Position position) {
        for (int i = 1; i < 8; i++) {
            Position p = new Position(position.getX()+i, position.getY());
            if (blocked(hashSet, position, p)) {
                return;
            }
        }
    }

    /**
     * Create a new identical object.
     * @param board     the new game board for the piece to be on
     * @return              a new Rook object
     */
    public Rook clone (Board board) {
        Rook rook = new Rook(board, getTeam());
        rook.moved = this.moved;
        return rook;
    }

    @Override
    public String toString () {
        return super.toString() + " Rook";
    }

}
