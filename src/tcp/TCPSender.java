package tcp;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import core.FSMSender;
import Fsm.FsmException;
import utils.CircularBuffer;
import utils.Utils;


public class TCPSender {

	private final String TAG = getClass().getSimpleName();
	private FSMSender tcpfsm;
	private boolean isConnect;

	
	public TCPSender(int port) throws SocketException, FsmException {
		tcpfsm = new FSMSender(port);
	}

	public void connect(String address, int port) throws IOException {
		isConnect = true;
		try {
			tcpfsm.connect(address, port);
		} catch (FsmException e) {
			e.printStackTrace();
		}
	}
	

	public OutputStream getOutputStream() {
		if (!isConnect) throw new IllegalStateException("Not connected yet");
		return tcpfsm.getOutputStream();
	}


}
