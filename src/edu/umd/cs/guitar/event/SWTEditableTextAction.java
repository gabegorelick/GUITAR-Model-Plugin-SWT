package edu.umd.cs.guitar.event;

import org.eclipse.swt.widgets.Text;

import edu.umd.cs.guitar.model.GComponent;

public class SWTEditableTextAction extends SWTAction {

	public SWTEditableTextAction() {
		// this space left intentionally blank
	}
	
	public static String GUITAR_DEFAULT_TEXT = "GUITAR DEFAULT TEXT: "
		+ "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

	/**
	 * Sets text on the given component to {@link #GUITAR_DEFAULT_TEXT}.
	 * 
	 * @throws ClassCastException
	 *             thrown if argument is not an {@link SWTText}
	 * @throws NullPointerException
	 *             thrown if argument holds a null {@link Widget}
	 */
	@Override
	public void perform(GComponent gComponent) {
		Text widget = (Text) getWidget(gComponent);
		widget.setText(GUITAR_DEFAULT_TEXT);		
	}
}
