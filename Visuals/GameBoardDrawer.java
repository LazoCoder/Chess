package Visuals;

import Chess.*;
import Chess.Pieces.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Used to draw the chess game to the screen using java Graphics2D.
 */
public class GameBoardDrawer {

    private Board board;
    private BufferedImage wRook, wKnight, wBishop, wKing, wQueen, wPawn;
    private BufferedImage bRook, bKnight, bBishop, bKing, bQueen, bPawn;
    private BufferedImage boardImage;
    private Position highlighted;
    private Position selected;
    private Composite composite;

    private static final int boardX = 60;
    private static final int boardY = 102 - 41;
    private static final int boardSize = 483;
    private static final double tileSize = 60.5;

    private boolean transparency = false;
    private boolean showMarker = false;
    private boolean showMoves = false;
    private boolean showAllMoves = false;

    /**
     * Constructs the Game Board Drawer.
     * @param board     the game board that will be drawn
     */
    public GameBoardDrawer (Board board) {
        this.board = board;
        loadImages();
    }

    /**
     * Loads all the chess assets to local variables.
     */
    private void loadImages () {
        wRook   = getImage("Chess Assets/white_rook");
        wKnight = getImage("Chess Assets/white_knight");
        wBishop = getImage("Chess Assets/white_bishop");
        wKing   = getImage("Chess Assets/white_king");
        wQueen  = getImage("Chess Assets/white_queen");
        wPawn   = getImage("Chess Assets/white_pawn");

        bRook   = getImage("Chess Assets/black_rook");
        bKnight = getImage("Chess Assets/black_knight");
        bBishop = getImage("Chess Assets/black_bishop");
        bKing   = getImage("Chess Assets/black_king");
        bQueen  = getImage("Chess Assets/black_queen");
        bPawn   = getImage("Chess Assets/black_pawn");

        boardImage = getImage("Chess Assets/board");
    }

    /**
     * Gets a particular chess asset from the asset folder.
     * @param path  the path of the .png image
     * @return      a BufferedImage of the asset
     */
    private static BufferedImage getImage (String path) {

        BufferedImage image;

        try {
            image = ImageIO.read(GameBoardDrawer.class.getResource(path + ".png"));
        } catch (IOException ex) {
            throw new RuntimeException("Image could not be loaded.");
        }

        return image;
    }

    /**
     * Paints the entire game board to the screen using Graphics2D.
     * @param graphics  the Graphics2D object that will be used for painting
     */
    public void paint (Graphics2D graphics) {

        paintBoard(graphics);
        composite = graphics.getComposite();

        if (showMoves) {
            paintAllPossibleMoves(graphics);
        }
        if (showAllMoves) {
            paintAllPossibleMovesForAllPieces(graphics);
        }
        if (showMarker) {
            paintMarkerForHighlightedPiece(graphics);
        }

        if (isSelected() && showMoves) {
            paintAllPossibleMoves(graphics, selected);
        }

        paintMarkerForSelectedPiece(graphics);
        paintPieces(graphics);

        if (transparency) {
            paintHighlightedPiece(graphics);
        }
        paintTurnIndicator(graphics);
    }

    /**
     * Draws the background image (the chess board) to the screen.
     * @param graphics  the Graphics2D object that will be used for painting
     */
    private void paintBoard (Graphics2D graphics) {
        int width = (int)(1207*0.50);
        int height = (int)(1295*0.50);
        graphics.setClip(0, 0, width, height);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.drawImage(boardImage, 0, -41, width, height, null);
    }

    /**
     * Draws all the pieces to the screen.
     * @param graphics  the Graphics2D object that will be used for painting
     */
    private void paintPieces (Graphics2D graphics) {
        transparencyOn(graphics);
        for (Position p : board) {
            paintPiece(graphics, determineImage(board.get(p)), p);
        }
        transparencyOff(graphics);
    }

    /**
     * Determines which image belongs to a particular piece.
     * @param piece     the chess piece of the desired image
     * @return          a BufferedImage of the chess piece
     */
    private BufferedImage determineImage (Piece piece) {
        if (piece.getTeam().equals(Team.WHITE)) {
            if (piece instanceof Rook) {
                return wRook;
            } else if (piece instanceof Knight) {
                return wKnight;
            } else if (piece instanceof Bishop) {
                return wBishop;
            } else if (piece instanceof Queen) {
                return wQueen;
            } else if (piece instanceof King) {
                return wKing;
            } else if (piece instanceof Pawn) {
                return wPawn;
            }
        } else {
            if (piece instanceof Rook) {
                return bRook;
            } else if (piece instanceof Knight) {
                return bKnight;
            } else if (piece instanceof Bishop) {
                return bBishop;
            } else if (piece instanceof Queen) {
                return bQueen;
            } else if (piece instanceof King) {
                return bKing;
            } else if (piece instanceof Pawn) {
                return bPawn;
            }
        }
        return null;
    }

    /**
     * Draws a particular chess piece to the screen.
     * @param graphics  the Graphics2D object that will be used for painting
     * @param image     the image of the piece
     * @param position  the position of the piece
     */
    private void paintPiece (Graphics2D graphics, BufferedImage image, Position position) {

        int pWidth = image.getWidth();
        int pHeight = image.getHeight();

        double ratio = pWidth / 45.0;
        int newWidth = (int) (pWidth / ratio);
        int newHeight = (int) (pHeight / ratio);

        int posX = boardX + (int)(tileSize * position.getX()) + 7;
        int posY = boardY + (int)(tileSize * position.getY()) - (newHeight - 50);

        graphics.drawImage(image, posX, posY, newWidth, newHeight, null);
    }

    /**
     * Draws all the possible moves that the highlighted piece can go to.
     * @param graphics  the Graphics2D object that will be used for painting
     */
    private void paintAllPossibleMoves (Graphics2D graphics) {
        if (highlighted != null && !board.isEmptySpot(highlighted)
                && board.get(highlighted).getTeam() == board.getTurn()) {
            paintAllPossibleMoves(graphics, highlighted);
        }
    }

    /**
     * Draws all the possible moves that a particular piece can go to.
     * @param graphics  the Graphics2D object that will be used for painting
     * @param position  the position of the piece who's potential moves will be drawn
     */
    private void paintAllPossibleMoves (Graphics2D graphics, Position position) {

        if (board.get(position) == null) {
            return;
        }

        for (Position pos : board.get(position).getAllPossibleMoves(position)) {

            graphics.setColor(new Color(255, 255, 255));
            graphics.setStroke(new BasicStroke(4));
            graphics.drawRect((int)(boardX + (pos.getX() * tileSize)),
                    (int)(boardY + (pos.getY() * tileSize)),
                    (int)tileSize, (int)tileSize);

            graphics.setColor(new Color(255, 255, 255, 80));
            graphics.setStroke(new BasicStroke(1));
            graphics.fillRect((int)(boardX + (pos.getX() * tileSize)),
                    (int)(boardY + (pos.getY() * tileSize)),
                    (int)tileSize, (int)tileSize);
        }

    }

    /**
     * Draws all the possible moves that all the pieces can go to.
     * @param graphics  the Graphics2D object that will be used for painting
     */
    private void paintAllPossibleMovesForAllPieces (Graphics2D graphics) {
        for (Position p : board) {
            if (board.get(p).getTeam().equals(board.getTurn())) {
                paintAllPossibleMoves(graphics, p);
            }
        }
    }

    /**
     * Draws a rectangular marker around the highlighted piece.
     * @param graphics  the Graphics2D object that will be used for painting
     */
    private void paintMarkerForHighlightedPiece (Graphics2D graphics) {
        if (highlighted != null && !board.isEmptySpot(highlighted)) {

            if (board.get(highlighted).getTeam().equals(board.getTurn())
                    && !board.get(highlighted).getAllPossibleMoves(highlighted).isEmpty()) {
                graphics.setColor(new Color(255, 255, 0));
            } else {
                graphics.setColor(Color.RED);
            }

            graphics.setStroke(new BasicStroke(4));
            graphics.drawRect((int) (boardX + (highlighted.getX() * tileSize)),
                    (int) (boardY + (highlighted.getY() * tileSize)),
                    (int) tileSize, (int) tileSize);
        }
    }

    /**
     * Draws a rectangular marker around the selected piece.
     * @param graphics  the Graphics2D object that will be used for painting
     */
    private void paintMarkerForSelectedPiece (Graphics2D graphics) {
        if (selected != null) {
            graphics.setColor(Color.GREEN);
            graphics.setStroke(new BasicStroke(4));
            graphics.drawRect((int)(boardX + (selected.getX()*tileSize)),
                    (int)(boardY + (selected.getY() * tileSize)),
                    (int)tileSize, (int)tileSize);
        }
    }

    /**
     * Draws the highlightedPiece.
     * This is used when transparency is enabled.
     * The highlighted piece should be the only opaque piece.
     * @param graphics  the Graphics2D object that will be used for painting
     */
    private void paintHighlightedPiece (Graphics2D graphics) {
        if (highlighted != null && !board.isEmptySpot(highlighted)) {
            paintPiece(graphics, determineImage(board.get(highlighted)), highlighted);
        }
    }

    /**
     * Draws a small rectangle in the upper left corner.
     * The colour of this rectangle signifies which Team's turn it is.
     * @param graphics  the Graphics2D object that will be used for painting
     */
    private void paintTurnIndicator (Graphics2D graphics) {

        if (board.getTurn().equals(Team.WHITE)) {
            graphics.setColor(new Color(0xFAFBFB));
        } else {
            graphics.setColor(new Color(0x22313E));
        }

        graphics.setStroke(new BasicStroke(1));
        graphics.fillRect(boardX-30, boardY-30, 15, 15);
    }

    /**
     * Sets a particular piece to be the highlighted piece.
     * @param x     the x coordinate (or column) of the piece
     * @param y     the y coordinate (or row) of the piece
     */
    public void setHighlighted (int x, int y) {
        highlighted = convertCoordinateToPosition(x, y);
    }

    /**
     * Sets a particular position to be the one that is selected.
     * @param position  the position to be selected
     */
    public void select (Position position) {
        selected = position;
    }

    /**
     * UnSelects the selected position.
     */
    public void unSelect () {
        selected = null;
    }

    /**
     * Checks to see if a position is selected.
     * @return  true if a position is selected
     */
    public boolean isSelected () {
        return selected != null;
    }

    /**
     * Gets the position that is selected.
     * @return  the selected position
     */
    public Position getSelected () {
        return selected;
    }

    /**
     * Converts x, y coordinates to a chess board position.
     * @param x     the x coordinate (probably of the mouse)
     * @param y     the y coordinate (probably of the mouse)
     * @return      the chess board position
     */
    public static Position convertCoordinateToPosition (int x, int y) {
        if (!isOnBoard(x, y))
            return null;

        x -= boardX;
        y -= boardY;
        x /= tileSize;
        y /= tileSize;
        Position p = new Position(x, y);

        if (Board.isInBounds(p))
            return p;
        else
            return null;
    }

    /**
     * Checks to see if a coordinate is within the bounds of the chess board.
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @return      true if it is in bounds
     */
    public static boolean isOnBoard (int x, int y) {
        return x >= boardX && x <= boardX + boardSize
                && y >= boardY && y <= boardY + boardSize;
    }

    /**
     * Toggles the transparency of the pieces.
     * This is useful for identifying pieces that have been overlapped.
     */
    public void toggleTransparency () {
        transparency = !transparency;
    }

    /**
     * Toggles the visibility of the squares that a piece can go to.
     */
    public void toggleShowMoves () {
        showMoves = !showMoves;
    }

    /**
     * Toggles the visibility of all the squares that all the current
     * player's pieces can go to.
     */
    public void toggleShowAllMoves () {
        showAllMoves = !showAllMoves;
    }

    /**
     * Toggles the visibility of the marker.
     * The marker is a rectangle that identifies whether a piece may be
     * selected when the mouse is hovering over it.
     */
    public void toggleShowMarker () {
        showMarker = !showMarker;
    }

    /**
     * Turns the transparency of all the pieces on with the exception
     * of the highlighted or currently selected piece.
     * @param graphics  the Graphics2D object that will be used for painting
     */
    private void transparencyOn (Graphics2D graphics) {
        if (!transparency) {
            return;
        }
        if (highlighted != null && !board.isEmptySpot(highlighted)) {
            graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }
    }

    /**
     * Turn the transparency of all the pieces off.
     * @param graphics  the Graphics2D object that will be used for painting
     */
    private void transparencyOff (Graphics2D graphics) {
        graphics.setComposite(composite);
    }

}
