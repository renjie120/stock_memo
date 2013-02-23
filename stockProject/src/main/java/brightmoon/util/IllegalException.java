package brightmoon.util;

public class IllegalException extends RuntimeException {
	public IllegalException() {
		super();
	}

	public IllegalException(String s) {
		super(s);
	}

	public IllegalException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = -23456789L;
}
