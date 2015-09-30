package com.newroad.mongodb.orm.builder;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.newroad.mongodb.orm.MongoSQLConfig;
import com.newroad.mongodb.orm.MongoSQLConfig.Update;
import com.newroad.mongodb.orm.MongoSQLContext;
import com.newroad.mongodb.orm.mapping.Mapping;
import com.newroad.mongodb.orm.mapping.MappingEntry;

/**
 * @info  :  Update 节点解析
 * @author: tangzj
 * @data  : 2013-6-8
 * @since : 1.5
 */
@SuppressWarnings("unchecked")
public class UpdateNodeBuilder extends BaseNodeBuilder {

	@Override
	public void builder(Element element, MongoSQLContext context) throws Exception {
		String id = element.attributeValue("id");

		String collection = element.attributeValue("collection");
		MongoSQLConfig config = new MongoSQLConfig(id, collection);
		
		Update update = config.new Update();
		for (Element e : (List<Element>)element.elements()) {
			String name = e.attributeValue("name");

			// 设置update根节点
			wapperUpdateNode(name, id, e, update);
		}
		config.setUpdate(update);
		context.put(id, config);
	}
	
	private void wapperUpdateNode(String name, String id, Element e, Update update) throws Exception {
		String _clazz = e.attributeValue("class");
		String mapping = e.attributeValue("mapping");
		Mapping m = null;
		if (_clazz!=null) { // class 映射 
			Class<?> clazz = Class.forName(_clazz);
			List<MappingEntry> om = new ArrayList<MappingEntry>(e.elements().size());
			
			// 递归设置mapping
			super.recursionMapping(e, om);
			
			m = new Mapping(mapping, clazz, om);
		} else if(mapping!=null) {  // mapping 映射
			m = new Mapping(mapping, null, null);
		} else if (e.attributes().size() == 1) {  // 使用配置的值
			Class<?> clazz = Class.forName("java.util.Map");
			List<MappingEntry> om = new ArrayList<MappingEntry>(e.elements().size());
			
			// 递归设置mapping
			super.recursionMapping(e, om);
			
			m = new Mapping(null, clazz, om);
		} else {
			throw new RuntimeException("错误的"+name+"属性:"+id);
		}
		
		// 设置内容
		if ("param".equals(name)) {
			update.setParam(m);
		} else if ("action".equals(name)) {
			update.setAction(m);
		} else if("result".equals(name)) {
			update.setResult(m);
		}else {
			throw new RuntimeException("错误的update根节点:"+name);
		}
	}
}
