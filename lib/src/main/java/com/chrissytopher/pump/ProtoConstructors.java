package com.chrissytopher.pump;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import com.chrissytopher.pump.proto.PumpMessage.Address;
import com.chrissytopher.pump.proto.PumpMessage.Attachment;
import com.chrissytopher.pump.proto.PumpMessage.EncryptedMessage;
import com.chrissytopher.pump.proto.PumpMessage.EncryptedMessageType;
import com.chrissytopher.pump.proto.PumpMessage.ID;
import com.chrissytopher.pump.proto.PumpMessage.Message;
import com.chrissytopher.pump.proto.PumpMessage.Packet;
import com.chrissytopher.pump.proto.PumpMessage.PacketType;
import com.chrissytopher.pump.proto.PumpMessage.Reaction;
import com.chrissytopher.pump.proto.PumpMessage.ReadReceipt;
import com.chrissytopher.pump.proto.PumpMessage.ReadReceiptType;
import com.google.protobuf.ByteString;

public class ProtoConstructors {
    public static String selfAddress;

    public static Message Message(String text, String recipients[]) {
        Message.Builder messageBuilder = Message.newBuilder();
        if (selfAddress != null) {
            messageBuilder.setSender(Address(selfAddress));
        }

        Address[] addresses = Addresses(recipients);
        messageBuilder.addAllRecipients(Arrays.asList(addresses));
        messageBuilder.setText(text);
        
        UUID id = UUID.randomUUID();
        messageBuilder.setMessageId(ID.newBuilder().setId(id.toString()));
        messageBuilder.setSentTimestamp(System.currentTimeMillis());

        return messageBuilder.build();
    }

    public static Message Message(String text, String recipients[], Attachment attachments[]) {
        Message.Builder messageBuilder = Message.newBuilder();
        if (selfAddress != null) {
            messageBuilder.setSender(Address(selfAddress));
        }

        Address[] addresses = Addresses(recipients);
        messageBuilder.addAllRecipients(Arrays.asList(addresses));
        messageBuilder.addAllAttachments(Arrays.asList(attachments));
        messageBuilder.setText(text);

        UUID id = UUID.randomUUID();
        messageBuilder.setMessageId(ID.newBuilder().setId(id.toString()));
        messageBuilder.setSentTimestamp(System.currentTimeMillis());

        return messageBuilder.build();
    }

    public static Message Message(String text, String recipients[], String reply) {
        Message.Builder messageBuilder = Message.newBuilder();
        if (selfAddress != null) {
            messageBuilder.setSender(Address(selfAddress));
        }

        Address[] addresses = Addresses(recipients);
        messageBuilder.addAllRecipients(Arrays.asList(addresses));
        messageBuilder.setReplyTo(ID(reply));
        messageBuilder.setText(text);

        UUID id = UUID.randomUUID();
        messageBuilder.setMessageId(ID(id.toString()));
        messageBuilder.setSentTimestamp(System.currentTimeMillis());

        return messageBuilder.build();
    }

    public static Message Message(String text, String recipients[], String id, long timestamp) {
        Message.Builder messageBuilder = Message.newBuilder();
        if (selfAddress != null) {
            messageBuilder.setSender(Address(selfAddress));
        }

        Address[] addresses = Addresses(recipients);
        messageBuilder.addAllRecipients(Arrays.asList(addresses));
        messageBuilder.setText(text);
        
        messageBuilder.setMessageId(ID(id));
        messageBuilder.setSentTimestamp(timestamp);

        return messageBuilder.build();
    }

    public static ID ID(String id) {
        return ID.newBuilder().setId(id).build();
    }

    public static Address[] Addresses(String[] recipients) {
        ArrayList<Address> addresses = new ArrayList<>();

        for (int i = 0; i < recipients.length; i++) {
            addresses.add(Address(recipients[i]));
        }

        return addresses.toArray(new Address[] {});
    }

    public static Address Address(String address) {
        return Address.newBuilder().setAddress(address).build();
    }

    public static Attachment Attachment(String url, String mimeType, String fileName) {
        return Attachment.newBuilder().setUrl(url).setMimeType(mimeType).setFileName(fileName).build();
    }

    public static EncryptedMessage EncryptedMessage(Message message) throws IOException {
        EncryptedMessage.Builder builder = EncryptedMessage.newBuilder();
        builder.setMessageBytes(ByteString.copyFrom(Serialization.serializeMessage(message)));
        //encryption is not actually supported yet, curse you Malicious Malory!
        builder.setType(EncryptedMessageType.NotEncrypted);

        return builder.build();
    }

    public static Packet MessagePacket(EncryptedMessage message) {
        Packet.Builder packetBuilder = BasicPacket();

        packetBuilder.setType(PacketType.MessagePacket);
        packetBuilder.setUserMessage(message);

        return packetBuilder.build();
    }

    public static Reaction Reaction(String message_id, String reaction) {
        Reaction.Builder reactionBuilder = Reaction.newBuilder();
        reactionBuilder.setMessageId(ID(message_id));
        reactionBuilder.setReaction(reaction);

        return reactionBuilder.build();
    }

    public static Packet ReactionPacket(Reaction reaction) {
        Packet.Builder packetBuilder = BasicPacket();

        packetBuilder.setType(PacketType.ReactionPacket);
        packetBuilder.setReaction(reaction);

        return packetBuilder.build();
    }

    public static ReadReceipt ReadReceipt(String message_id, ReadReceiptType type) {
        ReadReceipt.Builder reactionBuilder = ReadReceipt.newBuilder();
        reactionBuilder.setMessageId(ID(message_id));
        reactionBuilder.setType(type);

        return reactionBuilder.build();
    }

    public static Packet ReadReceiptPacket(ReadReceipt readReceipt) {
        Packet.Builder packetBuilder = BasicPacket();
        
        packetBuilder.setType(PacketType.ReadReceiptPacket);
        packetBuilder.setReadReceipt(readReceipt);

        return packetBuilder.build();
    }

    public static Packet.Builder BasicPacket() {
        Packet.Builder packetBuilder = Packet.newBuilder();
        UUID id = UUID.randomUUID();
        packetBuilder.setPacketId(ID(id.toString()));
        if (selfAddress == null) {
            throw new NullPointerException("self address is null");
        } else {
            packetBuilder.setSender(Address(selfAddress));
        }
        return packetBuilder;
    }
}