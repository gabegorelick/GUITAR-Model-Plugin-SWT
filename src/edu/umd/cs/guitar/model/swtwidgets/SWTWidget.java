/* Copyright (c) 2010
 * Matt Kirn (mattkse@gmail.com) and Alex Loeb (atloeb@gmail.com)
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.model.swtwidgets;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.event.EventManager;
import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GUITARConstants;
import edu.umd.cs.guitar.model.SWTConstants;
import edu.umd.cs.guitar.model.SWTWindow;
import edu.umd.cs.guitar.model.data.PropertyType;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Models a SWT Widget
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 * 
 */
public abstract class SWTWidget extends GComponent {
	
	private final Widget widget;
	private final SWTWindow window;

	protected SWTWidget(Widget widget, SWTWindow window) {
		super(window);
		this.widget = widget;
		this.window = window;
	}

	/**
	 * @return the component
	 */
	public Widget getWidget() {
		return widget;
	}
	/**
	 * @return the window
	 */
	
	public SWTWindow getWindow() {
		return window;
	}
/**
 * @return the title of the widget
 */
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
	/**
	 * returns this widget's x coordinate
	 */
	@Override
	public int getX() {
		return getLocation().x;
	}
	/**
	 * returns this widget's y coordinate
	 */
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
	protected Point getLocation() {
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

	/**
	 * 
	 * @returns the gui properties in list form
	 */
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

	/**
	 * returns this widget's name
	 */
	@Override
	public String getClassVal() {
		return widget.getClass().getName();
	}

	/**
	 * @return the list of events in this widget
	 */
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
	public abstract List<GComponent> getChildren();

	/**
	 * Get this widget's parent. By default, {@link Widget Widgets} have no
	 * parent, so this method returns <code>null</code>. Subclasses are
	 * encouraged to override this method if they wrap widgets that do have
	 * parents.
	 * 
	 * @return <code>null</code>
	 */
	@Override
	public GComponent getParent() {
		return null;
	}

	/**
	 * @return type of windowing interaction. One of: TERMINAL, SYSTEM
	 */
	@Override
	public String getTypeVal() {
		if (isTerminal()) {
			return GUITARConstants.TERMINAL;
		} else {
			return GUITARConstants.SYSTEM_INTERACTION;
		}
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
	}

	/**
	 * <p>
	 * Checks whether a widget is terminal. A widget is terminal if it closes
	 * its parent shell. Note that this is technically different from
	 * terminating the application (although closing the root shell will
	 * terminate the application if it is the only root shell).
	 * </p>
	 * <p>
	 * Instead of merely checking the title of this widget for things like
	 * "Quit" or "Exit", as JFCModel does, this method invokes all actions the
	 * widget is listening on and checks if any of them attempt to close the
	 * shell.
	 * </p>
	 * 
	 * @return <code>true</code> if widget is terminal, <code>false</code> if
	 *         not
	 */
	@Override
	public boolean isTerminal() {
		final boolean[] terminal = { false };
				
		widget.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				Shell shell = window.getShell();
							
				// Remove existing close listeners so they don't get notified.
				// This may not be necessary on all platforms, but better safe
				// than sorry
				Listener[] closeListeners = shell.getListeners(SWT.Close);
				for (Listener l : closeListeners) {
					shell.removeListener(SWT.Close, l);
				}
				
				ShellListener listener = new ShellAdapter() {
					@Override
					public void shellClosed(ShellEvent e) {
						terminal[0] = true;
						e.doit = false; // prevent shell from actually closing
					}
				};
				
				shell.addShellListener(listener);
				
				// signal all events this widget is listening for
				Event event = new Event();
				for (int i : SWTConstants.SWT_EVENT_LIST) {
					event.type = i;
					if (widget.isListening(i)) {
						widget.notifyListeners(i, event);
					}
				}
				
				// remove our close listener
				shell.removeShellListener(listener);
				
				// add back the close listeners we removed
				for (Listener l : closeListeners) {
					shell.addListener(SWT.Close, l);
				}
			}
		});
		
		return terminal[0];
	}

	/**
	 * Returns whether this widget is enabled.
	 * 
	 * @deprecated Use {@link #isEnabled()} instead
	 * @return whether the widget is enabled
	 */
	@Override
	@Deprecated
	public final boolean isEnable() {
		return isEnabled();
	}

	/**
	 * <p>
	 * Returns whether this widget is enabled. As {@link Widget Widgets} by
	 * default have no notion of being enabled, this method is abstract.
	 * </p>
	 * <p>
	 * This method is simply the correct spelling of {@link #isEnable()}, but
	 * its use is preferred.
	 * </p>
	 * 
	 * @return whether this widget is enabled
	 */
	public abstract boolean isEnabled();

}
