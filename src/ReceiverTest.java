import Fsm.FsmException;
import tcp.TCPReceiver;

import java.io.*;
import java.util.Arrays;

/**
 * Created by Dandoh on 12/1/15.
 */
public class ReceiverTest {

    public static void main(String[] args) {
        TCPReceiver tcpReceiver = null;
        try {
            tcpReceiver = new TCPReceiver(4567);
            tcpReceiver.accept();

            InputStream inputStream = tcpReceiver.getInputStream();

//            // Receive file
//            File file = new File("output.pdf");
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            while (true) {
//                fileOutputStream.write(inputStream.read());
//            }

            // Receive String
//            DataInputStream dataInputStream = new DataInputStream(inputStream);
//            while (true) {
//                System.out.println(dataInputStream.readUTF());
//            }


//             Receive bytes
//            while (true) {
//                byte b = (byte) inputStream.read();
//            }
        } catch (FsmException | IOException e) {
            e.printStackTrace();
        }
    }
}
