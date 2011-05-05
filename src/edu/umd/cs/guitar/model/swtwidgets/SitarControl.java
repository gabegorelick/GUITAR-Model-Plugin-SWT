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
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SitarWindow;

/**
 * Wraps a {@link Control}.
 * 
 * @author Gabe Gorelick
 * 
 */
public class SitarControl extends SitarWidget {

	private final Control control;
	
	/**
	 * Wrap the given widget that lives in the given window.
	 * @param widget the widget to wrap
	 * @param window the window the widget lives in
	 */
	protected SitarControl(Control widget, SitarWindow window) {
		super(widget, window);
		this.control = widget;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Control getWidget() {
		return control;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The only children of {@code Control}s is any menus they have.
	 * 
	 * @see Control#getMenu()
	 */
	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();

		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				// use synchronized to flush writes writes to main memory
				synchronized (children) {
					SitarWidgetFactory factory = SitarWidgetFactory.INSTANCE;
					
					// Menu is special case, since not child of parent
					Menu menu = control.getMenu();
					if (menu != null) {
						children.add(factory.newSWTWidget(menu, getWindow()));
					}
				}
			}
		});
		
		return children;
	}

	/**
	 * Get the parent of this widget.
	 * 
	 * @return the parent of this widget
	 * @see Control#getParent()
	 */
	@Override
	public SitarComposite getParent() {
		SitarWidgetFactory factory = SitarWidgetFactory.INSTANCE;
		
		final AtomicReference<Composite> parent = new AtomicReference<Composite>();
		
		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				parent.set(control.getParent());
			}
		});
		
		return (SitarComposite) factory.newSWTWidget(parent.get(), getWindow());
	}
	
	/**
	 * Returns whether this control is enabled or not.
	 * 
	 * @return {@code true} if control is enabled
	 * @see Control#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		final AtomicBoolean enabled = new AtomicBoolean();
		
		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				enabled.set(control.isEnabled());
			}
		});
		
		return enabled.get();
	}
}
