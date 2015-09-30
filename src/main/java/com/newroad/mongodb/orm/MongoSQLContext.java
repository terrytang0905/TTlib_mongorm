package com.newroad.mongodb.orm;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.newroad.mongodb.orm.mapping.Mapping;

/**
 * @info  : Mongo SQL 解析下全局下文  运行时单实例
 * @author: tangzj
 * @data  : 2013-6-4
 * @since : 1.5
 */
public class MongoSQLContext {
	
	/**
	 * SQL 配置   KEY: SQL ID
	 */
	private Map<String, MongoSQLConfig> config;
	
	/**
	 * ORM
	 */
	private Map<String, Mapping> mapping;
	
	public MongoSQLConfig get(String key){
		if(MapUtils.isEmpty(config)) return null;
		return config.get(key);
	}
	
	public MongoSQLContext put(String key, MongoSQLConfig value){
		if(MapUtils.isEmpty(config))
			this.config = new HashMap<String, MongoSQLConfig>(100);
		config.put(key, value);
		return this;
	}
	
	public Mapping getOM(String key){
		if(MapUtils.isEmpty(mapping)) return null;
		return mapping.get(key);
	}
	
	public MongoSQLContext putOM(String key, Mapping value){
		if(MapUtils.isEmpty(mapping))
			this.mapping = new HashMap<String, Mapping>(50);
		mapping.put(key, value);
		return this;
	}
	
	public Map<String, MongoSQLConfig> getConfig() {
		return config;
	}
	public void setConfig(Map<String, MongoSQLConfig> config) {
		this.config = config;
	}
	public Map<String, Mapping> getMapping() {
		return mapping;
	}
	public void setMapping(Map<String, Mapping> mapping) {
		this.mapping = mapping;
	}
}
