package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabItem;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;

public class SWTTabItem extends SWTItem {

	private final TabItem item;
	
	protected SWTTabItem(TabItem item, SWTWindow window) {
		super(item, window);
		this.item = item;
	}

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
