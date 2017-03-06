package action;

import core.FSMSender;
import event.ReceiveACKEvent;
import tcp.Constant;
import tcp.TCPSender;
import Fsm.Action;
import Fsm.Event;
import Fsm.FSM;
import utils.EncapsulateUtils;
import utils.Utils;

import java.net.DatagramPacket;
import java.util.HashMap;

public class ReceiveACKAction extends Action{

	private final String TAG = getClass().getSimpleName();

	@Override
	public void execute(FSM fsm, Event e) {
		FSMSender tcpfsm = (FSMSender) fsm;
		ReceiveACKEvent event = (ReceiveACKEvent) e;

		// received ack number, increase base number
		int seq = EncapsulateUtils.getSequenceNumber(event.getDatagramPacket());
		// if seq = 0 mean it is accepted packet, then ignore its data
		if (seq == 0) return;

		Utils.log(TAG, "Received ACK with number " + seq);

		// Clear packet have been ACKed
		HashMap<Integer, DatagramPacket> sendNotAck = tcpfsm.getNotAckPackets();
		for (int i = tcpfsm.getBase(); i <= seq; i++) {
			sendNotAck.remove(i);
		}

		if (tcpfsm.getLastSeq() == seq) {
			tcpfsm.getSentSemaphore().release();
		}

		// Set current base
		tcpfsm.setBase(seq + 1);

		// restart timer or stop timer
		if (tcpfsm.getBase() == tcpfsm.getNextseqnum()) {
//			Utils.log(TAG, "Stop timer");
			tcpfsm.stopTimer();
		} else {

//			Utils.log(TAG, "Restart timer");
			tcpfsm.stopTimer();
			tcpfsm.startTimer();
		}
//
		if (tcpfsm.getBase() + Constant.N > tcpfsm.getNextseqnum()) {
			if (!tcpfsm.getSemaphore().tryAcquire()) {
				tcpfsm.getSemaphore().release();
			}
		}




	}

}
