package event;

import Fsm.Event;

import java.net.DatagramPacket;

public class SendDataEvent extends Event {

    private byte[] data;

    public SendDataEvent(String name, byte[] data) {
        super(name);
        this.data = data;
    }

    public SendDataEvent(String name) {
        super(name);
    }


    public byte[] getData() {
        return data;
    }
}
