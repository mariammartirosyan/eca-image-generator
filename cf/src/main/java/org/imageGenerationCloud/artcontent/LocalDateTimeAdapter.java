package org.imageGenerationCloud.artcontent;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.LocalDateTime;

// implemented based on https://www.novixys.com/blog/using-jaxb-java-xml-conversion/
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    @Override
    public LocalDateTime unmarshal(String string) throws Exception {
            return LocalDateTime.parse(string);
    }

    @Override
    public String marshal(LocalDateTime dateTime) throws Exception {
        return dateTime == null ? null : dateTime.toString();
    }
}
