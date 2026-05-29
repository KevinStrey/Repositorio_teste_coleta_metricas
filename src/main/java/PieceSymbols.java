public final class PieceSymbols {
    private PieceSymbols() {
    }

    public static String fromPiece(String piece) {
        if (piece == null || piece.length() < 2) {
            return "";
        }

        char color = piece.charAt(0);
        char type = piece.charAt(1);

        if (color == 'w') {
            return switch (type) {
                case 'K' -> "♔";
                case 'Q' -> "♕";
                case 'R' -> "♖";
                case 'B' -> "♗";
                case 'N' -> "♘";
                case 'P' -> "♙";
                default -> "";
            };
        }

        return switch (type) {
            case 'K' -> "♚";
            case 'Q' -> "♛";
            case 'R' -> "♜";
            case 'B' -> "♝";
            case 'N' -> "♞";
            case 'P' -> "♟";
            default -> "";
        };
    }
}
