package core;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import action.CorrectReceiveAction;
import action.DefaultReceiveAction;
import event.CorrectReceiveEvent;
import event.DefaultReceiveEvent;
import state.StartState;
import tcp.Constant;
import Fsm.FSM;
import Fsm.FsmException;
import Fsm.Transition;
import utils.CheckSumUtils;
import utils.CircularBuffer;
import utils.EncapsulateUtils;
import utils.Utils;

public class FSMReceiver extends FSM {

    private static final int NUM_TRY_ACCEPT = 5;

    private final String TAG = getClass().getSimpleName();

    private DatagramSocket datagramSocket;
    private DatagramPacket toSend;


    private int expectedSeq = 1;

    private final CircularBuffer buffer = new CircularBuffer(Constant.SIZE_BUFFER);

    private int port;
    private String address;

    private Timer connectTimer;

    // Semaphore to lock when there is no data in buffer
//    private Semaphore semaphore = new Semaphore(0);


    private InputStream inputStream = new InputStream() {
        @Override
        public int read() throws IOException {
//            if (buffer.isEmpty()) {
//                try {
//                    semaphore.acquire();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            return buffer.delete();

            while (true) {
                synchronized (buffer) {
                    if (!buffer.isEmpty()) break;
                }
            }
            return buffer.delete();
        }

    };
    ;


    public FSMReceiver(int port) throws SocketException {
        // Only one state
        super("FSM receive", new StartState("Start State"));
        this.datagramSocket = new DatagramSocket(port);
    }

    public void accept() throws IOException, FsmException {
        // TODO - wait for connect packet
        Utils.log(TAG, "accepting");
        byte[] buffer = new byte[Constant.SIZE_SEGMENT];
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(packet);

            if (CheckSumUtils.isCorrect(packet)) {
                if (isConnectPacket(packet)) {
                    // Make accepted packet and send
                    // TODO - LOOP - Optimize
                    for (int i = 1; i <= NUM_TRY_ACCEPT; i++) {
                        byte[] content = "Accepted".getBytes();
                        // Sequence number 0 is safe
                        DatagramPacket acceptedPacket = EncapsulateUtils.encapsulate(content, 0);
                        datagramSocket.send(acceptedPacket);
                    }

                    break;
                }
            }
        }

        completeAccept();
    }

    private boolean isOkAccept(DatagramPacket packet) {

        byte[] data = Arrays.copyOfRange(packet.getData(), 1, packet.getLength());
        String stringData = new String(data);

        if (stringData.equals("OK")) {
            return true;
        } else {
            return false;
        }


    }

    private void completeAccept() throws FsmException {

        Utils.log(TAG, "Completed accept");

        // Make initial default ack packet, have sequence number = 0
        // deliver ACK packet
        toSend = EncapsulateUtils.encapsulate(new byte[0],
                0);

        addTransition(new Transition(currentState(), new CorrectReceiveEvent(
                Constant.EVENT_CORRECT_RECEIVE), currentState(),
                new CorrectReceiveAction()));
        addTransition(new Transition(currentState(), new DefaultReceiveEvent(
                Constant.EVENT_DEFAULT_RECEIVE), currentState(),
                new DefaultReceiveAction()));

        // Receive thread
        new Thread(() -> {
            while (true) {
                byte[] buffer = new byte[Constant.SIZE_SEGMENT];
                DatagramPacket packet = new DatagramPacket(buffer,
                        buffer.length);
                try {
                    datagramSocket.receive(packet);

                    // Only receive packet of accepted host and port
                    if (!packet.getAddress().getHostAddress().equals(address)
                            || packet.getPort() != port)
                        continue;

                    if (CheckSumUtils.isCorrect(packet) && hasExpectedSeqnum(packet)) {
                        doEvent(new CorrectReceiveEvent(
                                Constant.EVENT_CORRECT_RECEIVE, packet));
                    } else {
                        doEvent(new DefaultReceiveEvent(
                                Constant.EVENT_DEFAULT_RECEIVE, packet));
                    }
                } catch (IOException | FsmException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    protected boolean hasExpectedSeqnum(DatagramPacket packet) {
        int sequenceNumber = EncapsulateUtils.getSequenceNumber(packet);
        Utils.log(TAG, "Received packet have number " + sequenceNumber);

        if (sequenceNumber == expectedSeq) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isConnectPacket(DatagramPacket packet) {
        byte[] data = Arrays.copyOfRange(packet.getData(), 1, packet.getLength());
        String stringData = new String(data);

        if (stringData.equals("Connect")) {
            port = packet.getPort();
            address = packet.getAddress().getHostAddress();
            Utils.log(TAG, "Connected to " + address + ":" + port);
            try {
                datagramSocket.connect(InetAddress.getByName(address), port);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }


    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public void setToSendPacket(DatagramPacket toSend) {
        this.toSend = toSend;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public CircularBuffer getBuffer() {
        return buffer;
    }

    public int getExpectedSequenceNumber() {
        return expectedSeq;
    }

    public void setExpectedSequenceNumber(int expectedSeq) {
        this.expectedSeq = expectedSeq;
    }

    public DatagramPacket getToSend() {
        return toSend;
    }

//    public Semaphore getSemaphore() {
//        return semaphore;
//    }

    public void insert(byte b) {
        synchronized (buffer) {
            buffer.insert(b);
        }
    }
}
