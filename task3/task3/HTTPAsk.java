import tcpclient.TCPClient;
import java.net.*;
import java.io.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class HTTPAsk {
    public static String hostname = null;
    public static Integer port = null;
    public static byte[] string = null;
    public static boolean shutdown = false;
    public static Integer timeout = null;
    public static Integer limit = null;

    public static void main(String[] args) throws Exception {
        byte[] getRequest = new byte[1024];
        String HTTP404 = "HTTP/1.1 404 Not Found\r\n\r\n";
        String HTTP200 = "HTTP/1.1 200 OK\r\n\r\n";
        String HTTP400 = "HTTP/1.1 400 Bad Request\r\n\r\n";
        String HTTP501 = "HTTP/1.1 501 Not Implemented\r\n\r\n";
        String HTTP505 = "HTTP/1.1 505 version not supported\r\n\r\n";

        //port part
        if (args.length < 1) {
            port = 8080;
        } else {
            port = Integer.parseInt(args[0]);
        }
        //sätter upp conection med servern
        //set upp clientsocket,  input and outputstream
        ServerSocket serverSocket = new ServerSocket(port);

        Socket clientSocket = serverSocket.accept();
        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();

        //handle the get message we get from serverSocket.accept()
        inputStream.read(getRequest);
        String requestToString = new String(getRequest);
        String[] bigM = requestToString.split("\n");
        requestToString = bigM[0];
        String[] smallM = requestToString.split("[?\\ \\&\\=]");
            /*
            if (!(smallM[0].contains("GET") && smallM[smallM.length - 1].contains("HTTP/1.1"))) {
                outputStream.write(HTTP400.getBytes());
            } else if (!bigM[0].contains("ask")) {
                outputStream.write(HTTP501.getBytes());
            } else if (!bigM[0].contains("HTTP/1.1")) {
                outputStream.write(HTTP505.getBytes());
             */
        if (!(bigM[0].contains("GET") && bigM[0].contains("HTTP/1.1"))) {
            outputStream.write(HTTP400.getBytes());
        } else if (!bigM[0].contains("ask")) {
            outputStream.write(HTTP404.getBytes());
        } else if (!(bigM[0].contains("hostname") && bigM[0].contains("port"))) {
            outputStream.write(HTTP404.getBytes());
        } else {
            //switch case
            for (int i = 0; i < smallM.length; i++) {
                switch (smallM[i]) {
                    case "hostname":
                        hostname = smallM[i + 1];
                        break;
                    case "port":
                        port = Integer.parseInt(smallM[i + 1]);
                        break;
                    case "string":
                        string = smallM[i + 1].getBytes();
                        break;
                    case "shutdown":
                        shutdown = Boolean.parseBoolean(smallM[i + 1]);
                        break;
                    case "timeout":
                        timeout = Integer.parseInt(smallM[i + 1]);
                        break;
                    case "limit":
                        limit = Integer.parseInt(smallM[i + 1]);
                        break;
                    default:
                }
            }

            //HTTP status handling
            if (hostname != null && port != null && string != null) {
                try {
                    TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                    byte[] sendToServer = tcpClient.askServer(hostname, port, string);
                    outputStream.write(HTTP200.getBytes());
                    outputStream.write(sendToServer);
                } catch (Exception ex) {
                    outputStream.write(HTTP404.getBytes());
                }
            } else if (hostname != null && port != null) {
                try {
                    TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                    byte[] sendToServer = tcpClient.askServeR(hostname, port);
                    outputStream.write(HTTP200.getBytes());
                    outputStream.write(sendToServer);
                } catch (Exception ex) {
                    outputStream.write(HTTP404.getBytes());
                }
            } else {
                outputStream.write(HTTP404.getBytes());
            }
        }
        clientSocket.close();
    }
}