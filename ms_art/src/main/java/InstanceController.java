import org.imageGenerationCloud.requestmanagement.msutils.MsCommConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class InstanceController {
    public static int createdInstancesCount=0;

    public static void checkToTerminate(long startTimeInMillis, long terminationMillisForEmptyQueue) {
        if (startTimeInMillis!=-1 && System.currentTimeMillis() - startTimeInMillis >= terminationMillisForEmptyQueue) {
            System.out.println("Instance is being terminated.");
            System.exit(0);
        }
    }

    public static void checkToCreateInstance(String masterIP,int masterPort,int maxInstancesCount) {
        try{
            if (createdInstancesCount < maxInstancesCount) {
                System.out.println("creating instance");
                int instancePort = masterPort+createdInstancesCount+1; //.//out/artifacts/ms_art_jar/ms_art.jar
                //args example for an instance: "IP-10.100.3.5 TCP_PORT-1234 UDP_PORT-1235 IsMasterInstance=true"
                ProcessBuilder msBuilder = new ProcessBuilder("java", "-jar", System.getProperty("user.dir")  +"/out/artifacts/ms_art_jar/ms_art.jar",
                        "IP-"+ masterIP,"TCP_PORT-"+instancePort,"IsMasterInstance-"+ false);
                Process microserviceInstance = msBuilder.start();
                //source begin: https://www.geeksforgeeks.org/java-lang-processbuilder-class-java/
                BufferedReader stdInput
                        = new BufferedReader(new InputStreamReader(
                        microserviceInstance.getInputStream()));
                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    System.out.println(s);
                }
                //source end
                createdInstancesCount++;
                System.out.println("instance created");
            }
        }
        catch(Exception ex){

        }

    }

}
