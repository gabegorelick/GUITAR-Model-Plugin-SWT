package edu.umd.cs.guitar.model.swtwidgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWindow;

/**
 * Models a swt table
 * 
 *
 */
public class SWTTable extends SWTComposite {

	private final Table table;
	
	protected SWTTable(Table table, SWTWindow window) {
		super(table, window);
		this.table = table;
	}
	/**
	 * 
	 * @return whether the children of this widget
	 */
	@Override
	public List<GComponent> getChildren() {
		final List<GComponent> children = new ArrayList<GComponent>();
		final SWTWidgetFactory factory = SWTWidgetFactory.newInstance();
		
		table.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				for (TableColumn col : table.getColumns()) {
					children.add(factory.newSWTWidget(col, getWindow()));
				}
				for (TableItem i : table.getItems()) {
					children.add(factory.newSWTWidget(i, getWindow()));
				}
			}
		});
		 				
		return children;
	}

}
