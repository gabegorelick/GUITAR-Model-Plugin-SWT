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

import java.util.List;

import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SitarWindow;

/**
 * Wraps a {@link Decorations}. {@code Decorations} is the parent class of
 * {@link Shell}.
 * 
 * @author Gabe Gorelick
 * 
 */
public class SitarDecorations extends SitarComposite {

	private final Decorations decorations;
	
	/**
	 * Wrap the given widget that lives in the given window.
	 * @param widget the widget to wrap
	 * @param window the window the widget lives in
	 */
	protected SitarDecorations(Decorations widget, SitarWindow window) {
		super(widget, window);
		this.decorations = widget;
	}

	/**
	 * Get the children of this widget. In addition to what is supported by
	 * {@code Composite}, {@code Decorations} also support having menu bars as
	 * children.
	 * 
	 * @return a list of children
	 * @see Decorations#getMenuBar()
	 */
	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = super.getChildren();
		
		decorations.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				synchronized (children) {
					SitarWidgetFactory factory = SitarWidgetFactory.INSTANCE;

					// MenuBar is special case, since not child of parent
					Menu menuBar = decorations.getMenuBar();
					if (menuBar != null) {
						children.add(factory.newSWTWidget(menuBar, getWindow()));
					}
				}
			}
		});
						
		return children;
	}

	/**
	 * Return whether this widget is terminal or not. Decorations are considered
	 * terminal by default.
	 * 
	 * @return {@code true}
	 */
	@Override
	public boolean isTerminal() {
		return true;
	}

}
