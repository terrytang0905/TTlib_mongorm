package com.newroad.mongodb.orm.transaction;

import com.mongodb.DBCollection;

/**
 * User: Denis Rystsov
 */
public class Db extends TransactionCRUD {
    public TransationLog repairingGet(DBCollection storage, Object id) {
    	TransationLog entity = get(storage, id);
        if (entity==null) return entity;
        if (entity.tx==null) return entity;

        TransationLog tx = get(storage, entity.tx);
        if (tx==null) {
            entity.value = entity.updated;
            entity.tx = null;
        } else {
            // force tx to fall on commit
            // may fall if tx have been committed
            update(storage, tx);

            entity.updated = null;
            entity.tx = null;
        }
        update(storage, entity);
        return repairingGet(storage, id);
    }
}
