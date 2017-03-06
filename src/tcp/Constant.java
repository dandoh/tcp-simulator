package tcp;

public class Constant {
	public static final String EVENT_SEND = "Event send";
	public static final String EVENT_ACK_RECEIVE = "Event ack";
	public static final String EVENT_TIMEOUT = "Event time out";
	public static final String EVENT_CORRECT_RECEIVE = "event correct receive";
	public static final String EVENT_DEFAULT_RECEIVE = "event default receive";
	
	
	public final static int SIZE_DATA = 512;
	public final static int SIZE_HEADER = 5;
	public final static int SIZE_SEGMENT = SIZE_DATA + SIZE_HEADER;
	public final static int TIME_OUT = 80; // 20
	public final static int N = 50; // 50
	public static final int SIZE_BUFFER = 20 * 1024 * 1024; // 20 MB


	public static final int LOST_PROBABILITY = 95; //100
}
