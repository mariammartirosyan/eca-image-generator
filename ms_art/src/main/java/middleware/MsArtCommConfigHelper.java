package middleware;

import org.imageGenerationCloud.requestmanagement.msutils.IMsCommConfigHelper;

public class MsArtCommConfigHelper implements IMsCommConfigHelper {

    public Runnable getInvoker(){
        return new ArtInvoker();
    }
}