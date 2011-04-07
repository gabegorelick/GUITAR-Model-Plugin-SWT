package edu.umd.cs.guitar.model.swtwidgets;

import org.eclipse.swt.widgets.Item;

import edu.umd.cs.guitar.model.GWindow;

public abstract class SWTItem extends SWTWidget {

	protected SWTItem(Item item, GWindow window) {
		super(item, window);
	}

	/**
	 * Returns whether this widget is enabled or not. By default, {@link Item
	 * Items} have no notion of being enabled. Thus, this method simply returns
	 * <code>false</code>. Subclasses, such as {@link SWTMenuItem}, may support
	 * being enabled, and are encouraged to override this method.
	 * 
	 * @return <code>false</code>
	 */
	@Override
	public boolean isEnabled() {
		return false;
	}
}
