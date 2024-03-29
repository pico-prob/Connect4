package VierGewinnt;

import VierGewinnt.Constants;
import java.io.IOException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 *
 * @author thsc
 */
public class BTServer implements Runnable {

    private LocalDevice localDevice;
    private StreamConnectionNotifier notifier;
    private ServiceRecord record;
    private BTStreamHandler handler;
    private boolean stopped = false;

    public void acceptClient(BTStreamHandler handler) {

        // remember handler - inversion of control :)
        this.handler = handler;

        this.stopped = false;

        // start thread and wait for clients
        new Thread(this).start();
    }

    public void stopAccepting() {
        this.stopped = true;
    }

    public void run() {
        try {
            this.localDevice = LocalDevice.getLocalDevice();

            this.localDevice.setDiscoverable(DiscoveryAgent.GIAC);

            // prepare a URL to create a notifier
            StringBuffer url = new StringBuffer("btspp://");

            // indicate this is a server
            url.append("localhost").append(':');

            // add the UUID to identify this service
            url.append(Constants.SAMPLE_SERVER_UUID.toString());

            // add the name for our service
            url.append(";name=" + Constants.SERVER_NAME);

            // request all of the client not to be authorized
            // some devices fail on authorize=true
            url.append(";authorize=false");

            // create notifier now
            this.notifier = (StreamConnectionNotifier) Connector.open(url.toString());

            // and remember the service record for the later updates
            this.record = localDevice.getRecord(notifier);

            // everything is prepared - lets wait for clients

            StreamConnection conn = null;

            while (!this.stopped) {
                try {
                    conn = notifier.acceptAndOpen();

                    // sequential client handling
                    // start another thread to make handle multiple clients
                    this.handler.handleStream(conn.openDataInputStream(),
                            conn.openDataOutputStream());

                    // finished handling - accept another client

                } catch (IOException e) {
                    // wrong client or interrupted - continue anyway
                    continue;
                }
            }

        } catch (BluetoothStateException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
