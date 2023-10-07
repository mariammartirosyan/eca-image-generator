package org.imageGenerationCloud;

import org.imageGenerationCloud.exceptions.IpRangeException;
import org.imageGenerationCloud.requestmanagement.CfCommConfig;
import org.imageGenerationCloud.requestmanagement.StartArgumentsManager;
import org.imageGenerationCloud.requestmanagement.addresses.LocalAddressInfo;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, IpRangeException {

        StartArgumentsManager.setValues(args);

        new CfCommConfig();
    }
}