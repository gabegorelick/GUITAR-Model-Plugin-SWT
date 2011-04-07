package edu.umd.cs.guitar.model.swtwidgets;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;

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

	protected SWTUnknownWidget(Widget widget, GWindow window) {
		super(widget, window);
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public List<GComponent> getChildren() {
		return Collections.emptyList();
	}

}
