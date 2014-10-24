/**
 * Copyright (C) 2012-2014 the original author or authors.
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
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
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

    private final static Logger logger = LoggerFactory
            .getLogger(AssetsController.class);
    
    public final static String ASSETS_DIR = "assets";
    
    public final static String FILENAME_PATH_PARAM = "fileName";

    /** Used as seen by http request */
    //remove when removing "serve" method.
    @Deprecated
    final String PUBLIC_PREFIX = "/" + ASSETS_DIR + "/";

    /** Used for storing files locally */
    //remove when removing "serve" method.
    @Deprecated
    final String ASSETS_PREFIX_WITH_TRAILING_SLASH = ASSETS_DIR + "/";
    
    /** Used for dev mode streaming directly from src dir without jetty reload. */
    final String srcDir  = System.getProperty("user.dir")
                + File.separator 
                + "src" 
                + File.separator 
                + "main" 
                + File.separator
                + "java";  

    private final String defaultAssetBaseDir;

    private final Optional<String> assetBaseDir;

    private final MimeTypes mimeTypes;

    private final HttpCacheToolkit httpCacheToolkit;

    private final NinjaProperties ninjaProperties;

    @Inject
    public AssetsController(HttpCacheToolkit httpCacheToolkit,
                            MimeTypes mimeTypes,
                            NinjaProperties ninjaProperties) {
        
        this.httpCacheToolkit = httpCacheToolkit;
        this.mimeTypes = mimeTypes;
        this.ninjaProperties = ninjaProperties;
        this.assetBaseDir = getNormalizedAssetPath(ninjaProperties);
        this.defaultAssetBaseDir = srcDir + File.separator + ASSETS_DIR + File.separator;
    }

    /**
     * Deprecated. Please use serveDir or serveFile.
     */
    @Deprecated
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

                streamOutUrlEntity(url, context, result);

            }
        };

        return Results.ok().render(renderable);

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
    public Result serveStatic(Context context) {
        Object renderable = new Renderable() {

            @Override
            public void render(Context context, Result result) {

                String fileName = getFileNameFromPathOrReturnRequestPath(context);
                
                URL url = getStaticFileFromAssetsDir(context, fileName);

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
    public Result serveWebJars(Context context) {
        Object renderable = new Renderable() {

            @Override
            public void render(Context context, Result result) {
                    
                String fileName = getFileNameFromPathOrReturnRequestPath(context);
   
                URL url = getStaticFileFromMetaInfResourcesDir(context, fileName);

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

                            if (mimeType != null
                                    && !mimeType.isEmpty()) {
                                result.contentType(mimeType);
                            }

                            // finalize headers:
                            ResponseStreams responseStreams = context
                                    .finalizeHeadersWithoutFlashAndSessionCookie(result);
                            
                            try (
                                InputStream inputStream = urlConnection.getInputStream();
                                OutputStream outputStream = responseStreams.getOutputStream()) {

                                ByteStreams.copy(inputStream, outputStream);
                            }


                        } 

                    } catch (FileNotFoundException e) {
                        logger.error("error streaming file", e);
                    } catch (IOException e) {
                        logger.error("error streaming file", e);
                    }

                }
    
    
    }
    
    /**
     * Loads files from assets directory. This is the default diretory
     * of Ninja where to store stuff. Usually in src/main/java/assets/.
     * 
     * @deprecated Please remove once "serve" method has been removed.
     */
    @Deprecated
    private URL getFileFromAssetsDir(Context context) {
        // We need simplifyPath to remove relative paths before we process it.
        // Otherwise an attacker can read out arbitrary urls via ".."
        String finalName = Files.simplifyPath(context.getRequestPath())
                .replaceFirst(PUBLIC_PREFIX, "");

        URL url = null;
        
        // This allows to directly stream assets from src directory.
        // Therefore jetty does not have to reload.
        // Especially cool when developing js apps inside assets folder.
        if (ninjaProperties.isDev()) {

            File possibleFileInSrc = new File(
                    srcDir + File.separator + ASSETS_PREFIX_WITH_TRAILING_SLASH + finalName);

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
                    .getResource(ASSETS_PREFIX_WITH_TRAILING_SLASH + finalName);
        }

        return url;
    }
    
    /**
     * Loads files from assets directory. This is the default directory
     * of Ninja where to store stuff. Usually in src/main/java/assets/.
     * But if user wants to use a dir outside of application project dir, then base dir can
     * be overridden by static.asset.base.dir in application conf file.
     */
    private URL getStaticFileFromAssetsDir(Context context, String fileName) {
        
        String finalNameWithoutLeadingSlash = 
                normalizePathWithoutTrailingSlash(fileName);

        Optional<URL> url = Optional.absent();

        //Serve from the static asset base directory specified by user in application conf.
        if(assetBaseDir.isPresent()){

            File possibleFile = new File(assetBaseDir.get() + File.separator + finalNameWithoutLeadingSlash);

            if(possibleFile.exists()){
                url = getUrlForFile(possibleFile);
            }
        }

        // If asset base dir not specified by user, this allows to directly stream assets from src directory.
        // Therefore jetty does not have to reload. Especially cool when developing js apps inside assets folder.
        if (ninjaProperties.isDev() && !url.isPresent()) {

            File possibleFile = new File(defaultAssetBaseDir + finalNameWithoutLeadingSlash);

            if (possibleFile.exists()) {
                url = getUrlForFile(possibleFile);
            }
        }

        if (!url.isPresent()) {

            // In mode test and prod, if static.asset.base.dir not specified then we stream via the classloader.
            //
            // In dev mode: If we cannot find the file in src we are also looking for the file
            // on the classpath (can be the case for plugins that ship their own assets.
            url = Optional.fromNullable( this.getClass().getClassLoader()
                    .getResource(
                            ASSETS_DIR
                                    + "/"
                                    + finalNameWithoutLeadingSlash));
        }

        return url.orNull();
    }

    private Optional<URL> getUrlForFile( File possibleFileInSrc) {
        try {
            return  Optional.fromNullable( possibleFileInSrc.toURI().toURL() );

        } catch(MalformedURLException malformedURLException) {

            logger.error("Error in dev mode while streaming files from src dir. ", malformedURLException);
        }
        return Optional.absent();
    }

    /**
     * Loads files from META-INF/resources directory.
     * This is compatible with Servlet 3.0 specification and allows
     * to use e.g. webjars project.
     * 
     */
    private URL getStaticFileFromMetaInfResourcesDir(Context context, String fileName) {

        String finalNameWithoutLeadingSlash 
                = normalizePathWithoutTrailingSlash(fileName);

        URL url = null;
        
        url = this.getClass().getClassLoader().getResource("META-INF/resources/webjars/" + finalNameWithoutLeadingSlash);

        return url;
        
        
    }
    
    /**
     * If we get - for whatever reason - a relative URL like 
     * assets/../conf/application.conf we expand that to the "real" path.
     * In the above case conf/application.conf.
     * 
     * You should then add the assets prefix.
     * 
     * Otherwise someone can create an attack and read all resources of our
     * app. If we expand and normalize the incoming path this is no longer
     * possible.
     * 
     * @param fileName A potential "fileName"
     * @return A normalized fileName.
     */
    public String normalizePathWithoutTrailingSlash(String fileName) {
    
        // We need simplifyPath to remove relative paths before we process it.
        // Otherwise an attacker can read out arbitrary urls via ".."
        String fileNameNormalized = Files.simplifyPath(fileName);
        
        if (fileNameNormalized.charAt(0) == '/') {
            return fileNameNormalized.substring(1);
        }
        
        return fileNameNormalized;
    }

    
    
    /**
     * Loads files from META-INF/resources directory.
     * This is compatible with Servlet 3.0 specification and allows
     * to use e.g. webjars project.
     * 
     * @deprecated Please remove once "serve" method has been removed.
     */
    @Deprecated
    private URL getFileFromMetaInfResourcesDir(Context context) {
        
        // We need simplifyPath to remove relative paths before we process it.
        // Otherwise an attacker can read out arbitrary urls via ".."
        String finalName = Files.simplifyPath(context.getRequestPath()).replaceFirst(PUBLIC_PREFIX, "");

        URL url = null;
        
        url = this.getClass().getClassLoader().getResource("META-INF/resources/" + finalName);

        return url;
        
        
    }
    
    /**
     * Checks if path begins with correct prefix. 
     * @deprecated Please remove once "serve" method has been deprecated.
     */
    @Deprecated
    private boolean isFileIsValidAssetFile(Context context) {
        
        // We need simplifyPath to remove relative paths before we process it.
        // Otherwise an attacker can read out arbitrary urls via ".."
        String finalName = Files.simplifyPath(context.getRequestPath());
        
        if (finalName.startsWith(PUBLIC_PREFIX)) {
            return true;
        } else {
            return false;
        }
        
    }

    public static String getFileNameFromPathOrReturnRequestPath(Context context) {

        String fileName = context.getPathParameter(FILENAME_PATH_PARAM);

        if (fileName == null) {
            fileName = context.getRequestPath();
        }
        return fileName;

    }

    private Optional<String> getNormalizedAssetPath(NinjaProperties ninjaProperties){
        String baseDir = ninjaProperties.get( NinjaConstant.APPLICATION_STATIC_ASSET_BASEDIR );
        return Optional.fromNullable(FilenameUtils.normalizeNoEndSeparator(baseDir));
    }
}
