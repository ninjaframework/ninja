package ninja;

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

    private static Logger logger = LoggerFactory.getLogger(WatchAndRestartNinjaMachine.class);

    NinjaJettyInsideSeparateJvm revolverSingleBullet;

    WatchService watchService;

    AtomicInteger atomicInteger = new AtomicInteger(0);

    private final Map<WatchKey, Path> keys;

    private boolean trace = false;



    /**
     * Creates a WatchService and registers the given
     * directoryToWatchRecursivelyForChangesectory
     */
    public WatchAndRestartNinjaMachine(
            Path directoryToWatchRecursivelyForChanges,
            List<String> classpath) throws IOException {

        this.revolverSingleBullet = new NinjaJettyInsideSeparateJvm(classpath);

        RestartAfterSomeTimeAndChanges restartAfterSomeTimeAndChanges 
                = new RestartAfterSomeTimeAndChanges(
                        atomicInteger, 
                        revolverSingleBullet);
        
        restartAfterSomeTimeAndChanges.start();

        this.watchService = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();

        System.out.format("Scanning1 %s ...\n", directoryToWatchRecursivelyForChanges);
        registerAll(directoryToWatchRecursivelyForChanges);
        System.out.println("Done.");

        // enable trace after initial registration
        this.trace = true;

    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        
        WatchKey key = dir.register(
                watchService, 
                ENTRY_CREATE, 
                ENTRY_DELETE,
                ENTRY_MODIFY);
        
        keys.put(key, dir);

    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                    BasicFileAttributes attrs)
                    throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Process all events for keys queued to the watchService
     */
    public void processEvents() {

        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            System.out.println("reload...");
            atomicInteger.incrementAndGet();

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);

                // revolverSingleBullet.restartNinjaJetty();
                // if directory is created, then
                // register it and its sub-directories recursively
                if (kind == ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readable
                    }

                }

            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }

            }

        }

    }

}
