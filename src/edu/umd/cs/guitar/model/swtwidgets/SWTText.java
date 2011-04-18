package edu.umd.cs.guitar.model.swtwidgets;

import java.util.List;

import org.eclipse.swt.widgets.Text;

import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.event.SWTEditableTextAction;
import edu.umd.cs.guitar.model.SWTWindow;

public class SWTText extends SWTControl {

	private final Text text;
	
	protected SWTText(Text text, SWTWindow window) {
		super(text, window);
		this.text = text;
	}
	
	@Override
	public List<GEvent> getEventList() {
		List<GEvent> events = super.getEventList();
				
		final boolean[] editable = { false };
		text.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				editable[0] = text.getEditable();
			}
		});
		
		if (editable[0]) {
			events.add(new SWTEditableTextAction());
		} 
		
		return events;
	}

}
