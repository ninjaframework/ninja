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

package ninja;

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

import org.apache.commons.io.IOUtils;
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

    private static Logger logger = LoggerFactory
            .getLogger(AssetsController.class);

    /** Used as seen by http request */
    final String PUBLIC_PREFIX = "/assets/";

    /** Used for storing files locally */
    final String ASSETS_PREFIX = "assets/";
    
    /** Used for dev mode streaming directly from src dir without jetty reload. */
    final String srcDir  = System.getProperty("user.dir")
                + File.separator 
                + "src" 
                + File.separator 
                + "main" 
                + File.separator
                + "java";  

    private final MimeTypes mimeTypes;

    private final HttpCacheToolkit httpCacheToolkit;

    private NinjaProperties ninjaProperties;

    @Inject
    public AssetsController(HttpCacheToolkit httpCacheToolkit,
                            MimeTypes mimeTypes,
                            NinjaProperties ninjaProperties) {
        
        this.httpCacheToolkit = httpCacheToolkit;
        this.mimeTypes = mimeTypes;
        this.ninjaProperties = ninjaProperties;

    }

    public Result serve(Context context) {
        Object renderable = new Renderable() {

            @Override
            public void render(Context context, Result result) {

                URL url = null;
                
                if (isFileIsValidAssetFile(context)) {
                    
                    url = getFileFromAssetsDir(context);
                    
                    if (url == null) {
                        url = getFileFromMetaInfResourcesDir(context);
                    }
                    
                }


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

                            if (!mimeType.isEmpty()) {
                                result.contentType(mimeType);
                            }

                            // finalize headers:
                            ResponseStreams responseStreams = context
                                    .finalizeHeadersWithoutFlashAndSessionCookie(result);

                            InputStream inputStream = urlConnection
                                    .getInputStream();
                            OutputStream outputStream = responseStreams
                                    .getOutputStream();

                            ByteStreams.copy(inputStream, outputStream);

                            IOUtils.closeQuietly(inputStream);
                            IOUtils.closeQuietly(outputStream);

                        } 

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
    
    /**
     * Loads files from assets directory. This is the default diretory
     * of Ninja where to story stuff. Usually in src/main/java/assets/.
     */
    private URL getFileFromAssetsDir(Context context) {
        String finalName = context.getRequestPath().replaceFirst(
                PUBLIC_PREFIX, "");

        URL url = null;
        
        // This allows to directly stream assets from src directory.
        // Therefore jetty does not have to reload.
        // Especially cool when developing js apps inside assets folder.
        if (ninjaProperties.isDev()) {
            
            File possibleFileInSrc = new File(
                    srcDir + File.separator + ASSETS_PREFIX + finalName);
            
            if (possibleFileInSrc.exists()) {
                
                try {
                    url = possibleFileInSrc.toURI().toURL();
                    
                } catch(MalformedURLException malformedURLException) {
                    
                    logger.error("Error in dev mode while streaming files from src dir. ", malformedURLException);
                }
            }

        }
            
        
        if (url == null) {
            // In mode test and prod we stream via the classloader
            //
            // In dev mode: If we cannot find the file in src we are also looking for the file
            // on the classpath (can be the case for plugins that ship their own assets.
            url = this.getClass().getClassLoader()
                    .getResource(ASSETS_PREFIX + finalName);
        }
        
        
        return url;
        
        
    }
    
    /**
     * Loads files from META-INF/resources directory.
     * This is compatible with Servlet 3.0 specification and allows
     * to use e.g. webjars project.
     */
    private URL getFileFromMetaInfResourcesDir(Context context) {
        
        String finalName = context.getRequestPath().replaceFirst(PUBLIC_PREFIX, "");

        URL url = null;
        
        url = this.getClass().getClassLoader().getResource("META-INF/resources/" + finalName);

        return url;
        
        
    }
    
    /**
     * Checks if path begins with correct prefix. 
     */
    private boolean isFileIsValidAssetFile(Context context) {
        
        String finalName = context.getRequestPath();
        
        if (finalName.startsWith(PUBLIC_PREFIX)) {
            return true;
        } else {
            return false;
        }
        
    }

}
