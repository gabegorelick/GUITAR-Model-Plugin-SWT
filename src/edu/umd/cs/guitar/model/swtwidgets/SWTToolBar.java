package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;

/**
 * Models a swt tool bar
 *
 */
public class SWTToolBar extends SWTComposite {

	private final ToolBar toolbar;
	
	protected SWTToolBar(ToolBar toolbar, SWTWindow window) {
		super(toolbar, window);
		this.toolbar = toolbar;
	}
	/**
	 * @return whether the children of this widget
	 */
	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();
		final SWTWidgetFactory factory = SWTWidgetFactory.newInstance();
		
		toolbar.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				for (ToolItem i : toolbar.getItems()) {
					children.add(factory.newSWTWidget(i, getWindow()));
				}
			}
		});
		 				
		return children;
	}

}
