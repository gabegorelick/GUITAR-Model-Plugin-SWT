/*	
 *  Copyright (c) 2011-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
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
 * {@code SWTWidgetFactory} handles the adapting of {@link Widget Widgets} onto
 * GUITAR types, specifically {@link SWTWidget SWTWidgets}. Clients can register
 * widget adapters that wrap {@link Widget Widgets} in
 * {@code SWTWidget SWTWidgets}). {@link SWTWidgetFactory} automatically
 * registers the default widget adapters located in this package. These adapters
 * can be overriden with the {@link #registerWidgetAdapter(Class, Class)
 * registerWidgetAdapter} method. For example, the following code will override
 * the default adapter ({@link SWTTree}) for SWT's {@link Tree} type:
 * 
 * <pre>
 * SWTWidgetFactory.INSTANCE.registerWidgetAdapter(Tree.class, FOO.class);
 * </pre>
 * 
 * </p>
 * <p>
 * There are some limitations to widget adapters: they must be a subtype of
 * {@code SWTWidget}, and they must have an accessible
 * {@link Class#getDeclaredConstructor(Class...) declared constructor} that
 * accepts two parameters. The first parameter must be the type of the
 * {@code Widget} that it is registered to wrap, and the second parameter must
 * be of type {@link SWTWindow}. For example, if we want a new adapter named
 * {@code SWTShell} to wrap an SWT {@code Shell} it would need to have a
 * constructor with the following signature:
 * 
 * <pre>
 * public SWTShell(Shell, SWTWindow)
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * When selecting an appropriate {@code SWTWidget} subtype for a given
 * {@code Widget}, {@code SWTWidgetFactory} attempts to find the most relevant
 * registered adapter. For example, if we have both a {@link Composite} adapter
 * and a {@link Control} adapter registered, a call to
 * {@link #getWidgetAdapter(Widget) getWidgetAdapter(Shell)} will return the
 * {@code Composite} adapter, since that is the closer type to {@code Shell}
 * (since {@code Shell} is a descendant of {@code Composite} and
 * {@code Composite} is a descendant of {@code Control}).
 * </p>
 * <p>
 * An instance of this class can be obtained through its singleton
 * {@link #INSTANCE} member. See Joshua Bloch's <i>Effective Java</i>, the
 * relevant portions of which are reproduced <a
 * href="http://drdobbs.com/java/208403883?pgno=3">here</a>, for why this
 * singleton is implemented as an enum.
 * </p>
 * <p>
 * All methods of this class are thread safe.
 * </p>
 * 
 * @author Gabe Gorelick
 * 
 */
public enum SWTWidgetFactory {

	/**
	 * The singleton instance of this class.
	 */
	INSTANCE;
		
	private final Map<Class<? extends Widget>, Class<? extends SWTWidget>> widgetAdapters;
	
	private SWTWidgetFactory() {
		// thread safety VERY important since there's only one instance of this
		// class that's shared among all threads
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
	 * <p>
	 * Register a widget adapter. The adapter must have an accessible declared
	 * constructor that accepts two parameters. The first parameter must be of
	 * type {@code widgetType}, and the second parameter must be of type
	 * {@link SWTWindow}. See the class documentation for more information.
	 * </p>
	 * <p>
	 * This method, like all methods of this class, is thread safe, since
	 * registered adapters are stored in a
	 * {@link Collections#synchronizedMap(Map) synchronized Map}.
	 * </p>
	 * 
	 * @param widgetType
	 *            type of widget this adapter will wrap
	 * @param adapterType
	 *            type of adapter
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
		
		// sort by type hierarchy
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

	/**
	 * Get the most appropriate widget adapter for the given widget. This is the
	 * adapter that is registered to the closest type that the given widget is
	 * assignable to. See the class documentation for more information.
	 * 
	 * @param widget
	 *            widget to wrap
	 * @return the most appropriate widget adapter type
	 */
	public Class<? extends SWTWidget> getWidgetAdapter(Widget widget) {
		return getWidgetAdapterEntry(widget).getValue();
	}

	/**
	 * Create a new {@code SWTWidget} for a given widget using the registered
	 * widget adapters. The actual type of the returned object will be the type
	 * returned by {@link #getWidgetAdapter(Widget) getWidgetAdapter(widget)}.
	 * 
	 * @param widget
	 *            widget to wrap
	 * @param window
	 *            window to pass to the adapter's constructor
	 * @return a widget adapter
	 */
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
