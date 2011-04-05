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

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.event.EventManager;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Models a SWT Widget
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 * 
 */
public class SWTWidget extends GComponent {
	// TODO make a good, modular API so we can uses subclasses instead of hardcoding

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
			return "";
		}
		
		final String[] title = { "" };
		
		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				Object data = widget.getData("name");
				if (data != null && (data instanceof String)) {
					title[0] = (String) data;
				} else if (widget instanceof Decorations) {
					Decorations shell = (Decorations) widget;
					title[0] = shell.getText();
				} else if (widget instanceof Item) {
					Item item = (Item) widget;
					title[0] = item.getText();
				} else if (widget instanceof Button) {
					Button button = (Button) widget;
					title[0] = button.getText();
				}
				
				// TODO finish once we understand what this method is for
			}
			
		});
		
		return title[0];
	}

	@Override
	public int getX() {
		return getLocation().x;
	}

	@Override
	public int getY() {
		return getLocation().y;
	}
	
	/**
	 * Gets the location of a {@link Control} relative to its ancestor
	 * {@link Shell}. Note that this <code>Shell</code> need not be a direct
	 * parent, e.g. it can be a grandparent. In that case, the position is
	 * relative to the first ancestor <code>Shell</code> encountered.
	 * 
	 * @return
	 */
	private Point getLocation() {
		final Point[] point = new Point[1];
		point[0] = new Point(0, 0);
		
		if (!(widget instanceof Control)) {
			return point[0];
		}
		
		final Control[] control = new Control[1]; 
		control[0] = (Control) widget;

		if (control[0] == null || control[0] instanceof Shell) {
			return point[0];
		}

		control[0].getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if (control[0].isDisposed()) {
					throw new AssertionError("control is disposed");
				}
												
				while (!(control[0] instanceof Shell)) {
					point[0].x += control[0].getLocation().x;
					point[0].y += control[0].getLocation().y;
					control[0] = control[0].getParent();
					if (control[0] == null) {
						break;
					}
				}
			}
		});
		
		return point[0];
	}

	@Override
	public List<PropertyType> getGUIProperties() { 
		final List<String> propertyNames = new ArrayList<String>();
		final List<PropertyType> retList = new ArrayList<PropertyType>();
		
		String title = getTitle();
		if (title != null) {
			PropertyType prop = factory.createPropertyType();
			prop.setName(SWTConstants.TITLE_TAG);
			List<String> propertyValue = new ArrayList<String>();
			propertyValue.add(title);
			prop.setValue(propertyValue);
			retList.add(prop);
		}
		
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
						GUITARLog.log.debug("Ignoring duplicate property: " + sPropertyName);
						continue;
					}

					if (SWTConstants.GUI_PROPERTIES_LIST.contains(sPropertyName)) {
						try {
							Object value = m.invoke(widget, new Object[0]);
							if (value != null) {
								PropertyType p = factory.createPropertyType();
								List<String> lPropertyValue = new ArrayList<String>();
								lPropertyValue.add(value.toString());
								p.setName(sPropertyName);
								p.setValue(lPropertyValue);
								retList.add(p);
								
								propertyNames.add(sPropertyName);
							}
						} catch (IllegalArgumentException e) {
							GUITARLog.log.error(e);
						} catch (IllegalAccessException e) {
							GUITARLog.log.error(e);
						} catch (InvocationTargetException e) {
							GUITARLog.log.error(e);
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
		final List<GComponent> children = new ArrayList<GComponent>();

		// Decorations is superclass of Shell
		if (widget instanceof Decorations) {
			final Decorations decs = (Decorations) widget;
			decs.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					// MenuBar is special case, since not child of parent
					Menu menuBar = decs.getMenuBar();
					if (menuBar != null) {
						children.add(new SWTWidget(menuBar, window));
					}
										
					// TODO handle system trays, hard b/c they're owned by Display
				}
			});
		}
		
		if (widget instanceof Control) {
			final Control control = (Control) widget;
			control.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					// Menu is special case, since not child of parent
					Menu menu = control.getMenu();
					if (menu != null) {
						children.add(new SWTWidget(menu, window));
					}
				}
			});
		}
		
		if (widget instanceof Composite) {
			final Composite composite = (Composite) widget;
			composite.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					for (Control c : composite.getChildren()) {
						children.add(new SWTWidget(c, window));
					}
				}
			});
		} 
				
		children.addAll(SWTWidgetAdder.handleWidget(widget, window));
				
		return children;
	}
	
	@Override
	public GComponent getParent() {
		if (widget instanceof Control) {
			Control control = (Control) widget;
			return new SWTWidget(control.getParent(), window);
		} else {
			return null;
		}
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
		return getChildren().size() > 0;
//		List<GComponent> children = new ArrayList<GComponent>();
//
//		// Process root window
//		if (!SWTGlobals.rootSeen) {
//			if (widget instanceof Composite) {
//				SWTComposite root = new SWTComposite((Control) widget, window);
//				children = root.getChildren();
//			} else {
//				System.out.println("Expected root window");
//				System.exit(1);
//			}
//		} else {
//			children = SWTWidgetAdder.handleWidget(widget, window);
//		}
//		return children.size() > 0;
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
		if (widget instanceof Control) {
			final Control control = (Control) widget;
			final boolean[] isEnabled = new boolean[1];
			
			control.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					isEnabled[0] = control.isEnabled();
				}
			});
			
			return isEnabled[0];
		}
		
		return false;
	}

}
