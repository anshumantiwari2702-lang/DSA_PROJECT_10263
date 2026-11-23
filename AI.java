import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** AI: computer opponent using Minimax (recursion) with Alpha-Beta Pruning. */
public class AI {
    private static final int INF = 1_000_000;

    private final char aiPlayer, humanPlayer;
    private int maxDepth;
    private boolean random;                 // Easy level plays random moves
    private final Random rng = new Random();

    public AI(char aiPlayer, char humanPlayer) {
        this.aiPlayer = aiPlayer;
        this.humanPlayer = humanPlayer;
    }

    /** level: 1 = Easy (random), 2 = Medium (shallow depth), 3 = Hard (deeper depth). */
    public void setDifficulty(int level, boolean connect4) {
        random = (level == 1);
        if (level == 2) maxDepth = connect4 ? 3 : 2;
        if (level == 3) maxDepth = connect4 ? 6 : 9;      // Tic Tac Toe is searched fully
    }

    public int getRandomMove(Board b) {
        List<Integer> moves = b.validMoves();
        return moves.get(rng.nextInt(moves.size()));
    }

    /** Tries every legal move and keeps the one with the best Minimax score. */
    public int getBestMove(Board b) {
        if (random) return getRandomMove(b);
        int bestScore = -INF;
        List<Integer> bestMoves = new ArrayList<>();
        for (int move : b.validMoves()) {
            int r = b.placeMove(move, aiPlayer);
            int score = minimax(b, maxDepth - 1, -INF, INF, false);
            b.undoMove(r, b.colOf(move));
            if (score > bestScore) { bestScore = score; bestMoves.clear(); }
            if (score == bestScore) bestMoves.add(move);
        }
        return bestMoves.get(rng.nextInt(bestMoves.size()));   // random choice among equal moves
    }

    /** Recursive Minimax with Alpha-Beta Pruning. Worst case O(b^d), best case O(b^(d/2)). */
    public int minimax(Board b, int depth, int alpha, int beta, boolean maximizing) {
        if (b.checkWin(aiPlayer))    return 1000 + depth;       // faster wins score higher
        if (b.checkWin(humanPlayer)) return -1000 - depth;      // slower losses score higher
        if (b.isFull() || depth == 0) return evaluate(b);

        if (maximizing) {
            int best = -INF;
            for (int move : b.validMoves()) {
                int r = b.placeMove(move, aiPlayer);
                best = Math.max(best, minimax(b, depth - 1, alpha, beta, false));
                b.undoMove(r, b.colOf(move));
                alpha = Math.max(alpha, best);
                if (beta <= alpha) break;                       // prune
            }
            return best;
        } else {
            int best = INF;
            for (int move : b.validMoves()) {
                int r = b.placeMove(move, humanPlayer);
                best = Math.min(best, minimax(b, depth - 1, alpha, beta, true));
                b.undoMove(r, b.colOf(move));
                beta = Math.min(beta, best);
                if (beta <= alpha) break;                       // prune
            }
            return best;
        }
    }

    /** Heuristic score for a non-final position: centre pieces and open pairs / triples. */
    public int evaluate(Board b) {
        int score = 0, rows = b.getRows(), cols = b.getCols(), len = b.getWinLength();
        if (b.isGravity()) {
            for (int r = 0; r < rows; r++) {
                if (b.get(r, cols / 2) == aiPlayer) score += 3;
                else if (b.get(r, cols / 2) == humanPlayer) score -= 3;
            }
        }
        int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                for (int[] d : dirs) {
                    int er = r + d[0] * (len - 1), ec = c + d[1] * (len - 1);
                    if (er < 0 || er >= rows || ec < 0 || ec >= cols) continue;
                    int ai = 0, hu = 0;
                    for (int k = 0; k < len; k++) {
                        char ch = b.get(r + d[0] * k, c + d[1] * k);
                        if (ch == aiPlayer) ai++;
                        else if (ch == humanPlayer) hu++;
                    }
                    if (hu == 0) score += windowScore(ai, len);
                    if (ai == 0) score -= windowScore(hu, len);
                }
            }
        }
        return score;
    }

    private int windowScore(int count, int len) {
        if (count == len - 1) return 5;     // triple (Connect 4) / pair (Tic Tac Toe)
        if (count == len - 2) return 2;     // pair (Connect 4)
        return 0;
    }
}