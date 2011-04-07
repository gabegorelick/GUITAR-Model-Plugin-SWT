package edu.umd.cs.guitar.model;

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

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.swtwidgets.SWTWidgetFactory;

/**
 * Helper class for finding widgets in the GUI hierarchy
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 */
@SuppressWarnings("deprecation")
// package-private, ooh la la
class SWTWidgetAdder {
		
	private static List<GComponent> handleWidget(final Widget widget, final GWindow window) {
		final List<GComponent> children = new ArrayList<GComponent>();
		
		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				SWTWidgetFactory factory = SWTWidgetFactory.newInstance();
				
				if (widget instanceof Menu) {
					for (MenuItem item : ((Menu) widget).getItems()) {
						children.add(factory.newSWTWidget(item, window));
					}
				} else if (widget instanceof MenuItem) {
					Menu menu = ((MenuItem) widget).getMenu();
					if (menu != null) {
						children.add(factory.newSWTWidget(menu, window));
					}
				} else if (widget instanceof TabFolder) {
					for (TabItem item : ((TabFolder) widget).getItems()) {
						children.add(factory.newSWTWidget(item, window));
					}
				} else if (widget instanceof TabItem) {
					Control control = ((TabItem) widget).getControl();
					if (control != null) {
						children.add(factory.newSWTWidget(control, window));
					}
				} else if (widget instanceof Tree) {
					Tree tree = (Tree) widget;
					for (TreeItem item : tree.getItems()) {
						children.add(factory.newSWTWidget(item, window));
					}
					for (TreeColumn col : tree.getColumns()) {
						children.add(factory.newSWTWidget(col, window));
					}
				} else if (widget instanceof TreeItem) {
					for (TreeItem i : ((TreeItem) widget).getItems()) {
						children.add(factory.newSWTWidget(i, window));
					}
				} else if (widget instanceof Table) {
					Table table = (Table) widget;
					for (TableColumn col : table.getColumns()) {
						children.add(factory.newSWTWidget(col, window));
					}
					for (TableItem i : table.getItems()) {
						children.add(factory.newSWTWidget(i, window));
					}
				} else if (widget instanceof TableTree) {
					for (TableTreeItem i : ((TableTree) widget).getItems()) {
						children.add(factory.newSWTWidget(i, window));
					}
				} else if (widget instanceof TableTreeItem) {
					for (TableTreeItem i : ((TableTreeItem) widget).getItems()) {
						children.add(factory.newSWTWidget(i, window));
					}
				} else if (widget instanceof ToolBar) {
					for (ToolItem i : ((ToolBar) widget).getItems()) {
						children.add(factory.newSWTWidget(i, window));
					}
				} else if (widget instanceof Tray) {
					for (TrayItem i : ((Tray) widget).getItems()) {
						children.add(factory.newSWTWidget(i, window));
					}
				}
			}
		});
		
		return children;
	}
}
