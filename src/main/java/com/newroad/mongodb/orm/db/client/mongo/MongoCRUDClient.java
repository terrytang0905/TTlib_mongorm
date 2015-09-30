package com.newroad.mongodb.orm.db.client.mongo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.bson.types.ObjectId;

import com.newroad.mongodb.orm.db.DBContext;
import com.newroad.mongodb.orm.db.client.DBCRUDClient;
import com.newroad.mongodb.orm.exception.MongoDaoException;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

/**
 * @info : Mongo 客户端
 * @author: tangzj1
 * @data : 2013-10-10
 * @since : 2.0
 */
public class MongoCRUDClient implements DBCRUDClient {

  private MongoManager mongoManager;

  public void setMongoManager(MongoManager mongoManager) {
    this.mongoManager = mongoManager;
  }

  public JSONObject getStats() {
    DB db = mongoManager.getDB();
    CommandResult result = db.getStats();
    return JSONObject.fromObject(result);
  }

  public JSONObject runCommand(DBContext context) {
    DB db = mongoManager.getDB();
    Map<String, Object> param = context.getParam();
    DBObject queryDbo = (DBObject) new BasicDBObject();
    queryDbo.putAll(param);

    CommandResult result = db.command(queryDbo);
    return JSONObject.fromObject(result);
  }

  public JSONObject selectOne(DBContext context) {
    DB db = mongoManager.getDB();
    // 获取collection
    DBCollection coll = db.getCollection(context.getCollection());

    Map<String, Object> param = context.getParam();
    DBObject queryDbo = (DBObject) new BasicDBObject();
    queryDbo.putAll(param);

    Map<String, String> field = context.getField();
    DBObject fieldDbo = null;
    if (field != null) {
      fieldDbo = (DBObject) new BasicDBObject();
      fieldDbo.putAll(field);
    }

    Map<String, Integer> orderMap = context.getOrder();
    DBObject orderDbo = null;
    if (orderMap != null) {
      orderDbo = (DBObject) new BasicDBObject();
      orderDbo.putAll(orderMap);
    }

    ReadPreference preference = ReadPreference.secondaryPreferred();
    coll.setReadPreference(preference);
    DBObject r = translateId2String(coll.findOne(queryDbo, fieldDbo, orderDbo));
    return JSONObject.fromObject(r);
  }

  public List<JSONObject> selectList(DBContext context) {
    DB db = mongoManager.getDB();
    // 获取collection
    DBCollection coll = db.getCollection(context.getCollection());

    Map<String, Object> param = context.getParam();
    DBObject queryDbo = (DBObject) new BasicDBObject();
    queryDbo.putAll(param);

    Map<String, String> field = context.getField();
    DBObject fieldDbo = null;
    if (field != null) {
      fieldDbo = (DBObject) new BasicDBObject();
      fieldDbo.putAll(field);
    }

    ReadPreference preference = ReadPreference.secondaryPreferred();
    coll.setReadPreference(preference);

    Map<String, Integer> orderMap = context.getOrder();
    DBCursor cursor = null;
    if (orderMap != null) {
      DBObject orderDbo = (DBObject) new BasicDBObject();
      orderDbo.putAll(orderMap);
      cursor = coll.find(queryDbo, fieldDbo).sort(orderDbo).limit(context.getLimit()).skip(context.getSkip());
    } else {
      cursor = coll.find(queryDbo, fieldDbo).limit(context.getLimit()).skip(context.getSkip());
    }

    try {
      List<JSONObject> list = new ArrayList<JSONObject>();
      while (cursor.hasNext()) {
        list.add(JSONObject.fromObject(translateId2String(cursor.next())));
      }
      return list;
    } finally {
      cursor.close();
    }
  }

  public long count(DBContext context) {
    DB db = mongoManager.getDB();
    // 获取collection
    DBCollection coll = db.getCollection(context.getCollection());

    Map<String, Object> param = context.getParam();
    DBObject queryDbo = (DBObject) new BasicDBObject();
    queryDbo.putAll(param);

    Map<String, String> field = context.getField();
    DBObject fieldDbo = null;
    if (field != null) {
      fieldDbo = (DBObject) new BasicDBObject();
      fieldDbo.putAll(field);
    }

    ReadPreference preference = ReadPreference.secondaryPreferred();
    coll.setReadPreference(preference);
    
    long count = coll.getCount(queryDbo, fieldDbo, context.getLimit(), context.getSkip());
    return count;
  }

  public List<?> distinct(DBContext context) {
    DB db = mongoManager.getDB();
    // 获取collection
    DBCollection coll = db.getCollection(context.getCollection());

    Map<String, Object> param = context.getParam();
    DBObject queryDbo = (DBObject) new BasicDBObject();
    queryDbo.putAll(param);

    Map<String, String> field = context.getField();
    if (field == null) {
      throw new MongoDaoException("The key in mongo distinct query is null!");
    }
    int fieldSize = field.size();
    if (fieldSize != 1) {
      throw new MongoDaoException("The key size " + fieldSize + " in mongo distinct query is incorrect!");
    }
    String key = null;
    for (Map.Entry<String, String> entry : field.entrySet()) {
      key = entry.getKey();
    }

    Map<String, Integer> orderMap = context.getOrder();
    DBObject orderDbo = null;
    if (orderMap != null) {
      orderDbo = (DBObject) new BasicDBObject();
      orderDbo.putAll(orderMap);
    }
    
    ReadPreference preference = ReadPreference.secondaryPreferred();
    coll.setReadPreference(preference);

    List<?> keyList = coll.distinct(key, queryDbo);
    return keyList;
  }

  public List<JSONObject> aggregate(DBContext context) {
    DB db = mongoManager.getDB();
    // 获取collection
    DBCollection coll = db.getCollection(context.getCollection());

    Map<String, Object> param = context.getParam();
    DBObject firstDbo = (DBObject) new BasicDBObject();
    firstDbo.putAll(param);

    List<Map<String, Object>> paramList = context.getParamList();
    int dboSize = paramList.size();
    DBObject[] dboArray = new DBObject[dboSize];
    for (int i = 0; i < paramList.size(); i++) {
      DBObject dbo = (DBObject) new BasicDBObject();
      dbo.putAll(paramList.get(i));
      dboArray[i] = dbo;
    }
    
    ReadPreference preference = ReadPreference.secondaryPreferred();
    coll.setReadPreference(preference);

    AggregationOutput result = coll.aggregate(firstDbo, dboArray);

    List<JSONObject> jsonList = new ArrayList<JSONObject>();
    Iterable<DBObject> iterable = result.results();
    Iterator<DBObject> iterator = iterable.iterator();
    while (iterator.hasNext()) {
      JSONObject json = JSONObject.fromObject(iterator.next());
      jsonList.add(json);
    }

    return jsonList;
  }

  public String insert(DBContext context) {
    DB db = mongoManager.getDB();
    // 获取collection
    DBCollection coll = db.getCollection(context.getCollection());

    List<Map<String, Object>> inserts = context.getParamList();
    List<DBObject> list = new ArrayList<DBObject>(inserts.size());
    for (Map<String, Object> map : inserts) {
      DBObject dbobj = (DBObject) new BasicDBObject();
      dbobj.putAll(map);
      list.add(dbobj);
    }

    CommandResult result = coll.insert(list, WriteConcern.SAFE).getLastError(WriteConcern.SAFE);
    if (result.getException() != null) {
      throw new MongoDaoException("Mongo Insert Exception:" + result.getErrorMessage());
    }
    DBObject dbo = list.get(0);
    return (String) translateId2String(dbo).get("_id");
  }

  public List<String> insertBatch(DBContext context) {
    DB db = mongoManager.getDB();
    // 获取collection
    DBCollection coll = db.getCollection(context.getCollection());

    List<Map<String, Object>> inserts = context.getParamList();
    List<DBObject> list = new ArrayList<DBObject>(inserts.size());
    for (Map<String, Object> map : inserts) {
      DBObject dbobj = (DBObject) new BasicDBObject();
      dbobj.putAll(map);
      list.add(dbobj);
    }

    WriteResult insertResult = coll.insert(list, WriteConcern.SAFE);
    CommandResult cresult = insertResult.getLastError(WriteConcern.SAFE);
    if (cresult.getException() != null) {
      throw new MongoDaoException("Mongo InsertBatch Exception:" + cresult.getErrorMessage());
    }
    List<String> result = new ArrayList<String>(list.size());
    for (DBObject dbo : list) {
      result.add((String) translateId2String(dbo).get("_id"));
    }
    return result;
  }

  public int update(DBContext context) {
    DB db = mongoManager.getDB();
    DBCollection coll = db.getCollection(context.getCollection());

    Map<String, Object> param = context.getParam();

    DBObject dbo = new BasicDBObject();
    dbo.putAll(param);

    DBObject action = new BasicDBObject(context.getAction());
    WriteResult wresult = coll.update(dbo, action, false, true, WriteConcern.SAFE);
    CommandResult cresult = wresult.getLastError(WriteConcern.SAFE);
    if (cresult.getException() != null) {
      throw new MongoDaoException("Mongo Update Exception:" + cresult.getErrorMessage());
    }
    return wresult.getN();
  }

  public JSONObject findAndModify(DBContext context) {
    DB db = mongoManager.getDB();
    DBCollection coll = db.getCollection(context.getCollection());

    Map<String, Object> param = context.getParam();

    DBObject dbo = new BasicDBObject();
    dbo.putAll(param);

    DBObject action = new BasicDBObject(context.getAction());
    DBObject field = null;
    if (context.getField() != null) {
      field = new BasicDBObject(context.getField());
    }

    DBObject object = coll.findAndModify(dbo, field, null, false, action, context.getReturnNew(), context.getUpsert());
    if (object == null) {
      return null;
    }
    DBObject returnObj = translateId2String(object);
    return JSONObject.fromObject(returnObj);
  }

  public int delete(DBContext context) {
    DB db = mongoManager.getDB();
    DBCollection coll = db.getCollection(context.getCollection());

    DBObject param = new BasicDBObject(context.getParam());
    WriteResult dresult = coll.remove(param, WriteConcern.SAFE);
    CommandResult cresult = dresult.getLastError(WriteConcern.SAFE);
    if (cresult.getException() != null) {
      throw new MongoDaoException("Mongo Delete Exception:" + cresult.getErrorMessage());
    }
    return dresult.getN();
  }

  /**
   * 将作为查询返回结果的DBObject对象中的字段_id类型由{@link ObjectId}转为String
   * 
   * @param dbo
   * @return
   */
  private DBObject translateId2String(DBObject dbo) {
    if (dbo != null && dbo.containsField("_id") && dbo.get("_id") instanceof ObjectId)
      dbo.put("_id", ((ObjectId) dbo.get("_id")).toString());
    return dbo;
  }

  /**
   * query one ducument by json query
   */
  @Override
  public JSONObject queryByJson(String collection, JSONObject query, JSONObject field, JSONObject sort) {
    DB db = mongoManager.getDB();
    DBCollection coll = db.getCollection(collection);

    ReadPreference preference = ReadPreference.secondaryPreferred();
    coll.setReadPreference(preference);
    
    DBObject queryDbo = new BasicDBObject(query);
    DBObject fieldDbo = new BasicDBObject(field);
    DBObject result = null;
    if (sort != null && sort.size() > 0) {
      DBObject sortDbo = new BasicDBObject(sort);
      result = coll.findOne(queryDbo, fieldDbo, sortDbo);
    } else {
      result = coll.findOne(queryDbo, fieldDbo);
    }

    return JSONObject.fromObject(result);
  }

  /**
   * query ducument list by json query
   */
  @Override
  public List<JSONObject> queryListByJson(String collection, JSONObject query, JSONObject field, JSONObject sort, int skip, int limit) {
    DB db = mongoManager.getDB();
    DBCollection coll = db.getCollection(collection);

    ReadPreference preference = ReadPreference.secondaryPreferred();
    coll.setReadPreference(preference);
    
    DBCursor cursor = null;
    DBObject queryDbo = new BasicDBObject(query);
    DBObject fieldDbo = new BasicDBObject(field);
    if (sort != null && sort.size() > 0) {
      DBObject sortDbo = new BasicDBObject(sort);
      cursor = coll.find(queryDbo, fieldDbo).sort(sortDbo).limit(limit).skip(skip);
    } else {
      cursor = coll.find(queryDbo, fieldDbo).limit(limit).skip(skip);
    }

    List<JSONObject> result = new ArrayList<JSONObject>();
    while (cursor.hasNext()) {
      result.add(JSONObject.fromObject(cursor.next()));
    }
    return result;
  }
  
  @Override
  public DBObject group(String collection, DBObject key, DBObject cond, DBObject initial, String reduce) {
    DB db = mongoManager.getDB();
    DBCollection coll = db.getCollection(collection);
    
    DBObject result = coll.group(key, cond, initial, reduce);
    return result;
  }

  @Override
  public AggregationOutput aggregate(String collection, DBObject firstOp, DBObject[] additionalOps) {
    DB db = mongoManager.getDB();
    DBCollection coll = db.getCollection(collection);

    ReadPreference preference = ReadPreference.secondaryPreferred();
    coll.setReadPreference(preference);
    
    AggregationOutput aggregate = coll.aggregate(firstOp, additionalOps);
    return aggregate;
  }

  @Override
  public int updateByJson(String collection, JSONObject query, JSONObject update) {
    DB db = mongoManager.getDB();
    DBCollection coll = db.getCollection(collection);

    DBObject queryDbo = new BasicDBObject(query);
    DBObject actionDbo = new BasicDBObject(update);

    WriteResult wresult = coll.update(queryDbo, actionDbo, false, true, WriteConcern.SAFE);

    CommandResult cresult = wresult.getLastError(WriteConcern.SAFE);
    if (cresult.getException() != null) {
      throw new MongoDaoException("Mongo Update Exception:" + cresult.getErrorMessage());
    }

    return wresult.getN();
  }

  @Override
  public int deleteByJson(String collection, JSONObject query) {
    DB db = mongoManager.getDB();
    DBCollection coll = db.getCollection(collection);

    DBObject param = new BasicDBObject(query);

    WriteResult dresult = coll.remove(param, WriteConcern.SAFE);
    CommandResult cresult = dresult.getLastError(WriteConcern.SAFE);
    if (cresult.getException() != null) {
      throw new MongoDaoException("Mongo Delete Exception:" + cresult.getErrorMessage());
    }

    return dresult.getN();
  }

  @Override
  public JSONObject insertByJson(String collection, JSONObject insertData) {
    DB db = mongoManager.getDB();
    DBCollection coll = db.getCollection(collection);

    DBObject insertDbo = new BasicDBObject(insertData);
    WriteResult insertResult = coll.insert(insertDbo, WriteConcern.SAFE);

    CommandResult cresult = insertResult.getLastError(WriteConcern.SAFE);
    if (cresult.getException() != null) {
      throw new MongoDaoException("Mongo Insert Exception:" + cresult.getErrorMessage());
    }

    return JSONObject.fromObject(insertDbo);
  }

  @Override
  public JSONObject findAndModifyByJson(String collection, JSONObject query, JSONObject update, JSONObject field, boolean upsert) {
    DB db = mongoManager.getDB();
    DBCollection coll = db.getCollection(collection);

    DBObject queryDbo = new BasicDBObject(query);
    DBObject actionDbo = new BasicDBObject(update);
    DBObject fieldDbo = new BasicDBObject(field);

    DBObject object = coll.findAndModify(queryDbo, fieldDbo, null, false, actionDbo, true, upsert);

    return JSONObject.fromObject(object);
  }

}
