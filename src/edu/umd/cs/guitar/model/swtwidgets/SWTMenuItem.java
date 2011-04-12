package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;

public class SWTMenuItem extends SWTItem {

	private final MenuItem item;
	
	protected SWTMenuItem(MenuItem item, SWTWindow window) {
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
				Menu menu = item.getMenu();
				if (menu != null) {
					children.add(factory.newSWTWidget(menu, getWindow()));
				}
			}
		});
		
		return children;		
	}
	
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
