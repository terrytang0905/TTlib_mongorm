package com.newroad.mongodb.orm.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import com.newroad.mongodb.orm.MongoSQLContext;
import com.newroad.mongodb.orm.mapping.Mapping;
import com.newroad.mongodb.orm.mapping.MappingEntry;
import com.newroad.mongodb.orm.mapping.MappingEntry.Type;

/**
 * @info  : OM 映射 节点解析
 * @author: tangzj
 * @data  : 2013-6-8
 * @since : 1.5
 */
public class MappingNodeBuilder extends BaseNodeBuilder {

	@Override
	public void builder(Element element, MongoSQLContext context) throws Exception {
		String id = element.attributeValue("id");
		
		// 解析映射模块
		Class<?> clazz = Class.forName(element.attributeValue("class"));
		List<MappingEntry> om = new ArrayList<MappingEntry>();
		
		//  递归设置 mapping 
		this.recursionMapping(element, om);
		
		Mapping orm = new Mapping(id, clazz, om);
		if(context.getOM(id)!=null) 
			throw new RuntimeException("已经存在相同的SQL MAPPING: "+id);
		
		context.putOM(id, orm);
	}
	
	@SuppressWarnings("unchecked")
	public void recursionMapping(Element element, List<MappingEntry> mapping) throws Exception {
		for (Element e : (List<Element>) element.elements()) {
			MappingEntry entry = new MappingEntry(e.attributeValue("column"),
					e.attributeValue("name"), e.attributeValue("operate"),
					e.attributeValue("value"));
			String type = e.attributeValue("type");
			if(!StringUtils.isBlank(type))
				entry.setType(Type.fromName(type));
			
			Element node = e.element("value");
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
}
