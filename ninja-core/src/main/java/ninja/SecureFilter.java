package ninja;

public class SecureFilter implements Filter {

	@Override
	public void filter(Context context) {
		System.out.println("secure filter!");		
	}

}
