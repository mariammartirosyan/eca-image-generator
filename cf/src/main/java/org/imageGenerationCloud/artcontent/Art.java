package org.imageGenerationCloud.artcontent;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDateTime;

@XmlRootElement(name = "ArtNode")
@XmlAccessorType(XmlAccessType.NONE)
public class Art {
    @XmlElement(name = "id")
    private int id;
    @XmlElement(name = "artFile")
    private String artFile;
    @XmlElement(name = "storageTimestamp")
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime storageTimestamp;
    @XmlElement(name = "accountID")
    private int accountID;
    @XmlElement(name = "width")
    private int width;
    @XmlElement(name = "height")
    private int height;
    @XmlElement(name = "simulatedGenCount")
    private int simulatedGenCount;
    @XmlElement(name = "imageType")
    private String imageType;

    public Art() {
    }

    public Art(int id, LocalDateTime storageTimestamp, int accountID,
               int width, int height, int simulatedGenCount,
               String imageType, String artFile) {
        this.id = id;
        this.storageTimestamp = storageTimestamp;
        this.accountID = accountID;
        this.width = width;
        this.height = height;
        this.simulatedGenCount = simulatedGenCount;
        this.imageType = imageType;
        this.artFile = artFile;
    }

    public int getId() {
        return id;
    }

    public String getArtFile() {
        return artFile;
    }

    public LocalDateTime getStorageTimestamp() {
        return storageTimestamp;
    }

    public int getAccountID() {
        return accountID;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSimulatedGenCount() {
        return simulatedGenCount;
    }

    public String getImageType() {
        return imageType;
    }
}
