/**
 * Copyright (C) 2012-2018 the original author or authors.
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

package ninja.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import ninja.utils.ResponseStreams;

/**
 * Make sure to only write to either the OutputStream OR the Writer...
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
    @Override
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
    @Override
    public Writer getWriter() throws IOException {
        return httpServletResponse.getWriter();
    }

}
