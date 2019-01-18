package com.github.teocci.socket.net;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.github.teocci.socket.net.TcpClient.ETX;
import static com.github.teocci.socket.net.TcpClient.STX;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-Jan-03
 */
public class TcpClientThread extends Thread
{

    private static final byte CMD_SERVER_OPE = -125;
    //    private BufferedReader inReader;
    private DataInputStream inReader;

    private Socket socket;
    private TcpClient client;

    private int maxCount = 5;

    private volatile boolean running = false;

    // Constructor
    public TcpClientThread(TcpClient client, Socket socket)
    {
        this.client = client;
        this.socket = socket;

        open();
        start();
    }

    public void run()
    {
        while (running) {
            try {
                // Read data from the server until we finish reading the document
//                String line = inReader.readln();
//                client.handle(line);
                List<Byte> bytes = new ArrayList<>();
                byte ch = inReader.readByte();

                if (ch == STX) {
                    int count = 0;
                    while (ch != ETX || (count < maxCount - 1)) {
                        System.out.println("count: " + count + " | maxCount: " + maxCount);
                        if (count == 1) {
                            maxCount = ch == CMD_SERVER_OPE ? 10 : 5;
                        }
                        bytes.add(ch);
                        ch = inReader.readByte();
                        count++;
                    }

                    bytes.add(ch);
                    client.handle(getArray(bytes));
                }
            } catch (IOException ioe) {
                System.out.println("Listening error: " + ioe.getMessage());
                close();
                client.requestShutdown();
//                client.stop();
            }
        }
    }

    public void open()
    {
        try {
            inReader = new DataInputStream(socket.getInputStream());
//            inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            running = true;
        } catch (IOException ioe) {
            System.out.println("Error getting input stream: " + ioe);
            client.requestShutdown();
//            client.stop();
        }
    }

    public void close()
    {
        try {
            if (inReader != null) inReader.close();
            running = false;
        } catch (IOException ioe) {
            System.out.println("Error closing input stream: " + ioe);
        }
    }

    public byte[] getArray(List<Byte> list)
    {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }

        return bytes;
    }
}
