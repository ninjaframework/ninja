package ninja.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * Make sure to only write to either the OutputStream
 * OR the Writer...
 * 
 * @author rbauer
 *
 */
public interface ResponseStreams {

    /**
     * Get the output stream to write the response.
     *
     * Must not be used if getWriter has been called.
     *
     * @return The output stream
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Get the writer to write the response.
     *
     * Must not be used if getOutputStream has been called.
     *
     * @return The writer
     */
    Writer getWriter() throws IOException;

}
