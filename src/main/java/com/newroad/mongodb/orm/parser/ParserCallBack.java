package com.newroad.mongodb.orm.parser;

import com.newroad.mongodb.orm.MongoSQLContext;
import com.newroad.mongodb.orm.mapping.Mapping;

/**
 * @info  : 解析的回调函数 
 * @author: tangzj
 * @data  : 2013-6-7
 * @since : 1.5
 */
public interface ParserCallBack<T> {
	T callBack(Mapping orm, MongoSQLContext context, Object obj) throws Exception;
}
