package com.iokays.orientdb.utils;


import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaio.uuid.UUIDGen;

/**
 * http://www.lanindex.com/twitter-snowflake%EF%BC%8C64%E4%BD%8D%E8%87%AA%E5%A2%9Eid%E7%AE%97%E6%B3%95%E8%AF%A6%E8%A7%A3/
 * https://github.com/adyliu/idcenter/issues/1
 * tweeter的snowflake 移植到Java:
 *   (a) id构成: 42位的时间前缀 + 10位的节点标识 + 12位的sequence避免并发的数字(12位不够用时强制得到新的时间前缀)
 *       注意这里进行了小改动: snowkflake是5位的datacenter加5位的机器id; 这里变成使用10位的机器id
 *   (b) 对系统时间的依赖性非常强，需关闭ntp的时间同步功能。当检测到ntp时间调整后，将会拒绝分配id
 */

class IdWorker2 {

    private final static Logger logger = LoggerFactory.getLogger(IdWorker2.class);
    private final static byte[] mac = UUIDGen.getMACAddress().getBytes();

    private final long workerId;
    private final long epoch = 1419696000000L;   // 时间起始标记点，作为基准，一般取系统的最近时间
    private final long workerIdBits = 10L;      // 机器标识位数
    private final long maxWorkerId = -1L ^ -1L << this.workerIdBits;// 机器ID最大值: 1023
    private long sequence = 0L;                   // 0，并发控制
    private final long sequenceBits = 12L;      //毫秒内自增位

    private final long workerIdShift = this.sequenceBits;                             // 12
    private final long timestampLeftShift = this.sequenceBits + this.workerIdBits;// 22
    private final long sequenceMask = -1L ^ -1L << this.sequenceBits;                 // 4095,111111111111,12位
    private long lastTimestamp = -1L;
    
    private final static class IdWorkerBuilder {
    	private final static IdWorker2 BUILDER = new IdWorker2();
    }
    
    public static IdWorker2 getInstance(){
    	return IdWorkerBuilder.BUILDER;
    }
    
    private IdWorker2(){
    	this(getWorkId());
    }

    private IdWorker2(long workerId) {
        if (workerId > this.maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", this.maxWorkerId));
        }
        this.workerId = workerId;
    }
    
    public long getId() {
        long id = nextId();
        return id;
    }

    private synchronized long nextId() {
        long timestamp = this.timeGen();
        if (this.lastTimestamp == timestamp) { // 如果上一个timestamp与新产生的相等，则sequence加一(0-4095循环); 对新的timestamp，sequence从0开始
            this.sequence = this.sequence + 1 & this.sequenceMask;
            if (this.sequence == 0) {
                timestamp = this.tilNextMillis(this.lastTimestamp);// 重新生成timestamp
            }
        } else {
            this.sequence = 0;
        }

        if (timestamp < this.lastTimestamp) {
            logger.error(String.format("clock moved backwards.Refusing to generate id for %d milliseconds", (this.lastTimestamp - timestamp)));
            throw new RuntimeException(String.format("clock moved backwards.Refusing to generate id for %d milliseconds", (this.lastTimestamp - timestamp)));
        }

        this.lastTimestamp = timestamp;
        return timestamp - this.epoch << this.timestampLeftShift | this.workerId << this.workerIdShift | this.sequence;
    }

//    private static IdWorker flowIdWorker = new IdWorker(1);
//    public static IdWorker getFlowIdWorkerInstance() {
//        return flowIdWorker;
//    }
    


    protected static long getWorkId() {
//    	 private final long clockSeqAndNode = UUIDGen.getMACAddress();
        /*try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            long id;
            if (network == null) {
                id = 1;
            } else {
                byte[] mac = network.getHardwareAddress();
                id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
            }
            return id;
        } catch (SocketException|UnknownHostException e) {
            throw new RuntimeException(e);
        }*/
    	
        long id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
        return id;
    }


    /**
     * 等待下一个毫秒的到来, 保证返回的毫秒数在参数lastTimestamp之后
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    /**
     * 获得系统当前毫秒数
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }
    
    public static void main(String[] args) {
    	System.out.println(-1L ^ -1L << 10);
//		System.out.println(getWorkId());
		// get name representing the running Java virtual machine.  
		String name = ManagementFactory.getRuntimeMXBean().getName();  
		System.out.println(name);  
		// get pid  
		String pid = name.split("@")[0];  
		System.out.println("Pid is:" + pid); 
		
		System.out.println("mac is:" + UUIDGen.getMACAddress()); 
		byte[] macTemp = UUIDGen.getMACAddress().getBytes();
		System.out.println(macTemp.length);
		System.out.println((long) macTemp[macTemp.length - 1]);
		System.out.println((long) macTemp[macTemp.length - 2]);
		try {
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("ip="+ip);
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            long id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
            System.out.println(id+"/"+new String(mac,Charsets.UTF_16LE));
            
//            for(byte b:mac){
//            	System.out.println(b);
//            }
            mac = UUIDGen.getMACAddress().getBytes();
            for(byte b:mac){
            	System.out.println(b);
            }
            id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
            System.out.println(id);
        } catch (SocketException|UnknownHostException e) {
            throw new RuntimeException(e);
        }
	}
}
