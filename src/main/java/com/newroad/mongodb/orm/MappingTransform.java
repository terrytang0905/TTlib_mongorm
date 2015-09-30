package com.newroad.mongodb.orm;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.mongodb.orm.mapping.Mapping;
import com.newroad.mongodb.orm.parser.NodeParser;

/**
 * @info : Mongo SQL 映射转换器
 * @author: tangzj
 * @data : 2013-6-4
 * @since : 1.5
 */
@SuppressWarnings("unchecked")
public class MappingTransform {

  private static Logger log = LoggerFactory.getLogger(MappingTransform.class);

  private Map<ParserType, NodeParser<?>> parser;

  /**
   * select 参数转换
   * 
   * @param obj 条件参数 (赋值优先: 即xml中设置了值，obj中也设置了该值 优先使用obj中的值)
   */
  public Map<String, Object> tsfsp(MongoSQLContext context, MongoSQLConfig config, Object obj) {
    try {
      Mapping orm = config.getSelect().getParam();
      return (Map<String, Object>) parser.get(ParserType.QUERY_PARAM).parser(orm, context, obj);
    } catch (Exception e) {
      log.error("[select]parameter transfer error :sqlId=" + config.getSqlId(), e);
      throw new RuntimeException("[select]parameter transfer error :sqlId=" + config.getSqlId() + "," + obj.getClass(), e);
    }
  }

  /**
   * select 结果集转换 （查询前的转换）
   * 
   * @param param 条件参数 (优先 即 xml中设置了值， param中也传了该值 优先使用 param中的值)
   */
  public Map<String, String> tsfsrb(MongoSQLContext context, MongoSQLConfig config, Object obj) {
    try {
      Mapping orm = config.getSelect().getResult();
      return (Map<String, String>) parser.get(ParserType.SELECT_RESULT_BEFORE).parser(orm, context, obj);
    } catch (Exception e) {
      log.error("[select]result query transfer error :sqlId=" + config.getSqlId(), e);
      throw new RuntimeException("[select]result query transfer error :sqlId=" + config.getSqlId() + "," + obj.getClass(), e);
    }
  }

  /**
   * select 结果集转换 （查询后的转换） 将结果集映射到 对象中
   */
  public Object tsfsra(MongoSQLContext context, MongoSQLConfig config, Object obj) {
    try {
      Mapping orm = config.getSelect().getResult();
      return (Object) parser.get(ParserType.SELECT_RESULT_AFTER).parser(orm, context, obj);
    } catch (Exception e) {
      log.error("[select]result transfer error:sqlId=" + config.getSqlId(), e);
      throw new RuntimeException("[select]result transfer error :sqlId=" + config.getSqlId() + "," + obj.getClass(), e);
    }
  }

  /**
   * insert 插入转换
   */
  public List<Map<String, Object>> tsfi(MongoSQLContext context, MongoSQLConfig config, Object obj) {
    try {
      Mapping orm = config.getInsert().getParam();
      return (List<Map<String, Object>>) parser.get(ParserType.INSERT).parser(orm, context, obj);
    } catch (Exception e) {
      log.error("[insert]object transfer error :sqlId=" + config.getSqlId(), e);
      throw new RuntimeException("[insert]object transfer error :sqlId=" + config.getSqlId() + "," + obj.getClass(), e);
    }
  }

  /**
   * update 更新 condition(条件) 转换
   */
  public Map<String, Object> tsfuc(MongoSQLContext context, MongoSQLConfig config, Map<String, Object> map) {
    try {
      Mapping orm = config.getUpdate().getParam();
      return (Map<String, Object>) parser.get(ParserType.QUERY_PARAM).parser(orm, context, map);
    } catch (Exception e) {
      log.error("[update]condition transfer error :" + config.getSqlId(), e);
      throw new RuntimeException("[update]condition transfer error :" + config.getSqlId(), e);
    }
  }

  /**
   * update 更新 action(替换内容) 转换
   */
  public Map<String, Object> tsfua(MongoSQLContext context, MongoSQLConfig config, Map<String, Object> map) {
    try {
      Mapping orm = config.getUpdate().getAction();
      return (Map<String, Object>) parser.get(ParserType.UPDATE_ACTION).parser(orm, context, map);
    } catch (Exception e) {
      log.error("[update]object transfer error :" + config.getSqlId(), e);
      throw new RuntimeException("[update]object transfer error :" + config.getSqlId(), e);
    }
  }


  /**
   * update 更新 condition(条件) 转换
   */
  public Map<String, Object> tsfuc(MongoSQLContext context, MongoSQLConfig config, Object queryObject) {
    try {
      Mapping orm = config.getUpdate().getParam();
      return (Map<String, Object>) parser.get(ParserType.QUERY_PARAM).parser(orm, context, queryObject);
    } catch (Exception e) {
      log.error("[update]condition transfer error :" + config.getSqlId(), e);
      throw new RuntimeException("[update]condition transfer error :" + config.getSqlId(), e);
    }
  }

  /**
   * update 更新 action(替换内容) 转换
   */
  public Map<String, Object> tsfua(MongoSQLContext context, MongoSQLConfig config, Object updateObject) {
    try {
      Mapping orm = config.getUpdate().getAction();
      return (Map<String, Object>) parser.get(ParserType.UPDATE_ACTION).parser(orm, context, updateObject);
    } catch (Exception e) {
      log.error("[update]object transfer error :" + config.getSqlId(), e);
      throw new RuntimeException("[update]object transfer error :" + config.getSqlId(), e);
    }
  }


  /**
   * delete 删除转换
   */
  public Map<String, Object> tsfd(MongoSQLContext context, MongoSQLConfig config, Object obj) {
    try {
      Mapping orm = config.getDelete().getParam();

      return (Map<String, Object>) parser.get(ParserType.QUERY_PARAM).parser(orm, context, obj);
    } catch (Exception e) {
      log.error("[delete]object transfer error :" + config.getSqlId(), e);
      throw new RuntimeException("[delete]object transfer error :" + config.getSqlId() + "," + obj.getClass(), e);
    }
  }

  /**
   * Transform sql config
   */
  public Map<String, Object> transformConfig(MongoSQLContext context, Mapping orm, Object obj) {
    try {
      return (Map<String, Object>) parser.get(ParserType.CONFIG).parser(orm, context, obj);
    } catch (Exception e) {
      log.error("transform sql config error :", e);
      throw new RuntimeException("transform sql config error :" + obj.getClass(), e);
    }
  }

  /**
   * Transform sql config update action
   */
  public Map<String, Object> transformAction(MongoSQLContext context, Mapping orm, Object obj) {
    try {
      return (Map<String, Object>) parser.get(ParserType.CONFIG_ACTION).parser(orm, context, obj);
    } catch (Exception e) {
      log.error("transform sql config update ation error :", e);
      throw new RuntimeException("transform sql config update ation error :" + obj.getClass(), e);
    }
  }

  /**
   * Transform sql config return field
   */
  public Map<String, String> transformReturnField(MongoSQLContext context, Mapping orm, Object obj) {
    try {
      return (Map<String, String>) parser.get(ParserType.SELECT_RESULT_BEFORE).parser(orm, context, obj);
    } catch (Exception e) {
      log.error("Transform sql config return field error :", e);
      throw new RuntimeException("Transform sql config return field error :" + obj.getClass(), e);
    }
  }

  /**
   * Transform sql config return result
   */
  public Object transformReturnResult(MongoSQLContext context, Mapping orm, Object obj) {
    try {
      return (Object) parser.get(ParserType.SELECT_RESULT_AFTER).parser(orm, context, obj);
    } catch (Exception e) {
      log.error("Transform sql config return result error :", e);
      throw new RuntimeException("Transform sql config return result error :" + obj.getClass(), e);
    }
  }

  public void setParser(Map<ParserType, NodeParser<?>> parser) {
    this.parser = parser;
  }

  /**
   * 分析类型
   */
  public static enum ParserType {
    QUERY_PARAM, // 查询 参数
    SELECT_RESULT_BEFORE, // 查询 结果
    SELECT_RESULT_AFTER, // 查询 结果
    INSERT, // 插入
    UPDATE_ACTION, // 更新内容
    CONFIG_ACTION, // 配置文件 action
    CONFIG; // 解析所有类型匹配
  }
}
