package ninja.template;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import ninja.Context;
import ninja.Route;
import ninja.Router;
import ninja.utils.NinjaConstant;

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

			Route route = router.getRouteFor(
					context.getHttpServletRequest().getMethod(),
					context.getHttpServletRequest().getRequestURI());
			
			
			Class controller = route.getController();
			
			// Calculate the correct path of the template.
			// We always assume the template in the subdir "views"
			
			// 1) If we are in the main project => /views/ControllerName/templateName.ftl.html
			// 2) If we are in a plugin / subproject
			//    => some/packages/submoduleName/views/ControllerName/templateName.ftl.html
			
			// So let's calculate the parent package of the controller:
			String controllerPackageName = controller.getPackage().getName();
			// This results in something like controllers or some.package.controllers
			
			// Let's remove "controllers" so we cat all parent packages:
			String parentPackageOfController = controllerPackageName.replaceAll(NinjaConstant.CONTROLLERS_DIR, "");
			
			// And now we rewrite everything from "." notation to directories /
			String parentControllerPackageAsPath = parentPackageOfController.replaceAll("\\.", "/");
			
			
			// and the final path of the controller will be something like:
			// some/package/views/ControllerName/templateName.ftl.html
			templateName = String.format("%sviews/%s/%s%s", parentControllerPackageAsPath, controller
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
