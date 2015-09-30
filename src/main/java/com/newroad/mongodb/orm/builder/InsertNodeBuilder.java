package com.newroad.mongodb.orm.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import com.newroad.mongodb.orm.MongoSQLConfig;
import com.newroad.mongodb.orm.MongoSQLContext;
import com.newroad.mongodb.orm.MongoSQLConfig.Insert;
import com.newroad.mongodb.orm.mapping.Mapping;
import com.newroad.mongodb.orm.mapping.MappingEntry;

/**
 * @info  : Insert 节点解析
 * @author: tangzj
 * @data  : 2013-6-8
 * @since : 1.5
 */
@SuppressWarnings("unchecked")
public class InsertNodeBuilder extends BaseNodeBuilder {

	@Override
	public void builder(Element element, MongoSQLContext context) throws Exception {
		String id = element.attributeValue("id");
		String collection = element.attributeValue("collection");
		MongoSQLConfig config = new MongoSQLConfig(id, collection);
		
		Insert insert = config.new Insert();
		for (Element e : (List<Element>)element.elements()) {
			String name = e.attributeValue("name");
			if (!"param".equals(name)) {
				throw new RuntimeException("错误的insert根节点:"+name);
			}
			String _clazz = e.attributeValue("class");
			String mapping = e.attributeValue("mapping");
			if (!StringUtils.isBlank(_clazz)) {
				Class<?> clazz = Class.forName(_clazz);
				List<MappingEntry> om = new ArrayList<MappingEntry>(e.elements().size());
				
				// 递归设置mapping
				super.recursionMapping(e, om);
				
				insert.setParam(new Mapping(mapping, clazz, om));
			} else if (!StringUtils.isBlank(mapping)) {
				insert.setParam(new Mapping(mapping, null, null));
			} else {
				throw new RuntimeException("请设置param的属性映射");
			}
		}
		config.setInsert(insert);
		context.put(id, config);
	}
}
