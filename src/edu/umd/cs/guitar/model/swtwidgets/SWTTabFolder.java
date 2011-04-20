package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;

public class SWTTabFolder extends SWTComposite {

	private final TabFolder tabFolder;
	
	protected SWTTabFolder(TabFolder tabFolder, SWTWindow window) {
		super(tabFolder, window);
		this.tabFolder = tabFolder;
	}
	
	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();
		
		tabFolder.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				synchronized (children) {
					SWTWidgetFactory factory = SWTWidgetFactory.INSTANCE;
					for (TabItem item : tabFolder.getItems()) {
						children.add(factory.newSWTWidget(item, getWindow()));
					}
				}
			}
		});
		 				
		return children;
	}

}
