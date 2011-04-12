package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Menu;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;

public class SWTDecorations extends SWTComposite {

	private final Decorations decorations;
	
	protected SWTDecorations(Decorations widget, SWTWindow window) {
		super(widget, window);
		this.decorations = widget;
	}
	
	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();
		final SWTWidgetFactory factory = SWTWidgetFactory.newInstance();

		decorations.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				// MenuBar is special case, since not child of parent
				Menu menuBar = decorations.getMenuBar();
				if (menuBar != null) {
					children.add(factory.newSWTWidget(menuBar, getWindow()));
				}
				
				for (Control c : decorations.getChildren()) {
					children.add(factory.newSWTWidget(c, getWindow()));
				}

				// TODO handle system trays, hard b/c they're owned by Display
			}
		});
						
		return children;
	}

}
