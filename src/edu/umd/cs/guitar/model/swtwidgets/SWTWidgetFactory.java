package edu.umd.cs.guitar.model.swtwidgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * <p>
 * <code>SWTWidgetFactory</code> handles the wrapping of SWT widgets into their
 * appropriate SWTGuitar type. Its methods are the only way for client code to
 * obtain instances of {@link SWTWidget SWTWidgets}.
 * </p>
 * <p>
 * This class's methods automatically return the most relevant type. For
 * example, passing a {@link Menu} object to the {@link #newSWTWidget} method
 * will cause it to return an {@link SWTMenu} (by way of {@link #newSWTMenu}).
 * This is done according to runtime type, so even if the <code>Menu</code> is
 * cast to another type, an <code>SWTMenu</code> will still be returned. Thus,
 * clients can get by with just the <code>newSWTWidget</code> method. The other
 * methods are merely provided for convenience and type safety.
 * </p>
 * <p>
 * Instances of this class can be obtained through the static
 * {@link #newInstance()} method, as there is no public constructor.
 * </p>
 * 
 * @author Gabe Gorelick
 * 
 */
public class SWTWidgetFactory {

	private static final SWTWidgetFactory factory = new SWTWidgetFactory();
	
	private SWTWidgetFactory() {
		// this space left intentionally blank
	}
	
	/**
	 * Get an instance of <code>SWTWidgetFactory</code>
	 * @return
	 */
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
		} else if (composite instanceof Table) { 
			return newSWTTable((Table) composite, window);			
		} else if (composite instanceof ToolBar) {
			return newSWTToolBar((ToolBar) composite, window);
		} else {
			return new SWTComposite(composite, window);
		}
	}
		
	public SWTDecorations newSWTDecorations(Decorations decorations, GWindow window) {
		return new SWTDecorations(decorations, window);
	}
	
	public SWTTable newSWTTable(Table table, GWindow window) {
		return new SWTTable(table, window);
	}
		
	public SWTTabFolder newSWTTabFolder(TabFolder tabFolder, GWindow window) {
		return new SWTTabFolder(tabFolder, window);
	}
	
	public SWTToolBar newSWTToolBar(ToolBar toolbar, GWindow window) {
		return new SWTToolBar(toolbar, window);
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
