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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ninja;


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
                    
                    System.out.println("Restarting SuperDevMode");
                    
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
