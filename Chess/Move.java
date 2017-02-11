package Chess;

import Chess.Pieces.Piece;

/**
 * Represents a move that is made on the board. Used for keeping track
 * of the moves and for undoing moves.
 * </br>
 * The Move object holds four positions and their respective pieces.
 * The first two positions are the starting location and destination
 * of the piece that the player moved directly. The other two are used
 * when special moves are made. When the player castles, the third and
 * fourth position store the starting location and destination of the
 * Rook. When an en passant occurs, the third position stores the Pawn
 * that had been eaten. Finally, the Move object also holds a position
 * for en passant itself, that is, the position that would result in
 * an en passant if a Pawn was to move there.
 */
class Move {

    private Board board;

    private Position position1, position2, position3, position4;
    private Piece piece1, piece2, piece3, piece4;

    private Position enPassant;

    /**
     * The states of two positions on the board prior to a move being made.
     * @param board     the board that contains the pieces being moved
     * @param position      the location of the piece that will be moved
     * @param destination   the destination of the piece to be moved
     */
    Move (Board board, Position position, Position destination) {

        this.board = board;

        this.position1 = position;
        this.position2 = destination;

        // The piece being moved will never be null.
        this.piece1 = board.get(position).clone(board);

        // The destination may be null, if no piece is eaten.
        this.piece2 = (board.get(destination) == null) ? null : board.get(destination).clone(board);
    }

    /**
     * Get the starting position of the piece that was moved directly by the player.
     * @return  the starting position of the piece
     */
    Position getPosition1 () {
        return position1;
    }

    /**
     * Get the destination position of the piece that was moved directly by the player.
     * @return  the destination position of the piece
     */
    Position getPosition2 () {
        return position2;
    }

    /**
     * Get the piece that was moved directly by the player, before it was moved.
     * @return  the piece that was moved
     */
    Piece getPiece1 () {
        return piece1;
    }

    /**
     * Get the piece that was eaten.
     * @return  null if no piece was eaten
     */
    Piece getPiece2 () {
        return piece2;
    }

    /**
     * Save the original position of the Rook if castling occurred.
     * Or save the position of the Pawn that was eaten during an en passant.
     * @param position  the starting position of the Rook
     */
    void setPosition3 (Position position) {
        this.position3 = position;
        this.piece3 = board.get(position).clone(board);
    }

    /**
     * Get the original position of the Rook before castling occurred.
     * Or get the position of the Pawn that was eaten during an en passant.
     * @return  null if no castling or en passant occurred
     */
    Position getPosition3 () {
        return position3;
    }

    /**
     * Get the Rook piece if castling occurred, in the state it was in
     * before castling occurred. Or get the Pawn piece if en passant occurred.
     * @return  null if no castling or en passant occurred
     */
    Piece getPiece3 () {
        return piece3;
    }

    /**
     * Save the destination position of the Rook if castling occurred.
     * @param position  the destination position of the Rook
     */
    void setPosition4 (Position position) {
        this.position4 = position;
        this.piece4 = (board.get(position) == null) ? null : board.get(position).clone(board);
    }

    /**
     * Save the destination position of the Rook if castling occurred.
     * @return  null if no castling occurred
     */
    Position getPosition4 () {
        return position4;
    }

    /**
     * Get the Rook piece if castling occurred, in the state it was in
     * after castling occurred.
     * @return  null if no castling occurred
     */
    Piece getPiece4 () {
        return piece4;
    }

    /**
     * Set the position that would result in an en passant if a Pawn moved there.
     * @param enPassant     the en passant position
     */
    void setEnPassant (Position enPassant) {
        this.enPassant = (enPassant == null) ? null : enPassant;
    }

    /**
     * Get the position that would result in an en passant if a Pawn moved there.
     * @return  the en passant position
     */
    Position getEnPassant () {
        return enPassant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (position1 != null ? !position1.equals(move.position1) : move.position1 != null) return false;
        if (position2 != null ? !position2.equals(move.position2) : move.position2 != null) return false;
        if (piece1 != null ? !piece1.equals(move.piece1) : move.piece1 != null) return false;
        return piece2 != null ? piece2.equals(move.piece2) : move.piece2 == null;

    }

    @Override
    public int hashCode() {
        int result = position1 != null ? position1.hashCode() : 0;
        result = 31 * result + (position2 != null ? position2.hashCode() : 0);
        result = 31 * result + (piece1 != null ? piece1.hashCode() : 0);
        result = 31 * result + (piece2 != null ? piece2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString () {

        StringBuilder sb = new StringBuilder();

        sb.append(piece1);
        sb.append(" (" + position1.getX() + ", " + position1.getY() + ")\t-> ");
        sb.append("(" + position2.getX() + ", " + position2.getY() + ")");
        sb.append(" | EnPassant: " + enPassant);

        if (position3 != null) {
            sb.append(" | " + piece3);
            sb.append(" (" + position3.getX() + ", " + position3.getY() + ")");
        }

        if (position4 != null) {
            sb.append("\t-> (" + position4.getX() + ", " + position4.getY() + ")");

        }

        sb.append("\n");

        return new String(sb);
    }

}
