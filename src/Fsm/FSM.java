package Fsm;



import Fsm.Event;
import Fsm.FsmException;
import Fsm.State;
import Fsm.Transition;
import java.util.Hashtable;

public class FSM {
	String name;
	State currentState;
	State startState;
	Hashtable transTable;
	boolean trace = false;

	public FSM() {
		new FSM("none");
		this.traceOut("FSM() allocated");
	}

	public FSM(String fsmName) {
		new FSM(fsmName, (State) null);
		this.traceOut("FSM(" + fsmName + ") allocated");
	}

	public FSM(String fsmName, State start) {
		this.name = fsmName;
		this.startState = start;
		this.currentState = start;
		this.transTable = new Hashtable();
		this.traceOut("FSM(" + fsmName + ") allocated, start State is " + start);
	}

	public synchronized void reset() {
		this.currentState = this.startState;
		this.traceOut("FSM(" + this.name + ") reset");
	}

	public State currentState() {
		return this.currentState;
	}

	public void nextState(State s) {
		this.currentState = s;
		this.traceOut("FSM(" + this.name + ".nextState(): current State = " + s);
	}

	public synchronized void addTransition(Transition t) throws FsmException {
		Long key = t.getKey();
		if (this.transTable.contains(key)) {
			throw new FsmException("Duplicate Transition");
		} else {
			this.transTable.put(key, t);
			this.traceOut("FSM(" + this.name + ") add transition: " + t);
		}
	}

	public synchronized void doEvent(Event e) throws FsmException {
		Long key = Transition.key(this.currentState, e);
		Transition t = (Transition) this.transTable.get(key);
		this.traceOut("FSM(" + this.name + ").doEvent(" + e + ") " + t);
		if (t == null) {
			throw new FsmException("Event: " + e.getName()
					+ " not defined for State: " + this.currentState);
		} else {
			this.currentState = t.getNextState();
			t.doAction(this, e);
		}
	}

	public void traceOn() {
		this.trace = true;
	}

	public void traceOff() {
		this.trace = false;
	}

	private void traceOut(String o) {
		if (this.trace) {
			System.out.println(o);
		}

	}
}
