package edu.umd.cs.guitar.model.swtwidgets;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;
/**
 * Models a swt unknown item
 *
 */
public class SWTUnknownItem extends SWTItem {

	protected SWTUnknownItem(Item item, SWTWindow window) {
		super(item, window);
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
		return false;
	}

	/**
	 *
	 *@return the children of this unknown item
	 */
	@Override
	public List<GComponent> getChildren() {
		return Collections.emptyList();
	}
}
