package ninja.utils;

import java.util.Random;


public class SecretGenerator {
    
    /**
     * Generates a random String of length 64. This string is suitable
     * as secret for your application (key "application.secret" in conf/application.conf).
     * 
     * @return A string that can be used as "application.secret".
     */
    public static String generateSecret() {
        
        return generateSecret(new Random());

    }
    
    /**
     * !!!! Only for testing purposes !!!!
     * 
     * Usually you want to use {@link SecretGenerator#generateSecret()}
     * 
     * @param random the random generator to use. Usually new Random(), but for testing you can
     *          use a predefined seed.
     * @return A String suitable as random secret for eg signing a session.
     */
    protected static String generateSecret(Random random) {
        
        String charsetForSecret = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        
        StringBuffer stringBuffer = new StringBuffer();
        
        for (int i = 0; i < 64; i++) {
            
            int charToPoPickFromCharset = random.nextInt(charsetForSecret.length());            
            stringBuffer.append(charsetForSecret.charAt(charToPoPickFromCharset));
            
        }

        return stringBuffer.toString(); 
        
    }
    


}
