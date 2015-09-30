package com.newroad.mongodb.orm.db;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.mongodb.orm.DBClientBeanFactory;
import com.newroad.mongodb.orm.MongoSQLConfig;
import com.newroad.mongodb.orm.MongoSQLService;
import com.newroad.mongodb.orm.db.client.DBCRUDClient;
import com.newroad.mongodb.orm.exception.MongoDaoException;
import com.newroad.mongodb.orm.mapping.QueryEntry;
import com.newroad.util.cosure.CosureUtils;
import com.mongodb.AggregationOutput;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * @info : DAO 层提供基础服务
 * @author: tangzj, tangzj1
 * @data : 2013-6-26
 * @since : 1.5
 */
public class MongoDao implements MongoDaoIf {

  private static Logger log = LoggerFactory.getLogger(MongoDao.class);

  private DBClientBeanFactory dbClientBeanFactory;

  private MongoSQLService mongoSQLService;

  public Object runCommand(Map<String, Object> obj) {
    try {
      DBContext context = new DBContext(null);
      context.setParam(obj);
      JSONObject json = getDbCRUDClient().runCommand(context);
      if (json == null) {
        return null;
      }
      log.info("DB runCommand info:" + json.toString());
      return json;
    } catch (RuntimeException e) {
      log.error("DB runCommand Exception", e);
      throw new MongoException("DB runCommand Exception:" + e.getMessage(), e);
    }
  }

  public Object getStats() {
    try {
      JSONObject json = getDbCRUDClient().getStats();
      if (json == null) {
        return null;
      }
      log.info("DB Status info:" + json.toString());
      return json;
    } catch (RuntimeException e) {
      log.error("DB Status Exception", e);
      throw new MongoException("DB Status Exception:" + e.getMessage(), e);
    }
  }

  /**
   * @param sqlId
   * @param obj
   * @return
   */
  public Object selectOne(String sqlId, Object obj) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return selectOne(config, config.getCollection(), obj);
  }

  /**
   * @param sqlId
   * @param collection
   * @param obj
   * @return
   */
  public Object selectOne(String sqlId, String collection, Object obj) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return selectOne(config, collection, obj);
  }

  /**
   * @param config
   * @param collection
   * @param obj
   * @return
   */
  private Object selectOne(MongoSQLConfig config, String collection, Object obj) {
    String sqlId = config.getSqlId();
    try {
      //long s = System.nanoTime();
      DBContext context = new DBContext(collection);
      context.setParam(mongoSQLService.tsfsp(config, obj));
      context.setField(mongoSQLService.tsfsrb(config, obj));
      context.setOrder(config.getSelect().getOrder());
      // mongoSQLService.tsfo(config, param),

      JSONObject json = getDbCRUDClient().selectOne(context);
      if (json == null || json.isEmpty()) {
        return null;
      }
      //log.debug("##########  selectOne:{},use:{} ", config.getSqlId(), (System.nanoTime() - s));
      return mongoSQLService.tsfsra(config, json);
    } catch (IllegalArgumentException iae) {
      log.error("DB selectOne IllegalArgumentException: sqlID=" + sqlId, iae);
      throw new IllegalArgumentException("DB selectOne IllegalArgumentException: sqlID=" + sqlId + "," + iae.getMessage(), iae);
    } catch (RuntimeException e) {
      log.error("DB selectOne Exception: sqlID=" + sqlId, e);
      throw new MongoException("DB selectOne Exception: sqlID=" + sqlId + "," + e.getMessage(), e);
    }
  }

  /**
   * @param sqlId
   * @param obj
   * @param limitRecord
   * @param skipRecord
   * @return
   */
  public List<?> selectList(String sqlId, Object obj, Integer limitRecord, Integer skipRecord) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return selectList(config, config.getCollection(), obj, limitRecord, skipRecord);
  }

  /**
   * @param sqlId
   * @param collection
   * @param obj
   * @param limitRecord
   * @param skipRecord
   * @return
   */
  public List<?> selectList(String sqlId, String collection, Object obj, Integer limitRecord, Integer skipRecord) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return selectList(config, collection, obj, limitRecord, skipRecord);
  }

  /**
   * @param config
   * @param collection
   * @param obj
   * @param limitRecord
   * @param skipRecord
   * @return
   */
  private List<?> selectList(MongoSQLConfig config, String collection, Object obj, Integer limitRecord, Integer skipRecord) {
    try {
      // long s = System.nanoTime();
      DBContext context = new DBContext(collection);
      context.setParam(mongoSQLService.tsfsp(config, obj));
      context.setField(mongoSQLService.tsfsrb(config, obj));
      context.setOrder(config.getSelect().getOrder());
      context.setLimit(limitRecord == null ? config.getSelect().getLimit() : limitRecord);
      context.setSkip(skipRecord == null ? config.getSelect().getSkip() : skipRecord);

      List<JSONObject> records = getDbCRUDClient().selectList(context);
      if (CollectionUtils.isEmpty(records))
        return Collections.emptyList();

      // log.debug("##########  selectList:{},use:{} ", config.getSqlId(), (System.nanoTime() - s));
      return (List<?>) mongoSQLService.tsfsra(config, records);
    } catch (IllegalArgumentException iae) {
      String sqlId = config.getSqlId();
      log.error("DB selectList IllegalArgumentException: sqlID=" + sqlId, iae);
      throw new RuntimeException("DB IllegalArgumentException: sqlID=" + sqlId + "," + iae.getMessage(), iae);
    } catch (RuntimeException e) {
      String sqlId = config.getSqlId();
      log.error("DB selectList Exception: sqlID=" + sqlId, e);
      throw new MongoException("DB selectList Exception: sqlID=" + sqlId + "," + e.getMessage(), e);
    }
  }

  /**
   * @param sqlId
   * @param obj
   * @param limitRecord
   * @param skipRecord
   * @return
   */
  public long count(String sqlId, Object obj, Integer limitRecord, Integer skipRecord) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return count(config, config.getCollection(), obj, limitRecord, skipRecord);
  }

  /**
   * @param sqlId
   * @param collection
   * @param obj
   * @param limitRecord
   * @param skipRecord
   * @return
   */
  public long count(String sqlId, String collection, Object obj, Integer limitRecord, Integer skipRecord) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return count(config, collection, obj, limitRecord, skipRecord);
  }

  /**
   * @param config
   * @param collection
   * @param obj
   * @param limitRecord
   * @param skipRecord
   * @return
   */
  private long count(MongoSQLConfig config, String collection, Object obj, Integer limitRecord, Integer skipRecord) {

    try {
      //long s = System.nanoTime();
      DBContext context = new DBContext(collection);
      context.setParam(mongoSQLService.tsfsp(config, obj));
      context.setField(mongoSQLService.tsfsrb(config, obj));
      context.setOrder(config.getSelect().getOrder());
      context.setLimit(limitRecord == null ? config.getSelect().getLimit() : limitRecord);
      context.setSkip(skipRecord == null ? config.getSelect().getSkip() : skipRecord);

      long count = getDbCRUDClient().count(context);
      //log.debug("##########  count:{},use:{} ", config.getSqlId(), (System.nanoTime() - s));
      return count;
    } catch (IllegalArgumentException iae) {
      String sqlId = config.getSqlId();
      log.error("DB count IllegalArgumentException: sqlID=" + sqlId, iae);
      throw new IllegalArgumentException("DB count IllegalArgumentException: sqlID=" + sqlId + "," + iae.getMessage(), iae);
    } catch (RuntimeException e) {
      String sqlId = config.getSqlId();
      log.error("DB count Exception: sqlID=" + sqlId, e);
      throw new MongoException("DB count Exception: sqlID=" + sqlId + "," + e.getMessage(), e);
    }
  }

  public List<?> distinct(String sqlId, Object obj) {
    try {
      MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
      if (config == null) {
        throw new MongoDaoException(sqlId, "No mongo config found.");
      }

      DBContext context = new DBContext(config.getCollection());
      context.setParam(mongoSQLService.tsfsp(config, obj));
      context.setField(mongoSQLService.tsfsrb(config, obj));
      context.setOrder(config.getSelect().getOrder()); // mongoSQLService.tsfo(config,
      // param),
      List<?> list = getDbCRUDClient().distinct(context);
      return list;
    } catch (IllegalArgumentException iae) {
      log.error("DB distinct IllegalArgumentException: sqlID=" + sqlId, iae);
      throw new IllegalArgumentException("DB distinct IllegalArgumentException: sqlID=" + sqlId + "," + iae.getMessage(), iae);
    } catch (RuntimeException e) {
      log.error("DB distinct Exception: sqlID=" + sqlId, e);
      throw new MongoException("DB distinct Exception: sqlID=" + sqlId + "," + e.getMessage(), e);
    }
  }

  /**
   * @param sqlId
   * @param obj
   * @return
   */
  public String insert(String sqlId, Object obj) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return insert(config, config.getCollection(), obj);
  }

  /**
   * @param sqlId
   * @param collection
   * @param obj
   * @return
   */
  public String insert(String sqlId, String collection, Object obj) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return insert(config, collection, obj);
  }

  /**
   * @param config
   * @param collection
   * @param obj
   * @return
   */
  private String insert(MongoSQLConfig config, String collection, Object obj) {

    try {
      //long s = System.nanoTime();
      DBContext context = new DBContext(collection);
      context.setParamList(mongoSQLService.tsfi(config, obj));
      String str = getDbCRUDClient().insert(context);

      //log.debug("##########  insert:{},use:{} ", config.getSqlId(), (System.nanoTime() - s));
      return str;
    } catch (RuntimeException e) {
      String sqlId = config.getSqlId();
      log.error("DB insert Exception: sqlID=" + sqlId, e);
      throw new MongoException("DB insert Exception: sqlID=" + sqlId + "," + e.getMessage(), e);
    }
  }

  /**
   * @param sqlId
   * @param list
   * @return
   */
  public List<String> insertBatch(String sqlId, List<?> list) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return insertBatch(config, config.getCollection(), list);
  }

  /**
   * @param sqlId
   * @param collection
   * @param list
   * @return
   */
  public List<String> insertBatch(String sqlId, String collection, List<?> list) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return insertBatch(config, collection, list);
  }

  /**
   * @param config
   * @param collection
   * @param list
   * @return
   */
  private List<String> insertBatch(MongoSQLConfig config, String collection, List<?> list) {
    try {
      //long s = System.nanoTime();
      DBContext context = new DBContext(collection);
      context.setParamList(mongoSQLService.tsfi(config, list));
      List<String> re = getDbCRUDClient().insertBatch(context);

      //log.debug("##########  insertBatch:{},use:{} ", config.getSqlId(), (System.nanoTime() - s));
      return re;
    } catch (RuntimeException e) {
      String sqlId = config.getSqlId();
      log.error("DB insertBatch Exception: sqlID=" + sqlId, e);
      throw new MongoException("DB insertBatch Exception: sqlID=" + sqlId + "," + e.getMessage(), e);
    }
  }

  /**
   * find the specific document and modify it.
   * 
   * @param sqlId
   * @param map
   * @return
   */
  public Object findAndModify(String sqlId, Map<String, Object> map) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return findAndModify(config, config.getCollection(), map);
  }

  /**
   * find the specific document and modify it.
   * 
   * @param sqlId
   * @param collection
   * @param map
   * @return
   */
  public Object findAndModify(String sqlId, String collection, Map<String, Object> map) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return findAndModify(config, collection, map);
  }

  /**
   * find the specific document and modify it.
   * 
   * @param config
   * @param collection
   * @param map
   * @return
   */
  private Object findAndModify(MongoSQLConfig config, String collection, Map<String, Object> map) {
    try {
      //long s = System.nanoTime();
      DBContext context = new DBContext(collection);
      context.setParam(mongoSQLService.tsfuc(config, map));
      context.setAction(mongoSQLService.tsfua(config, map));
      context.setField(mongoSQLService.transformReturnField(config.getUpdate().getResult(), map));
      JSONObject json = getDbCRUDClient().findAndModify(context);
      if (json == null) {
        return null;
      }

      mongoSQLService.transformReturnResult(config.getUpdate().getResult(), json);
      //log.debug("##########  findAndModify:{},use:{} ", config.getSqlId(), (System.nanoTime() - s));
      return mongoSQLService.tsfsra(config, json);
    } catch (RuntimeException e) {
      String sqlId = config.getSqlId();
      log.error("DB findAndModify Exception: sqlID=" + sqlId, e);
      throw new MongoException("DB findAndModify Exception: sqlID=" + sqlId + "," + e.getMessage(), e);
    }
  }

  /**
   * update one document or many documents
   */
  public int update(String sqlId, Map<String, Object> map) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return update(config, config.getCollection(), map);
  }

  /**
   * update one document or many documents
   */
  public int update(String sqlId, String collection, Map<String, Object> map) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return update(config, collection, map);
  }

  /**
   * @param config
   * @param collection
   * @param map
   * @return
   */
  private int update(MongoSQLConfig config, String collection, Map<String, Object> map) {
    try {
      //long s = System.nanoTime();
      DBContext context = new DBContext(collection);
      context.setParam(mongoSQLService.tsfuc(config, map));
      context.setAction(mongoSQLService.tsfua(config, map));
      int count = getDbCRUDClient().update(context);

      //log.debug("##########  update:{},use:{} ", config.getSqlId(), (System.nanoTime() - s));
      return count;
    } catch (RuntimeException e) {
      String sqlId = config.getSqlId();
      log.error("DB update Exception: sqlID=" + sqlId, e);
      throw new MongoException("DB Update Exception: sqlID=" + sqlId + "," + e.getMessage(), e);
    }
  }

  /**
   * @param sqlId
   * @param obj
   * @return
   */
  public int delete(String sqlId, Object obj) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return delete(config, config.getCollection(), obj);
  }

  /**
   * @param sqlId
   * @param collection
   * @param obj
   * @return
   */
  public int delete(String sqlId, String collection, Object obj) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return delete(config, collection, obj);
  }

  /**
   * @param config
   * @param collection
   * @param obj
   * @return
   */
  private int delete(MongoSQLConfig config, String collection, Object obj) {

    try {
      //long s = System.nanoTime();
      DBContext context = new DBContext(collection);
      context.setParam(mongoSQLService.tsfd(config, obj));
      int count = getDbCRUDClient().delete(context);

      //log.debug("##########  delete:{},use:{} ", config.getSqlId(), (System.nanoTime() - s));
      return count;
    } catch (RuntimeException e) {
      String sqlId = config.getSqlId();
      log.error("DB delete Exception: sqlID=" + sqlId, e);
      throw new MongoException("DB delete Exception: sqlID=" + sqlId + "," + e.getMessage(), e);
    }
  }

  private DBCRUDClient getDbCRUDClient() {
    return dbClientBeanFactory.getDbCRUDClient();
  }

  public void setDbClientBeanFactory(DBClientBeanFactory dbClientBeanFactory) {
    this.dbClientBeanFactory = dbClientBeanFactory;
  }

  public void setMongoSQLService(MongoSQLService mongoSQLService) {
    this.mongoSQLService = mongoSQLService;
  }

  @Override
  public int update(String sqlId, Object query, Object update) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return update(config, config.getCollection(), query, update);
  }

  @Override
  public int update(String sqlId, String collection, Object query, Object update) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return update(config, collection, query, update);
  }

  private int update(MongoSQLConfig config, String collection, Object query, Object update) {
    try {
      //long s = System.nanoTime();
      DBContext context = new DBContext(collection);
      context.setParam(mongoSQLService.transformConfig(config.getUpdate().getParam(), query));
      context.setAction(mongoSQLService.transformAction(config.getUpdate().getAction(), update));
      int count = getDbCRUDClient().update(context);

      //log.debug("##########  update:{},use:{} ", config.getSqlId(), (System.nanoTime() - s));
      return count;
    } catch (RuntimeException e) {
      log.error("DB update Exception: sqlID=" + config.getSqlId(), e);
      throw new MongoException("DB Update Exception: sqlID=" + config.getSqlId() + "," + e.getMessage(), e);
    }
  }

  @Override
  public Object findAndModify(String sqlId, Object query, Object update) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return findAndModify(config, config.getCollection(), query, update, false, false);
  }

  @Override
  public Object findAndModify(String sqlId, String collection, Object query, Object update) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return findAndModify(config, config.getCollection(), query, update, false, false);
  }

  @Override
  public Object findAndModify(String sqlId, Object query, Object update, boolean returnNew, boolean upsert) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return findAndModify(config, config.getCollection(), query, update, returnNew, upsert);
  }

  @Override
  public Object findAndModify(String sqlId, String collection, Object query, Object update, boolean returnNew, boolean upsert) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }
    return findAndModify(config, collection, query, update, returnNew, upsert);
  }

  private Object findAndModify(MongoSQLConfig config, String collection, Object query, Object update, boolean returnNew, boolean upsert) {
    try {
      //long s = System.nanoTime();
      DBContext context = new DBContext(collection);
      context.setParam(mongoSQLService.transformConfig(config.getUpdate().getParam(), query));
      context.setAction(mongoSQLService.transformAction(config.getUpdate().getAction(), update));
      context.setField(mongoSQLService.transformReturnField(config.getUpdate().getResult(), query));
      context.setReturnNew(returnNew);
      context.setUpsert(upsert);

      JSONObject result = getDbCRUDClient().findAndModify(context);
      //log.debug("##########  findAndModify:{},use:{} ", config.getSqlId(), (System.nanoTime() - s));
      return mongoSQLService.transformReturnResult(config.getUpdate().getResult(), result);
    } catch (RuntimeException e) {
      log.error("DB findAndModify Exception: sqlID=" + config.getSqlId(), e);
      throw new MongoException("DB findAndModify Exception: sqlID=" + config.getSqlId() + "," + e.getMessage(), e);
    }
  }

  @Override
  public JSONObject queryByJson(String collection, JSONObject query, JSONObject field, JSONObject sort) {
    return getDbCRUDClient().queryByJson(collection, query, field, sort);
  }

  @Override
  public List<JSONObject> queryListByJson(String collection, JSONObject query, JSONObject field, JSONObject sort, int skip, int limit) {
    return getDbCRUDClient().queryListByJson(collection, query, field, sort, skip, limit);
  }

  @Override
  public int updateByJson(String collection, JSONObject query, JSONObject update) {
    return getDbCRUDClient().updateByJson(collection, query, update);
  }

  @Override
  public int deleteByJson(String collection, JSONObject query) {
    return getDbCRUDClient().deleteByJson(collection, query);
  }

  @Override
  public JSONObject insertByJson(String collection, JSONObject insertData) {
    return getDbCRUDClient().insertByJson(collection, insertData);
  }

  @Override
  public JSONObject findAndModifyByJson(String collection, JSONObject query, JSONObject update, JSONObject field, boolean upsert) {
    return getDbCRUDClient().findAndModifyByJson(collection, query, update, field, upsert);
  }

  @Override
  public DBObject group(String collection, DBObject key, DBObject cond, DBObject initial, String reduce) {
    try {
      return getDbCRUDClient().group(collection, key, cond, initial, reduce);
    } catch (RuntimeException e) {
      log.error("DB group error.", e);
      throw new MongoException("DB group error.", e);
    }
  }

  @Override
  public AggregationOutput aggregate(String collection, DBObject firstOp, DBObject[] additionalOps) {
    try {
      return getDbCRUDClient().aggregate(collection, firstOp, additionalOps);
    } catch (RuntimeException e) {
      log.error("DB aggregate error.", e);
      throw new MongoException("DB aggregate error.", e);
    }
  }

  @Override
  public Object selectOneByQuery(String sqlId, Object queryObj, List<QueryEntry> querys) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }

    try {
      //long s = System.nanoTime();
      DBContext context = new DBContext(config.getCollection());

      Map<String, Object> parameter = mongoSQLService.tsfsp(config, queryObj);
      if (!CollectionUtils.isEmpty(querys)) {
        List<Map<String, Object>> queryList = CosureUtils.convert(querys, QueryEntry.TO_QUERY);
        for (Map<String, Object> q : queryList) {
          parameter.putAll(q);
        }
      }

      context.setParam(parameter);
      context.setField(mongoSQLService.tsfsrb(config, queryObj));
      context.setOrder(config.getSelect().getOrder());

      JSONObject json = getDbCRUDClient().selectOne(context);
      if (json == null || json.isEmpty()) {
        return null;
      }
      //log.debug("##########  selectOne:{},use:{} ", config.getSqlId(), (System.nanoTime() - s));
      return mongoSQLService.tsfsra(config, json);
    } catch (IllegalArgumentException iae) {
      log.error("DB selectOne IllegalArgumentException: sqlID=" + sqlId, iae);
      throw new IllegalArgumentException("DB selectOne IllegalArgumentException: sqlID=" + sqlId + "," + iae.getMessage(), iae);
    } catch (RuntimeException e) {
      log.error("DB selectOne Exception: sqlID=" + sqlId, e);
      throw new MongoException("DB selectOne Exception: sqlID=" + sqlId + "," + e.getMessage(), e);
    }
  }

  @Override
  public List<?> selectListByQuery(String sqlId, Object queryObj, Integer limitRecord, Integer skipRecord, List<QueryEntry> querys) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }

    try {
      //long s = System.nanoTime();
      DBContext context = new DBContext(config.getCollection());
      Map<String, Object> parameter = mongoSQLService.tsfsp(config, queryObj);
      if (!CollectionUtils.isEmpty(querys)) {
        List<Map<String, Object>> queryList = CosureUtils.convert(querys, QueryEntry.TO_QUERY);
        for (Map<String, Object> q : queryList) {
          parameter.putAll(q);
        }
      }

      context.setParam(parameter);
      context.setField(mongoSQLService.tsfsrb(config, queryObj));
      context.setOrder(config.getSelect().getOrder());
      context.setLimit(limitRecord == null ? config.getSelect().getLimit() : limitRecord);
      context.setSkip(skipRecord == null ? config.getSelect().getSkip() : skipRecord);

      List<JSONObject> records = getDbCRUDClient().selectList(context);
      if (CollectionUtils.isEmpty(records)) {
        return Collections.emptyList();
      }

      //log.debug("##########  selectList:{},use:{} ", config.getSqlId(), (System.nanoTime() - s));
      return (List<?>) mongoSQLService.tsfsra(config, records);
    } catch (IllegalArgumentException iae) {
      log.error("DB selectList IllegalArgumentException: sqlID=" + sqlId, iae);
      throw new RuntimeException("DB IllegalArgumentException: sqlID=" + sqlId + "," + iae.getMessage(), iae);
    } catch (RuntimeException e) {
      log.error("DB selectList Exception: sqlID=" + sqlId, e);
      throw new MongoException("DB selectList Exception: sqlID=" + sqlId + "," + e.getMessage(), e);
    }
  }

  @Override
  public long countByQuery(String sqlId, Object queryObj, List<QueryEntry> querys) {
    MongoSQLConfig config = mongoSQLService.getConfig(sqlId);
    if (config == null) {
      throw new MongoDaoException(sqlId, "No mongo config found.");
    }

    try {
      //long s = System.nanoTime();
      DBContext context = new DBContext(config.getCollection());
      Map<String, Object> parameter = mongoSQLService.tsfsp(config, queryObj);
      if (!CollectionUtils.isEmpty(querys)) {
        List<Map<String, Object>> queryList = CosureUtils.convert(querys, QueryEntry.TO_QUERY);
        for (Map<String, Object> q : queryList) {
          parameter.putAll(q);
        }
      }

      context.setParam(parameter);
      context.setField(mongoSQLService.tsfsrb(config, queryObj));
      context.setOrder(config.getSelect().getOrder());

      long count = getDbCRUDClient().count(context);
      //log.debug("##########  count:{},use:{} ", config.getSqlId(), (System.nanoTime() - s));
      return count;
    } catch (IllegalArgumentException iae) {
      log.error("DB count IllegalArgumentException: sqlID=" + sqlId, iae);
      throw new IllegalArgumentException("DB count IllegalArgumentException: sqlID=" + sqlId + "," + iae.getMessage(), iae);
    } catch (RuntimeException e) {
      log.error("DB count Exception: sqlID=" + sqlId, e);
      throw new MongoException("DB count Exception: sqlID=" + sqlId + "," + e.getMessage(), e);
    }
  }
}
