package com.iokays.orientdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.jdbc.OrientJdbcConnection;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public class SelectGraphDatabase2 {

	private static final Logger logger = LoggerFactory.getLogger(PrepareGraphDatabase.class);
	
	public static final void main(String[] args) throws SQLException {
		long start = System.currentTimeMillis();
		Properties info = new Properties();
		info.put("user", "root");
		info.put("password", "Aa123456");
		Connection conn = (OrientJdbcConnection) DriverManager.getConnection("jdbc:orient:remote:192.168.0.15,192.168.0.15/demo_3",
				info);
		
		long accountId = 230021993155317760L;
		
		for (int i = 0; i < 100; i++) {
			logger.info("------------------------------------------------");
			accountId += i;
			start = System.currentTimeMillis();
			List<Long> _1_0 = friendAccountId(accountId);
			logger.info("_1_0: {}, time: {}, size: {}", 0, System.currentTimeMillis() - start, _1_0.size());
			
			start = System.currentTimeMillis();
			List<Long> _0 = friend2_1AccountIds(conn, accountId);
			logger.info("_2_0: {}, time: {}, size: {}", 0, System.currentTimeMillis() - start, _0.size());
			
			start = System.currentTimeMillis();
			Map<Long, Integer> _1 = friend2_2AccountIds(conn, accountId);
			logger.info("_2_1: {}, time: {}, size: {}", 1, System.currentTimeMillis() - start, _1.size());
			logger.info("------------------------------------------------");
		}
	}

	private static void graph() {
		OrientGraphFactory factory = new OrientGraphFactory("remote:192.168.0.15/demo_3", "root", "Aa123456");
		
		OrientGraph graph = factory.getTx();
		
		final long time = System.currentTimeMillis();
		final Vertex vertex = graph.getVertexByKey("accountId", 144428483040034816L);
		logger.info("vertex: {}, time: {}", vertex, System.currentTimeMillis() - time);
		final Iterable<Edge> edegs = vertex.getEdges(Direction.OUT, "link");
		for (Edge edge : edegs) {
			logger.info("in: {}, out: {}", edge.getVertex(Direction.IN).getProperty("accountId"),
					edge.getVertex(Direction.OUT).getProperty("accountId"));
		}
		graph.shutdown();
	}

	private static void graph2() {
		OrientGraphFactory factory = new OrientGraphFactory("remote:192.168.0.15/demo_3", "root", "Aa123456");
		OrientGraph graph = factory.getTx();
		List<ODocument> list = graph.getRawGraph().query(new OSQLSynchQuery("select from V limit 10").execute());
		for (ODocument oDocument : list) {
			logger.info("oDocument.accountId: {}", oDocument.getOriginalValue("accountId"));
		}
		graph.shutdown();
	}

	// 一度好友
	private static List<Long> friendAccountId(final long accountId) throws SQLException {
		Properties info = new Properties();
		info.put("user", "root");
		info.put("password", "Aa123456");
		Connection conn = (OrientJdbcConnection) DriverManager.getConnection("jdbc:orient:remote:192.168.0.15/demo_3",
				info);
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(
				"SELECT accountId, OUT('link').accountId AS friendAccountId FROM V where accountId = " + accountId);
		rs.next();
		List<Long> friendAccountIds = new Gson().fromJson(rs.getString("friendAccountId"), new TypeToken<List<Long>>() {
		}.getType());
		rs.close();
		stmt.close();
		return friendAccountIds;
	}

	// 一度好友
	private static void friend1AccountIds(final long accountId) throws SQLException {
		Properties info = new Properties();
		info.put("user", "root");
		info.put("password", "Aa123456");
		Connection conn = (OrientJdbcConnection) DriverManager.getConnection("jdbc:orient:remote:192.168.0.15/demo_3",
				info);
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(
				"MATCH {class: people, as: people, where: (accountId = " +accountId+ ")} RETURN people.accountId as accountId");
		rs.next();
		logger.info("@version: {}", rs.getInt("@version"));
		logger.info("@class: {}", rs.getString("@class"));
		logger.info("@rid: {}", rs.getString("@rid"));
		logger.info("accountId: {}", rs.getLong("accountId"));
		rs.close();
		stmt.close();
	}

	// 二度好友
	private static List<Long> friend2_1AccountIds(Connection conn, final long accountId) throws SQLException {
		List<Long> friendAccountIds = Lists.newArrayList();
		Statement stmt = conn.createStatement();
		String sql = "MATCH {class: people, as: people, where: (accountId = " +accountId+ ")}.both('friend').both('friend') {as: friendOfFriend, where: ($matched.people != $currentMatch)} RETURN people.accountId as accountId, friendOfFriend.accountId as friendOfFriendId";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			long friendOfFriendId = rs.getLong("friendOfFriendId");
			friendAccountIds.add(friendOfFriendId);
		}
		rs.close();
		stmt.close();
		return friendAccountIds;
	}

	// 二度好友
	private static Map<Long, Integer> friend2_2AccountIds(Connection conn, final long accountId) throws SQLException {
		Map<Long, Integer> map = Maps.newHashMap();
		Statement stmt = conn.createStatement();
		String sql = "SELECT accountId, OUT('friend').OUT('friend').accountId AS friendOfFriendId FROM people where accountId = " + accountId;
		ResultSet rs = stmt.executeQuery(sql);
		rs.next();
		List<Long> friendAccountIds = new Gson().fromJson(rs.getString("friendOfFriendId"), new TypeToken<List<Long>>() {}.getType());
		rs.close();
		stmt.close();
		
		for (Long friendAccountId : friendAccountIds) {
			map.put(friendAccountId, null != map.get(friendAccountId) ? map.get(friendAccountId) + 1 : 1);
		}
		return map;
	}
}
