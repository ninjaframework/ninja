package ninja;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;

import ninja.utils.MimeTypes;

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
				
				String finalName = context.getHttpServletRequest().getRequestURI()
				        .replaceFirst(PUBLIC_PREFIX, "");
				
				InputStream inputStream = this.getClass().getClassLoader()
				        .getResourceAsStream(ASSETS_PREFIX + finalName);	
				
				// check if stream exists. if not print a notfound exception
				if (inputStream == null) {
					
					context.finalizeHeaders(Results.status(404));

				} else {
					try {
						
						result.status(200);
						
						String mimeType = mimeTypes.getContentType(context, finalName);
						if (!mimeType.isEmpty()) {
							result.contentType(mimeType);
						}
						
						context.finalizeHeaders(result);
						
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
					
				}
				
				
			}
		};
		
		
		
		return Results.status(200).render(renderable);


	}

}
