package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;

public class SWTMenu extends SWTWidget {

	private final Menu menu;
	
	protected SWTMenu(Menu menu, SWTWindow window) {
		super(menu, window);
		this.menu = menu;
	}
	
	/**
	 * Returns whether this widget is enabled. This method is simply a wrapper
	 * around SWT's {@link Menu#isEnabled()}.
	 * 
	 * @return <code>true</code> if this widget is enabled, <code>false</code>
	 *         if not enabled
	 * @see Menu#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		// can't just use SWTControl's version, even though it's exactly the
		// same, since Menus aren't Controls
				
		final AtomicBoolean enabled = new AtomicBoolean();

		menu.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				enabled.set(menu.isEnabled());
			}
		});

		return enabled.get();
	}

	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();
				
		menu.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				synchronized (children) {
					SWTWidgetFactory factory = SWTWidgetFactory.INSTANCE;
					for (MenuItem item : menu.getItems()) {
						children.add(factory.newSWTWidget(item, getWindow()));
					}
				}
			}
		});
		
		return children;
	}

}
