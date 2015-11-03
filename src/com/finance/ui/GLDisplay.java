package com.finance.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.finance.core.Bar;
import com.finance.core.Bound;
import com.finance.core.Constants;
import com.finance.core.MD;
import com.finance.core.indicator.MA;
import com.finance.core.indicator.MACD;
import com.finance.events.ZoomEvent;
import com.finance.events.ZoomListener;
import com.finance.ui.bean.GLBar;
import com.finance.ui.bean.GLCJL;
import com.finance.util.MathUtil;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.swt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * @author 陈霖 2015-8-31
 */
/**
 * @author Chen Lin 2015-11-2
 */
public class GLDisplay implements KeyListener {

	/**
	 * 移动平均线指数
	 */
	public static final int[] MA_N = { 5, 10, 20, 40, 60 };
	/**
	 * 是否图区大小初始化
	 */
	private Bound barBound = new Bound();
	private Bound dealBound = new Bound();
	private Bound macdBound = new Bound();
	private Bound detailBound = new Bound();
	/**
	 * 锁
	 */
	private final ReentrantLock lock;
	/**
	 * 窗口
	 */
	private Shell shell;
	/**
	 * 画布
	 */
	private GLCanvas canvas;
	/**
	 * 刷新线程
	 */
	private FPSAnimator animator;
	/**
	 * 辅助监听器
	 */
	private MyListener listener = new MyListener();

	/**
	 * tick数据
	 */
	private List<MD> mds = new ArrayList<MD>();
	/**
	 * 完成的K线数据
	 */
	private List<Bar> bars = new ArrayList<Bar>();
	/**
	 * K线画图数据
	 */
	private List<GLBar> glbars = new ArrayList<GLBar>();
	/**
	 * MA移动平均线
	 */
	private Map<Integer, List<MA>> mas = new ConcurrentHashMap<Integer, List<MA>>();
	/**
	 * MA移动平均线颜色
	 */
	private Map<Integer, float[]> colors = new ConcurrentHashMap<Integer, float[]>();
	/**
	 * 成交量画图数据
	 */
	private List<GLCJL> glcjls = new ArrayList<GLCJL>();
	/**
	 * MACD指标
	 */
	private List<MACD> macds = new ArrayList<MACD>();
	/**
	 * 展示条数
	 */
	private int count = Constants.BAR_SHOW_NUM;
	/**
	 * plot中K线开始位置
	 */
	private int head;
	/**
	 * plot中K线结束位置
	 */
	private int tail;
	// /**
	// * 展示区域最高价
	// */
	// private double high;
	// /**
	// * 展示区域最低价
	// */
	// private double low;
	/**
	 * 图区内中间价位
	 */
	private double mid;
	/**
	 * 图区内价差
	 */
	private double del;
	/**
	 * 图区内成交量最大值
	 */
	private int deal;
	/**
	 * K线宽度(像素)
	 */
	private float span;

	/**
	 * @param display
	 */
	public GLDisplay(Display display) {
		this.shell = new Shell(display);
		this.shell.setLayout(new FillLayout(SWT.VERTICAL));
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		this.canvas = GLCanvas.create(shell, SWT.NONE, capabilities, null);
		addListener();
		this.animator = new FPSAnimator(canvas, Constants.SHOW_RATE);
		this.lock = new ReentrantLock();
	}

	/**
	 * add listener
	 */
	private void addListener() {
		canvas.addGLEventListener(listener);
		canvas.addKeyListener(this);
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				e.doit = false;
				stop();
			}
		});
	}

	/**
	 * @param listener
	 */
	public void addGLEventListener(final GLEventListener listener) {
		this.listener.addGLEventListener(listener);
	}

	/**
	 * @param listener
	 * @return GLEventListener
	 */
	public GLEventListener removeGLEventListener(final GLEventListener listener) {
		return this.listener.removeGLEventListener(listener);
	}

	/**
	 * @param listener
	 */
	public void addZoomListener(final ZoomListener listener) {
		this.listener.addZoomListener(listener);
	}

	/**
	 * @param listener
	 * @return ZoomListener
	 */
	public ZoomListener removeZoomListener(final ZoomListener listener) {
		return this.listener.removeZoomListener(listener);
	}

	/**
	 * @param listener
	 */
	public void addMouseListener(MouseListener listener) {
		canvas.addMouseListener(listener);
	}

	/**
	 * @param listener
	 */
	public void addMouseMoveListener(MouseMoveListener listener) {
		canvas.addMouseMoveListener(listener);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.keyCode) {
		case SWT.ARROW_UP:
			// 同时按下Ctrl键
			if (e.stateMask == SWT.CTRL) {

			} else {
				// 放大
				zoomIn(e);
			}
			break;
		case SWT.ARROW_DOWN:
			// 同时按下Ctrl键
			if (e.stateMask == SWT.CTRL) {

			} else {
				// 缩小
				zoomOut(e);
			}

			break;
		case SWT.ARROW_LEFT:
			// 同时按下Ctrl键
			if (e.stateMask == SWT.CTRL) {

			} else {

			}

			break;
		case SWT.ARROW_RIGHT:
			// 同时按下Ctrl键
			if (e.stateMask == SWT.CTRL) {

			} else {

			}

			break;
		case SWT.ESC:
			// 按Esc键退出
			stop();
			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	/**
	 * @param e
	 */
	private void zoomIn(KeyEvent e) {
		if (count == Constants.BAR_SHOW_NUM_MIN) {
			// 已经缩小到最小
			return;
		}
		int num = count / 2 + 1;
		if (num < Constants.BAR_SHOW_NUM_MIN) {
			count = Constants.BAR_SHOW_NUM_MIN;
		} else {
			count = num;
		}
		int mid = (head + tail) / 2;
		head = (head + mid) / 2;
		tail = head + count;
		// listener.zoomIn(new ZoomEvent(e.widget));
	}

	/**
	 * @param e
	 */
	private void zoomOut(KeyEvent e) {
		if (count >= bars.size()) {
			// 已经放大到最大
			return;
		}
		int num = count * 2;
		if (num >= bars.size()) {
			num = bars.size();
		}
		int h = head - count / 2;
		head = h < 0 ? 0 : h;
		int t = tail + count / 2;
		tail = t > num ? num : t;
		count = num;
		// listener.zoomOut(new ZoomEvent(e));
	}

	/**
	 * start
	 */
	public void start() {
		animator.start();
		shell.open();
	}

	/**
	 * stop
	 */
	public void stop() {
		animator.stop();
		canvas.dispose();
		shell.dispose();
		System.exit(0);
	}

	/**
	 * @param bar
	 */
	public void add(Bar bar) {
		// if (bar.isFinished()) {
		// bars.add(bar);
		// glbars.add(new GLBar(bar));
		// } else {
		// Bar last = bars.get(bars.size() - 1);
		// last.setClose(bar.getClose());
		// last.setHigh(bar.getHigh());
		// last.setLow(bar.getLow());
		// last.setEndTime(bar.getEndTime());
		//
		// }
		lock.lock();
		bars.add(bar);
		glbars.add(new GLBar(this, bar));
		glcjls.add(new GLCJL(this, bar.getDealVol(), bar.getVol()));
		head++;
		tail++;
		lock.unlock();
		refresh();
	}

	/**
	 * @param bars
	 */
	public void addAll(Collection<Bar> bars) {
		if (bars.isEmpty()) {
			return;
		}
		int size = bars.size();
		if (count > size) {
			count = size;
		}
		MACD last = null;
		int index = 0;
		double sum = 0;
		for (int n : MA_N) {
			List<MA> list = new ArrayList<MA>(bars.size());
			mas.put(n, list);
		}
		float[] white = { 1.0f, 1.0f, 1.0f };
		colors.put(5, white);
		float[] yellow = { 1.0f, 1.0f, 0.0f };
		colors.put(10, yellow);
		float[] purple = { 1.0f, 0.0f, 1.0f };
		colors.put(20, purple);
		float[] green = { 0.0f, 1.0f, 0.0f };
		colors.put(40, green);
		float[] gray = { 0.5f, 0.5f, 0.5f };
		colors.put(60, gray);
		for (Bar bar : bars) {
			this.bars.add(bar);
			glbars.add(new GLBar(this, bar));
			sum += bar.getClose();
			add(index, sum, bar.getClose());

			glcjls.add(new GLCJL(this, bar.getDealVol(), bar.getVol()));

			MACD macd = new MACD();
			macd.caculate(last, bar.getClose());
			macds.add(macd);
			last = macd;
			index++;
		}
		tail = size - 1;
		head = tail - count + 3;
	}

	/**
	 * @param i
	 * @param sum
	 * @param close 
	 */
	private void add(int i, double sum, double close) {
		for (int n : MA_N) {
			List<MA> list = mas.get(n);
			if (i == n - 1) {
				MA ma = new MA(n);
				ma.caculate(sum);
				list.add(ma);
			} else if (i >= n) {
				MA ma = new MA(n);
				ma.caculate(list.get(i - 1).getMa(), bars.get(i - n).getClose(), close);
				list.add(ma);
			} else {
				list.add(null);
			}
		}
	}

	/**
	 * 刷新K线数据
	 */
	public void refresh() {
		double high = Double.MIN_VALUE;
		double low = Double.MAX_VALUE;
		double mh = Double.MIN_VALUE;

		int hvol = Integer.MIN_VALUE;
		int lvol = Integer.MAX_VALUE;
		// 刷新区域最大最小值
		for (int i = head; i <= tail; i++) {
			Bar bar = bars.get(i);
			if (high < bar.getHigh()) {
				high = bar.getHigh();
			}
			if (low > bar.getLow()) {
				low = bar.getLow();
			}
			if (deal < bar.getDealVol()) {
				deal = bar.getDealVol();
			}
			if (hvol < bar.getVol()) {
				hvol = bar.getVol();
			}
			if (lvol > bar.getVol()) {
				lvol = bar.getVol();
			}

			MACD macd = macds.get(i);
			double max = MathUtil.max(macd.getDif(), macd.getDea(), macd.getDel());
			double min = MathUtil.min(macd.getDif(), macd.getDea(), macd.getDel());
			double m = Math.max(max, -min);
			if (mh < m) {
				mh = m;
			}
		}
		this.mid = (high + low) / 2.0;
		this.del = high - low;
		this.span = barBound.getWidth() / (float) count;
		// this.glvols = new float[2 * count - 4];
		int mvol = (hvol + lvol) / 2;
		int dvol = hvol - lvol;
		// GL(-1,1)，则宽度应该2倍
		float delw = 2.0f / (barBound.getWidth() - 1.0f);
		float wid = span * delw * Constants.BAR_VALUE_SCALE;
		for (int i = head; i <= tail; i++) {
			GLBar bar = glbars.get(i);
			// K线区域从左下角(0,0)开始
			float x = (i - head) * span + span / 2.0f + 1;
			float xf = x * delw - 1.0f;
			bar.refresh(xf, wid, mid, del);
			// 刷新MA值
			for (int n : MA_N) {
				MA ma = mas.get(n).get(i);
				ma.refresh(xf, mid, del);
			}

			Bar k = bars.get(i);
			GLCJL cjl = glcjls.get(i);
			cjl.refresh(xf, wid, deal, mvol, dvol, k.getOpen() <= k.getClose());

			macds.get(i).refresh(xf, mh);
		}
	}

	/**
	 * @param md
	 */
	public void add(MD md) {

	}

	/**
	 * @return
	 */
	public List<MD> getMds() {
		return mds;
	}

	/**
	 * @return
	 */
	public List<Bar> getBars() {
		return bars;
	}

	/**
	 * @return
	 */
	public List<GLBar> getGlbars() {
		return glbars;
	}

	/**
	 * @return the mas
	 */
	public Map<Integer, List<MA>> getMas() {
		return mas;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public Map<Integer, float[]> getColors() {
		return colors;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public List<GLCJL> getGlcjls() {
		return glcjls;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public List<MACD> getMacds() {
		return macds;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public Bound getBarBound() {
		return barBound;
	}

	/**
	 * 设置 xxx
	 * 
	 * @param barBound
	 *            xxx
	 */
	public void setBarBound(Bound barBound) {
		this.barBound = barBound;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public Bound getDealBound() {
		return dealBound;
	}

	/**
	 * 设置 xxx
	 * 
	 * @param dealBound
	 *            xxx
	 */
	public void setDealBound(Bound dealBound) {
		this.dealBound = dealBound;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public Bound getMacdBound() {
		return macdBound;
	}

	/**
	 * 设置 xxx
	 * 
	 * @param macdBound
	 *            xxx
	 */
	public void setMacdBound(Bound macdBound) {
		this.macdBound = macdBound;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public Bound getDetailBound() {
		return detailBound;
	}

	/**
	 * 设置 xxx
	 * 
	 * @param detailBound
	 *            xxx
	 */
	public void setDetailBound(Bound detailBound) {
		this.detailBound = detailBound;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @return the head
	 */
	public int getHead() {
		return head;
	}

	/**
	 * @return the tail
	 */
	public int getTail() {
		return tail;
	}

	/**
	 * @return the mid
	 */
	public double getMid() {
		return mid;
	}

	/**
	 * @return the del
	 */
	public double getDel() {
		return del;
	}

	/**
	 * 获取 xxx
	 * 
	 * @return xxx
	 */
	public int getDeal() {
		return deal;
	}

	/**
	 * @return the span
	 */
	public float getSpan() {
		return span;
	}

	/**
	 * 辅助监听器
	 */
	private class MyListener implements GLEventListener, ZoomListener {
		/**
		 * GL监听器
		 */
		private List<GLEventListener> glListeners = new ArrayList<GLEventListener>();
		/**
		 * 放大缩小监听器
		 */
		private List<ZoomListener> zListeners = new ArrayList<ZoomListener>();

		/**
		 * @param listener
		 */
		public void addGLEventListener(final GLEventListener listener) {
			glListeners.add(listener);
		}

		/**
		 * @param listener
		 * @return
		 */
		public GLEventListener removeGLEventListener(final GLEventListener listener) {
			return glListeners.remove(listener) ? listener : null;
		}

		/**
		 * @param listener
		 */
		public void addZoomListener(final ZoomListener listener) {
			zListeners.add(listener);
		}

		/**
		 * @param listener
		 * @return ZoomListener
		 */
		public ZoomListener removeZoomListener(final ZoomListener listener) {
			return zListeners.remove(listener) ? listener : null;
		}

		@Override
		public void init(GLAutoDrawable drawable) {
			for (int i = 0; i < glListeners.size(); i++) {
				glListeners.get(i).init(drawable);
			}
		}

		@Override
		public void dispose(GLAutoDrawable drawable) {
			for (int i = 0; i < glListeners.size(); i++) {
				glListeners.get(i).dispose(drawable);
			}
		}

		@Override
		public void display(GLAutoDrawable drawable) {
			for (int i = 0; i < glListeners.size(); i++) {
				glListeners.get(i).display(drawable);
			}
		}

		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
			for (int i = 0; i < glListeners.size(); i++) {
				glListeners.get(i).reshape(drawable, x, y, width, height);
			}
		}

		@Override
		public void zoomIn(ZoomEvent e) {
			for (int i = 0; i < zListeners.size(); i++) {
				zListeners.get(i).zoomIn(e);
			}
		}

		@Override
		public void zoomOut(ZoomEvent e) {
			for (int i = 0; i < zListeners.size(); i++) {
				zListeners.get(i).zoomOut(e);
			}
		}

	}
}
