/* Copyright (c) 2010
 * Matt Kirn (mattkse@gmail.com) and Alex Loeb (atloeb@gmail.com)
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.event.EventManager;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.internal.SWTGlobals;
import edu.umd.cs.guitar.internal.SWTWidgetAdder;
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

	private final Widget widget;

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
		if (widget == null) {
			return ""; // FIXME
		}
		
		final String[] title = { "" };
		
		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if (widget instanceof Item) {
					Item item = (Item) widget;
					title[0] = item.getText();
				}
			}
		});
		
		return title[0];
	}

	@Override
	public int getX() {
		return 0; // TODO is this right?
	}

	@Override
	public int getY() {
		return 0;
	}

	@Override
	public List<PropertyType> getGUIProperties() { // TODO Gabe: refactor with SWTComposite's version 
		final List<String> propertyNames = new ArrayList<String>();
		final List<PropertyType> retList = new ArrayList<PropertyType>();
		
		final Method[] methods = widget.getClass().getMethods();
		Arrays.sort(methods, new Comparator<Method>() {
			@Override
			public int compare(Method m1, Method m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});

		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				PropertyType p;
				List<String> lPropertyValue;

				for (Method m : methods) {
					if (m.getParameterTypes().length > 0) {
						continue;
					}
					
					String sMethodName = m.getName();
					String sPropertyName = sMethodName;

					if (sPropertyName.startsWith("get")) {
						sPropertyName = sPropertyName.substring(3);
					} else if (sPropertyName.startsWith("is")) {
						sPropertyName = sPropertyName.substring(2);
					} else {
						continue;
					}

					// make sure property is in lower case
					sPropertyName = sPropertyName.toLowerCase();
					
					// we don't want duplicate properties, this happens, e.g. in Shell
					// which has getVisible() and isVisible()
					if (propertyNames.contains(sPropertyName)) {
						continue;
					}

					if (SWTConstants.GUI_PROPERTIES_LIST.contains(sPropertyName)) {

						Object value;
						try {
							// value = m.invoke(aComponent, new Object[0]);
							value = m.invoke(widget, new Object[0]);
							if (value != null) {
								p = factory.createPropertyType();
								lPropertyValue = new ArrayList<String>();
								lPropertyValue.add(value.toString());
								p.setName(sPropertyName);
								p.setValue(lPropertyValue);
								retList.add(p);
								
								propertyNames.add(sPropertyName);
							}
						} catch (IllegalArgumentException e) {
						} catch (IllegalAccessException e) {
						} catch (InvocationTargetException e) {
						}
					}
				}
			}
		});
		
		return retList;
	}

	@Override
	public String getClassVal() {
		Class<? extends Widget> clazz = widget.getClass();
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
		return null; // TODO why return null?
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
		if (widget instanceof Menu) { // TODO check for more conditions?
			return false;
		}
		return true;
	}

}
