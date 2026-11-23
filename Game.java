import java.io.*;
import java.util.Scanner;
import java.util.Stack;

/** Game: controls turns, the undo Stack, difficulty level and scoreboard file handling. */
public class Game {
    public static final char HUMAN = 'X', COMPUTER = 'O';
    private static final String FILE_NAME = "scoreboard.txt";
    private static final String[] NAMES = {"TicTacToe", "Connect4"};
    private static final int[][] scores = new int[2][3];        // [game][wins, losses, draws]

    private final Board board;
    private final AI ai;
    private final Stack<int[]> moveStack = new Stack<>();       // each entry = {row, column}
    private final boolean connect4;
    private final int gameIndex;
    private final Scanner in;
    private char currentPlayer = HUMAN;

    public Game(int gameIndex, int difficulty, Scanner in) {
        this.gameIndex = gameIndex;
        this.connect4 = (gameIndex == 1);
        this.in = in;
        this.board = connect4 ? new Board(6, 7, 4, true) : new Board(3, 3, 3, false);
        this.ai = new AI(COMPUTER, HUMAN);
        this.ai.setDifficulty(difficulty, connect4);
    }

    /** Plays one game. Returns 1 = human win, -1 = computer win, 0 = draw, 2 = quit. */
    public int play() {
        System.out.println("\nYou are X, computer is O. Type "
                + (connect4 ? "a column (1-7)" : "a cell (1-9)") + ", U to undo, Q to quit.");
        while (true) {
            board.display();
            if (currentPlayer == HUMAN) {
                String s = readLine("Your move: ");
                if (s == null || s.equalsIgnoreCase("Q")) return 2;
                if (s.equalsIgnoreCase("U")) { undoLastMove(); continue; }
                if (!humanMove(s)) continue;
            } else {
                computerMove();
            }
            if (board.checkWin(currentPlayer)) {
                board.display();
                return currentPlayer == HUMAN ? 1 : -1;
            }
            if (board.isFull()) { board.display(); return 0; }
            switchTurn();
        }
    }

    /** Validates the typed input and places the human move. False = move rejected. */
    private boolean humanMove(String s) {
        int n;
        try { n = Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) {
            System.out.println("Invalid input. Enter a number from 1 to "
                    + (connect4 ? 7 : 9) + ".");
            return false;
        }
        int move = n - 1;
        int limit = connect4 ? board.getCols() : board.getRows() * board.getCols();
        if (move < 0 || move >= limit) {
            System.out.println(connect4 ? "Invalid column. Enter a number from 1 to 7."
                                        : "Invalid cell. Enter a number from 1 to 9.");
            return false;
        }
        if (!board.isValidMove(move)) {
            System.out.println(connect4 ? "Column " + n + " is full. Try again."
                                        : "Cell " + n + " is already occupied. Try again.");
            return false;
        }
        int row = board.placeMove(move, HUMAN);
        moveStack.push(new int[]{row, board.colOf(move)});
        System.out.println(connect4
                ? "You dropped X in column " + n + " (row " + (board.getRows() - row) + ")"
                : "You placed X at cell " + n);
        return true;
    }

    private void computerMove() {
        int move = ai.getBestMove(board);
        int row = board.placeMove(move, COMPUTER);
        moveStack.push(new int[]{row, board.colOf(move)});
        System.out.println(connect4
                ? "AI dropped O in column " + (move + 1)
                        + " (row " + (board.getRows() - row) + ")"
                : "AI placed O at cell " + (move + 1));
    }

    /** Pops the AI move and then the human move from the Stack. O(1). */
    public void undoLastMove() {
        if (moveStack.size() < 2) { System.out.println("Nothing to undo."); return; }
        int[] aiMove = moveStack.pop();
        int[] humanMove = moveStack.pop();
        board.undoMove(aiMove[0], aiMove[1]);
        board.undoMove(humanMove[0], humanMove[1]);
        System.out.println("Last moves undone (Human: " + describe(humanMove)
                + ", AI: " + describe(aiMove) + ")");
    }

    private String describe(int[] m) {
        return connect4 ? "column " + (m[1] + 1) : "cell " + (m[0] * board.getCols() + m[1] + 1);
    }

    public void switchTurn() { currentPlayer = (currentPlayer == HUMAN) ? COMPUTER : HUMAN; }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return in.hasNextLine() ? in.nextLine() : null;
    }

    /** Updates the scoreboard for this game and saves it to the file. */
    public void saveScore(int result) {
        if (result == 1) scores[gameIndex][0]++;
        else if (result == -1) scores[gameIndex][1]++;
        else if (result == 0) scores[gameIndex][2]++;
        saveScores();
    }

    // ---------------------------- file handling ----------------------------
    public static void saveScores() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int g = 0; g < 2; g++) {
                bw.write(NAMES[g] + "|" + scores[g][0] + "|" + scores[g][1] + "|" + scores[g][2]);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Could not save scoreboard: " + e.getMessage());
        }
    }

    public static void loadScores() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length != 4) continue;
                for (int g = 0; g < 2; g++) {
                    if (p[0].equals(NAMES[g]))
                        for (int i = 0; i < 3; i++)
                            scores[g][i] = Integer.parseInt(p[i + 1].trim());
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Scoreboard file is unreadable, starting fresh.");
        }
    }

    public static void showScores() {
        System.out.println("\n===== SCOREBOARD =====");
        for (int g = 0; g < 2; g++)
            System.out.printf("%-10s Wins %d | Losses %d | Draws %d%n",
                    NAMES[g], scores[g][0], scores[g][1], scores[g][2]);
    }
}