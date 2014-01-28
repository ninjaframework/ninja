package ninja;

import com.sun.nio.file.SensitivityWatchEventModifier;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchAndRestartMachine {
    
    List<String> exludePatterns;

    private static Logger logger = LoggerFactory.getLogger(WatchAndRestartMachine.class);

    RunClassInSeparateJvmMachine ninjaJettyInsideSeparateJvm;

    WatchService watchService;

    DelayedRestartTrigger restartAfterSomeTimeAndChanges;

    private final Map<WatchKey, Path> mapOfWatchKeysToPaths;



    /**
     * Creates a WatchService and registers the given
 pathectoryToWatchRecursivelyForChangesectory
     */
    public WatchAndRestartMachine(
            String classNameWithMainToRun,
            Path directoryToWatchRecursivelyForChanges,
            List<String> classpath,
            List<String> excludeRegexPatterns) throws IOException {

        
        this.exludePatterns = excludeRegexPatterns;
        
        this.ninjaJettyInsideSeparateJvm = new RunClassInSeparateJvmMachine(
                classNameWithMainToRun, classpath);

        this.restartAfterSomeTimeAndChanges 
                = new DelayedRestartTrigger(
                        ninjaJettyInsideSeparateJvm);
        
        this.restartAfterSomeTimeAndChanges.start();

        this.watchService = FileSystems.getDefault().newWatchService();
        this.mapOfWatchKeysToPaths = new HashMap<WatchKey, Path>();

        //System.out.format("Scanning: %s ...\n", directoryToWatchRecursivelyForChanges);
        registerAll(directoryToWatchRecursivelyForChanges);

    }

    /**
     * Register the given path with the WatchService
     */
    private void register(Path path) throws IOException {

        ////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //// USUALLY THIS IS THE DEFAULT WAY TO REGISTER THE EVENTS:
        ////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        WatchKey watchKey = path.register(
//                watchService, 
//                ENTRY_CREATE, 
//                ENTRY_DELETE,
//                ENTRY_MODIFY);
        
        ////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //// BUT THIS IS DAMN SLOW (at least on a Mac)
        //// THEREFORE WE USE EVENTS FROM COM.SUN PACKAGES THAT ARE WAY FASTER
        //// THIS MIGHT BREAK COMPATABILITY WITH OTHER JDKS
        //// MORE: http://stackoverflow.com/questions/9588737/is-java-7-watchservice-slow-for-anyone-else
        ////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        
        WatchKey watchKey = path.register(
                watchService, 
           new WatchEvent.Kind[]{
               StandardWatchEventKinds.ENTRY_CREATE,
               StandardWatchEventKinds.ENTRY_MODIFY,
               StandardWatchEventKinds.ENTRY_DELETE
           }, 
           SensitivityWatchEventModifier.HIGH);
        
        

        
        mapOfWatchKeysToPaths.put(watchKey, path);

    }


    private void registerAll(final Path path) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path path,
                    BasicFileAttributes attrs)
                    throws IOException {
                register(path);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void startWatching() {

        for (;;) {

            WatchKey watchKey;
            try {
                watchKey = watchService.take();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
                return;
            }

            Path path = mapOfWatchKeysToPaths.get(watchKey);
            if (path == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }



            for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                WatchEvent.Kind watchEventKind = watchEvent.kind();

                // TBD - provide example of how OVERFLOW watchEvent is handled
                if (watchEventKind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry watchEvent is the file name of entry
                WatchEvent<Path> ev = (WatchEvent<Path>) watchEvent;
                Path name = ev.context();
                Path child = path.resolve(name);

                // print out watchEvent
                //System.out.format("%s: %s\n", watchEvent.kind().name(), child);
                
                if (watchEventKind == ENTRY_MODIFY) {
                    
                    // we are not interested in events from parent directories...
                    if (! child.toFile().isDirectory()) {
                        
                        if (!checkIfMatchesPattern(exludePatterns, child.toFile().getAbsolutePath())) {
                        
                            System.out.println(
                                    "Found file modification - triggering reload:  " 
                                        + child.toFile().getAbsolutePath());
                            
                            restartAfterSomeTimeAndChanges.triggerRestart();
                            
                        }
                    
                    }

                }

                // ninjaJettyInsideSeparateJvm.restartNinjaJetty();
                // if directory is created, then
                // register it and its sub-directories recursively
                if (watchEventKind == ENTRY_CREATE) {
                    
                    restartAfterSomeTimeAndChanges.triggerRestart();
                    
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        // ignore to keep sample readable
                    }

                }

            }

            // reset watchKey and remove from set if directory no longer accessible
            boolean valid = watchKey.reset();
            if (!valid) {
                mapOfWatchKeysToPaths.remove(watchKey);

                // all directories are inaccessible
                if (mapOfWatchKeysToPaths.isEmpty()) {
                    break;
                }

            }

        }

    }
    
    
    
    public static boolean checkIfMatchesPattern(List<String> regexPatterns, String string) {
    
        
        for (String regex : regexPatterns) {
        
            if (string.matches(regex)) {
                return true;
            }
            
        }
        
        
        return false;
    }
    
   

}
