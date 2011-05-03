/*	
 *  Copyright (c) 2009-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
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
package edu.umd.cs.guitar.event;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.swt.widgets.Widget;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.swtwidgets.SitarWidget;

/**
 * Superclass of all events in SWTGuitar. Subclasses are required to implement
 * the {@link #perform(GComponent) perform} method.
 * 
 * @author Gabe Gorelick
 */
public abstract class SitarAction implements GEvent {

	/**
	 * Default constructor.
	 */
	protected SitarAction() {
		// this space left intentionally blank
	}

	/**
	 * Get a <code>Widget</code> from a <code>GComponent</code>.
	 * 
	 * @param gComponent
	 * @return <code>Widget</code> contained in specified
	 *         <code>GComponent</code>
	 * @throws ClassCastException
	 *             thrown if argument is not an {@link SitarWidget}.
	 */
	protected Widget getWidget(GComponent gComponent) {
		SitarWidget widget = (SitarWidget) gComponent;
		return widget.getWidget();
	}

	/**
	 * Check if this action is supported by a given component.
	 * 
	 * @deprecated Use {@link SitarWidget#getEventList()} instead.
	 * 
	 * @throws ClassCastException
	 *             thrown if argument is not an {@link SitarWidget}
	 */
	@Deprecated
	@Override
	public final boolean isSupportedBy(GComponent gComponent) {
		SitarWidget widget = (SitarWidget) gComponent;
		return widget.getEventList().contains(this);
	}

	/**
	 * Perform this action on a given component.
	 * 
	 * @param gComponent
	 *            the component to perform this action on
	 */
	public abstract void perform(GComponent gComponent);

	/**
	 * This method behaves exactly the same as {@link #perform(GComponent)}.
	 * 
	 * @deprecated Use {@link #perform(GComponent)} instead. There is no need to
	 *             pass optional data to <code>SWTActions</code>. If such a need
	 *             arises, it makes more sense to make that data a member of the
	 *             class, and not just pass it to this method.
	 * 
	 * @param gComponent
	 *            the component to perform the event on
	 * @param optionalData
	 *            this parameter is not used
	 */
	@Deprecated
	@Override
	public final void perform(GComponent gComponent,
			Hashtable<String, List<String>> optionalData) {
		
		perform(gComponent);
	}

	/**
	 * This method behaves exactly the same as {@link #perform(GComponent)}.
	 * 
	 * @deprecated Use {@link #perform(GComponent)} instead. There is no need to
	 *             pass optional data to <code>SWTActions</code>. If such a need
	 *             arises, it makes more sense to make that data a member of the
	 *             class, and not just pass it to this method.
	 * 
	 * @param gComponent
	 *            the component to perform the event on
	 * @param parameters
	 *            this parameter is not used
	 * @param optionalData
	 *            this parameter is not used
	 */
	@Deprecated
	@Override
	public final void perform(GComponent gComponent, Object parameters,
			Hashtable<String, List<String>> optionalData) {

		perform(gComponent, optionalData);
	}

}
