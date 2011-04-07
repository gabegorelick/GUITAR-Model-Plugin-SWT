package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;

public class SWTTabFolder extends SWTComposite {

	private final TabFolder tabFolder;
	
	protected SWTTabFolder(TabFolder tabFolder, GWindow window) {
		super(tabFolder, window);
		this.tabFolder = tabFolder;
	}
	
	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();

		final SWTWidgetFactory factory = SWTWidgetFactory.newInstance();
		
		tabFolder.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				for (TabItem item : tabFolder.getItems()) {
					// TODO add children?
					children.add(factory.newSWTWidget(item, getWindow()));
				}
			}
		});
		 				
		return children;
	}

}
