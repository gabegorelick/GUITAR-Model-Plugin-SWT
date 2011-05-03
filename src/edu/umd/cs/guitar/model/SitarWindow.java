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
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.widgets.Shell;

import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.ContainerType;
import edu.umd.cs.guitar.model.data.ContentsType;
import edu.umd.cs.guitar.model.data.GUIType;
import edu.umd.cs.guitar.model.data.ObjectFactory;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.model.swtwidgets.SitarWidget;
import edu.umd.cs.guitar.model.swtwidgets.SitarWidgetFactory;
import edu.umd.cs.guitar.model.wrapper.ComponentTypeWrapper;
import edu.umd.cs.guitar.util.GUITARLog;

/** 
 * Adapts GUITAR's {@code GWindow} type to work with {@link Shell Shells}.
 * 
 * @author Gabe Gorelick
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 *
 */
public class SitarWindow extends GWindow {
	
	private final Shell shell;
	
	// delegate most functionality to an SitarWidget
	private final SitarWidget swtWidget;
	
	/**
	 * Default constructor.
	 * @param shell the shell to wrap
	 */
	public SitarWindow(Shell shell) {
		this.shell = shell;
		this.swtWidget = SitarWidgetFactory.INSTANCE.newSWTWidget(shell, this); 
	}

	/**
	 * Get the {@code Shell} this class wraps.
	 * 
	 * @return the shell this class wraps
	 */
	public Shell getShell() {
		return shell;
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 * 
	 * @see SitarWidget#getX()
	 */
	@Override
	public int getX() {
		return swtWidget.getX();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see SitarWidget#getY()
	 */
	@Override
	public int getY() {
		return swtWidget.getY();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PropertyType> getGUIProperties() {
		// TODO refactor with SitarComposite.getGUIProperties()
		
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
				List<String> propertyNames = new ArrayList<String>();
				
				synchronized (retList) {
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

						// property list is only difference between this and SitarWidget's version
						if (SitarConstants.WINDOW_PROPERTIES_LIST
								.contains(sPropertyName)) {
							try {
								Object value = m.invoke(shell, new Object[0]);
								if (value != null) {
									PropertyType p = factory
											.createPropertyType();
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
			}
		});
		
		return retList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SitarWindow other = (SitarWindow) obj;
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return shell.hashCode() + getTitle().hashCode() * 31;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This method is not used by SWTGuitar.
	 */
	@Override
	public GUIType extractGUIProperties() {
		final GUIType retGUI = factory.createGUIType();

		shell.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				synchronized (retGUI) {
					ObjectFactory factory = new ObjectFactory();
					// Window	
					Accessible context = shell.getAccessible();
					ComponentType dWindow = factory.createComponentType();
					ComponentTypeWrapper gaWindow = new ComponentTypeWrapper(
							dWindow);
					dWindow = gaWindow.getDComponentType();
					gaWindow.addValueByName("Size", context.getControl()
							.getSize().toString());
					retGUI.setWindow(dWindow);
					ComponentType dContainer = factory.createContainerType();
					ComponentTypeWrapper gaContainer = new ComponentTypeWrapper(
							dContainer);
					gaContainer.addValueByName("Size", context.getControl()
							.getSize().toString());
					dContainer = gaContainer.getDComponentType();
					ContentsType dContents = factory.createContentsType();
					((ContainerType) dContainer).setContents(dContents);
					retGUI.setContainer((ContainerType) dContainer);
				}
			}
		});
		
		return retGUI;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * {@code SitarWindow}s are valid if they are visible.
	 * 
	 * @return {@code true} if window is valid
	 * @see Shell#isVisible()
	 */
	@Override
	public boolean isValid() {
		final AtomicBoolean visible = new AtomicBoolean();
		
		shell.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				// Check if window is visible
				visible.set(shell.isVisible());
			}
		});
		
		return visible.get();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return the {@link SitarWidget} we delegate to
	 */
	@Override
	public GComponent getContainer() {
		return swtWidget;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return {@code true} if window is modal
	 */
	@Override
	public boolean isModal() {
		final AtomicBoolean modal = new AtomicBoolean();
		
		shell.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				// style is a bit field, how un-Java
				int style = shell.getStyle() & SWT.APPLICATION_MODAL;
				modal.set(style == SWT.APPLICATION_MODAL);
			}
		});
		
		return modal.get();
	}

}
