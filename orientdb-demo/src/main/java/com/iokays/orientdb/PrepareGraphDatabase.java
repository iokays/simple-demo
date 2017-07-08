package com.iokays.orientdb;

import java.sql.SQLException;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class PrepareGraphDatabase {
	
	private static final Logger logger = LoggerFactory.getLogger(PrepareGraphDatabase.class);
	
	private static final long min = 230021993155317760L;
	private static final int size = 1000000;
	private static final long max = min + size;
	
	public static void main(String[] args) throws SQLException {
		OrientGraphFactory factory = new OrientGraphFactory("remote:192.168.0.15/demo_1", "root", "root");
		factory.setSaveOriginalIds(true);
//		insertV(factory);
		insertE(factory);
	}
	
	public static void insertE(OrientGraphFactory factory) {
		final Random random = new Random(0);
		int n = 0;
		for (long i = min; i < max; i++) {
			OrientGraph graph = factory.getTx();
			try {
				final long startTime = System.currentTimeMillis();
				final Vertex out = graph.getVertexByKey("uid", i);
				for (int j = 0; j < random.nextInt(40) + 10; j++) {
					final Vertex in = graph.getVertexByKey("uid", min + (random.nextInt(size)));
					if (!out.equals(in)) {
						out.addEdge("E", in);
						in.addEdge("E", out);
					}
				}
				logger.info("i: {}, time: {}", i - min, System.currentTimeMillis() - startTime);
	        } finally {
	            graph.shutdown();
	        }
		}
		
//		long i = min;
//		int n = 0;
//		while (i < min + 100000) {
//			OrientGraph graph = factory.getTx();
//			try {
//	        	for (int j = 100; j > 0; j--) {
//	        		final long startTime = System.currentTimeMillis();
////	        		graph.commit();
//	        		final OrientVertex out = graph.getVertex("#39:" + (i - min));
//	        		for (int k = 0; k < 100; k++) {
//	        			final String outString = "#39:" + (random.nextInt(100000));
//						final OrientVertex in = graph.getVertex(outString);
//						if (!out.equals(in)) {
//							graph.addEdge("class:E,cluster:contact," + n++, out, in, "link");
//							graph.addEdge("class:E,cluster:contact," + n++, in, out, "link");
//						}
//	        		}
//	        		i++;
//	        		logger.info("i: {}, time: {}", i - min, System.currentTimeMillis() - startTime);
//	        	}
//	        } finally {
//	            graph.shutdown();
//	        }
//		}
		
		
	}
	
	public static void insertV(OrientGraphFactory factory) {
		for (long i = min; i < max; i++) {
			OrientGraph graph = factory.getTx();
			try {
				final long startTime = System.currentTimeMillis();
        		final OrientVertex vertex =  graph.addVertex("class:V," + i);
        		vertex.setProperty("uid", i);
        		logger.info("vertex: {}, n: {}, time: {}", vertex.getId(), i - min, System.currentTimeMillis() - startTime);
        		if (i % 1024 == 0) {
        			graph.commit();
        		}
	        } finally {
	            graph.shutdown();
	        }
		}
	}
}


//
////graph.createVertexType("Person");
////Vertex person = graph.addVertex("Person", "Person");
////person.setProperty("name", "Dzmitry");
////graph.commit();
//
//for (Vertex vertex : graph.getVertices()) {
//    logger.debug("Vertex: {}", vertex);
//    for (Edge edge : vertex.getEdges(Direction.BOTH)) {
//        logger.debug("Edge: {}", edge);
//    }
//
//    logger.debug("Property keys: {}", vertex.getPropertyKeys());
//}
//
//for (Edge edge : graph.getEdges()) {
//    logger.debug("Edge: {}", edge);
//}

//graph.addEdge(null, graph.getVertex("#13:0"), graph.getVertex("#13:0"), "link");