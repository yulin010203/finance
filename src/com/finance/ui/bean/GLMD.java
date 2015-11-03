package com.finance.ui.bean;

import java.util.ArrayList;
import java.util.List;

import com.finance.core.MD;
import com.finance.core.Pointf;
import com.jogamp.opengl.GL2;

/**
 * @author 陈霖 2015-9-19
 */
public class GLMD {
	/**
	 * 
	 */
	private GL2 gl2;
	/**
	 * 行情数据 
	 */
	private List<MD> mds = new ArrayList<MD>();
	/**
	 * 对应点位置
	 */
	private List<Pointf> points = new ArrayList<Pointf>();
	/**
	 * 对应行情位置
	 */
	private int index;
	/**
	 * plot中行情开始位置
	 */
	private int head;
	/**
	 * plot中行情结束位置
	 */
	private int tail;
	/**
	 * plot中行情跨度
	 */
	private int del;

	/**
	 * @param gl2
	 */
	public GLMD(GL2 gl2) {
		this.gl2 = gl2;
	}
}
