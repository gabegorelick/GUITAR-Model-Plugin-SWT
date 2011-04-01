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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.event.EventManager;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Models a SWT Composite
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 */
public class SWTComposite extends GComponent { // TODO Gabe: subclass SWTWidget instead?

	private final Control control;

	/**
	 * @param component
	 */
	public SWTComposite(Control control, GWindow window) {
		super(window);

		this.control = control;
	}

	/**
	 * @return the component
	 */
	public Control getControl() {
		return control;
	}

	@Override
	public String getTitle() {
		if (control == null) {
			return "";
		}
		
		// workaround since can't set non-final value in anonymous inner class
		final String[] text = new String[1];
		
		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if (control.isDisposed()) {
					throw new AssertionError("Widget is disposed");
				}
				
				text[0] = control.getShell().getText();
			}
		});
		
		// Shell.getText returns empty String by default, NOT null
		if (text[0].isEmpty()) {
			text[0] = getIconName();
		} 
		
		if (text[0] == null) {
			return "";
		} else {
			return text[0];
		}
	}
	
	private Point getLocation() {
		final Control[] widget = new Control[1]; 
		widget[0] = control;

		final Point[] point = new Point[1];
		point[0] = new Point(0, 0);
		
		if (widget[0] == null || widget[0] instanceof Shell) {
			return point[0];
		}

		widget[0].getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if (widget[0].isDisposed()) {
					throw new AssertionError("Control is disposed");
				}
												
				while (!(widget[0] instanceof Shell)) {
					point[0].x += widget[0].getLocation().x;
					point[0].y += widget[0].getLocation().y;
					widget[0] = widget[0].getParent();
					if (widget[0] == null) {
						break;
					}
				}
			}
		});
		
		return point[0];
	}

	@Override
	public int getX() {
		return getLocation().x;
	}

	@Override
	public int getY() {
		return getLocation().y; 	
	}

	@Override
	public List<PropertyType> getGUIProperties() {
		List<PropertyType> retList = new ArrayList<PropertyType>();
		// Other properties

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

		// Icon
		sValue = null;
		sValue = getIconName();
		if (sValue != null) {
			p = factory.createPropertyType();
			p.setName(SWTConstants.ICON_TAG);
			lPropertyValue = new ArrayList<String>();
			lPropertyValue.add(sValue);
			p.setValue(lPropertyValue);
			retList.add(p);
		}

		// // Index in parent
		// if (isSelectedByParent()) {
		//
		// // Debugger.pause("GOT HERE!!!");
		//
		// sValue = null;
		// sValue = getIndexInParent().toString();
		//
		// p = factory.createPropertyType();
		// p.setName(SWTConstants.INDEX_TAG);
		// lPropertyValue = new ArrayList<String>();
		// lPropertyValue.add(sValue);
		// p.setValue(lPropertyValue);
		// retList.add(p);
		//
		// }

		// Get bean properties
		List<PropertyType> lBeanProperties = getGUIBeanProperties();
		retList.addAll(lBeanProperties);

		// Get Screenshot
		return retList;
	}

	@Override
	public String getClassVal() {
		// getClass not an SWT method, so can call on non-UI thread
		return control.getClass().getName();
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
	 * This is a critical function in the GUI ripping process.
	 * 
	 * @return a list of GUI components that are beneath the root window in the
	 *         GUI hierarchy.
	 */
	@Override
	public List<GComponent> getChildren() {
		final ArrayList<GComponent> children = new ArrayList<GComponent>();

		try {
			final Composite composite = (Composite) control;
			
			composite.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					if (composite.isDisposed()) {
						throw new AssertionError("composite is disposed");
					}
					
					// Add a menu if it exists
					Menu menu = composite.getShell().getMenuBar();
					if (menu != null) {
						children.add(new SWTWidget(menu, window));
					} else {
						menu = composite.getShell().getMenu();
						if (menu != null) {
							children.add(new SWTWidget(menu, window));
						}
					}

					// Add a tray if it exists
					Tray tray = composite.getDisplay().getSystemTray();
					if (tray != null && tray.getItemCount() > 0) {
						children.add(new SWTWidget(tray, window));
					}

					// Add any other children
					for (Widget widget : composite.getShell().getChildren()) {
						children.add(new SWTWidget(widget, window));
					}
				}
			});
			
		} catch (Exception e) {
			GUITARLog.log.error("getChildren");
			GUITARLog.log.error(e);
		}
		
		return children;
	}

	@Override
	public GComponent getParent() {
		final Composite[] parent = new Composite[1];
		
		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if (control.isDisposed()) {
					throw new AssertionError("control is disposed");
				}
				
				parent[0] = control.getParent();
			}
		});
		
		return new SWTWidget(parent[0], window);
	}

	@Override
	public String getTypeVal() {
		String retProperty;

		if (isTerminal())
			retProperty = GUITARConstants.TERMINAL;
		else
			retProperty = GUITARConstants.SYSTEM_INTERACTION;
		return retProperty;
	}

	@Override
	public boolean hasChildren() {
		List<GComponent> children = getChildren();
		if (children.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Check if this component is a terminal widget
	 * 
	 * <p>
	 * 
	 * @return
	 */
	@Override
	public boolean isTerminal() {
		String sName = getTitle();

		List<AttributesTypeWrapper> termSig = SWTConstants.sTerminalWidgetSignature;
		for (AttributesTypeWrapper sign : termSig) {
			// TODO implement this when we have terminal widget signatures
//			String titleVals = sign.getFirstValByName(SWTConstants.TITLE_TAG);
//
//			if (titleVals == null)
//				continue;
//
//			if (titleVals.equalsIgnoreCase(sName))
//				return true;

		}

		return false;
	}

	@Override
	public boolean isEnable() {
		final boolean[] enabled = new boolean[1];
		
		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if (control.isDisposed()) {
					throw new AssertionError("control is disposed");
				}
				
				enabled[0] = control.isEnabled();
			}
		});
		
		return enabled[0];
		
		// I have no idea why they were doing this reflectively
//		try {
//			Class<?>[] types = new Class<?>[] {};	
//			
////			Method method = control.getClass().getMethod("isEnabled", types);
////			Object result = method.invoke(control, new Object[0]);
//
//			if (result instanceof Boolean)
//				return (Boolean) result;
//			else
//				return false;
//		} catch (Exception e) {
//			return false;
//		}
	}

	/**
	 * Parse the icon name of a widget from the resource's absolute path.
	 * 
	 * <p>
	 * 
	 * @param component
	 * @return
	 */
	private String getIconName() {
		String retIcon = null;
		try {
			Class<?> partypes[] = new Class[0];
			// FIXME this method doesn't seem to work
			Method m = control.getClass().getMethod("getIcon", partypes);

			String sIconPath = null;
			// if (m != null) {
			// Object obj = (m.invoke(aComponent, new Object[0]));
			// if (obj != null)
			// sIconPath = obj.toString();
			// }

			if (m != null) {
				Object obj = (m.invoke(control, new Object[0]));

				if (obj != null) {
					sIconPath = obj.toString();
				}
			}

			if (sIconPath == null || sIconPath.contains("@"))
				return null;

			String[] sIconElements = sIconPath.split(File.separator);
			retIcon = sIconElements[sIconElements.length - 1];

		} catch (SecurityException e) {
			// e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			// e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
			return null;
		} catch (InvocationTargetException e) {
			// e.printStackTrace();
			return null;
		}
		return retIcon;
	}

	/**
	 * Check if the component is activated by an action in parent
	 * 
	 * @return
	 */
//	private boolean isSelectedByParent() {
//		Composite parent = control.getParent();
//
//		if (parent == null)
//			return false;
//
//		if (parent instanceof TabFolder)
//			return true;
//		return false;
//	}

	/**
	 * Get all bean properties of the component
	 * 
	 * @return
	 */
	private List<PropertyType> getGUIBeanProperties() { // TODO refactor with SWTWindow
		final List<String> propertyNames = new ArrayList<String>();
		final List<PropertyType> retList = new ArrayList<PropertyType>();
		
		final Method[] methods = control.getClass().getMethods();
		Arrays.sort(methods, new Comparator<Method>() {
			@Override
			public int compare(Method m1, Method m2) {
				return m1.getName().compareTo(m2.getName());
			}
		});

		control.getDisplay().syncExec(new Runnable() {
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
							value = m.invoke(control, new Object[0]);
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

	/**
	 * Get component index in its parent
	 * 
	 * @return
	 */
	// private Integer getIndexInParent() {
	// control.get
	// AccessibleContext aContext = control.getAccessibleContext();
	// if (aContext != null) {
	// return aContext.getAccessibleIndexInParent();
	// }
	//
	// return 0;
	// }

}
