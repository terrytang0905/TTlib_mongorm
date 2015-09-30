package com.newroad.mongodb.orm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.newroad.mongodb.orm.db.client.mongo.MongoManager;
import com.newroad.mongodb.orm.exception.MongoDaoException;

public class NoteSyncDBTest extends TestCase {

	private MongoManager mongoManager;

	public void setMongoManager(MongoManager mongoManager) {
		this.mongoManager = mongoManager;
	}

	@Before
	public void setUp() throws Exception {
		// mongoDao.insert(sqlId, collection, obj);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void addNoteBook() {
		DB db = mongoManager.getDB();
		DBCollection coll = db.getCollection(MongoDBConstants.NOTEBOOK);

		List<Map<String, Object>> inserts = new ArrayList<Map<String, Object>>();

		List<DBObject> list = new ArrayList<DBObject>(inserts.size());
		for (Map<String, Object> map : inserts) {
			DBObject dbobj = (DBObject) new BasicDBObject();
			dbobj.putAll(map);
			list.add(dbobj);
		}

		CommandResult result = coll.insert(list, WriteConcern.SAFE)
				.getLastError(WriteConcern.SAFE);
	}

	@Test
	public void updateNoteBookName() {
		DB db = mongoManager.getDB();
		DBCollection coll = db.getCollection(MongoDBConstants.NOTEBOOK);

		Map<String, Object> queryMap = new HashMap<String, Object>();
		DBObject dbo = new BasicDBObject();
		dbo.putAll(queryMap);

		Map<String, Object> updateMap = new HashMap<String, Object>();
		DBObject action = new BasicDBObject(updateMap);

		DBObject object = coll.findAndModify(dbo, action);
	}

	@Test
	public void deleteNoteBook() {
		DB db = mongoManager.getDB();
		DBCollection coll = db.getCollection(MongoDBConstants.NOTEBOOK);

		Map<String, Object> queryMap = new HashMap<String, Object>();

		DBObject param = new BasicDBObject(queryMap);
		WriteResult dresult = coll.remove(param, WriteConcern.SAFE);
		CommandResult cresult = dresult.getLastError(WriteConcern.SAFE);
		if (cresult.getException() != null) {
			throw new MongoDaoException("Mongo Delete Exception:"
					+ cresult.getErrorMessage());
		}
	}

	@Test
	public void addNoteTx() {

	}

	@Test
	public void updateNoteTx() {

	}

	@Test
	public void moveNoteByNotebookTx() {
		DB db = mongoManager.getDB();
		DBCollection coll = db.getCollection(MongoDBConstants.NOTEDATA);

		Map<String, Object> queryMap = new HashMap<String, Object>();
		DBObject dbo = new BasicDBObject();
		dbo.putAll(queryMap);

		Map<String, Object> updateMap = new HashMap<String, Object>();
		DBObject action = new BasicDBObject(updateMap);

		DBObject object = coll.findAndModify(dbo, action);
	}

	@Test
	public void deleteNoteTx() {

	}

	@Test
	public void listNotesByNoteBook() {

	}

	@Test
	public void listNoteByTag() {

	}

	@Test
	public void updateResource() {

	}

	@Test
	public void syncNotes() {

	}

	@Test
	public void syncMixNotes() {

	}

	@Test
	public void recoverNotes() {

	}

}
