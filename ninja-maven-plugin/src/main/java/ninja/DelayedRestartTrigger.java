/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja;

import static java.lang.Thread.sleep;

public class DelayedRestartTrigger extends Thread {

    private volatile boolean restart;
    private RunClassInSeparateJvmMachine runClassInSeparateJvmMachine;

    public DelayedRestartTrigger(
            RunClassInSeparateJvmMachine runClassInSeparateJvmMachine) {
        
        restart = false;
        this.runClassInSeparateJvmMachine = runClassInSeparateJvmMachine;

    }

    @Override
    public void run() {

        while (true) {
            
            try {

                sleep(50);
                if (restart) {
                    restart = false;

                    System.out.println("restarting really...");
                    runClassInSeparateJvmMachine.restartNinjaJetty();

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
