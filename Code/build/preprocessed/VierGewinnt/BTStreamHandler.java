package VierGewinnt;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 *
 * @author thsc
 */
public interface BTStreamHandler {

    public void handleStream(DataInputStream in, DataOutputStream out);
}
