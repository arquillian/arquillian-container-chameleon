package org.arquillian.container.chameleon.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class IOUtils {

    public static String asStringPreservingNewLines(InputStream response) {
        StringWriter logwriter = new StringWriter();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                logwriter.write(line);
                logwriter.write(System.lineSeparator());
            }

            return logwriter.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
