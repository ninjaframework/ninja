package ninja.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * Convenient class for minification of css and js assets
 * Based on
 * https://github.com/davidB/yuicompressor-maven-plugin/blob/master/src/main/java/net_alchim31_maven_yuicompressor/YuiCompressorMojo.java 
 * 
 * @author svenkubiak
 *
 */
public final class MinificationUtils {
    private static Logger LOG = LoggerFactory.getLogger(MinificationUtils.class);
    private static final String OUTPUTPATH = "/src/main/java/assets/";
    private static final String ENCODING = "UTF-8";
    private static final String JS = "js";
    private static final String CSS = "css";
    private static final boolean DISABLEOPTIMIZATION = false;
    private static final boolean PRESERVESEMICOLONS = false;
    private static final boolean VERBOSE = false;
    private static final boolean MUNGE = true;
    private static final int LINEBREAK = -1;
   
    public static void minify(String absolutePath) {
        if (absolutePath == null || absolutePath.contains("min")) {
            return;
        }
        
        if (absolutePath.endsWith(JS)) {
            minifyJS(new File(absolutePath));
        } else if (absolutePath.endsWith(CSS)) {
            minifyCSS(new File(absolutePath));
        }
    }
    
    private static void minifyJS(File inputFile) {
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
            File outputFile = getOutputFile(inputFile, JS);
            inputStreamReader = new InputStreamReader(new FileInputStream(inputFile), ENCODING);
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(outputFile));
            
            JavaScriptCompressor compressor = new JavaScriptCompressor(inputStreamReader, new MinificationErrorReporter());
            compressor.compress(outputStreamWriter, LINEBREAK, MUNGE, VERBOSE, PRESERVESEMICOLONS, DISABLEOPTIMIZATION);  
            
            outputStreamWriter.flush();
            
            log(inputFile, outputFile);
        } catch (IOException e) {
            LOG.error("Failed to minify JS");
        } finally {
            try {
                inputStreamReader.close();
                outputStreamWriter.close();
            } catch (IOException e) {
                LOG.error("Failed to close reader/writer while minifing JS", e);
            }
        }
    }

    private static void log(File inputFile, File outputFile) {
        LOG.info(String.format("Minified asset %s (%db) -> %s (%db) [compressed to %d%% of original size]", inputFile.getName(), inputFile.length(), outputFile.getName(), outputFile.length(), ratioOfSize(inputFile, outputFile)));
    }

    private static void minifyCSS(File inputFile) {
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        try {
            File outputFile = getOutputFile(inputFile, CSS);
            inputStreamReader = new InputStreamReader(new FileInputStream(inputFile), ENCODING);
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(outputFile));
            
            CssCompressor compressor = new CssCompressor(inputStreamReader);
            compressor.compress(outputStreamWriter, LINEBREAK);
            
            outputStreamWriter.flush();
            
            log(inputFile, outputFile);
        } catch (IOException e) {
            LOG.error("Failed to minify CSS", e);
        } finally {
            try {
                inputStreamReader.close();
                outputStreamWriter.close();
            } catch (IOException e) {
                LOG.error("Failed to close reader/writer while minifing CSS", e);
            }
        }
    }
    
    private static File getOutputFile(File file, String suffix) {
        String path = file.getAbsolutePath().split("target")[0];
        
        String fileName = file.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        
        String folder = null;
        if (CSS.equals(suffix)) {
            folder = "stylesheets";
        } else if (JS.equals(suffix)) {
            folder = "javascripts";
        }
        
        File outputFile = new File(path + OUTPUTPATH + "/" + folder + "/" + fileName + ".min." + suffix);
        
        return outputFile;
    }
    
    private static long ratioOfSize(File inputFile, File outputFile) {
        long inFile = Math.max(inputFile.length(), 1);
        long outFile = Math.max(outputFile.length(), 1);
        return (outFile * 100) / inFile;
    }
    
    private static class MinificationErrorReporter implements ErrorReporter {
        public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
            if (line < 0) {
                LOG.warn(message);
            } else {
                LOG.warn(line + ':' + lineOffset + ':' + message);
            }
        }
     
        public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
            if (line < 0) {
                LOG.error(message);
            } else {
                LOG.error(line + ':' + lineOffset + ':' + message);
            }
        }
     
        public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
            error(message, sourceName, line, lineSource, lineOffset);
            return new EvaluatorException(message);
        }
    }
}