package edu.umd.cs.guitar.model;

/**
 * Thrown to indicate that SWT GUITAR was unable to start the SWT application
 * under test.
 * 
 * @author Gabe Gorelick
 * 
 */
public class SWTApplicationStartException extends Exception {

	private static final long serialVersionUID = 4311888908754360804L;

	public SWTApplicationStartException() {
		super();
	}

	/**
	 * throws the exception with this message
	 * @param message
	 */
	public SWTApplicationStartException(String message) {
		super(message);
	}

	/**
	 * throws exception with cause
	 * @param cause
	 */
	public SWTApplicationStartException(Throwable cause) {
		super(cause);
	}
	/**
	 * throws exception wtih cause and message
	 * 
	 * @param message
	 * @param cause
	 */
	public SWTApplicationStartException(String message, Throwable cause) {
		super(message, cause);
	}
}
