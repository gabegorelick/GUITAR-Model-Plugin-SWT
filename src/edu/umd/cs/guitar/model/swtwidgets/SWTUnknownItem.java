package edu.umd.cs.guitar.model.swtwidgets;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Item;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;

public class SWTUnknownItem extends SWTItem {

	protected SWTUnknownItem(Item item, GWindow window) {
		super(item, window);
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public List<GComponent> getChildren() {
		return Collections.emptyList();
	}
}
