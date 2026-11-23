import java.util.ArrayList;
import java.util.List;

/** Board: stores the game grid in a 2D char array. Works for Tic Tac Toe and Connect 4. */
public class Board {
    public static final char EMPTY = ' ';

    private final char[][] grid;
    private final int rows, cols, winLength;
    private final boolean gravity;          // true = Connect 4 (pieces fall down a column)

    public Board(int rows, int cols, int winLength, boolean gravity) {
        this.rows = rows;
        this.cols = cols;
        this.winLength = winLength;
        this.gravity = gravity;
        this.grid = new char[rows][cols];
        for (char[] row : grid) java.util.Arrays.fill(row, EMPTY);
    }

    public int getRows()      { return rows; }
    public int getCols()      { return cols; }
    public int getWinLength() { return winLength; }
    public boolean isGravity(){ return gravity; }
    public char get(int r, int c) { return grid[r][c]; }

    /** Column of a move: Connect 4 move = column, Tic Tac Toe move = cell index 0..8. */
    public int colOf(int move) { return gravity ? move : move % cols; }

    /** True if the move (0-based) is on the board and the cell / column is not full. */
    public boolean isValidMove(int move) {
        if (gravity) return move >= 0 && move < cols && grid[0][move] == EMPTY;
        return move >= 0 && move < rows * cols && grid[move / cols][move % cols] == EMPTY;
    }

    /** Places a piece and returns the row it landed in (-1 if invalid). O(1) / O(R). */
    public int placeMove(int move, char player) {
        if (!isValidMove(move)) return -1;
        if (gravity) {
            for (int r = rows - 1; r >= 0; r--) {      // piece falls to the lowest empty row
                if (grid[r][move] == EMPTY) { grid[r][move] = player; return r; }
            }
            return -1;
        }
        grid[move / cols][move % cols] = player;
        return move / cols;
    }

    /** Removes the piece at (r, c). Used by the undo Stack and by Minimax backtracking. */
    public void undoMove(int r, int c) { grid[r][c] = EMPTY; }

    /** All legal moves; Connect 4 columns are ordered centre-first so pruning works better. */
    public List<Integer> validMoves() {
        List<Integer> moves = new ArrayList<>();
        int n = gravity ? cols : rows * cols;
        for (int i = 0; i < n; i++) {
            int m = gravity ? (cols / 2 + ((i % 2 == 0) ? 1 : -1) * ((i + 1) / 2)) : i;
            if (isValidMove(m)) moves.add(m);
        }
        return moves;
    }

    /** Checks rows, columns and both diagonals for winLength connected pieces. O(R x C). */
    public boolean checkWin(char p) {
        int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] != p) continue;
                for (int[] d : dirs) {
                    int k = 1;
                    while (k < winLength) {
                        int nr = r + d[0] * k, nc = c + d[1] * k;
                        if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) break;
                        if (grid[nr][nc] != p) break;
                        k++;
                    }
                    if (k == winLength) return true;
                }
            }
        }
        return false;
    }

    /** Draw detection: no empty cell is left. */
    public boolean isFull() {
        for (int c = 0; c < cols; c++)
            for (int r = 0; r < rows; r++)
                if (grid[r][c] == EMPTY) return false;
        return true;
    }

    public void display() {
        System.out.println();
        if (gravity) {
            for (int c = 1; c <= cols; c++) System.out.print("  " + c + " ");
            System.out.println();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) System.out.print("| " + grid[r][c] + " ");
                System.out.println("|");
            }
            System.out.println("+" + "---+".repeat(cols));
        } else {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    char ch = grid[r][c];
                    String cell = (ch == EMPTY) ? String.valueOf(r * cols + c + 1)
                                                : String.valueOf(ch);
                    System.out.print(" " + cell + " ");
                    if (c < cols - 1) System.out.print("|");
                }
                System.out.println();
                if (r < rows - 1) System.out.println("---+---+---");
            }
        }
        System.out.println();
    }
}