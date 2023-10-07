package org.imageGenerationCloud.marshaller;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.imageGenerationCloud.messages.Message;

import java.io.StringReader;
import java.io.StringWriter;

public class Marshaller {
    public static String marshal(Message message) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
            jakarta.xml.bind.Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(jakarta.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter marshallerStringWriter = new StringWriter();
            jaxbMarshaller.marshal(message, marshallerStringWriter);
            return marshallerStringWriter.toString();
        }
        catch (JAXBException e) {
            System.out.println("Marshalling error: " + e.getMessage());
            return null;
        }
    }

    public static Message unmarshal(String message) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (Message) jaxbUnmarshaller.unmarshal(new StringReader(message));
        } catch (JAXBException e) {
            System.out.println("Unmarshalling error: " + e.getMessage());
            return null;
        }
    }
}
