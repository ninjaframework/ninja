package ninja;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.inject.Provider;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.Email;
import net.sf.oval.constraint.EmailCheck;
import ninja.params.Param;

import com.google.common.collect.Lists;

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

			final Class[] paramTypes = method.getParameterTypes();
			final Annotation[][] paramAnnotations = method
					.getParameterAnnotations();

			for (int i = 0; i < paramTypes.length; i++) {

				// if this is the context we just add it right away:
				if (paramTypes[i].equals(Context.class)) {
					objectsForMethod.add(i, context);
				} else

					for (Annotation a : paramAnnotations[i]) {

						if (a instanceof Param) {

							System.out.println("got a parameter...");
							
							Param param  = (Param) a;
							
							String path = context.getPathParameter(param.value());
							
							objectsForMethod.add(i, path);

						}
						
						if (a.annotationType().getPackage())
						
						if (a instanceof Email) {
							
							String noEmail = "noemail";							
							
							System.out.println("check email.");
							
							Validator validator = new Validator();
							validator.addChecks(noEmail.getClass(), new Check EmailCheck());
							
							List<ConstraintViolation> violations = validator.validate(noEmail);
							
							for (ConstraintViolation constraintViolation : violations) {
								System.out.println(constraintViolation);
							}
							
						}

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
