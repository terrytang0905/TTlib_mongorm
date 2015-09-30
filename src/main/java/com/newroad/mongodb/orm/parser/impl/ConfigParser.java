package com.newroad.mongodb.orm.parser.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.newroad.mongodb.orm.MongoSQLContext;
import com.newroad.mongodb.orm.mapping.Dynamic;
import com.newroad.mongodb.orm.mapping.Mapping;
import com.newroad.mongodb.orm.mapping.MappingEntry;
import com.newroad.mongodb.orm.mapping.MappingEntry.Type;
import com.newroad.mongodb.orm.parser.NodeParser;
import com.newroad.mongodb.orm.parser.ParserWapper;

/**
 * @info : Mongo sql config parser 
 * @author: tangzj
 * @data : 2014-4-21
 * @since : 1.5
 */
@SuppressWarnings("unchecked")
public class ConfigParser implements NodeParser<Object> {

	@Override
	public Object parser(Mapping orm, MongoSQLContext context, Object obj) throws Exception {
		if (!StringUtils.isBlank(orm.getId())) {
			Mapping map = context.getOM(orm.getId());  // 全局mapping映射
			List<MappingEntry> om = map.getOm();
			Class<?> clazz = map.getClazz();
			if(obj instanceof List) {
				List<Object> list = (List<Object>)obj;
				List<Object> parameterList = new ArrayList<Object>(list.size());
				
				if (clazz.equals(Map.class)) {  // Map 转换
					for(Object o : list) {
						parameterList.add(mapToMap(context, om, (Map<String, Object>)o));
					}
				} else {  //  对象转换
					for(Object o : list) {
						parameterList.add(objectToMap(context, om, o));
					}
				}
				return parameterList;
			} else {
				if (clazz.equals(Map.class)) {  // Map 转换
					return mapToMap(context, om, (Map<String, Object>)obj);
				} else {  //  对象转换
					return objectToMap(context, om, obj);
				}
			}
		} else if (orm.getClazz() !=null) {
			Class<?> clazz = orm.getClazz();
			List<MappingEntry> om = orm.getOm();
			
			if(obj instanceof List) {
				List<Object> list = (List<Object>)obj;
				List<Object> parameterList = new ArrayList<Object>(list.size());
				if (clazz.equals(Map.class)) {  // class 为  map
					for(Object o : list) { 
						parameterList.add(mapToMap(context, om, (Map<String, Object>)o));
					}
				}  else if (!ParserWapper.unitClass.contains(clazz)) {
					for(Object o : list) { 
						parameterList.add(objectToMap(context, om, o));
					}
				} else {// 其他情况  如 (String, Long, Integer, Double 等)
					for(Object o : list) { 
						parameterList.add(unitToMap(om, o));
					}
				} 
				return parameterList;
			} else {
				if (clazz.equals(Map.class)) {  // class 为  map
					return mapToMap(context, om, (Map<String, Object>)obj);
				}  else {  
					if (!ParserWapper.unitClass.contains(clazz))
						return objectToMap(context, om, obj);
					else // 其他情况  如 (String, Long, Integer, Double 等)
						return unitToMap(om, obj);
				} 
			}
		} else {
			throw new RuntimeException("SQL配置规则异常, 请设置mapping或class属性");
		}
	}
	
	/**
	 * 从map mapping转换map
	 */
	public Map<String, Object> mapToMap (MongoSQLContext context, List<MappingEntry> om, Map<String, Object> para) throws Exception {
		Map<String, Object> map = new HashMap<String,Object>();
		for (MappingEntry entry : om) {
			String column = entry.getColumn();
			String name = entry.getName();
			String value = entry.getValue();
			String operate = entry.getOperate();
			Type type = entry.getType(); // value 类型
			Dynamic dynamic = entry.getDynamic();
			
			// 子节点
			Mapping node = entry.getNode();
			
			Object v = null;
			
			// 取值优先级  dynamic  >  node  > obj.value > value
			if (dynamic!=null) {
				v = dynamic.parser(para);
			} else if (node != null) { // 回调 递归函数
				Object cbj = para==null ? null : para.get(name);
				v = parser(node, context, cbj);
				if(!(cbj instanceof List) && v!=null && v instanceof List && ((List<?>)v).size()>0) {
					v = ((List<?>)v).get(0);
				}
			} else if (para!=null && !StringUtils.isBlank(name) && para.get(name)!=null) {   // 使用 obj中的值 	
				v = para.get(name);
			} else if (!StringUtils.isBlank(value)) { //  加入value的默认值
				v = value;
			} 
			
			// 转换值类型
			v = ParserWapper.fitValue(type, v);
			
			if(ParserWapper.isNotNull(v) || Type.IGNORENULL.equals(type)) {  // operate  表达式操作符  默认为= 
				if (!StringUtils.isBlank(operate)) {
					Map<String, Object> m = new HashMap<String, Object>();
					m.put(operate, v);
					map.put(column, m);
				} else {
					map.put(column, v);
				}
			}
		}
		return map;
	}
	
	/**
	 * 从object mapping转换map
	 */
	public Map<String, Object> objectToMap (MongoSQLContext context, List<MappingEntry> om, Object obj) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		for (MappingEntry entry : om) {
			String column = entry.getColumn();
			String name = entry.getName();
			String value = entry.getValue();
			String operate = entry.getOperate();
			Type type = entry.getType(); // value 类型
			Dynamic dynamic = entry.getDynamic();
			
			// 子节点
			Mapping node = entry.getNode();
			
			Object v = null;
			
			Object invoke = null;
			Class<?> returnType = null;
			if (obj != null && !StringUtils.isBlank(name)) {
				String methodName = ParserWapper.getInvokeMethod("get", name);
				Method method = obj.getClass().getDeclaredMethod(methodName);
				returnType = method.getReturnType();
				invoke = method.invoke(obj);  //  对象属性的值 
			}
			
			// 取值优先级  dynamic  >  node  > obj.value > value
			if (dynamic != null) {
				v = dynamic.parser(obj);
			} else if (node != null && invoke!=null) {
				//  回调函数 递归
				v = parser(node, context, invoke);
				if (ParserWapper.isNotNull(v) && v instanceof List && ((List<?>)v).size()>0 && !returnType.equals(List.class)) {
					v = ((List<?>)v).get(0);
				}
			} else if (invoke!=null) {
				v = invoke;
			} else if (!StringUtils.isBlank(value)) {
				v = value;
			} 
			
			// 转换值类型
			v = ParserWapper.fitValue(type, v);
			
			if(ParserWapper.isNotNull(v) || Type.IGNORENULL.equals(type)) {  // operate  表达式操作符  默认为= 
				if (!StringUtils.isBlank(operate)) {
					Map<String, Object> m = new HashMap<String, Object>();
					m.put(operate, v);
					map.put(column, m);
				} else {
					map.put(column, v);
				}
			}
		}
		return map;	
	}
	
	/**
	 * 从其他单元类型转换map  如(String, Long, Integer, Double 等)
	 */
	public Map<String, Object> unitToMap (List<MappingEntry> om, Object obj) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		for (MappingEntry entry : om) {
			String column = entry.getColumn();
			String name = entry.getName();
			String value = entry.getValue();
			String operate = entry.getOperate();
			Type type = entry.getType(); // value 类型
			Dynamic dynamic = entry.getDynamic();
			
			Object v = null;
			
			if (dynamic != null) {
				v = dynamic.parser(obj);
			} else if ("${value}".equals(name) && obj !=null) {
				v = obj;
			} else if (!StringUtils.isBlank(value)) {
				v = value;
			} 
			
			// 转换值类型
			v = ParserWapper.fitValue(type, v);
			
			if(ParserWapper.isNotNull(v) || Type.IGNORENULL.equals(type)) {  // operate  表达式操作符  默认为= 
				if (!StringUtils.isBlank(operate)) {
					Map<String, Object> m = new HashMap<String, Object>();
					m.put(operate, v);
					map.put(column, m);
				} else {
					map.put(column, v);
				}
			}
		}
		return map; 
	}
}
