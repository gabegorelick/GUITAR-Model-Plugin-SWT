/*	
 *  Copyright (c) 2011-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.model;

/**
 * Thrown to indicate that SWTGuitar was unable to start the SWT application
 * under test.
 * 
 * @author Gabe Gorelick
 * 
 */
public class SitarApplicationStartException extends Exception {

	private static final long serialVersionUID = 4311888908754360804L;

	/**
	 * Construct a new {@code SitarApplicationStartException} with no detail
	 * message or cause.
	 * 
	 * @see Exception#Exception()
	 */
	// inheritDoc doesn't work for constructors
	public SitarApplicationStartException() {
		super();
	}

	/**
	 * Construct a new {@code SitarApplicationStartException} with the given
	 * detail message.
	 * 
	 * @param message
	 *            the detail message
	 * 
	 * @see Exception#Exception(String)
	 */
	public SitarApplicationStartException(String message) {
		super(message);
	}

	/**
	 * Construct a new {@code SitarApplicationStartException} with the given
	 * cause.
	 * 
	 * @param cause
	 *            the cause of this exception
	 * @see Exception#Exception(Throwable)
	 */
	public SitarApplicationStartException(Throwable cause) {
		super(cause);
	}

	/**
	 * Construct a new {@code SitarApplicationStartException} with the given
	 * detail message and cause.
	 * 
	 * @param message
	 *            the detail message
	 * @param cause
	 *            the cause of this exception
	 * 
	 * @see Exception#Exception(String, Throwable)
	 */
	public SitarApplicationStartException(String message, Throwable cause) {
		super(message, cause);
	}
}
