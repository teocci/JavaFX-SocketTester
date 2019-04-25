package com.github.teocci.socket.net;

import com.github.teocci.socket.interfaces.ShutdownRequester;
import com.github.teocci.socket.utils.Common;
import javafx.application.Application.Parameters;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import static com.github.teocci.socket.utils.Random.bernoulli;
import static com.github.teocci.socket.utils.Random.uniform;
import static com.github.teocci.socket.utils.UInt16.MAX_VALUE;

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
    private static final int MAX_INDEX = 49;
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
    public final static int DUS = 4;
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

    private final String CSV_FILE_NAME = "TAG4221-49.csv";
    private final String CSV_FILE_PATH = "/csv/" + CSV_FILE_NAME;

    private volatile byte stage = STAGE_INIT;
    private boolean firstUpdate = true;

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private PrintStream outWriter;
    //    private DataOutputStream outWriter;
//    private DataOutputStream streamOut = null;

    private Socket socket = null;
    private Thread thread = null;
    private TcpClientThread client = null;

    private byte[] operations = new byte[MAX_INDEX];
    private byte[] filterReplacementCycles = new byte[MAX_INDEX];

    private byte[] alarms = new byte[MAX_INDEX];


    private String server = "127.0.0.1";
    private byte region = 42;
    private byte type = 1;
    private short code = 8668;

    private ShutdownRequester requester;

    public TcpClient(Parameters params, ShutdownRequester requester)
    {
        if (params == null) return;
        if (requester == null) return;

        this.requester = requester;
        List<String> rawParams = params.getRaw();
        if (!rawParams.isEmpty() && rawParams.size() == 4) {
            server = rawParams.get(0).isEmpty() ? server : rawParams.get(0);
            region = Byte.valueOf(rawParams.get(1));
            type = Byte.valueOf(rawParams.get(2));
            code = Short.valueOf(rawParams.get(3));
        }

        Arrays.fill(operations, (byte) 0x00);
        Arrays.fill(filterReplacementCycles, (byte) 0x00);
        Arrays.fill(alarms, (byte) 0x00);

        System.out.println("Loading contents of URL: " + server);
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
                                filterReplacementCycles[index] = bytes[6];

                                System.out.println("Operation: " + operations[index] + "Filter Cycle: " + filterReplacementCycles[index]);
                            }
                        }

                        controlResponse(gwId, startERV, endERV);
                        System.out.println("CMD_SERVER_OPE: OK");
                    }
                    break;
                case CMD_SERVER_BYE:
                    System.out.println("CMD_SERVER_BYE: OK");
                    stop();
                    requestShutdown();
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
            File file = Common.getFileFromJar(CSV_FILE_PATH);
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
                    byte occupancy = (byte) 45;
                    byte filterReplacementCycle = filterReplacementCycles[i];

                    int particles = generateIndicator(PAR);
                    byte[] particlesBytes = intTo16Bits(particles);

                    int dust = generateIndicator(DUS);
                    byte[] dustBytes = intTo16Bits(dust);

                    int dioxide = generateIndicator(DIO);
                    byte[] dioxideBytes = intTo16Bits(dioxide);

                    int voc = generateIndicator(VOC);
                    byte[] vocBytes = intTo16Bits(voc);

                    int radon = generateIndicator(RAD);
                    byte[] radonBytes = intTo16Bits(radon);

                    alarms[i] = generateAlarm(i);
                    byte alarm = alarms[i];

                    outputStream.write(ervId);
                    outputStream.write(operation);
                    outputStream.write(occupancy);
                    outputStream.write(filterReplacementCycle);
                    outputStream.write(particlesBytes);
                    outputStream.write(dustBytes);
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

    private int generateIndicator(int element)
    {
        int[] ranges;
        switch (element) {
            case PAR:
            case DUS:
                ranges = new int[]{5000, 10000, 25000};
                return generateValue(ranges);
            case DIO:
                ranges = new int[]{6000, 13000, 26000};
                return generateValue(ranges);
            case VOC:
                ranges = new int[]{13000, 26000, 40000};
                return generateValue(ranges);
            case RAD:
                ranges = new int[]{2000, 10000, 26000};
                return generateValue(ranges);
            default:
                return -1;
        }
    }

    private byte generateAlarm(int index)
    {
        if (firstUpdate) return 0;

        if (alarms[index] == 0) {
            return bernoulli(0.99) ? alarms[index] : (byte) (1 + uniform(255));
        } else {
            return bernoulli(0.90) ? alarms[index] : 0;
        }
    }

    private int generateValue(int[] ranges)
    {
        if (firstUpdate) return uniform(ranges[0]);

        return bernoulli(0.95) ? (bernoulli(0.01) ? uniform(ranges[0]) : uniform(ranges[0], ranges[1])) :
                (bernoulli(0.95) ? uniform(ranges[1], ranges[2]) : uniform(ranges[2], MAX_VALUE + 1));
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
