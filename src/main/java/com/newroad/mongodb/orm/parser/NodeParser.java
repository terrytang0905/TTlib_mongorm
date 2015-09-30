package com.newroad.mongodb.orm.parser;

import com.newroad.mongodb.orm.MongoSQLContext;
import com.newroad.mongodb.orm.mapping.Mapping;

public interface NodeParser<T> {

	String TAG = "#"; 
	String VALUE = "value";
	
	public  T parser(Mapping orm, MongoSQLContext context, Object obj) throws Exception;
}