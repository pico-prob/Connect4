package VierGewinnt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class VierGewinntCanvas extends Canvas implements Runnable {

    // Eigenschaften Spielbrett
    private Image board, win, lose, notmyturn, full, bluetoothError;
    private Grid grid;
    private int backgroundColor = 0xFFFFFF;
    private int w = this.getWidth();
    private int h = this.getHeight();
    private int squareLength = w / 7;
    private int squareHeight = 40;
    private int discLength = w / 7;
    private int discHeight = 34;
    
    // Eigenschaften Spielsteuerung
    private int currentCol, droppedCol;
    private int you, opponent, winner;
    private boolean myTurn;
    
    // Eigenschaften Kommunikationssteuerung
    private DataInputStream in;
    private DataOutputStream out;
    private boolean sending = false;
    private boolean btrErrFound = false;

    VierGewinntCanvas(int player, DataInputStream in, DataOutputStream out) {
        try {
            this.board = Image.createImage("/images/board.png");
            this.win = Image.createImage("/images/win.png");
            this.lose = Image.createImage("/images/lose.png");
            this.notmyturn = Image.createImage("/images/notmyturn.png");
            this.full = Image.createImage("/images/full.png");
            this.bluetoothError = Image.createImage("/images/bterr.png");
        } catch (IOException ex) {
        }

        this.in = in;
        this.out = out;

        this.you = player;
        this.initGame(true);
    }

    // initGame () lagert die Initialisierung 
    // aus dem Konstruktor aus (für wdh. Spiel)     
    private void initGame(boolean firstGame) {
        this.grid = new Grid(7, 6);
        if (!firstGame) { // Spielerwechsel beim Folgespiel
            int temp_you;
            if (this.you == 1) {
                temp_you = 2;
            } else {
                temp_you = 1;
            }
            this.you = temp_you;
        }
        if (this.you == 1) {
            this.opponent = 2;
        } else {
            this.opponent = 1;
        }
        if (this.you == 1) {
            this.myTurn = true;
        } else {
            this.myTurn = false;
        }
        this.winner = 0;
        currentCol = 0;

        repaint();
        
        // Spieler Zwei muss in der ersten Runde auf den Spieler Eins warten 
        if (this.you == 2) {
            Thread thread = new Thread(this);
            thread.start();
        }
    }

// paint () setzt das Bild zusammen    
    protected void paint(Graphics g) {
        int[][] gridArray = grid.getArray();

        g.setColor(backgroundColor);
        g.fillRect(0, 0, w, h);

        // Steine zeichnen
        gridArray = grid.getArray();
        for (int j = 0; j < gridArray.length; j++) {
            for (int i = 0; i < gridArray[j].length; i++) {
                if (gridArray[j][i] != 0) {
                    int playerColor;
                    //playerColor = 0x36BC3F;
                    playerColor = 0xECD702;
                    if (gridArray[j][i] == 2) {
                        playerColor = 0xE01717;
                    }
                    g.setColor(playerColor);
                    g.fillRect(discLength * i, (discHeight * j) + 48, 
                               discLength, discHeight);
                }
            }
        }

        g.drawImage(board, 0, 0, Graphics.TOP | Graphics.LEFT);

        // Pfeile verdecken
        g.setColor(backgroundColor);
        int[] row = {0, 0, 0, 0, 0, 0, 0};
        row[currentCol] = 1;
        for (int i = 0; i < row.length; i++) {
            if (row[i] == 0) {
                g.fillRect(squareLength * i, 0, squareLength, squareHeight);
            }
        }

        // Gewinner/Verlierer Grafik einblenden
        if (this.winner != 0) {
            if (winner == this.you) {
                g.drawImage(win, 0, 0, Graphics.TOP | Graphics.LEFT);
            } else {
                g.drawImage(lose, 0, 0, Graphics.TOP | Graphics.LEFT);
            }
        }
        // Unentschieden Grafik einblenden
        if (grid.isFull()) {
            g.drawImage(full, 0, 0, Graphics.TOP | Graphics.LEFT);
        }

        // Warten auf anderen Spieler Grafik einblenden
        if (!myTurn && this.winner == 0) {
            g.drawImage(notmyturn, 0, 0, Graphics.TOP | Graphics.LEFT);

        }
        // Verbindung Verloren Grafik einblenden
        if (btrErrFound) {
            g.drawImage(bluetoothError, 0, 0, Graphics.TOP | Graphics.LEFT);
        }

    }

    // keyPressed fängt alle User-Eingaben ab und filtert die relevanten raus
    protected void keyPressed(int keyCode) {

        int gameAction = getGameAction(keyCode);
        if (gameAction == LEFT) {
            doLeftAction();
        } else if (gameAction == RIGHT) {
            doRightAction();
        } else if (gameAction == FIRE) {
            doFireAction();
        }
        repaint();
    }

    private void doLeftAction() {
        if (this.currentCol != 0) {
            this.currentCol--;
        }
    }

    private void doRightAction() {
        if (this.currentCol != 6) {
            this.currentCol++;
        }
    }

    // doFireAction nimmt die Spaltenauswahl an und prüft den Spielstand
    private void doFireAction() {
        // Zug überhaupt möglich? 
        if ((this.winner != 0) || (grid.isFull())) {
            initGame(false);
        } else {
            if (this.myTurn) {
                // Spielstein setzen, wenn gültiger Zug (>=0)
                if (this.grid.dropDisc(this.currentCol, you) > -1) {
                    this.winner = grid.checkForRow();
                    this.myTurn = false;
                    this.sending = true;
                    repaint();
                    Thread thread = new Thread(this);
                    thread.start();
                }
            }
        }
    }

    // Der Runniable-Thread koppelt die Bluetooth-Verbindung aus
    public void run() {
        if (this.sending) {
            try {
                out.writeInt(this.currentCol);
            } catch (IOException ex) {
                btrErrFound = true;
            }
            this.sending = false;
        }

        // Nur wenn das Spiel noch laeuft, gibt es eine Antwort
        // vom remoteDevice (nicht bei gewonnen/unentschieden) 
        if (this.winner == 0 && !this.grid.isFull()) {
            try {
                int inv = this.in.readInt();
                this.grid.dropDisc(inv, this.opponent);
                this.winner = grid.checkForRow();
                this.myTurn = true;
            } catch (IOException ex) {
                btrErrFound = true;
            }
        }
        repaint();
    }
}