package com.newroad.mongodb.orm.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import com.newroad.mongodb.orm.MongoSQLConfig;
import com.newroad.mongodb.orm.MongoSQLContext;
import com.newroad.mongodb.orm.MongoSQLConfig.Select;
import com.newroad.mongodb.orm.mapping.Mapping;
import com.newroad.mongodb.orm.mapping.MappingEntry;

/**
 * @info  :  Select 节点解析
 * @author: tangzj
 * @data  : 2013-6-8
 * @since : 1.5
 */
@SuppressWarnings("unchecked")
public class SelectNodeBuilder extends BaseNodeBuilder {

	@Override
	public void builder(Element element, MongoSQLContext context) throws Exception {
		String id = element.attributeValue("id");
		String collection = element.attributeValue("collection");
		MongoSQLConfig config = new MongoSQLConfig(id, collection);
		
		Select select = config.new Select();
		for(Element e : (List<Element>)element.elements()){
			String name = e.attributeValue("name");
			if ("param".equals(name)) {
				String _clazz = e.attributeValue("class");
				String mapping = e.attributeValue("mapping");
				if (_clazz!=null) { // class 映射 
					Class<?> clazz = Class.forName(_clazz);
					List<MappingEntry> om = new ArrayList<MappingEntry>(e.elements().size());
					
					// 递归设置mapping
					super.recursionMapping(e, om);
					
					select.setParam(new Mapping(mapping, clazz, om));
				} else if(mapping!=null) {  // mapping 映射
					select.setParam(new Mapping(mapping, null, null));
				} else if (e.attributes().size() == 1) {  // 使用配置的值
					Class<?> clazz = Class.forName("java.util.Map");
					List<MappingEntry> om = new ArrayList<MappingEntry>(e.elements().size());
					
					// 递归设置mapping
					super.recursionMapping(e, om);
					
					select.setParam(new Mapping(null, clazz, om));
				} else {
					throw new RuntimeException("错误的param属性:"+id);
				}
			} else if ("result".equals(name)) {
				String _clazz = e.attributeValue("class");
				String mapping = e.attributeValue("mapping");
				if (_clazz!=null){
					Class<?> clazz = Class.forName(_clazz);
					List<MappingEntry> om = new ArrayList<MappingEntry>(e.elements().size());
					
					// 递归设置mapping
					super.recursionMapping(e, om);
					
					select.setResult(new Mapping(mapping, clazz, om));
				} else if(mapping!=null) {
					select.setResult(new Mapping(mapping, null, null));
				} else {
					throw new RuntimeException("请设置result的属性映射:"+id);
				}
			} else if ("order".equals(name)) {
				Map<String, Integer> order = new HashMap<String, Integer>(e.elements().size());
				for(Element paramE : (List<Element>)e.elements()){
					order.put(paramE.attributeValue("key"), Integer.parseInt(paramE.attributeValue("value")));
				}
				select.setOrder(order);
			} else if ("skip".equals(name)) {
				String skipRecord = e.attributeValue("value");
				if (!StringUtils.isEmpty(skipRecord)) 
					select.setSkip(Integer.parseInt(skipRecord));
			} else if ("limit".equals(name)) {
				String limitRecord = e.attributeValue("value");
				if (!StringUtils.isEmpty(limitRecord)) 
					select.setLimit(Integer.parseInt(limitRecord));
			} else {
				throw new RuntimeException("错误的select根节点: id["+id+"], name:"+name);
			}
		}
		config.setSelect(select);
		context.put(id, config);
	}
}
