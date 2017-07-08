package com.iokays.demo;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class Demo {
	
	private static final Logger logger = LoggerFactory.getLogger(Demo.class);
	
	public static void main(String[] args) {
		logger.debug("main");
		HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
		Map customers = hazelcastInstance.getMap("customers");
		customers.put(1, "Joe");
		customers.put(2, "Ali");
		customers.put(3, "Avi");
		System.out.println("Customer with key 1: " + customers.get(1));
		System.out.println("Map Size:" + customers.size());
		Queue queueCustomers = hazelcastInstance.getQueue("customers");
		queueCustomers.offer("Tom");
		queueCustomers.offer("Mary");
		queueCustomers.offer("Jane");
		System.out.println("First customer: " + queueCustomers.poll());
		System.out.println("Second customer: " + queueCustomers.peek());
		System.out.println("Queue size: " + queueCustomers.size());
	}
}
