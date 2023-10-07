package org.imageGenerationCloud.requestmanagement;

import org.imageGenerationCloud.requestmanagement.addresses.LocalAddressInfo;

public class StartArgumentsManager {
    public static boolean isMasterInstance;
    public static void setValues(String[] args){
        //args example for an instance: "IP-10.100.3.5 TCP_PORT-1234 UDP_PORT-1235 IsMasterInstance-true"
        for(String arg: args){
            String[] parts = arg.split("-");
            switch(parts[0]){
                case "IP":
                    LocalAddressInfo.IP = parts[1];
                    break;
                case "TCP_PORT":
                    LocalAddressInfo.TCP_PORT = Integer.parseInt(parts[1]);
                    break;
                case "UDP_PORT":
                    LocalAddressInfo.UDP_PORT = Integer.parseInt(parts[1]);
                    break;
                case "IsMasterInstance":
                    isMasterInstance = Boolean.parseBoolean(parts[1]);
                    break;
            }

        }
    }
}
