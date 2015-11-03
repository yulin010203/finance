package com.finance.events;

/**
 * 放大缩小事件
 * 
 * @author Chen Lin 2015-10-30
 */
public class ZoomEvent {
	private static final long serialVersionUID = -7035070750564151512L;

	/**
	 * 起始位置
	 */
	public int head;
	/**
	 * 结束位置
	 */
	public int tail;

	/**
	 * @param e
	 */
	public ZoomEvent() {
	}

}
