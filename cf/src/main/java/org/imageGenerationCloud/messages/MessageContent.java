package org.imageGenerationCloud.messages;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.imageGenerationCloud.artcontent.Art;

import java.util.List;

/**
 *
 */
@XmlRootElement(name = "messageContentNode")
@XmlAccessorType(XmlAccessType.NONE)
public class MessageContent {
    @XmlElement(name = "operation")
    private String operation;
    @XmlElement(name = "art")
    private Art art;
    @XmlElement(name = "artList")
    private List<Art> artList;
    @XmlElement(name = "accountID")
    private int accountID;

    @XmlElement(name = "artId")
    private int artId;

    public MessageContent() {
    }

    public MessageContent(String operation, Art art, List<Art> artList, int accountID, int artId) {
        this.operation = operation;
        this.art = art;
        this.artList = artList;
        this.accountID = accountID;
        this.artId = artId;
    }

    public String getOperation() {
        return operation;
    }

    public Art getArt() {
        return art;
    }

    public List<Art> getArtList() {
        return artList;
    }

    public int getAccountID() {
        return accountID;
    }

    public int getArtId() {
        return artId;
    }
}
