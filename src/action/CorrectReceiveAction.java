package action;

import core.FSMReceiver;
import Fsm.Action;
import Fsm.Event;
import Fsm.FSM;
import event.CorrectReceiveEvent;
import utils.EncapsulateUtils;
import utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.Random;

public class CorrectReceiveAction extends Action{

    public final String TAG = getClass().getSimpleName();

	@Override
	public void execute(FSM fsm, Event e) {
        Utils.log(TAG, "Received correct data");

        // the actual object in this context
		FSMReceiver fsmReceiver = (FSMReceiver) fsm;
		CorrectReceiveEvent event = (CorrectReceiveEvent) e;


        // deliver data to buffer
        DatagramPacket datagramPacket = event.getPacket();
        for (byte b : EncapsulateUtils.getActualData(datagramPacket)) {
//            fsmReceiver.getBuffer().insert(b);
            fsmReceiver.insert(b);
        }

//        if (!fsmReceiver.getSemaphore().tryAcquire())
//            fsmReceiver.getSemaphore().release();


        int expected = fsmReceiver.getExpectedSequenceNumber();

        // deliver ACK packet
        DatagramPacket returnPacket = EncapsulateUtils.encapsulate(new byte[0],
                expected);
        fsmReceiver.setToSendPacket(returnPacket);
        try {

            if (Utils.testLost()) {
                fsmReceiver.getDatagramSocket().send(returnPacket);
            } else {
                Utils.log(TAG, "Lost ACK " + EncapsulateUtils.getSequenceNumber(returnPacket));
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // increase expected sequence number
        expected++;
        fsmReceiver.setExpectedSequenceNumber(expected);



    }

}
