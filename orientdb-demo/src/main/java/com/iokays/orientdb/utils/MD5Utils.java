package com.iokays.orientdb.utils;

import org.apache.commons.lang.StringUtils;

import com.google.common.hash.Hashing;

/**
 * MD5
 * 
 * <p>
 * <a href="MD5Utils.java"><i>View Source</i></a>
 * </p>
 * 
 * @author zhaoxunyong
 * @version 1.0
 * @since 1.0
 */
public class MD5Utils {
    /**
     * md5，保留16位小写
     * 
     * @param source source
     * @return md5 16位小写
     */
    public static String encode16(String source) {
        if (StringUtils.isBlank(source)) {
            return "";
        }
        return encode32(source).substring(8, 24);
    }

    /**
     * 字符串转md5编码，32位小写
     * 
     * @param source source
     * @return md5 32位小写
    */
    public static String encode32(String source) {
        if (StringUtils.isBlank(source)) {
            return source;
        }
        return Hashing.md5().hashBytes(source.getBytes()).toString();
    }

    /**
     * 将long值转为16位的md5
     * 
     * @param source source
     * @return md5 16位小写 
    */
    public static String encode16Long(Long source) {
        return encode16(source == null ? null : source.toString());
    }
    
    public static void main(String[] args) {
        System.out.println(MD5Utils.encode16("174917575989055488#1"));
    }
}
