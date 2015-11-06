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

package ninja.diagnostics;

import com.google.common.collect.ImmutableMap;
import ninja.Context;
import ninja.NinjaDefault;
import ninja.Result;
import ninja.Results;
import ninja.utils.NinjaProperties;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author joelauer
 */
@RunWith(MockitoJUnitRunner.class)
public class DiagnosticErrorRendererTest {
    
    @Mock
    private Context context;
    
    @Test
    public void buildAndRender401UnauthorizedDiagnosticError() throws Exception {

        when(context.getMethod()).thenReturn("GET");
        when(context.getContextPath()).thenReturn("/");
        
        Result testResult = Results.unauthorized();
        
        DiagnosticError de = DiagnosticErrorBuilder.build401UnauthorizedDiagnosticError();
        
        Assert.assertEquals(-1, de.getLineNumberOfError());
        Assert.assertEquals(-1, de.getLineNumberOfSourceLines());
        Assert.assertNull(de.getThrowable());
        
        DiagnosticErrorRenderer renderer = DiagnosticErrorRenderer.build(context, testResult, de);
        
        String out = renderer.render();
        
        Assert.assertTrue(out.contains("Diagnostic Error"));
        Assert.assertTrue(out.contains("401"));
        
    }
    
    @Test
    public void buildAndRenderDiagnosticErrorWithHTMLEntities() throws Exception {

        NinjaDefault ninja = Mockito.spy(new NinjaDefault());
        
        doReturn(Boolean.TRUE).when(ninja).isDiagnosticsEnabled();
        
        // build context that will have HTML entites in key spots
        
        when(context.getMethod()).thenReturn("GET");
        when(context.getContextPath()).thenReturn("/");
        when(context.getAttributes()).thenReturn(ImmutableMap.of("TEST-ATTR", (Object)"Attribute with < > & entities"));
        
        Result testResult = ninja.getInternalServerErrorResult(context, new Exception("Exception message with < > & entities"));
        
        // renderable in testResult is the DiagnosticError
        
        Assert.assertNotNull(testResult);
        Assert.assertNotNull(testResult.getRenderable());
        Assert.assertThat(testResult.getRenderable(), instanceOf(DiagnosticError.class));
        
        DiagnosticError de = (DiagnosticError)testResult.getRenderable();
        
        DiagnosticErrorRenderer renderer = DiagnosticErrorRenderer.build(context, testResult, de);
        
        String out = renderer.render();

        Assert.assertThat(out, containsString("Attribute with &lt; &gt; &amp; entities"));
        Assert.assertThat(out, containsString("Exception message with &lt; &gt; &amp; entities"));
    }
    
}
