package ninja;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class NinjaServletDispatcher implements Filter {

	Injector injector;
	
	Ninja ninja;

	public void init(FilterConfig filterConfig) throws ServletException {
		
		 injector = Guice.createInjector(new conf.Configuration());		 
		 ninja = injector.getInstance(Ninja.class);

	}

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		Context context = injector.getProvider(Context.class).get();
		
		context.setHttpServletRequest(request);
		context.setHttpServletResponse(response);
		
		ninja.invoke(context);

	}

	public void destroy() {

		System.out.println("destroy...");

	}

}
