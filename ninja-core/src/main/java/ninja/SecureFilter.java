package ninja;

import javax.servlet.http.Cookie;

import ninja.Context.HTTP_STATUS;

public class SecureFilter implements Filter {

	boolean continueExecution = true;
	
	@Override
	public void filter(Context context) {
		
		Cookie[] cookies = context.getHttpServletRequest().getCookies();
			
		// if we got no cookies we break:
		if (cookies == null) {
			continueExecution = false;
			context.status(HTTP_STATUS.forbidden403).template("/views/forbidden403.ftl.html").renderHtml();
			
		} else {
			
			for (int i = 0; i < cookies.length; i++) {
				
				Cookie cookie = cookies[i];
				if (cookie.getName().equals("NINJA_COOKIE")) {
					
					System.out.println("got cookie...");
					
					//do nothing and continue...
					
				} else {

					context.status(HTTP_STATUS.forbidden403).template("/views/forbidden403.ftl.html").renderHtml();
					
					continueExecution = false;
					break;
				}
				
			} 
		
			
		}

	}

	@Override
    public boolean continueExecution() {
	    return continueExecution;	    
    }

}
