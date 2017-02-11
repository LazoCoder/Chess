package Chess.Pieces;

import Chess.Board;
import Chess.Position;
import Chess.Team;

import java.util.HashSet;

/**
 * A representation of a Queen piece. Contains the logic of where the piece can move to.
 */
public class Queen extends Piece {

    /**
     * Constructs the Queen.
     * @param board     the game board that the Queen was placed on
     * @param team          the team the Queen is on
     */
    public Queen(Board board, Team team) {
        super(board, team);
    }

    @Override
    public HashSet<Position> getAllPossibleMoves(Position position) {

        HashSet<Position> hashSet = new HashSet<>();

        // Queen has same moves as Bishop and Rook together.
        hashSet.addAll(new Rook(board, getTeam()).getAllPossibleMoves(position));
        hashSet.addAll(new Bishop(board, getTeam()).getAllPossibleMoves(position));

        return hashSet;
    }

    /**
     * Create a new identical object.
     * @param board     the new game board for the piece to be on
     * @return              a new Queen object
     */
    public Queen clone (Board board) {
        Queen queen = new Queen(board, getTeam());
        queen.moved = this.moved;
        return queen;
    }

    @Override
    public String toString () {
        return super.toString() + " Queen";
    }

}
