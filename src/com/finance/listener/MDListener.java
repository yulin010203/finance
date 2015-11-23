package com.finance.listener;

import com.finance.core.MD;

/**
 * MD行情监听器
 * @author YuLin Nov 12, 2015
 */
public interface MDListener {

	/**
	 * @param md
	 */
	public void onMD(MD md);
}
