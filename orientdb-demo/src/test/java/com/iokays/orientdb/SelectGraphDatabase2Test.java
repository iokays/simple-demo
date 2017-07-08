package com.iokays.orientdb;

import java.sql.SQLException;
import java.util.Random;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class SelectGraphDatabase2Test {
	
	private static final Logger LOGGERE = LoggerFactory.getLogger(SelectGraphDatabase2Test.class);

	@Test
	public void test1() throws SQLException {
		OrientGraphFactory factory = new OrientGraphFactory("remote:192.168.0.16/demo_3", "root", "root");
		factory.setSaveOriginalIds(true);
		OrientGraph graph = factory.getTx();
		graph.addVertex("class:client,cluster:client_usa,id_001_");
		graph.commit();
		graph.shutdown();
	}
	
	@Test
	public void test2() {
		final String[] urls = new String[] {"192.168.0.16", "192.168.0.15", "192.168.0.229"};
		final Long[] ids = new Long[] {3L, 4L, 5L};
		for (int i = 0; i < urls.length; ++i) {
			final String url = urls[i];
			OrientGraphFactory factory = new OrientGraphFactory("remote:" + url + "/demo_3", "root", "root");
			factory.setSaveOriginalIds(true);
			OrientGraph graph = factory.getTx();
			final OrientVertex vertex = graph.addVertex("_id_2016_09_27_09_12");
			vertex.setProperty("uid", ids[i]);
			graph.commit();
			graph.shutdown();
		}
	}
	
	@Test
	public void testRandomV() {
		final Random random = new Random(System.currentTimeMillis());
		int n = 0;
		while (n < 1000) {
			final String[] urls = new String[] {"192.168.0.15", "192.168.0.16", "192.168.0.229"};
			final String url = urls[random.nextInt(urls.length)];
			OrientGraphFactory factory = new OrientGraphFactory("remote:" +url+ "/demo_3", "root", "root");
			factory.setSaveOriginalIds(true);
			OrientGraph graph = factory.getTx();
			final long uid =  random.nextLong();
			final OrientVertex vertex = graph.addVertex("" + uid);
			vertex.setProperty("uid", uid);
			LOGGERE.info("url: {}, uid: {}", url, uid);
			graph.commit();
			graph.shutdown();
			factory.close();
			n++;
		}
	}
	
	@Test
	public void testRandomE() {
		final Random random = new Random(System.currentTimeMillis());
		int n = 0;
		while (n < 1000) {
			final String[] urls = new String[] {"192.168.0.15", "192.168.0.16"};
			final String url = urls[random.nextInt(urls.length)];
			OrientGraphFactory factory = new OrientGraphFactory("remote:" + url + "/demo_3", "root", "root");
			factory.setSaveOriginalIds(true);
			OrientGraph graph = factory.getTx();
			
			final Vertex out = graph.getVertexByKey("uid", 9024856508384091635L);
			final Vertex in = graph.getVertexByKey("uid", 8823953986596432123L);
			
			final String lable = "friend3";
			LOGGERE.info("url: {}, lable: {}", url, lable);
			out.addEdge(lable, in);
			graph.commit();
			graph.shutdown();
			factory.close();
			n++;
		}
	}
	
	
	
	@Test
	public void testClass() {
//		OClass client = database.getMetadata().getSchema().createClass("Client");
	}
}
