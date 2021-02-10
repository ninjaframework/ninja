/**
 * Copyright (C) the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import ninja.NinjaTest;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import models.FormWithFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

public class UploadControllerAutoTest extends NinjaTest {

    @Test
    public void testThatUploadWorks()
            throws FileNotFoundException, IOException {

        File file = new File("src/test/resources/test_for_upload.txt");
        File file2 = new File("src/test/resources/test_for_upload_2.txt");

        // Let's upload a simple txt file...
        String result = ninjaTestBrowser.uploadFiles(
                getServerAddress() + "uploadFinishAuto",
                new String[] { "file", "file", "file2" },
                new File[] { file, file2, file2 });

        // compute excepted result
        StringBuilder sb = new StringBuilder();

        String strFile = IOUtils.toString(new FileInputStream(file));
        String strFile2 = IOUtils.toString(new FileInputStream(file2));
        // file
        sb.append("file\n").append(strFile).append("\n");
        // files
        sb.append("files\n").append(strFile).append("\n");
        sb.append("files\n").append(strFile2).append("\n");
        // file inputstream
        sb.append("inputstream\n").append(strFile).append("\n");
        // files inputstream
        sb.append("inputstreams\n").append(strFile).append("\n");
        sb.append("inputstreams\n").append(strFile2).append("\n");
        // file fileItem
        sb.append("fileitem\n").append(strFile).append("\n");
        // file fileItems
        sb.append("fileitems\n").append(strFile).append("\n");
        sb.append("fileitems\n").append(strFile2).append("\n");
        // context.getParameterAsFileItem
        sb.append("getParameterAsFileItem\n").append(strFile).append("\n");
        // file2
        sb.append("file2\n").append(strFile2).append("\n");

        // The upload simply displays back the file we uploaded.
        // Let's see if that has worked...

        assertEquals(sb.toString(), result);

    }


    @Test
    public void testPostFormWithFile() throws IOException {

        File file = new File("src/test/resources/test_for_upload.txt");

        Map<String, String> formParameters = Maps.newHashMap();

        formParameters.put("name", "tester");
        formParameters.put("email", "test@email.com");
        
        // Let's upload a simple txt file...
        String result = ninjaTestBrowser
                .uploadFileWithForm(getServerAddress() + "uploadWithForm", 
                        "file", file, formParameters);
        
        ObjectMapper objectMapper = new ObjectMapper();
        FormWithFile returnedObject = objectMapper.readValue(result, FormWithFile.class);

        // And assert that returned object has same values
        assertEquals("tester", returnedObject.name);
        assertEquals("test@email.com", returnedObject.email);
        assertTrue(returnedObject.fileReceived);
        
    }

    @Test
    public void testPostFormWithFileAndQueryString() throws IOException {

        File file = new File("src/test/resources/test_for_upload.txt");

        Map<String, String> formParameters = Maps.newHashMap();

        formParameters.put("name", "tester");
        formParameters.put("email", "test@email.com");
        
        // Let's upload a simple txt file...
        String result = ninjaTestBrowser
                .uploadFileWithForm(getServerAddress() + "uploadWithForm?a=1&b=hello&c=%E2%82%AC", 
                        "file", file, formParameters);
        
        ObjectMapper objectMapper = new ObjectMapper();
        FormWithFile returnedObject = objectMapper.readValue(result, FormWithFile.class);

        // And assert that returned object has same values
        assertEquals("tester", returnedObject.name);
        assertEquals("test@email.com", returnedObject.email);
        assertTrue(returnedObject.fileReceived);
        assertThat(returnedObject.parameters, hasEntry("a", "1"));
        assertThat(returnedObject.parameters, hasEntry("b", "hello"));
        assertThat(returnedObject.parameters, hasEntry("c", "\u20AC"));
        assertThat(returnedObject.a, is(1L));
        assertThat(returnedObject.b, is("hello"));
    }


}
