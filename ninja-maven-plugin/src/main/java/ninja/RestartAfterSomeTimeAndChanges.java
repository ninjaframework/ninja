/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja;

import static java.lang.Thread.sleep;
import java.util.concurrent.atomic.AtomicInteger;

public class RestartAfterSomeTimeAndChanges extends Thread {

    AtomicInteger atomicInteger;
    private NinjaJettyInsideSeparateJvm revolver;

    public RestartAfterSomeTimeAndChanges(
            AtomicInteger atomicInteger,
            NinjaJettyInsideSeparateJvm revolver) {
        
        this.atomicInteger = atomicInteger;
        this.revolver = revolver;

    }

    @Override
    public void run() {

        while (true) {
            
            try {

                sleep(50);
                if (atomicInteger.get() > 0) {
                    atomicInteger.set(0);

                    System.out.println("restarting really...");
                    revolver.restartNinjaJetty();

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
