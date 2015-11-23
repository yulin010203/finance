package com.finance.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.internal.win32.RECT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.finance.core.Bar;
import com.finance.core.BarComputer;
import com.finance.core.Bound;
import com.finance.core.Constants;
import com.finance.core.MD;
import com.finance.core.indicator.MA;
import com.finance.core.indicator.MACD;
import com.finance.listener.BarListener;
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
public class GLDisplay extends PlotBase implements BarListener, KeyListener, MouseListener, MouseMoveListener, MouseWheelListener {
	private static final Log log = LogFactory.getLog(GLDisplay.class);
	/**
	 * 移动平均线指数
	 */
	public static final int[] MA_N = { 5, 10, 20, 40, 60 };
	/**
	 * K线图区域
	 */
	private Bound barBound = new Bound();
	/**
	 * 成交量图区域
	 */
	private Bound dealBound = new Bound();
	/**
	 * MACD图区域
	 */
	private Bound macdBound = new Bound();
	/**
	 * 明细图区域
	 */
	private Bound detailBound = new Bound();
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
	 * 鼠标点的位置
	 */
	private Point point;
	/**
	 * 拖拽K线图
	 */
	private boolean move;
	/**
	 * 展示条数
	 */
	private int count = Constants.BAR_SHOW_NUM;
	/**
	 * 显示十字线
	 */
	private boolean showTip;
	/**
	 * plot中K线开始位置
	 */
	private int head;
	/**
	 * plot中K线结束位置
	 */
	private int tail;
	/**
	 * 图区内最高价
	 */
	private double high;
	/**
	 * 图区内最低价
	 */
	private double low;
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
	 * 图区macd最大值
	 */
	private double macdMax;
	/**
	 * K线结算价求和
	 */
	private double sum;
	/**
	 * K线宽度(像素)
	 */
	private float span;
	/**
	 * K线计算器
	 */
	private BarComputer computer;
	/**
	 * 画面更新锁
	 */
	private final Object lock = new Object();

	/**
	 * @param display
	 */
	public GLDisplay(Display display) {
		this.shell = new Shell(display);
		this.shell.setLayout(new FillLayout(SWT.VERTICAL));
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		this.canvas = GLCanvas.create(shell, SWT.NONE, capabilities, null);
		this.computer = new BarComputer();
		addListener();
		this.animator = new FPSAnimator(canvas, Constants.SHOW_RATE);
	}

	/**
	 * add listener
	 */
	private void addListener() {
		canvas.addGLEventListener(listener);
		canvas.addKeyListener(listener);
		canvas.addMouseListener(listener);
		canvas.addMouseMoveListener(listener);
		canvas.addMouseWheelListener(listener);
		listener.addKeyListener(this);
		listener.addMouseListener(this);
		listener.addMouseMoveListener(this);
		listener.addMouseWheelListener(this);
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
	public void addKeyListener(KeyListener listener) {
		this.listener.addKeyListener(listener);
	}

	/**
	 * @param listener
	 * @return KeyListener
	 */
	public KeyListener removeKeyListener(KeyListener listener) {
		return this.listener.removeKeyListener(listener);
	}

	/**
	 * @param listener
	 */
	public void addMouseListener(MouseListener listener) {
		this.listener.addMouseListener(listener);
	}

	/**
	 * @param listener
	 * @return MouseListener
	 */
	public MouseListener removeMouseListener(MouseListener listener) {
		return this.listener.removeMouseListener(listener);
	}

	/**
	 * @param listener
	 */
	public void addMouseMoveListener(MouseMoveListener listener) {
		this.listener.addMouseMoveListener(listener);
	}

	/**
	 * @param listener
	 * @return MouseMoveListener
	 */
	public MouseMoveListener removeMouseMoveListener(MouseMoveListener listener) {
		return this.listener.removeMouseMoveListener(listener);
	}

	/**
	 * @param listener
	 */
	public void addMouseWheelListener(MouseWheelListener listener) {
		this.listener.addMouseWheelListener(listener);
	}

	/**
	 * @param listener
	 * @return MouseWheelListener
	 */
	public MouseWheelListener removeMouseWheelListener(MouseWheelListener listener) {
		return this.listener.removeMouseWheelListener(listener);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.keyCode) {
		case SWT.ARROW_UP:
			// 同时按下Ctrl键
			if (e.stateMask == SWT.CTRL) {

			} else {
				// 放大
				zoomOut();
			}
			break;
		case SWT.ARROW_DOWN:
			// 同时按下Ctrl键
			if (e.stateMask == SWT.CTRL) {

			} else {
				// 缩小
				zoomIn();
			}

			break;
		case SWT.ARROW_LEFT:
			// 同时按下Ctrl键
			if (e.stateMask == SWT.CTRL) {

			} else {
				if (!showTip) {
					// 左翻页
					left(e);
				}
			}
			break;
		case SWT.ARROW_RIGHT:
			// 同时按下Ctrl键
			if (e.stateMask == SWT.CTRL) {

			} else {
				if (!showTip) {
					// 右翻页
					right(e);
				}
			}
			break;
		case SWT.PAGE_UP:
			// 左翻页
			left(e);
			break;
		case SWT.PAGE_DOWN:
			// 右翻页
			right(e);
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

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		showTip = !showTip;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (bars.isEmpty()) {
			return;
		}
		// 此时还未转变为左下角原点
		if (e.x < barBound.x || e.x > detailBound.x || e.y < Constants.UP_SPAN || height - e.y <= Constants.DOWN_SPAN) {
			return;
		}
		if (!showTip && e.count == 1) {
			canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_SIZEE));
			move = true;
		}
		point = new Point(e.x, height - e.y);
	}

	@Override
	public void mouseUp(MouseEvent e) {
		move = false;
		canvas.setCursor(canvas.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (bars.isEmpty()) {
			return;
		}
		// 此时还未转变为左下角原点
		if (e.x < barBound.x || e.x > detailBound.x || e.y < Constants.UP_SPAN || height - e.y <= Constants.DOWN_SPAN) {
			return;
		}
		if (move) {
			int mx = e.x - point.x;
			if (mx > 0) {
				int num = (int) (mx / span);
				left(Constants.BAR_MOVE_SPEED * (num * num + 1));
			}
			if (mx < 0) {
				int num = (int) (-mx / span);
				right(Constants.BAR_MOVE_SPEED * (num * num + 1));
			}
		}
		point = new Point(e.x, height - e.y);
	}

	@Override
	public void mouseScrolled(MouseEvent e) {
		// if (e.button == SWT.MouseWheel && e.stateMask != SWT.CTRL) {
		if (e.stateMask != SWT.CTRL) {
			if (e.count < 0) {
				zoomIn();
			} else {
				zoomOut();
			}
		}
	}

	/**
	 * 放大
	 * 
	 * @param e
	 */
	private synchronized void zoomIn() {
		if (count >= bars.size()) {
			// 已经放大到最大
			return;
		}
		synchronized (lock) {
			count = count * 2;
			int size = bars.size();
			if (count > size) {
				count = size;
			}
			// 以屏幕中心放大一倍
			tail += count / 4;
			if (tail > size - 1) {
				tail = size - 1;
			}
			head = tail - count + 3;
			if (head < 0) {
				head = 0;
				tail = count - 3;
			}
			refresh();
		}
	}

	/**
	 * 缩小
	 * 
	 * @param e
	 */
	private synchronized void zoomOut() {
		if (count <= Constants.BAR_SHOW_NUM_MIN) {
			// 已经缩小到最小
			return;
		}
		synchronized (lock) {
			count = count / 2 + 1;
			if (count < Constants.BAR_SHOW_NUM_MIN) {
				count = Constants.BAR_SHOW_NUM_MIN;
			}
			tail -= count / 2;
			head = tail - count + 3;
			refresh();
		}
	}

	/**
	 * 左翻页
	 * 
	 * @param e
	 */
	public void left(KeyEvent e) {
		left(count - 2);
	}

	/**
	 * 左移num条K线
	 * 
	 * @param num
	 */
	private void left(int num) {
		if (head <= 0 || num < 1) {
			return;
		}
		synchronized (lock) {
			tail = tail - num;
			head = head - num;
			if (head < 0) {
				head = 0;
				tail = count - 3;
			}
			refresh();
		}
	}

	/**
	 * 右翻页
	 * 
	 * @param e
	 */
	public void right(KeyEvent e) {
		right(count - 2);
	}

	/**
	 * 右移num条K线
	 * 
	 * @param num
	 */
	private void right(int num) {
		if (tail >= bars.size() - 1 || num < 1) {
			return;
		}
		synchronized (lock) {
			tail = tail + num;
			head = head + num;
			if (tail > bars.size() - 1) {
				tail = bars.size() - 1;
				head = tail - count + 3;
			}
			refresh();
		}
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
		log.info("***************************end*************************");
		animator.stop();
		canvas.dispose();
		shell.dispose();
		System.exit(0);
	}

	@Override
	public void onBar(Bar bar) {
	}

	@Override
	public void onBarInside(Bar bar) {
		if (bar.isStart()) {
			add(bar);
		}
	}

	/**
	 * @param bar
	 */
	public void add(Bar bar) {
		// if (!bar.isFinished()) {
		// return;
		// }

		if (bars.isEmpty()) {
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
		}
		bars.add(bar);
		int size = bars.size();
		glbars.add(new GLBar(this, bar));
		sum += bar.getClose();
		addMA(size - 1, sum, bar.getClose());

		glcjls.add(new GLCJL(this, bar.getDealVol(), bar.getVol()));

		MACD macd = new MACD();
		macd.caculate(macds.isEmpty() ? null : macds.get(size - 2), bar.getClose());
		macds.add(macd);
		synchronized (lock) {
			if (size < count - 2) {
				tail = size - 1;
				head = 0;
			} else {
				head++;
				tail++;
			}
			refresh();
		}
	}

	/**
	 * @param bars
	 */
	public void addAll(Collection<Bar> bars) {
		if (bars.isEmpty()) {
			return;
		}
		// int size = bars.size();
		// if (count > size) {
		// count = size;
		// }
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

		MACD last = null;
		int index = 0;
		double sum = 0;
		for (Bar bar : bars) {
			this.bars.add(bar);
			glbars.add(new GLBar(this, bar));
			sum += bar.getClose();
			addMA(index, sum, bar.getClose());

			glcjls.add(new GLCJL(this, bar.getDealVol(), bar.getVol()));

			MACD macd = new MACD();
			macd.caculate(last, bar.getClose());
			macds.add(macd);
			last = macd;
			index++;
		}
		int size = bars.size();
		tail = size - 1;
		if (size < count - 2) {
			head = 0;
		} else {
			head = tail - count + 3;
		}
	}

	/**
	 * 添加并计算MA指标
	 * 
	 * @param i
	 * @param sum
	 * @param close
	 */
	private void addMA(int i, double sum, double close) {
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
	 * @param show
	 */
	public void refresh(boolean show) {
		if (!show) {
			return;
		}
		Bar bar = bars.get(tail);
		if (bar.getClose() > high) {
			high = bar.getClose();
			this.mid = (high + low) / 2.0;
			this.del = high - low;
		}
		if (bar.getClose() < low) {
			low = bar.getClose();
			this.mid = (high + low) / 2.0;
			this.del = high - low;
		}
		float wid = span * barBound.delw * Constants.BAR_VALUE_SCALE;
		GLBar glbar = glbars.get(tail);
		float[] v = glbar.getClosef();
		glbar.refresh(v[0], wid, mid, del);
		// 刷新MA值
		for (int n : MA_N) {
			MA ma = mas.get(n).get(tail);
			if (ma == null) {
				continue;
			}
			ma.refresh(v[0], mid, del);
		}

		GLCJL cjl = glcjls.get(tail);
		// cjl.refresh(v[0], wid, deal, mvol, dvol, bar.getOpen() <= bar.getClose());
		MACD macd = macds.get(tail);
		if (macd == null) {
		}
		// macd.refresh(v[0], mh);
	}

	/**
	 * 刷新K线数据
	 */
	public void refresh() {
		if (head == tail) {
			return;
		}
		double high = Double.MIN_VALUE;
		double low = Double.MAX_VALUE;
		double mh = Double.MIN_VALUE;

		int hvol = Integer.MIN_VALUE;
		int lvol = Integer.MAX_VALUE;
		this.deal = 0;
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
		this.high = high;
		this.low = low;
		this.macdMax = mh;
		this.mid = (high + low) / 2.0;
		this.del = high - low;
		int mvol = (hvol + lvol) / 2;
		int dvol = hvol - lvol;
		// K线宽度
		this.span = barBound.width / (float) count;
		float wid = span * barBound.delw * Constants.BAR_VALUE_SCALE;
		for (int i = head; i <= tail; i++) {
			GLBar bar = glbars.get(i);
			// K线区域从左下角(0,0)开始
			int x = (int) ((i - head) * span + span / 2.0f + 1);
			float xf = barBound.toxf(x);
			bar.refresh(xf, wid, mid, del);
			// 刷新MA值
			for (int n : MA_N) {
				MA ma = mas.get(n).get(i);
				if (ma == null) {
					continue;
				}
				ma.refresh(xf, mid, del);
			}

			Bar k = bars.get(i);
			GLCJL cjl = glcjls.get(i);
			cjl.refresh(xf, wid, deal, mvol, dvol, k.getOpen() <= k.getClose());
			MACD macd = macds.get(i);
			if (macd == null) {
				continue;
			}
			macd.refresh(xf, mh);
		}
	}

	/**
	 * @param md
	 */
	public void add(MD md) {
		computer.update(md);

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
	 * @return the macdMax
	 */
	public double getMacdMax() {
		return macdMax;
	}

	/**
	 * @return the span
	 */
	public float getSpan() {
		return span;
	}

	/**
	 * 获取窗口位置
	 * 
	 * @return Point
	 */
	public Point getLocation() {
		RECT rect = new RECT();
		OS.GetWindowRect(canvas.handle, rect);
		return new Point(rect.left, rect.top);
	}

	/**
	 * 辅助监听器
	 */
	private class MyListener implements GLEventListener, KeyListener, MouseListener, MouseMoveListener, MouseWheelListener {
		/**
		 * GL监听器
		 */
		private List<GLEventListener> glListeners = new ArrayList<GLEventListener>();
		/**
		 * 按键监听器
		 */
		private List<KeyListener> kListeners = new ArrayList<KeyListener>();
		/**
		 * 鼠标监听器
		 */
		private List<MouseListener> mListeners = new ArrayList<MouseListener>();
		/**
		 * 鼠标移动监听器
		 */
		private List<MouseMoveListener> moveListeners = new ArrayList<MouseMoveListener>();
		/**
		 * 鼠标滚轮监听器
		 */
		private List<MouseWheelListener> wheelListeners = new ArrayList<MouseWheelListener>();

		/**
		 * @param listener
		 */
		public void addGLEventListener(final GLEventListener listener) {
			glListeners.add(listener);
		}

		/**
		 * @param listener
		 * @return GLEventListener
		 */
		public GLEventListener removeGLEventListener(final GLEventListener listener) {
			return glListeners.remove(listener) ? listener : null;
		}

		/**
		 * @param listener
		 */
		public void addKeyListener(KeyListener listener) {
			kListeners.add(listener);
		}

		/**
		 * @param listener
		 * @return KeyListener
		 */
		public KeyListener removeKeyListener(KeyListener listener) {
			return kListeners.remove(listener) ? listener : null;
		}

		/**
		 * @param listener
		 */
		public void addMouseListener(MouseListener listener) {
			mListeners.add(listener);
		}

		/**
		 * @param listener
		 * @return MouseListener
		 */
		public MouseListener removeMouseListener(MouseListener listener) {
			return mListeners.remove(listener) ? listener : null;
		}

		/**
		 * @param listener
		 */
		public void addMouseMoveListener(MouseMoveListener listener) {
			moveListeners.add(listener);
		}

		/**
		 * @param listener
		 * @return MouseMoveListener
		 */
		public MouseMoveListener removeMouseMoveListener(MouseMoveListener listener) {
			return moveListeners.remove(listener) ? listener : null;
		}

		/**
		 * @param listener
		 */
		public void addMouseWheelListener(MouseWheelListener listener) {
			wheelListeners.add(listener);
		}

		/**
		 * @param listener
		 * @return MouseWheelListener
		 */
		public MouseWheelListener removeMouseWheelListener(MouseWheelListener listener) {
			return wheelListeners.remove(listener) ? listener : null;
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
		public void keyPressed(KeyEvent e) {
			for (int i = 0; i < kListeners.size(); i++) {
				kListeners.get(i).keyPressed(e);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			for (int i = 0; i < kListeners.size(); i++) {
				kListeners.get(i).keyReleased(e);
			}
		}

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			for (int i = 0; i < mListeners.size(); i++) {
				mListeners.get(i).mouseDoubleClick(e);
			}
		}

		@Override
		public void mouseDown(MouseEvent e) {
			for (int i = 0; i < mListeners.size(); i++) {
				mListeners.get(i).mouseDown(e);
			}
		}

		@Override
		public void mouseUp(MouseEvent e) {
			for (int i = 0; i < mListeners.size(); i++) {
				mListeners.get(i).mouseUp(e);
			}
		}

		@Override
		public void mouseMove(MouseEvent e) {
			for (int i = 0; i < moveListeners.size(); i++) {
				moveListeners.get(i).mouseMove(e);
			}
		}

		@Override
		public void mouseScrolled(MouseEvent e) {
			for (int i = 0; i < wheelListeners.size(); i++) {
				wheelListeners.get(i).mouseScrolled(e);
			}
		}
	}

}
