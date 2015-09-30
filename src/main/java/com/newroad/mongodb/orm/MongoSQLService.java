package com.newroad.mongodb.orm;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.mongodb.orm.mapping.Mapping;


/**
 * @info  : mongo 生成 sql 定制服务  
 * @author: tangzj
 * @data  : 2013-5-31
 * @since : 1.5
 */
//Need to remove SynchronousInitial dependency
public class MongoSQLService {
	private static Logger log = LoggerFactory.getLogger(MongoSQLService.class);
	
	private MongoSQLLoader loader;
	private MongoSQLContext context;
	private MappingTransform transform;

	public void initial() {
		MongoSQLContext _context = loader.load();
		synchronized (this) {
			context = _context;
		}
		_context = null;
	}
	
	/**
	 * @param  sqlId     对应该sql的配置
	 */
	public MongoSQLConfig getConfig(String sqlId){
		try {
			return context.getConfig().get(sqlId);
		} catch(Exception e){
			log.error("sql 不存在, 请检查.  sqlId="+sqlId, e);
			throw new RuntimeException("sql 不存在, 请检查. sqlId="+sqlId, e);
		}
	}
	
	/**
	 * select param 转换
	 */
	public Map<String, Object> tsfsp(MongoSQLConfig config, Object obj) {
		return transform.tsfsp(context, config, obj);
	}

	/**
	 * select result 转换
	 */
	public Map<String, String> tsfsrb(MongoSQLConfig config, Object obj) {
		return transform.tsfsrb(context, config, obj);
	}
	
	/**
	 * select result 转换
	 */
	public Object tsfsra(MongoSQLConfig config, Object obj) {
		return transform.tsfsra(context, config, obj);
	}
	
	/**
	 * insert 块转换
	 */
	public List<Map<String, Object>> tsfi(MongoSQLConfig config, Object obj) {
		return transform.tsfi(context, config, obj);
	}
	
	/**
	 * update condition 块转换
	 */
	public Map<String, Object> tsfuc(MongoSQLConfig config, Map<String, Object> map) {
		return transform.tsfuc(context, config, map);
	}
	
	/**
	 * update action 块转换
	 */
	public Map<String, Object> tsfua(MongoSQLConfig config, Map<String, Object> map) {
		return transform.tsfua(context, config, map);
	}
	
	/**
	 * update condition 块转换
	 */
	public Map<String, Object> tsfuc(MongoSQLConfig config, Object queryObject) {
		return transform.tsfuc(context, config, queryObject);
	}
	
	/**
	 * update action 块转换
	 */
	public Map<String, Object> tsfua(MongoSQLConfig config, Object updateObject) {
		return transform.tsfua(context, config, updateObject);
	}
	
	/**
	 * delete 块转换
	 */
	public Map<String, Object> tsfd(MongoSQLConfig config, Object obj) {
		return transform.tsfd(context, config, obj);
	}
	
	/**
	 * 配置文件转换
	 */
	public Map<String, Object> transformConfig(Mapping mapping, Object obj) {
		return transform.transformConfig(context, mapping, obj);
	}
	
	/**
	 * update action 转换
	 */
	public Map<String, Object> transformAction(Mapping mapping, Object obj) {
		return transform.transformAction(context, mapping, obj);
	}
	
	/**
	 * 返回列转换
	 */
	public Map<String, String> transformReturnField(Mapping mapping, Object obj) {
		return transform.transformReturnField(context, mapping, obj);
	}
	
	/**
	 * 返回结果转换
	 */
	public Object transformReturnResult(Mapping mapping, Object obj) {
		return transform.transformReturnResult(context, mapping, obj);
	}
	
    public void setLoader(MongoSQLLoader loader) {
    	this.loader = loader;
    }
	public void setTransform(MappingTransform transform) {
		this.transform = transform;
	}
}
