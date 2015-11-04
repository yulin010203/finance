package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.finance.core.Bar;
import com.ibm.icu.text.SimpleDateFormat;

public class BarReadTest {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");

	public static void main(String[] args) {
		parse(new File("IF0000.txt"), 5 * 60 * 1000);
	}

	/**
	 * @param file
	 * @param period
	 * @return
	 */
	public static List<Bar> parse(File file, long period) {
		if (file == null) {
			return null;
		}
		List<Bar> results = new ArrayList<Bar>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String tmp = null;
			while ((tmp = br.readLine()) != null) {
				String[] arr = tmp.split("\\|");
				Bar bar = create(arr, period);
				results.add(bar);
			}
		} catch (Throwable e) {
			System.out.println(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
		return results;
	}

	/**
	 * @param arr
	 * @param period
	 * @return
	 */
	private static Bar create(String[] arr, long period) {
		// 时间|合约|周期|开盘价|收盘价|最高价|最低价|成交均价|成交量|持仓量
		// 201510260915|IF0000|5m|3497.40|3486.00|3503.40|3485.40|3497.35|1075|20388
		try {
			Bar bar = new Bar();
			bar.setStartTime(sdf.parse(arr[0]).getTime());
			bar.setEndTime(bar.getStartTime() + period);
			bar.setCode(arr[1]);
			bar.setPeriod(period);
			bar.setOpen(Double.parseDouble(arr[3]));
			bar.setClose(Double.parseDouble(arr[4]));
			bar.setHigh(Double.parseDouble(arr[5]));
			bar.setLow(Double.parseDouble(arr[6]));
			bar.setDealVol(Integer.parseInt(arr[8]));
			bar.setVol(Integer.parseInt(arr[9].trim()));
			bar.setPriceChange(bar.getOpen() - bar.getClose());
			if (bar.getOpen() != 0) {
				bar.setPriceChangeRate(bar.getPriceChange() / bar.getOpen() * 100d);
			}
			bar.setFinished(true);
			return bar;
		} catch (Throwable e) {
			System.out.println(e);
			throw new RuntimeException(e);
		}
	}
}
