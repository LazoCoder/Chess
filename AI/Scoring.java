package AI;

import Chess.Board;
import Chess.Pieces.*;
import Chess.Position;
import Chess.Team;

class Scoring {

    private static final int PAWN_VALUE     =    100;
    private static final int KNIGHT_VALUE   =    320;
    private static final int BISHOP_VALUE   =    330;
    private static final int ROOK_VALUE     =    500;
    private static final int QUEEN_VALUE    =    900;
    private static final int KING_VALUE     = 20_000;

    private static int[][] PawnTable = new int[][] {
        {  0,  0,  0,  0,  0,  0,  0,  0 },
        { 50, 50, 50, 50, 50, 50, 50, 50 },
        { 10, 10, 20, 30, 30, 20, 10, 10 },
        {  5,  5, 10, 25, 25, 10,  5,  5 },
        {  0,  0,  0, 20, 20,  0,  0,  0 },
        {  5, -5,-10,  0,  0,-10, -5,  5 },
        {  5, 10, 10,-20,-20, 10, 10,  5 },
        {  0,  0,  0,  0,  0,  0,  0,  0 }
    };

    private static int[][] KnightTable = new int[][] {
        { -50,-40,-30,-30,-30,-30,-40,-50 },
        { -40,-20,  0,  0,  0,  0,-20,-40 },
        { -30,  0, 10, 15, 15, 10,  0,-30 },
        { -30,  5, 15, 20, 20, 15,  5,-30 },
        { -30,  0, 15, 20, 20, 15,  0,-30 },
        { -30,  5, 10, 15, 15, 10,  5,-30 },
        { -40,-20,  0,  5,  5,  0,-20,-40 },
        { -50,-40,-30,-30,-30,-30,-40,-50 }
    };

    private static int[][] BishopTable = new int[][] {
        { -20,-10,-10,-10,-10,-10,-10,-20 },
        { -10,  0,  0,  0,  0,  0,  0,-10 },
        { -10,  0,  5, 10, 10,  5,  0,-10 },
        { -10,  5,  5, 10, 10,  5,  5,-10 },
        { -10,  0, 10, 10, 10, 10,  0,-10 },
        { -10, 10, 10, 10, 10, 10, 10,-10 },
        { -10,  5,  0,  0,  0,  0,  5,-10 },
        { -20,-10,-10,-10,-10,-10,-10,-20 }
    };

    private static int[][] RookTable = new int[][] {
        {  0,  0,  0,  0,  0,  0,  0,  0 },
        {  5, 10, 10, 10, 10, 10, 10,  5 },
        { -5,  0,  0,  0,  0,  0,  0, -5 },
        { -5,  0,  0,  0,  0,  0,  0, -5 },
        { -5,  0,  0,  0,  0,  0,  0, -5 },
        { -5,  0,  0,  0,  0,  0,  0, -5 },
        { -5,  0,  0,  0,  0,  0,  0, -5 },
        {  0,  0,  0,  5,  5,  0,  0,  0 }
    };

    private static int[][] QueenTable = new int[][] {
        { -20,-10,-10, -5, -5,-10,-10,-20 },
        { -10,  0,  0,  0,  0,  0,  0,-10 },
        { -10,  0,  5,  5,  5,  5,  0,-10 },
        {  -5,  0,  5,  5,  5,  5,  0, -5 },
        {   0,  0,  5,  5,  5,  5,  0, -5 },
        { -10,  5,  5,  5,  5,  5,  0,-10 },
        { -10,  0,  5,  0,  0,  0,  0,-10 },
        { -20,-10,-10, -5, -5,-10,-10,-20 }
    };

    private static int[][] KingTableMiddleGame = new int[][] {
        { -30,-40,-40,-50,-50,-40,-40,-30 },
        { -30,-40,-40,-50,-50,-40,-40,-30 },
        { -30,-40,-40,-50,-50,-40,-40,-30 },
        { -30,-40,-40,-50,-50,-40,-40,-30 },
        { -20,-30,-30,-40,-40,-30,-30,-20 },
        { -10,-20,-20,-20,-20,-20,-20,-10 },
        {  20, 20,  0,  0,  0,  0, 20, 20 },
        {  20, 30, 10,  0,  0, 10, 30, 20 }
    };

    private static int[][] KingTableEndGame = new int[][] {
        { -50,-40,-30,-20,-20,-30,-40,-50 },
        { -30,-20,-10,  0,  0,-10,-20,-30 },
        { -30,-10, 20, 30, 30, 20,-10,-30 },
        { -30,-10, 30, 40, 40, 30,-10,-30 },
        { -30,-10, 30, 40, 40, 30,-10,-30 },
        { -30,-10, 20, 30, 30, 20,-10,-30 },
        { -30,-30,  0,  0,  0,  0,-30,-30 },
        { -50,-30,-30,-30,-30,-30,-30,-50 }
    };

    /**
     * Get the intrinsic value of a piece.
     * @param piece     the piece to evaluate
     * @return          the score of the piece
     */
    private static int valueOfPiece (Piece piece) {
        if (piece instanceof Pawn)          return PAWN_VALUE;
        else if (piece instanceof Knight)   return KNIGHT_VALUE;
        else if (piece instanceof Bishop)   return BISHOP_VALUE;
        else if (piece instanceof Rook)     return ROOK_VALUE;
        else if (piece instanceof  Queen)   return QUEEN_VALUE;
        else                                return KING_VALUE;
    }

    private static int valueOfPosition (Board board, Position position) {

        Piece piece = board.get(position);

        int x = position.getX();
        int y = (piece.getTeam() == Team.WHITE) ? (position.getY()) : (7 - position.getY());

        if (piece instanceof Pawn) {
            return PawnTable[y][x];
        }
        else if (piece instanceof Knight) {
            return KnightTable[y][x];
        }
        else if (piece instanceof Bishop) {
            return BishopTable[y][x];
        }
        else if (piece instanceof Rook) {
            return RookTable[y][x];
        }
        else if (piece instanceof  Queen) {
            return QueenTable[y][x];
        }
        else if (endGame(board)) {
            return KingTableEndGame[y][x];
        } else {
            return KingTableMiddleGame[y][x];
        }

    }

    private static boolean endGame (Board board) {

        final int endGameScore = KING_VALUE + QUEEN_VALUE + ROOK_VALUE;

        boolean endWhite = hasQueen(Team.WHITE, board);
        endWhite = endWhite && teamScore(Team.WHITE, board) <= endGameScore;

        boolean endBlack = hasQueen(Team.WHITE, board);
        endBlack = endBlack && teamScore(Team.BLACK, board) <= endGameScore;

        return noQueensLeft(board) || (endWhite && endBlack);
    }

    private static boolean hasQueen (Team team, Board board) {
        for (Position piece : board) {
            if (board.get(piece).getTeam() == team && board.get(piece) instanceof Queen) {
                    return true;
            }
        }
        return false;
    }

    private static boolean noQueensLeft (Board board) {
        for (Position piece : board) {
            if (board.get(piece) instanceof Queen) {
                return false;
            }
        }
        return true;
    }

    private static int teamScore (Team team, Board board) {
        int score = 0;
        for (Position piece : board) {
            if (board.get(piece).getTeam() == team) {
                score += valueOfPiece(board.get(piece));
            }
        }
        return score;
    }


    private static int findScore (Team team, Board board, int currentPly) {
        int score = 0;

        for (Position p : board) {
            if (board.get(p).getTeam() == team) {
                score += valueOfPiece(board.get(p));
                score += valueOfPosition(board, p);
                score -= currentPly; // Quicker win or slower loss is preferred.
                score += new java.util.Random().nextInt(4); // Element of randomness.
            } else {
                score -= valueOfPiece(board.get(p));
                score -= valueOfPosition(board, p);
                score += currentPly; // Quicker win or slower loss is preferred.
                score -= new java.util.Random().nextInt(4); // Element of randomness.
            }
        }

        return score;
    }

    static int score (Team team, Board board, int currentPly) {
        Team opponent = (team == Team.BLACK) ? Team.WHITE : Team.BLACK;

        if (board.isGameOver() && board.getWinner() == team) {
            return (int)Double.POSITIVE_INFINITY;
        } else if (board.isGameOver() && board.getWinner() == opponent) {
            return (int)Double.NEGATIVE_INFINITY;
        } else if (board.isGameOver() && board.getWinner() == null) { // Draw.
            return 0;
        } else {
            return Scoring.findScore(team, board, currentPly);
        }
    }

}
