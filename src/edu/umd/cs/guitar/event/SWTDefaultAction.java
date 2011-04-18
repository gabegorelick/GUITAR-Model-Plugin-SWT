package edu.umd.cs.guitar.event;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTConstants;

public class SWTDefaultAction extends SWTAction {
		
	/**
	 * Execute all events on the specified component.
	 * 
	 * @param gComponent
	 *            the component to perform this action on
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
