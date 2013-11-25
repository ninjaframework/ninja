package ninja;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.base.Joiner;

public class RunClassInSeparateJvmMachine {

    private Process processCurrentlyActive;

    // Main class that will be used to load...
    private String classNameWithMainToRun;

    private List<String> classpath;

    public RunClassInSeparateJvmMachine(
            String classNameWithMainToRun,
            List<String> classpath) {

        this.classNameWithMainToRun = classNameWithMainToRun;

        this.classpath = classpath;

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

        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator
                + "java";

        String pathSeparator = System.getProperty("path.separator");

        String classpathAsString = Joiner.on(pathSeparator).join(classpath);

        
        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpathAsString,
                classNameWithMainToRun);
        
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
