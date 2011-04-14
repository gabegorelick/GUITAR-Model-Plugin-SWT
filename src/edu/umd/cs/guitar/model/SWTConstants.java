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
/* Copyright (c) 2010
 * Matt Kirn (mattkse@gmail.com) and Alex Loeb (atloeb@gmail.com)
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
package edu.umd.cs.guitar.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;

import edu.umd.cs.guitar.event.GEvent;
import edu.umd.cs.guitar.event.SWTAction;
import edu.umd.cs.guitar.event.SWTEditableTextAction;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;

/**
 * Constants specific to SWT GUITAR.
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 */
public class SWTConstants {

	// ------------------------------
	// Ignored components
	// ------------------------------

	public static final String RESOURCE_DIR = "resources";
	public static final String CONFIG_DIR = RESOURCE_DIR + File.separator + "config";

	/**
	 * The SWT events SWTGuitar is interested in. This includes most SWT events,
	 * with the notable exception of {@link SWT#Dispose}.
	 */
	public static final int[] SWT_EVENT_LIST = { SWT.KeyDown, SWT.KeyUp,
			SWT.MouseDown, SWT.MouseUp, SWT.MouseMove, SWT.MouseEnter,
			SWT.MouseExit, SWT.MouseDoubleClick, SWT.Paint, SWT.Move,
			SWT.Resize, /*SWT.Dispose,*/ SWT.Selection, SWT.DefaultSelection,
			SWT.FocusIn, SWT.FocusOut, SWT.Expand, SWT.Collapse, SWT.Iconify,
			SWT.Deiconify, SWT.Close, SWT.Show, SWT.Hide, SWT.Modify,
			SWT.Verify, SWT.Activate, SWT.Deactivate, SWT.Help, SWT.DragDetect,
			SWT.Arm, SWT.Traverse, SWT.MouseHover, SWT.HardKeyDown,
			SWT.HardKeyUp, SWT.MenuDetect };

	// public static String IGNORED_DIR_DEFAULT = RESOURCE_DIR +
	// File.separator+"data" + File.separator
	// + "ignore" + File.separator;

	// String JABREF_IGNORED_DIR = "data" + File.separator + "ignore"
	// + File.separator + "jabref" + File.separator;

	// public static String IGNORED_DIR = IGNORED_DIR_DEFAULT;

	// public static String IGNORED_WIDGET_FILE = "ignore_widget.ign";

	// public static String IGNORED_WINDOW_FILE = "ignore_window.ign";

	public static final String TERMINAL_WIDGET_FILE = "terminal_widget.ign";

	// public static Logger logger=Logger.getLogger("Default.log");
	//
	// public static String IGNORED_TAB_FILE = "ignore_tab.ign";
	//
	// public static String IGNORED_TREE_NODE_FILE = "ignore_tree_node.ign";

	// public static List<String> sIgnoreWidgetList = Util.getListFromFile(
	// IGNORED_DIR + IGNORED_WIDGET_FILE, false);
	//
	// public static List<String> sIgnoreWindowList = Util.getListFromFile(
	// IGNORED_DIR + IGNORED_WINDOW_FILE, false);

	// public static List<String> sTerminalWidgetList = Util.getListFromFile(
	// CONFIG_DIR + File.separator + TERMINAL_WIDGET_FILE, true);

	// public static List<GSignature> sTerminalWidgetPaterns = new
	// LinkedList<GSignature>();

	// public static List<ComponentType> sTerminalWidgetPaterns = new
	// LinkedList<ComponentType>();

	public static final List<AttributesTypeWrapper> sTerminalWidgetSignature = new LinkedList<AttributesTypeWrapper>();
	public static final List<String> sIgnoredWins = new ArrayList<String>();

	// public static List<String> sIgnoreTabList = Util.getListFromFile(
	// IGNORED_DIR + IGNORED_TAB_FILE, false);
	//
	// public static List<String> sIgnoreTreeNodeList = Util.getListFromFile(
	// IGNORED_DIR + IGNORED_TREE_NODE_FILE, false);

	// public static String GUI_DIR = "data" + File.separator + "gui"
	// + File.separator;

	// ------------------------------
	// GUI Properties of interest, should be in lower case
	// ------------------------------

	/**
	 * List of interested GUI properties
	 */
	public static final List<String> GUI_PROPERTIES_LIST = Arrays.asList("opaque", 
			"height", "width", /*"foreground", "background",*/ "visible", // foreground and background are platform-specific
			"tooltip", /*"font",*/ "accelerator", "enabled", "editable", // there's no platform-independent features of Fonts
			"focusable", "selected", "text");

	public static final List<String> WINDOW_PROPERTIES_LIST = Arrays.asList("layout", "x",
			"y", "height", "width", "opaque", "visible", "alwaysOnTop",
			"defaultLookAndFeelDecorated", /*"font",*/ /*"foreground",*/ "insets", 
			"resizable", /*"background",*/ "colorModel", "iconImage", "locale");

	/**
	 * List of properties used to identify a widget on the GUI
	 */
	public static final List<String> ID_PROPERTIES = Arrays.asList("Title", "Class",
			"Icon", "Index");

	public static final List<Class<? extends GEvent>> DEFAULT_SUPPORTED_EVENTS = new ArrayList<Class<? extends GEvent>>();
	static {
		DEFAULT_SUPPORTED_EVENTS.add(SWTAction.class);
		DEFAULT_SUPPORTED_EVENTS.add(SWTEditableTextAction.class);
	}
	
	/**
	 * JFC specific tags
	 * 
	 */

	public static final String TITLE_TAG = "Title";
	public static final String ICON_TAG = "Icon";
	public static final String INDEX_TAG = "Index";

	// ------------------------------
	// GUITAR LOG
	public static final String LOG4J_PROPERTIES_FILE = // CONFIG_DIR
	// + File.separator +
	"log4j.properties"; // This name needs to be a command-line parameter

	// public static final String LOG4J_PROPERTIES_XMLFILE = File.separator
	// +RESOURCE_DIR
	// + File.separator + "log4j.properties.xml";

	
	// ------------------------------
	// Runtime constants
	// ------------------------------

	// public static int DELAY = 1000;
}
