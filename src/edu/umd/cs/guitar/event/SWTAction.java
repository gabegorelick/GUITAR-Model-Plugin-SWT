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
		if (!(gComponent instanceof SWTWidget)) {
			return false; // TODO refactor into abstract class
		} else if (new SWTEditableTextAction().isSupportedBy(gComponent)) {
			return false;
		}
		
		final Widget widget = getWidget(gComponent);
		final boolean[] isListening = { false };
		
		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				for (int eventType : SWTConstants.SWT_EVENT_LIST) {
					if (widget.isListening(eventType)) {
						isListening[0] = true;
					}
				}
			}
		});
				
		return isListening[0];
	}

	/**
	 * Execute all events on the specified component.
	 * 
	 * @param gComponent
	 *            the component to perform the event on
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

	/**
	 * Execute all events on the specified component. This method behaves
	 * exactly the same as {@link #perform(GComponent)}.
	 * 
	 * @deprecated Use {@link #perform(GComponent)} instead. There is no need to
	 *             pass optional data to <code>SWTActions</code>. If such a need
	 *             arises, it makes more sense to make that data a member of the
	 *             class, and not just pass it to this method.
	 * 
	 * @param gComponent
	 *            the component to perform the event on
	 * @param optionalData
	 *            this parameter is not used
	 */
	@Deprecated
	@Override
	public void perform(GComponent gComponent,
			Hashtable<String, List<String>> optionalData) {
		
		perform(gComponent);
	}

	/**
	 * Execute all events on the specified component. This method behaves
	 * exactly the same as {@link #perform(GComponent)}.
	 * 
	 * @deprecated Use {@link #perform(GComponent)} instead. There is no need to
	 *             pass optional data to <code>SWTActions</code>. If such a need
	 *             arises, it makes more sense to make that data a member of the
	 *             class, and not just pass it to this method.
	 * 
	 * @param gComponent
	 *            the component to perform the event on
	 * @param parameters
	 *            this parameter is not used
	 * @param optionalData
	 *            this parameter is not used
	 */
	@Deprecated
	@Override
	public void perform(GComponent gComponent, Object parameters,
			Hashtable<String, List<String>> optionalData) {

		perform(gComponent, optionalData);
	}

}
