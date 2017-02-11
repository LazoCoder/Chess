package Chess.Pieces;

import Chess.Board;
import Chess.Position;
import Chess.Team;

import java.util.HashSet;

/**
 * A representation of an abstract chess piece. All the chess pieces inherit
 * this class. It contains a team, a reference to the game board, and a flag
 * for whether or not it has moved or remained still in the game.
 */
public abstract class Piece {

    Board board;
    private Team team;
    boolean moved = false;

    /**
     * If this flag is set to true, then moves are filtered so that no move
     * that results in a check on the player's own King is possible. Moves
     * by pieces from the idle player should not be filtered as this would
     * result in an endless loop.
     */
    public static boolean filterAgainstCheck = true;

    /**
     * Construct Piece.
     * @param board     the game board that the piece was placed on
     * @param team          the team the piece is on
     */
    public Piece (Board board, Team team) {
        this.board = board;
        this.team = team;
    }

    /**
     * Get all the possible positions that a piece can move to.
     * @param position      the position of the piece on the board
     * @return              all the positions that the piece can move to
     */
    public abstract HashSet<Position> getAllPossibleMoves (Position position);

    /**
     * Create a new identical Piece object.
     * @param board     the new game board for the piece to be on
     * @return              a new identical piece
     */
    public abstract Piece clone (Board board);

    /**
     * Helper method to filter a potential move by making sure that it is
     * not added if the result would lead to a check on the Player's own King.
     * <br/>
     * This method is quintessential because a check should only be
     * search for, for the Player who's turn it is. Without this filter
     * there is a chance of an infinite loop and stack overflow.
     * @param hashSet       the add that the potential moves are loaded to
     * @param position      the location of the piece
     * @param destination   the position that is being validated
     */
    void add (HashSet<Position> hashSet, Position position, Position destination) {

        if (filterAgainstCheck && board.getTurn() == getTeam()) {
            if (!board.wouldBeCheck(position, destination)) {
                hashSet.add(destination);
            }
        } else {
            hashSet.add(destination);
        }
    }

    /**
     * Add positions that are valid and check for obstacles. This prevents a
     * piece from jumping over another piece if it is not allowed to
     * (Knight is allowed to do this).
     * @param hashSet       the add that the positions are loaded to
     * @param position      the location of the piece
     * @param destination   the position that is being validated
     * @return              true if there is an obstacle
     */
    boolean blocked (HashSet<Position> hashSet, Position position, Position destination) {
        if (!isInBounds(destination)) {
            return true;
        } else if (isEmptySpot(destination)) {
            add(hashSet, position, destination);
            return false;
        } else if (isSameTeam(destination)) {
            return true;
        } else { // Eating an enemy piece.
            add(hashSet, position, destination);
            return true;
        }
    }

    /**
     * Check to see which team the Piece is on.
     * @return          the team (black or white)
     */
    public Team getTeam () {
        return team;
    }

    /**
     * Check to see if a particular position is not outside the bounds
     * of the chess board.
     * @param position  the position that is being checked
     * @return          true if it is in bounds
     */
    boolean isInBounds (Position position) {
        return Board.isInBounds(position);
    }

    /**
     * Check to see if a particular position is on the board is empty.
     * @param position  the position that is being checked
     * @return          true if it is an empty spot
     */
    boolean isEmptySpot (Position position) {
        return board.isEmptySpot(position);
    }

    /**
     * Check to see if this piece is on the same team as a piece on
     * a particular position.
     * @param position  the position of the other piece
     * @return          true if both pieces are on the same team
     */
    boolean isSameTeam (Position position) {
        return board.sameTeam(this, board.get(position));
    }

    /**
     * Check to see if a piece can move to a particular position.
     * @param piece     the piece that is to be moved
     * @param position  the position that the piece is to be moved to
     * @return          true if the position is in bounds, empty or an enemy
     */
    boolean isValidSpot (Piece piece, Position position) {
        return board.isValidSpot(piece, position);
    }

    /**
     * Check to see if a particular position on the board is safe from
     * enemy pieces at the current moment in time.
     * @param team      the team that would be safe on the position
     * @param position  the position to wouldBeCheck for safety
     * @return          return true if the position is safe
     */
    boolean safeSpot (Team team, Position position) {
        return board.safeSpot(team, position);
    }


    /**
     * Check to see if the Piece has been moved.
     * Useful for castling.
     * @return  true if the Piece has been moved even once since the start of the game.
     */
    public boolean hasMoved () {
        return moved;
    }

    /**
     * Mark the Piece as having been moved.
     * Useful for castling.
     */
    public void markAsMoved () {
        moved = true;
    }

    /**
     * Turn on filter against check. This means that pieces won't include
     * positions that would result in a check on their own team's King.
     * This should be on when getting the potential moves of the Player
     * who's turn it is.
     */
    public static void filterAgainstCheckON () {
        filterAgainstCheck = true;
    }

    /**
     * Turn off filter against check. This means that pieces will include
     * positions that would result in a check on their own team's King.
     * This should be off when getting the potential moves of the Player
     * who's turn it is not.
     */
    public static void filterAgainstCheckOFF () {
        filterAgainstCheck = false;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Piece piece = (Piece) o;

        if (moved != piece.moved) return false;
        return getTeam() == piece.getTeam();
    }


    @Override
    public int hashCode() {
        int result = team.hashCode();
        result = 31 * result + (moved ? 1 : 0);
        result = 31 * result + getClass().hashCode();
        return result;
    }

    @Override
    public String toString () {
        String team = getTeam().toString();
        team = team.toLowerCase();
        team = team.substring(0, 1).toUpperCase() + team.substring(1);
        return team;
    }

}
