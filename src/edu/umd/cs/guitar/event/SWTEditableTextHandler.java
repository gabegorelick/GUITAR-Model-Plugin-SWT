package edu.umd.cs.guitar.event;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleEditableText;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.widgets.Text;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWidget;
import edu.umd.cs.guitar.util.Debugger;
import edu.umd.cs.guitar.util.GUITARLog;

public class SWTEditableTextHandler extends SWTEventHandler{

	public SWTEditableTextHandler() {
	}
	
	private static String GUITAR_DEFAULT_TEXT = "GUITAR DEFAULT TEXT: "
		+ "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

	@Override
	public boolean isSupportedBy(GComponent gComponent) {
		if (!(gComponent instanceof SWTWidget))
			return false;
		SWTWidget jComponent = (SWTWidget) gComponent;
		Widget component = jComponent.getWidget();
		if (component instanceof Text)
			return true;
		else
			return false;
		/*AccessibleContext aContext = component.getAccessibleContext();
		
		if (aContext == null)
			return false;

		Object event;

		// Text
		event = aContext.getAccessibleEditableText();
		if (event != null) {
			return true;
		}
		return false*/
	}

	@Override
	protected void performImpl(GComponent gComponent,
			Hashtable<String, List<String>> optionalData) {
		List<String> args = new ArrayList<String>();
		args.add(GUITAR_DEFAULT_TEXT);
		performImpl(gComponent, args,optionalData);
		
	}

	@Override
	protected void performImpl(GComponent gComponent, Object parameters,
			Hashtable<String, List<String>> optionalData) {
		
		if (gComponent == null) {
			return;
		}

		if (parameters instanceof List) {

			List<String> lParameter = (List<String>) parameters;
			String sInputText;
			if (lParameter == null) {
				sInputText = GUITAR_DEFAULT_TEXT;
			} else
				sInputText = (lParameter.size() != 0) ? lParameter.get(0)
						: GUITAR_DEFAULT_TEXT;

			// Accessible aComponent = getAccessible(gComponent);

			// AccessibleContext aContext = aComponent.getAccessibleContext();
			Widget component = getComponent(gComponent);
			if (component != null && component instanceof Text) {
				Text textWidget = (Text) component;
				textWidget.setText(sInputText);
			}
			/*AccessibleContext aContext = component.getAccessibleContext();
			AccessibleEditableText aTextEvent = aContext.getAccessibleEditableText();

			if (aTextEvent == null) {
				System.err.println(this.getClass().getName()
						+ " doesn't support");
				return;
			}
			try {
				aTextEvent.setTextContents(sInputText);
			} catch (Exception e) {
				try {
					Method setText = component.getClass().getMethod("setText",
							String.class);
					setText.invoke(component, sInputText);

					Debugger.pause(GUITAR_DEFAULT_TEXT);

				} catch (SecurityException e1) {
					// TODO Auto-generated catch block
					GUITARLog.log.error(e1);
				} catch (NoSuchMethodException e1) {
					GUITARLog.log.error(e1);
				} catch (IllegalArgumentException e1) {
					// TODO Auto-generated catch block
					GUITARLog.log.error(e1);
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					GUITARLog.log.error(e1);
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					GUITARLog.log.error(e1);
				}
			}*/

		}
		
	}

}
