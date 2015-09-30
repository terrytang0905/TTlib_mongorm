package com.newroad.mongodb.orm.transaction;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * User: Denis Rystsov
 */
public class MongoTransaction implements TransactionManager{
    private final TransationLog tx;
    private final Db db;

    private Map<TransationLog, DBCollection> log = new HashMap<TransationLog, DBCollection>();

    public MongoTransaction(Db db, TransationLog tx) {
        this.tx = tx;
        this.db = db;
    }

    public void change(DBCollection storage, TransationLog entity, DBObject value) {
        entity.tx = tx.id;
        entity.updated = value;

        log.put(db.update(storage, entity), storage);
    }

    public void openTransaction(){
    	
    }
    
    @Override
    public void commitTransaction(Object txObj) {
    	DBCollection txStorage=(DBCollection)txObj;
        // if this operation pass, tx will be committed
        db.delete(txStorage, tx);
        // tx is committed, this is just a clean up
        try {
            for (TransationLog entity : log.keySet()) {
                entity.value = entity.updated;
                entity.updated = null;
                entity.tx = null;
                db.update(log.get(entity), entity);
            }
        } catch (Exception e) { }
    }
    
    public void rollbackTransaction(Object txObj) {
    
    }

	@Override
	public void commitTransaction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rollbackTransaction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeTransaction() {
		// TODO Auto-generated method stub
		
	}

	

}
