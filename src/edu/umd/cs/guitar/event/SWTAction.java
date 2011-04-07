package edu.umd.cs.guitar.event;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTConstants;
import edu.umd.cs.guitar.model.swtwidgets.SWTWidget;

/**
 * Superclass of all SWT events in GUITAR.
 * 
 */
public class SWTAction implements GEvent {

	public SWTAction() {
		// this space left intentionally blank
	}

	/**
	 * Get a <code>Widget</code> from a <code>GComponent</code>.
	 * 
	 * @param gComponent
	 * @return <code>Widget</code> contained in specified
	 *         <code>GComponent</code>
	 * @throws ClassCastException
	 *             if argument is not an {@link SWTWidget}.
	 */
	protected Widget getWidget(GComponent gComponent) {
		SWTWidget widget = (SWTWidget) gComponent;
		return widget.getWidget();
	}
	
	@Override
	public boolean isSupportedBy(GComponent gComponent) {
		return false; // TODO turn on
		// TODO use isListening?
//		if (!(gComponent instanceof SWTWidget)) {
//			return false; // TODO refactor into abstract class
//		} else if (new SWTEditableTextHandler().isSupportedBy(gComponent)) {
//			return false;
//		}
//		
//		return true;
	}

	/**
	 * Execute all events on the specified component. 
	 * 
	 * @param gComponent the component to perform the event on
	 * @param optionalData this parameter is not used
	 */
	@Override
	public void perform(GComponent gComponent, Hashtable<String, List<String>> optionalData) {
		if (gComponent == null) {
			return;
		}

		final Widget component = getWidget(gComponent);
		component.getDisplay().syncExec(new Runnable() {
			public void run() {
				Event event = new Event();
				for (int eventType : SWTConstants.swtEventList) {
					if (component.isListening(eventType)) {
						event.type = eventType;
						component.notifyListeners(eventType, event);
					}
				}	
			}
		});		
	}

	/**
	 * Execute all events on the specified component. This method behaves
	 * exactly the same as {@link #perform(GComponent, Hashtable)}.
	 */
	@Override
	public void perform(GComponent gComponent, Object parameters,
			Hashtable<String, List<String>> optionalData) {

		perform(gComponent,optionalData);
	}

}
