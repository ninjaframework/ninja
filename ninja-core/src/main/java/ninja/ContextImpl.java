package ninja;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.inject.Inject;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class ContextImpl implements Context {
	

	private HttpServletRequest httpServletRequest;

	private HttpServletResponse httpServletResponse;
	
	private Router router;

	@Inject
	public ContextImpl(Router router) {
		
		this.router = router;
	}
	
	
	/**
	 * Default render method.
	 * 
	 * when execute by:
	 * /controller/ApplicationController.index(Context context)
	 * displays the following template:
	 * /views/ApplicationController/index
	 */
	public void render(Tuple<String, String>... tuples) {
		
		// 1st => determine which 

		Configuration cfg = new Configuration();
		// Specify the data source where the template files come from.
		// Here I set a file directory for it:
		try {
			
			Route route = router.getRouteFor(httpServletRequest.getServletPath());

			String defaultTemplatePath = String.format("views/%s/%s.ftl.html", 
					route.getController().getSimpleName(), 
					route.getControllerMethod());
			
			cfg.setClassForTemplateLoading(this.getClass(), "/");
			Template freemarkerTemplate = cfg
					.getTemplate(defaultTemplatePath);

			//convert tuples:
			
			Map<String, String> map = new HashMap<String, String>();
			
			
			for (Tuple<String, String> tuple : tuples) {
				map.put(tuple.getX(), tuple.getY());
				
			}

			
			
			Writer out = new OutputStreamWriter(
					httpServletResponse.getOutputStream());

			freemarkerTemplate.process(map, out);

			out.flush();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void renderJson(Object object) {

		Gson gson = new Gson();
		String json = gson.toJson(object);  
		
		try {
			httpServletResponse.getOutputStream().print(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
		this.httpServletRequest = httpServletRequest;
		
	}

	public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
		
	}
	
	public void setHttServletResponse(HttpServletResponse httServletResponse) {
		this.httpServletResponse = httServletResponse;
	}

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}
	
	public HttpServletResponse getHttpServletResponse() {
		return this.httpServletResponse;
	}
	@Override
	public String getPathParameter(String key) {
		
		//FIXME: not really efficient...
		Route route = router.getRouteFor(httpServletRequest.getServletPath());
		
		return route.getParameters(httpServletRequest.getServletPath()).get(key);

	}

	@Override
	public void render(HTTP_STATUS httpStatus) {
		
		if (httpStatus.equals(HTTP_STATUS.notFound_404)) {
			
			try {
				httpServletResponse.setStatus(404);
				httpServletResponse.getOutputStream().print("oop2ssdddsdsds. not found...");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}


	@Override
	public void redirect(String url) {
		
		try {
			httpServletResponse.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
