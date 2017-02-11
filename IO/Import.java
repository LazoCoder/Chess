package IO;

import Chess.Board;
import Chess.Pieces.*;
import Chess.Position;
import Chess.Team;

import javax.swing.*;
import java.io.File;

/**
 * For loading a game of chess.
 */
public class Import {

    /**
     * Loads a .chess file of the user's choosing and then
     * transcribes it into a Board object.
     * @param board     the game board to load the .chess file to
     */
    public static void load (Board board) {

        File file;

        try {
            file = Utility.getFile();
        } catch (Exception ex) {
            return;
        }

        if (!isFileValid(file)) {
            errorPopUpWindow();
            return;
        }

        board.clear();
        String s = Utility.read(file);
        loadToGame(board, s);

    }

    /**
     * Converts the content of the .chess file to a Board object.
     * @param board     the game board object to load to
     * @param s             the string containing the details of the saved chess game
     */
    private static void loadToGame (Board board, String s) {

        String[] lines = s.split("\n");

        String[] line;
        int x, y;

        for (int i = 1; i < lines.length; i++) {

            line = lines[i].split(",");

            Team team;

            if (line[0].equals("BLACK"))
                team = Team.BLACK;
            else
                team = Team.WHITE;

            x = Integer.parseInt(line[2]);
            y = Integer.parseInt(line[3]);

            String type = line[1];

            if (type.equals("Rook"))
                board.add(new Rook(board, team), new Position(x, y));
            else if (type.equals("Knight"))
                board.add(new Knight(board, team), new Position(x, y));
            else if (type.equals("Bishop"))
                board.add(new Bishop(board, team), new Position(x, y));
            else if (type.equals("Queen"))
                board.add(new Queen(board, team), new Position(x, y));
            else if (type.equals("King"))
                board.add(new King(board, team), new Position(x, y));
            else if (type.equals("Pawn") && team.equals(Team.BLACK))
                board.add(new Pawn(board, team, false), new Position(x, y));
            else if (type.equals("Pawn") && team.equals(Team.WHITE))
                board.add(new Pawn(board, team, true), new Position(x, y));
            else {
                errorPopUpWindow();
                System.exit(-1);
            }

            boolean moved = Boolean.parseBoolean(line[4]);

            if (moved)
                board.get(new Position(x, y)).markAsMoved();

//            if (type.equals("Pawn") && line[5].equals("true")) {
//                ((Pawn)board.get(new Position(x, y))).markAsMovedTwoSquares();
//            }

        }

        line = lines[0].split(",");

        if (line[0].equals("BLACK"))
            board.setTurn(Team.BLACK);
        else
            board.setTurn(Team.WHITE);

        if (line[1].equals("null"))
            return;

        x = Integer.parseInt(line[1]);
        y = Integer.parseInt(line[2]);

        board.setEnPassant(new Position(x, y));

    }

    /**
     * Checks to see if the extension of the file is .chess.
     * @param file      the file to wouldBeCheck
     * @return          true if the extension is .chess
     */
    private static boolean isFileValid (File file) {
        try {

            String name = file.getName();

            if (name.length() < 7)
                return false;

            String extension = name.substring(name.length()-5, name.length());

            if (!extension.equals("chess"))
                return false;

        } catch (Exception ex) {}
        return true;
    }

    /**
     * Displays an error message.
     * This is for when the user selects a file with the wrong
     * extension.
     */
    private static void errorPopUpWindow () {
        JOptionPane.showMessageDialog(null, "Invalid file.");
    }

}
