package com.newroad.mongodb.orm.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import com.newroad.mongodb.orm.MongoSQLContext;
import com.newroad.mongodb.orm.mapping.Dynamic;
import com.newroad.mongodb.orm.mapping.Function;
import com.newroad.mongodb.orm.mapping.Mapping;
import com.newroad.mongodb.orm.mapping.MappingEntry;
import com.newroad.mongodb.orm.mapping.MappingEntry.Type;

/**
 * @info : SQL xml节点创建抽象类
 * @author: tangzj
 * @data : 2013-6-8
 * @since : 1.5
 */
@SuppressWarnings("unchecked")
public abstract class BaseNodeBuilder {

	/**
	 * 解析节点的动态函数
	 */
	private Map<String, Function> functionLibrary;

	/**
	 * 请在子类实现
	 */
	public void builder(Element element, MongoSQLContext context) throws Exception {
		throw new RuntimeException("请在子类实现该节点的解析");
	}

	/**
	 * 递归映射
	 */
	protected void recursionMapping(Element element, List<MappingEntry> mapping) throws Exception {
		for (Element e : (List<Element>) element.elements()) {
			MappingEntry entry = new MappingEntry(e.attributeValue("column"),
					e.attributeValue("name"), e.attributeValue("operate"),
					e.attributeValue("value"));
			String type = e.attributeValue("type");
			if(!StringUtils.isBlank(type))
				entry.setType(Type.fromName(type));
			
			Element dynamic = e.element("dynamic");
			if (dynamic != null) {
				Dynamic dyn = new Dynamic();
				List<Element> funcs = dynamic.elements();
				if (funcs != null) 
					dynamicFunctionWapper(funcs, dyn);
				entry.setDynamic(dyn);
			}

			Element node = e.element("property");
			if (node == null) {
				mapping.add(entry);
			} else {
				String mId = node.attributeValue("mapping");
				String clazz = node.attributeValue("class");
				if (!StringUtils.isBlank(mId)) { // 设置子节点 (子节点为指定 mapping)
					entry.setNode(new Mapping(mId, null, null));
					mapping.add(entry);
				} else if (!StringUtils.isBlank(clazz)) {
					List<MappingEntry> om = new ArrayList<MappingEntry>(node.elements().size());

					// 递归设置子节点内容
					this.recursionMapping(node, om);
					entry.setNode(new Mapping(null, Class.forName(clazz), om)); // 设置子节点
																				// (为指定class)
					mapping.add(entry);
				} else {
					throw new RuntimeException("请设置Mapping子节点属性:" + e.attributeValue("name"));
				}
				
			}
		}
	}

	/**
	 * 动态函数支持
	 * 
	 * @param
	 */
	private void dynamicFunctionWapper(List<Element> functions, Dynamic dyn) {
		for (Element f : functions) {
			functionLibrary.get(f.getName()).init(f, dyn);
		}
	}

	public void setFunctionLibrary(Map<String, Function> functionLibrary) {
		this.functionLibrary = functionLibrary;
	}
}
