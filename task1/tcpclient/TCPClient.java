package tcpclient;
import java.net.*;
import java.io.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TCPClient {

    public TCPClient() {
    }
    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

        //skapar en bufferarray
        byte[] bufferarray;

        //create socket
        Socket clientserver = new Socket(hostname,port);

        //skapar konstructorer inputstream och outputstream
        InputStream inputFromClient = clientserver.getInputStream();
        OutputStream outputToClient = clientserver.getOutputStream();

        //skickar information till server
        outputToClient.write(toServerBytes);

        //tar emot infromation från användaren
        bufferarray = inputFromClient.readAllBytes();

        //stänger socket
        clientserver.close();

        //skickar tillbaka infromationen från servern
        return bufferarray;
    }
}
