/* Copyright (c) 2010
 * Matt Kirn (mattkse@gmail.com) and Alex Loeb (atloeb@gmail.com)
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.uiuc.cs.guitar.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;

import edu.uiuc.cs.guitar.internal.SWTGlobals;
import edu.uiuc.cs.guitar.internal.SWTWidgetAdder;
import edu.umd.cs.guitar.event.EventManager;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.data.PropertyType;

/**
 * Models a SWT Widget
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 * 
 */
public class SWTWidget extends GComponent {

	Widget widget;

	public SWTWidget(Widget widget, GWindow window) {
		super(window);
		this.widget = widget;
	}

	/**
	 * @return the component
	 */
	public Widget getWidget() {
		return widget;
	}

	@Override
	public String getTitle() {
		String name = "";
		if (widget == null)
			return "";
		if (widget instanceof Item) {
			Item item = (Item) widget;
			name = item.getText();
		}
		return name;
	}

	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}

	@Override
	public List<PropertyType> getGUIProperties() {
		List<PropertyType> retList = new ArrayList<PropertyType>();
		PropertyType p;
		List<String> lPropertyValue;
		String sValue;
		// Title
		sValue = null;
		sValue = getTitle();
		if (sValue != null) {
			p = factory.createPropertyType();
			p.setName(SWTConstants.TITLE_TAG);
			lPropertyValue = new ArrayList<String>();
			lPropertyValue.add(sValue);
			p.setValue(lPropertyValue);
			retList.add(p);
		}
		return retList;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String getClassVal() {
		Class clazz = widget.getClass();
		String name = clazz.getName();
		return name;
	}

	@Override
	public List<GEvent> getEventList() {
		List<GEvent> retEvents = new ArrayList<GEvent>();
		EventManager em = EventManager.getInstance();

		// Loop over default events, construct it, check if it's supported
		// If it's supported, add it to return list to be returned
		for (Class<? extends GEvent> event : em.getEvents()) {
			Constructor<? extends GEvent> constructor;
			try {
				constructor = event.getConstructor(new Class[] {});
				Object obj = constructor.newInstance();
				if (obj instanceof GEvent) {
					GEvent gEvent = (GEvent) obj;
					if (gEvent.isSupportedBy(this))
						retEvents.add(gEvent);
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		return retEvents;
	}

	/**
	 * This is a critical function in the ripping process.
	 * 
	 * @return the children widgets of the current widget to be added to the
	 *         ripping queue.
	 */
	@Override
	public List<GComponent> getChildren() {
		List<GComponent> children = new ArrayList<GComponent>();

		// Process root window
		if (!SWTGlobals.rootSeen) {
			if (widget instanceof Composite) {
				SWTComposite root = new SWTComposite((Control) widget, window);
				children = root.getChildren();
				SWTGlobals.rootSeen = true;
			} else {
				System.out.println("Expected root window");
				System.exit(1);
			}
		} else {
			children = SWTWidgetAdder.handleWidget(widget, window);
		}
		return children;
	}

	@Override
	public GComponent getParent() {
		return null;
	}

	/**
	 * @return type of windowing interaction. One of: TERMINAL, SYSTEM
	 */
	@Override
	public String getTypeVal() {
		String retProperty;

		if (isTerminal())
			retProperty = GUITARConstants.TERMINAL;
		else
			retProperty = GUITARConstants.SYSTEM_INTERACTION;
		return retProperty;
	}

	/**
	 * This is a critical function in the ripping process. This function is
	 * equivalent in behavior to getChildren().
	 * 
	 * @return whether or not this widget has any children in the GUI hierarchy
	 */
	@Override
	public boolean hasChildren() {
		List<GComponent> children = new ArrayList<GComponent>();

		// Process root window
		if (!SWTGlobals.rootSeen) {
			if (widget instanceof Composite) {
				SWTComposite root = new SWTComposite((Control) widget, window);
				children = root.getChildren();
			} else {
				System.out.println("Expected root window");
				System.exit(1);
			}
		} else {
			children = SWTWidgetAdder.handleWidget(widget, window);
		}
		return children.size() > 0;
	}

	/**
	 * @return whether the current widget can be closed
	 */
	@Override
	public boolean isTerminal() {
		String sName = getTitle();
		if (sName.equalsIgnoreCase("Quit") || sName.equalsIgnoreCase("Exit")) {
			return true;
		}
		return false;
	}

	/**
	 * @return whether the widget can be expanded
	 */
	@Override
	public boolean isEnable() {
		// Menus cannot be expanded
		if (widget instanceof Menu) {
			return false;
		}
		return true;
	}

}
