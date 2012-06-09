package ninja;

import java.io.IOException;

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

			try {
				// FIXME:
				context.getHttpServletResponse().getWriter()
						.print("not found... sorry");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
