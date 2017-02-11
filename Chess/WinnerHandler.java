package Chess;

import Chess.Pieces.Piece;

import javax.swing.*;
import java.util.HashMap;
import java.util.Stack;

/**
 * For handling everything related to winning. This includes detecting
 * checkmates, stalemates and displaying the winner to the screen.
 */
class WinnerHandler {

    private Board board;

    /**
     * Construct the WinnerHandler.
     * @param board     a reference to the board where the handling will occur
     */
    WinnerHandler (Board board) {
        this.board = board;
    }

    /**
     * Check to see if a piece can move to a position without getting
     * a check on it's own team's King.
     * @param position      the starting position of the piece
     * @param destination   the position the piece would move to
     * @return              true if there would be a wouldBeCheck
     */
    boolean wouldBeCheck (Position position, Position destination) {

        Piece temp = null;
        Piece piece = board.get(position);
        boolean result;

        if (!board.isEmptySpot(destination)) {
            temp = board.get(destination).clone(board);
        }

        move(piece, position, destination);
        Team team = piece.getTeam();
        result = !board.safeSpot(team, board.getKing(team));
        move(piece, destination, position);

        if (temp != null) {
            board.add(temp, destination);
        }

        return result;
    }

    /**
     * Check to see if a piece can move to a position without getting checkmated.
     * @param position      the starting position of the piece
     * @param destination   the position the piece would move to
     * @return              true if there would be a checkmate
     */
    private boolean wouldBeCheckmate (Position position, Position destination) {
        Piece temp = null;
        Piece piece = board.get(position);
        boolean result;

        if (!board.isEmptySpot(destination)) {
            temp = board.get(destination).clone(board);
        }

        move(piece, position, destination);
        result = piece.getAllPossibleMoves(destination).size() == 0;
        move(piece, destination, position);

        if (temp != null) {
            board.add(temp, destination);
        }

        return result;
    }

    /**
     * Move the piece without triggering any reciprocal functions (this is what
     * would happen if the move(...) method from game board is called).
     * @param piece         the piece to move
     * @param position      the position of the piece
     * @param destination   the destination of the piece
     */
    private void move (Piece piece, Position position, Position destination) {
        board.add(piece, destination);
        board.delete(position);
    }

    /**
     * Check to see if it is currently a stalemate.
     * @param team      the team to check for a stalemate on
     * @return          true if there is a stalemate on the specified team
     */
    private boolean isStalemate (Team team) {
        for (Position position : board) {
            if (board.get(position).getTeam() != team) {
                continue;
            }
            for (Position pos : board.get(position).getAllPossibleMoves(position)) {
                if (!wouldBeCheckmate(position, pos)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check to see if there is a checkmate.
     * @param team  the team to check if there is a checkmate on
     * @return      return true if there is a checkmate
     */
    private boolean isCheckmate(Team team) {
        for (Position position : board) {
            if (board.get(position).getTeam().equals(team)) {
                if (board.get(position).getAllPossibleMoves(position).size() != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isDraw () {
        boolean onePieceLeft = onePieceLeft();
        boolean tripleTurn = tripleMove(Team.WHITE) && tripleMove(Team.BLACK);
        return onePieceLeft || tripleTurn;
    }

    /**
     * Check to see if there is only one piece left on either teams.
     * @return      true if only two pieces are left on the board
     */
    private boolean onePieceLeft () {
        Stack<Position> whitePieces = new Stack<>();
        for (Position p : board) {
            if (board.get(p).getTeam().equals(Team.WHITE)) {
                whitePieces.push(p);
            }
        }

        Stack<Position> blackPieces = new Stack<>();
        for (Position p : board) {
            if (board.get(p).getTeam().equals(Team.BLACK)) {
                blackPieces.push(p);
            }
        }

        return blackPieces.size() == 1 && whitePieces.size() == 1;
    }

    /**
     * Check to see if a move was made 3 times.
     * @return      true if a move was made 3 times
     */
    private boolean tripleMove (Team team) {
        HashMap<Move, Integer> hashMap = new HashMap<>();
        for (Move m : board.movementHandler.moves) {
            if (m.getPiece1().getTeam() != team) continue;
            if (!hashMap.containsKey(m)) {
                hashMap.put(m, 1);
            }  else {
                int num = hashMap.get(m);
                hashMap.put(m, ++num);
            }
            if (hashMap.get(m) == 3) {
                return true;
            }
        }
        return false;
    }

    boolean checkIfGameIsOver () {

        if (isCheckmate(Team.WHITE)) {
            board.setWinner(Team.BLACK);
            return true;
        } else if (isCheckmate(Team.BLACK)) {
            board.setWinner(Team.WHITE);
            return true;
        } else if (isStalemate(Team.WHITE)) {
            board.setWinner(Team.BLACK);
            return true;
        } else if (isStalemate(Team.BLACK)) {
            board.setWinner(Team.WHITE);
            return true;
        } else if (isDraw()) {
            board.setWinner(null);
            return true;
        }

        return false;
    }

    void declareWinner () {
        if (!board.isGameOver()) {
            throw new IllegalStateException("Cannot declare winner if game is not over.");
        }

        String msg;

        if (board.getWinner() == null) {
            msg = "The game is a draw.";
        } else if (board.getWinner() == Team.WHITE) {
            msg = "White wins!";
        } else {
            msg = "Black wins!";
        }

        MessageBox msgBox = new MessageBox(msg);
        Thread th = new Thread(msgBox);
        th.start();
    }

    /**
     * Pop up window that shows who won the game. This needs to be run
     * in another thread, otherwise the window pops up before the final
     * move is painted to the screen.
     */
    private class MessageBox implements Runnable {

        private String message;

        /**
         * Constructs the Window.
         * @param message   the message to display
         */
        MessageBox (String message) {
            this.message = message;
        }

        @Override
        public void run() {
            JOptionPane.showMessageDialog(null, message);
        }
    }

}
