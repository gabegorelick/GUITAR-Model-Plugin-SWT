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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.event.EventManager;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Models a SWT Composite
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 */
public class SWTComposite extends GComponent {

	Control control;

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
		String sName = "";
		if (control == null)
			return "";

		sName = control.getShell().getText();

		if (sName != null)
			return sName;

		if (sName == null)
			sName = getIconName();

		if (sName != null)
			return sName;

		return sName;
	}

	@Override
	public int getX() {
		Control pointer = control;

		if (pointer == null || pointer instanceof Shell)
			return 0;

		// Component pointerParent = component.getParent();

		int x = 0;

		while (!(pointer instanceof Shell)) {
			x += pointer.getLocation().x;
			pointer = pointer.getParent();
			if (pointer == null)
				break;
		}

		return x;
	}

	@Override
	public int getY() {
		Control pointer = control;

		if (pointer == null || pointer instanceof Shell)
			return 0;

		// Component pointerParent = component.getParent();

		int y = 0;

		while (!(pointer instanceof Shell)) {
			y += pointer.getLocation().y;
			pointer = pointer.getParent();
			if (pointer == null)
				break;
		}

		return y;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
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
		ArrayList<GComponent> children = new ArrayList<GComponent>();

		try {
			Composite composite = (Composite) control;

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
		} catch (Exception e) {
			GUITARLog.log.error("getChildren");
			GUITARLog.log.error(e);
		}
		return children;
	}

	@Override
	public GComponent getParent() {
		Composite parent = this.control.getParent();

		return new SWTWidget(parent, window);
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
			String titleVals = sign.getFirstValByName(SWTConstants.TITLE_TAG);

			if (titleVals == null)
				continue;

			if (titleVals.equalsIgnoreCase(sName))
				return true;

		}

		return false;
	}

	@Override
	public boolean isEnable() {
		try {
			Class[] types = new Class[] {};
			Method method = control.getClass().getMethod("isEnabled", types);
			Object result = method.invoke(control, new Object[0]);

			if (result instanceof Boolean)
				return (Boolean) result;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
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
	private boolean isSelectedByParent() {
		Composite parent = control.getParent();

		if (parent == null)
			return false;

		if (parent instanceof TabFolder)
			return true;
		return false;
	}

	/**
	 * Get all bean properties of the component
	 * 
	 * @return
	 */
	private List<PropertyType> getGUIBeanProperties() {
		List<PropertyType> retList = new ArrayList<PropertyType>();
		Method[] methods = control.getClass().getMethods();
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
			} else
				continue;

			// make sure property is in lower case
			sPropertyName = sPropertyName.toLowerCase();

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
					}
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				} catch (InvocationTargetException e) {
				}
			}
		}
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
