package org.arquillian.container.chameleon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.InitializationError;

/**
 * @author <a href="mailto:mjobanek@redhat.com">Matous Jobanek</a>
 */
public class ManualSettingsXmlTestCase {

    private static String target = System.getProperty("user.dir") + "/target/";
    private static File dummySettingsXml = new File(target + "/test-classes/settings-repository/dummy-settings.xml");
    private String arqXmlWithSettings = target + "/test-classes/settings-repository/arquillian-with-settings.xml";
    private File localRepo = new File(target + "/local-repository");
    private File chameleonDistributionDownloadFolder = new File(target + "/chameleonDistributionDownloadFolder");
    private File chameleonResolveCacheFolder = new File("/chameleonResolveCacheFolder");

    @BeforeClass
    public static void prepare() throws IOException {
        // replace %basedir% string (in settings.xml) with the real path to the base directory
        String content = IOUtils.toString(new FileInputStream(dummySettingsXml), "UTF-8");
        content = content.replaceAll("%basedir%", System.getProperty("user.dir"));
        IOUtils.write(content, new FileOutputStream(dummySettingsXml), "UTF-8");
    }

    @Before
    public void cleanup() throws IOException {
        FileUtils.deleteDirectory(localRepo);
        FileUtils.deleteDirectory(chameleonDistributionDownloadFolder);
        FileUtils.deleteDirectory(chameleonResolveCacheFolder);
    }

    @Test
    public void runTestWithSettingsXmlFileSet() throws InitializationError, IOException {

        // set arquillian properties
        System.setProperty("arquillian.xml", arqXmlWithSettings);
        System.setProperty("arquillian.launch", "chameleon-manual-with-settings");

        // run test
        List<Failure> failures = JUnitCore.runClasses(TestClass.class).getFailures();
        StringBuffer message = new StringBuffer("The test should have NOT failed but it did!");
        if (!failures.isEmpty()) {
            message.append("\nThe stacktrace:\n" + failures.get(0).getTrace());
        }
        // it should have not failed
        Assert.assertTrue(message.toString(), failures.isEmpty());

        // verify that target/local-repository was used
        Assert.assertTrue("target/local-repository directory should exist", localRepo.exists());
        Assert.assertTrue("target/local-repository directory should be a directory", localRepo.isDirectory());
        Assert.assertTrue("target/local-repository directory should not be empty", localRepo.listFiles().length > 0);
        Assert.assertTrue("target/local-repository directory should contain org directory",
                          new File(localRepo + "/org").exists());
    }

    @Test
    public void runTestWithoutSettingsXmlFileSet() throws InitializationError, IOException {
        // set arquillian properties
        System.setProperty("arquillian.xml", arqXmlWithSettings);
        System.setProperty("arquillian.launch", "chameleon-manual-without-settings");

        // run test
        List<Failure> failures = JUnitCore.runClasses(TestClass.class).getFailures();
        // it should have failed
        Assert.assertFalse("The test should have failed but it didn't", failures.isEmpty());
        Assert.assertFalse("target/local-repository directory should NOT exist", localRepo.exists());
    }

    @RunWith(Arquillian.class)
    @RunAsClient
    public static class TestClass {

        @Test
        public void test() {
        }
    }

}
