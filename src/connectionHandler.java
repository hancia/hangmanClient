import java.io.*;
import java.net.*;

public class connectionHandler {
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public void setBufferedWriter(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }


    public static void main(String[] args){
        new connectionHandler(args[0], args[1]);
    }

    private connectionHandler(String address, String pt){
        String testServerName = address;
        int port = Integer.parseInt(pt);
        try
        {
            // open a socket
            socket = openSocket(testServerName, port);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            hangmanWindow window = new hangmanWindow(this);

            // write-to, and read-from the socket.
            // in this case just write a simple command to a web server.
            //writeToAndReadFromSocket(socket, "0");

            // close the socket, and we're done
            //System.out.println("Closing socket connection");
            //socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void writeToAndReadFromSocket(Socket socket, String writeTo) throws Exception
    {
        try
        {
            boolean run = true;
            // write text to the socket
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write(writeTo);
            bufferedWriter.flush();

            // read text from the socket
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(run)
                System.out.println(bufferedReader.readLine());

            // close the reader, and return the results as a String
            bufferedReader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    private Socket openSocket(String server, int port) throws Exception
    {
        Socket socket;

        // create a socket with a timeout
        try
        {
            InetAddress inteAddress = InetAddress.getByName(server);
            SocketAddress socketAddress = new InetSocketAddress(inteAddress, port);

            // create a socket
            socket = new Socket();

            // this method will block no more than timeout ms.
            int timeoutInMs = 10*1000;   // 10 seconds
            socket.connect(socketAddress, timeoutInMs);

            return socket;
        }
        catch (SocketTimeoutException ste)
        {
            System.err.println("Timed out waiting for the socket.");
            ste.printStackTrace();
            throw ste;
        }
    }
}
