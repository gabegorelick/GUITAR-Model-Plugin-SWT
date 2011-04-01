package edu.umd.cs.guitar.event;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTConstants;
import edu.umd.cs.guitar.model.SWTWidget;

/**
 * SWT analog of <code>JFCActionEDT</code>.
 * 
 */
public class SWTActionEDT implements GEvent {
	
	public SWTActionEDT() {
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
	public boolean isSupportedBy(GComponent arg0) {
		// TODO Auto-generated method stub
		return false;
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
				for (int i = 0; i < SWTConstants.swtEventList.length; i++) {
					event.type = SWTConstants.swtEventList[i];
					component.notifyListeners(SWTConstants.swtEventList[i], event);
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
