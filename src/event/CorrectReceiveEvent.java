package event;

import Fsm.Event;

import java.net.DatagramPacket;

public class CorrectReceiveEvent extends Event {

	private DatagramPacket packet;

	public CorrectReceiveEvent(String name, DatagramPacket packet) {
		super(name);
        this.packet = packet;
	}
	
	public CorrectReceiveEvent(String name) {
		super(name);
	}

    public DatagramPacket getPacket() {
        return packet;
    }
}
