package com.iokays.orientdb;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

public class SelectGraphDatabase {

	private static final Logger logger = LoggerFactory.getLogger(PrepareGraphDatabase.class);

	public static void main(String[] args) throws SQLException {
		OrientGraphFactory factory = new OrientGraphFactory("remote:192.168.0.230/demo_1", "root", "10086");
		OrientGraph graph = factory.getTx();
		final long time = System.currentTimeMillis();
		final OrientVertex vertex = graph.getVertex("#9:0");
		if (null != vertex) {
			logger.info("Vertex: {}", vertex);
			for (Edge edge : vertex.getEdges(Direction.BOTH)) {
				logger.info("Edge: {}", edge);
			}
		}
		logger.info("end: {}", System.currentTimeMillis() - time);	
	}
}
