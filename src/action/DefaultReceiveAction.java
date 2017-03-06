package action;

import Fsm.Action;
import Fsm.Event;
import Fsm.FSM;
import core.FSMReceiver;
import utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;

public class DefaultReceiveAction extends Action {

	private final String TAG = getClass().getSimpleName();

	@Override
	public void execute(FSM fsm, Event event) {
		Utils.log(TAG, "Default received packet, not expected packet");

		FSMReceiver fsmReceiver = (FSMReceiver) fsm;

		//just deliver the packet have latest sequence number have been ACKed
		DatagramPacket toSend = fsmReceiver.getToSend();
		try {
			fsmReceiver.getDatagramSocket().send(toSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
