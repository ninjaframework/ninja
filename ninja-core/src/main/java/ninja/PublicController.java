package ninja;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimetypesFileTypeMap;

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

	/** Used as seen by http request */
	final String PUBLIC_PREFIX = "/assets/";

	/** Used for storing files locally */
	final String ASSETS_PREFIX = "assets/";

	public void serve(Context context) {

		String finalName = context.getHttpServletRequest().getRequestURI()
		        .replaceFirst(PUBLIC_PREFIX, "");

		
		InputStream inputStream = this.getClass().getClassLoader()
		        .getResourceAsStream(ASSETS_PREFIX + finalName);
		
		
		// check if stream exists. if not print a notfound exception
		if (inputStream == null) {
			
			context.status(HTTP_STATUS.notFound404);
			context.render();

		} else {

			try {
				ByteStreams.copy(this.getClass().getClassLoader()
				        .getResourceAsStream(ASSETS_PREFIX + finalName),
				        context.getHttpServletResponse().getOutputStream());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Default rendering is simple by convention
			// This renders the page in
			// views/ApplicationController/index.ftl.html
			context.status(HTTP_STATUS.ok200);
		}

	}

}
