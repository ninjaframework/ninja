/**
 * Copyright (C) 2015 Fizzed, Inc.
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

package ninja.rocker;

import com.fizzed.rocker.RenderingException;
import com.fizzed.rocker.RockerOutput;
import com.fizzed.rocker.runtime.ArrayOfByteArraysOutput;
import com.fizzed.rocker.runtime.DefaultRockerTemplate;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import ninja.Context;
import ninja.Result;
import ninja.exceptions.InternalServerErrorException;
import ninja.utils.ResponseStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * New base class for all Rocker templates targeted for the Ninja Framework.
 * Permits templates to be directly used as a <code>Renderable</code> in a
 * Result. Also adds all <i>implicit</i> variables that Ninja's default
 * FreeMarker template system includes.
 * 
 * @author joelauer
 * @param <T>
 * 
 * @author Fizzed, Inc (http://fizzed.com)
 * @author joelauer (http://twitter.com/jjlauer)
 */
public abstract class NinjaRockerTemplate<T extends NinjaRockerTemplate> extends DefaultRockerTemplate<T> {
    static private final Logger log = LoggerFactory.getLogger(NinjaRockerTemplate.class);
    
    protected NinjaRocker N;
    
    /**
     * Configure this template from another template.  Most important is that
     * the "implicit" variables from the other template are copied into this
     * template.
     * @param <T>
     * @param other
     * @throws RenderingException 
     */
    @Override
    protected <T> void __configure(T other) throws RenderingException {
        super.__configure(other);
        if (other instanceof NinjaRockerTemplate) {
            NinjaRockerTemplate otherTemplate = (NinjaRockerTemplate)other;
            // share the ninja+rocker context
            this.N = otherTemplate.N;
        } else {
            throw new RenderingException("Unable to configure template (not an instance of " + this.getClass().getName() + ")");
        }
    }
    
    @Override
    protected RockerOutput __newOutput() {
        // output optimized for array of byte arrays...
        return new ArrayOfByteArraysOutput(__internal.getCharset());
    }
    
    /**
     * Implements <code>Renderable</code> for Ninja result so that this template
     * can be directly used in a Result. Not intended to be called by application
     * directly.
     * 
     * @param ninjaRockerContext  The ninja+rocker context
     * @param context The context of the request
     * @param result The result we are rendering for
     */
    public void ninjaRender(NinjaRockerContext ninjaRockerContext, Context context, Result result) {
        
        this.N = new NinjaRocker(ninjaRockerContext, context, result);
   
        // set content type if not set
        if (result.getContentType() == null) {
            switch (this.__internal.getContentType()) {
                case HTML:
                    result.html();
                    break;
                case RAW:
                    result.contentType(Result.APPLICATION_OCTET_STREAM);
                    break;
            }
        }
        
        // looks like jetty gives us a single shot to write out the bytes
        // to the underlying output stream -- chunked results could be more
        // interesting to look at down the road
        RockerOutput out = null;
        
        try {
            // try to render output
            out = this.render();
        } catch (RenderingException e) {
            throwRenderingException(context, result, e);
        }
        
        ArrayOfByteArraysOutput abao = (ArrayOfByteArraysOutput)out;
        
        // rendering was successful, finalize headers, and write it to output
        ResponseStreams responseStreams = context.finalizeHeaders(result);
        
        try (OutputStream os = responseStreams.getOutputStream()) {
            os.write(abao.toByteArray());
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }
    
    public void throwRenderingException(
            Context context,
            Result result,
            RenderingException cause) {
        
        // likely project source code
        String sourcePath = null;
        if (this.__internal.getTemplatePackageName() != null
                && this.__internal.getTemplateName() != null) {
            sourcePath = this.__internal.getTemplatePackageName().replace(".", File.separator)
                    + File.separator
                    + this.__internal.getTemplateName();
        }
        
        throw new ninja.exceptions.RenderingException(
                cause.getMessage(),
                cause,
                result,
                "Rocker rendering exception",
                sourcePath,
                cause.getSourceLine());
    }
}
