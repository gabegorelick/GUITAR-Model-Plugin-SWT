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
package edu.umd.cs.guitar.event;

import org.eclipse.swt.widgets.Text;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.swtwidgets.SitarText;

/**
 * An action for {@code Text}s. This event sets the text of the given
 * {@link Text} to {@link #GUITAR_DEFAULT_TEXT}.
 * 
 * @see SitarText
 */
public class SitarEditableTextAction extends SitarAction {

	/**
	 * Default constructor.
	 */
	public SitarEditableTextAction() {
		// this space left intentionally blank
	}
	
	/**
	 * String to set the contents of the {@code Text} to.
	 */
	public static final String GUITAR_DEFAULT_TEXT = "GUITAR DEFAULT TEXT: "
		+ "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

	/**
	 * Sets text on the given component to {@link #GUITAR_DEFAULT_TEXT}.
	 * 
	 * @throws ClassCastException
	 *             thrown if argument is not an {@link SitarText}
	 * @throws NullPointerException
	 *             thrown if argument holds a null widget
	 */
	@Override
	public void perform(GComponent gComponent) {
		final Text widget = (Text) getWidget(gComponent);
		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				widget.setText(GUITAR_DEFAULT_TEXT);
			}
		});		
	}
}
