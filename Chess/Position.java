package Chess;

/**
 * Representation of a coordinate on the chess board.
 * <br />
 * Positions are synonymous with chess board squares.
 */
public class Position {

    private int x, y;

    /**
     * Constructs the position.
     * @param x     the x coordinate of the position (or column)
     * @param y     the y coordinate of the position (or row)
     */
    public Position (int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x value (or column) of the position.
     * @return  the x value of the position
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y value (or row) of the position.
     * @return  the y value of the position
     */
    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Position position = (Position) o;

        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}
