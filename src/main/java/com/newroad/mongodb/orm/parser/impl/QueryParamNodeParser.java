package com.newroad.mongodb.orm.parser.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.newroad.mongodb.orm.MongoSQLContext;
import com.newroad.mongodb.orm.mapping.Mapping;
import com.newroad.mongodb.orm.mapping.MappingEntry;
import com.newroad.mongodb.orm.parser.NodeParser;
import com.newroad.mongodb.orm.parser.ParserCallBack;
import com.newroad.mongodb.orm.parser.ParserWapper;

/**
 * @info  : 用于解析 param节点 的分析器 (适用于所有的条件解析) 
 * @author: tangzj
 * @data  : 2013-7-4
 * @since : 1.5
 */
@SuppressWarnings("unchecked")
public class QueryParamNodeParser implements NodeParser<Map<String, Object>> {
	
	@Override
	public Map<String, Object> parser (Mapping orm, MongoSQLContext context, Object obj) throws Exception {
		if (!StringUtils.isBlank(orm.getId())) {
			Mapping map = context.getOM(orm.getId());  // 全局mapping映射
			List<MappingEntry> om = map.getOm();
			Class<?> clazz = map.getClazz();
			if (clazz.equals(Map.class)) {  // Map 转换
				return ParserWapper.mapToMap(context, om, (Map<String, Object>)obj, callBack);
			} else {  //  对象转换
				return ParserWapper.objectToMap(context, om, obj, callBack);
			}
		} else if (orm.getClazz() !=null) {
			Class<?> clazz = orm.getClazz();
			List<MappingEntry> om = orm.getOm();
			if (clazz.equals(Map.class)) {  // class 为  map
				return ParserWapper.mapToMap(context, om, (Map<String, Object>)obj, callBack);
			}  else {  
				if (!ParserWapper.unitClass.contains(clazz))
					return ParserWapper.objectToMap(context, om, obj, callBack);
				else // 其他情况  如 (String, Long, Integer, Double 等)
					return ParserWapper.unitToMap(om, obj);
			} 
		} else {
			throw new RuntimeException("SQL配置规则异常, 请设置mapping或class属性");
		}
	}
	
	/**
	 *  递归时回调
	 */
	ParserCallBack<Map<String, Object>> callBack = new ParserCallBack<Map<String, Object>>() {
		@Override
		public Map<String, Object> callBack(Mapping orm, MongoSQLContext context, Object obj) throws Exception {
			return parser(orm, context, obj);
		}
	};
}
