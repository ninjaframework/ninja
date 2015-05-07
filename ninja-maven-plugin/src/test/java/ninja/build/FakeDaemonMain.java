package ninja.build;

public class FakeDaemonMain {
     
    public static void main(String[] args) {
        
        System.out.println("Hello, i am a fake daemon");
        try {
            Thread.sleep(10000000L);
        } catch (InterruptedException e) {
            System.exit(0);
        }
        
    }
    
}