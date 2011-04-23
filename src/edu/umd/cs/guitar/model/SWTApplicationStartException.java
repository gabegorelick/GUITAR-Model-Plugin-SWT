package edu.umd.cs.guitar.model;

/**
 * Thrown to indicate that SWTGuitar was unable to start the SWT application
 * under test.
 * 
 * @author Gabe Gorelick
 * 
 */
public class SWTApplicationStartException extends Exception {

	private static final long serialVersionUID = 4311888908754360804L;

	/**
	 * Construct a new {@code SWTApplicationStartException} with no detail
	 * message or cause.
	 * 
	 * @see Exception#Exception()
	 */
	// inheritDoc doesn't work for constructors
	public SWTApplicationStartException() {
		super();
	}

	/**
	 * Construct a new {@code SWTApplicationStartException} with the given
	 * detail message.
	 * 
	 * @param message
	 *            the detail message
	 * 
	 * @see Exception#Exception(String)
	 */
	public SWTApplicationStartException(String message) {
		super(message);
	}

	/**
	 * Construct a new {@code SWTApplicationStartException} with the given
	 * cause.
	 * 
	 * @param cause
	 *            the cause of this exception
	 * @see Exception#Exception(Throwable)
	 */
	public SWTApplicationStartException(Throwable cause) {
		super(cause);
	}

	/**
	 * Construct a new {@code SWTApplicationStartException} with the given
	 * detail message and cause.
	 * 
	 * @param message
	 *            the detail message
	 * @param cause
	 *            the cause of this exception
	 * 
	 * @see Exception#Exception(String, Throwable)
	 */
	public SWTApplicationStartException(String message, Throwable cause) {
		super(message, cause);
	}
}
