/*
 * Copyright (C) 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.servlet.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.ThresholdingOutputStream;

/**
 * Special output stream implementation used by {@link InMemoryFileItem}.
 */
public class InMemoryFileOutputStream extends ThresholdingOutputStream {

    private final ByteArrayOutputStream out;

    public InMemoryFileOutputStream(int threshold) {
        super(threshold);
        out = new ByteArrayOutputStream();
    }

    /**
     * Gets data currently written to this output stream.
     *
     * @return array of written bytes
     */
    public byte[] getData() {
        return out.toByteArray();
    }

    /**
     * Gets the number of currently written bytes.
     *
     * @return number of written bytes
     */
    public long size() {
        return getByteCount();
    }

    @Override
    protected OutputStream getStream() throws IOException {
        return out;
    }

    @Override
    protected void thresholdReached() throws IOException {
        String msg = String.format(
                "Size limit reached for file uploads. Application does not accept file uploads with sizes more than %d bytes",
                getThreshold());
        throw new IOException(msg);
    }

}
