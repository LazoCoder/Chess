package Controller;

import Chess.Board;
import Chess.Position;
import Visuals.GameBoardDrawer;
import IO.*;

import java.awt.*;

/**
 * Used for interacting with the chess board. This class brings the
 * game logic, player interaction and visuals together.
 */
class Controller {

    public Board board;
    private GameBoardDrawer gameBoardDrawer;

    /**
     * Construct the controller with a default board.
     */
    public Controller () {
        this(new Board());
    }

    /**
     * Construct the controller with a customized board.
     * @param board     the customized board
     */
    public Controller (Board board) {
        this.board = board;
        gameBoardDrawer = new GameBoardDrawer(board);
    }

    /**
     * Perform hovering over a particular coordinate.
     * @param x     the x coordinate
     * @param y     the y coordinate
     */
    void hover (int x, int y) {
        highlight(x, y);
    }

    /**
     * Performs a left click on a particular coordinate.
     * @param x     the x coordinate
     * @param y     the y coordinate
     */
    void leftClick (int x, int y) {

        if (board.isGameOver()) {
            return;
        }

        if (isSelected()) {
            move(x, y);
            highlight(x, y);

            if (board.isGameOver()) {
                board.declareWinner();
            }

        } else {
            select(x, y);
        }

    }

    /**
     * Perform a right click on a particular coordinate.
     * @param x     the x coordinate
     * @param y     the y coordinate
     */
    void rightClick (int x, int y) {
        unSelect();
        highlight(x, y);
    }

    /**
     * Draw the game board to the screen using Graphics2D.
     * @param graphics  the Graphics2D object that will be used for painting
     */
    void paint (Graphics2D graphics) {
        gameBoardDrawer.paint(graphics);
    }

    /**
     * Highlight a chess piece if one exists at the particular coordinate.
     * This is used when the mouse is hovering over a piece.
     * @param x     the x coordinate
     * @param y     the y coordinate
     */
    private void highlight (int x, int y) {
        gameBoardDrawer.setHighlighted(x, y);
    }

    /**
     * Select a chess piece if one exists at the particular coordinate.
     * A piece must be selected before being moved to another square.
     * @param x     the x coordinate
     * @param y     the y coordinate
     */
    private void select (int x, int y) {
        Position pos = GameBoardDrawer.convertCoordinateToPosition(x, y);

        if (pos == null) {
            return;
        }

        if (!board.isEmptySpot(pos)
                && board.getTurn().equals(board.get(pos).getTeam())
                && !board.get(pos).getAllPossibleMoves(pos).isEmpty()) {
            gameBoardDrawer.select(pos);
        }

    }

    /**
     * UnSelect a chess piece if one is currently selected.
     */
    private void unSelect () {
        gameBoardDrawer.unSelect();
    }

    /**
     * Moves the selected chess piece to a new location.
     * @param x     the x coordinate of the new location
     * @param y     the y coordinate of the new location
     */
    private void move (int x, int y) {
        if (!GameBoardDrawer.isOnBoard(x, y)) {
            unSelect();
            return;
        }
        if (!gameBoardDrawer.isSelected()) return;
        Position destination = GameBoardDrawer.convertCoordinateToPosition(x, y);
        board.move(gameBoardDrawer.getSelected(), destination.getX(), destination.getY());
        unSelect();
    }

    /**
     * Undo a move.
     */
    void undo () {
        unSelect();
        board.undo();
    }

    /**
     * Convert the current game board to a String and saves
     * it to a text file of the user's choosing.
     */
    void save () {
        Export.save(board);
    }

    /**
     * Load a previously saved game.
     */
    void load () {
        Import.load(board);
    }

    /**
     * Check to see if a chess piece is currently selected.
     * @return  true if a chess piece is currently selected
     */
    private boolean isSelected () {
        return gameBoardDrawer.isSelected();
    }

    /**
     * Toggle the transparency of all the pieces that are not
     * selected or highlighted. This is useful for identifying
     * pieces that have been overlapped.
     */
    void toggleTransparency () {
        gameBoardDrawer.toggleTransparency();
    }

    /**
     * Toggle the visibility of the squares that a piece can go to.
     */
    void toggleShowMoves () {
        gameBoardDrawer.toggleShowMoves();
    }

    /**
     * Toggle the visibility of all the squares that all the current
     * player's pieces can go to.
     */
    void toggleShowAllMoves () {
        gameBoardDrawer.toggleShowAllMoves();
    }

    /**
     * Toggle the visibility of the marker.
     * The marker is a rectangle that identifies whether a piece may be
     * selected when the mouse is hovering over it.
     */
    void toggleShowMarker () {
        gameBoardDrawer.toggleShowMarker();
    }

}
