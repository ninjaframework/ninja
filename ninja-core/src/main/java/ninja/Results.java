package ninja;


public class Results {
	
	public static Result status(int statusCode) {
		
		Result result = new Result(statusCode);
		return result;
		
	}
	
	public static Result redirect(String url) {
		
		Result result = status(Result.SC_307_TEMPORARY_REDIRECT);
		result.addHeader(Result.LOCATION, url);
		
		return result;
	}
	
	public static Result html() {
		Result result = status(Result.SC_200_OK);
		result.setContentType(Result.TEXT_HTML);

		return result;
	}
	
	public static Result html(int statusCode) {
		Result result = status(statusCode);
		result.setContentType(Result.TEXT_HTML);

		return result;
	}
	
	public static Result html(Object renderable) {
		Result result = status(Result.SC_200_OK);
		result.setContentType(Result.TEXT_HTML);
		result.render(renderable);

		return result;
	}
	
	public static Result json() {
		Result result = status(Result.SC_200_OK);
		result.setContentType(Result.APPLICATON_JSON);

		return result;
	}
	
	public static Result json(Object renderable) {
		Result result = status(Result.SC_200_OK);
		result.setContentType(Result.APPLICATON_JSON);
		result.render(renderable);

		return result;
	}
	
	public static Result TODO() {
		Result result = status(Result.SC_501_NOT_IMPLEMENTED);
		result.setContentType(Result.APPLICATON_JSON);

		return result;
	}

    public static AsyncResult async() {
        return new AsyncResult();
    }

}
