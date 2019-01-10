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

    //    private BufferedReader inReader;
    private DataInputStream inReader;

    private Socket socket = null;
    private TcpClient client = null;

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
                    while (ch != ETX) {
                        bytes.add(ch);
                        ch = inReader.readByte();
                    }
                    bytes.add(ch);
                    client.handle(getArray(bytes));
                }
            } catch (IOException ioe) {
                System.out.println("Listening error: " + ioe.getMessage());
                close();
                client.stop();
            }
        }
//
//        try {
//            // Connect to the server
//            Socket socket = new Socket(server, PORT);
//
//            // Create input and output streams to read from and write to the server
//            outWriter = new PrintStream(socket.getOutputStream());
////            outWriter = new DataOutputStream(socket.getOutputStream());
//
//            File file = getFileFromURL("TAG.CSV");
//            long size = file.length();
//
//            int code = 8668;
//
//            byte[] codeBytes = new byte[2];
//            codeBytes[0] = (byte) ((code >> 8) & 0xFF);
//            codeBytes[1] = (byte) (code & 0xFF);
//
//            byte[] fileSize = new byte[3];
//            fileSize[0] = (byte) ((size >> 16) & 0xFF);
//            fileSize[1] = (byte) ((size >> 8) & 0xFF);
//            fileSize[2] = (byte) (size & 0xFF);
//
//
//            System.out.println(code);
//            System.out.println(size);
////            System.out.println(bytesToHex(fileSize));
//
//            byte[] buf;
//
//            byte region = 42;
//            byte type = 1;
//
//            outputStream.write(STX);
//            outputStream.write(CMD);
//            outputStream.write(region);
//            outputStream.write(type);
//            outputStream.write(codeBytes[0]);
//            outputStream.write(codeBytes[1]);
//            outputStream.write(fileSize[0]);
//            outputStream.write(fileSize[1]);
//            outputStream.write(fileSize[2]);
//            outputStream.write(EMPTY);
//            outputStream.write(EMPTY);
//            outputStream.write(EMPTY);
//            outputStream.write(EMPTY);
//            outputStream.write(EMPTY);
//            outputStream.write(ETX);
//
//            buf = outputStream.toByteArray();
//
//            outWriter.write(buf);
//            outWriter.flush();
//
//            byte[] fileData = new byte[(int) size];
//            FileInputStream fis = new FileInputStream(file);
//            BufferedInputStream bis = new BufferedInputStream(fis);
//
//            int sizeRead = bis.read(fileData, 0, fileData.length);
//            if (sizeRead != -1) {
//                System.out.println("Start sending (" + sizeRead + " bytes)");
//                outWriter.write(fileData, 0, fileData.length);
//                outWriter.flush();
//                System.out.println("Done.");
//            }
//
//
//            // Follow the HTTP protocol of GET <path> HTTP/1.0 followed by an empty line
////            out.println("GET " + path + " HTTP/1.0");
////            out.println();
//
//            // Read data from the server until we finish reading the document
//            String line = inReader.readLine();
//            while (line != null) {
//                System.out.println(line);
//                line = inReader.readLine();
//            }
//
//            // Close our streams
//            inReader.close();
//            outWriter.close();
//            socket.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void open()
    {
        try {
            inReader = new DataInputStream(socket.getInputStream());
//            inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            running = true;
        } catch (IOException ioe) {
            System.out.println("Error getting input stream: " + ioe);
            client.stop();
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
