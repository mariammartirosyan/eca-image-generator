package org.imageGenerationCloud.communication;

import org.imageGenerationCloud.exceptions.IpRangeException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RangeCalculator {
    private final static int IGNORED_IP_ADDRESS = 3;
    private final List<String> ipList;
    private final List<String> portList;
    private final Properties properties;


    public RangeCalculator(String reader) throws IOException, IpRangeException {
        FileReader fileReader = new FileReader(reader);
        properties = new Properties();
        this.properties.load(fileReader);
        this.ipList = calculateIpRange();
        this.portList = calculatePortRange();
    }

    /**
     * @return Return a list with all possible ports which should be checked
     * @throws IpRangeException
     */
    private List<String> calculatePortRange() throws IpRangeException {
        String portRangeFrom = properties.getProperty("portRangeFrom");
        String portRangeTo = properties.getProperty("portRangeTo");

        if(Integer.parseInt(portRangeFrom) > Integer.parseInt(portRangeTo)) {
            throw new IpRangeException("Starting Port is not allowed to be greater than Ending Port range!");
        }

        List<String> ret = new ArrayList<>();

        for(int i = Integer.parseInt(portRangeFrom); i <= Integer.parseInt(portRangeTo); i++) {
            ret.add(Integer.toString(i));
        }
        return ret;
    }

    /**
     * @return Return a list with all possible ips which should be checked
     * @throws IpRangeException
     */
    private List<String> calculateIpRange() throws IpRangeException {
        String ipRangeFrom = properties.getProperty("ipRangeFrom");
        String ipRangeTo = properties.getProperty("ipRangeTo");
        String threeAddressNumbers = getThreeFirstAddressNumbers(ipRangeFrom);


        int fromIpRange = getLastAddressNumber(ipRangeFrom);
        int toIpRange = getLastAddressNumber(ipRangeTo);

        if(fromIpRange > toIpRange) {
            throw new IpRangeException("Starting IP is not allowed to be greater than Ending IP range!");
        }

        return generateRangeFromTo(threeAddressNumbers, fromIpRange, toIpRange);
    }

    /**
     * @param firstAddress
     * @param ipRangeFrom
     * @param ipRangeTo
     * @return List with range from the given ipRangeFrom to ipRangeTo
     */
    private List<String> generateRangeFromTo(String firstAddress, int ipRangeFrom, int ipRangeTo) {
        List<String> ret = new ArrayList<>();
        ret.add("10.100.6.13");
        for(int i = ipRangeFrom; i <= ipRangeTo; i++) {
            ret.add(firstAddress + i);
        }
        return ret;
    }

    /**
     * @param ip
     * @return first three address numbers of an IP
     */
    private String getThreeFirstAddressNumbers(String ip) {
        int counter = 0;
        String ret = "";

        for(char c : ip.toCharArray()) {
            if(counter < IGNORED_IP_ADDRESS) {
                ret += c;
            }
            if(c == '.' ) {
                counter++;
            }
        }
        return ret;
    }

    /**
     * @param ipRange
     * @return last address number of an IP
     */
    private int getLastAddressNumber(String ipRange) {

        int counter = 0;
        String ret = "";

        for(char c : ipRange.toCharArray()) {
            if(c == '.') {
                counter ++;
            } else if (counter == IGNORED_IP_ADDRESS) {
                ret += c;
            }
        }
        return Integer.parseInt(ret);
    }
    public List<String> getIpList() {
        return ipList;
    }

    public List<String> getPortList() {
        return portList;
    }
}
