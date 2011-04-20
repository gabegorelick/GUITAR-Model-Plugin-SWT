package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
				
		item.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				synchronized (children) {
					SWTWidgetFactory factory = SWTWidgetFactory.INSTANCE;
					Menu menu = item.getMenu();
					if (menu != null) {
						children.add(factory.newSWTWidget(menu, getWindow()));
					}
				}
			}
		});
		
		return children;		
	}
	
	@Override
	public boolean isEnabled() {
		final AtomicBoolean enabled = new AtomicBoolean();
		
		item.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				enabled.set(item.isEnabled());
			}
		});
		
		return enabled.get();
	}

}
