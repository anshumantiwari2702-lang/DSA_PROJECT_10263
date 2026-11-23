import java.util.Scanner;

/** Main: menu-driven console interface (entry point of the application). */
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Game.loadScores();                                  // load scoreboard.txt if it exists
        boolean running = true;
        while (running) {
            showMenu();
            int choice = readValidInput(sc, "Enter your choice: ", 1, 4);
            if (choice == -1) break;                        // input stream ended
            switch (choice) {
                case 1:
                case 2:
                    int level = readValidInput(sc, "Difficulty (1=Easy, 2=Medium, 3=Hard): ",
                                               1, 3);
                    if (level == -1) { running = false; break; }
                    Game game = new Game(choice - 1, level, sc);
                    int result = game.play();
                    if (result == 1) System.out.println("Congratulations, you win!");
                    else if (result == -1) System.out.println("Computer wins. Try again!");
                    else if (result == 0) System.out.println("Game over - Draw!");
                    else System.out.println("Game abandoned.");
                    if (result != 2) { game.saveScore(result); Game.showScores(); }
                    break;
                case 3:
                    Game.showScores();
                    break;
                case 4:
                    running = false;
                    break;
            }
        }
        Game.saveScores();                                  // save on exit
        System.out.println("Scoreboard saved. Goodbye!");
        sc.close();
    }

    static void showMenu() {
        System.out.println("\n===== TIC TAC TOE & CONNECT 4 WITH AI =====");
        System.out.println("1. Play Tic Tac Toe");
        System.out.println("2. Play Connect 4");
        System.out.println("3. View Scoreboard");
        System.out.println("4. Exit");
    }

    /** Asks until the user enters an integer in [min, max]. Returns -1 at end of input. */
    static int readValidInput(Scanner sc, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            if (!sc.hasNextLine()) return -1;
            String s = sc.nextLine().trim();
            try {
                int n = Integer.parseInt(s);
                if (n >= min && n <= max) return n;
            } catch (NumberFormatException ignored) { }
            System.out.println("Invalid input. Enter a number from " + min + " to " + max + ".");
        }
    }
}