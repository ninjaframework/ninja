package ninja;


public class Results {
	
	public static Result status(int statusCode) {
		
		Result result = new Result(statusCode);
		return result;
		
	}

    public static Result ok() {
        return status(Result.SC_200_OK);
    }

    public static Result notFound() {
        return status(Result.SC_404_NOT_FOUND);
    }

    public static Result forbidden() {
        return status(Result.SC_403_FORBIDDEN);
    }

    public static Result badRequest() {
        return status(Result.SC_400_BAD_REQUEST);
    }

    public static Result noContent() {
        return status(Result.SC_204_NO_CONTENT);
    }

	public static Result internalServerError() {
		return status(Result.SC_500_INTERNAL_SERVER_ERROR);
	}

	/**
	 * A redirect that uses 303 see other.
	 * @param url The url used as redirect target.
	 * @return A nicely configured result with status code 303 and the url set as Location header.
	 */
    public static Result redirect(String url) {
		
		Result result = status(Result.SC_303_SEE_OTHER);
		result.addHeader(Result.LOCATION, url);
		
		return result;
	}
    
	/**
	 * A redirect that uses 307 see other.
	 * @param url The url used as redirect target.
	 * @return A nicely configured result with status code 308 and the url set as Location header.
	 */
    public static Result redirectTemporary(String url) {
		
		Result result = status(Result.SC_307_TEMPORARY_REDIRECT);
		result.addHeader(Result.LOCATION, url);
		
		return result;
	}
	
	public static Result contentType(String contentType) {
		Result result = status(Result.SC_200_OK);
		result.contentType(contentType);

		return result;
	}
	
	public static Result html() {
		Result result = status(Result.SC_200_OK);
		result.contentType(Result.TEXT_HTML);

		return result;
	}
	
	public static Result html(int statusCode) {
		Result result = status(statusCode);
		result.contentType(Result.TEXT_HTML);

		return result;
	}
	
	/**
	 * html should take only int status or nothing.
	 * => otherwise the html(..) methods might not
	 * work properly..
	 * 
	 * please use "render" from Result to do so.
	 * Should be as easy as html().render(myObject)
	 *
	 * @param renderable
	 * @return
	 */
	@Deprecated
	public static Result html(Object renderable) {
		Result result = status(Result.SC_200_OK);
		result.contentType(Result.TEXT_HTML);
		result.render(renderable);

		return result;
	}
	
	/**
	 * Only supporting one overloaded method.
	 * => you can still use json().status(200). and so on...
	 * @param statusCode
	 * @return
	 */
	@Deprecated
	public static Result json(int statusCode) {
		Result result = status(statusCode);
		result.contentType(Result.APPLICATON_JSON);

		return result;
	}
	
	public static Result json() {
		Result result = status(Result.SC_200_OK);
		result.contentType(Result.APPLICATON_JSON);

		return result;
	}
	
	
	public static Result json(Object renderable) {
		Result result = status(Result.SC_200_OK);
		result.contentType(Result.APPLICATON_JSON);
		result.render(renderable);

		return result;
	}
	
	
	public static Result xml() {
		Result result = status(Result.SC_200_OK);
		result.contentType(Result.APPLICATION_XML);

		return result;
	}
	
	public static Result TODO() {
		Result result = status(Result.SC_501_NOT_IMPLEMENTED);
		result.contentType(Result.APPLICATON_JSON);

		return result;
	}

    public static AsyncResult async() {
        return new AsyncResult();
    }

}
