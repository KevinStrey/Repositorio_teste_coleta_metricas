public class ChessBoard {
    private static final int SIZE = 8;

    private final String[][] board = new String[SIZE][SIZE];

    public ChessBoard() {
        reset();
    }

    public void reset() {
        String[] backRank = {"R", "N", "B", "Q", "K", "B", "N", "R"};

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = null;
            }
        }

        for (int col = 0; col < SIZE; col++) {
            board[0][col] = "b" + backRank[col];
            board[1][col] = "bP";
            board[6][col] = "wP";
            board[7][col] = "w" + backRank[col];
        }
    }

    public String getPiece(int row, int col) {
        return board[row][col];
    }

    public String movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        String moving = board[fromRow][fromCol];
        String target = board[toRow][toCol];

        board[toRow][toCol] = moving;
        board[fromRow][fromCol] = null;

        return target;
    }

    public boolean isKingCapturable(char attacker) {
        char defender = attacker == 'w' ? 'b' : 'w';
        int[] kingPos = findKing(defender);
        if (kingPos == null) {
            return false;
        }
        return isSquareUnderAttack(attacker, kingPos[0], kingPos[1]);
    }

    private int[] findKing(char color) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String piece = board[row][col];
                if (piece != null && piece.charAt(0) == color && piece.charAt(1) == 'K') {
                    return new int[] {row, col};
                }
            }
        }
        return null;
    }

    private boolean isSquareUnderAttack(char attacker, int row, int col) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                String piece = board[r][c];
                if (piece != null && piece.charAt(0) == attacker) {
                    if (canAttack(piece, r, c, row, col)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean canAttack(String piece, int fromRow, int fromCol, int toRow, int toCol) {
        char color = piece.charAt(0);
        char type = piece.charAt(1);
        int dr = toRow - fromRow;
        int dc = toCol - fromCol;

        switch (type) {
            case 'P': {
                int direction = color == 'w' ? -1 : 1;
                return dr == direction && Math.abs(dc) == 1;
            }
            case 'N':
                return (Math.abs(dr) == 2 && Math.abs(dc) == 1)
                        || (Math.abs(dr) == 1 && Math.abs(dc) == 2);
            case 'B':
                return Math.abs(dr) == Math.abs(dc) && isPathClear(fromRow, fromCol, toRow, toCol);
            case 'R':
                return (dr == 0 || dc == 0) && isPathClear(fromRow, fromCol, toRow, toCol);
            case 'Q':
                return ((dr == 0 || dc == 0) || (Math.abs(dr) == Math.abs(dc)))
                        && isPathClear(fromRow, fromCol, toRow, toCol);
            case 'K':
                return Math.abs(dr) <= 1 && Math.abs(dc) <= 1;
            default:
                return false;
        }
    }

    private boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol) {
        int stepRow = Integer.compare(toRow, fromRow);
        int stepCol = Integer.compare(toCol, fromCol);
        int r = fromRow + stepRow;
        int c = fromCol + stepCol;

        while (r != toRow || c != toCol) {
            if (board[r][c] != null) {
                return false;
            }
            r += stepRow;
            c += stepCol;
        }

        return true;
    }
}
