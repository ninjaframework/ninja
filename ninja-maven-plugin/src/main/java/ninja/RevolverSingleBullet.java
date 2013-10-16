//package ninja;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.net.URLClassLoader;
//
//public class RevolverSingleBullet {
//
//    Process process0;
//
//    // Main class that will be used to load...
//    private Class klass;
//    
//    private String classpath;
//
//    public RevolverSingleBullet(Class klass, String classpath) {
//        this.klass = klass;
//        this.classpath = classpath;
//
//        // initial startup
//        try {
//            process0 = exec(klass);
//
//        } catch (IOException | InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//
//        }
//
//        Runtime.getRuntime().addShutdownHook(new Thread() {
//            @Override
//            public void run() {
//                System.out.println("Inside Add Shutdown Hook");
//                process0.destroy();
//
//            }
//        });
//        System.out.println("Shut Down Hook Attached.");
//
//    }
//
//    public synchronized void pullTheTrigger() {
//
//        try {
//            process0.destroy();
//            process0.waitFor();
//            process0 = exec(klass);
//        } catch (InterruptedException | IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }
//
//    public Process exec(Class klass) throws IOException,
//            InterruptedException {
//
//        ClassLoader cl = ClassLoader.getSystemClassLoader();
//
//        URL[] urls = ((URLClassLoader) cl).getURLs();
//
//        for (URL url : urls) {
//            System.out.println(url.getFile());
//        }
//
//        String javaHome = System.getProperty("java.home");
//        String javaBin = javaHome + File.separator + "bin" + File.separator
//                + "java";
//        // String classpath = System.getProperty("java.class.path") + ":" +
//        // "/Users/ra/bibliothek/coden/workspace_t35/ninja-maven-plugin/target/classes/";
//
//        // String className = klass.getCanonicalName();
//
// 
//        System.out.println("path: " + classpath);
//        String className = klass.getCanonicalName();
//        //
//        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath,
//                className);
//        //
//
//        builder.redirectErrorStream(true);
//
//        Process process = builder.start();
//
//        // StreamGobbler errorGobbler = new StreamGobbler(
//        // process.getErrorStream(), "ERROR");
//        //
//        // // any output?
//        StreamGobbler outputGobbler = new StreamGobbler(
//                process.getInputStream(), "OUTPUT");
//        //
//        // // start gobblers
//        outputGobbler.start();
//        // errorGobbler.start();
//
//        return process;
//    }
//
//    private static class StreamGobbler extends Thread {
//        InputStream is;
//        String type;
//
//        private StreamGobbler(InputStream is, String type) {
//            this.is = is;
//            this.type = type;
//        }
//
//        @Override
//        public void run() {
//            try {
//                InputStreamReader isr = new InputStreamReader(is);
//                BufferedReader br = new BufferedReader(isr);
//                String line = null;
//                while ((line = br.readLine()) != null)
//                    System.out.println(type + "> " + line);
//            } catch (IOException ioe) {
//                ioe.printStackTrace();
//            }
//        }
//    }
//
//}
