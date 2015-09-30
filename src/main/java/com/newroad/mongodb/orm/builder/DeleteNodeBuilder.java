package com.newroad.mongodb.orm.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import com.newroad.mongodb.orm.MongoSQLConfig;
import com.newroad.mongodb.orm.MongoSQLContext;
import com.newroad.mongodb.orm.MongoSQLConfig.Delete;
import com.newroad.mongodb.orm.mapping.Mapping;
import com.newroad.mongodb.orm.mapping.MappingEntry;

/**
 * @info  : Delete 节点解析
 * @author: tangzj
 * @data  : 2013-6-8
 * @since : 1.5
 */
@SuppressWarnings("unchecked")
public class DeleteNodeBuilder extends BaseNodeBuilder {

	@Override
	public void builder(Element element, MongoSQLContext context) throws Exception {
		String id = element.attributeValue("id");
		String collection = element.attributeValue("collection");
		MongoSQLConfig config = new MongoSQLConfig(id, collection);
		
		Delete delete = config.new Delete();
		for (Element e : (List<Element>)element.elements()) {
			String name = e.attributeValue("name");
			if (!"param".equals(name)) {
				throw new RuntimeException("错误的delete根节点:"+name);
			}
			String _clazz = e.attributeValue("class");
			String mapping = e.attributeValue("mapping");
			if (!StringUtils.isBlank(_clazz)) {
				Class<?> clazz = Class.forName(_clazz);
				List<MappingEntry> om = new ArrayList<MappingEntry>(e.elements().size());
				
				// 递归设置mapping
				super.recursionMapping(e, om);
				
				delete.setParam(new Mapping(mapping, clazz, om));
			} else if (!StringUtils.isBlank(mapping)) {
				delete.setParam(new Mapping(mapping, null, null));
			} else {
				throw new RuntimeException("请设置param的属性映射");
			}
		}
		config.setDelete(delete);
		context.put(id, config);
	}
}
