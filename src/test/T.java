package test;

import java.io.File;
import java.util.List;

import com.finance.core.Bar;
import com.finance.util.BufferUtil;

public class T {

	public static void main(String[] args) {
		List<Bar> bars = BarReadTest.parse(new File("IF0002.txt"),  60 * 1000);
		BufferUtil.write(new File("IF0002.dat"), bars);
		// List<Bar> bars = BufferUtil.read(new File("IF0000.dat"));
		// for (Bar bar : bars) {
		// System.out.println(bar.date2Str());
		// }
	}
}
