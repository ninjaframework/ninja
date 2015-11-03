/**
 * Copyright (C) 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.diagnostics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for reading lines (snippet) from a source file.
 * 
 * @author Joe Lauer (https://twitter.com/jjlauer)
 * @author Fizzed, Inc. (http://fizzed.com)
 */
public class SourceSnippetHelper {
    
    static public SourceSnippet readFromQualifiedSourceCodePath(File baseDirectory,
                                                                    String packageName,
                                                                    String fileName,
                                                                    int lineFrom,
                                                                    int lineTo) throws IOException {
        // try to find source template as local file
        if (baseDirectory != null) {
            Path templatePath = baseDirectory.toPath()
                .resolve(packageName.replace(".", File.separator))
                .resolve(fileName);
            File templateFile = templatePath.toFile();
            return readFromFile(templateFile, lineFrom, lineTo);
        }
        
        return null;
    }
    
    static public SourceSnippet readFromRelativeFilePath(File baseDirectory,
                                                            String templateRelativePath,
                                                            int lineFrom,
                                                            int lineTo) throws IOException {
        // try to find source template as local file
        if (baseDirectory != null && templateRelativePath != null) {
            File templateFile = baseDirectory.toPath()
                .resolve(templateRelativePath).toFile();
            return readFromFile(templateFile, lineFrom, lineTo);
        }
        
        return null;
    }
    
    static public SourceSnippet readFromFile(File file,
                                            int lineFrom,
                                            int lineTo) throws IOException {
        // try to find source as local file
        if (file != null) {
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    URI source = file.toURI();
                    return readFromInputStream(fis, source, lineFrom, lineTo);
                }
            }
        }
        
        return null;
    }
    
    static private SourceSnippet readFromInputStream(InputStream is,
                                                        URI source,
                                                        int lineFrom,
                                                        int lineTo) throws IOException {
        // did the user provide a strange range (e.g. negative values)?
        // this sometimes may happen when a range is provided like an error
        // on line 3 and you want 5 before and 5 after
        if (lineFrom < 1 && lineTo > 0) {
            // calculate intended range
            int intendedRange = lineTo - lineFrom;
            lineFrom = 1;
            lineTo = lineFrom + intendedRange;
        }
        else if (lineFrom < 0 && lineTo < 0) {
            if (lineFrom < lineTo) {
                int intendedRange = -1 * (lineFrom - lineTo);
                lineFrom = 1;
                lineTo = lineFrom + intendedRange;
            }
            else {
                // giving up
                return null;
            }
        }
        
        BufferedReader in = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8));
        
        List<String> lines = new ArrayList<>();
  
        int i = 0;
        String line;
        while ((line = in.readLine()) != null) {
            i++;                        // lines index are 1-based
            if (i >= lineFrom) {
                if (i <= lineTo) {
                    lines.add(line);
                } else {
                    break;
                }
            }
        }
        
        if (lines.isEmpty()) {
            return null;
        }
        
        // since file may not contain enough lines for requested lineTo -- 
        // we calculate the actual range here by number read "from" line
        // since we are inclusive and not zero based we adjust the "from" by 1
        return new SourceSnippet(source, lines, lineFrom, lineFrom + lines.size() - 1);
    }
}
