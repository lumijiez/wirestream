package io.github.lumijiez.core.ws;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WebSocketFrame {
    private boolean fin;
    private byte opcode;
    private byte[] payload;
    private boolean masked;

    public static WebSocketFrame read(InputStream in) throws IOException {
        WebSocketFrame frame = new WebSocketFrame();

        int firstByte = in.read();
        if (firstByte == -1) return null;

        frame.fin = (firstByte & 0x80) != 0;
        frame.opcode = (byte)(firstByte & 0x0F);

        int secondByte = in.read();
        if (secondByte == -1) return null;

        frame.masked = (secondByte & 0x80) != 0;
        int payloadLength = secondByte & 0x7F;

        if (payloadLength == 126) {
            payloadLength = (in.read() << 8) | in.read();
        } else if (payloadLength == 127) {
            throw new IOException("Payload length too large");
        }

        byte[] maskingKey = new byte[4];
        if (frame.masked) {
            int bytesRead = in.read(maskingKey);
            if (bytesRead != 4) return null;
        }

        frame.payload = new byte[payloadLength];
        int bytesRead = in.read(frame.payload);
        if (bytesRead != payloadLength) return null;

        if (frame.masked) {
            for (int i = 0; i < frame.payload.length; i++) {
                frame.payload[i] ^= maskingKey[i % 4];
            }
        }

        return frame;
    }

    public void write(OutputStream out) throws IOException {
        int firstByte = (fin ? 0x80 : 0x00) | (opcode & 0x0F);
        out.write(firstByte);

        if (payload.length < 126) {
            out.write(payload.length);
        } else if (payload.length < 65536) {
            out.write(126);
            out.write(payload.length >> 8);
            out.write(payload.length & 0xFF);
        } else {
            throw new IOException("Payload too large");
        }

        // Write payload
        out.write(payload);
        out.flush();
    }

    public void setFin(boolean fin) {
        this.fin = fin;
    }

    public void setOpcode(int opcode) {
        this.opcode = (byte) opcode;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public byte getOpcode() {
        return opcode;
    }

    public byte[] getPayload() {
        return payload;
    }
}