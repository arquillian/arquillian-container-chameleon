package org.arquillian.container.chameleon;


import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.InputStream;

public class FileUtilsTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void should_not_load_configuration_file() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Could not find built-in configuration as file nor classloader resource: containers.yaml. Make sure that this file exists in classpath resource or in the project folder.");

        FileUtils.loadConfiguration("containers.yaml", true);
    }

    @Test
    public void should_load_configuration_file() {
        final InputStream inputStream = FileUtils.loadConfiguration("chameleon/default/containers.yaml", true);

        Assertions.assertThat(inputStream).isNotNull();
    }


}
