package com.finance.events;

import org.eclipse.swt.internal.SWTEventListener;

/**
 * 放大缩小监听器
 * 
 * @author Chen Lin 2015-10-30
 */
public interface ZoomListener extends SWTEventListener {

	/**
	 * 放大
	 * 
	 * @param e
	 */
	public void zoomIn(ZoomEvent e);

	/**
	 * 缩小
	 * 
	 * @param e
	 */
	public void zoomOut(ZoomEvent e);
}
