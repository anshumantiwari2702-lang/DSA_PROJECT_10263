# Tic Tac Toe & Connect 4 with AI

A console-based Java game where you play **Tic Tac Toe** or **Connect 4** against a computer opponent. The AI uses **Minimax with Alpha-Beta Pruning**, has three difficulty levels, and your scores are saved to a file between sessions.

## Features

- Two games in one program: Tic Tac Toe (3x3) and Connect 4 (6x7)
- AI opponent using recursive Minimax with Alpha-Beta Pruning
- Three difficulty levels: Easy (random), Medium (shallow search), Hard (deep search)
- Undo your last move (removes both your move and the AI's reply)
- Input validation for invalid numbers, occupied cells and full columns
- Persistent scoreboard saved in `scoreboard.txt`
- Menu-driven interface, no external libraries needed

## Project Structure

| File | Description |
|------|-------------|
| `Main.java` | Entry point with the menu and input handling |
| `Game.java` | Turn control, undo stack, difficulty and scoreboard file handling |
| `Board.java` | 2D `char[][]` grid, move validation, win and draw detection |
| `AI.java` | Minimax, Alpha-Beta Pruning and the evaluation function |

## Concepts Used

- 2D arrays for the game board
- Recursion (Minimax) with Alpha-Beta Pruning
- `Stack<int[]>` for undo
- File handling with `BufferedReader` and `BufferedWriter`
- Object-oriented design (separate classes for board, AI, game and UI)

## Requirements

- JDK 11 or higher

Check your version:

```bash
javac -version
```

## How to Run

1. Clone the repository:

   ```bash
   git clone https://github.com/<your-username>/<your-repo-name>.git
   cd <your-repo-name>
   ```

2. Compile:

   ```bash
   javac Board.java AI.java Game.java Main.java
   ```

3. Run:

   ```bash
   java Main
   ```

## How to Play

You are **X** and the computer is **O**.

```
===== TIC TAC TOE & CONNECT 4 WITH AI =====
1. Play Tic Tac Toe
2. Play Connect 4
3. View Scoreboard
4. Exit
```

| Input | Action |
|-------|--------|
| `1-9` | Place X in that cell (Tic Tac Toe) |
| `1-7` | Drop X in that column (Connect 4) |
| `U` | Undo the last move pair |
| `Q` | Quit the current game and return to the menu |

Difficulty levels: `1` = Easy, `2` = Medium, `3` = Hard.

## Scoreboard

Results are saved automatically to `scoreboard.txt` in the format:

```
TicTacToe|wins|losses|draws
Connect4|wins|losses|draws
```

The file is loaded again the next time you start the program.

## How the AI Works

- **Easy:** picks a random valid move.
- **Medium / Hard:** runs Minimax with Alpha-Beta Pruning. Faster wins and slower losses score higher.
- **Evaluation:** for unfinished positions, the AI scores centre pieces and open pairs or triples.
- **Hard on Tic Tac Toe** searches the full game tree, so it never loses.
- **Hard on Connect 4** searches 6 moves ahead.
- When several moves score equally, the AI picks one at random.

## Author

**Anshuman Tiwari**

## License

This project was created for educational purposes. Add a license of your choice (for example MIT) if you want others to reuse it.
