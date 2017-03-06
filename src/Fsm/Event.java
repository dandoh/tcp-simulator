//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package Fsm;

public abstract class Event {
    private String eventName;
    private Object eventValue;

    public Event(String name) {
        this.eventName = name;
        this.eventValue = null;
    }

    public Event(String name, Object obj) {
        this.eventName = name;
        this.eventValue = obj;
    }

    public String getName() {
        return this.eventName;
    }

    public Object getValue() {
        return this.eventValue;
    }

    public void setValue(Object o) {
        this.eventValue = o;
    }

    public String toString() {
        return new String("Event(" + this.eventName + ") Value(" + this.eventValue + ")");
    }
}
