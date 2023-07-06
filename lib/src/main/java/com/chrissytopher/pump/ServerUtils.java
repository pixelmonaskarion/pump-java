package com.chrissytopher.pump;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import com.chrissytopher.pump.proto.PumpMessage.Attachment;
import com.google.common.io.Files;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerUtils {
    public static Attachment PostAttachment(String url, File attachment) throws IOException {
        String uuid = UUID.randomUUID().toString();
        OkHttpClient client = new OkHttpClient();
        byte[] attachmentBytes = Files.asByteSource(attachment).read();
        String mimeType = java.nio.file.Files.probeContentType(attachment.toPath());
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder();
        MultipartBody requestBody = requestBodyBuilder.setType(MultipartBody.FORM)
            .addFormDataPart(
                "file", attachment.getName(),
                RequestBody.create(attachmentBytes, MediaType.get(mimeType))
            )
            .addFormDataPart("uuid", uuid)
            .addFormDataPart("extension", Files.getFileExtension(attachment.getName()))
            .build();
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder
                //no one use my ec2 for web storage lol
            .url(url+"/post-attachment")
            .post(requestBody)
            .build();
        try (Response response = client.newCall(request).execute()) {
            return ProtoConstructors.Attachment(url+"/"+uuid+"."+Files.getFileExtension(attachment.getName()), mimeType, attachment.getName());
        }
    }
}
