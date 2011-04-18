package edu.umd.cs.guitar.event;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.swtwidgets.SWTWidget;
/**
 * The action described as being text that can be edited in SWT widgets
 * 
 * 
 * @author Gabe Gorelick-Feldman
 *
 */
public class SWTEditableTextAction extends SWTAction {

	public SWTEditableTextAction() {
		// this space left intentionally blank
	}
	
	private static String GUITAR_DEFAULT_TEXT = "GUITAR DEFAULT TEXT: "
		+ "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

	/**
	 * Check if this event is supported by the given component. Only
	 * {@link Text Texts} that are {@link Text#getEditable() editable} by the
	 * user are supported. 
	 */
	@Override
	public boolean isSupportedBy(GComponent gComponent) {
		if (!(gComponent instanceof SWTWidget)) {
			return false;
		}
		
		SWTWidget swtWidget = (SWTWidget) gComponent;
		Widget widget = swtWidget.getWidget();
		
		if (!(widget instanceof Text)) {
			return false;
		}
		
		// make sure user can edit, and not just programs
		final Text text = (Text) widget;
		final boolean[] editable = { false };
		
		text.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				editable[0] = text.getEditable();
			}
		});
		
		return editable[0];
		
	}
	
	/**
	 * edits the text of the component if possible
	 * 
	 * @param gComponenet
	 * 		
	 */
	@Override
	public void perform(GComponent gComponent) {
		if (gComponent == null) {
			return;
		}

		Widget widget = getWidget(gComponent);
		if (widget instanceof Text) {
			Text textWidget = (Text) widget;
			textWidget.setText(GUITAR_DEFAULT_TEXT);
		}
		
	}
}
