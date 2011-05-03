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
package edu.umd.cs.guitar.model;

import java.util.List;

import org.eclipse.swt.widgets.Shell;

import edu.umd.cs.guitar.model.swtwidgets.SitarWidget;

/**
 * Encapsulates all the state that can be gleaned from interaction with a
 * widget. Used so that the ripper only needs to interact with a widget once to
 * determine all its important properties, which minimizes potentially harmful
 * side effects.
 * 
 * @author Gabe Gorelick
 * 
 * @see SitarWidget#interact()
 */
public class SitarGUIInteraction {

	private final SitarWidget source;
	
	private List<Shell> openedShells;
	private List<Shell> closedShells;
	
	private boolean terminal;
	
	public SitarGUIInteraction(SitarWidget source) {
		this.source = source;			
	}
	
	public SitarWidget getSource() {
		return source;
	}

	public List<Shell> getOpenedShells() {
		return openedShells;
	}
	
	public void setOpenedShells(List<Shell> shells) {
		this.openedShells = shells;
	}

	public List<Shell> getClosedShells() {
		return closedShells;
	}
	
	public void setClosedShells(List<Shell> shells) {
		this.closedShells = shells;
	}

	public boolean isTerminal() {
		return terminal;
	}
	
	public void setTerminal(boolean terminal) {
		this.terminal = terminal;
	}

}
