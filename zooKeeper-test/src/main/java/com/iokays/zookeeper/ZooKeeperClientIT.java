package com.iokays.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZooKeeperClientIT {

	public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
		ZooKeeper zk = new ZooKeeper("192.168.0.230:2181", 300000, new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				System.out.println("----------->");
				System.out.println("path:" + event.getPath());
				System.out.println("type:" + event.getType());
				System.out.println("stat:" + event.getState());
				System.out.println("<-----------");
			}
		});

		String node = "/app1";
		Stat stat = zk.exists(node, false);// 检测/app1是否存在
		if (stat == null) {
			// 创建节点
			String createResult = zk.create(node, "test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
					CreateMode.PERSISTENT);
			System.out.println(createResult);
		}
		// 获取节点的值
		byte[] b = zk.getData(node, false, stat);
		System.out.println(new String(b));
		zk.close();
	}
}
