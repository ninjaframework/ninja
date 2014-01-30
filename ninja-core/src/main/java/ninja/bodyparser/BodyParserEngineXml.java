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

package ninja.bodyparser;

import java.io.IOException;

import ninja.ContentTypes;
import ninja.Context;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.LoggerFactory;

@Singleton
public class BodyParserEngineXml implements BodyParserEngine {
    
    private final static Logger logger = LoggerFactory.getLogger(BodyParserEngineXml.class);
    
    private final XmlMapper xmlMapper;
    

    @Inject
    public BodyParserEngineXml(XmlMapper xmlMapper) {
        
        this.xmlMapper = xmlMapper;

    }

    public <T> T invoke(Context context, Class<T> classOfT) {
        T t = null;

        try {
            
            t = xmlMapper.readValue(context.getInputStream(), classOfT);

        } catch (JsonParseException e) {
            logger.error("Error parsing incoming Xml", e);
        } catch (JsonMappingException e) {
            logger.error("Error parsing incoming Xml", e);
        } catch (IOException e) {
            logger.error("Error parsing incoming Xml", e);
        }

        return t;
    }
    
    public String getContentType() {
        return ContentTypes.APPLICATION_XML; 
    }

}
