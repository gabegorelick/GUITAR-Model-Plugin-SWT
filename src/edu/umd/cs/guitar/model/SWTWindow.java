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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.widgets.Shell;

import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.ContentsType;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.swtwidgets.SWTWidget;
import edu.umd.cs.guitar.model.swtwidgets.SWTWidgetFactory;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.util.GUITARLog;

/** 
 * Models an SWT Shell
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 *
 */
public class SWTWindow extends GWindow {
	
	private final Shell shell;
	
	// delegate most functionality to an SWTWidget
	private final SWTWidget swtWidget;
		
	public SWTWindow(Shell shell) {
		this.shell = shell;
		this.swtWidget = SWTWidgetFactory.newInstance().newSWTWidget(shell, this); 
	}
	
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

	@Override
	public String getTitle() {
		// TODO figure out in what cases title can be empty
		String title = swtWidget.getTitle();
		if (title.isEmpty()) {
			return shell.getClass().getName();
		} else {
			return title;
		}
	}

	@Override
	public int getX() {
		return swtWidget.getX();
	}

	@Override
	public int getY() {
		return swtWidget.getY();
	}

	@Override
	public List<PropertyType> getGUIProperties() {
		// TODO refactor with SWTComposite.getGUIProperties()
		
		final List<String> propertyNames = new ArrayList<String>();
		final List<PropertyType> retList = new ArrayList<PropertyType>();
		
		// getClass is not an SWT method, so can call from non-UI thread
		final Method[] methods = shell.getClass().getMethods();
		
		// getMethods doesn't guarantee ordering, so sort
		Arrays.sort(methods, new Comparator<Method>() {
			@Override
			public int compare(Method m1, Method m2) {
				return m1.getName().compareTo(m2.getName());
			}	
		});
		
		shell.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if (shell.isDisposed()) {
					throw new AssertionError("shell is disposed");
				}
				
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
					
					// property list is only difference between this and SWTWidget's version
					if (SWTConstants.WINDOW_PROPERTIES_LIST.contains(sPropertyName)) {
						try {
							Object value = m.invoke(shell, new Object[0]);
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
	public int hashCode() {
		return shell.hashCode() + getTitle().hashCode() * 31;
	}

	@Override
	public GUIType extractGUIProperties() {
		final ObjectFactory factory = new ObjectFactory();
		final GUIType retGUI = factory.createGUIType();

		shell.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if (shell.isDisposed()) {
					throw new AssertionError("shell is disposed");
				}
				
				// Window
				
				Accessible context = shell.getAccessible();
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

			}
		});
		
		return retGUI;
	}

	@Override
	public boolean isValid() {
		final boolean[] visible = new boolean[1];
		
		shell.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				// Check if window is visible
				visible[0] = shell.isVisible();
			}
		});
		
		return visible[0];
	}

	@Override
	public GComponent getContainer() {
		return SWTWidgetFactory.newInstance().newSWTWidget(shell, this);
	}

	@Override
	public boolean isModal() {
		final int[] style = new int[1];
		
		shell.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				style[0] = shell.getStyle();
			}
		});
		
		// style is a bit field, how un-Java
		return (style[0] & SWT.APPLICATION_MODAL) == SWT.APPLICATION_MODAL;
	}

}
