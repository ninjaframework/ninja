package ninja.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

/**
 * Make sure to only write to either the OutputStream
 * OR the Writer...
 * 
 * @author rbauer
 *
 */
public class ResponseStreamsServlet implements ResponseStreams {

	private HttpServletResponse httpServletResponse;

	public void init(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
		
	}
	
    /**
     * Get the output stream to write the response.
     *
     * Must not be used if getWriter has been called.
     *
     * @return The output stream
     */
    public OutputStream getOutputStream() throws IOException {
    	return httpServletResponse.getOutputStream();
    }

    /**
     * Get the writer to write the response.
     *
     * Must not be used if getOutputStream has been called.
     *
     * @return The writer
     */
    public Writer getWriter() throws IOException {
    	return httpServletResponse.getWriter();
    }

}
