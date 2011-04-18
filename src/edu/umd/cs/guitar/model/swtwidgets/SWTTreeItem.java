package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;
/**
 * Models a swt tree item
 *
 */
public class SWTTreeItem extends SWTItem {

	private final TreeItem item;

	protected SWTTreeItem(TreeItem item, SWTWindow window) {
		super(item, window);
		this.item = item;
	}
	/**
	 * @return whether the children of this widget
	 */
	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();
		final SWTWidgetFactory factory = SWTWidgetFactory.newInstance();

		item.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				for (Item i : item.getItems()) {
					children.add(factory.newSWTWidget(i, getWindow()));
				}
			}
		});

		return children;
	}

}
