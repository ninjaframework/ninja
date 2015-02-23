package ninja.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
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
    public static String basePath;
    private static String outputPath;
    private static PropertiesConfiguration properties;
    private static final String ENCODING = "UTF-8";
    private static final String JS = "js";
    private static final String CSS = "css";
    private static final String JAVA_DIR = "/src/main/java/";
    private static final boolean DISABLEOPTIMIZATION = false;
    private static final boolean PRESERVESEMICOLONS = false;
    private static final boolean VERBOSE = false;
    private static final boolean MUNGE = true;
    private static final int LINEBREAK = -1;

    private MinificationUtils() {
    }
    
    public static void minify(String absolutePath) {
        properties = SwissKnife.loadConfigurationInUtf8("file://" + basePath + JAVA_DIR + NinjaProperties.CONF_FILE_LOCATION_BY_CONVENTION);    
        outputPath = properties.getString("application.minify.assetsdir", JAVA_DIR + "assets/");  
        
        if (absolutePath == null || absolutePath.contains("min")) {
            return;
        }
        
        if (properties.getBoolean("application.minify.js", false) && absolutePath.endsWith(JS)) {
            minifyJS(new File(absolutePath));
        } else if (properties.getBoolean("application.minify.css", false) && absolutePath.endsWith(CSS)) {
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
            
            if (properties.getBoolean("application.minify.gzipjs", false)) {
                createGzipFile(outputFile);
            }
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

            if (properties.getBoolean("application.minify.gzipcss", false)) {
                createGzipFile(outputFile);
            }
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
            folder = properties.getString("application.minify.cssfolder", "stylesheets");
        } else if (JS.equals(suffix)) {
            folder = properties.getString("application.minify.jsfolder", "javascripts");
        }
        
        File outputFile = new File(path + outputPath + "/" + folder + "/" + fileName + ".min." + suffix);
        
        return outputFile;
    }
    
    private static void createGzipFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        
        File gzipped = new File(file.getAbsolutePath() + ".gz");
        GZIPOutputStream outpuStream = null;
        FileInputStream inputStream = null;
        try {
            outpuStream = new GZIPOutputStream(new FileOutputStream(gzipped));
            inputStream = new FileInputStream(file);
            IOUtils.copy(inputStream, outpuStream);
            LOG.info("Created gzipped asset " + gzipped.getName());
        } catch (IOException e) {
            LOG.error("Failed to create gzipped file", e);
        } finally {
            try {
                outpuStream.close();
                inputStream.close();
            } catch (IOException e) {
                LOG.error("Failed to close streams while creating gzipped file", e);
            }
        }
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