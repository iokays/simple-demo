/* 
 * Copyright (C), 2014-2016, 时代际客(深圳)软件有限公司
 * File Name: @(#)App.java
 * Encoding UTF-8
 * Author: gongqiao
 * Version: 3.0
 * Date: 2017年8月18日
 */
package com.iokays.habse;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;

/**
 * 功能描述
 * 
 * <p>
 * <a href="App.java"><i>View Source</i></a>
 * </p>
 * 
 * @author gongqiao
 * @version 3.0
 * @since 1.0
 */
public class App {
    public static void main(String[] args) throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
        System.out.println("hello");
        final Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "192.168.0.227");
        configuration.set("hbase.zookeeper.property.clientPort","2181");
        final HBaseAdmin hBaseAdmin = new HBaseAdmin(configuration);
        hBaseAdmin.tableExists("test");
        hBaseAdmin.close();
        System.out.println("end");
    }
}
