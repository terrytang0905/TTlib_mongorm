package com.newroad.mongodb.orm.transaction;

import com.mongodb.DBObject;

public class TransationLog {
    public Object id;
    public Object version;
    public DBObject value;
    public DBObject updated;
    public Object status;
    public Object tx;
    
	public Object getId() {
		return id;
	}
	public void setId(Object id) {
		this.id = id;
	}
	public Object getVersion() {
		return version;
	}
	public void setVersion(Object version) {
		this.version = version;
	}
	public DBObject getValue() {
		return value;
	}
	public void setValue(DBObject value) {
		this.value = value;
	}
	public DBObject getUpdated() {
		return updated;
	}
	public void setUpdated(DBObject updated) {
		this.updated = updated;
	}
	
	public Object getStatus() {
		return status;
	}
	public void setStatus(Object status) {
		this.status = status;
	}
	public Object getTx() {
		return tx;
	}
	public void setTx(Object tx) {
		this.tx = tx;
	}
}
