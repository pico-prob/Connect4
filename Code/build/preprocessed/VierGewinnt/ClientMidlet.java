package VierGewinnt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

public class ClientMidlet extends MIDlet implements BTStreamHandler, CommandListener {

    BTClient btClient;
    private Canvas myCanvas;
    Command cmdExit;

    public void startApp() {
        this.btClient = new BTClient();
        this.btClient.findServer(this);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    private Canvas getMyCanvas(DataInputStream in, DataOutputStream out) {
        if (myCanvas == null) {
            this.cmdExit = new Command("Exit", Command.EXIT, 1);
            myCanvas = new VierGewinntCanvas(2, in, out);
            myCanvas.addCommand(cmdExit);
            myCanvas.setCommandListener(this);
        }
        return myCanvas;
    }

    public void commandAction(Command cmd, Displayable d) {
        if (d == this.myCanvas && cmd == this.cmdExit) {
            this.notifyDestroyed();
        }
    }

    public void handleStream(DataInputStream in, DataOutputStream out) {
        try {
            Display.getDisplay(this).setCurrent(getMyCanvas(in, out));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
