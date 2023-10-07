import config.Config;
import middleware.ArtInvoker;
import middleware.MsArtCommConfigHelper;
import org.imageGenerationCloud.enums.EMSTypes;
import org.imageGenerationCloud.exceptions.IpRangeException;
import org.imageGenerationCloud.requestmanagement.StartArgumentsManager;
import org.imageGenerationCloud.requestmanagement.addresses.LocalAddressInfo;
import org.imageGenerationCloud.requestmanagement.msutils.MsCommConfig;

import BLL.GeometricImageService;

import java.io.IOException;

public class Main{

    public static void main(String[] args)  {
        try{
           StartArgumentsManager.setValues(args);
            //LocalAddressInfo.IP = "10.100.3.5";
           //LocalAddressInfo.TCP_PORT = 1105;

            var isMasterInstance = StartArgumentsManager.isMasterInstance;

            Config config = new Config();
            var startTimeInMillis = System.currentTimeMillis();
            var terminationMillisForEmptyQueue = (long) config.getTerminationMinutesForEmptyQueue() * 60 * 1000;
            var maxInstancesCount = config.getMaxInstancesCount();

            new MsCommConfig(new MsArtCommConfigHelper(), EMSTypes.ART);

            while(true){
                if(!isMasterInstance){
                    InstanceController.checkToTerminate(startTimeInMillis,terminationMillisForEmptyQueue);
                }

                if(MsCommConfig.tcpRequestQueue.size()>0){
                    startTimeInMillis = -1;
                    if (MsCommConfig.tcpRequestQueue.size() >= config.getMaxRequestsCount()) {

                        try{
                            if(isMasterInstance){
                                InstanceController.checkToCreateInstance(LocalAddressInfo.IP,LocalAddressInfo.TCP_PORT,maxInstancesCount);
                            }

                        }
                        catch(Exception ex){

                        }
                    }
                }
                else {
                    startTimeInMillis = System.currentTimeMillis();
                }
            }
        }
        catch(Exception ex){
            System.out.print(ex.getMessage());
        }
    }

}