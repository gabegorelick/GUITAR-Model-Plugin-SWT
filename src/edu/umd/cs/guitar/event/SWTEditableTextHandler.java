package edu.umd.cs.guitar.event;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleEditableText;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.widgets.Text;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.SWTWidget;
import edu.umd.cs.guitar.util.Debugger;
import edu.umd.cs.guitar.util.GUITARLog;

public class SWTEditableTextHandler extends SWTEventHandler{

	public SWTEditableTextHandler() {
	}
	
	private static String GUITAR_DEFAULT_TEXT = "GUITAR DEFAULT TEXT: "
		+ "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"
		+ "Â¡Â¢Â£Â¤Â¥Â¦Â§Â¨Â©ÂªÂ«Â¬Â­Â®Â¯Â°Â±Â²Â³Â´ÂµÂ¶Â·Â¸Â¹ÂºÂ»Â¼Â½Â¾Â¿Ã€Ã�Ã‚ÃƒÃ„Ã…Ã†Ã‡ÃˆÃ‰ÃŠÃ‹ÃŒÃ�ÃŽÃ�Ã�Ã‘Ã’Ã“Ã”Ã•Ã–Ã—Ã˜Ã™ÃšÃ›ÃœÃ�ÃžÃŸÃ Ã¡Ã¢Ã£Ã¤Ã¥Ã¦Ã§Ã¨Ã©ÃªÃ«Ã¬Ã­Ã®Ã¯Ã°Ã±Ã²Ã³Ã´ÃµÃ¶Ã·Ã¸Ã¹ÃºÃ»Ã¼Ã½Ã¾"
		+ "Ã¿Ä€Ä�Ä‚ÄƒÄ„Ä…Ä†Ä‡ÄˆÄ‰ÄŠÄ‹ÄŒÄ�ÄŽÄ�Ä�Ä‘Ä’Ä“Ä”Ä•Ä–Ä—Ä˜Ä™ÄšÄ›ÄœÄ�ÄžÄŸÄ Ä¡Ä¢Ä£Ä¤Ä¥Ä¦Ä§Ä¨Ä©ÄªÄ«Ä¬Ä­Ä®Ä¯Ä°Ä±Ä²Ä³Ä´ÄµÄ¶Ä·Ä¸Ä¹ÄºÄ»Ä¼Ä½Ä¾Ä¿Å€Å�Å‚ÅƒÅ„Å…Å†Å‡ÅˆÅ‰ÅŠÅ‹ÅŒÅ�ÅŽÅ�Å�Å‘Å’Å“Å”Å•Å–Å—Å˜Å™ÅšÅ›ÅœÅ�ÅžÅŸÅ Å¡Å¢Å£Å¤Å¥Å¦Å§Å¨Å©ÅªÅ«Å¬Å­Å®Å¯Å°Å±Å²Å³Å´ÅµÅ¶Å·Å¸Å¹ÅºÅ»Å¼Å½Å¾Å¿Æ€Æ�Æ‚ÆƒÆ„Æ…Æ†Æ‡ÆˆÆ‰ÆŠÆ‹ÆŒÆ�ÆŽÆ�Æ�Æ‘Æ’Æ“Æ”Æ•Æ–Æ—Æ˜Æ™ÆšÆ›ÆœÆ�ÆžÆŸÆ Æ¡Æ¢Æ£Æ¤Æ¥Æ¦Æ§Æ¨Æ©ÆªÆ«Æ¬Æ­Æ®Æ¯Æ°Æ±Æ²Æ³Æ´ÆµÆ¶Æ·Æ¸Æ¹ÆºÆ»Æ¼Æ½Æ¾Æ¿Ç€Ç�Ç‚Çƒ"
		+ "Ç�ÇŽÇ�Ç�Ç‘Ç’Ç“Ç”Ç•Ç–Ç—Ç˜Ç™ÇšÇ›ÇœÇ�ÇžÇŸÇ Ç¡Ç¢Ç£Ç¤Ç¥Ç¦Ç§Ç¨Ç©ÇªÇ«Ç¬Ç­Ç®Ç¯Ç°"
		+ "Ç¾Ç¿È€È�È‚ÈƒÈ„È…È†È‡ÈˆÈ‰ÈŠÈ‹ÈŒÈ�ÈŽÈ�È�È‘È’È“È”È•È–È—È˜È™ÈšÈ›ÈœÈ�ÈžÈŸÈ È¡È¢È£È¤È¥È¦È§È¨È©ÈªÈ«È¬È­È®È¯È°È±È²È³È´ÈµÈ¶È·È¸È¹ÈºÈ»È¼È½È¾È¿É€É�É‚ÉƒÉ„É…É†É‡ÉˆÉ‰ÉŠÉ‹ÉŒÉ�ÉŽÉ�É�É‘É’É“É”É•É–É—É˜É™ÉšÉ›ÉœÉ�ÉžÉŸÉ É¡É¢É£É¤É¥É¦É§É¨É©ÉªÉ«É¬É­É®É¯É°É±É²É³É´ÉµÉ¶É·É¸É¹ÉºÉ»É¼É½É¾É¿Ê€Ê�Ê‚ÊƒÊ„Ê…Ê†Ê‡ÊˆÊ‰ÊŠÊ‹ÊŒÊ�ÊŽÊ�Ê�Ê‘Ê’Ê“Ê”Ê•Ê–Ê—Ê˜Ê™ÊšÊ›ÊœÊ�ÊžÊŸÊ Ê¡Ê¢Ê£Ê¤Ê¥Ê¦Ê§Ê¨Ê©ÊªÊ«Ê¬Ê­Ê®Ê¯Ê°Ê±Ê²Ê³Ê´ÊµÊ¶Ê·Ê¸Ê¹";
	
	@Override
	public boolean isSupportedBy(GComponent gComponent) {
		if (!(gComponent instanceof SWTWidget))
			return false;
		SWTWidget jComponent = (SWTWidget) gComponent;
		Widget component = jComponent.getWidget();
		if (component instanceof Text)
			return true;
		else
			return false;
		/*AccessibleContext aContext = component.getAccessibleContext();
		
		if (aContext == null)
			return false;

		Object event;

		// Text
		event = aContext.getAccessibleEditableText();
		if (event != null) {
			return true;
		}
		return false*/
	}

	@Override
	protected void performImpl(GComponent gComponent,
			Hashtable<String, List<String>> optionalData) {
		List<String> args = new ArrayList<String>();
		args.add(GUITAR_DEFAULT_TEXT);
		performImpl(gComponent, args,optionalData);
		
	}

	@Override
	protected void performImpl(GComponent gComponent, Object parameters,
			Hashtable<String, List<String>> optionalData) {
		
		if (gComponent == null) {
			return;
		}

		if (parameters instanceof List) {

			List<String> lParameter = (List<String>) parameters;
			String sInputText;
			if (lParameter == null) {
				sInputText = GUITAR_DEFAULT_TEXT;
			} else
				sInputText = (lParameter.size() != 0) ? lParameter.get(0)
						: GUITAR_DEFAULT_TEXT;

			// Accessible aComponent = getAccessible(gComponent);

			// AccessibleContext aContext = aComponent.getAccessibleContext();
			Widget component = getComponent(gComponent);
			if (component != null && component instanceof Text) {
				Text textWidget = (Text) component;
				textWidget.setText(sInputText);
			}
			/*AccessibleContext aContext = component.getAccessibleContext();
			AccessibleEditableText aTextEvent = aContext.getAccessibleEditableText();

			if (aTextEvent == null) {
				System.err.println(this.getClass().getName()
						+ " doesn't support");
				return;
			}
			try {
				aTextEvent.setTextContents(sInputText);
			} catch (Exception e) {
				try {
					Method setText = component.getClass().getMethod("setText",
							String.class);
					setText.invoke(component, sInputText);

					Debugger.pause(GUITAR_DEFAULT_TEXT);

				} catch (SecurityException e1) {
					// TODO Auto-generated catch block
					GUITARLog.log.error(e1);
				} catch (NoSuchMethodException e1) {
					GUITARLog.log.error(e1);
				} catch (IllegalArgumentException e1) {
					// TODO Auto-generated catch block
					GUITARLog.log.error(e1);
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					GUITARLog.log.error(e1);
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					GUITARLog.log.error(e1);
				}
			}*/

		}
		
	}

}
