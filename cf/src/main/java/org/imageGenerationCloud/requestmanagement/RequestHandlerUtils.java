package org.imageGenerationCloud.requestmanagement;

import org.imageGenerationCloud.requestmanagement.addresses.SenderAddress;

import java.util.Arrays;

public class RequestHandlerUtils {

    // implemented based on: https://gist.github.com/nicholascloud/4152377
    public static byte[] pruneByteArray(byte[] bytes) {
        if (bytes.length == 0) return bytes;

        int i = bytes.length - 1;
        while (bytes[i] == 0) {
            i--;

            if (i == -1) return bytes;
        }

        return Arrays.copyOf(bytes, i + 1);
    }

    public static SenderAddress parseAddress(String address) {
        String[] parsedAddress = address.split(":");
        return new SenderAddress(parsedAddress[0], Integer.parseInt(parsedAddress[1]), parsedAddress[2]);
    }
}
