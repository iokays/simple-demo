package com.iokays.orientdb.utils;

import java.util.Random;

import org.apache.commons.lang.StringUtils;


/**
 * id生成工具
 * 
 * <p>
 * <a href="IdUtils.java"><i>View Source</i></a>
 * </p>
 * 
 * @author liangjin
 * @version 1.0
 * @since 1.0
 */
public class IdUtils {
	private final static IdWorker2 idWorker = IdWorker2.getInstance();
    private final static int MIN = 100;
    private final static int MAX = 999;
    public final static String SPLIT = "_";

    public static int random() {
        return new Random().nextInt(MAX - MIN + 1) + MIN;
    }
    
    private static final int ID_LENGTH = 16;
    

    public static long getId() {
        return idWorker.getId();
    }

    /**
     * 生成规则为：uuid取md5后再截取16位
     * 
     * @return
     */
    @Deprecated
    public static String generateId() {
        return generateId(null);
    }

    @Deprecated
    public static String generateId(String id) {
        String uuid = null;
        if(StringUtils.isBlank(id)) {
            uuid = UUIDUtils.newUUID();
        } else {
            uuid = id;
        }
        String md5 = MD5Utils.encode16(uuid);
        if (md5.length() > ID_LENGTH) {
            md5 = md5.substring(0, ID_LENGTH);
        }
        return md5;
    }
    

    /**
     * 生成时间的唯一id，用当前时间精确到ms+3位随机数，再用long相减
     * 注意：全局的话，可能会重复，如果加上accountId的话，应该不会重复
     * 
     * @date date date
     * @return id
     */
    public static long generateReverseTimeIdByRandom(long date) {
        long dateLong = Long.MAX_VALUE-Long.parseLong(date + ""+ random());
        return dateLong;
    }
    
}