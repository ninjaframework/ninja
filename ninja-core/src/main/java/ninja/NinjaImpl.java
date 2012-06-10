package ninja;

import java.io.IOException;

import ninja.Context.HTTP_STATUS;

import com.google.inject.Inject;

public class NinjaImpl implements Ninja {

	Router router;

	@Inject
	public NinjaImpl(Router router) {

		this.router = router;

	}

	/**
	 * I want to get a session request etc pp...
	 * 
	 * @param uri
	 */
	public void invoke(Context context) {

		Route route = router.getRouteFor(context.getHttpServletRequest()
				.getServletPath());

		if (route != null) {
			route.invoke(context);
		} else {
			// throw a 404
			context.render(HTTP_STATUS.notFound_404);

		}

	}

}
