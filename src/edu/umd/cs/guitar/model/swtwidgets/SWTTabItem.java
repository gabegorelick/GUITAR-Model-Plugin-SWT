package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;

/**
 * Models a swt tab item
 * 
 * 
 *
 */
public class SWTTabItem extends SWTItem {

	private final TabItem item;
	
	protected SWTTabItem(TabItem item, SWTWindow window) {
		super(item, window);
		this.item = item;
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
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();
		final SWTWidgetFactory factory = SWTWidgetFactory.newInstance();
		
		item.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				Control control = item.getControl();
				if (control != null) {
					children.add(factory.newSWTWidget(control, getWindow()));
				}
			}
		});
		
		return children;
	}

}
