package ninja;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ninja.utils.MimeTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This controller serves public resources under /public
 * 
 * @author ra
 * 
 */
@Singleton
public class AssetsController {
	
	private Logger logger = LoggerFactory.getLogger(AssetsController.class);

	/** Used as seen by http request */
	final String PUBLIC_PREFIX = "/assets/";

	/** Used for storing files locally */
	final String ASSETS_PREFIX = "assets/";

	private final MimeTypes mimeTypes;

	@Inject
	public AssetsController(MimeTypes mimeTypes) {
		this.mimeTypes = mimeTypes;
		
	}
	
	public Result serve(Context context) {
		Object renderable = new Renderable() {
			
			@Override
			public void render(Context context, Result result) {
				
				String finalName = context.getRequestPath()
				        .replaceFirst(PUBLIC_PREFIX, "");
				
				InputStream inputStream = this.getClass().getClassLoader()
				        .getResourceAsStream(ASSETS_PREFIX + finalName);	
				
				// check if stream exists. if not print a notfound exception
				if (inputStream == null) {
					
					context.finalizeHeaders(Results.status(404));

				} else {
					try {						
						result.status(200);
						
						//try to set the mimetype:
						String mimeType = mimeTypes.getContentType(context, finalName);

						if (!mimeType.isEmpty()) {
							result.contentType(mimeType);
						}
						
						//finalize headers:
						context.finalizeHeaders(result);
						
						
						ByteStreams.copy(this.getClass().getClassLoader()
						        .getResourceAsStream(ASSETS_PREFIX + finalName),
						        context.getHttpServletResponse().getOutputStream());
						
					} catch (FileNotFoundException e) {
						logger.error("error streaming file", e);
					} catch (IOException e) {
						logger.error("error streaming file", e);
					}
					
				}
				
				
			}
		};
		
		
		
		return Results.status(200).render(renderable);


	}

}
