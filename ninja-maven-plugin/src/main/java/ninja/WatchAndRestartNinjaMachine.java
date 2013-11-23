package ninja;

import java.io.File;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchAndRestartNinjaMachine {
    
    String EXLUDE_PATTERN = "ftl.html";

    private static Logger logger = LoggerFactory.getLogger(WatchAndRestartNinjaMachine.class);

    NinjaJettyInsideSeparateJvm ninjaJettyInsideSeparateJvm;

    WatchService watchService;

    AtomicInteger atomicInteger = new AtomicInteger(0);

    private final Map<WatchKey, Path> mapOfWatchKeysToPaths;



    /**
     * Creates a WatchService and registers the given
 pathectoryToWatchRecursivelyForChangesectory
     */
    public WatchAndRestartNinjaMachine(
            Path directoryToWatchRecursivelyForChanges,
            List<String> classpath) throws IOException {

        this.ninjaJettyInsideSeparateJvm = new NinjaJettyInsideSeparateJvm(classpath);

        RestartAfterSomeTimeAndChanges restartAfterSomeTimeAndChanges 
                = new RestartAfterSomeTimeAndChanges(
                        atomicInteger, 
                        ninjaJettyInsideSeparateJvm);
        
        restartAfterSomeTimeAndChanges.start();

        this.watchService = FileSystems.getDefault().newWatchService();
        this.mapOfWatchKeysToPaths = new HashMap<WatchKey, Path>();

        System.out.format("Scanning1 %s ...\n", directoryToWatchRecursivelyForChanges);
        registerAll(directoryToWatchRecursivelyForChanges);
        System.out.println("Done.");

    }

    /**
     * Register the given path with the WatchService
     */
    private void register(Path path) throws IOException {
        
        WatchKey watchKey = path.register(
                watchService, 
                ENTRY_CREATE, 
                ENTRY_DELETE,
                ENTRY_MODIFY);
        
        mapOfWatchKeysToPaths.put(watchKey, path);

    }

    /**
     * Register the given pathectory, and all its sub-pathectories, with the
 WatchService.
     */
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

    /**
     * Process all watchEvents for watchKeys queued to the watchService
     */
    public void processEvents() {

        for (;;) {

            System.out.println("run");
            // wait for watchKey to be signalled
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
                System.out.format("%s: %s\n", watchEvent.kind().name(), child);
                
                if (watchEventKind == ENTRY_MODIFY) {
                    
                    // we are not interested in events from parent directories...
                    if (! child.toFile().isDirectory()) {
                        
                        if (!child.toFile().getAbsolutePath().endsWith(EXLUDE_PATTERN)) {
                        
                            System.out.println("found file modification - reloading:  " + child.toFile().getAbsolutePath());
                            atomicInteger.getAndIncrement();
                            
                        }
                    
                    }

                }

                // ninjaJettyInsideSeparateJvm.restartNinjaJetty();
                // if directory is created, then
                // register it and its sub-directories recursively
                if (watchEventKind == ENTRY_CREATE) {
                    
                    atomicInteger.incrementAndGet();
                    
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

}
