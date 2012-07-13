package ninja;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import ninja.params.Param;
import ninja.params.PathParam;

import com.google.common.collect.Lists;
import com.google.inject.Provider;

/**
 * The end of the filter chain
 * 
 * @author James Roper
 */
class FilterChainEnd implements FilterChain {
	private final Provider<?> controllerProvider;
	private final Method method;

	FilterChainEnd(Provider<?> controllerProvider, Method method) {
		this.controllerProvider = controllerProvider;
		this.method = method;
	}

	@Override
	public Result next(Context context) {

		Result result;

		try {

			List<Object> objectsForMethod = Lists.newArrayList();

			// get both the parameters...
			final Class[] paramTypes = method.getParameterTypes();
			// ... and all annotations for the parameters
			final Annotation[][] paramAnnotations = method
			        .getParameterAnnotations();

			// now we skip through the parameters and process the annotations
			for (int i = 0; i < paramTypes.length; i++) {

				// if this is the context we just add it right away:
				if (paramTypes[i].equals(Context.class)) {
					objectsForMethod.add(i, context);
					
					//If it is a string we try to get the string parameters
				} else if (paramTypes[i].equals(String.class)) {

					for (Annotation a : paramAnnotations[i]) {

						if (a instanceof Param) {

							Param param = (Param) a;
							String parameter = context.getParameter(param
							        .value());
							objectsForMethod.add(i, parameter);

						} else if (a instanceof PathParam) {

							PathParam pathParam = (PathParam) a;
							String pathParameter = context
							        .getPathParameter(pathParam.value());
							objectsForMethod.add(i, pathParameter);

						} else {
							//that's an annotation i don't know...
						}

						// if (a.annotationType().getPackage())
						// validation is work in progress:
						// if (a instanceof Email) {
						//
						// String noEmail = "noemail";
						//
						// System.out.println("check email.");
						//
						// Validator validator = new Validator();
						// validator.addChecks(noEmail.getClass(), new
						// EmailCheck());
						//
						// List<ConstraintViolation> violations =
						// validator.validate(noEmail);
						//
						// for (ConstraintViolation constraintViolation :
						// violations) {
						// System.out.println(constraintViolation);
						// }
						//
						// }

					}

					//if we don't know the class => we do nothing and add null
				} else {
					objectsForMethod.add(i, null);
				}

			}

			result = (Result) method.invoke(controllerProvider.get(),
			        objectsForMethod.toArray());

			if (result instanceof AsyncResult) {
				// Make sure handle async has been called
				context.handleAsync();
				Result newResult = context.controllerReturned();
				if (newResult != null) {
					result = newResult;
				}
			}

		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			} else {
				throw new RuntimeException(e.getCause());
			}
		}

		return result;
	}
}
