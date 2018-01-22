package org.arquillian.container.chameleon.runner;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DomManipulation {

    public static Document createDocumentFromInputStream(InputStream inputStream) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(inputStream);
        } catch (ParserConfigurationException | SAXException | IOException parserException) {
            throw new IllegalArgumentException(parserException);
        }
    }

    public static Document createDocumentFromTemplate() {
        return createDocumentFromInputStream(Thread
            .currentThread().getContextClassLoader()
            .getResourceAsStream("arquillian_template.xml"));
    }

    public static Document createDocumentFromContainerTemplate() {
        return createDocumentFromInputStream(Thread
            .currentThread().getContextClassLoader()
            .getResourceAsStream("container_template.xml"));
    }

    public static void writeToPath(Document document, Path file) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(file.toFile());
        Source input = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(input, output);
    }
}
