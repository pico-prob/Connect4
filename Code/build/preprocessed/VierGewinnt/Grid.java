package VierGewinnt;

// Klasse für Repräsentation eines Vier-Gewinnt-Spielbretts
public class Grid {

    private int[][] grid;
    int n;
    int m;

    // Konstruktor mit Zeilen und Spaltengröße als Parameter
    public Grid(int n, int m) {
        grid = new int[m][n];
        this.m = m;
        this.n = n;
    }

    // Platziert einen Spielstein in einer bestimmten Spalte des Spiels
    public int dropDisc(int n, int spieler) {
        int i = -1; // spalte voll
        for (i = this.grid.length - 1; i >= 0; i--) {
            try {
                if (this.grid[i][n] == 0) {
                    this.grid[i][n] = spieler;
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                i = -2; // spalte existiert nicht
            }
        }
        return i;
    }

    // Prüft ob eine vertikale Viererreihe existiert
    public int checkVertical() {
        int i_z = 0;
        int i_f = 0;
        int player = 0;

        for (int i = 0; i < this.grid.length; i++) {
            i_f = 0;
            for (int j = 0; j < this.grid[i].length; j++) {
                try {
                    if (grid[i][j] != 0) {
                        if (this.grid[i_z + 1][i_f] == grid[i][j]
                                && this.grid[i_z + 2][i_f] == grid[i][j]
                                && this.grid[i_z + 3][i_f] == grid[i][j]) {
                            player = grid[i][j];
                        }
                    }
                    i_f++;
                } catch (ArrayIndexOutOfBoundsException e) {
                    continue;
                }
            }
            i_z++;
        }
        return player;
    }

    // Prüft ob eine horizontale Viererreihe existiert
    public int checkHorizontal() {
        int i_z = 0;
        int i_f = 0;
        int player = 0;

        for (int i = 0; i < this.grid.length; i++) {
            i_f = 0;
            for (int j = 0; j < this.grid[i].length; j++) {
                try {
                    if (grid[i][j] != 0) {
                        if (this.grid[i_z][i_f + 1] == grid[i][j]
                                && this.grid[i_z][i_f + 2] == grid[i][j]
                                && this.grid[i_z][i_f + 3] == grid[i][j]) {
                            player = grid[i][j];
                        }
                    }
                    i_f++;
                } catch (ArrayIndexOutOfBoundsException e) {
                    continue;
                }
            }
            i_z++;
        }
        return player;
    }

    // Prüft ob eine diagonal Viererreihe existiert
    public int checkDiagonal() {
        int i_z = 0;
        int i_f = 0;
        int field_win = 0;

        for (int i = 0; i < this.grid.length; i++) {
            i_f = 0;
            for (int j = 0; j < this.grid[i].length; j++) {
                if (grid[i][j] != 0) {
                    if (i_z - 3 > 0
                            && i_f + 3 < this.grid[i].length
                            && this.grid[i_z - 1][i_f + 1] == grid[i][j]
                            && this.grid[i_z - 2][i_f + 2] == grid[i][j]
                            && this.grid[i_z - 3][i_f + 3] == grid[i][j]) {
                        field_win = grid[i][j];
                    }
                    if (i_z + 3 < this.grid.length
                            && i_f + 3 < grid[i].length
                            && this.grid[i_z + 1][i_f + 1] == grid[i][j]
                            && this.grid[i_z + 2][i_f + 2] == grid[i][j]
                            && this.grid[i_z + 3][i_f + 3] == grid[i][j]) {
                        field_win = grid[i][j];
                    }
                }
                i_f++;
            }
            i_z++;
        }
        return field_win;
    }

    // Alle Prüffunktionen durchführen und 
    // bei Viererreihe Spielernummer zurückgeben
    public int checkForRow() {
        int player = 0;
        player = this.checkVertical();
        if (player == 0) {
            player = this.checkHorizontal();
        }
        if (player == 0) {
            player = this.checkDiagonal();
        }
        return player;
    }

    // Prüfen ob alle Felder belegt sind
    public boolean isFull() {
        boolean isFull = true;
        for (int i = 0; i < this.grid.length; i++) {
            for (int j = 0; j < this.grid[i].length; j++) {
                if (this.grid[i][j] == 0) {
                    isFull = false;
                }
            }
        }
        return isFull;
    }

    // array mit werten zurückgeben
    public int[][] getArray() {
        return this.grid;
    }
}