package com.github.teocci.socket.net;

import com.github.teocci.socket.interfaces.ShutdownRequester;
import com.github.teocci.socket.utils.Common;
import com.github.teocci.socket.utils.UInt16;
import javafx.application.Application.Parameters;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import static com.github.teocci.socket.utils.Random.bernoulli;
import static com.github.teocci.socket.utils.Random.uniform;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-Jan-04
 */
public class TcpClient implements Runnable
{
    /**
     * Maximum possible value.
     */
    private static final int MAX_INDEX = 11;
    /**
     * Minimum possible value.
     */
    private static final int MIN_INDEX = 0;

    private final int PORT = 9090;
    private final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private final byte EMPTY = 0x00;

    public final static byte STX = 0x02;
    public final static byte ETX = 0x03;

    public final static int PAR = 0;
    public final static int DIO = 1;
    public final static int VOC = 2;
    public final static int RAD = 3;
    public final static int ANO = 4;
    public final static int ALA = 5;

    public final static byte STAGE_INIT = 0x01;
    public final static byte STAGE_UPDT = 0x02;

    public final static byte CMD_CLIENT_INIT = 0x01;
    public final static byte CMD_CLIENT_UPDT = 0x02;
    public final static byte CMD_CLIENT_RESP = 0x03;
    public final static byte CMD_CLIENT_BYE = 0x05;

    public final byte CMD_SERVER_REA = -122;
    public final byte CMD_SERVER_FIL = -127;
    public final byte CMD_SERVER_UPD = -126;
    public final byte CMD_SERVER_OPE = -125;
    public final byte CMD_SERVER_BYE = -124;

    public final int CMD_SERVER_REA_SIZE = 5;
    public final int CMD_SERVER_FIL_SIZE = 5;
    public final int CMD_SERVER_UPD_SIZE = 5;
    public final int CMD_SERVER_OPE_SIZE = 10;
    public final int CMD_SERVER_BYE_SIZE = 5;


    private final byte RES_SERVER_FILE_OK = -95;
    private final byte RES_SERVER_FILE_RETRY = -94;

    private volatile byte stage = STAGE_INIT;
    private boolean firstUpdate = true;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private PrintStream outWriter;
    //    private DataOutputStream outWriter;
//    private DataOutputStream streamOut = null;

    private Socket socket = null;
    private Thread thread = null;
    private TcpClientThread client = null;

    private byte[] operations = new byte[]{
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    private byte[] alarms = new byte[]{
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    private byte region = 42;
    private byte type = 1;
    private short code = 8668;

    private ShutdownRequester requester;

    public TcpClient(String server, ShutdownRequester requester, Parameters params)
    {
        this.requester = requester;

        List<String> rawParams = params.getRaw();
        if (!rawParams.isEmpty() && rawParams.size() == 3) {
            region = Byte.valueOf(rawParams.get(0));
            type = Byte.valueOf(rawParams.get(1));
            code = Short.valueOf(rawParams.get(2));
        }

        System.out.println("region, type, code: " + region + ", " + type + ", " + code);

        System.out.println("Establishing connection. Please wait ...");
        try {
            // Connect to the server
            socket = new Socket(server, PORT);
            System.out.println("Connected: " + socket);
            start();
        } catch (UnknownHostException uhe) {
            System.out.println("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            System.out.println("Unexpected exception: " + ioe.getMessage());
        }
    }

    public void run()
    {
        while (thread != null) {
//            try {
//                streamOut.write('\n');
//                streamOut.flush();
//            } catch (IOException ioe) {
//                System.out.println("Sending error: " + ioe.getMessage());
//                stop();
//            }
        }
    }

    public void start() throws IOException
    {
        outWriter = new PrintStream(socket.getOutputStream());
//        streamOut = new DataOutputStream(socket.getOutputStream());
        if (thread == null) {
            client = new TcpClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    public void requestShutdown()
    {
        if (requester != null) requester.onConnectionLost();
    }

    public void stop()
    {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        try {
            if (outWriter != null) outWriter.close();
            if (socket != null) socket.close();
        } catch (IOException ioe) {
            System.out.println("Error closing ...");
        }
        client.close();
        client.interrupt();
    }

    public void handle(byte[] bytes)
    {
        if (bytes == null) return;

        System.out.println(bytesToHex(bytes));

        byte firstByte = bytes[0];
        byte cmd = bytes[1];

        if (firstByte == STX) {
            System.out.println("CMD: " + cmd);
            switch (cmd) {
                case CMD_SERVER_REA:
                    initClient();
                    break;
                case CMD_SERVER_FIL:
                    byte response = bytes[2];
                    if (response == RES_SERVER_FILE_OK) {
                        stage = STAGE_UPDT;
                    }
                    break;
                case CMD_SERVER_UPD:
                    if (stage == STAGE_UPDT) {
                        System.out.println("CMD_SERVER_UPD: OK");
                    }
                    break;
                case CMD_SERVER_OPE:
                    if (stage == STAGE_UPDT) {
                        byte gwId = bytes[2];
                        byte startERV = bytes[3];
                        byte endERV = bytes[4];

                        System.out.println("startERV: " + startERV);

                        if (startERV == endERV) {
                            int index = getIndex(startERV);
                            if (index > -1) {
                                operations[index] = bytes[5];
                            }
                        }

                        controlResponse(gwId, startERV, endERV);
                        System.out.println("CMD_SERVER_OPE: SENDED");
                    }
                    break;
                case CMD_SERVER_BYE:
                    initClient();
                    break;
            }
        }
    }

    private int getIndex(byte value)
    {
        return value > MIN_INDEX && value < MAX_INDEX + 1 ? value - 1 : -1;
    }

    private void initClient()
    {
        try {
            File file = Common.getFileFromJar("/csv/TAG.CSV");
            if (file == null) return;

            addHeader(CMD_CLIENT_INIT);

            int size = (int) file.length();
            byte[] fileSize = intTo24Bits(size);

            System.out.println(size);
//            System.out.println(bytesToHex(fileSize));

            outputStream.write(fileSize[0]);
            outputStream.write(fileSize[1]);
            outputStream.write(fileSize[2]);
            outputStream.write(EMPTY);
            outputStream.write(EMPTY);
            outputStream.write(EMPTY);
            outputStream.write(EMPTY);
            outputStream.write(EMPTY);
            outputStream.write(ETX);

            sendBuffer(outputStream.toByteArray());
            outputStream.reset();

            byte[] fileData = new byte[size];
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            int sizeRead = bis.read(fileData, 0, fileData.length);
            if (sizeRead != -1) {
                System.out.println("Start sending (" + sizeRead + " bytes)");
                outWriter.write(fileData, 0, fileData.length);
                outWriter.flush();
                System.out.println("Done.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void controlResponse(byte gwId, byte startERV, byte endERV)
    {
        try {
            addHeader(CMD_CLIENT_RESP);

            outputStream.write(gwId);
            outputStream.write(startERV);
            outputStream.write(endERV);
            outputStream.write(ETX);

            sendBuffer(outputStream.toByteArray());
            outputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handle(String line)
    {
        System.out.println("Line: " + line + " | Length: " + line.length());
        byte[] bytes = getBytes(line);
        if (bytes == null) return;

        System.out.println(bytesToHex(bytes));

        byte firstByte = bytes[0];
        byte cmd = bytes[1];

        if (firstByte == STX) {
            switch (cmd) {
                case STX:
                    break;
            }
        }
    }

    public void sendUpdate()
    {
        if (stage == STAGE_UPDT) {
            try {
                addHeader(CMD_CLIENT_UPDT);

                byte gwId = 1;
                byte ervTotal = MAX_INDEX;
                outputStream.write(gwId);
                outputStream.write(ervTotal);

                for (int i = 0; i < ervTotal; i++) {
                    byte ervId = (byte) (i + 1);
                    byte operation = operations[i];
                    byte a02 = (byte) 45;
                    byte a03 = (byte) 12;

                    int particles = generateValue(PAR);
                    byte[] particlesBytes = intTo16Bits(particles);

                    int p02 = generateValue(ANO);
                    byte[] p02Bytes = intTo16Bits(p02);

                    int dioxide = generateValue(DIO);
                    byte[] dioxideBytes = intTo16Bits(dioxide);

                    int voc = generateValue(VOC);
                    byte[] vocBytes = intTo16Bits(voc);

                    int radon = generateValue(RAD);
                    byte[] radonBytes = intTo16Bits(radon);

                    byte alarm = standardize(i);

                    outputStream.write(ervId);
                    outputStream.write(operation);
                    outputStream.write(a02);
                    outputStream.write(a03);
                    outputStream.write(particlesBytes);
                    outputStream.write(p02Bytes);
                    outputStream.write(dioxideBytes);
                    outputStream.write(vocBytes);
                    outputStream.write(radonBytes);
                    outputStream.write(alarm);
                    outputStream.write(EMPTY);
                    outputStream.write(EMPTY);
                }

                outputStream.write(ETX);

                sendBuffer(outputStream.toByteArray());
                outputStream.reset();

                if (firstUpdate) firstUpdate = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendCommand()
    {

    }

    public void sendDisconnect()
    {
        try {
            addHeader(CMD_CLIENT_BYE);
            addByeBody();

            sendBuffer(outputStream.toByteArray());
            outputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addHeader(byte cmd)
    {
        byte[] codeBytes = intTo16Bits(code);

        System.out.println(code);

        outputStream.write(STX);
        outputStream.write(cmd);
        outputStream.write(region);
        outputStream.write(type);
        outputStream.write(codeBytes[0]);
        outputStream.write(codeBytes[1]);
    }

    private void sendBuffer(byte[] buffer) throws IOException
    {
        outWriter.write(buffer);
        outWriter.flush();
    }

    private int generateValue(int element)
    {
        int[] ranges;
        switch (element) {
            case PAR:
            case ANO:
                ranges = new int[]{5000, 10000, 25000};
                return standardize(ranges);
            case DIO:
                ranges = new int[]{6000, 13000, 26000};
                return standardize(ranges);
            case VOC:
                ranges = new int[]{13000, 26000, 40000};
                return standardize(ranges);
            case RAD:
                ranges = new int[]{2000, 10000, 26000};
                return standardize(ranges);
            default:
                return -1;
        }
    }

    private byte standardize(int index)
    {
        if (!firstUpdate) {
            if (alarms[index] == 0) {
                return (byte) (bernoulli(0.95) ? 0 : 1);
            } else {
                return (byte) (bernoulli(0.95) ? 1 : 0);
            }
        } else {
            return 0;
        }
    }

    private int standardize(int[] ranges)
    {
        if (!firstUpdate) {
            if (bernoulli(0.95)) {
                if (bernoulli(0.1)) {
                    return uniform(ranges[0]);
                } else {
                    return uniform(ranges[0], ranges[1]);
                }
            } else {
                if (bernoulli(0.90)) {
                    return uniform(ranges[1], ranges[2]);
                } else {
                    return uniform(ranges[2], UInt16.MAX_VALUE + 1);
                }
            }
        } else {
            return uniform(ranges[0]);
        }
    }

    private int normalize(double value, int[] ranges)
    {
        if (value < (ranges[0] + 1)) {
            return 0;
        } else if (value > ranges[0] && value < (ranges[1] + 1)) {
            return 1;
        } else if (value > ranges[1] && value < (ranges[2] + 1)) {
            return 2;
        } else if (value > ranges[2]) {
            return 3;
        } else {
            return -1;
        }
    }

    private void addByeBody()
    {
        outputStream.write(EMPTY);
        outputStream.write(EMPTY);
        outputStream.write(EMPTY);
        outputStream.write(ETX);
    }

    private byte[] intTo16Bits(int value)
    {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) ((value >> 8) & 0xFF);
        bytes[1] = (byte) (value & 0xFF);

        return bytes;
    }

    private byte[] intTo24Bits(int value)
    {
        byte[] bytes = new byte[3];
        bytes[0] = (byte) ((value >> 16) & 0xFF);
        bytes[1] = (byte) ((value >> 8) & 0xFF);
        bytes[2] = (byte) (value & 0xFF);

        return bytes;
    }

    private byte[] getBytes(String line)
    {
        if (line == null || line.isEmpty()) return null;

        byte[] bytes = new byte[line.length()];
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            bytes[i] = (byte) c;
            System.out.println("Byte: " + bytes[i] + " | Integer: " + (int) c + " | Index: " + i);
        }

        return bytes;
    }

    private String bytesToHex(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    private void printByte(byte cmd)
    {
        String s1 = String.format("%8s", Integer.toBinaryString(cmd & 0xFF)).replace(' ', '0');
        System.out.println(s1);
    }
}
