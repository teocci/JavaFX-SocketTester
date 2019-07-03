package com.github.teocci.socket.test;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-Jul-03
 */
public class BinaryOperations
{
    public static void main(String[] args)
    {
        System.out.println("\nRun setLong():");
        byte[] buffer = new byte[4];
        buffer[0] = (byte) 24;
        buffer[1] = (byte) 152;
        buffer[2] = (byte) 96;
        buffer[3] = (byte) 234;
        setLong(buffer, 5200, 0, 4);
        printBuffer(buffer);

        System.out.println("\nRun writeInt():");

        buffer[0] = (byte) 24;
        buffer[1] = (byte) 152;
        buffer[2] = (byte) 96;
        buffer[3] = (byte) 234;
        writeInt(buffer, 5200, 0);
        printBuffer(buffer);

        System.out.println("\nRun writeInt24():");
        buffer[0] = (byte) 24;
        buffer[1] = (byte) 152;
        buffer[2] = (byte) 96;
        buffer[3] = (byte) 234;
        writeInt24(buffer, 5200, 0);
        printBuffer(buffer);

        byte forbiddenZeroBit = (byte) 0x80;

        buffer[0] = (byte) (forbiddenZeroBit & 0xFF);
        buffer[1] = (byte) ((forbiddenZeroBit | (49 << 1)) & 0xFF);
        buffer[2] = g(byte) ((1) & 0xFF);
        buffer[3] = (byte) 234;

        System.out.println("\n8-bit header:");
        printBuffer(buffer);
    }

    public static void setLong(byte[] buffer, long n, int begin, int end)
    {
        for (end--; end >= begin; end--) {
            buffer[end] = (byte) (n % 256);
            n >>= 8;
        }
    }

    public static void writeInt(byte[] buffer, int n, int outOffset)
    {
        buffer[outOffset] = (byte) (n & 0xFF);
        buffer[outOffset + 1] = (byte) ((n >>> 8) & 0xFF);
        buffer[outOffset + 2] = (byte) ((n) >>> 16 & 0xFF);
        buffer[outOffset + 3] = (byte) (n >> 24);
    }

    public static void writeInt24(byte[] buffer, int n, int off)
    {
        buffer[off++] = (byte) (n >> 24);
        buffer[off++] = (byte) (n >> 16);
        buffer[off++] = (byte) (n >> 8);
        buffer[off] = (byte) n;
    }

    public static void printBuffer(byte[] buffer)
    {
        printBuffer(buffer, 8);
    }

    public static void printBuffer(byte[] buffer, int length)
    {
        for (byte num : buffer) {
            printBynary(num, length);
        }
    }
    public static void printBynary(byte num)
    {
        printBynary(num, 8);
    }

    public static void printBynary(byte num, int length)
    {
        int aux = Byte.toUnsignedInt(num);
        String binary = String.format("%" + length + 's', Integer.toBinaryString(aux)).replace(' ', '0');
        System.out.println(binary);
    }

    public static void printBufferBinaryFormat(byte[] buffer)
    {
        for (byte n : buffer) {
            String binary = String.format("%8s", Integer.toBinaryString(n & 0xFF)).replace(' ', '0');
            System.out.println(binary);
        }
    }

    public static void printBufferBinary(byte[] buffer)
    {
        for (byte num : buffer) {
            System.out.println(getByteBinaryString(num, 16));
        }
    }

    public static String getByteBinaryString(byte b, int length)
    {
        StringBuilder sb = new StringBuilder();
        int n = length - 1;
        int aux = Byte.toUnsignedInt(b);
        for (int i = n; i >= 0; --i) {
            sb.append(aux >>> i & 1);
        }
        return sb.toString();
    }
}
