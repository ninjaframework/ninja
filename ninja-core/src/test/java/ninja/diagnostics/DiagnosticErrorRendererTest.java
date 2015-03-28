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

import ninja.Context;
import ninja.Result;
import ninja.Results;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
    
}
