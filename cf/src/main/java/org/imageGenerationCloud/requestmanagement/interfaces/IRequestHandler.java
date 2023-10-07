package org.imageGenerationCloud.requestmanagement.interfaces;

public interface IRequestHandler<T> {
    void receive(T request) throws Exception;
}