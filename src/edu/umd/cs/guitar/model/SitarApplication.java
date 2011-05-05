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
import java.util.concurrent.atomic.AtomicReference;

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
public class SitarApplication extends GApplication {

	/**
	 * Default maximum time (in milliseconds) to wait for the application under
	 * test to start. This can be overridden with {@link #setTimeout(int)}.
	 */
	public static final int DEFAULT_TIMEOUT = 5000;
	
	private String mainClassName;
	private Thread guiThread;
	
	private Display guiDisplay;
	private Method mainMethod;
	private String[] argsToApp;
	
	private int timeout;
	private int initialWait;
	private Set<URL> urls;
	
	/**
	 * Constructor.
	 * 
	 * @param mainClassName
	 *            name of class that contains the GUI's <code>main</code> method
	 * @param guiThread
	 *            thread the GUI is running on (usually the <code>main</code>
	 *            thread)
	 */
	public SitarApplication(String mainClassName, Thread guiThread) {
		this.mainClassName = mainClassName;
		this.guiThread = guiThread;
		this.timeout = DEFAULT_TIMEOUT;
		this.initialWait = 0;
		
		argsToApp = new String[0];
		
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
	 * Get the URLs used as the GUI's classpath.
	 * 
	 * @return the URLs that constitute the GUI's classpath
	 * @see #addURL(URL)
	 * @see #addURLs(URL[])
	 * @see #startGUI()
	 */
	public URL[] getURLs() {
		return urls.toArray(new URL[urls.size()]);
	}

	/**
	 * <p>
	 * Wait for the GUI to start. In other GUITAR plugins, this method
	 * starts the GUI application. But in SWT GUITAR, the application has
	 * already been started by the time this method is called. Thus, this method
	 * only waits for the application to be ready for ripping.
	 * </p>
	 * <p>
	 * Ideally, this method would wait until it is notified that the application
	 * under test has started. But starting the application blocks, so no
	 * notification can be sent. Thus, this method has to simply
	 * {@link Thread#sleep(long) sleep} until the application is ready.
	 * </p>
	 * 
	 * @throws ApplicationConnectException
	 *             thrown if application did not start within the
	 *             {@link #setTimeout(int) max wait time} or if sleeping failed
	 */
	@Override
	public void connect() {
		try {

			// sleep because user said so
			if (initialWait > 0) {
				Thread.sleep(initialWait);
			}

			// sleep because we have to
			int sleepIncrement = 100;
			int totalSleepTime = 0;

			while ((guiDisplay = Display.findDisplay(guiThread)) == null) {				
				GUITARLog.log.debug("GUI not ready yet");
				
				// wait forever if timeout == 0
				if (timeout != 0 && totalSleepTime > timeout) {
					GUITARLog.log.error("Timed out waiting for GUI to start");
					throw new ApplicationConnectException();
				}
				GUITARLog.log.debug("Waiting for GUI to initialize for: "
						+ sleepIncrement + "ms");
				Thread.sleep(sleepIncrement);
				totalSleepTime += sleepIncrement;
			}
			
			// make sure display is not only non-null, but ready
			Thread.sleep(sleepIncrement);

			// force update of display, just to be sure
			// also good since syncExec waits for "reasonable opportunity"
			guiDisplay.syncExec(new Runnable() {
				@Override
				public void run() {
					guiDisplay.update();
					GUITARLog.log.debug("Display ready");
				}
			});
		} catch (InterruptedException e) {
			// doesn't support causes :(
			GUITARLog.log.error("connect encountered InterruptedException, "
					+ "throwing ApplicationConnectException");
			throw new ApplicationConnectException();
		}
	}

	/**
	 * Wait for SWT application to start. This method behaves identically to 
	 * {@link #connect()}.
	 *  
	 * @param args
	 *            usually the arguments to the GUI main class, but since the GUI
	 *            is already started by the time this method is called, this
	 *            argument is ignored
	 * 
	 * @throws ApplicationConnectException
	 *             thrown if application did not start within the
	 *             {@link #setTimeout(int) max wait time} or if sleeping failed
	 * 
	 * @deprecated Use {@link #setArgsToApp(String[])} with {@link #connect()}
	 *             instead
	 */
	@Override
	@Deprecated
	public void connect(String[] args) throws ApplicationConnectException {
		connect();
	}
	
	/**
	 * Thrown to indicate the application under test has attempted to exit the
	 * JVM, e.g. with a call to {@link System#exit(int)}.
	 */
	public static class ExitException extends SecurityException {
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Start the application under test. This method simply invokes the
	 * application's main method with the arguments specified in the
	 * configuration.
	 * 
	 * @throws SitarApplicationStartException
	 *             if there was any exception encountered while starting the GUI
	 */
	public void startGUI() throws SitarApplicationStartException {
		GUITARLog.log.debug("=============================");
		GUITARLog.log.debug("Application URLs: ");
		GUITARLog.log.debug("-----------------------------");
				
		URL[] aURLs = getURLs();
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
		} catch (ClassNotFoundException e) {
			// all these catch blocks make me want Java 7
			throw new SitarApplicationStartException(e);
		} catch (IllegalArgumentException e) {
			throw new SitarApplicationStartException(e);
		} catch (SecurityException e) {
			throw new SitarApplicationStartException(e);
		} catch (NoSuchMethodException e) {
			throw new SitarApplicationStartException(e);
		} 
		
		try {
			mainMethod.invoke(null, new Object[] { argsToApp });
		} catch (IllegalArgumentException e) {
			throw new SitarApplicationStartException(e);
		} catch (IllegalAccessException e) {
			throw new SitarApplicationStartException(e);
		} catch (InvocationTargetException e) {
			if (!(e.getCause().getClass() == ExitException.class)
					&& !(e.getTargetException().getClass() == ExitException.class)) {
				throw new SitarApplicationStartException(e);
			}
		}
	}
	
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<GWindow> getAllWindow() { // TODO deprecate this and fix spelling (getAllWindows)
		final AtomicReference<Shell[]> shells = new AtomicReference<Shell[]>();
				
		guiDisplay.syncExec(new Runnable() {
			@Override
			public void run() {
				// returns all windows, not just ones that have display as parent
				shells.set(guiDisplay.getShells());
			}
		});
		
		Set<GWindow> retWindows = new HashSet<GWindow>();

		for (Shell aWindow : shells.get()) {
			GWindow gWindow = new SitarWindow(aWindow);
			if (gWindow.isValid()) {
				retWindows.add(gWindow);
			}
		}

		return retWindows;
	}

	/**
	 * Get the display the GUI runs on.
	 * 
	 * @return the display the GUI is on
	 */
	public Display getDisplay() {
		return guiDisplay;
	}

	/**
	 * Get the arguments passed to the application's {@code main} method.
	 * 
	 * @return arguments passed to GUI
	 * 
	 * @see #setArgsToApp(String[])
	 */
	public String[] getArgsToApp() {
		return argsToApp;
	}

	/**
	 * Set the arguments passed to the application's {@code main} method.
	 * 
	 * @param args
	 *            the arguments to be passed to the GUI
	 * 
	 * @see #getArgsToApp()
	 */
	public void setArgsToApp(String[] args) {
		argsToApp = args;
	}

	/**
	 * Get how long to wait for the GUI to start.
	 * 
	 * @return maximum time to wait (in milliseconds)
	 * 
	 * @see #connect()
	 * @see #setTimeout(int)
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Set the maximum amount of time to wait until the GUI is ready. By
	 * default, this is {@link #DEFAULT_TIMEOUT}. Specifying a timeout of 0 will
	 * cause this class to wait forever until the GUI is ready.
	 * 
	 * @param timeout
	 *            maximum time to wait in milliseconds
	 * 
	 * @throws IllegalArgumentException
	 *             thrown if timeout is negative
	 *             
	 * @see #getTimeout()
	 */
	public void setTimeout(int timeout) {
		if (timeout < 0) {
			throw new IllegalArgumentException("timeout cannot be negative");
		}
		this.timeout = timeout;
	}

	/**
	 * Get the amount of time to wait before checking if the GUI has started.
	 * 
	 * @return amount of time to wait (in milliseconds)
	 * 
	 * @see #setInitialWait(int)
	 */
	public int getInitialWait() {
		return initialWait;
	}

	/**
	 * Set the amount of time to wait before we even attempt to connect to the
	 * GUI. This is usually not needed, as the intelligent waiting in
	 * {@link #connect()} is usually good enough. However, in extreme cases it
	 * may appear that the GUI is ready when in reality it is not. In these
	 * cases, use this method to force <code>connect</code> to wait for the GUI
	 * even if it thinks the GUI is ready.
	 * 
	 * @param initialWait
	 *            time to wait in milliseconds, values <= 0 mean not to wait
	 */
	public void setInitialWait(int initialWait) {
		this.initialWait = initialWait;
	}
	
}
