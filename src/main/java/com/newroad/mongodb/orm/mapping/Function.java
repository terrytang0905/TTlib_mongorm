package com.newroad.mongodb.orm.mapping;

import java.io.Serializable;

import org.dom4j.Element;

/**
 * @info  : 动态解析函数 
 * @author: tangzj
 * @data  : 2013-6-8
 * @since : 1.5
 */
public interface Function extends Serializable{
	
	public void init(Element element, Dynamic dynamic);
	
	public Object parser(FunctionData data, Object obj) ;
}
