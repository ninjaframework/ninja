/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2012 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013 Thomas Broyer
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package ninja.grizzly;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ninja.Ninja;
import org.glassfish.grizzly.*;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.io.NIOOutputStream;
import org.glassfish.grizzly.http.util.MimeType;
import org.glassfish.grizzly.http.util.Header;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.grizzly.memory.MemoryManager;
import org.slf4j.Logger;

/**
 * {@link HttpHandler}, which processes requests to static resources served
 * from the classpath in META-INF/resources, or dispatches the request to the
 * Ninja web framework.
 * <p>
 * This class heavily borrows from {@link org.glassfish.grizzly.http.server.StaticHttpHandler}
 * with changes to work with classpath resources, log using Slf4j and fallback
 * to Ninja. It is therefore governed by the CDDL+GPL license, like
 * {@code StaticHttpHandler}.
 *
 * @author Jeanfrancois Arcand
 * @author Alexey Stashok
 * @author Thomas Broyer
 */
public class NinjaHandler extends HttpHandler {

    @Inject
    Ninja ninja;

    @Inject
    Provider<ContextImpl> contextProvider;

    @Inject
    Logger logger;

    // ------------------------------------------------ Methods from HttpHandler


    @Override
    public void service(final Request request, final Response response)
            throws Exception {
        final String uri = getRelativeURI(request);

        if (uri == null || !handleStaticResource(uri, request, response)) {
            ContextImpl context = contextProvider.get();
            context.init(request, response);
            ninja.invoke(context);
        }
    }


    // ------------------------------------------------------- Protected Methods


    protected String getRelativeURI(final Request request) {
        String uri = request.getRequestURI();
        if (uri.contains("..")) {
            return null;
        }

        final String resourcesContextPath = request.getContextPath();
        if (resourcesContextPath != null && !resourcesContextPath.isEmpty()) {
            if (!uri.startsWith(resourcesContextPath)) {
                return null;
            }

            uri = uri.substring(resourcesContextPath.length());
        }

        return uri;
    }

    protected boolean handleStaticResource(String path, final Request request,
                                           final Response response) throws Exception {

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        path = "/META-INF/resources" + path;

        boolean tryIndexHtml;
        if (path.endsWith("/")) {
            path += "index.html";
            tryIndexHtml = false;
        } else {
            tryIndexHtml = true;
        }
        URL resource = cl.getResource(path);
        if (resource == null && tryIndexHtml) {
            resource = cl.getResource(path + "/index.html");
        }

        if (resource == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("Resource not found {}", path);
            }
            return false;
        }

        // If it's not HTTP GET - return method is not supported status
        if (!Method.GET.equals(request.getMethod())) {
            if (logger.isTraceEnabled()) {
                logger.trace("Resource found {}, but HTTP method {} is not allowed",
                        resource, request.getMethod());
            }
            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED_405);
            response.setHeader(Header.Allow, "GET");
            return true;
        }

        URLConnection conn = resource.openConnection();
        pickupContentType(response, conn);

        sendResource(response, conn, null);

        return true;
    }


    // --------------------------------------------------------- Private Methods


    private static void pickupContentType(final Response response,
                                          final URLConnection conn) {
        if (response.getResponse().isContentTypeSet()) {
            return;
        }

        String ct = conn.getContentType();
        if (ct != null) {
            response.setContentType(ct);
            return;
        }

        final String path = conn.getURL().getPath();
        int dot = path.lastIndexOf('.');

        if (dot < 0) {
            response.setContentType(MimeType.get("html"));
            return;
        }

        String ext = path.substring(dot + 1);
        ct = MimeType.get(ext);
        if (ct != null) {
            response.setContentType(ct);
        }
    }

    private void sendResource(final Response response, final URLConnection conn,
                             final CompletionHandler<URLConnection> completionHandler) throws IOException {
        response.setStatus(HttpStatus.OK_200);

        // In case this sendResource(...) is called directly by user - pickup the content-type
        pickupContentType(response, conn);

        // FIXME
        final long length = conn.getContentLengthLong();
        response.setContentLengthLong(length);
        response.addDateHeader(Header.Date, System.currentTimeMillis());

        final int chunkSize = 8192;

        final NIOOutputStream outputStream = response.getNIOOutputStream();

        final NonBlockingDownloadHandler nonBlockingDownloadHandler =
                new NonBlockingDownloadHandler(response, outputStream,
                        conn, completionHandler, chunkSize);

        if (!response.isSuspended()) {
            response.suspend();
        }

        outputStream.notifyCanWrite(nonBlockingDownloadHandler);
    }

    private class NonBlockingDownloadHandler implements WriteHandler {
        // keep the remaining size
        private final URLConnection conn;
        private final CompletionHandler<URLConnection> completionHandler;
        private final Response response;
        private final NIOOutputStream outputStream;
        private final ReadableByteChannel channel;
        private final MemoryManager mm;
        private final int chunkSize;

        private volatile long size;

        NonBlockingDownloadHandler(final Response response,
                                   final NIOOutputStream outputStream, final URLConnection conn,
                                   final CompletionHandler<URLConnection> completionHandler,
                                   final int chunkSize) throws IOException {

            this.response = response;
            this.outputStream = outputStream;
            this.conn = conn;
            this.completionHandler = completionHandler;
            this.chunkSize = chunkSize;

            channel = Channels.newChannel(conn.getInputStream());
            mm = response.getRequest().getContext().getMemoryManager();
            size = conn.getContentLengthLong();
        }

        @Override
        public void onWritePossible() throws Exception {
            logger.debug("[onWritePossible]");
            // send CHUNK of data
            final boolean isWriteMore = sendChunk();

            if (isWriteMore) {
                // if there are more bytes to be sent - reregister this WriteHandler
                outputStream.notifyCanWrite(this);
            }
        }

        @Override
        public void onError(Throwable t) {
            logger.debug("[onError] ", t);
            response.setStatus(500, t.getMessage());
            fail(t);
        }

        /**
         * Send next CHUNK_SIZE of file
         */
        private boolean sendChunk() throws IOException {
            // allocate Buffer
            final Buffer buffer = mm.allocate(chunkSize);
            // mark it available for disposal after content is written
            buffer.allowBufferDispose(true);

            final ByteBuffer dest = buffer.toByteBuffer();
            // read file to the Buffer
            final int justReadBytes = channel.read(dest);
            if (justReadBytes <= 0) {
                complete();
                return false;
            }

            if (buffer.isComposite()) {
                dest.flip();
                buffer.put(dest); // TODO COPY
            }

            // prepare buffer to be written
            buffer.position(justReadBytes);
            buffer.trim();

            // write the Buffer
            outputStream.write(buffer);
            size -= justReadBytes;

            // check the remaining size here to avoid extra onWritePossible() invocation
            if (size <= 0) {
                complete();
                return false;
            }

            return true;
        }

        /**
         * Complete the download
         */
        private void complete() {
            try {
                channel.close();
            } catch (IOException e) {
                response.setStatus(500, e.getMessage());
            }

            try {
                outputStream.close();
            } catch (IOException e) {
                response.setStatus(500, e.getMessage());
            }

            if (completionHandler != null) {
                completionHandler.completed(conn);
            }

            if (response.isSuspended()) {
                response.resume();
            } else {
                response.finish();
            }
        }

        /**
         * Complete the download
         */
        private void fail(final Throwable t) {
            try {
                channel.close();
            } catch (IOException e) {
            }

            try {
                outputStream.close();
            } catch (IOException e) {
            }

            if (completionHandler != null) {
                completionHandler.failed(t);
            }

            if (response.isSuspended()) {
                response.resume();
            } else {
                response.finish();
            }
        }

    }
}
