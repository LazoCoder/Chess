package Chess;

import Chess.Pieces.*;

import java.io.Serializable;
import java.util.Iterator;

/**
 * The chess board, which contains all of the chess pieces and methods
 * for adding, removing, and checking pieces and squares on the board.
 */
public class Board implements Iterable<Position>, Serializable {

    private Piece[][] array;
    private Team turn, winner;
    private boolean gameOver;

    /**
     * Deals with special moves (en passant, castling etc..).
     */
    MovementHandler movementHandler;

    /**
     * Determines if there is a check, checkmate, stalemate or draw.
     */
    private WinnerHandler winnerHandler;

    /**
     * Keep a reference to each king as they need to be analyzed often to
     * determine checkmates and stalemates.
     */
    private Position kingWhite, kingBlack;

    /**
     * Construct the Board.
     */
    public Board() {
        reset();
    }

    /**
     * Clear the board and place the default starting pieces.
     */
    public void reset () {
        clear();
        loadPieces();
    }

    /**
     * Clear all the pieces from the board.
     */
    public void clear () {
        turn = Team.WHITE;
        winner = null;
        gameOver = false;
        array = new Piece[8][8];
        movementHandler = new MovementHandler(this);
        winnerHandler = new WinnerHandler(this);
    }
    /**
     * Load the pieces to the board in the default chess starting arrangement.
     */
    private void loadPieces () {

        add(new Rook    (this, Team.BLACK), new Position(0, 0));
        add(new Knight  (this, Team.BLACK), new Position(1,0));
        add(new Bishop  (this, Team.BLACK), new Position(2,0));
        add(new Queen   (this, Team.BLACK), new Position(3,0));
        add(new Bishop  (this, Team.BLACK), new Position(5,0));
        add(new Knight  (this, Team.BLACK), new Position(6,0));
        add(new Rook    (this, Team.BLACK), new Position(7,0));
        add(new King    (this, Team.BLACK), new Position(4, 0));

        for (int i = 0; i < 8; i++) {
            add(new Pawn(this, Team.BLACK, false), new Position(i, 1));
        }

        add(new Rook     (this, Team.WHITE), new Position(0,7));
        add(new Knight   (this, Team.WHITE), new Position(1,7));
        add(new Bishop   (this, Team.WHITE), new Position(2,7));
        add(new Queen    (this, Team.WHITE), new Position(3,7));
        add(new Bishop   (this, Team.WHITE), new Position(5,7));
        add(new Knight   (this, Team.WHITE), new Position(6,7));
        add(new Rook     (this, Team.WHITE), new Position(7,7));
        add(new King     (this, Team.WHITE), new Position(4, 7));

        for (int i = 0; i < 8; i++) {
            add(new Pawn(this, Team.WHITE, true), new Position(i, 6));
        }
    }

    /**
     * Add a piece to the board.
     * @param piece     the piece to add to the board
     * @param position  the position to add the piece to on the board
     */
    public void add (Piece piece, Position position) {
        updateKings(piece, position);
        array[position.getY()][position.getX()] = piece;
    }

    /**
     * Get the piece at a particular position.
     * @param position  the position on the board
     * @return          the piece at the position
     */
    public Piece get (Position position) {
        return array[position.getY()][position.getX()];
    }

    /**
     * Remove a piece from the board.
     * @param position  the position of the piece to delete
     */
    void delete (Position position) {
        array[position.getY()][position.getX()] = null;
    }

    /**
     * Move the selected piece to the specified location.
     * @param x     the x coordinate of the destination
     * @param y     the y coordinate of the destination
     */
    public void move (Position p, int x, int y) {
        movementHandler.move(p, x, y);
        updateKings(get(p), p);
        winnerHandler.checkIfGameIsOver();
    }

    /**
     * Update the references to the Kings since they are checked often.
     * This is for efficiency.
     * @param piece     the piece to check
     * @param position  the position of the piece
     */
    void updateKings (Piece piece, Position position) {
        if (piece instanceof King && piece.getTeam() == Team.WHITE) {
            kingWhite = position;
        } else if (piece instanceof King && piece.getTeam() == Team.BLACK) {
            kingBlack = position;
        }
    }

    /**
     * Move a piece to a new position, removing the piece in the new position.
     */
    void move (Position location, Position destination) {
        array[destination.getY()][destination.getX()] = array[location.getY()][location.getX()];
        delete(location);
        get(destination).markAsMoved();
        toggleTurn();
    }

    /**
     * Undo the last move that was made.
     */
    public void undo () {
        if (movementHandler.undo()) {
            gameOver = false;
            winner = null;
            toggleTurn();
        }
    }

    /**
     * Get the number of moves made in the entire game. Counts each player separately.
     * @return  the total number of moves made
     */
    public int getMoveCount () {
        return movementHandler.getMoveCount();
    }

    /**
     * Get all the moves made in the entire game.
     * @return  the moves made
     */
    public String getMoves () {
        return movementHandler.toString();
    }

    /**
     * Check to see if a particular piece can move to a position
     * without breaking any rules (example: hopping off the board
     * or eating a piece from the same team)
     * @param piece     the piece to move
     * @param position  the position to move the piece to
     * @return          true if the position is valid
     */
    public boolean isValidSpot (Piece piece, Position position) {
        if (!isInBounds(position)) {
            return false;
        } else if (!isEmptySpot(position) && sameTeam(get(position), piece)) {
            return false;
        }
        return true;
    }

    /**
     * Check to see if a particular spot on the board is empty.
     * @param position  the position to check
     * @return          true if the spot is empty
     */
    public boolean isEmptySpot (Position position) {
        if (!isInBounds(position)) {
            throw new IndexOutOfBoundsException();
        }
        return array[position.getY()][position.getX()] == null;
    }

    /**
     * Check to see if two pieces are on the same team.
     * @param p1    the first piece
     * @param p2    the second piece
     * @return      returns true if the pieces are on the same team
     */
    public boolean sameTeam (Piece p1, Piece p2) {
        return p1.getTeam().equals(p2.getTeam());
    }

    /**
     * Check to see if a spot is safe from enemies.
     * @param team          the team the spot would be safe for
     * @param destination   the position to check for safety
     * @return              true if no enemies can move there
     */
    public boolean safeSpot (Team team, Position destination) {
        boolean result = true;
        boolean dummyUsed = false;
        Rook dummy;

        /*
         * Put a dummy piece at the destination if there isn't already a piece there.
         * The dummy necessary because Pawns can't move diagonally unless there is a
         * piece there.
         */
        if (isEmptySpot(destination)) {
            dummyUsed = true;
            dummy = new Rook(this, team);
            add(dummy, destination);
        }

        for (Position pos : this) {
            if (!get(pos).getTeam().equals(team)) {
                Piece.filterAgainstCheckOFF();
                if (kingContainsMove(pos, destination)|| pieceContainsMove(pos, destination)) {
                    result = false;
                    break;
                }
                Piece.filterAgainstCheckON();
            }
        }

        if (dummyUsed) {
            delete(destination);
        }
        Piece.filterAgainstCheckON();
        return result;
    }

    /**
     * Check to see if a King contains a particular move.
     * This does not include castling.
     * @param destination   the move to check for
     * @return              true if the King contains the move
     */
    private boolean kingContainsMove (Position position, Position destination) {
        return (isKing(get(position))
                && ((King) get(position)).getSurroundingSquares(position).contains(destination));
    }

    /**
     * Check to see if a piece contains a particular move.
     * @param destination   the move to check for
     * @return              true if the Piece contains the move
     */
    private boolean pieceContainsMove (Position position, Position destination) {
        return !isKing(get(position)) && containsMove(position, destination);
    }

    /**
     * Check to see if a particular piece is a King.
     * @param piece         the piece to check
     * @return              true if the piece is a King
     */
    private boolean isKing (Piece piece) {
        return piece instanceof King;
    }

    /**
     * Check to see if a piece contains a particular move.
     * @param destination   the move to check for
     * @return              true if the piece contains the move
     */
    private boolean containsMove (Position position, Position destination) {
        return get(position).getAllPossibleMoves(position).contains(destination);
    }

    /**
     * Change the turn to the other player.
     */
    void toggleTurn () {
        turn = (turn.equals(Team.WHITE)) ? Team.BLACK : Team.WHITE;
    }

    /**
     * Check to see who's turn it is (white or black).
     * @return      returns the team who's turn it is
     */
    public Team getTurn () {
        return turn;
    }

    /**
     * Check to see which position would result in an en passant.
     * @return          the position that would result in an en passant
     */
    public Position getEnPassant () {
        return movementHandler.getEnPassant();
    }

    /**
     * Set the position that would result in an en passant.
     * @param enPassant the position that would result in an en passant
     */
    public void setEnPassant (Position enPassant) {
        movementHandler.setEnPassant(enPassant);
    }

    /**
     * Get the King of the specified piece's team.
     * @param team      team who's king to check
     * @return          the king of the piece's team
     */
    Position getKing (Team team) {
        return (team == Team.WHITE) ? kingWhite : kingBlack;
    }

    /**
     * The Iterator for the game board.
     * @return  returns the game board iterator
     */
    public Iterator<Position> iterator () {
        return new BoardIterator(array);
    }

    /**
     * Set the team who's turn it should be. Intended to be used when a game
     * is being loaded from a saved game file.
     * @param team  the team who's turn it should be
     */
    public void setTurn (Team team) {
        this.turn = team;
    }

    /**
     * Check to see if the game is over.
     * @return      true if the game is over
     */
    public boolean isGameOver () {
        return gameOver;
    }

    /**
     * Get the winner of the game if the game is over.
     * @return      the team that won the game
     */
    public Team getWinner () {
        if (!gameOver) {
            throw new IllegalStateException("Cannot get winner if game is not over.");
        }

        return winner;
    }

    /**
     * Set the winner of the game. Intended to be used by the winner handler.
     * @param team  the team that won the game
     */
    void setWinner (Team team) {
        gameOver = true;
        winner = team;
    }

    /**
     * Check to see if a piece can move to a position without
     * getting a check on it's own team's King.
     * @param destination   the position the piece would move to
     * @return              true if there would be a check
     */
    public boolean wouldBeCheck (Position position, Position destination) {
        return winnerHandler.wouldBeCheck(position, destination);
    }

    /**
     * Declare the winner in a pop up window that is run on a different thread.
     */
    public void declareWinner () {
        winnerHandler.declareWinner();
    }

    /**
     * Check to see if a position is outside of the bounds of the chess board.
     * @param position  the position to check
     * @return          true if it is in bounds
     */
    public static boolean isInBounds (Position position) {
        return position.getX() >= 0
                && position.getX() <= 7
                && position.getY() >= 0
                && position.getY() <= 7;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {

                Position pos = new Position(x, y);
                Piece p;

                if (!isEmptySpot(pos)) {
                    p = get(pos);
                } else {
                    p = null;
                }

                if (p instanceof Rook) {
                    sb.append("R ");
                } else if (p instanceof Knight) {
                    sb.append("N ");
                } else if (p instanceof Bishop) {
                    sb.append("B ");
                } else if (p instanceof Queen) {
                    sb.append("Q ");
                } else if (p instanceof King) {
                    sb.append("K ");
                } else if (p instanceof Pawn) {
                    sb.append("P ");
                } else {
                    sb.append("- ");
                }
            }
            sb.append("\n");
        }

        return new String(sb);
    }

}
