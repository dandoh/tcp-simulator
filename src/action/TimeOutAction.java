package action;

import core.FSMSender;
import Fsm.Action;
import Fsm.Event;
import Fsm.FSM;
import utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

public class TimeOutAction extends Action{

    private String TAG = getClass().getSimpleName();

    @Override
	public void execute(FSM fsm, Event e) {
		FSMSender tcpfsm = (FSMSender) fsm;
		Utils.log(TAG, "Resend packets");

		//TODO
		HashMap<Integer, DatagramPacket> sendNotAck = tcpfsm.getNotAckPackets();


		DatagramSocket datagramSocket = tcpfsm.getDatagramSocket();

		// Resend packet

		int i = 0;
		for (Integer key : sendNotAck.keySet()) {
			i++;
			try {
				Utils.log(TAG, "Retransmission packet " + key);
				if (Utils.testLost()) {
					datagramSocket.send(sendNotAck.get(key));
				} else {
					Utils.log(TAG, "Packet " + key + " has lost");
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

//		Utils.log(TAG, "Resend " + i + " packets");

		tcpfsm.startTimer();
	}

}
