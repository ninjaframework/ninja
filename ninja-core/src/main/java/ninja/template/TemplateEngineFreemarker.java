package ninja.template;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import ninja.Context;
import ninja.Route;
import ninja.Router;

import com.google.inject.Inject;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class TemplateEngineFreemarker implements TemplateEngine {

	private String FILE_SUFFIX = ".ftl.html";
	private final Router router;

	@Inject
	TemplateEngineFreemarker(Router router) {
		this.router = router;

	}

	@Override
	public void invoke(Context context, Object object) {

		Map map;

		if (!(object instanceof Map)) {
			throw new RuntimeException(
					"Freemarker Templating engine can only render Map of Strings...");

		} else {
			map = (Map) object;
		}

		String templateName = context.getTemplateName();
		// compute default route if view is not set explicitly
		if (templateName == null) {

			Route route = router.getRouteFor(context.getHttpServletRequest()
					.getRequestURI());

			templateName = String.format("views/%s/%s%s", route.getController()
					.getSimpleName(), route.getControllerMethod(), FILE_SUFFIX);
		}

		// 1st => determine which

		Configuration cfg = new Configuration();
		// Specify the data source where the template files come from.
		// Here I set a file directory for it:
		try {

			cfg.setClassForTemplateLoading(this.getClass(), "/");
			Template freemarkerTemplate = cfg.getTemplate(templateName);

			// convert tuples:

			Writer out = new OutputStreamWriter(context
					.getHttpServletResponse().getOutputStream());

			freemarkerTemplate.process(map, out);

			out.flush();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String getSuffixOfTemplatingEngine() {
		return FILE_SUFFIX;

	}

}
