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

    private final ChessBoard board = new ChessBoard();
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

    private void handleCellClick(int row, int col) {
        String piece = board.getPiece(row, col);

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
        String moving = board.getPiece(selectedRow, selectedCol);
        String target = board.movePiece(selectedRow, selectedCol, row, col);

        selectedRow = -1;
        selectedCol = -1;

        if (target != null && target.endsWith("K")) {
            showGameOver(moving.charAt(0), "King captured.");
            board.reset();
        } else if (board.isKingCapturable(moving.charAt(0))) {
            showGameOver(moving.charAt(0), "Checkmate (simplified). King can be captured next move.");
            board.reset();
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
                String piece = board.getPiece(row, col);

                cell.setText(PieceSymbols.fromPiece(piece));
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
