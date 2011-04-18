package edu.umd.cs.guitar.event;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.swtwidgets.SWTWidget;

/**
 * Superclass of all SWT events in GUITAR.
 * 
 */
public abstract class SWTAction implements GEvent {

	protected SWTAction() {
		// this space left intentionally blank
	}

	/**
	 * Get a <code>Widget</code> from a <code>GComponent</code>.
	 * 
	 * @param gComponent
	 * @return <code>Widget</code> contained in specified
	 *         <code>GComponent</code>
	 * @throws ClassCastException
	 *             thrown if argument is not an {@link SWTWidget}.
	 */
	protected Widget getWidget(GComponent gComponent) {
		SWTWidget widget = (SWTWidget) gComponent;
		return widget.getWidget();
	}

	/**
	 * Check if this action is supported by a given component.
	 * 
	 * @deprecated Use {@link SWTWidget#getEventList()} instead.
	 * 
	 * @throws ClassCastException
	 *             thrown if argument is not an {@link SWTWidget}
	 */
	@Deprecated
	@Override
	public final boolean isSupportedBy(GComponent gComponent) {
		SWTWidget widget = (SWTWidget) gComponent;
		return widget.getEventList().contains(this);
	}

	/**
	 * Perform this action on a given component.
	 * 
	 * @param gComponent
	 *            the component to perform this action on
	 */
	public abstract void perform(GComponent gComponent);

	/**
	 * This method behaves exactly the same as {@link #perform(GComponent)}.
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
	public final void perform(GComponent gComponent,
			Hashtable<String, List<String>> optionalData) {
		
		perform(gComponent);
	}

	/**
	 * This method behaves exactly the same as {@link #perform(GComponent)}.
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
	public final void perform(GComponent gComponent, Object parameters,
			Hashtable<String, List<String>> optionalData) {

		perform(gComponent, optionalData);
	}

}
