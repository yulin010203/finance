package com.finance.listener;

import com.finance.core.Bar;

/**
 * K线监听器
 * 
 * @author YuLin Nov 12, 2015
 */
public interface BarListener {

	/**
	 * @param bar
	 */
	public void onBar(Bar bar);
	
	/**
	 * @param bar
	 */
	public void onBarInside(Bar bar);
}
