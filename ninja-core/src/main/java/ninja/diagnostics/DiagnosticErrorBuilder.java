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

import java.io.File;
import java.io.IOException;
import ninja.Result;

/**
 * Utility class for building <code>DiagnosticError</code> instances.
 * 
 * @author Joe Lauer (https://twitter.com/jjlauer)
 * @author Fizzed, Inc. (http://fizzed.com)
 */
public class DiagnosticErrorBuilder {
    
    // base dir will remain static throughout life of app
    static String baseDirectory
            = System.getProperty("user.dir")
                + File.separator 
                + "src" 
                + File.separator 
                + "main" 
                + File.separator
                + "java";
    
    
    static public DiagnosticError build404NotFoundDiagnosticError(
            boolean tryToReadLinesFromSourceCode) {
        
        SourceSnippet snippet = null;
        
        if (tryToReadLinesFromSourceCode) {
            // try to read entire routes file
            snippet = tryToReadSourceSnippetInPackage("conf", "Routes.java", 0, 200);
        }
        
        return buildDiagnosticError(
            "Route not found",
            null,
            snippet,
            -1,
            null);
    }
    
    static public DiagnosticError build403ForbiddenDiagnosticError() {
        return buildDiagnosticError(
            "Forbidden",
            null, false, null);
    }
    
    static public DiagnosticError build401UnauthorizedDiagnosticError() {
        return buildDiagnosticError(
            "Not authorized",
            null, false, null);
    }
    
    static public DiagnosticError build500InternalServerErrorDiagnosticError(
            Throwable cause,
            boolean tryToReadLinesFromSourceCode,
            Result underlyingResult) {
        
        return buildDiagnosticError(
            "Application exception",
            cause,
            tryToReadLinesFromSourceCode, underlyingResult);
    }
    
    static public DiagnosticError build400BadRequestDiagnosticError(
            Throwable cause,
            boolean tryToReadLinesFromSourceCode) {
        
        return buildDiagnosticError(
            "Bad request to application",
            cause,
            tryToReadLinesFromSourceCode, null);
    }
    
    
    static public DiagnosticError buildDiagnosticError(String title,
                                                        Throwable throwable,
                                                        boolean tryToReadLinesFromSourceCode,
                                                        Result underlyingResult) {
        if (tryToReadLinesFromSourceCode) {       
            // see if we can find the source code for this error
            StackTraceElement ste = findFirstStackTraceElementWithSourceCodeInProject(throwable);
            if (ste != null) {
                String relativeSourcePath = getSourceCodeRelativePathForStackTraceElement(ste);
                int lineNumberOfError = ste.getLineNumber();
                
                return buildDiagnosticError(title, throwable, relativeSourcePath, lineNumberOfError, underlyingResult);
            }
        }
        
        // fallback to just displaying the error w/o any source
        return new DiagnosticError(
            title,
            throwable,
            underlyingResult);
    }
    
    
    
    static public DiagnosticError buildDiagnosticError(String title,
                                                        Throwable throwable,
                                                        String packageName,
                                                        String fileName,
                                                        int lineNumberOfError,
                                                        Result underlyingResult) {
        
        String relativeSourcePath = 
            packageName.replace(".", File.separator)
            + fileName;
        
        return buildDiagnosticError(title, throwable, relativeSourcePath, lineNumberOfError, underlyingResult);
    }
    
    
    static public DiagnosticError buildDiagnosticError(String title,
                                                        Throwable throwable,
                                                        String relativeSourcePath,
                                                        int lineNumberOfError,
                                                        Result underlyingResult) {
        int lineNumberFrom = lineNumberOfError - 4;
        int lineNumberTo = lineNumberOfError + 5;

        SourceSnippet snippet = tryToReadSourceSnippet(relativeSourcePath, lineNumberFrom, lineNumberTo);

        return buildDiagnosticError(title, throwable, snippet, lineNumberOfError, underlyingResult);
    }
    
    
    static public DiagnosticError buildDiagnosticError(String title,
                                                        Throwable throwable,
                                                        SourceSnippet snippet,
                                                        int lineNumberOfError,
                                                        Result underlyingResult) {
        
        // if source snippet exists then include it with diagnostic error
        if (snippet != null && snippet.getLines() != null && snippet.getLines().size() > 0) {
            return new DiagnosticError(
                title, 
                throwable,
                snippet.getSourceLocation(),
                snippet.getLines(),
                snippet.getLineNumberFrom(),
                lineNumberOfError,
                underlyingResult);
        }
        
        // fallback to just displaying the error w/o any source
        return new DiagnosticError(
            title,
            throwable,
            underlyingResult);
    }
    
    static public StackTraceElement findFirstStackTraceElementWithSourceCodeInProject(Throwable throwable) {
        if (throwable != null) {
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            if (stackTrace != null) {
                for (StackTraceElement stackTraceElement : stackTrace) {
                    String sourceRelativePath = getSourceCodeRelativePathForStackTraceElement(stackTraceElement);
                    if (sourceCodeExistsInProject(sourceRelativePath)) {
                        return stackTraceElement;
                    }
                }
            }
        }
        return null;
    }
    
    static public boolean sourceCodeExistsInProject(String sourceRelativePath) {
        File sourceCodeFile = new File(baseDirectory, sourceRelativePath);
        return sourceCodeFile.exists();
    }
    
    static public SourceSnippet tryToReadSourceSnippetInPackage(String packageName,
                                                                String fileName,
                                                                int lineFrom,
                                                                int lineTo) {
        try {
            return SourceSnippetHelper.readFromQualifiedSourceCodePath(
                    new File(baseDirectory),
                    packageName,
                    fileName,
                    lineFrom,
                    lineTo);
        } catch (IOException e) {
            return null;
        }
    }
    
    static public SourceSnippet tryToReadSourceSnippet(String sourceRelativePath,
                                                        int lineFrom,
                                                        int lineTo) {
        try {
            return SourceSnippetHelper.readFromRelativeFilePath(
                    new File(baseDirectory),
                    sourceRelativePath,
                    lineFrom,
                    lineTo);
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Calculates the relative path of the source code file of a StackTrace
     * element if its available. Uses the packageName of the class to create a relative path
     * and appends the "filename" included with the stack trace element.
     * @param ste The stack trace element
     * @return The relative path of the source code file or null if the information
     *      wasn't available in the stack trace element.
     */
    static public String getSourceCodeRelativePathForStackTraceElement(StackTraceElement ste) {
        String packageName = ste.getClassName();
        // e.g. com.fizzed.test.Application$1 for an internal class
        int pos = packageName.lastIndexOf('.');
        if (pos > 0) {
            packageName = packageName.substring(0, pos);
            return
                packageName.replace(".", File.separator)
                + File.separator
                + ste.getFileName();
        } else {
            // must be in default package
            return
                ste.getFileName();
        }
    }
    
}
