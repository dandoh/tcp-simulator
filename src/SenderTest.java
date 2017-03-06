import Fsm.FsmException;
import tcp.Constant;
import tcp.TCPSender;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Dandoh on 12/1/15.
 */
public class SenderTest {


    public static void main(String[] args) throws IOException, FsmException {
        TCPSender tcp = new TCPSender(5678);
//        tcp.connect("192.168.43.166", 4567);
        tcp.connect(null, 4567);

        OutputStream outputStream = tcp.getOutputStream();

        long time = System.currentTimeMillis();
//         Send images file
//        Path path = Paths.get("sach.pdf");
//        byte[] data = Files.readAllBytes (path);
//        outputStream.write(data);
//        outputStream.flush();

        // Send string
//        DataOutputStream dataOutputStream = new DataOutputStream(tcp.getOutputStream());
//        dataOutputStream.writeUTF("Hello world asdjhaskdh");
//        dataOutputStream.flush();

//         Send byte[] ~ 10MB

        outputStream.write(new byte[1024 * 1024]);
        outputStream.flush();
        time = System.currentTimeMillis() - time;


        System.out.println("Elapsed time : " + time);



    }

}
