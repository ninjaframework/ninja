package ninja;

/**
 * That's just a simple tuple. Nothing to see here...
 * 
 * @author ra
 *
 * @param <X>
 * @param <Y>
 */
public class Tuple<X, Y> {

	private X x;
	private Y y;

	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;

	}

	public X getX() {
		return this.x;
	}

	public Y getY() {
		return this.y;
	}

}
