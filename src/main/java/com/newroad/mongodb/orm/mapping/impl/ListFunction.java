package com.newroad.mongodb.orm.mapping.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.dom4j.Element;

import com.newroad.mongodb.orm.mapping.Dynamic;
import com.newroad.mongodb.orm.mapping.Function;
import com.newroad.mongodb.orm.mapping.FunctionData;
import com.newroad.mongodb.orm.mapping.MappingEntry.Type;

/**
 * @info  : 动态迭代函数 
 * @author: tangzj
 * @data  : 2013-6-8
 * @since : 1.5
 */
public class ListFunction implements Function {

	/**
	 * 
	 */
	private static final long serialVersionUID = -983084028074067901L;

	@Override
	public void init(Element element, Dynamic dynamic) {
		String text = element.getTextTrim(); // 迭代目标
		Type type = Type.STRING;
		String _type = element.attributeValue("type");
		if(_type != null)
			type = Type.fromName(_type);
		dynamic.addFunction(this, new ListData(text, type));
	}
	
	@Override
	public Object parser(FunctionData data, Object obj) {
		ListData list = (ListData)data;
		String text = new String(list.getText());
		String[] array = text.split(",");
		Type type = list.getType();
		List<Object> res = new ArrayList<Object>(array.length);
		if(Type.INT.equals(type)) {
			for (String item : array) {
				res.add(Integer.valueOf(item));
			}
			return res;
		} else if (Type.FLOAT.equals(type)) {
			for (String item : array) {
				res.add(Float.valueOf(item));
			}
			return res;
		} else if (Type.DOUBLE.equals(type)) {
			for (String item : array) {
				res.add(Double.valueOf(item));
			}
			return res;
		} else if (Type.LONG.equals(type)) {
			for (String item : array) {
				res.add(Long.valueOf(item));
			}
			return res;
		} else if (Type.OBJECTID.equals(type)) {
			for (String item : array) {
				if(ObjectId.isValid(item))
					res.add(new ObjectId(item));
			}
			return res;
		} else {
			return array;
		}
	}
	
	class ListData implements FunctionData {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1374770701409080581L;
		/**
		 * 迭代目标
		 */
		private String text;
		private Type type;
		
		public ListData(String text, Type type){
			this.text = text;
			this.type = type;
		}

		public String getText() {
			return text;
		}
		public Type getType() {
			return type;
		}
	}
}
