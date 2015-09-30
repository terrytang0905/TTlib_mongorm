package com.newroad.mongodb.orm.db.client;

import java.util.List;

import net.sf.json.JSONObject;

import com.newroad.mongodb.orm.db.DBContext;
import com.mongodb.AggregationOutput;
import com.mongodb.DBObject;

/**
 * @info mongo client api
 * @author tangzj1
 * @date Sep 16, 2013
 * @version
 */
public interface DBCRUDClient {

	/**
	 * @return JSONObject
	 */
	public JSONObject getStats();

	/**
	 * @param context
	 * @return JSONObject
	 */
	public JSONObject runCommand(DBContext context);

	/**
	 * 查询单个结果
	 * 
	 * @param context
	 * @return JSONObject
	 */
	public JSONObject selectOne(DBContext context);

	/**
	 * 查询列表
	 * 
	 * @param context
	 * @return List<JSONObject>
	 */
	public List<JSONObject> selectList(DBContext context);

	/**
	 * count
	 * 
	 * @param context
	 * @return long
	 */
	public long count(DBContext context);

	/**
	 * distinct
	 * 
	 * @param context
	 * @return
	 */
	public List<?> distinct(DBContext context);

	/**
	 * aggregate
	 * 
	 * @param context
	 * @return
	 */
	public List<JSONObject> aggregate(DBContext context);

	/**
	 * 单条插入
	 * 
	 * @param context
	 * @return
	 */
	public String insert(DBContext context);

	/**
	 * 批量插入
	 * 
	 * @param context
	 * @return
	 */
	public List<String> insertBatch(DBContext context);

	/**
	 * 更新
	 * 
	 * @param context
	 * @return
	 */
	public int update(DBContext context);

	/**
	 * find & modify
	 * 
	 * @param context
	 * @return
	 */
	public JSONObject findAndModify(DBContext context);

	/**
	 * 删除
	 * 
	 * @param context
	 * @return
	 */
	public int delete(DBContext context);

	/**
	 * find all documents by json query
	 * 
	 * @param collection
	 *            mongo collection
	 * @param query
	 *            query json
	 * @param field
	 *            return field
	 * @param sort
	 *            query order by
	 * @return query result
	 */
	public JSONObject queryByJson(String collection, JSONObject query,
			JSONObject field, JSONObject sort);

	/**
	 * find all documents by json query
	 * 
	 * @param collection
	 *            mongo collection
	 * @param query
	 *            query json
	 * @param field
	 *            return field
	 * @param sort
	 *            query order by
	 * @param skip
	 *            the number of matching documents to skip before returning
	 *            results
	 * @param limit
	 *            the maximum number of matching documents to return
	 * @return query result
	 */
	public List<JSONObject> queryListByJson(String collection,
			JSONObject query, JSONObject field, JSONObject sort, int skip,
			int limit);

	/**
	 * update documents by json query
	 * 
	 * @param collection
	 *            mongo collection
	 * @param query
	 *            query json
	 * @param updatge
	 * @return
	 */
	public int updateByJson(String collection, JSONObject query,
			JSONObject update);

	/**
	 * delete documents by json query
	 * 
	 * @param collection
	 *            mongo collection
	 * @param query
	 *            query json
	 * @return
	 */
	public int deleteByJson(String collection, JSONObject query);

	/**
	 * insert document by json data
	 * 
	 * @param collection
	 *            mongo collection
	 * @param insertData
	 *            insert json data
	 * @return
	 */
	public JSONObject insertByJson(String collection, JSONObject insertData);

	/**
	 * find and modify documents by json query
	 * 
	 * @param collection
	 *            mongo collection
	 * @param query
	 *            query json
	 * @param update
	 *            update json
	 * @param upsert
	 * @return
	 */
	public JSONObject findAndModifyByJson(String collection, JSONObject query,
			JSONObject update, JSONObject field, boolean upsert);

	/**
	 * group
	 * @param collection
	 * @param key
	 * @param cond
	 * @param initial
	 * @param reduce
	 * @return
	 */
  public DBObject group(String collection, DBObject key, DBObject cond, DBObject initial, String reduce);

  /**
   * aggregate
   * @param collection
   * @param firstOp
   * @param additionalOps
   * @return
   */
  public AggregationOutput aggregate(String collection, DBObject firstOp, DBObject[] additionalOps);

}
