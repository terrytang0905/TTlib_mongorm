package com.newroad.mongodb.orm.db;

import java.util.List;
import java.util.Map;

/**
 * 数据库操作上下文
 */
public class DBContext {
	private String collection;
	private Map<String, Object> param;
	private List<Map<String, Object>> paramList;
	private Map<String, Object> action;
	//return field
	private Map<String, String> field=null;
	private Map<String, Integer> order=null;

	/* The maximum number of matching documents to return*/
	private Integer limit = 0;
	/* The number of matching documents to skip before returning results.*/
	private Integer skip = 0;
	
	/**
	 * findAndModify upset
	 */
	private Boolean returnNew = false;
	private Boolean upsert = false;
	
	public DBContext(String collection) {
		this.collection = collection;
	}

	public String getCollection() {
		return collection;
	}
	
	public Map<String, Object> getAction() {
		return action;
	}
	public void setAction(Map<String, Object> action) {
		this.action = action;
	}
	public Map<String, Object> getParam() {
		return param;
	}
	public void setParam(Map<String, Object> param) {
		this.param = param;
	}
	public List<Map<String, Object>> getParamList() {
		return paramList;
	}
	public void setParamList(List<Map<String, Object>> paramList) {
		this.paramList = paramList;
	}
	public Map<String, String> getField() {
		return field;
	}
	public void setField(Map<String, String> field) {
		this.field = field;
	}
	public Map<String, Integer> getOrder() {
		return order;
	}
	public void setOrder(Map<String, Integer> order) {
		this.order = order;
	}
	public Integer getSkip() {
		return skip;
	}
	public void setSkip(Integer skip) {
		this.skip = skip;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public Boolean getReturnNew() {
		return returnNew;
	}

	public void setReturnNew(Boolean returnNew) {
		this.returnNew = returnNew;
	}

	public Boolean getUpsert() {
		return upsert;
	}
	public void setUpsert(Boolean upsert) {
		this.upsert = upsert;
	}
}
