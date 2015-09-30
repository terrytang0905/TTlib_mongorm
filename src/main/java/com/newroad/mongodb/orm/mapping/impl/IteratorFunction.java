package com.newroad.mongodb.orm.mapping.impl;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import com.newroad.mongodb.orm.mapping.Dynamic;
import com.newroad.mongodb.orm.mapping.Function;
import com.newroad.mongodb.orm.mapping.FunctionData;
import com.newroad.mongodb.orm.parser.NodeParser;
import com.newroad.mongodb.orm.parser.ParserWapper;

/**
 * @info  : 动态迭代函数 
 * @author: tangzj
 * @data  : 2013-6-8
 * @since : 1.5
 */
@SuppressWarnings("unchecked")
public class IteratorFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4520041773586974121L;

	@Override
	public void init(Element element, Dynamic dynamic) {
		String append = element.attributeValue("append");
		String open = element.attributeValue("open");
		String close = element.attributeValue("close");
		String text = element.getTextTrim(); // 迭代目标
		
		dynamic.addFunction(this, new IteratorData(text, append, open, close));
	}
	
	@Override
	public String parser(FunctionData data, Object obj) {
		String text = new String(data.getText());
		IteratorData iter = (IteratorData)data;
		String property = text.replaceAll(NodeParser.TAG, "");
		if(obj instanceof Map) {
			return getForMap(property, iter, obj);
		} else if (obj instanceof List) {
			return getForList(property, iter, obj);
		} else {
			return getForObject(property, iter, obj);
		}
	}
	
	private String getForMap(String property, IteratorData iter, Object obj){
		Map<String, Object> map = (Map<String, Object>)obj;
		Object value = map.get(property);
		if(value==null)
			throw new RuntimeException("dynamic解析异常, 请检查参数:"+property);
		if(!(value instanceof List))
			throw new RuntimeException("dynamic解析异常, 请检查参数:"+property+",不可迭代["+value+"]");
		List<Object> list = (List<Object>)value;
		if(list.size()==0)
			throw new RuntimeException("dynamic解析异常, 请检查参数:"+property+",空集合["+value+"]");
		StringBuffer r = new StringBuffer();
		for(Object o : (List<Object>)value) {
			r.append(iter.getOpen());
			r.append(o);
			r.append(iter.getClose());
			r.append(iter.getAppend());
		}
		return r.substring(0, r.length()-1);
	}
	
	private String getForList(String property, IteratorData iter, Object obj){
		List<Object> list = (List<Object>)obj;
		if(list.size()==0)
			throw new RuntimeException("dynamic解析异常, 请检查参数:"+property+",空集合["+obj+"]");
		StringBuffer r = new StringBuffer();
		for(Object o : (List<Object>)obj) {
			r.append(iter.getOpen());
			r.append(o);
			r.append(iter.getClose());
			r.append(iter.getAppend());
		}
		return r.substring(0, r.length()-1);
	}
	
	private String getForObject(String property, IteratorData iter, Object obj){
		Object value = null;
		try {
			String file = ParserWapper.getInvokeMethod("get", property);
			Method m = obj.getClass().getDeclaredMethod(file);
			value = m.invoke(obj); 
		} catch (Exception e) {
			throw new RuntimeException("dynamic解析异常, 请检查对象属性:"+property, e);
		}
		if (!(value instanceof List))
			throw new RuntimeException("dynamic解析异常, 请检查参数:"+property+",不可迭代["+value+"]");
		List<Object> list = (List<Object>)value;
		if(list.size()==0)
			throw new RuntimeException("dynamic解析异常, 请检查参数:"+property+",空集合["+obj+"]");
		StringBuffer r = new StringBuffer();
		for(Object o : (List<Object>)obj) {
			r.append(iter.getOpen());
			r.append(o);
			r.append(iter.getClose());
			r.append(iter.getAppend());
		}
		return r.substring(0, r.length()-1);
	}
	
	class IteratorData implements FunctionData {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4721427255051053672L;
		
		private String text;
		
		private String append;
		private String open;
		private String close;
		
		public IteratorData(String text, String append, String open, String close){
			this.text = text;
			this.append = append;
			this.open = open;
			this.close = close;
		}
		
		public String getText() {
			return text;
		}
		public String getAppend() {
			return append;
		}
		public String getOpen() {
			return open;
		}
		public String getClose() {
			return close;
		}
	}
}
