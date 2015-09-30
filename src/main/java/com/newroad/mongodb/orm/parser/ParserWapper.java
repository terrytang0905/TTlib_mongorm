package com.newroad.mongodb.orm.parser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.mongodb.orm.MongoSQLContext;
import com.newroad.mongodb.orm.mapping.Dynamic;
import com.newroad.mongodb.orm.mapping.Mapping;
import com.newroad.mongodb.orm.mapping.MappingEntry;
import com.newroad.mongodb.orm.mapping.MappingEntry.Type;

/**
 * @info  : 解析支持类 
 * @author: tangzj
 * @data  : 2013-6-7
 * @since : 1.5
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ParserWapper {
	
	private static Logger log = LoggerFactory.getLogger(ParserWapper.class);
	
	public static List unitClass = Arrays.asList(String.class, Long.class, Integer.class, Double.class, Float.class, Boolean.class);
	
	/**
	 * 从map mapping转换map
	 */
	public static Map<String, Object> mapToMap (MongoSQLContext context, List<MappingEntry> om, 
															   		   Map<String, Object> para, ParserCallBack<?> callBack) throws Exception {
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
				v = callBack.callBack(node, context, cbj);
				if(!(cbj instanceof List) && v!=null && v instanceof List && ((List)v).size()>0) {
					v = ((List)v).get(0);
				}
			} else if (para!=null && !StringUtils.isBlank(name) && para.get(name)!=null) {   // 使用 obj中的值 	
				v = para.get(name);
			} else if (!StringUtils.isBlank(value)) { //  加入value的默认值
				v = value;
			} 
			
			// 转换值类型
			v = fitValue(type, v);
			
			if(isNotNull(v) || Type.IGNORENULL.equals(type)) {  // operate  表达式操作符  默认为= 
				if (!StringUtils.isBlank(operate)) {
					if(StringUtils.isBlank(column))
						map.put(operate, v);
					else {	
						Map<String, Object> m = new HashMap<String, Object>();
						m.put(operate, v); 
						map.put(column, m);
					}
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
	public static Map<String, Object> objectToMap (MongoSQLContext context, List<MappingEntry> om, 
																		  Object obj, ParserCallBack<?> callBack) throws Exception {
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
				v = callBack.callBack(node, context, invoke);
				if (isNotNull(v) && v instanceof List && ((List)v).size()>0 && !returnType.equals(List.class)) {
					v = ((List)v).get(0);
				}
			} else if (invoke!=null) {
				v = invoke;
			} else if (!StringUtils.isBlank(value)) {
				v = value;
			} 
			
			// 转换值类型
			v = fitValue(type, v);
			
			if(isNotNull(v) || Type.IGNORENULL.equals(type)) {  // operate  表达式操作符  默认为= 
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
	public static Map<String, Object> unitToMap (List<MappingEntry> om, Object obj) throws Exception {
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
			v = fitValue(type, v);
			
			if(Type.IGNORENULL.equals(type) || isNotNull(v)) {  // operate  表达式操作符  默认为= 
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
	 * 返回map 的映射 
	 */
	public static Object jsonToMap(MongoSQLContext context, List<MappingEntry> om, 
															   Object obj, ParserCallBack<?> callBack) throws Exception {
		if (obj instanceof List) {
			List<JSONObject> list = (List<JSONObject>)obj;
			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(list.size());  // 返回 map 数组
			for (JSONObject json : list) {
				result.add(getMap(context, om, json, callBack));
			}
			return result;
		} else {
			return getMap(context, om,  (JSONObject) obj, callBack);
		}
	}
	
	private static Map<String, Object> getMap(MongoSQLContext context,  List<MappingEntry> om,
																				JSONObject json,ParserCallBack<?> callBack) throws Exception {
		if (json==null || json.isEmpty()) return MapUtils.EMPTY_MAP;
		
		Map<String, Object> rm = new HashMap<String, Object>();
		if (CollectionUtils.isEmpty(om)) {
			 for (Iterator<String> iter = json.keys(); iter.hasNext();){
				 String key = iter.next();
				 rm.put(key, json.get(key));
			 }
		} else {
			for(MappingEntry entry : om) {
				String column = entry.getColumn();
				String name = entry.getName();
				
				Mapping node = entry.getNode();
				if (node !=null )  // 递归回调函数
					rm.put(name, callBack.callBack(node, context, json.get(column)));
				else
					rm.put(name, json.get(column));
			}
		}
		return rm;
	}
	
	/**
	 * 返回object 的映射
	 */
	public static Object jsonToObject(MongoSQLContext context,  List<MappingEntry> om, 
																 Class<?> clazz, Object obj,ParserCallBack<?> callBack) throws Exception {
		if(obj instanceof List) {
			List<JSONObject> list = (List<JSONObject>)obj; 
			List<Object> result = new ArrayList<Object>();   // 返回的集合对象
			for (JSONObject json : list) {
				result.add(invokeObject(context, om, clazz, json, callBack));
			}
			return result;
		} else {
			return invokeObject(context, om, clazz,  (JSONObject)obj, callBack);
		}
	}
	
	private static Object invokeObject(MongoSQLContext context,  List<MappingEntry> om, 
		 									Class<?> clazz, JSONObject json,ParserCallBack<?> callBack) throws Exception{
		if (json==null || json.isEmpty()) return null;
		
		Object instance = clazz.newInstance(); // 返回结果对象
		for (MappingEntry entry : om) {
			String column = entry.getColumn();
			String name = entry.getName();
			
			Mapping node = entry.getNode();
			Object v = null;
			if (node !=null ) {
				// 递归回调函数
				v = callBack.callBack(node, context,  json.get(column));
			} else {
				v = json.get(column);
			}
			
			if (isNotNull(v)) {
				// 通过 get方法取属性类型
				String getMethodName = ParserWapper.getInvokeMethod("get", name);
				Method getMethod = clazz.getDeclaredMethod(getMethodName);
				Class<?> rt = getMethod.getReturnType();
				
				String methodName = ParserWapper.getInvokeMethod("set", name);
				Method method = clazz.getDeclaredMethod(methodName, rt);
				try {
					method.invoke(instance, fitClassValue(rt, v));  //  设置对象属性的值
				} catch (Exception e) {
					log.error("对象属性异常:  name[" +name+"], value["+v+"]!", e);
					throw e;
				}
			}
		}
		return instance;
	}
	
	/**
	 * 返回单一类型映射
	 */
	public static Object jsonToUnit (Class<?> clazz, JSONObject json, String name) throws Exception {
		if (json==null || json.isEmpty()) return null;
		Object obj =  json.get(name);
		return fitClassValue(clazz, obj);
	}
	
	/**
	 * 返回正确类型的值
	 */
	private static Object fitClassValue(Class clazz, Object obj) {
	    if(obj == null) {
	      return null;
	    }
		if (clazz.equals(String.class)) {
			return String.valueOf(obj);
		} else if (clazz.equals(Integer.class)) {
			return (int)(Double.parseDouble(String.valueOf(obj)));
		} else if (clazz.equals(Double.class)) {
			return Double.parseDouble(String.valueOf(obj));
		} else if (clazz.equals(Float.class)) {
			return Float.parseFloat(String.valueOf(obj));
		} else if (clazz.equals(Long.class)) {
			return (long)(Double.parseDouble(String.valueOf(obj)));
		} else if (clazz.equals(Boolean.class)) {
			return Boolean.parseBoolean(String.valueOf(obj));
		} else if (clazz.isEnum()){
		    return Enum.valueOf(clazz, obj.toString());
		}
		return obj;
	}
	
	/**
	 * 返回正确类型的值
	 */
	public static Object fitValue(Type type, Object value) {
		if (!isNotNull(value)) return value;
		
		if (value instanceof List) {
			List<Object> _list = (List<Object>)value;
			List<Object> list = new ArrayList<Object>(_list.size());
			if (Type.INT.equals(type)) {
				for (Object o : _list) {
					list.add(Integer.valueOf(o.toString()));
				}
			} else if (Type.FLOAT.equals(type)) {
				for (Object o : _list) {
					list.add(Float.valueOf(o.toString()));
				}
			} else if (Type.DOUBLE.equals(type)) {
				for (Object o : _list) {
					list.add(Double.valueOf(o.toString()));
				}
			} else if (Type.LONG.equals(type)) {
				for (Object o : _list) {
					list.add(Long.valueOf(o.toString()));
				}
			} else if (Type.BOOLEAN.equals(type)) {
				for (Object o : _list) {
					list.add(Boolean.valueOf(o.toString()));
				}
			} else if (Type.STRING.equals(type)) {
				for (Object o : _list) {
					list.add(String.valueOf(o));
				}
			} else if (Type.REGEX.equals(type)) {
				for (Object o : _list) {
					list.add(Pattern.compile(o.toString()));
				}
			} else if (Type.OBJECTID.equals(type)) {
				for (Object o : _list) {
					String v = o.toString();
					if (ObjectId.isValid(v))
						list.add(new ObjectId(v));
				}
			} else {
			  if(!CollectionUtils.isEmpty(_list) && (_list.get(0) instanceof Enum)) {
			    for (Object o : _list) {
                  list.add(o.toString());
			    }
              } else {
                list = _list;
              }
			}
			return list;
		} else {
			if (Type.INT.equals(type)) {
				return Integer.valueOf(value.toString());
			} else if (Type.FLOAT.equals(type)) {
				return Float.valueOf(value.toString());
			} else if (Type.DOUBLE.equals(type)) {
				return Double.valueOf(value.toString());
			} else if (Type.LONG.equals(type)) {
				return Long.valueOf(value.toString());
			} else if (Type.BOOLEAN.equals(type)) {
				return Boolean.valueOf(value.toString());
			} else if (Type.STRING.equals(type)) {
				return String.valueOf(value);
			} else if (Type.REGEX.equals(type)) {
				return Pattern.compile(value.toString());
			} else if (Type.OBJECTID.equals(type)) {
				if (ObjectId.isValid(value.toString()))
					return new ObjectId(value.toString());
			} else if (value instanceof Enum) {
			  return value.toString();
			}
			return value;
		}
	}
	
	public static boolean isNotNull(Object v) {
		if (v == null) {
		  return false;
		}
		//if (v instanceof Collection) return ((Collection)v).size() > 0;
		//if (v instanceof Map) return ((Map)v).size() > 0;
		// 更新笔记时，笔记内容全部删掉  采用以下判断的话 不会修改数据库
		//if (v instanceof String) return ((String)v).length() > 0;
		return true;
	}
	
	public static String getInvokeMethod(String tag, String name) {
		if(StringUtils.isBlank(name)) return name;
		StringBuffer method = new StringBuffer(name.length()+3);
		method.append(tag);
		method.append(name.substring(0, 1).toUpperCase());
		method.append(name.substring(1, name.length()));
		return method.toString();
	}
}
