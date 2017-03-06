package tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.nio.ByteBuffer;

import core.FSMReceiver;
import Fsm.FsmException;
import utils.CircularBuffer;

public class TCPReceiver {

    private FSMReceiver fsm;

	public TCPReceiver(int port) throws SocketException {
		fsm = new FSMReceiver(port);
	}

    public void accept() throws IOException, FsmException {
        fsm.accept();
    }
	
	public InputStream getInputStream() {
        return fsm.getInputStream();
	}



}
