package edu.umd.cs.guitar.event;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTConstants;
import edu.umd.cs.guitar.model.swtwidgets.SWTWidget;

/**
 * The default action supported by most {@code SWTWidget}s.
 * {@code SWTDefaultAction} simulates a user interacting with a widget by
 * notifying all event listeners registered on the widget.
 * 
 * @author Gabe Gorelick
 * 
 * @see SWTWidget#getEventList()
 */
public class SWTDefaultAction extends SWTAction {
		
	/**
	 * Execute all events that the given widget is listening for.
	 * 
	 * @param gComponent
	 *            the component to perform this action on
	 *            
	 * @see Widget#isListening(int)
	 */
	public void perform(GComponent gComponent) {
		if (gComponent == null) {
			return;
		}

		final Widget widget = getWidget(gComponent);
		
		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				Event event = new Event();
				
				for (int eventType : SWTConstants.SWT_EVENT_LIST) {
					if (widget.isListening(eventType)) {
						event.type = eventType;
						widget.notifyListeners(eventType, event);
					}
				}	
			}
		});		
	}

}
