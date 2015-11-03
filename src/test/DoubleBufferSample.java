package test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DoubleBufferSample {

	private static String text = "This is a sample about how to use double buffer to reduce flashing. ";
	private static int index = 0;

	private Canvas canvas;

	public static void main(String[] args) {
		final DoubleBufferSample inst = new DoubleBufferSample();
		final Display display = new Display();
		Shell shell = new Shell(display);
		inst.createContents(shell);
		shell.open();

		Runnable runnable = new Runnable() {
			public void run() {
				display.timerExec(20, this);
				inst.canvas.redraw();
			}
		};

		display.timerExec(20, runnable);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.timerExec(-1, runnable);
		display.dispose();
	}

	private void createContents(final Shell shell) {
		shell.setLayout(new FillLayout());
		canvas = new Canvas(shell, SWT.COLOR_RED | SWT.DOUBLE_BUFFERED);

		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				Image image = new Image(shell.getDisplay(), canvas.getBounds());

				GC gc = new GC(image);
				gc.setBackground(event.gc.getBackground());
//				gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				gc.fillRectangle(image.getBounds());
				image.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_GREEN));
//				gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

				if (index < text.length() - 1)
					index++;
				else
					index = 0;
				gc.drawText(text.substring(0, index), 0, 0);

				event.gc.drawImage(image, 50, 50);

				image.dispose();
				gc.dispose();

			}
		});
	}
}