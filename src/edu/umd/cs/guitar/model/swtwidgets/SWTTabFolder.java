package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;

/**
 * 
 * Models a swt tab folder
 * 
 * @author Gabe Gorelick-Feldman
 *
 */
public class SWTTabFolder extends SWTComposite {

	private final TabFolder tabFolder;
	
	protected SWTTabFolder(TabFolder tabFolder, SWTWindow window) {
		super(tabFolder, window);
		this.tabFolder = tabFolder;
	}
	/**
	 * @return the children of the tab folder
	 */
	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();

		final SWTWidgetFactory factory = SWTWidgetFactory.newInstance();
		
		tabFolder.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				for (TabItem item : tabFolder.getItems()) {
					children.add(factory.newSWTWidget(item, getWindow()));
				}
			}
		});
		 				
		return children;
	}

}
