/**
 * Copyright (C) 2013 the original author or authors.
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

package ninja.grizzly;

import ninja.utils.ResponseStreams;
import org.glassfish.grizzly.http.server.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * @author Thomas Broyer
 */
public class ResponseStreamsGrizzly implements ResponseStreams {

    private Response response;

    public void init(Response response) {
        this.response = response;

    }

    public OutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

    public Writer getWriter() throws IOException {
        return response.getWriter();
    }
}
