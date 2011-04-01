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

	private String mainClassName;
	private Thread appThread;
	
	private Display guiDisplay;
	private Method mainMethod;
	private String[] argsToApp;
	
	private int initialDelay;
	private Set<URL> urls;
	
	public SWTApplication(String mainClassName, Thread appThread) {
		this.mainClassName = mainClassName;
		this.appThread = appThread;
		
		urls = new HashSet<URL>();

		// System URLs
		URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		for (URL u : sysLoader.getURLs()) {
			urls.add(u);
		}
	}

	/**
	 * Add additional URLs to be used when loading the application. The URLs are
	 * added to the <code>URLClassLoader</code> used to load the application
	 * under test.
	 * 
	 * @param urls
	 *            URLs to be added
	 *            
	 * @see #addURL(URL)
	 */
	public void addURLs(URL[] urls) {
		for (URL u : urls) {
			this.urls.add(u);
		}
	}
	
	/**
	 * Add an additional URL to be used when loading the application. The URL is
	 * added to the <code>URLClassLoader</code> used to load the application
	 * under test.
	 * 
	 * @param url
	 *            URL to be added
	 * 
	 * @see #addURLs(URL[])
	 */
	public void addURL(URL url) {
		this.urls.add(url);
	}
		
	/**
	 * Wait for SWT application to start. This method behaves identically to 
	 * {@link #connect(String[])}.
	 */
	@Override
	public void connect() {
		connect(null);
	}
	
	/**
	 * Wait for SWT application to start. In other GUITAR plugins, this method
	 * starts the GUI application. But in SWT GUITAR, the application has 
	 * already been started by the time this method is called. Thus, this 
	 * method only waits for the application to be ready for ripping. 
	 * 
	 * @param args - usually the arguments to the GUI main class, but since
	 * the GUI is already started by the time this method is called, this
	 * argument is ignored
	 */
	@Override
	public void connect(String[] args) throws ApplicationConnectException {
		try {
			// sleep because user said so
			Thread.sleep(initialDelay);
			
			// sleep because we have to
			int ms = 2000;
			System.out.println("Waiting for GUI to initialize for: " + ms + "ms");
			Thread.sleep(ms); // TODO wait for event from Display instead of sleeping
			onGuiStarted();
		} catch (InterruptedException e) {
			// doesn't support causes :(
			throw new ApplicationConnectException();
		}
	}
	
	/**
	 * This method is called once the GUI has been started. Subclasses should
	 * still call this method when overriding, as this method does needed set
	 * up.
	 */
	protected void onGuiStarted() {
		// set display now that there is one
		guiDisplay = Display.findDisplay(appThread);
	}

	/**
	 * Start the application under test. This method simply invokes the 
	 * application's main method with the arguments specified in the 
	 * configuration.
	 */
	public void startGUI() throws SWTApplicationStartException {
		GUITARLog.log.debug("=============================");
		GUITARLog.log.debug("Application URLs: ");
		GUITARLog.log.debug("-----------------------------");
				
		URL[] aURLs = urls.toArray(new URL[urls.size()]);
		for (URL url : aURLs) {
			GUITARLog.log.debug("\t" + url.getPath());
		}

		GUITARLog.log.debug("=============================");
		GUITARLog.log.debug("Application Parameters: ");
		GUITARLog.log.debug("-----------------------------");
		for (int i = 0; i < argsToApp.length; i++) {
			GUITARLog.log.debug("\t" + argsToApp[i]);
		}
		
		URLClassLoader loader = new URLClassLoader(aURLs);
		try {
			Class<?> clazz = Class.forName(mainClassName, true, loader);
			mainMethod = clazz.getMethod("main", new Class[] { String[].class });
			mainMethod.invoke(null, new Object[] { argsToApp });
		} catch (ClassNotFoundException e) {
			// all these catch blocks make me want Java 7
			throw new SWTApplicationStartException(e);
		} catch (IllegalArgumentException e) {
			throw new SWTApplicationStartException(e);
		} catch (IllegalAccessException e) {
			throw new SWTApplicationStartException(e);
		} catch (InvocationTargetException e) {
			throw new SWTApplicationStartException(e);
		} catch (SecurityException e) {
			throw new SWTApplicationStartException(e);
		} catch (NoSuchMethodException e) {
			throw new SWTApplicationStartException(e);
		}
	}
	
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.guitar.model.GApplication#getAllWindow()
	 */
	@Override
	public Set<GWindow> getAllWindow() {
		final Shell[][] windows = new Shell[1][];
		
		guiDisplay.syncExec(new Runnable() {
			@Override
			public void run() {
				// returns all windows, not just ones that have display as parent
				windows[0] = guiDisplay.getShells();
			}
		});
		
		Set<GWindow> retWindows = new HashSet<GWindow>();

		for (Shell aWindow : windows[0]) {
			GWindow gWindow = new SWTWindow(aWindow);
			if (gWindow.isValid()) {
				retWindows.add(gWindow);
			}
		}

		return retWindows;
	}

	public Thread getAppThread() {
		return appThread;
	}
	
	public Display getDisplay() {
		return guiDisplay;
	}
	
	public String[] getArgsToApp() {
		return argsToApp;
	}
	
	public void setArgsToApp(String[] args) {
		argsToApp = args;
	}
	
	public int getInitialDelay() {
		return initialDelay;
	}
	
	public void setInitialDelay(int delay) {
		initialDelay = delay;
	}

}
