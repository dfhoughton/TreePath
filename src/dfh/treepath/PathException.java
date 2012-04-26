package dfh.treepath;

/**
 * Generally signifies a path compilation error.
 * <p>
 * 
 * @author David F. Houghton - Apr 22, 2012
 * 
 */
public class PathException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PathException() {
	}

	public PathException(String arg0) {
		super(arg0);
	}

	public PathException(Throwable arg0) {
		super(arg0);
	}

	public PathException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
