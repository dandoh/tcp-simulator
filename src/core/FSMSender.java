package core;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import state.StartState;
import tcp.Constant;
import Fsm.FSM;
import Fsm.FsmException;
import Fsm.Transition;
import action.ReceiveACKAction;
import action.SendDataAction;
import action.TimeOutAction;
import event.ReceiveACKEvent;
import event.SendDataEvent;
import event.TimeOutEvent;
import utils.CheckSumUtils;
import utils.CircularBuffer;
import utils.EncapsulateUtils;
import utils.Utils;

public class FSMSender extends FSM {

    private final String TAG = getClass().getSimpleName();

    private final CircularBuffer buffer = new CircularBuffer(Constant.SIZE_BUFFER);
    private OutputStream outputStream = new TCPOutputStream();

    private int base = 1;
    private int nextseqnum = 1;

    private int lastSeq;

    private DatagramSocket datagramSocket;


    private ArrayList<Timer> timers;

    private boolean isConnect;
    private String desAdress;
    private int desPort;


    // lock to handle full window and after send data
    private Semaphore semaphore = new Semaphore(0);
    private Semaphore sentSemaphore = new Semaphore(0);

    private HashMap<Integer, DatagramPacket> notAckPackets = new HashMap<>();

    public FSMSender(int port) throws SocketException, FsmException {
        super("name", new StartState("Start"));
        this.datagramSocket = new DatagramSocket(port);
    }


    private void initialize() throws FsmException {

        this.timers = new ArrayList<>();

        addTransition(new Transition(currentState(), new SendDataEvent(
                Constant.EVENT_SEND), currentState(), new SendDataAction()));
        addTransition(new Transition(currentState(), new ReceiveACKEvent(
                Constant.EVENT_ACK_RECEIVE), currentState(),
                new ReceiveACKAction()));
        addTransition(new Transition(currentState(), new TimeOutEvent(
                Constant.EVENT_TIMEOUT), currentState(), new TimeOutAction()));

        new Thread(() -> {
            while (true) {
                byte[] buffer = new byte[Constant.SIZE_SEGMENT];
                DatagramPacket datagramPacket = new DatagramPacket(buffer,
                        buffer.length);
                try {
                    datagramSocket.receive(datagramPacket);

                    // Only receive data from connected host and port
                    if (datagramPacket.getPort() != desPort
                            || !datagramPacket.getAddress().getHostAddress().equals(desAdress))
                        continue;

                    // if receive data is correct
                    if (CheckSumUtils.isCorrect(datagramPacket)) {
                        doEvent(new ReceiveACKEvent(
                                Constant.EVENT_ACK_RECEIVE, datagramPacket));
                    }
                } catch (IOException | FsmException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // TODO
    public void send(byte[] data) throws Exception {

        if (!isConnect)
            throw new Exception("Haven't connected");
//		Utils.log(TAG, new String(data));
        byte[][] splitedData = EncapsulateUtils.splitData(data);
        int l = splitedData.length;

        lastSeq = nextseqnum + l - 1;

        for (int i = 0; i < l; i++) {
            if (nextseqnum >= base + Constant.N) {
                semaphore.acquire();
            }

            doEvent(new SendDataEvent(Constant.EVENT_SEND, splitedData[i]));
        }

        sentSemaphore.acquire();
    }


    /**
     * Send connect request to destination datagram socket.
     * Simulate the connection
     *
     * @param address destination address
     * @param port    destination port
     */
    public void connect(String address, int port) throws FsmException, IOException {
        // TODO send connect request
        datagramSocket.connect(InetAddress.getByName(address), port);

        desAdress = datagramSocket.getInetAddress().getHostAddress();
        desPort = datagramSocket.getPort();

        // Make connect packet and send
        byte[] content = "Connect".getBytes();
        byte[] checksum = CheckSumUtils.getCheckSumBytes(content);
        byte[] connectPacket = EncapsulateUtils.concat(checksum, content);
        datagramSocket.send(new DatagramPacket(connectPacket, connectPacket.length));

//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                Utils.log(TAG, "Resend connect packet");
//                try {
//                    datagramSocket.send(new DatagramPacket(connectPacket, connectPacket.length));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, Constant.TIME_OUT, Constant.TIME_OUT);


        // Receive return packet
        byte[] buf = new byte[20];
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
        while (true) {
            datagramSocket.receive(datagramPacket);
            if (CheckSumUtils.isCorrect(datagramPacket)
                    && isAccept(datagramPacket)) {
//                timer.cancel();
                break;
            }
        }

        this.isConnect = true;
        initialize();
    }

    private boolean isAccept(DatagramPacket packet) {
        byte[] data = EncapsulateUtils.getActualData(packet);
        String stringData = new String(data);

        Utils.log(TAG, stringData);

        if (stringData.equals("Accepted")) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Start timer
     */
    public synchronized void startTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    Utils.log(TAG, "time out");
                    doEvent(new TimeOutEvent(Constant.EVENT_TIMEOUT));
                } catch (FsmException e) {
                    e.printStackTrace();
                }
            }
        }, Constant.TIME_OUT);

        timers.add(timer);

    }

    public synchronized int getNextseqnum() {
        return nextseqnum;
    }

    public synchronized void setNextseqnum(int nextseqnum) {
        this.nextseqnum = nextseqnum;
    }

    public synchronized int getBase() {
        return base;
    }

    public synchronized void setBase(int base) {
        this.base = base;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public HashMap<Integer, DatagramPacket> getNotAckPackets() {
        return notAckPackets;
    }

    public synchronized void stopTimer() {
        for (Timer timer : timers) {
            timer.cancel();
        }

        timers.clear();
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public Semaphore getSentSemaphore() {
        return sentSemaphore;
    }

    public int getLastSeq() {
        return lastSeq;
    }


    private class TCPOutputStream extends OutputStream {

        /**
         * Writes the specified byte to this output stream. The general
         * contract for <code>write</code> is that one byte is written
         * to the output stream. The byte to be written is the eight
         * low-order bits of the argument <code>b</code>. The 24
         * high-order bits of <code>b</code> are ignored.
         * <p>
         * Subclasses of <code>OutputStream</code> must provide an
         * implementation for this method.
         *
         * @param b the <code>byte</code>.
         * @throws IOException if an I/O error occurs. In particular,
         *                     an <code>IOException</code> may be thrown if the
         *                     output stream has been closed.
         */
        @Override
        public void write(int b) throws IOException {
            if (buffer.isFull())
                flush();
            buffer.insert((byte) b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            super.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            try {
                byte[] toSend = buffer.getBytesAndClear();
                Utils.log(TAG, "Sending " + toSend.length + " bytes ");
                send(toSend);
            } catch (FsmException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public OutputStream getOutputStream() {
        if (!isConnect) throw new IllegalStateException("Not connected yet");
        return outputStream;
    }


}
