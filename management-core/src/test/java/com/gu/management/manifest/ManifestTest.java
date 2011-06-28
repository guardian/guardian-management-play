package com.gu.management.manifest;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ManifestTest {

    private Manifest productionManifest;

    @Before
    public void setUp() {
        StubApplicationFileProvider stubApplicationFileProvider = new StubApplicationFileProvider(".");
        stubApplicationFileProvider.setReturnedFileContents(Arrays.asList("Manifest-Version: 1.0",
                "Ant-Version: Apache Ant 1.7.0",
                "Created-By: 1.5.0_06-b05 (Sun Microsystems Inc.)",
                "Built-By: dtwswells",
                "Title: guardian.co.uk R2 Web Application",
                "Date: March 2 2007",
                "Branch: trunk",
                "Revision: 1234"));
        productionManifest = new Manifest(stubApplicationFileProvider);
        productionManifest.setManifestFilePath("src/test/com/gu/r2/common/management/MANIFEST.MF");
    }

    @Test
    public void shouldReadRevision() throws Exception {
        assertThat(productionManifest.getRevisionNumber(), equalTo("1234"));
    }

    @Test
    public void testShouldReadManifestWithNonIntegerRevision() throws Exception {
        StubApplicationFileProvider fileProvider = new StubApplicationFileProvider(".");
        fileProvider.setReturnedFileContents(Arrays.asList("Revision: 2March20071245PM"));
        Manifest manifest = new Manifest(fileProvider);
        assertThat(manifest.getRevisionNumber(), nullValue());
    }

    @Test
    public void shouldReturnStringWithAllManifestData() throws Exception {
        assertThat(productionManifest.getManifestInformation(), equalTo(
                "Absolute-Path: " + new File(".", "src/test/com/gu/r2/common/management/MANIFEST.MF").getAbsolutePath() + "\n" +
                        "Manifest-Version: 1.0\n" +
                        "Ant-Version: Apache Ant 1.7.0\n" +
                        "Created-By: 1.5.0_06-b05 (Sun Microsystems Inc.)\n" +
                        "Built-By: dtwswells\n" +
                        "Title: guardian.co.uk R2 Web Application\n" +
                        "Date: March 2 2007\n" +
                        "Branch: trunk\n" +
                        "Revision: 1234\n"));
    }

    @Test
    public void shouldReturnErrorStringIfManifestFileNotFound() throws Exception {
        StubApplicationFileProvider fileProvider = new StubApplicationFileProvider(".");
        fileProvider.setReturnedFileContents(null);
        Manifest manifest = new Manifest(fileProvider);
        manifest.setManifestFilePath("cant/find/me");
        assertThat(manifest.getManifestInformation(), equalTo("Manifest file not found: '" + new File(".", "cant/find/me").getAbsolutePath() + "'"));
    }

    @Test
    public void shouldGenerateRevisionNumberIfTheManifestIsNotFound() throws Exception {
        StubApplicationFileProvider fileProvider = new StubApplicationFileProvider(".");
        fileProvider.setReturnedFileContents(null);
        Manifest manifest = new Manifest(fileProvider);
        manifest.setManifestFilePath("src/test/com/gu/r2/common/management/MANIFEST.MF");
        assertThat(manifest.getRevisionNumber(), not(nullValue()));
    }

    @Test
    public void shouldReloadManifest() throws Exception {
        StubApplicationFileProvider fileProvider = new StubApplicationFileProvider(".");
        fileProvider.setReturnedFileContents(Arrays.asList("Revision: 678"));
        Manifest manifest = new Manifest(fileProvider);
        manifest.setManifestFilePath("src/test/com/gu/r2/common/management/MANIFEST.MF");
        assertThat(manifest.getRevisionNumber(), equalTo("678"));
        fileProvider.setReturnedFileContents(Arrays.asList("Revision: 890"));
        manifest.reload();
        assertThat(manifest.getRevisionNumber(), equalTo("890"));
    }


    public class StubApplicationFileProvider extends ApplicationFileProvider {

        private List<String> fileToReturn;
        private String pathToRootOfApplication;

        public StubApplicationFileProvider(String pathToRootOfApplication) {
            this.pathToRootOfApplication = pathToRootOfApplication;
        }

        @Override
        public List<String> getFileContents(String relativePath) {
            return fileToReturn;
        }

        public void setReturnedFileContents(List<String> fileToReturn) {
            this.fileToReturn = fileToReturn;
        }

        @Override
        public String getAbsolutePath(String relativePath) {
            return new File(pathToRootOfApplication, relativePath).getAbsolutePath();
        }
    }
}