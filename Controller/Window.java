package Controller;

import AI.*;
import Chess.Board;
import Chess.Team;
import CustomBoard.CustomBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Allows the user to see the game of controller in a GUI and
 * to interact with it using the mouse and keys.
 */
public class Window extends JFrame {

    private static final int WIDTH = (int)(1207*0.50);
    private static final int HEIGHT = (int)(1295*0.50) - 42;
    private JPanel panel;
    private Controller controller;
    private static boolean playerVersusPlayer = false;
    private static int ply = 1;

    /**
     * Construct the Window with a default Chess board.
     */
    private Window () {
        controller = new Controller();
        loadPanel();
        setWindowProperties();
    }

    /**
     * Construct the Window with a customized Chess board.
     */
    private Window (Board board) {
        controller = new Controller(board);
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
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addListeners(cp);
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
        setTitle("Chess");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Adds the Key, Mouse, and Mouse Motion listeners.
     * @param cp    the container to add the listeners to
     */
    private void addListeners (Container cp) {
        cp.addMouseMotionListener(new MyMouseMotionAdapter());
        cp.addMouseListener(new MyMouseAdapter());
        cp.addKeyListener(new MyKeyAdapter());
        cp.setFocusable(true);
        cp.requestFocus();
    }

    /**
     * The panel that the controller game will be drawn to.
     */
    class Panel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            controller.paint((Graphics2D)graphics);
        }
    }

    /**
     * Mouse motion detection for hovering over and highlighting controller pieces.
     */
    private class MyMouseMotionAdapter extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            super.mouseMoved(mouseEvent);
            controller.hover(mouseEvent.getX(), mouseEvent.getY());
            panel.repaint();
        }
    }

    /**
     * Mouse button detection for selecting and moving controller pieces.
     */
    private class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            super.mousePressed(mouseEvent);

            if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
                controller.leftClick(mouseEvent.getX(), mouseEvent.getY());
            } else {
                controller.rightClick(mouseEvent.getX(), mouseEvent.getY());
            }

            panel.paintImmediately(0, 0, WIDTH, HEIGHT);
            letAIMakeMove();
        }
    }

    /**
     * Key detection for changing settings in the game.
     */
    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent keyEvent) {
            super.keyPressed(keyEvent);

            // Visual changes.
            if (keyEvent.getKeyCode() == KeyEvent.VK_F1) {
                controller.toggleTransparency();
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_F2) {
                controller.toggleShowMarker();
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_F3) {
                controller.toggleShowMoves();
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_F4) {
                controller.toggleShowAllMoves();
            }

            // IO.
            if (keyEvent.getKeyCode() == KeyEvent.VK_L) {
                controller.load();
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_S) {
                controller.save();
            }

            panel.paintImmediately(0, 0, WIDTH, HEIGHT);
            letAIMakeMove();
        }
    }

    /**
     * Allow AI to make a move if the game is not over and if 'Player vs AI' mode is enabled.
     */
    private void letAIMakeMove () {
        if (!playerVersusPlayer
                && !controller.board.isGameOver()
                && controller.board.getTurn() == Team.BLACK) {
            AlphaBetaPruning.playMove(Team.BLACK, controller.board, ply);

            panel.paintImmediately(0, 0, WIDTH, HEIGHT);

            if (controller.board.isGameOver()) {
                controller.board.declareWinner();
            }
        }
    }

    /**
     * Print how to use the command line parameters.
     */
    private static void printUsage() {
        System.out.println("\nUsage:");
        System.out.println("  java Controller.Window [mode] [ply] [custom]");
        System.out.println("\nParameters:");
        System.out.println("  [mode]     'pvp' for Player vs. Player.");
        System.out.println("             'avp' for Player vs. AI.");
        System.out.println("  [ply]      Numerical value for the AI's ply.");
        System.out.println("             This value is ignored for pvp mode.");
        System.out.println("  [custom]   'true' to create a custom board.");
        System.out.println("             'false' to go with the default board.");
        System.out.println("\nExamples:");
        System.out.println("  java Controller.Window avp 4 false");
        System.out.println("  java Controller.Window pvp 0 true");
        System.out.println();
    }

    /**
     * The start of the program.
     * @param args  command line arguments
     */
    public static void main(String[] args) {

        if (args.length == 3) {
            if (args[0].equals("pvp"))
                playerVersusPlayer = true;
            else if (args[0].equals("avp"))
                playerVersusPlayer = false;
            else {
                printUsage();
                System.exit(0);
            }
            try{
                ply = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                printUsage();
                System.exit(0);
            }
            if (args[2].equals("true")) {
                Board customBoard = CustomBoard.createBoard();
                if (!playerVersusPlayer)
                    customBoard.setTurn(Team.WHITE);
                SwingUtilities.invokeLater(() -> new Window(customBoard));
            } else if (args[2].equals("false"))
                SwingUtilities.invokeLater(() -> new Window());
            else {
                printUsage();
                System.exit(0);
            }
        } else {
            printUsage();
            System.exit(0);
        }

        String mode = (playerVersusPlayer) ? "Player vs. Player" : "Player vs. AI";
        System.out.println("Mode:   " + mode);
        System.out.println("Ply:    " + ply);

    }

}
