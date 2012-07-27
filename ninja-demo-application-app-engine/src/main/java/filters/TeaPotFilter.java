package filters;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;

/**
 * Just a simple demo filter that changes exemplifies two things 1. Change the
 * output of the response 2. Change the status code. 3. Stops execution of all
 * other filters and the route method itself.
 * 
 * We are simply using 418 I'm a teapot (RFC 2324) .
 * 
 * @author ra
 * 
 */
public class TeaPotFilter implements Filter {

	@Override
	public Result filter(FilterChain chain, Context context) {

		return Results.html().status(418).template("/views/TeaPotFilter/TeaPot.ftl.html");

	}
}
