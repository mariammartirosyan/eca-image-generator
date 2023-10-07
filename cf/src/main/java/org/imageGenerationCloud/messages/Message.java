package org.imageGenerationCloud.messages;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "messageNode")
@XmlAccessorType(XmlAccessType.NONE)
public class Message {
    @XmlElement(name = "targetMS")
    private String targetMS;
    @XmlElement(name = "senderAddress")
    private String senderAddress;
    @XmlElement(name = "messageContent")
    private MessageContent messageContent;
    @XmlElement(name = "errorMessage")
    private String errorMessage;

    public String getTargetMS() {
        return targetMS;
    }
    public String getSenderAddress() {
        return senderAddress;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public MessageContent getMessageContent() {
        return messageContent;
    }

    public Message() {

    }

    public Message(String targetMS, String senderAddress, String errorMessage, MessageContent messageContent) {
        this.targetMS = targetMS;
        this.senderAddress = senderAddress;
        this.errorMessage = errorMessage;
        this.messageContent = messageContent;
    }
}
