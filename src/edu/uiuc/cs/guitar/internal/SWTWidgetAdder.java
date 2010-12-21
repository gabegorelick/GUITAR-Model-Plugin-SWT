package edu.uiuc.cs.guitar.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import edu.uiuc.cs.guitar.model.SWTWidget;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;

/**
 * Helper class for finding widgets in the GUI hierarchy
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 */
public class SWTWidgetAdder {
	
	public static List<GComponent> handleWidget(Widget widget, GWindow window) {
		List<GComponent> children = new ArrayList<GComponent>();
		// handle menus as a special case
		if (widget instanceof Menu) {
			for (MenuItem item : ((Menu) widget).getItems()) {
				children.add(new SWTWidget(item, window));
			}
		}
		// handle menu items as a special case
		else if (widget instanceof MenuItem) {
			Menu menu = ((MenuItem) widget).getMenu();
			if (menu != null) {
				children.add(new SWTWidget(menu, window));
			}
		}
		else if (widget instanceof TabFolder) {
			for (TabItem item : ((TabFolder) widget).getItems()) {
				children.add(new SWTWidget(item, window));
			}
		}
		else if (widget instanceof TabItem) {
			Control control = ((TabItem) widget).getControl();
			if (control != null) {
				children.add(new SWTWidget(control, window));
			}
		}
		else if (widget instanceof Tree) {
			Tree tree = (Tree) widget;
			for (TreeItem item : tree.getItems()) {
				children.add(new SWTWidget(item, window));
			}
			for (TreeColumn col : tree.getColumns()) {
				children.add(new SWTWidget(col, window));
			}
		}
		else if (widget instanceof TreeItem) {
			for (TreeItem i : ((TreeItem) widget).getItems()) {
				children.add(new SWTWidget(i, window));
			}
		}
		else if (widget instanceof Table) {
			Table table = (Table) widget;
			for (TableColumn col : table.getColumns()) {
				children.add(new SWTWidget(col, window));
			}
			for (TableItem i : table.getItems()) {
				children.add(new SWTWidget(i, window));
			}
		}
		else if (widget instanceof TableTree) {
			for (TableTreeItem i : ((TableTree) widget).getItems()) {
				children.add(new SWTWidget(i, window));
			}
		}
		else if (widget instanceof TableTreeItem) {
			for (TableTreeItem i : ((TableTreeItem) widget).getItems()) {
				children.add(new SWTWidget(i, window));
			}
		}
		else if (widget instanceof ToolBar) {
			for (ToolItem i : ((ToolBar) widget).getItems()) {
				children.add(new SWTWidget(i, window));
			}
		}
		else if (widget instanceof Tray) {
			for (TrayItem i : ((Tray) widget).getItems()) {
				children.add(new SWTWidget(i, window));
			}
		}
		return children;
	}
}
