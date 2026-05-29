import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
    private static final int SIZE = 8;

    private final String[][] board = new String[SIZE][SIZE];
    private final Button[][] cells = new Button[SIZE][SIZE];
    private final Label turnLabel = new Label();

    private int selectedRow = -1;
    private int selectedCol = -1;
    private char currentPlayer = 'w';

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        initBoard();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));

        GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setPadding(new Insets(8));

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Button cell = new Button();
                cell.setMinSize(60, 60);
                cell.setMaxSize(60, 60);
                cell.setFocusTraversable(false);
                cell.setFont(Font.font("DejaVu Sans", 24));

                int r = row;
                int c = col;
                cell.setOnAction(event -> handleCellClick(r, c));

                cells[row][col] = cell;
                grid.add(cell, col, row);
            }
        }

        HBox header = new HBox(turnLabel);
        header.setPadding(new Insets(0, 0, 8, 0));

        root.setTop(header);
        root.setCenter(grid);

        updateTurnLabel();
        renderBoard();

        Scene scene = new Scene(root);
        stage.setTitle("Chess (Local Two-Player)");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void initBoard() {
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

    private void handleCellClick(int row, int col) {
        String piece = board[row][col];

        if (selectedRow == -1) {
            if (piece != null && piece.charAt(0) == currentPlayer) {
                selectedRow = row;
                selectedCol = col;
                renderBoard();
            }
            return;
        }

        if (row == selectedRow && col == selectedCol) {
            selectedRow = -1;
            selectedCol = -1;
            renderBoard();
            return;
        }

        if (piece != null && piece.charAt(0) == currentPlayer) {
            selectedRow = row;
            selectedCol = col;
            renderBoard();
            return;
        }

        moveSelectedPieceTo(row, col);
    }

    private void moveSelectedPieceTo(int row, int col) {
        String moving = board[selectedRow][selectedCol];
        String target = board[row][col];

        board[row][col] = moving;
        board[selectedRow][selectedCol] = null;

        selectedRow = -1;
        selectedCol = -1;

        if (target != null && target.endsWith("K")) {
            showGameOver(moving.charAt(0), "King captured.");
            initBoard();
        } else if (isKingCapturable(moving.charAt(0))) {
            showGameOver(moving.charAt(0), "Checkmate (simplified). King can be captured next move.");
            initBoard();
        } else {
            currentPlayer = (currentPlayer == 'w') ? 'b' : 'w';
        }

        updateTurnLabel();
        renderBoard();
    }

    private void renderBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Button cell = cells[row][col];
                String piece = board[row][col];

                cell.setText(pieceToSymbol(piece));
                cell.setTextFill(piece != null && piece.charAt(0) == 'w' ? Color.BLACK : Color.DARKRED);

                boolean isLight = (row + col) % 2 == 0;
                String base = isLight ? "#f0d9b5" : "#b58863";
                String border = (row == selectedRow && col == selectedCol)
                        ? "-fx-border-color: #1e90ff; -fx-border-width: 3px;"
                        : "-fx-border-color: transparent; -fx-border-width: 3px;";
                cell.setStyle("-fx-background-color: " + base + ";" + border);
            }
        }
    }

    private void updateTurnLabel() {
        String player = currentPlayer == 'w' ? "White" : "Black";
        turnLabel.setText("Turn: " + player);
    }

    private boolean isKingCapturable(char attacker) {
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

    private String pieceToSymbol(String piece) {
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

    private void showGameOver(char winner, String reason) {
        String player = winner == 'w' ? "White" : "Black";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Game Over");
        alert.setContentText(player + " wins. " + reason);
        alert.showAndWait();

        currentPlayer = 'w';
        selectedRow = -1;
        selectedCol = -1;
    }
}
