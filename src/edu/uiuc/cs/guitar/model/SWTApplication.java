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
package edu.uiuc.cs.guitar.model;

import java.lang.reflect.Field;
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
import edu.umd.cs.guitar.model.GApplication;
import edu.umd.cs.guitar.model.GWindow;

/**
 * Implementation of {@link GApplication} for SWT
 * 
 * @see GApplication
 * 
 * @author <a href="mailto:mattkse@gmail.com"> Matt Kirn </a>
 * @author <a href="mailto:atloeb@gmail.com"> Alex Loeb </a>
 */
public class SWTApplication extends GApplication {

	private Display guiDisplay;

	private Field threadField = null;

	private Thread t;

	private Class<?> cClass;
	String sClassName;
	int iInitialDelay;

	public Display getDisplay() {
		return guiDisplay;
	}

	public Field getThreadField() {
		return threadField;
	}

	public Thread getThread() {
		return t;
	}

	/**
	 * @param sClassName
	 * @param iInitalDelay
	 * @throws ClassNotFoundException
	 */
	@Deprecated
	public SWTApplication(String sClassName, int iInitialDelay)
			throws ClassNotFoundException {
		super();
		this.cClass = Class.forName(sClassName);
		this.sClassName = sClassName;
		this.iInitialDelay = iInitialDelay;
	}

	final String[] URL_PREFIX = { "file:", "jar:", "http:" };

	/**
	 * @param sClassName
	 * @param sURLs
	 * @throws ClassNotFoundException
	 * @throws MalformedURLException
	 */
	public SWTApplication(String sClassName, String[] sURLs)
			throws ClassNotFoundException, MalformedURLException {
		super();

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
		this.sClassName = sClassName;
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

		GUITARLog.log.debug("=============================");
		GUITARLog.log.debug("Application Parameters: ");
		GUITARLog.log.debug("-----------------------------");
		for (int i = 0; i < args.length; i++)
			GUITARLog.log.debug("\t" + args[i]);
		GUITARLog.log.debug("");

		final Method method;

		try {
			method = cClass.getMethod("main", new Class[] { String[].class });

			GUITARLog.log.debug("Main method FOUND!");

			if (method != null) {
				try {
					startGUIandSubvertOwnership(args, method);
				} catch (InterruptedException e) {
					System.out.println("interrupt after launch");
				}

				GUITARLog.log.debug("Main method INVOKED!");
			}

			else
				throw new ApplicationConnectException();

			// } catch (SecurityException e) {
			// // TODO Auto-generated catch block
		} catch (NoSuchMethodException e) {
			GUITARLog.log
					.debug("Coundn't find main method for the application");
			GUITARLog.log.error(e);
		}

		try {
			Thread.sleep(iInitialDelay);
		} catch (InterruptedException e) {
			GUITARLog.log.error(e);
		}
	}

	/**
	 * Start up the GUI, wait for the infinite loop, then subvert the ownership
	 * of the thread that controls the GUI so that we can access the GUI
	 * hierarchy without getting stuck in the infinite SWT readAndDispatch()
	 * loop.
	 * 
	 * @param args
	 *            args to the application
	 * @param method
	 *            main method of the GUI to be launched
	 * @throws InterruptedException
	 */
	private void startGUIandSubvertOwnership(final String[] args,
			final Method method) throws InterruptedException {

		// Launch the GUI in a separate thread so that we can still rip in the
		// main thread
		t = new Thread(new Runnable() {

			@Override
			public void run() {
				launchGUI();
			}

			private void launchGUI() {
				try {
					method.invoke(null, new Object[] { args });
					// The GUI thread will now block forever in the infinite
					// readAndDispatch() loop ... Control will never reach this
					// point until the GUI closes
				} catch (InvocationTargetException e) {
					// Control will get here because the GUI code will throw an
					// exception once we subvert the the GUI thread. Due to
					// this, we suggest ripping the structure first, then
					// rerunning the GUI without subverting the owner in order
					// to be able to send events while the application is
					// running
				} catch (IllegalAccessException e) {
					GUITARLog.log.error(e);
				} catch (IllegalArgumentException e) {
					GUITARLog.log.error(e);
				}
			}
		});

		t.start();
		subvertGUIThreadOwner();
	}

	void subvertGUIThreadOwner() {
		try {
			// Use this loop to wait until the entire GUI is invoked
			// Increase the timeout value if application doesn't fully
			// start prior to ripping
			int ms = 2000;
			System.out.println("Waiting for GUI to initialize for: " + ms
					+ "ms");
			Thread.sleep(ms);

			// Get a reference to the main Ripping thread
			Thread myThread = Thread.currentThread();

			// Get a reference to the GUI thread of the application under test
			guiDisplay = Display.findDisplay(t);

			// Find the internal field variable which indicates the thread that
			// owns the GUI
			threadField = Display.class.getDeclaredField("thread");
			// Subvert permissions checks
			threadField.setAccessible(true);
			// Override the owner of the GUI thread as the main Ripping thread
			threadField.set(guiDisplay, myThread);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
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
		Shell[] windows = Display.getDefault().getShells();

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

}
