package test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import com.finance.core.Bar;
import com.finance.core.MD;
import com.finance.core.Pointf;
import com.finance.ui.bean.GLBar;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.swt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * @author 陈霖 2015-8-31
 */
public class GLPlot1 extends Composite implements GLEventListener, MouseListener, MouseMoveListener, KeyListener {

	/**
	 * 默认展示K线条数
	 */
	private int count = 100;
	/**
	 * 画布
	 */
	private GLCanvas canvas;
	/**
	 * 刷新线程
	 */
	private FPSAnimator animator;
	/**
	 * 
	 */
	/**
	 * 未完成的K线数据
	 */
	private Bar bar;
	/**
	 * 完成的K线数据
	 */
	private List<Bar> bars = new ArrayList<Bar>();
	/**
	 * K线画图数据
	 */
	private List<GLBar> glbars = new ArrayList<GLBar>();
	/**
	 * K线提示框
	 */
	private GLBarTip bartip = new GLBarTip();
	/**
	 * 对应K线位置
	 */
	private int index;
	/**
	 * plot中K线开始位置
	 */
	private int head;
	/**
	 * plot中K线结束位置
	 */
	private int tail;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public GLPlot1(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new FillLayout());
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		canvas = GLCanvas.create(parent, SWT.NONE, capabilities, null);
		// canvas.setSize(parent.getSize());
		canvas.addGLEventListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMoveListener(this);
		canvas.addKeyListener(this);
		animator = new FPSAnimator(canvas, 30);
		animator.start();
	}

	@Override
	protected void checkSubclass() {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glClearColor(0.1f, 0.1f, 0f, 0.1f); // Black Background
		gl.glViewport(0, 0, drawable.getSurfaceWidth(), drawable.getSurfaceHeight());
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		animator.stop();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl2 = drawable.getGL().getGL2();
		// 清除画面
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT);

		// 背景刷新(放最底层)
		// background.refresh(gl2);

		// 刷新K线
		for (int i = head; i < tail; i++) {
			glbars.get(i).refresh(gl2);
		}
		if (!bars.isEmpty()) {
			bartip.refresh(gl2, bars.get(index));
		}
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	/**
	 * @param bar
	 */
	public void add(Bar bar) {
		if (bar.isFinished()) {
			bars.add(bar);
		} else {

		}
	}

	/**
	 * @param bars
	 */
	public void addAll(Collection<Bar> bars) {
		this.bars.addAll(bars);
	}

	/**
	 * @param md
	 */
	public void add(MD md) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.keyCode) {
		case SWT.ARROW_UP:
			if ((e.stateMask | SWT.CTRL) == 0) {

			} else {

			}
			break;
		case SWT.ARROW_DOWN:
			if ((e.stateMask | SWT.CTRL) == 0) {

			} else {

			}

			break;
		case SWT.ARROW_LEFT:
			if ((e.stateMask | SWT.CTRL) == 0) {

			} else {

			}

			break;
		case SWT.ARROW_RIGHT:
			if ((e.stateMask | SWT.CTRL) == 0) {

			} else {

			}

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
		bartip.setVisible(!bartip.isVisible());
		if (bartip.isVisible()) {
			bartip.setPoint(toPointf(e.x, e.y));
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {
	}

	@Override
	public void mouseUp(MouseEvent e) {
	}

	@Override
	public void mouseMove(MouseEvent e) {
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	private Pointf toPointf(int x, int y) {
		return new Pointf(x, y);
	}

	@Override
	public void addControlListener(ControlListener listener) {
		canvas.addControlListener(listener);
	}

	@Override
	public void addDisposeListener(DisposeListener listener) {
		canvas.addDisposeListener(listener);
	}

	@Override
	public void addFocusListener(FocusListener listener) {
		canvas.addFocusListener(listener);
	}

	@Override
	public void addDragDetectListener(DragDetectListener listener) {
		canvas.addDragDetectListener(listener);
	}

	@Override
	public void addHelpListener(HelpListener listener) {
		canvas.addHelpListener(listener);
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		canvas.addKeyListener(listener);
	}

	@Override
	public void addListener(int eventType, Listener listener) {
		canvas.addListener(eventType, listener);
	}

	@Override
	public void addMenuDetectListener(MenuDetectListener listener) {
		canvas.addMenuDetectListener(listener);
	}

	@Override
	public void addMouseListener(MouseListener listener) {
		canvas.addMouseListener(listener);
	}

	@Override
	public void addMouseMoveListener(MouseMoveListener listener) {
		canvas.addMouseMoveListener(listener);
	}

	@Override
	public void addMouseTrackListener(MouseTrackListener listener) {
		canvas.addMouseTrackListener(listener);
	}

	@Override
	public void addMouseWheelListener(MouseWheelListener listener) {
		canvas.addMouseWheelListener(listener);
	}

	@Override
	public void addPaintListener(PaintListener listener) {
		canvas.addPaintListener(listener);
	}

	@Override
	public void addTraverseListener(TraverseListener listener) {
		canvas.addTraverseListener(listener);
	}

}
