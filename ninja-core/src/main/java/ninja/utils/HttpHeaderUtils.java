package ninja.utils;

public class HttpHeaderUtils {
    
    
    /**
     * A http content type should contain a character set like
     * "application/json; charset=utf-8".
     * 
     * If you only want to get "application/json" you can use this method.
     * 
     * See also: http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7.1
     * 
     * @param rawContentType "application/json; charset=utf-8" or "application/json"
     * @return only the contentType without charset. Eg "application/json"
     */
    public static String getContentTypeFromContentTypeAndCharacterSetting(String rawContentType) {
        
        if (rawContentType.contains(";")) {
            return rawContentType.split(";")[0];           
        } else {
            return rawContentType;
        }
        
    }
    
    
    /**
     * A http content type should contain a character set like
     * "application/json; charset=utf-8".
     * 
     * If you only want to get the character set you can use this method.
     * 
     * See also: http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7.1
     * 
     * @param rawContentType "application/json; charset=utf-8" or "application/json"
     * @return only the character set like utf-8 OR the defaultEncoding when not set
     */
    public static String getCharsetOfContentType(String rawContentType, String defaultEncoding) {
        
        if (rawContentType.contains("charset=")) {
            String charset = rawContentType.split("charset=") [1];
       
            return charset;
        } else {
            return defaultEncoding;
        }
        
    }
    
    /**
     * A http content type should contain a character set like
     * "application/json; charset=utf-8".
     * 
     * If you only want to get the character set you can use this method.
     * 
     * See also: http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7.1
     * 
     * @param rawContentType "application/json; charset=utf-8" or "application/json"
     * @return only the character set like utf-8 OR utf-8 when not set
     */
    public static String getCharsetOfContentTypeOrUtf8(String rawContentType) {
        return getCharsetOfContentType(rawContentType, NinjaConstant.UTF_8);
  
    }

}
