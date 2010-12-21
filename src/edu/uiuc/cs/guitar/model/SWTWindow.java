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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.ContentsType;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;

/** 
 * Models a SWT Shell
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 *
 */
public class SWTWindow extends GWindow {
	
	Shell shell;
	
	/**
	 * Get the SWT shell object.
	 * 
	 * <p>
	 * 
	 * @return the window
	 */
	public Shell getShell() {
		return shell;
	}
	
	public SWTWindow(Shell shell) {
		this.shell = shell; 
	}

	@Override
	public String getTitle() {
		String sName = shell.getText();
		if (sName != null) {
			return sName;
		}
		return shell.getClass().getName();
	}

	@Override
	public int getX() {
		return shell.getLocation().x;
	}

	@Override
	public int getY() {
		return shell.getLocation().y;
	}

	@Override
	public List<PropertyType> getGUIProperties() {
		List<PropertyType> retList = new ArrayList<PropertyType>();
		Method[] methods = shell.getClass().getMethods();
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

			if (SWTConstants.WINDOW_PROPERTIES_LIST.contains(sPropertyName)) {

				Object value;
				try {
					value = m.invoke(shell, new Object[0]);
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SWTWindow other = (SWTWindow) obj;
		if (shell == null) {
			if (other.shell != null)
				return false;
		} else {
			String myID = getTitle();
			String otherID = other.getTitle();
			if (!myID.equals(otherID))
				return false;
		}
		return true;
	}

	@Override
	public GUIType extractGUIProperties() {
		GUIType retGUI;

		ObjectFactory factory = new ObjectFactory();
		retGUI = factory.createGUIType();

		// Window
		
		Accessible context = shell.getAccessible();

//		AccessibleContext wContext = shell.getAccessibleContext();
		ComponentType dWindow = factory.createComponentType();
		ComponentTypeWrapper gaWindow = new ComponentTypeWrapper(dWindow);
		dWindow = gaWindow.getDComponentType();

		gaWindow.addValueByName("Size", context.getControl()
				.getSize().toString());

		retGUI.setWindow(dWindow);

		// Container

		ComponentType dContainer = factory.createContainerType();
		ComponentTypeWrapper gaContainer = new ComponentTypeWrapper(dContainer);

		gaContainer.addValueByName("Size", context.getControl()
				.getSize().toString());
		dContainer = gaContainer.getDComponentType();

		ContentsType dContents = factory.createContentsType();
		((ContainerType) dContainer).setContents(dContents);

		retGUI.setContainer((ContainerType) dContainer);

		return retGUI;
	}

	@Override
	public boolean isValid() {
		// Check if window is visible
		if (!this.shell.isVisible())
			return false;

		String title = getTitle();
		if (title == null)
			return false;

//		if (INVALID_WINDOW_TITLE.contains(title))
//			return false;

		return true;
	}

	@Override
	public GComponent getContainer() {
		return new SWTWidget((Composite) shell, this);
	}

	@Override
	public boolean isModal() {
		int style = shell.getStyle();
		if ((style & SWT.APPLICATION_MODAL) == SWT.APPLICATION_MODAL) {
			return true;
		}
		return false;
	}

}
