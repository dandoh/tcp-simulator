package event;

import Fsm.Event;

import java.net.DatagramPacket;

public class ReceiveACKEvent extends Event {

	private DatagramPacket datagramPacket;

	public ReceiveACKEvent(String name, DatagramPacket obj) {
		super(name);
		this.datagramPacket = obj;
	}
	
	public ReceiveACKEvent(String name) {
		super(name);
	}

    public DatagramPacket getDatagramPacket() {
        return datagramPacket;
    }

}
