package ninja;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.util.logging.Level;
import java.util.logging.Logger;
import ninja.utils.NinjaConstant;

public class RunClassInSeparateJvmMachine {

    private Process processCurrentlyActive;

    // Main class that will be used to load...
    private String classNameWithMainToRun;

    private List<String> classpath;
    
    // Context path for web app.
    private String contextPath;

    public RunClassInSeparateJvmMachine(
            String classNameWithMainToRun,
            List<String> classpath, 
            String contextPath) {

        this.classNameWithMainToRun = classNameWithMainToRun;

        this.classpath = classpath;
        
        this.contextPath = contextPath;

        // initial startup
        try {
            processCurrentlyActive = startNewNinjaJetty();

        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                
                processCurrentlyActive.destroy();
                
                try {
                    
                    processCurrentlyActive.waitFor();
                    
                } catch (InterruptedException ex) {
                    
                    Logger.getLogger(RunClassInSeparateJvmMachine.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

    }

    public synchronized void restartNinjaJetty() {

        try {
            processCurrentlyActive.destroy();
            processCurrentlyActive.waitFor();
            processCurrentlyActive = startNewNinjaJetty();
        } catch (InterruptedException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private Process startNewNinjaJetty() throws IOException,
            InterruptedException {
        
        List<String> commandLine = Lists.newArrayList();
        
        

        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator
                + "java";
        
        commandLine.add(javaBin);

        String systemPropertyDevMode 
                = "-D" + NinjaConstant.MODE_KEY_NAME + "=" + NinjaConstant.MODE_DEV;
        
        commandLine.add(systemPropertyDevMode);
        
        if (contextPath != null) {
            String systemPropertyContextPath = "-Dninja.context=" + contextPath;
            commandLine.add(systemPropertyContextPath);
        }

        String pathSeparator = System.getProperty("path.separator");

        String classpathAsString = Joiner.on(pathSeparator).join(classpath);
        commandLine.add("-cp");
        commandLine.add(classpathAsString);
        commandLine.add(classNameWithMainToRun);
        
        ProcessBuilder builder = new ProcessBuilder(commandLine);
        
        builder.directory(new File(System.getProperty(NinjaMavenPluginConstants.USER_DIR)));

        builder.redirectErrorStream(true);

        
        Process process = builder.start();

        StreamGobbler outputGobbler = new StreamGobbler(
                process.getInputStream());
        outputGobbler.start();

        return process;
    }

    /** 
     * Just a stupid StreamGobbler that will print out all stuff from
     * the "other" process...
     */
    private static class StreamGobbler extends Thread {

        InputStream is;

        private StreamGobbler(InputStream is) {
            this.is = is;
        }

        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

}
