import java.io.*;
import java.net.*;

public class connectionHandler {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    Socket getSocket() {
        return socket;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }


    public static void main(String[] args){
        new connectionHandler(args[0], args[1]);
    }

    private connectionHandler(String address, String pt){
        String testServerName = address;
        int port = Integer.parseInt(pt);
        try
        {
            socket = openSocket(testServerName, port);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            hangmanWindow window = new hangmanWindow(this);
        }
        catch (Exception e) {
            System.out.println("Server down");
        }
    }

    private Socket openSocket(String server, int port) throws Exception
    {
        Socket socket;
        try
        {
            InetAddress inteAddress = InetAddress.getByName(server);
            SocketAddress socketAddress = new InetSocketAddress(inteAddress, port);

            socket = new Socket();

            int timeoutInMs = 10*1000;
            socket.connect(socketAddress, timeoutInMs);

            return socket;
        }
        catch (SocketTimeoutException ste)
        {
            System.err.println("Timed out waiting for the socket.");
            throw ste;
        }
    }
}
