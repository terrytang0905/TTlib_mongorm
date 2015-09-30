package com.newroad.mongodb.orm.mapping;

import java.io.Serializable;
import java.util.List;

/**
 * @info  : 对象映射信息 
 * @author: tangzj
 * @data  : 2013-6-8
 * @since : 1.5
 */
public class Mapping implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8691886299505473312L;
	private String id;
	private Class<?> clazz;
	private List<MappingEntry> om; // 对象映射
	
	public Mapping(String id, Class<?> clazz, List<MappingEntry> om){
		this.id = id;
		this.clazz = clazz;
		this.om = om;
	}

	public String getId() {
		return id;
	}
	public Class<?> getClazz() {
		return clazz;
	}
	public List<MappingEntry> getOm() {
		return om;
	}
}
