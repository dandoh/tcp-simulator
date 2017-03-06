package event;

import Fsm.Event;

public class DefaultReceiveEvent extends Event {

	public DefaultReceiveEvent(String name, Object obj) {
		super(name, obj);
	}
	
	public DefaultReceiveEvent(String name) {
		super(name);
	}

}
