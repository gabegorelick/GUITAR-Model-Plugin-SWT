package edu.umd.cs.guitar.model.swtwidgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.util.GUITARLog;

public class SWTWidgetFactory {

	private static final SWTWidgetFactory factory = new SWTWidgetFactory();
	
	private SWTWidgetFactory() {
		// this space left intentionally blank
	}
	
	public static SWTWidgetFactory newInstance() {
		return factory;
	}
	
	//---- Widgets ----
	
	public SWTWidget newSWTWidget(Widget widget, GWindow window) {
		// this is needed because Java handles overloaded methods at compile time
		if (widget instanceof Control) {
			return newSWTControl((Control) widget, window);
		} else if (widget instanceof Menu) {
			return newSWTMenu((Menu) widget, window);
		} else if (widget instanceof Item) {
			return newSWTItem((Item) widget, window);
		} else {
			GUITARLog.log.info("Unknown widget: " + widget.getClass());
			return new SWTUnknownWidget(widget, window);
		}
	}
		
	public SWTMenu newSWTMenu(Menu menu, GWindow window) {
		return new SWTMenu(menu, window);
	}
		
	public SWTControl newSWTControl(Control control, GWindow window) {
		if (control instanceof Composite) {
			return newSWTComposite((Composite) control, window);
		} else {
			return new SWTControl(control, window);
		}
	}
	
	//---- Composites ----
	
	public SWTComposite newSWTComposite(Composite composite, GWindow window) {
		if (composite instanceof Decorations) {
			return newSWTDecorations((Decorations) composite, window);
		} else if (composite instanceof TabFolder) {
			return newSWTTabFolder((TabFolder) composite, window);
		} else if (composite instanceof Tree) {
			return newSWTTree((Tree) composite, window);
		} else {
			return new SWTComposite(composite, window);
		}
	}
		
	public SWTDecorations newSWTDecorations(Decorations decorations, GWindow window) {
		return new SWTDecorations(decorations, window);
	}
		
	public SWTTabFolder newSWTTabFolder(TabFolder tabFolder, GWindow window) {
		return new SWTTabFolder(tabFolder, window);
	}
	
	public SWTTree newSWTTree(Tree tree, GWindow window) {
		return new SWTTree(tree, window);
	}
	
	//---- Items ----
	
	public SWTItem newSWTItem(Item item, GWindow window) {
		if (item instanceof MenuItem) {
			return newSWTMenuItem((MenuItem) item, window);
		} else if (item instanceof TabItem) {
			return newSWTTabItem((TabItem) item, window);
		} else if (item instanceof TreeItem) {
			return newSWTTreeItem((TreeItem) item, window);
		} else {
			GUITARLog.log.info("Unknown Item widget: " + item.getClass());
			return new SWTUnknownItem(item, window);
		}
	}
	
	public SWTMenuItem newSWTMenuItem(MenuItem item, GWindow window) {
		return new SWTMenuItem(item, window);
	}
	
	public SWTTabItem newSWTTabItem(TabItem item, GWindow window) {
		return new SWTTabItem(item, window);
	}
	
	public SWTTreeItem newSWTTreeItem(TreeItem item, GWindow window) {
		return new SWTTreeItem(item, window);
	}
	
}
