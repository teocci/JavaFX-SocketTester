package com.github.teocci.socket.model;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-Jun-10
 */
public class POLine
{
    private String[] data;

    public static class Builder
    {
        private int lineNumber;
        private int lineQuantity;
        private int lineVnpkQty;

        Builder() {}

        public Builder poLineNumber(int lineNumber)
        {
            this.lineNumber = lineNumber;
            return this;  // By returning the builder each time, we can create a fluent interface.
        }

        public Builder quantity(int lineQuantity)
        {
            this.lineQuantity = lineQuantity;
            return this;
        }

        public Builder vnpkQty(int lineVnpkQty)
        {
            this.lineVnpkQty = lineVnpkQty;
            return this;
        }

        public POLine build()
        {
            // Here we create the actual bank account object, which is always in a fully initialised state when it's returned.
            POLine poLine = new POLine(this.lineNumber + " " + this.lineQuantity + " " + this.lineVnpkQty);

            return poLine;
        }
    }

    private POLine() {}

    public POLine(String line)
    {
        data = line.split(" ");
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public int getPurchaseReferenceLineNumber()
    {
        return Integer.parseInt(data[0]);
    }


    public int getExpectedQty()
    {
        return Integer.parseInt(data[1]);
    }

    public int getVnpkQty()
    {
        return Integer.parseInt(data[2]);
    }

    @Override
    public String toString()
    {
        return "POLine {" + data[0] + " | " + data[1] + " | " + data[2] + '}';
    }
}
