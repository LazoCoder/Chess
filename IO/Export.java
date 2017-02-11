package IO;

import Chess.Board;
import Chess.Position;

import javax.swing.*;
import java.io.File;
import java.util.Random;

/**
 * For saving a game of chess.
 */
public class Export {

    /**
     * Converts the game board to a String containing all the positions
     * of all the pieces and the details of the board, then saves the
     * String to a file on the hard disk.
     * @param board     the game board to save
     */
    public static void save (Board board) {

        File file;

        try {
            file = Utility.getDirectory();
        } catch (Exception ex) {
            return;
        }

        if (!file.exists() || !file.isDirectory()) {
            JOptionPane.showMessageDialog(null, "Folder: " + file.toString()
                    + " does not exist.");
            return;
        }

        String s = convertGameBoardToString(board);

        Random r = new Random();
        Utility.write(file, "chess_" + r.nextInt(9999) + ".chess", s);
    }

    /**
     * Converts the game board and all its details to a String representation.
     * @param board     the game board to convert
     * @return              the String representation of the game board
     */
    private static String convertGameBoardToString (Board board) {
        StringBuilder sb = new StringBuilder();

        sb.append(board.getTurn());
        sb.append(",");

        if (board.getEnPassant() != null) {
            sb.append(board.getEnPassant().getX());
        } else {
            sb.append("null");
        }

        sb.append(",");

        if (board.getEnPassant() != null)
            sb.append(board.getEnPassant().getY());
        else
            sb.append("null");

        sb.append("\n");

        appendPieces(board, sb);

        return new String(sb);
    }

    /**
     * Turns each piece into a String and appends it to a StringBuilder.
     * @param board     the game board of the pieces which will be appended
     * @param sb            the StringBuilder which the pieces will be appended to
     */
    private static void appendPieces (Board board, StringBuilder sb) {
        for (Position p : board) {
            sb.append(board.get(p).getTeam());
            sb.append(",");
            sb.append(board.get(p).getClass().getSimpleName());
            sb.append(",");
            sb.append(p.getX());
            sb.append(",");
            sb.append(p.getY());
            sb.append(",");
            sb.append(board.get(p).hasMoved());

//            if (board.get(p) instanceof Pawn) {
//                sb.append(",");
//                sb.append(((Pawn) board.get(p)).hasMovedTwoSquares());
//            }

            sb.append("\n");
        }
    }

}
