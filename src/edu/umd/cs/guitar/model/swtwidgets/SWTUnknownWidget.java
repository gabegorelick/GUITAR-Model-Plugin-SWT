package edu.umd.cs.guitar.model.swtwidgets;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;

/**
 * Represents {@link Widget Widgets} which are unknown to the model, i.e. the
 * model has no way to handle them.
 * 
 * Unknown widgets are treated being disabled and having no children.
 * 
 * @author Gabe Gorelick
 * 
 */
public class SWTUnknownWidget extends SWTWidget {

	protected SWTUnknownWidget(Widget widget, SWTWindow window) {
		super(widget, window);
	}

	/**
	 * <p>
	 * Returns whether this widget is enabled. As {@link Widget Widgets} by
	 * default have no notion of being enabled, this method is abstract.
	 * </p>
	 * 
	 * @return whether this widget is enabled
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}
	/**
	 * @return the children of this widget
	 */
	@Override
	public List<GComponent> getChildren() {
		return Collections.emptyList();
	}

}
