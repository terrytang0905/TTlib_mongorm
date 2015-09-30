package com.newroad.mongodb.orm.db;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.newroad.mongodb.orm.mapping.QueryEntry;
import com.mongodb.AggregationOutput;
import com.mongodb.DBObject;

/**
 * @author tangzj1
 * @version 2.0
 * @since Apr 11, 2014
 */
public interface MongoDaoIf {

	public Object runCommand(Map<String, Object> queryObj);

	public Object getStats();

	/**
	 * @param sqlId
	 * @param obj
	 * @return
	 */
	public Object selectOne(String sqlId, Object queryObj);

	/**
	 * @param sqlId
	 * @param collection
	 * @param obj
	 * @return
	 */
	public Object selectOne(String sqlId, String collection, Object queryObj);

	/**
	 * @param sqlId
	 * @param obj
	 * @param limitRecord
	 * @param skipRecord
	 * @return
	 */
	public List<?> selectList(String sqlId, Object queryObj,
			Integer limitRecord, Integer skipRecord);

	/**
	 * @param sqlId
	 * @param collection
	 * @param obj
	 * @param limitRecord
	 * @param skipRecord
	 * @return
	 */
	public List<?> selectList(String sqlId, String collection, Object queryObj,
			Integer limitRecord, Integer skipRecord);

	/**
	 * @param sqlId
	 * @param obj
	 * @param limitRecord
	 * @param skipRecord
	 * @return
	 */
	public long count(String sqlId, Object queryObj, Integer limitRecord,
			Integer skipRecord);

	/**
	 * @param sqlId
	 * @param collection
	 * @param obj
	 * @param limitRecord
	 * @param skipRecord
	 * @return
	 */
	public long count(String sqlId, String collection, Object obj,
			Integer limitRecord, Integer skipRecord);

	public List<?> distinct(String sqlId, Object queryObj);

	/**
	 * @param sqlId
	 * @param obj
	 * @return
	 */
	public String insert(String sqlId, Object newObj);

	/**
	 * @param sqlId
	 * @param collection
	 * @param obj
	 * @return
	 */
	public String insert(String sqlId, String collection, Object newObj);

	/**
	 * @param sqlId
	 * @param list
	 * @return
	 */
	public List<String> insertBatch(String sqlId, List<?> insertList);

	/**
	 * @param sqlId
	 * @param collection
	 * @param list
	 * @return
	 */
	public List<String> insertBatch(String sqlId, String collection,
			List<?> insertList);

	/**
	 * find the specific document and modify it.
	 * 
	 * @param sqlId
	 * @param map
	 * @return
	 */
	public Object findAndModify(String sqlId, Map<String, Object> queryMap);

	/**
	 * find the specific document and modify it.
	 * 
	 * @param sqlId
	 * @param collection
	 * @param map
	 * @return
	 */
	public Object findAndModify(String sqlId, String collection,
			Map<String, Object> queryMap);

	/**
	 * update one document or many documents
	 */
	public int update(String sqlId, Map<String, Object> dataMap);

	/**
	 * update one document or many documents
	 */
	public int update(String sqlId, String collection,
			Map<String, Object> dataMap);

	/**
	 * @param sqlId
	 * @param obj
	 * @return
	 */
	public int delete(String sqlId, Object queryObj);

	/**
	 * @param sqlId
	 * @param collection
	 * @param obj
	 * @return
	 */
	public int delete(String sqlId, String collection, Object queryObj);

	/**
	 * update mongo documents based on sql config for sqlId
	 * 
	 * @param sqlId
	 *            Sql config id
	 * @param query
	 *            Select query
	 * @param update
	 *            Update object
	 * @return update row count
	 */
	public int update(String sqlId, Object query, Object update);

	/**
	 * update mongo documents based on sql config for sqlId
	 * 
	 * @param sqlId
	 *            Sql config id
	 * @param collection
	 *            MongoDB collection
	 * @param query
	 *            Select query
	 * @param update
	 *            Update object
	 * @return update row count
	 */
	public int update(String sqlId, String collection, Object query,
			Object update);

	/**
	 * find and modify mongo documents based on sql config for sqlId
	 * 
	 * @param sqlId
	 *            Sql config id
	 * @param query
	 *            Select query
	 * @param update
	 *            Update object
	 * @return update row count
	 */
	public Object findAndModify(String sqlId, Object query, Object update);

	/**
	 * find and modify mongo documents based on sql config for sqlId
	 * 
	 * @param sqlId
	 *            Sql config id
	 * @param collection
	 *            MongoDB collection
	 * @param query
	 *            Select query
	 * @param update
	 *            Update object
	 * @return update row count
	 */
	public Object findAndModify(String sqlId, String collection, Object query,
			Object update);

	/**
	 * find and modify mongo documents based on sql config for sqlId
	 * 
	 * @param sqlId
	 *            Sql config id
	 * @param collection
	 *            MongoDB collection
	 * @param query
	 *            Select query
	 * @param update
	 *            Update object
	 * @param upsert
	 * @return update row count
	 */
	public Object findAndModify(String sqlId, Object query, Object update,
			boolean returnNew, boolean upsert);

	/**
	 * find and modify mongo documents based on sql config for sqlId
	 * 
	 * @param sqlId
	 *            Sql config id
	 * @param collection
	 *            MongoDB collection
	 * @param query
	 *            Select query
	 * @param update
	 *            Update object
	 * @param upsert
	 * @return update row count
	 */
	public Object findAndModify(String sqlId, String collection, Object query,
			Object update, boolean returnNew, boolean upsert);

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
	
    
    /**
     * @param sqlId
     * @param queryObj
     * @return
     */
    public Object selectOneByQuery(String sqlId, Object queryObj, List<QueryEntry> querys);
	
    /**
     * @param sqlId
     * @param queryObj
     * @param limitRecord
     * @param skipRecord
     * @return
     */
    public List<?> selectListByQuery(String sqlId, Object queryObj, Integer limitRecord, Integer skipRecord, List<QueryEntry> querys);

    /**
     * @param sqlId
     * @param queryObj
     * @param querys
     * @return
     */
    public long countByQuery(String sqlId, Object queryObj, List<QueryEntry> querys);
}
