/*
 * Copyright 2010 Guardian News and Media
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gu.management.manifest;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
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
        assertThat(productionManifest.getRevisionNumber(), equalTo(1234L));
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
        assertThat(manifest.getRevisionNumber(), equalTo(678L));
        fileProvider.setReturnedFileContents(Arrays.asList("Revision: 890"));
        manifest.reload();
        assertThat(manifest.getRevisionNumber(), equalTo(890L));
    }

    @Test
    public void shouldRetrieveManifestValue(){
        assertEquals("trunk", productionManifest.getValueFor("Branch"));
        assertEquals("1.5.0_06-b05 (Sun Microsystems Inc.)", productionManifest.getValueFor("Created-By"));
        assertEquals(null, productionManifest.getValueFor("Does not exist"));
        assertEquals("", productionManifest.getValueFor("Does not exist", ""));
        assertEquals("somedefault", productionManifest.getValueFor("Does not exist", "somedefault"));
    }

    @Test
    public void shouldRetrieveManifestValueWithCrappyManifest(){
         StubApplicationFileProvider stubApplicationFileProvider = new StubApplicationFileProvider(".");
        stubApplicationFileProvider.setReturnedFileContents(Arrays.asList("Manifest-Version: 1.0",
                "Ant-Version: Apache Ant 1.7.0",
                "Created-By: 1.5.0_06-b05 (Sun Microsystems Inc.)",
                "Built-By: dtwswells",
                "Title: guardian.co.uk R2 Web Application",
                "Date: March 2 2007",
                "Branch: trunk",
                "Crappy",
                "Rubbish:",
                "Revision: 1234"));
        productionManifest = new Manifest(stubApplicationFileProvider);
        productionManifest.setManifestFilePath("src/test/com/gu/r2/common/management/MANIFEST.MF");

        assertEquals("somedefault", productionManifest.getValueFor("Crappy", "somedefault"));
        assertEquals("somedefault", productionManifest.getValueFor("DoestExist", "somedefault"));
        assertEquals("somedefault", productionManifest.getValueFor("Rubbish", "somedefault"));
        assertEquals(null, productionManifest.getValueFor("DoestExist"));
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