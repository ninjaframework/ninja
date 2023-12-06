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

import java.io.File;
import java.io.IOException;
import java.util.List;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.listener.ProcessListener;

public class RunClassInSeparateJvmMachine {
    static private final Logger log = LoggerFactory.getLogger(RunClassInSeparateJvmMachine.class);

    private final String name;
    private OutputStream output;
    StartedProcess activeProcess;
    private final AtomicBoolean restarting;
    // Main class that will be used to load...
    private final String classNameWithMainToRun;
    private final String classpath;
    private final List<String> jvmArguments;
    // basedir of project this plugin run in
    private final File mavenBaseDir;

    public RunClassInSeparateJvmMachine(
            String name,
            String classNameWithMainToRun,
            List<String> classpath, 
            List<String> jvmArguments,
            File mavenBaseDir) {

        this(name,
            classNameWithMainToRun,
            StringUtils.join(classpath, File.pathSeparator),
            jvmArguments,
            mavenBaseDir);

    }
    
    public RunClassInSeparateJvmMachine(
            String name,
            String classNameWithMainToRun,
            String classpath, 
            List<String> jvmArguments,
            File mavenBaseDir) {

        this.name = name;
        this.output = System.out;
        this.classNameWithMainToRun = classNameWithMainToRun;
        this.classpath = classpath;
        this.jvmArguments = jvmArguments;
        this.mavenBaseDir = mavenBaseDir;
        this.restarting = new AtomicBoolean(false);

    }

    public OutputStream getOutput() {
        return output;
    }

    public void setOutput(OutputStream output) {
        this.output = output;
    }

    public StartedProcess getActiveProcess() {
        return activeProcess;
    }

    public void setActiveProcess(StartedProcess activeProcess) {
        this.activeProcess = activeProcess;
    }

    public void restart() {
        restarting.set(true);
        try {
        
            if (this.activeProcess != null) {
                
                log.debug("Attempting to destroy previous {} process", name);
                this.activeProcess.getProcess().destroy();
                
                log.debug("Waiting for previous {} process to terminate", name);
                ProcessResult result =
                    this.activeProcess.getFuture().get();
                
            }
            
            log.debug("Starting new {}", name);
            this.activeProcess = startProcess();
            
        } catch (ExecutionException | InterruptedException | IOException e) {
            log.error("Something fishy happenend. Unable to cleanly restart {}!", name, e);
            log.error("You'll probably need to restart maven?");
        } finally {
            restarting.set(false);
        }
    }

    StartedProcess startProcess() throws IOException {
        ProcessExecutor processExecutor
            = buildProcessExecutor();
    
        return processExecutor.start();
    }

    ProcessExecutor buildProcessExecutor() {
        
        List<String> commandLine = new ArrayList<>();

        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator
                + "java";
        
        commandLine.add(javaBin);

        commandLine.addAll(jvmArguments);

        commandLine.add("-cp");
        commandLine.add(classpath);
        commandLine.add(classNameWithMainToRun);
        
        // not redirecting error stream used for unit tests
        return new ProcessExecutor(commandLine)
            .directory(mavenBaseDir)
            .destroyOnExit()
            .addListener(new ProcessListener() {
                @Override
                public void afterStop(Process process) {
                    if (!restarting.get()) {
                        log.error("JVM process for {} terminated (next file change will attempt to restart it)", name);
                    }
                }
            })
            .redirectErrorStream(true)
            .redirectOutput(this.output);
    }

}
