package Chess.Pieces;

import Chess.Board;
import Chess.Position;
import Chess.Team;

import java.util.HashSet;

/**
 * A representation of a Bishop piece. Contains the logic of where the piece can move to.
 */
public class Bishop extends Piece {

    /**
     * Constructs the Bishop.
     * @param board     the game board that the Bishop was placed on
     * @param team          the team the Bishop is on
     */
    public Bishop(Board board, Team team) {
        super(board, team);
    }

    @Override
    public HashSet<Position> getAllPossibleMoves(Position position) {
        HashSet<Position> hashSet = new HashSet<>();
        loadNorthWest(hashSet, position);
        loadNorthEast(hashSet, position);
        loadSouthWest(hashSet, position);
        loadSouthEast(hashSet, position);
        return hashSet;
    }

    /**
     * Loads all the positions that the Bishop can move to along the North-West diagonal.
     * @param hashSet   the add that the positions are loaded to
     * @param position  the position of the Bishop
     */
    private void loadNorthWest (HashSet<Position> hashSet, Position position) {
        for (int i = 1; i < 8; i++) {
            Position destination = new Position(position.getX()-i, position.getY()-i);
            if (blocked(hashSet, position, destination)) {
                return;
            }
        }
    }

    /**
     * Loads all the positions that the Bishop can move to along the North-East diagonal.
     * @param hashSet   the add that the positions are loaded to
     * @param position  the position of the Bishop
     */
    private void loadNorthEast (HashSet<Position> hashSet, Position position) {
        for (int i = 1; i < 8; i++) {
            Position destination = new Position(position.getX()+i, position.getY()-i);
            if (blocked(hashSet, position, destination)) {
                return;
            }
        }
    }

    /**
     * Loads all the positions that the Bishop can move to along the South-West diagonal.
     * @param hashSet   the add that the positions are loaded to
     * @param position  the position of the Bishop
     */
    private void loadSouthWest (HashSet<Position> hashSet, Position position) {
        for (int i = 1; i < 8; i++) {
            Position destination = new Position(position.getX()-i, position.getY()+i);
            if (blocked(hashSet, position, destination)) {
                return;
            }
        }
    }

    /**
     * Loads all the positions that that Bishop can move to along the South-East diagonal.
     * @param hashSet   the add that the positions are loaded to
     * @param position  the position of the Bishop
     */
    private void loadSouthEast (HashSet<Position> hashSet, Position position) {
        for (int i = 1; i < 8; i++) {
            Position destination = new Position(position.getX()+i, position.getY()+i);
            if (blocked(hashSet, position, destination)) {
                return;
            }
        }
    }

    /**
     * Create a new identical object.
     * @param board     the new game board for the piece to be on
     * @return              a new Bishop object
     */
    public Bishop clone (Board board) {
        Bishop bishop = new Bishop(board, this.getTeam());
        bishop.moved = this.moved;
        return bishop;
    }

    @Override
    public String toString () {
        return super.toString() + " Bishop";
    }

}
