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

import java.net.URI;
import java.util.List;

/**
 * Represents a snippet of a source file (1 or more lines).
 * 
 * @author Joe Lauer (https://twitter.com/jjlauer)
 * @author Fizzed, Inc. (http://fizzed.com)
 */
public class SourceSnippet {
    
    private final URI sourceLocation;
    private final List<String> lines;
    private final int lineNumberFrom;
    private final int lineNumberTo;

    public SourceSnippet(URI sourceLocation, List<String> lines, int lineNumberFrom, int lineNumberTo) {
        this.sourceLocation = sourceLocation;
        this.lines = lines;
        this.lineNumberFrom = lineNumberFrom;
        this.lineNumberTo = lineNumberTo;
    }

    public URI getSourceLocation() {
        return sourceLocation;
    }

    public List<String> getLines() {
        return lines;
    }

    public int getLineNumberFrom() {
        return lineNumberFrom;
    }

    public int getLineNumberTo() {
        return lineNumberTo;
    }
    
}
