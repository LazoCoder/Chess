package Chess;

import Chess.Pieces.Piece;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates through all the pieces on the Board.
 * Empty squares are skipped.
 */
class BoardIterator implements Iterator<Position> {

    private Piece[][] array;
    private Position current = new Position(0, 0);

    /**
     * Constructs the iterator.
     */
    BoardIterator(Piece[][] array) {
        this.array = array;
        current = findNext(current);
    }

    /**
     * Finds the next non-empty position on the board.
     * @param p     the position to start searching from (inclusive)
     * @return      the next non-empty position
     */
    private Position findNext (Position p) {

        while (p.getY() < 8 && array[p.getY()][p.getX()] == null) {
            p = increment(p);
        }

        return p;
    }

    /**
     * Checks to see if there is another piece on the board.
     * @return  true if there is another piece
     */
    public boolean hasNext () {
        return current.getX() < 8 && current.getY() < 8;
    }

    /**
     * Increments the position to the next square to its left.
     * If the position is the last square on a given row,
     * it returns the first square on the next row.
     * @param position  the position to increment
     * @return          the next immediate position to the right
     */
    private Position increment (Position position) {

        if (position.getX() < 7) {
            position = new Position(position.getX() + 1, position.getY());
        } else {
            position = new Position(0, position.getY() + 1);
        }

        return position;
    }

    /**
     * Gets the position of the next piece.
     * @return  the next piece.
     */
    public Position next () {

        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        Position prev = current;
        current = increment(current);
        current = findNext(current);

        return prev;
    }

}
