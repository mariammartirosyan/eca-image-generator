package org.imageGenerationCloud.requestmanagement.interfaces;

import org.imageGenerationCloud.requestmanagement.tasks.RequestorTask;

import java.io.IOException;

public interface IRequestor {
    void send(RequestorTask task) throws IOException;
}
