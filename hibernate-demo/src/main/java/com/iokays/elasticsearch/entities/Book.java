/* 
 * Copyright (C), 2014-2016, 时代际客(深圳)软件有限公司
 * File Name: @(#)Demo.java
 * Encoding UTF-8
 * Author: pengyuanbing
 * Version: 3.0
 * Date: 2016年6月22日
 */
package com.iokays.elasticsearch.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 功能描述
 * 
 * <p>
 * <a href="Demo.java"><i>View Source</i></a>
 * </p>
 * 
 * @author pengyuanbing
 * @version 3.0
 * @since 1.0
 */
@Document(indexName = "book", type = "book", shards = 5, replicas = 0, refreshInterval = "-1")
public class Book implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	private String name;
	@Field(type = FieldType.Object)
	private Author author;
	@Field(type = FieldType.Nested)
	private Map<Integer, Collection<String>> buckets = new HashMap<Integer, Collection<String>>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Map<Integer, Collection<String>> getBuckets() {
		return buckets;
	}

	public void setBuckets(Map<Integer, Collection<String>> buckets) {
		this.buckets = buckets;
	}

}
