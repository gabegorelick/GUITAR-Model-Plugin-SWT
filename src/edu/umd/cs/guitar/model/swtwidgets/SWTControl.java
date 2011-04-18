package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;

/**
 * Models a swt control widget
 * 
 *
 */
public class SWTControl extends SWTWidget {

	private final Control control;
	
	protected SWTControl(Control widget, SWTWindow window) {
		super(widget, window);
		this.control = widget;
	}
	
	/**
	 * @return this widget
	 */
	@Override
	public Control getWidget() {
		return control;
	}

	/**
	 * @return the children of this widget
	 */
	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();

		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				SWTWidgetFactory factory = SWTWidgetFactory.newInstance();
				
				// Menu is special case, since not child of parent
				Menu menu = control.getMenu();
				if (menu != null) {
					children.add(factory.newSWTWidget(menu, getWindow()));
				}
			}
		});
		
		return children;
	}
	
	/**
	 * @return the parent of this widget
	 */
	@Override
	public SWTComposite getParent() {
		SWTWidgetFactory factory = SWTWidgetFactory.newInstance();
		
		final Composite[] parent = new Composite[1];
		
		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				parent[0] = control.getParent();
			}
		});
		
		return factory.newSWTComposite(parent[0], getWindow());
	}
	
	/**
	 * <p>
	 * Returns whether this widget is enabled. As {@link Widget Widgets} by
	 * default have no notion of being enabled, this method is abstract.
	 * </p>
	 * 
	 * @return whether this widget is enabled
	 */
	@Override
	public boolean isEnabled() {
		final boolean[] isEnabled = new boolean[1];
		
		control.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				isEnabled[0] = control.isEnabled();
			}
		});
		
		return isEnabled[0];
	}
}
