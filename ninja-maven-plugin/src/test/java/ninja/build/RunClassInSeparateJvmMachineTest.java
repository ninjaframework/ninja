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

package ninja.build;

import ninja.build.RunClassInSeparateJvmMachine;
import com.google.code.tempusfugit.temporal.Condition;
import com.google.code.tempusfugit.temporal.Duration;
import com.google.code.tempusfugit.temporal.Timeout;
import com.google.code.tempusfugit.temporal.WaitFor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.zeroturnaround.exec.ProcessResult;
import org.zeroturnaround.exec.StartedProcess;

@RunWith(MockitoJUnitRunner.class)
public class RunClassInSeparateJvmMachineTest {
    
    public List<String> getOurClassPath() throws IOException {
        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(File.pathSeparator);
        
        return Arrays.asList(classpathEntries);
    }
    
    public Condition fakeDaemonCondition(final ByteArrayOutputStream baos) {
        return new Condition() {
            @Override
            public boolean isSatisfied() {
                try {
                    return baos.toString("UTF-8").trim().equals("Hello, i am a fake daemon");
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
    }
    
    @Test
    public void startProcess() throws Exception {
        
        RunClassInSeparateJvmMachine rcsjm = new RunClassInSeparateJvmMachine (
            "FakeDaemon",
            FakeDaemonMain.class.getName(),
            getOurClassPath(),
            Collections.EMPTY_LIST,
            new File(".")
        );
        
        // override output so we can capture it
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        rcsjm.setOutput(baos);
        
        StartedProcess startedProcess = rcsjm.startProcess();
        
        Assert.assertTrue(startedProcess.getProcess().isAlive());
        
        WaitFor.waitOrTimeout(fakeDaemonCondition(baos), Timeout.timeout(Duration.millis(10000)));
        
        startedProcess.getProcess().destroy();
        
        ProcessResult processResult = startedProcess.getFuture().get();
        
    }
    
    @Test
    public void restart() throws Exception {
        
        RunClassInSeparateJvmMachine rcsjm = new RunClassInSeparateJvmMachine (
            "FakeDaemon",
            FakeDaemonMain.class.getName(),
            getOurClassPath(),
            Collections.EMPTY_LIST,
            new File(".")
        );
        
        // override output so we can capture it
        final ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        
        rcsjm.setOutput(baos1);
        
        rcsjm.restart();
        
        StartedProcess startedProcess1 = rcsjm.getActiveProcess();
        
        Assert.assertTrue(startedProcess1.getProcess().isAlive());

        WaitFor.waitOrTimeout(fakeDaemonCondition(baos1), Timeout.timeout(Duration.millis(10000)));
        
        
        // override output so we can capture it
        final ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        
        rcsjm.setOutput(baos2);
        
        rcsjm.restart();
        
        StartedProcess startedProcess2 = rcsjm.getActiveProcess();
        
        Assert.assertTrue(startedProcess2.getProcess().isAlive());
        
        Assert.assertFalse(startedProcess1.getProcess().isAlive());
        
        Assert.assertFalse(startedProcess1.equals(startedProcess2));

        WaitFor.waitOrTimeout(fakeDaemonCondition(baos2), Timeout.timeout(Duration.millis(10000)));
        
    }
    
    @Test
    public void multipleRestartsReallyQuickly() throws Exception {
        
        RunClassInSeparateJvmMachine rcsjm = new RunClassInSeparateJvmMachine (
            "FakeDaemon",
            FakeDaemonMain.class.getName(),
            getOurClassPath(),
            Collections.EMPTY_LIST,
            new File(".")
        );
        
        // override output so we can capture it
        final ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        
        rcsjm.setOutput(baos1);
        
        rcsjm.restart();
        
        StartedProcess startedProcess1 = rcsjm.getActiveProcess();
        
        rcsjm.restart();
        
        StartedProcess startedProcess2 = rcsjm.getActiveProcess();
        
        rcsjm.restart();
        
        StartedProcess startedProcess3 = rcsjm.getActiveProcess();
        
        rcsjm.restart();
        
        StartedProcess startedProcess4 = rcsjm.getActiveProcess();
        
        Assert.assertTrue(startedProcess4.getProcess().isAlive());
        
        Assert.assertFalse(startedProcess1.getProcess().isAlive());
        
        Assert.assertFalse(startedProcess2.getProcess().isAlive());
        
        Assert.assertFalse(startedProcess3.getProcess().isAlive());
        
        Assert.assertFalse(startedProcess1.equals(startedProcess2));
        
    }
    
}
