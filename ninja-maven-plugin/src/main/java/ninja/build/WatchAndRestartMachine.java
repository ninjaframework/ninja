/**
 * Copyright (C) the original author or authors.
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

package ninja.build;

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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.nio.file.SensitivityWatchEventModifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class WatchAndRestartMachine implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(WatchAndRestartMachine.class);

    private boolean shutdown;
    private final DelayedRestartTrigger restartTrigger;
    private final Set<String> includes;
    private final Set<String> excludes;
    private final WatchService watchService;
    private final Map<WatchKey, Path> mapOfWatchKeysToPaths;
    private final AtomicInteger takeCount;

    public WatchAndRestartMachine(
            Path directoryToRecursivelyWatch,
            Set<String> includes,
            Set<String> excludes,
            DelayedRestartTrigger restartTrigger) throws IOException {

        this(new HashSet<>(Arrays.asList(directoryToRecursivelyWatch)),
                includes, excludes, restartTrigger);
        
    }
    
    public WatchAndRestartMachine(
            Set<Path> directoriesToRecursivelyWatch,
            Set<String> includes,
            Set<String> excludes,
            DelayedRestartTrigger restartTrigger) throws IOException {

        this.watchService = FileSystems.getDefault().newWatchService();
        this.mapOfWatchKeysToPaths = new HashMap<>();
        this.includes = includes;
        this.excludes = excludes;
        this.restartTrigger = restartTrigger;
        this.takeCount = new AtomicInteger(0);
        for (Path path: directoriesToRecursivelyWatch) {
            registerAll(path);
        }
        
    }

    public void shutdown() {
        this.shutdown = true;
        // doesn't do anything but does prevent logging an error
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
        //// THIS MIGHT BREAK COMPATIBILITY WITH OTHER JDKs
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

    @Override
    public void run() {
        
        for (;;) {

            WatchKey watchKey;
            try {
                watchKey = watchService.take();
                takeCount.incrementAndGet();
            } catch (InterruptedException e) {
                if (!shutdown) {
                    log.error("Unexpectedly interrupted while waiting for take()", e);
                }
                return;
            }

            Path path = mapOfWatchKeysToPaths.get(watchKey);
            if (path == null) {
                log.error("WatchKey not recognized!!");
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
                
                if (watchEventKind == ENTRY_MODIFY) {
                    // we are not interested in events from parent directories...
                    if (!child.toFile().isDirectory()) {
                        handleNewOrModifiedFile("Modified", child);
                    }
                }

                // if directory is created, then register it and its sub-directories recursively
                if (watchEventKind == ENTRY_CREATE) {
                    
                    if (!child.toFile().isDirectory()) {
                        handleNewOrModifiedFile("New", child);
                    }
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException e) {
                        log.error("Something fishy happened. Unable to register new dir for watching", e);
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
    
    public void handleNewOrModifiedFile(String newOrMod, Path path) {
        String f = path.toFile().getAbsolutePath();
        
        log.debug("{} file detected: {}", newOrMod, f);
        
        RuleMatch match = matchRule(includes, excludes, f);
        
        log.debug(" matched rule: type={}, pattern={}, proceed={}", match.type, match.pattern, match.proceed);
        
        if (match.proceed) {
            log.debug(" will trigger restart", newOrMod, f);
            restartTrigger.trigger();
        }
        else {
            log.debug(" will not trigger restart");
        }
    }
    
    public static enum RuleType {
        none,
        include,
        exclude
    }
    
    public static class RuleMatch {
        
        final RuleType type;
        final String pattern;
        final boolean proceed;

        public RuleMatch(RuleType type, String pattern, boolean proceed) {
            this.type = type;
            this.pattern = pattern;
            this.proceed = proceed;
        }
        
    }
    
    public static RuleMatch matchRule(Set<String> includes, Set<String> excludes, String string) {
        
        if (includes != null) {
            for (String regex: includes) {
                if (string.matches(regex)) {
                    return new RuleMatch(RuleType.include, regex, true);
                }
            }
        }
        
        if (excludes != null) {
            for (String regex: excludes) {
                if (string.matches(regex)) {
                    return new RuleMatch(RuleType.exclude, regex, false);
                }
            }
        }

        return new RuleMatch(RuleType.none, "", true);
    }
    
    public static boolean checkIfWouldBeExcluded(Set<String> patterns, String string) {
        // use "excludes" above to use this for testing
        return !matchRule(null, patterns, string).proceed;
    }

}
