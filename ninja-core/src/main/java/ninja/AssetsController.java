/**
 * Copyright (C) 2012-2015 the original author or authors.
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

package ninja;

import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import ninja.utils.HttpCacheToolkit;
import ninja.utils.MimeTypes;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.lang.StringUtils;

/**
 * This controller serves public resources under /assets.
 * It is capable of serving static files, webjars or files from a completely
 * different directory on the server.
 */
@Singleton
public class AssetsController {

    private final static Logger logger = LoggerFactory
            .getLogger(AssetsController.class);
    
    public final static String ASSETS_DIR = "assets";
    
    public final static String FILENAME_PATH_PARAM = "fileName";

    private final MimeTypes mimeTypes;

    private final HttpCacheToolkit httpCacheToolkit;

    private final NinjaProperties ninjaProperties;
    
    private final AssetsControllerHelper assetsControllerHelper;

    @Inject
    public AssetsController(AssetsControllerHelper assetsControllerHelper,
                            HttpCacheToolkit httpCacheToolkit,
                            MimeTypes mimeTypes,
                            NinjaProperties ninjaProperties) {
        this.assetsControllerHelper = assetsControllerHelper;
        this.httpCacheToolkit = httpCacheToolkit;
        this.mimeTypes = mimeTypes;
        this.ninjaProperties = ninjaProperties;
    }

    /**
     * Serves resources from the assets directory of your application.
     * 
     * For instance:
     * route: /robots.txt
     * A request to /robots.txt will be served from /assets/robots.txt.
     * 
     * You can also use a path like the following to serve files:
     * route: /assets/{fileName: .*}
     * 
     * matches
     * /assets/app/app.css 
     * and will return
     * /assets/app/app.css (from your jar).
     * 
     */
    public Result serveStatic() {
        Object renderable = new Renderable() {
            @Override
            public void render(Context context, Result result) {
                String fileName = getFileNameFromPathOrReturnRequestPath(context);
                URL url = getStaticFileFromAssetsDir(fileName);
                streamOutUrlEntity(url, context, result);
            }
        };
        return Results.ok().render(renderable);
    }
    
    
    /**
     * Serves resources from the assets directory of your application.
     * 
     * For instance:
     * A request to /robots.txt will be served from /assets/robots.txt.
     * Request to /public/css/app.css will be served from /assets/css/app.css.
     * 
     */
    public Result serveWebJars() {
        Object renderable = new Renderable() {
            @Override
            public void render(Context context, Result result) {
                String fileName = getFileNameFromPathOrReturnRequestPath(context);
                URL url = getStaticFileFromMetaInfResourcesDir(fileName);
                streamOutUrlEntity(url, context, result);
            }
        };
        return Results.ok().render(renderable);
    }
    
    private void streamOutUrlEntity(URL url, Context context, Result result) {
        // check if stream exists. if not print a notfound exception
        if (url == null) {
            context.finalizeHeadersWithoutFlashAndSessionCookie(Results.notFound());
        } else {
            try {
                URLConnection urlConnection = url.openConnection();
                Long lastModified = urlConnection.getLastModified();
                httpCacheToolkit.addEtag(context, result, lastModified);

                if (result.getStatusCode() == Result.SC_304_NOT_MODIFIED) {
                    // Do not stream anything out. Simply return 304
                    context.finalizeHeadersWithoutFlashAndSessionCookie(result);
                } else {
                    result.status(200);

                    // Try to set the mimetype:
                    String mimeType = mimeTypes.getContentType(context,
                            url.getFile());

                    if (mimeType != null && !mimeType.isEmpty()) {
                        result.contentType(mimeType);
                    }

                    ResponseStreams responseStreams = context
                            .finalizeHeadersWithoutFlashAndSessionCookie(result);

                    try (InputStream inputStream = urlConnection.getInputStream();
                        OutputStream outputStream = responseStreams.getOutputStream()) {
                        ByteStreams.copy(inputStream, outputStream);
                    }

                }

            } catch (IOException e) {
                logger.error("error streaming file", e);
            }

        }

    }

    /**
     * Loads files from assets directory. This is the default directory
     * of Ninja where to store stuff. Usually in src/main/java/assets/.
     * But if user wants to use a dir outside of application project dir, then base dir can
     * be overridden by static.asset.base.dir in application conf file.
     */
    private URL getStaticFileFromAssetsDir(String fileName) {

        URL url = null;

        if (ninjaProperties.isDev() 
                // Testing that the file exists is important because
                // on some dev environments we do not get the correct asset dir
                // via System.getPropery("user.dir").
                // In that case we fall back to trying to load from classpath
                && new File(assetsDirInDevModeWithoutTrailingSlash()).exists()) {
            String finalNameWithoutLeadingSlash = assetsControllerHelper.normalizePathWithoutLeadingSlash(fileName, false);
            File possibleFile = new File(
                    assetsDirInDevModeWithoutTrailingSlash() 
                            + File.separator 
                            + finalNameWithoutLeadingSlash);
            url = getUrlForFile(possibleFile);
        } else {
            String finalNameWithoutLeadingSlash = assetsControllerHelper.normalizePathWithoutLeadingSlash(fileName, true);
            url = this.getClass().getClassLoader()
                    .getResource(ASSETS_DIR
                                 + "/"
                                 + finalNameWithoutLeadingSlash);
        }

        return url;
    }

    private URL getUrlForFile(File possibleFileInSrc) {
        if (possibleFileInSrc.exists() && !possibleFileInSrc.isDirectory()) {
          try {
            return possibleFileInSrc.toURI().toURL();
          } catch(MalformedURLException malformedURLException) {
            logger.error("Error in dev mode while streaming files from src dir. ", malformedURLException);
          }
        }
        return null;
    }

    /**
     * Loads files from META-INF/resources directory.
     * This is compatible with Servlet 3.0 specification and allows
     * to use e.g. webjars project.
     * 
     */
    private URL getStaticFileFromMetaInfResourcesDir(String fileName) {
        String finalNameWithoutLeadingSlash 
                = assetsControllerHelper.normalizePathWithoutLeadingSlash(fileName, true);
        URL url = null; 
        url = this.getClass().getClassLoader().getResource("META-INF/resources/webjars/" + finalNameWithoutLeadingSlash);
        return url;
    }

    private static String getFileNameFromPathOrReturnRequestPath(Context context) {

        String fileName = context.getPathParameter(FILENAME_PATH_PARAM);

        if (fileName == null) {
            fileName = context.getRequestPath();
        }
        return fileName;

    }

    /**
     * Used in dev mode: Streaming directly from src dir without jetty reload.
     */
    private String assetsDirInDevModeWithoutTrailingSlash() {
        String srcDir  = System.
                getProperty("user.dir")
                + File.separator
                + "src"
                + File.separator
                + "main"
                + File.separator
                + "java";
        return srcDir + File.separator + ASSETS_DIR;
    }
}
