package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;

/**
 * Models an swt menu item
 * 
 */
public class SWTMenuItem extends SWTItem {

	private final MenuItem item;
	
	protected SWTMenuItem(MenuItem item, SWTWindow window) {
		super(item, window);
		this.item = item;
	}

	/**
	 * 
	 * @return the children of this menu item;
	 */
	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();
		final SWTWidgetFactory factory = SWTWidgetFactory.newInstance();
		
		item.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				Menu menu = item.getMenu();
				if (menu != null) {
					children.add(factory.newSWTWidget(menu, getWindow()));
				}
			}
		});
		
		return children;		
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
		final boolean[] enabled = new boolean[1];
		
		item.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				enabled[0] = item.isEnabled();
			}
		});
		
		return enabled[0];
	}

}
