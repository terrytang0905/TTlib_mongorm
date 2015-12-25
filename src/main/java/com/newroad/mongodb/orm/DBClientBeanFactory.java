package com.newroad.mongodb.orm;

import java.util.List;

import org.springframework.core.io.Resource;

import com.newroad.mongodb.orm.db.client.DBCRUDClient;

/**
 * Mongo Client Bean Factory
 */
public class DBClientBeanFactory {
	/**
	 * SQL 文件
	 */
	private List<Resource> configLocations;
	
	/**
	 * DB客户端
	 */
	private DBCRUDClient dbCRUDClient;
	
	public List<Resource> getConfigLocations() {
		return configLocations;
	}

	public void setConfigLocations(List<Resource> configLocations) {
		this.configLocations = configLocations;
	}


	public DBCRUDClient getDbCRUDClient() {
		return dbCRUDClient;
	}

	public void setDbCRUDClient(DBCRUDClient dbCRUDClient) {
		this.dbCRUDClient = dbCRUDClient;
	}
}
