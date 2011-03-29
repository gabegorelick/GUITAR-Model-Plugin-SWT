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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.umd.cs.guitar.exception.ApplicationConnectException;
import edu.umd.cs.guitar.util.GUITARLog;

/**
 * Implementation of {@link GApplication} for SWT
 * 
 * @see GApplication
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 */
public class SWTApplication extends GApplication {

	private static final String[] URL_PREFIX = { "file:", "jar:", "http:" };
	
	private Display guiDisplay;
	private Thread appThread;

	private Method mainMethod;
	private String[] argsToApp;
	
	private Class<?> cClass;
	private int initialDelay; 
	
	/**
	 * @param sClassName
	 * @param sURLs
	 * @throws ClassNotFoundException
	 * @throws MalformedURLException
	 */
	public SWTApplication(String sClassName, String[] sURLs, Thread appThread)
			throws ClassNotFoundException, MalformedURLException {
		super();

		this.appThread = appThread;
		guiDisplay = Display.findDisplay(appThread);
		initialDelay = 0;
		
		Set<URL> lURLs = new HashSet<URL>();

		// System URLs
		URLClassLoader sysLoader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		URL urls[] = sysLoader.getURLs();
		for (int i = 0; i < urls.length; i++) {
			lURLs.add(urls[i]);
		}

		// Additional URLs passed by arguments
		for (String sURL : sURLs) {
			for (String pref : URL_PREFIX) {
				if (sURL.startsWith(pref)) {

					URL appURL = new URL(sURL);
					lURLs.add(appURL);

					// GUITARLog.log.debug("GOT Application URL!!!!");
					// GUITARLog.log.debug("Original: " + sURL);
					// GUITARLog.log.debug("Converted: " + appURL.getPath());

					break;
				}
			}
		}

		URL[] arrayURLs = (lURLs.toArray(new URL[lURLs.size()]));
		// --------------
		GUITARLog.log.debug("=============================");
		GUITARLog.log.debug("Application URLs: ");
		GUITARLog.log.debug("-----------------------------");
		for (URL url : arrayURLs) {
			GUITARLog.log.debug("\t" + url.getPath());
		}
		GUITARLog.log.debug("");

		// ---------------

		URLClassLoader loader = new URLClassLoader(arrayURLs);
		this.cClass = Class.forName(sClassName, true, loader);
		// this.cClass = Class.forName(sClassName);
		
		try {
			this.mainMethod = cClass.getMethod("main", new Class[] { String[].class });
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.util.Application#start()
	 */
	@Override
	public void connect() throws ApplicationConnectException {
		String[] args = new String[0];
		connect(args);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.util.GApplication#start(java.lang.String[])
	 */
	@Override
	public void connect(final String[] args) throws ApplicationConnectException {
		this.argsToApp = args;
		
		GUITARLog.log.debug("=============================");
		GUITARLog.log.debug("Application Parameters: ");
		GUITARLog.log.debug("-----------------------------");
		for (int i = 0; i < args.length; i++) {
			GUITARLog.log.debug("\t" + args[i]);
		}
		GUITARLog.log.debug("");

		try {
			// sleep because user said so
			Thread.sleep(initialDelay);
			
			// sleep because we have to
			int ms = 2000;
			System.out.println("Waiting for GUI to initialize for: " + ms + "ms");
			Thread.sleep(ms); // TODO wait for event from Display instead of sleeping
		} catch (InterruptedException e) {
			GUITARLog.log.error(e);
		}
	}

	/**
	 * Start the application under test. This method simply invokes the 
	 * application's main method with the arguments specified in the 
	 * configuration.
	 */
	public void startGUI() {
		try {
			mainMethod.invoke(null, new Object[] { argsToApp });
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GApplication#getAllWindow()
	 */
	@Override
	public Set<GWindow> getAllWindow() {
		Shell[] windows = Display.getDefault().getShells(); // TODO make safe

		Set<GWindow> retWindows = new HashSet<GWindow>();

		for (Shell aWindow : windows) {
			GWindow gWindow = new SWTWindow(aWindow);
			if (gWindow.isValid())
				retWindows.add(gWindow);
			Set<GWindow> lOwnedWins = getAllOwnedWindow(aWindow);

			for (GWindow aOwnedWins : lOwnedWins) {
				if (aOwnedWins.isValid())
					retWindows.add(aOwnedWins);
			}
		}

		return retWindows;
	}

	// TODO make this work
	private Set<GWindow> getAllOwnedWindow(Shell parent) {
		Set<GWindow> retWindows = new HashSet<GWindow>();
		// Shell[] lOwnedWins = parent.getOwnedWindows();
		// for (Shell aOwnedWin : lOwnedWins) {
		// retWindows.add(new SWTWindow(aOwnedWin));
		// Set<GWindow> lOwnedWinChildren = getAllOwnedWindow(aOwnedWin);
		//
		// retWindows.addAll(lOwnedWinChildren);
		// }
		return retWindows;
	}

	public Thread getAppThread() {
		return appThread;
	}
	
	public Display getDisplay() {
		return guiDisplay;
	}
	
	public int getInitialDelay() {
		return initialDelay;
	}
	
	public void setInitialDelay(int delay) {
		initialDelay = delay;
	}

}
