package ninja;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import ninja.Context.HTTP_STATUS;

import com.google.common.io.ByteStreams;
import com.google.inject.Singleton;

/**
 * This controller serves public resources under /public
 * 
 * @author ra
 * 
 */
@Singleton
public class PublicController {

	public void serve(Context context) {

		File file = new File(context.getHttpServletRequest().getRequestURI());

		System.out.println(this.getClass().getClassLoader().getResourceAsStream("assets/bootstrap.css"));
		
		try {
	        ByteStreams.copy(
	        		this.getClass().getClassLoader().getResourceAsStream("assets/bootstrap.css"), 
	        		context
	                .getHttpServletResponse().getOutputStream());
        } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		System.out.println("file is: " + file.getAbsolutePath());
		// Default rendering is simple by convention
		// This renders the page in views/ApplicationController/index.ftl.html
		context.status(HTTP_STATUS.ok200);

	}

}
