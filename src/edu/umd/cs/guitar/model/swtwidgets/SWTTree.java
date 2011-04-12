package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;

public class SWTTree extends SWTComposite {

	private final Tree tree;

	protected SWTTree(Tree tree, SWTWindow window) {
		super(tree, window);
		this.tree = tree;
	}

	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();
		final SWTWidgetFactory factory = SWTWidgetFactory.newInstance();

		tree.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				for (TreeItem item : tree.getItems()) {
					children.add(factory.newSWTWidget(item, getWindow()));
				}
			}
		});

		return children;
	}

}
