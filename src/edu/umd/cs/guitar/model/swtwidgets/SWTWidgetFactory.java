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

import edu.umd.cs.guitar.model.SWTWindow;
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
	
	/**
	 * @return a new swt widget with same widget and window
	 */
	public SWTWidget newSWTWidget(Widget widget, SWTWindow window) {
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
	/**
	 * @return a new swt menu with same menu and window
	 */	
	public SWTMenu newSWTMenu(Menu menu, SWTWindow window) {
		return new SWTMenu(menu, window);
	}
	/**
	 * @return a new swt control widget with same control and window
	 */	
	public SWTControl newSWTControl(Control control, SWTWindow window) {
		if (control instanceof Composite) {
			return newSWTComposite((Composite) control, window);
		} else {
			return new SWTControl(control, window);
		}
	}
	
	//---- Composites ----
	/**
	 * @return a new swt composite widget with same composites and window
	 */
	public SWTComposite newSWTComposite(Composite composite, SWTWindow window) {
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
	/**
	 * @return a new swt decoration with same decorations and window
	 */	
	public SWTDecorations newSWTDecorations(Decorations decorations, SWTWindow window) {
		return new SWTDecorations(decorations, window);
	}
	/**
	 * @return a new swt table with same table  and window
	 */
	public SWTTable newSWTTable(Table table, SWTWindow window) {
		return new SWTTable(table, window);
	}
	/**
	 * @return a new swt tab folder with tabfolder  and window
	 */	
	public SWTTabFolder newSWTTabFolder(TabFolder tabFolder, SWTWindow window) {
		return new SWTTabFolder(tabFolder, window);
	}
	/**
	 * @return a new swt toolbar with toolbar  and window
	 */
	public SWTToolBar newSWTToolBar(ToolBar toolbar, SWTWindow window) {
		return new SWTToolBar(toolbar, window);
	}
	/**
	 * @return a new swt tree with same tree and window
	 */
	public SWTTree newSWTTree(Tree tree, SWTWindow window) {
		return new SWTTree(tree, window);
	}
	
	//---- Items ----
	/**
	 * @return a new swt item with same item and window
	 */
	public SWTItem newSWTItem(Item item, SWTWindow window) {
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
	/**
	 * @return a new swt item with same item and window
	 */
	public SWTMenuItem newSWTMenuItem(MenuItem item, SWTWindow window) {
		return new SWTMenuItem(item, window);
	}
	/**
	 * @return a new swt item with same item and window
	 */
	public SWTTabItem newSWTTabItem(TabItem item, SWTWindow window) {
		return new SWTTabItem(item, window);
	}
	/**
	 * @return a new swt item with same item and window
	 */
	public SWTTreeItem newSWTTreeItem(TreeItem item, SWTWindow window) {
		return new SWTTreeItem(item, window);
	}
	
}
