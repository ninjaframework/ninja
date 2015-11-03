/**
 * Copyright (C) 2012-2015 the original author or authors.
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

package ninja.diagnostics;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author joelauer
 */
public class SourceSnippetHelperTest {
    
    Path baseDir = Paths.get("src", "test", "java");
    Path testSourceDir = Paths.get("ninja", "diagnostics");
    Path relativeSourcePath = testSourceDir.resolve("TestSource.txt");
    Path relativeUTF8SourcePath = testSourceDir.resolve("TestSourceUTF8.txt");
    
    @Test
    public void readFromRelativeFilePath() throws Exception {
    
        SourceSnippet snippet;
        
        snippet = SourceSnippetHelper.readFromRelativeFilePath(
                baseDir.toFile(), relativeSourcePath.toString(), 1, 10);
        
        URI expectedSourceLocation = baseDir.resolve(relativeSourcePath).toUri();
        Assert.assertEquals(expectedSourceLocation, snippet.getSourceLocation());
        Assert.assertEquals(3, snippet.getLines().size());
        Assert.assertEquals("first line", snippet.getLines().get(0));
        Assert.assertEquals("second line", snippet.getLines().get(1));
        Assert.assertEquals("third line", snippet.getLines().get(2));
        Assert.assertEquals(1, snippet.getLineNumberFrom());
        Assert.assertEquals(3, snippet.getLineNumberTo());
        
        
        snippet = SourceSnippetHelper.readFromRelativeFilePath(
                baseDir.toFile(), relativeSourcePath.toString(), 1, 1);
        
        Assert.assertEquals(expectedSourceLocation, snippet.getSourceLocation());
        Assert.assertEquals(1, snippet.getLines().size());
        Assert.assertEquals("first line", snippet.getLines().get(0));
        Assert.assertEquals(1, snippet.getLineNumberFrom());
        Assert.assertEquals(1, snippet.getLineNumberTo());
        
        
        // graceful w/ bad from/to requested?
        snippet = SourceSnippetHelper.readFromRelativeFilePath(
                baseDir.toFile(), relativeSourcePath.toString(), -1, 1);
        
        Assert.assertEquals(expectedSourceLocation, snippet.getSourceLocation());
        Assert.assertEquals(3, snippet.getLines().size());
        Assert.assertEquals("first line", snippet.getLines().get(0));
        Assert.assertEquals("second line", snippet.getLines().get(1));
        Assert.assertEquals("third line", snippet.getLines().get(2));
        Assert.assertEquals(1, snippet.getLineNumberFrom());
        Assert.assertEquals(3, snippet.getLineNumberTo());
        
        
        // graceful w/ bad from/to requested?
        snippet = SourceSnippetHelper.readFromRelativeFilePath(
                baseDir.toFile(), relativeSourcePath.toString(), -10, -2);
        
        Assert.assertEquals(expectedSourceLocation, snippet.getSourceLocation());
        Assert.assertEquals(3, snippet.getLines().size());
        Assert.assertEquals("first line", snippet.getLines().get(0));
        Assert.assertEquals("second line", snippet.getLines().get(1));
        Assert.assertEquals("third line", snippet.getLines().get(2));
        Assert.assertEquals(1, snippet.getLineNumberFrom());
        Assert.assertEquals(3, snippet.getLineNumberTo());
        
    }

    @Test
    public void readFromUTF8File() throws Exception {

        SourceSnippet snippet = SourceSnippetHelper.readFromRelativeFilePath(
            baseDir.toFile(), relativeUTF8SourcePath.toString(), 1, 1);

        Assert.assertEquals("utf8=✓", snippet.getLines().get(0));

    }
    
}
