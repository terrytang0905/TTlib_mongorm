package com.newroad.mongodb.orm;

import java.io.Serializable;
import java.util.Map;

import com.newroad.mongodb.orm.mapping.Mapping;

/**
 * @info  : mongo sql 配置 
 * @author: tangzj
 * @data  : 2013-5-31
 * @since : 1.5
 */
public class MongoSQLConfig implements Serializable {
	// For distributed transaction in MemCache or other storage
    private static final long serialVersionUID = 810933064847978671L;

	private String sqlId;
	private String collection;
	
	private Select select;
	private Insert insert;
	private Update update;
	private Delete delete;
	
	public MongoSQLConfig(String sqlId, String collection) {
		this.sqlId = sqlId;
		this.collection = collection;
	}
	
	public String getSqlId() {
		return sqlId;
	}
	public String getCollection() {
		return collection;
	}
	public Select getSelect() {
		return select;
	}
	public void setSelect(Select select) {
		this.select = select;
	}
	public Insert getInsert() {
		return insert;
	}
	public void setInsert(Insert insert) {
		this.insert = insert;
	}
	public Update getUpdate() {
		return update;
	}
	public void setUpdate(Update update) {
		this.update = update;
	}
	public Delete getDelete() {
		return delete;
	}
	public void setDelete(Delete delete) {
		this.delete = delete;
	}

	public class Select implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8875419509181025688L;
		private Mapping param;
		private Mapping result;
		
		private Map<String, Integer> order;

		private Integer skip = 0;
		private Integer limit = 0;
		
		public Mapping getParam() {
			return param;
		}
		public void setParam(Mapping param) {
			this.param = param;
		}
		public Mapping getResult() {
			return result;
		}
		public void setResult(Mapping result) {
			this.result = result;
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
	
	}

	public class Update implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -2133377119752678263L;
		private Mapping param;
		private Mapping action;
		private Mapping result;
		
		public Mapping getParam() {
			return param;
		}
		public void setParam(Mapping param) {
			this.param = param;
		}
		public Mapping getAction() {
			return action;
		}
		public void setAction(Mapping action) {
			this.action = action;
		}
		public Mapping getResult() {
			return result;
		}
		public void setResult(Mapping result) {
			this.result = result;
		}
	}
	
	public class Insert implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 6233654774747916595L;
		private Mapping param;

		public Mapping getParam() {
			return param;
		}
		public void setParam(Mapping param) {
			this.param = param;
		}
	}
	
	public class Delete implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8835403595302683129L;
		private Mapping param;

		public Mapping getParam() {
			return param;
		}
		public void setParam(Mapping param) {
			this.param = param;
		}
	}
	  
}
