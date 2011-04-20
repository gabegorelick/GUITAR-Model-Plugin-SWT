package edu.umd.cs.guitar.model.swtwidgets;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.SWTWindow;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * <p>
 * <code>SWTWidgetFactory</code> handles the wrapping of SWT widgets into their
 * appropriate SWTGuitar type. TODO finish documenting
 * 
 * @author Gabe Gorelick
 * 
 */
public enum SWTWidgetFactory {

	INSTANCE;
		
	private final Map<Class<? extends Widget>, Class<? extends SWTWidget>> widgetAdapters;
	
	private SWTWidgetFactory() {
		widgetAdapters = Collections.synchronizedMap(new HashMap<Class<? extends Widget>, Class<? extends SWTWidget>>());
		registerDefaultAdapters();
	}

	/**
	 * Register all the default adapters provided with SWT GUITAR. These
	 * adapters are located in the {@code edu.umd.cs.guitar.model.swtwidgets}
	 * package.
	 */
	private void registerDefaultAdapters() {
		// Widgets
		registerWidgetAdapter(Widget.class, SWTUnknownWidget.class);
		registerWidgetAdapter(Menu.class, SWTMenu.class);
		
		// Controls
		registerWidgetAdapter(Control.class, SWTControl.class);
		registerWidgetAdapter(Text.class, SWTText.class);
		
		// Composites
		registerWidgetAdapter(Composite.class, SWTComposite.class);
		registerWidgetAdapter(Decorations.class, SWTDecorations.class);
		registerWidgetAdapter(TabFolder.class, SWTTabFolder.class);
		registerWidgetAdapter(Tree.class, SWTTree.class);
		registerWidgetAdapter(Table.class, SWTTable.class);
		registerWidgetAdapter(ToolBar.class, SWTToolBar.class);
		
		// Items
		registerWidgetAdapter(Item.class, SWTUnknownItem.class);
		registerWidgetAdapter(MenuItem.class, SWTMenuItem.class);
		registerWidgetAdapter(TabItem.class, SWTTabItem.class);
		registerWidgetAdapter(TreeItem.class, SWTTreeItem.class);
	}
	
	/**
	 * Register a widget adapter. 
	 * @param widgetType
	 * @param adapterType
	 * 
	 * @see Map#put(Object, Object)
	 */
	public void registerWidgetAdapter(Class<? extends Widget> widgetType, Class<? extends SWTWidget> adapterType) {
		widgetAdapters.put(widgetType, adapterType);
	}
	
	private Entry<Class<? extends Widget>, Class<? extends SWTWidget>> getWidgetAdapterEntry(Widget widget) {
		// I hate the verbosity of generics
		List<Entry<Class<? extends Widget>, Class<? extends SWTWidget>>> validTypes = 
			new ArrayList<Entry<Class<? extends Widget>, Class<? extends SWTWidget>>>();
		
		// get all possible adapters for this widget
		for (Entry<Class<? extends Widget>, Class<? extends SWTWidget>> e : widgetAdapters.entrySet()) {
			if (e.getKey().isInstance(widget)) {
				validTypes.add(e);
			}
		}
		
		Collections.sort(validTypes, new Comparator<Entry<Class<? extends Widget>, Class<? extends SWTWidget>>>() {
			@Override
			public int compare(
					Entry<Class<? extends Widget>, Class<? extends SWTWidget>> e1,
					Entry<Class<? extends Widget>, Class<? extends SWTWidget>> e2) {
				
				Class<? extends Widget> c1 = e1.getKey();
				Class<? extends Widget> c2 = e2.getKey();
				
				if (c1.equals(c2)) {
					return 0;
				} else if (c1.isAssignableFrom(c2)) {
					// c1 extends c2
					return 1;
				} else {
					// c2 extends c1
					return -1;
				}
			}
		});
		
		// return lowest class in type hierarchy
		return validTypes.get(0);
	}
	
	public Class<? extends SWTWidget> getWidgetAdapter(Widget widget) {
		return getWidgetAdapterEntry(widget).getValue();
	}
	
	public SWTWidget newSWTWidget(Widget widget, SWTWindow window) {
		Entry<Class<? extends Widget>, Class<? extends SWTWidget>> entry = getWidgetAdapterEntry(widget);
		Class<? extends Widget> widgetType = entry.getKey();
		Class<? extends SWTWidget> adapterType = entry.getValue();
		
		GUITARLog.log.debug("Found adapter type " + adapterType + " for widget " + widget);
		
		Constructor<? extends SWTWidget> constructor;
		try {	
			constructor = adapterType.getDeclaredConstructor(widgetType, SWTWindow.class);
		} catch (SecurityException e) {
			throw new IllegalArgumentException(e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(adapterType + " does not have necessary constructor", e);
		}
		
		try {
			return constructor.newInstance(widget, window);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(e);
		}
	}
		
}
