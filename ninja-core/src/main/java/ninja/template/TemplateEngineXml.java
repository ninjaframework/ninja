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

package ninja.template;

import java.io.IOException;
import java.io.OutputStream;

import javax.inject.Singleton;

import ninja.Context;
import ninja.Result;
import ninja.utils.ResponseStreams;

import org.slf4j.Logger;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;

@Singleton
public class TemplateEngineXml implements TemplateEngine {

    private final Logger logger;
    
    private final XmlMapper xmlMapper;

    @Inject
    public TemplateEngineXml(Logger logger, XmlMapper xmlMapper) {
        this.logger = logger;
        this.xmlMapper = xmlMapper;
    }

    @Override
    public void invoke(Context context, Result result) {

        ResponseStreams responseStreams = context.finalizeHeaders(result);
        
        try {
            
            OutputStream outputStream  = responseStreams.getOutputStream();
            xmlMapper.writeValue(outputStream, result.getRenderable());
            outputStream.close();
            
        } catch (IOException e) {

            logger.error("Error while rendering json", e);
        }
        

    }

    @Override
    public String getContentType() {
        return Result.APPLICATON_JSON;
    }

    @Override
    public String getSuffixOfTemplatingEngine() {
        // intentionally returns null...
        return null;
    }
}
