package com.chrissytopher.pump;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.chrissytopher.pump.proto.PumpMessage.*;

public class Serialization {
    public static byte[] serializeMessage(Message message) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        message.writeTo(baos);
        return baos.toByteArray();
    }

    public static byte[] serializePacket(Packet packet) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        packet.writeTo(baos);
        return baos.toByteArray();
    }
}
