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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;

public class SWTControl extends SWTWidget {

	private final Control control;
	
	protected SWTControl(Control widget, SWTWindow window) {
		super(widget, window);
		this.control = widget;
	}
	
	@Override
	public Control getWidget() {
		return control;
	}

	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();

		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				// use synchronized to flush writes writes to main memory
				synchronized (children) {
					SWTWidgetFactory factory = SWTWidgetFactory.INSTANCE;
					
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
	
	@Override
	public SWTComposite getParent() {
		SWTWidgetFactory factory = SWTWidgetFactory.INSTANCE;
		
		final Composite[] parent = new Composite[1];
		
		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				parent[0] = control.getParent();
			}
		});
		
		return (SWTComposite) factory.newSWTWidget(parent[0], getWindow());
	}
	
	@Override
	public boolean isEnabled() {
		final boolean[] isEnabled = new boolean[1];
		
		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				isEnabled[0] = control.isEnabled();
			}
		});
		
		return isEnabled[0];
	}
}
