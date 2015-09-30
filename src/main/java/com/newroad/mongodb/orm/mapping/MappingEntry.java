package com.newroad.mongodb.orm.mapping;

import java.io.Serializable;

/**
 * @info  : 列所有内容 
 * @author: tangzj
 * @data  : 2013-6-8
 * @since : 1.5
 */
public class MappingEntry  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8688546958061900484L;
	private String column;
	private String name;
	private String value;
	
	private Type type;
	
	/**
	 * 操作符  默认为 "="
	 */
	private String operate;
	
	/**
	 * 动态执行内容
	 */
	private Dynamic dynamic;
	
	/**
	 * Mapping子节点 (对象的属性为model 或 list 之类的结构时)
	 */
	private Mapping node;
	
	public MappingEntry(String column, String name, String value) {
		this.column = column;
		this.name = name;
		this.value = value;
	}

	public MappingEntry(String column, String name, String operate, String value){
		this.column = column;
		this.name = name;
		this.operate = operate;
		this.value = value;
	}
	
	public Mapping getNode() {
		return node;
	}
	public void setNode(Mapping node) {
		this.node = node;
	}
	public String getColumn() {
		return column;
	}
	public String getName() {
		return name;
	}
	public String getOperate() {
		return operate;
	}
	public String getValue() {
		return value;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Dynamic getDynamic() {
		return dynamic;
	}
	public void setDynamic(Dynamic dynamic) {
		this.dynamic = dynamic;
	}

	/**
	 * 类型枚举
	 */
	public enum Type {
		INT("int"),
		LONG("long"),
		FLOAT("float"),
		DOUBLE("double"),
		STRING("string"),
		BOOLEAN("boolean"),
		REGEX("regex"),
		OBJECTID("objectid"),
		IGNORENULL("ignore-null"),
		DYNAMIC("dynamic");
		
		private String name;
		
		private Type(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public static Type fromName(String name) {
			for (Type type : values()) {
				if (type.getName().equals(name))
					return type;
			}
			return null;  // return Type.STRING;  不使用默认值返回
		}
	}
}
