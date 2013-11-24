/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja;

import static java.lang.Thread.sleep;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RestartAfterSomeTimeAndChanges extends Thread {

    volatile boolean restart;
    private NinjaJettyInsideSeparateJvm revolver;

    public RestartAfterSomeTimeAndChanges(
            NinjaJettyInsideSeparateJvm revolver) {
        restart = false;
        this.revolver = revolver;

    }

    @Override
    public void run() {

        while (true) {
            
            try {

                sleep(50);
                if (restart) {
                    restart = false;

                    System.out.println("restarting really...");
                    revolver.restartNinjaJetty();

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    
    public void triggerRestart() {

        restart = true;
    }
    
}
