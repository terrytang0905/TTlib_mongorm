package com.newroad.mongodb.orm.transaction;


/**
 * Transaction Management api
 * @author tangzj1
 */
public interface TransactionManager {
	
	void openTransaction();
	
	void commitTransaction();
	
	void commitTransaction(Object obj);
	
	void rollbackTransaction();
	
	void rollbackTransaction(Object obj);
	
	void closeTransaction();
	
}
