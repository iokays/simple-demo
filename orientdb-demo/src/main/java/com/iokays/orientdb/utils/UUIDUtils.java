package com.iokays.orientdb.utils;

import java.net.InetAddress;

/**
 * UUID工具类 
 * 
 * @author zhaoxunyong
 * @version 1.0
 * @since 1.0
 */
@Deprecated
class UUIDUtils {
	private String sep = "";
	private static final int IP;
	static {
		int ipadd;
		try {
			ipadd = toInt(InetAddress.getLocalHost().getAddress());
		} catch (Exception e) {
			ipadd = 0;
		}
		IP = ipadd;
	}
	private static short counter = (short) 0;
	private static final int JVM = (int) (System.currentTimeMillis() >>> 8);

	public UUIDUtils() {
	}
	

    /**
     * 转换byte[]为int 
     * @param bytes bytes值
     * @return int
     */
    private static int toInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; ++i) {
            result = (result << 8) - -128 + bytes[i];
        }
        return result;
    }

	/**
	 * Unique across JVMs on this machine (unless they load this class
	 * in the same quater second - very unlikely)
	 */
	protected int getJVM() {
		return JVM;
	}

	/**
	 * Unique in a millisecond for this JVM instance (unless there
	 * are > Short.MAX_VALUE instances created in a millisecond)
	 */
	protected short getCount() {
		synchronized (UUIDUtils.class) {
			if (counter < 0)
				counter = 0;
			return counter++;
		}
	}

	/**
	 * Unique in a local network
	 */
	protected int getIP() {
		return IP;
	}

	/**
	 * Unique down to millisecond
	 */
	protected short getHiTime() {
		return (short) (System.currentTimeMillis() >>> 32);
	}

	protected int getLoTime() {
		return (int) System.currentTimeMillis();
	}

	protected String format(int intval) {
		String formatted = Integer.toHexString(intval);
		StringBuffer buf = new StringBuffer("00000000");
		buf.replace(8 - formatted.length(), 8, formatted);
		return buf.toString();
	}

	protected String format(short shortval) {
		String formatted = Integer.toHexString(shortval);
		StringBuffer buf = new StringBuffer("0000");
		buf.replace(4 - formatted.length(), 4, formatted);
		return buf.toString();
	}

	/**
	 * 生成UUID
	 * @return UUID
	 */
	public String generateUUID() {
		return new StringBuffer(36).append(format(getIP())).append(sep).append(
				format(getJVM())).append(sep).append(format(getHiTime()))
				.append(sep).append(format(getLoTime())).append(sep).append(
						format(getCount())).toString();
	}

	/**
	 * 静态方法生成UUID
	 * @return UUID
	 */
	public static String newUUID() {
	    return new UUIDUtils().generateUUID();
	}
}