package com.newroad.mongodb.orm;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.newroad.mongodb.orm.builder.BaseNodeBuilder;

/**
 * @info  : mongo sql加载器  将sql映射加载到内存
 *              实现热加载功能    
 *              多服务器时   热加载(即刷新) 后 需要多服务器同时更新  
 *              由同步管理器实现 {@link com.lenovo.tool.synchronous.SynchronousManager} 同步)  
 * @author: tangzj
 * @data  : 2013-5-31
 * @since : 1.5
 */

public class MongoSQLLoader{

	private static Logger log = LoggerFactory.getLogger(MongoSQLLoader.class);
	
	private DBClientBeanFactory dbClientBeanFactory;
	
	/**
	 * 节点解析库
	 */
	private Map<String, BaseNodeBuilder> builderLibrary;
	
	/**
	 * @return Map<模块域, Map<sqlID, 配置>
	 */
	public MongoSQLContext load() {
		long start = System.currentTimeMillis();
		MongoSQLContext context = new MongoSQLContext();
		try {
			List<Resource> resources = dbClientBeanFactory.getConfigLocations();
			for(Resource resource : resources){
				load(context, new InputStreamReader(resource.getInputStream(), "UTF-8"), resource.getFilename());
			}
			log.info("[mogno sql] Loading completed, loading size:"+ resources.size()+" files, spent ["+(System.currentTimeMillis()-start)+"] ms.");
			return context;
		} catch (Exception e) {
			log.error("mongo sql Initilize Exception:", e);
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
    private void load(MongoSQLContext context, Reader fileReader, String fileName) throws Exception{
		SAXReader reader = new SAXReader();
		Document doc = reader.read(fileReader);
		Element root = doc.getRootElement();
		
		for(Element element : (List<Element>)root.elements()){
			String name = element.getName();
			
			// 节点解析器
			BaseNodeBuilder builder = builderLibrary.get(name);
			if(builder == null)
				throw new RuntimeException("SQL format Exception: error node ["+name+"],file ["+fileName+"]");
			builder.builder(element, context);
		}
	}

	public void setBuilderLibrary(Map<String, BaseNodeBuilder> builderLibrary) {
		this.builderLibrary = builderLibrary;
	}

	public void setDbClientBeanFactory(DBClientBeanFactory dbClientBeanFactory) {
		this.dbClientBeanFactory = dbClientBeanFactory;
	}
	
}
