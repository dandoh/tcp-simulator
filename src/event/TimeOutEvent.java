package event;

import Fsm.Event;

public class TimeOutEvent extends Event {

	public TimeOutEvent(String name, Object obj) {
		super(name, obj);
	}
	
	public TimeOutEvent(String name) {
		super(name);
	}

}
