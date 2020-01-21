/**
 * Copyright (C) 2012-2020 the original author or authors.
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

import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author joelauer
 */
@RunWith(MockitoJUnitRunner.class)
public class DiagnosticErrorBuilderTest {
    
    Path baseDir = Paths.get("src", "test", "java");
    Path testSourceDir = Paths.get("ninja", "diagnostics");
    Path relativeSourcePath = testSourceDir.resolve("DiagnosticErrorBuilderTest.java");
    
    @Test
    public void stackTraceAnalysis() throws Exception {
    
        // throw exception from this class!
        Exception e = null;
        try {
            throw new Exception("me!");
        } catch (Exception ex) {
            e = ex;
        }
        
        // since we are in "test" directory this shouldn't find anything
        Assert.assertNull(DiagnosticErrorBuilder.findFirstStackTraceElementWithSourceCodeInProject(e));
        
        // change baseDir to "src/test/java"
        DiagnosticErrorBuilder.baseDirectory = baseDir.toString();
        
        StackTraceElement ste = DiagnosticErrorBuilder.findFirstStackTraceElementWithSourceCodeInProject(e);
        
        // should be first element in stacktrace
        Assert.assertSame(ste, e.getStackTrace()[0]);
        
        // verify what relative path of source it calculates
        String path = DiagnosticErrorBuilder.getSourceCodeRelativePathForStackTraceElement(ste);
        
        Assert.assertEquals(relativeSourcePath.toString(), path);
        
    }
    
}
