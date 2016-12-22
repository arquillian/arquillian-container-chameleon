package org.arquillian.container.chameleon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.InitializationError;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
@NotThreadSafe
public class ManualSettingsXmlTestCase {

    // path to a target directory of this project
    private static String target = System.getProperty("user.dir") + "/target/";

    // settings.xml using repository located in src/test/resources/settings-repository/repository
    private static File dummySettingsXml = new File(target + "/test-classes/settings-repository/dummy-settings.xml");
    // arquillian.xml JBoss EAP:7.1.3.Final:managed
    private String arqXmlWithEap = target + "/test-classes/settings-repository/arquillian-with-eap.xml";
    // our dummy local repository
    private static File localRepo = new File(target + "/local-repository");

    // chameleon cache and download folders
    private static File chameleonDistributionDownloadFolder = new File(target + "/chameleonDistributionDownloadFolder");
    private static File chameleonResolveCacheFolder = new File(target + "/chameleonResolveCacheFolder");

    // previous properties
    private static String previousArqXmlProperty = "";
    private static String previousArqLaunchProperty = "";

    @BeforeClass
    public static void prepare() throws IOException {
        // replace %basedir% string (in settings.xml) with the real path to the base directory
        String content = IOUtils.toString(new FileInputStream(dummySettingsXml), "UTF-8");
        content = content.replaceAll("%basedir%", System.getProperty("user.dir"));
        IOUtils.write(content, new FileOutputStream(dummySettingsXml), "UTF-8");

        // store properties
        previousArqXmlProperty = System.getProperty("arquillian.xml");
        previousArqLaunchProperty = System.getProperty("arquillian.launch");
    }

    @Before
    public void cleanBeforeTest() throws IOException {
        cleanup();
    }

    @After
    public void cleanSystemProperties(){
        System.setProperty("org.apache.maven.global-settings", "");
        System.setProperty("org.apache.maven.user-settings", "");
    }

    public static void cleanup() throws IOException {
        FileUtils.deleteDirectory(localRepo);
        FileUtils.deleteDirectory(chameleonDistributionDownloadFolder);
        FileUtils.deleteDirectory(chameleonResolveCacheFolder);
    }

    @AfterClass
    public static void resetPropertiesAndDoCleanup() throws IOException {
        // set properties to previous values
        System.setProperty("arquillian.xml", previousArqXmlProperty == null ? "" : previousArqXmlProperty);
        System.setProperty("arquillian.launch", previousArqLaunchProperty == null ? "" : previousArqLaunchProperty);

        // remove created directories
        cleanup();
    }

    @Test
    public void runTestWithSettingsXmlFileSetAsUserSettings() throws InitializationError, IOException {
        testWithSettingsXmlFileSetUsingSystemProperty("org.apache.maven.user-settings");
    }

    @Test
    public void runTestWithSettingsXmlFileSetAsGlobalSettings() throws InitializationError, IOException {
        testWithSettingsXmlFileSetUsingSystemProperty("org.apache.maven.global-settings");
    }

    private void testWithSettingsXmlFileSetUsingSystemProperty(String systemProperty) {
        // set sys properties
        System.setProperty("arquillian.xml", arqXmlWithEap);
        System.setProperty(systemProperty, dummySettingsXml.getAbsolutePath());
        System.setProperty("arquillian.launch", "chameleon-eap");

        // run test
        List<Failure> failures = JUnitCore.runClasses(TestClass.class).getFailures();
        StringBuffer message = new StringBuffer("The test should have NOT failed but it did!");
        if (!failures.isEmpty()) {
            message.append("\nThe stacktrace:\n" + failures.get(0).getTrace());
        }
        // it should have not failed
        assertThat(failures).as(message.toString()).isEmpty();

        String usedMavenRepoLocal = System.getProperty("maven.repo.local");

        if (usedMavenRepoLocal == null){
            usedMavenRepoLocal = localRepo.getAbsolutePath();
        }
        File localJBossAsDistDir = new File(usedMavenRepoLocal + "/org/jboss/as/jboss-as-dist/7.1.3.Final/");

        assertThat(localJBossAsDistDir).exists();
        assertThat(localJBossAsDistDir).isDirectory();
        assertThat(localJBossAsDistDir.listFiles()).as(localJBossAsDistDir + " directory should not be empty").isNotEmpty();
        assertThat(new File(localJBossAsDistDir + "/jboss-as-dist-7.1.3.Final.zip")).exists();
        assertThat(new File(localJBossAsDistDir + "/jboss-as-dist-7.1.3.Final.pom")).exists();
    }


    @Test
    public void runTestWithoutSettingsXmlFileSet() throws InitializationError, IOException {
        // set system properties
        System.setProperty("arquillian.xml", arqXmlWithEap);
        System.setProperty("arquillian.launch", "chameleon-eap");

        // run test
        List<Failure> failures = JUnitCore.runClasses(TestClass.class).getFailures();
        // it should have failed
        assertThat(failures).as("The test should have failed but it didn't").isNotEmpty();
        assertThat(localRepo).doesNotExist();
    }

    @RunWith(Arquillian.class)
    @RunAsClient
    public static class TestClass {

        @Test
        public void test() {
        }
    }

}
