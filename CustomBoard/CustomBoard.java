package CustomBoard;

import Chess.Board;
import Chess.Pieces.*;
import Chess.Position;
import Chess.Team;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static java.awt.event.KeyEvent.*;

/**
 * Used to create a board with custom piece positions.
 */
public class CustomBoard extends JFrame {

    private static final int WIDTH = (int)(1207*0.50*0.8) - 7;
    private static final int HEIGHT = (int)(1295*0.50*0.8) - 42;
    private static final int STEP = CustomBoard.HEIGHT / 8;

    private static final Color DARK_BROWN = new Color(0x60391e);
    private static final Color BROWN = new Color(0x9e6026);
    private static final Color ORANGE = new Color(0xfdc014);
    private static boolean running = true;

    private JPanel panel;
    private Piece[][] boardArray;
    private Position selected;
    private static Board board;

    // This determines whether the piece being place is white or black.
    private boolean teamWhite = true;

    // The limit and requirement is one king per team.
    private boolean whiteKing, blackKing;

    /**
     * Construct CustomBoard.
     */
    private CustomBoard () {
        whiteKing = false;
        blackKing = false;
        boardArray = new Piece[8][8];
        board = new Board();
        board.clear();
        loadPanel();
        setWindowProperties();
    }

    /**
     * Loads the JPanel into the window and sets the size.
     */
    private void loadPanel () {
        panel = new Panel();
        Container cp = getContentPane();
        cp.add(panel);
        cp.addKeyListener(new MyKeyAdapter());
        cp.addMouseMotionListener(new MyMouseMotionAdapter());
        cp.setFocusable(true);
        cp.requestFocus();
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    /**
     * Sets the properties of the window.
     */
    private void setWindowProperties () {
        int sWidth = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2;
        int sHeight = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2;
        int x = sWidth - (WIDTH / 2);
        int y = sHeight - (HEIGHT / 2);
        setLocation(x, y);

        setResizable(false);
        pack();
        setTitle("Custom Board Creator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * The panel that the custom board will be drawn on.
     */
    private class Panel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawString("", -10, -10); // Load up String Graphics so it doesn't lag later.
            Graphics2D graphics2D = (Graphics2D) g;
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawTiles(graphics2D);
            drawGrid(graphics2D);
            drawBoardArray(graphics2D);
            drawSelected(graphics2D);
        }

        /**
         * Draw the tiles to the screen wit the appropriate color.
         * @param g     The graphics object to draw with.
         */
        private void drawTiles (Graphics2D g) {
            for (int y = 0; y < CustomBoard.HEIGHT-STEP; y+= STEP) {
                for (int x = 0; x < CustomBoard.WIDTH-STEP; x += STEP) {
                    g.setColor(getColor(x, y));
                    g.fillRect(x+2, y+2, STEP, STEP);
                }
            }
        }

        /**
         * Draw the horizontal and vertical lines to the screen.
         * @param g     The graphics object to draw with.
         */
        private void drawGrid (Graphics2D g) {
            g.setStroke(new BasicStroke(5));
            g.setColor(DARK_BROWN);
            for (int y = 0; y < CustomBoard.HEIGHT; y += STEP) {
                g.drawLine(0, y+2, CustomBoard.WIDTH, y+2);
            }
            for (int x = 0; x < CustomBoard.WIDTH; x += STEP) {
                g.drawLine(x+2, 0, x+2, CustomBoard.HEIGHT);
            }
        }

        /**
         * Draw all the pieces to the screen.
         * @param g     The graphics object to draw with.
         */
        private void drawBoardArray (Graphics2D g) {
            for (int y = 0; y < boardArray.length; y++) {
                for (int x = 0; x < boardArray[0].length; x++) {
                    if (boardArray[y][x] == null) continue;
                    g.setColor(boardArray[y][x].color);
                    String label = getLabel(boardArray[y][x].type);
                    if (!boardArray[y][x].moved) {
                        g.setFont(new Font("Monaco", Font.ITALIC, 60));
                    } else {
                        g.setFont(new Font("Monaco", Font.PLAIN, 60));
                    }
                    g.drawString(label, x*STEP+12, y*STEP+STEP-6);
                }
            }
        }

        /**
         * Get a letter that represents the specified piece.
         * @param type  the particular type of piece
         * @return      the letter that represents it (usually the first letter)
         */
        private String getLabel (Type type) {
            if (type == Type.Knight) {
                return "N"; // Cannot be "K" since King is "K".
            } else {
                return type.name().substring(0, 1);
            }
        }

        /**
         * Draw the selected position to emphasize it.
         * @param g     the graphics object to draw with
         */
        private void drawSelected (Graphics2D g) {
            if (selected == null) {
                return;
            }
            int x = selected.getX();
            int y = selected.getY();
            g.setStroke(new BasicStroke(5));
            g.setColor(getColor(selected));
            g.fillRect(x*STEP-4, y*STEP-4, STEP+10, STEP+10);
            g.setColor(getTeam());
            g.drawRect(x*STEP-4, y*STEP-4, STEP+10, STEP+10);

            if (boardArray[y][x] != null) {
                g.setColor(boardArray[y][x].color);
                String label = getLabel(boardArray[y][x].type);
                if (!boardArray[y][x].moved) {
                    g.setFont(new Font("Monaco", Font.ITALIC, 70));
                } else {
                    g.setFont(new Font("Monaco", Font.PLAIN, 70));
                }
                g.drawString(label, x * STEP+9, y * STEP + STEP-2);
            }
        }

        /**
         * Get the color that the tile should be.
         * @param p     the position of the tile
         * @return      BROWN or ORANGE
         */
        private Color getColor (Position p) {
            return getColor(p.getX(), p.getY());
        }

        /**
         * Get the color that the tile should be.
         * @param x     the x coordinate of the tile
         * @param y     the y coordinate of the tile
         * @return      BROWN or ORANGE
         */
        private Color getColor (int x, int y) {
            if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) {
                return ORANGE;
            } else {
                return BROWN;
            }
        }

    }

    /**
     * Detects keyboard button presses mainly for adding and removing pieces.
     */
    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            if (selected == null) {
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_T) {
                teamWhite = !teamWhite;
            } else if (e.getKeyCode() == VK_P) {
                add(Type.Pawn);
            } else if (e.getKeyCode() == VK_R) {
                add(Type.Rook);
            } else if (e.getKeyCode() == VK_B) {
                add(Type.Bishop);
            } else if (e.getKeyCode() == VK_N) {
                add(Type.Knight);
            } else if (e.getKeyCode() == VK_Q) {
                add(Type.Queen);
            } else if (e.getKeyCode() == VK_K) {
                add(Type.King);
            } else if (e.getKeyCode() == VK_BACK_SPACE || e.getKeyCode() == VK_DELETE) {
                setSelected(null);
            } else if (e.getKeyCode() == VK_H) {
                showHelpMessage();
            } else if (e.getKeyCode() == VK_M) {
                if (getSelected() != null) {
                    getSelected().moved = !getSelected().moved;
                }
            } else if (e.getKeyCode() == VK_ENTER) {
                if (!blackKing || !whiteKing) {
                    JOptionPane.showMessageDialog(null, "You cannot proceed without a King on each side.");
                    return;
                }
                loadBoard();
                running = false;
                dispose();
            }
            panel.repaint();
        }
    }

    /**
     * Load the custom board to a Board object.
     */
    private void loadBoard() {
        for (int y = 0; y < boardArray.length; y++) {
            for (int x = 0; x < boardArray[0].length; x++) {
                Piece piece = boardArray[y][x];
                if (piece == null) continue;
                Type type = piece.type;
                Team team = (piece.color == Color.WHITE) ? Team.WHITE : Team.BLACK;
                boolean moved = piece.moved;
                Position pos = new Position(x, y);

                if (type == Type.Pawn) {
                    boolean north = team == Team.WHITE;
                    board.add(new Pawn(board, team, north), pos);
                } else if (type == Type.Rook) {
                    board.add(new Rook(board, team), pos);
                } else if (type == Type.Knight) {
                    board.add(new Knight(board, team), pos);
                } else if (type == Type.Bishop) {
                    board.add(new Bishop(board, team), pos);
                } else if (type == Type.King) {
                    board.add(new King(board, team), pos);
                } else if (type == Type.Queen) {
                    board.add(new Queen(board, team), pos);
                }

                if (moved) board.get(pos).markAsMoved();
            }
        }
        Team team = (teamWhite) ? Team.WHITE : Team.BLACK;
        board.setTurn(team);
    }

    /**
     * Add a piece to the board.
     */
    private void add (Type type) {
        if (type == Type.King) {
            addKing();
        } else {
            Piece p = new Piece(type, getTeam());
            setSelected(p);
        }
    }

    /**
     * Add a King to the board. Make sure there is only one King per team.
     */
    private void addKing () {
        Piece sel = getSelected();
        if (whiteKing && getTeam() == Color.WHITE) {
            if (sel == null || (sel.type != Type.King && sel.color == Color.WHITE)) {
                JOptionPane.showMessageDialog(null, "There is already a white King.");
            }
            return;
        } else if (blackKing && getTeam() == Color.BLACK) {
            if (sel == null || (sel.type != Type.King && sel.color == Color.BLACK)) {
                JOptionPane.showMessageDialog(null, "There is already a black King.");
            }
            return;
        }

        Piece p = new Piece(Type.King, getTeam());
        setSelected(p);

        if (getTeam() == Color.BLACK) {
            blackKing = true;
        } else {
            whiteKing = true;
        }
    }

    /**
     * Display an information window on how to use CustomBoard.
     */
    private void showHelpMessage () {
        String message = "Hover over a tile with the mouse to select it.\n\n"
                + "Press the following keys to place the respective piece:\n"
                + "P - Pawn\n"
                + "R - Rook\n"
                + "N - Knight\n"
                + "B - Bishop\n"
                + "K - King\n"
                + "Q - Queen\n\n"
                + "Press T to toggle the team.\n"
                + "Press DELETE or BACKSPACE to remove a piece.\n"
                + "Press ENTER to finish building the custom board and to begin the game.\n\n"
                + "The player's turn will be set to the team that was toggled last with button T.\n"
                + "If the game mode is 'Player vs AI' then it will be white's turn irregardless.\n\n"
                + "By default all pieces that are placed hold the property that they have been moved.\n"
                + "For pieces that have been stationary the entire game, hit the M key.\n"
                + "This is useful for castling.\n\n"
                + "Press H to see this dialogue again.";
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Get the color of team of the piece that the user is placing.
     * @return      black or white
     */
    private Color getTeam () {
        if (teamWhite) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }

    /**
     * Get the currently selected piece.
     * @return      the currently selected piece (may be null)
     */
    private Piece getSelected () {
        return boardArray[selected.getY()][selected.getX()];
    }

    /**
     * Set the currently selected piece to a new piece.
     * @param p     the new piece to set the tile to.
     */
    private void setSelected (Piece p) {
        if (getSelected() != null) {
            Piece sel = getSelected();
            Type type = sel.type;
            Color color = sel.color;
            boolean selectedIsWhiteKing = (type == Type.King && color == Color.WHITE);
            boolean selectedIsBlackKing = (type == Type.King && color == Color.BLACK);
            if (selectedIsWhiteKing) whiteKing = false;
            else if (selectedIsBlackKing) blackKing = false;
        }
        boardArray[selected.getY()][selected.getX()] = p;
    }

    /**
     * Detects mouse movement for tile selecion.
     */
    private class MyMouseMotionAdapter extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            super.mouseMoved(mouseEvent);
            int x = mouseEvent.getX() / STEP;
            int y = mouseEvent.getY() / STEP;
            if (x < 8 && y < 8) {
                selected = new Position(x, y);
            }
            panel.repaint();
        }
    }

    /**
     * Contains a piece type and a team color.
     */
    private class Piece {
        Type type;
        Color color;
        boolean moved;
        private Piece (Type type, Color color) {
            this.type = type;
            this.color = color;
            this.moved = true;
        }
    }

    /**
     * The type of Chess piece.
     */
    private enum Type {
        Pawn,
        Rook,
        Knight,
        Bishop,
        King,
        Queen
    }

    /**
     * Display a dialogue where the user can create a custom Chess board.
     * @return      the custom board
     */
    public static Board createBoard() {
        new CustomBoard();
        running = true;
        while (running) {
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return board;
    }

}
