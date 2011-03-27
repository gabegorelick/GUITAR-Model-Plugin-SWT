package edu.umd.cs.guitar.event;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import javax.swing.SwingUtilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTConstants;
import edu.umd.cs.guitar.model.SWTWidget;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class SWTActionEDT implements GEvent {

	protected Widget getComponent(GComponent gComponent) {
		SWTWidget jxComponent = (SWTWidget) gComponent;
		return jxComponent.getWidget();
	}
	
	public SWTActionEDT() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isSupportedBy(GComponent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void perform(GComponent gComponent, Hashtable<String, List<String>> optionalData) {
		if (gComponent == null) {
			return;
		}

		// Accessible aComponent = getAccessible(gComponent);
		//
		// if (aComponent == null)
		// return;
		// AccessibleContext aContext = aComponent.getAccessibleContext();
		final Widget component = getComponent(gComponent);
		component.getDisplay().syncExec(new Runnable() {
			public void run() {
				Event event = new Event();
				for(int i = 0; i < SWTConstants.swtEventList.length;i++) {
					event.type = SWTConstants.swtEventList[i];
					component.notifyListeners(SWTConstants.swtEventList[i], event);
				}
				
			}
		});
		/*
		AccessibleContext aContext = component.getAccessibleContext();

		if (aContext == null)
			return;
		final AccessibleAction aAction = aContext.getAccessibleAction();

		if (aAction == null)
			return;

		// try {
		int nActions = aAction.getAccessibleActionCount();
		if (nActions > 0) {

			// aAction.doAccessibleAction(0);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					aAction.doAccessibleAction(0);
				}
			});

		}
		*/
		
	}

	@Override
	public void perform(GComponent gComponent, Object parameters,
			Hashtable<String, List<String>> optionalData) {

		perform(gComponent,optionalData);
		
	}

}
