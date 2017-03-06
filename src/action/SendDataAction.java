package action;


import core.FSMSender;
import event.SendDataEvent;
import tcp.Constant;
import Fsm.Action;
import Fsm.Event;
import Fsm.FSM;
import utils.EncapsulateUtils;
import utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class SendDataAction extends Action {

    public final String TAG = getClass().getSimpleName();

    @Override
    public void execute(FSM fsm, Event event) {
        FSMSender tcpfsm = (FSMSender) fsm;
        SendDataEvent dataEvent = (SendDataEvent) event;

        int next = tcpfsm.getNextseqnum();
        Utils.log(TAG, "Sending packet with sequence number " + next);


        // Encapsulate data with checksum, next sequence number then send
        DatagramPacket datagramPacket = EncapsulateUtils.encapsulate(dataEvent.getData(), next);
        try {
            if (Utils.testLost()) {
                tcpfsm.getDatagramSocket().send(datagramPacket);
            } else {
                Utils.log(TAG, "Packet " + next + " has lost");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Save this packet, may be re-delivered later
        tcpfsm.getNotAckPackets().put(next, datagramPacket);

        // Start timer
        if (next == tcpfsm.getBase()) {
            tcpfsm.startTimer();
        }

        // Increase next sequence number
        next++;
        tcpfsm.setNextseqnum(next);


    }






}
