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

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SitarWindow;

/**
 * Wraps a {@link Tree}.
 * 
 * @author Gabe Gorelick
 * 
 */
public class SitarTree extends SitarComposite {

	private final Tree tree;

	/**
	 * Wrap the given widget that lives in the given window.
	 * @param tree the widget to wrap
	 * @param window the window the widget lives in
	 */
	protected SitarTree(Tree tree, SitarWindow window) {
		super(tree, window);
		this.tree = tree;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see Tree#getItems()
	 */
	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();
		
		tree.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				synchronized (children) {
					SitarWidgetFactory factory = SitarWidgetFactory.INSTANCE;
					for (TreeItem item : tree.getItems()) {
						children.add(factory.newSWTWidget(item, getWindow()));
					}
				}
			}
		});

		return children;
	}

}
