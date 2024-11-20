package tcpclient;
import java.net.*;
import java.io.*;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class TCPClient {
    private static boolean shutdown;
    private static Integer timeout;
    private static Integer limit;
    private static Integer bufferSize = 1024;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }
    public static byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        //creat Bytearrayoutputstream
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        //create socket
        Socket clientserver = new Socket(hostname, port);
        try {

            InputStream inputFromServer = clientserver.getInputStream();
            OutputStream outputToServer = clientserver.getOutputStream();

            outputToServer.write(toServerBytes);

            if (shutdown) {
                clientserver.shutdownOutput();
            }

            if (timeout != null) {
                clientserver.setSoTimeout(timeout);
            }
            //||
            if (limit != null){
                int count = 0;
                int numbytes = 0;
                while ((count = inputFromServer.read()) >= 0 && numbytes < limit){
                    byteArray.write(count);
                    numbytes++;
                }
                clientserver.close();
                return byteArray.toByteArray();
            }
                int count = 0;
           // System.out.println("prog");
                while ((count = inputFromServer.read()) >= 0){
                    //System.out.println(count);
                    byteArray.write(count);
                }
                clientserver.close();
                return byteArray.toByteArray();

        }
        catch (Exception ex){
            System.out.println(ex);
        }
        clientserver.close();
        return byteArray.toByteArray();
    }
    public static String askServer(String hostname, int port) throws  IOException, InterruptedException
    {
        Socket socket = new Socket(hostname, port);
        byte[] buf = new byte[bufferSize];
        String ret =" ";

        TimeUnit.SECONDS.sleep(1);

        if (socket.getInputStream().available()>0)
        {
            StringBuilder sb = new StringBuilder();

            int len = socket.getInputStream().read(buf);
            sb.append(new String(buf, 0, len, StandardCharsets.UTF_8));
            buf = new byte[bufferSize];

            ret = sb.toString();
        }
        else
        {
            ret = "connection was dropped";
        }

        socket.close();

        return ret;
    }
    public static byte[] askServeR(String hostname, int port) throws IOException {
        //creat Bytearrayoutputstream
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        //create socket
        Socket clientserver = new Socket(hostname, port);
        InputStream inputFromServer = clientserver.getInputStream();
        if (timeout != null) {
            clientserver.setSoTimeout(timeout);
        }
        //||
        if (limit != null){
            int count = 0;
            int numbytes = 0;
            while ((count = inputFromServer.read()) >= 0 && numbytes < limit){
                byteArray.write(count);
                numbytes++;
            }
            clientserver.close();
            return byteArray.toByteArray();
        }
        int count = 0;
        while ((count = inputFromServer.read()) >= 0){
            //System.out.println(count);
            byteArray.write(count);
        }
        clientserver.close();
        return byteArray.toByteArray();
    }
}

