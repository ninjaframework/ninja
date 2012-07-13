package ninja.classesfortest;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;

import com.google.inject.Singleton;

/**
 * This is a controller.
 * 
 * We use it to test if method invocation works.
 * Usually I would mock it, but as reflection stuff is made up of final classes
 * Mockito can't help us...
 * 
 * Therefore we have this simple controller with some methods we can check :)
 * 
 * @author ra
 *
 */
@Singleton
public class TestDummyController {
	
	//these parameters can be retrieved by the test:
	public boolean noParameterMethodExecuted = false;
	public boolean contextMethodExecuted = false;
	public boolean paramMethodExecuted = false;
	public boolean pathParamMethodExecuted = false;
	public boolean completelyMixedMethodSignatureExecuted = false;
	
	
	public Context context = null;
	
	public String param1 = null;
	public String pathParam1 = null;
	public String param2 = null;
	public String pathParam2 = null;
	
	

	public Result noParameter() {
		noParameterMethodExecuted = true;
		return Results.html();

	}
	public Result context(Context context) {
		contextMethodExecuted = true;
		this.context = context;
		return Results.html();

	}
	
	public Result param(Context context, @Param("param1") String param1) {
		paramMethodExecuted = true;
		this.context = context;
		this.param1 = param1;
		return Results.html();

	}
	
	public Result pathParam(Context context, @PathParam("pathParam1") String pathParam1) {
		pathParamMethodExecuted = true;
		this.context = context;
		this.pathParam1 = pathParam1;
		return Results.html();

	}

	
	public Result completelyMixedMethodSignature(@Param("param1") String param1, @PathParam("pathParam1") String pathParam1, Context context, @Param("param2") String param2, @PathParam("pathParam2") String pathParam2) {
		completelyMixedMethodSignatureExecuted = true;
		this.context = context;
		this.param1 = param1;
		this.pathParam1 = pathParam1;
		this.param2 = param2;
		this.pathParam2 = pathParam2;
		return Results.html();

	}

}
