package com.finance.util;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.finance.core.Bar;

/**
 * @author YuLin Nov 4, 2015
 */
public class BufferUtil {
	/**
	 * 字符编码
	 */
	private static final Charset charset = Charset.forName("UTF-8");
	/**
	 * K线数据二进制字节数
	 */
	public static final int BAR_BUFFER_NUM = 61;

	/**
	 * 字符串转为char*("\0"结束)
	 * 
	 * @param str
	 * @return char*
	 */
	public static byte[] str2c(String str) {
		if (str == null) {
			return null;
		}
		byte[] d = str.getBytes(charset);
		byte[] r = new byte[d.length + 1];
		System.arraycopy(d, 0, r, 0, d.length);
		return r;
	}

	/**
	 * char*转为字符串("\0"结束)
	 * 
	 * @param bytes
	 * @return string
	 */
	public static String c2str(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		return c2str(bytes, 0, bytes.length);
	}

	/**
	 * char*转为字符串("\0"结束)
	 * 
	 * @param bytes
	 * @param index
	 * @param len
	 * @return string
	 */
	public static String c2str(byte[] bytes, int index, int len) {
		if (bytes == null || len == 0) {
			return null;
		}
		if (bytes[index] == 0) {
			return "";
		}
		int max = len;
		for (int i = index; i < len; i++) {
			if (bytes[index + i] == 0) {
				max = index + i;
				break;
			}
		}
		return new String(bytes, index, max, charset);
	}

	/**
	 * 二进制数据转成K线
	 * 
	 * @param data
	 * @return bar
	 */
	public static Bar bytes2Bar(byte[] data) {
		// 时间|合约|周期|开盘价|收盘价|最高价|最低价|成交均价|成交量|持仓量
		if (data == null || data.length < BAR_BUFFER_NUM) {
			return null;
		}
		ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.nativeOrder());
		Bar bar = new Bar();
		bar.setStartTime(buffer.getLong(0)); // 开始时间
		bar.setCode(c2str(data, 8, 13)); // 合约代码
		bar.setPeriod(buffer.getLong(13)); // K线周期
		bar.setEndTime(bar.getStartTime() + bar.getPeriod()); // 结束时间
		bar.setOpen(buffer.getDouble(21)); // 开盘
		bar.setClose(buffer.getDouble(29)); // 收盘
		bar.setHigh(buffer.getDouble(37)); // 最高
		bar.setLow(buffer.getDouble(45)); // 最低
		bar.setDealVol(buffer.getInt(53)); // 成交量
		bar.setVol(buffer.getInt(57)); // 持仓量
		bar.setPriceChange(bar.getOpen() - bar.getClose());
		if (bar.getOpen() != 0) {
			bar.setPriceChangeRate(bar.getPriceChange() / bar.getOpen() * 100d);
		}
		bar.setFinished(true);
		return bar;
	}

	/**
	 * K线转成二进制数据
	 * 
	 * @param bar
	 * @return bytes
	 */
	public static byte[] bar2Bytes(Bar bar) {
		ByteBuffer buffer = ByteBuffer.allocate(BAR_BUFFER_NUM).order(ByteOrder.nativeOrder());
		buffer.putLong(0, bar.getStartTime()); // 开始时间
		buffer.position(8);
		buffer.put(str2c(bar.getCode()), 0, 5); // 合约代码
		buffer.putLong(13, bar.getPeriod()); // K线周期
		buffer.putDouble(21, bar.getOpen()); // 开盘
		buffer.putDouble(29, bar.getClose()); // 收盘
		buffer.putDouble(37, bar.getHigh()); // 最高
		buffer.putDouble(45, bar.getLow()); // 最低
		buffer.putInt(53, bar.getDealVol()); // 成交量
		buffer.putInt(57, bar.getVol()); // 持仓量
		return buffer.array();
	}

	/**
	 * 从二进制文件读取K线
	 * 
	 * @param file
	 * @return bars
	 */
	public static List<Bar> read(File file) {
		if (file == null || !file.exists()) {
			return new ArrayList<Bar>();
		}
		List<Bar> bars = new ArrayList<Bar>();
		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new FileInputStream(file));
			byte[] data = new byte[BAR_BUFFER_NUM];
			while (dis.read(data) != -1) {
				Bar bar = bytes2Bar(data);
				bars.add(bar);
			}
		} catch (Throwable e) {
		} finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
				}
			}
		}
		return bars;
	}

	/**
	 * 把K线数据写入二进制文件
	 * 
	 * @param file
	 * @param bars
	 */
	public static void write(File file, List<Bar> bars) {
		if (file == null || bars.isEmpty()) {
			return;
		}
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			for (Bar bar : bars) {
				dos.write(bar2Bytes(bar));
			}
		} catch (Throwable e) {
		} finally {
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
