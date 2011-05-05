/*	
 *  Copyright (c) 2011-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SitarWindow;

/**
 * Wraps a {@link Menu}.
 * 
 * @author Gabe Gorelick
 * 
 */
public class SitarMenu extends SitarWidget {

	private final Menu menu;
	
	/**
	 * Wrap the given widget that lives in the given window.
	 * @param menu the widget to wrap
	 * @param window the window the widget lives in
	 */
	protected SitarMenu(Menu menu, SitarWindow window) {
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
		// can't just use SitarControl's version, even though it's exactly the
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

	/**
	 * {@inheritDoc}
	 * 
	 * @see Menu#getItems()
	 */
	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();
				
		menu.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				synchronized (children) {
					SitarWidgetFactory factory = SitarWidgetFactory.INSTANCE;
					for (MenuItem item : menu.getItems()) {
						children.add(factory.newSWTWidget(item, getWindow()));
					}
				}
			}
		});
		
		return children;
	}

}
