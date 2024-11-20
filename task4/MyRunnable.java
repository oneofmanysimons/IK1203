import java.net.*;
import java.io.*;
import tcpclient.TCPClient;

public class MyRunnable implements Runnable {
    public String hostname = null;
    public Integer port = null;
    public byte[] string = null;
    public boolean shutdown = false;
    public Integer timeout = null;
    public Integer limit = null;
    Socket connection;

    byte[] getRequest = new byte[1024];
    String HTTP404 = "HTTP/1.1 404 Not Found\r\n\r\n";
    String HTTP200 = "HTTP/1.1 200 OK\r\n\r\n";
    String HTTP400 = "HTTP/1.1 400 Bad Request\r\n\r\n";

    public MyRunnable(Socket connection) throws IOException {
        this.connection = connection;
    }

    public void run() {
        try {
            InputStream inputStream = connection.getInputStream();
            OutputStream outputStream = connection.getOutputStream();
            //handle the get message we get from serverSocket.accept()
            inputStream.read(getRequest);
            String requestToString = new String(getRequest);
            String[] bigM = requestToString.split("\n");
            requestToString = bigM[0];
            String[] smallM = requestToString.split("[?\\ \\&\\=]");
            if (!(bigM[0].contains("GET /ask?") && bigM[0].contains("HTTP/1.1"))) {
                outputStream.write(HTTP400.getBytes());
                connection.close();
            } else {
                //switch case
                for (int i = 0; i < smallM.length; i++) {
                    if ("hostname".equals(smallM[i])) {
                        hostname = smallM[i + 1];
                    } else if ("port".equals(smallM[i])) {
                        port = Integer.parseInt(smallM[i + 1]);
                    } else if ("string".equals(smallM[i])) {
                        string = smallM[i + 1].getBytes();
                    } else if ("shutdown".equals(smallM[i])) {
                        shutdown = Boolean.parseBoolean(smallM[i + 1]);
                    } else if ("timeout".equals(smallM[i])) {
                        timeout = Integer.parseInt(smallM[i + 1]);
                    } else if ("limit".equals(smallM[i])) {
                        limit = Integer.parseInt(smallM[i + 1]);
                    }
                }

                //HTTP status handling
                if (hostname != null && port != null && string != null) {
                    try {
                        TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                        byte[] sendToServer = tcpClient.askServer(hostname, port, string);
                        outputStream.write((HTTP200).getBytes());
                        outputStream.write(sendToServer);
                    } catch (Exception ex) {
                        outputStream.write(HTTP404.getBytes());
                    }
                } else if (hostname != null && port != null) {
                    try {
                        TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                        byte[] sendToServer = tcpClient.askServeR(hostname, port);
                        outputStream.write((HTTP200).getBytes());
                        outputStream.write(sendToServer);
                    } catch (Exception ex) {
                        outputStream.write(HTTP404.getBytes());
                    }
                } else {
                    outputStream.write(HTTP400.getBytes());
                }
                connection.close();
            }
        }catch (Exception ex){
            System.out.println(ex);
        }
    }
}
