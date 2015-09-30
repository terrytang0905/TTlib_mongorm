package com.newroad.mongodb.orm.db.client.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.newroad.mongodb.orm.exception.MongoDaoException;
import com.newroad.mongodb.orm.transaction.TransactionManager;

/**
 * @info : Mongo DB Manager
 * @author: tangzj1
 * @data : 2013-10-10
 * @since : 2.0
 */
public class MongoManager implements TransactionManager {

  private static Logger log = LoggerFactory.getLogger(MongoManager.class);

  java.util.logging.Logger mongoLogger = java.util.logging.Logger.getLogger("com.mongodb");

  private String[] nodeiparray = null;
  private String[] nodeportarray = null;
  private String dbName = null;
  private String userName = null;
  private String passWord = null;
  private Integer connectionsPerHost;
  private Integer threadsAllowedToBlock;
  private Integer connectionTimeOut = 15 * 1000;
  private Integer maxRetryTime = 15 * 1000;
  private Integer socketTimeOut = 60 * 1000;
  private List<ServerAddress> serverAddressList;

  private Mongo mongoClient;

  private MongoManager() {
  }

  private MongoManager(String nodeiplist, String nodeportlist, String dbName, String userName,
      String passWord, Integer connectionsPerHost, Integer threadsAllowedToBlock,
      Integer connectionTimeOut, Integer maxRetryTime, Integer socketTimeOut) {
    mongoLogger.setLevel(java.util.logging.Level.SEVERE);
    if (mongoClient == null) {
      this.nodeiparray = nodeiplist.split(",");
      this.nodeportarray = nodeportlist.split(",");
      this.dbName = dbName;
      this.userName = userName;
      this.passWord = passWord;
      this.connectionsPerHost = connectionsPerHost;
      this.threadsAllowedToBlock = threadsAllowedToBlock;
      this.connectionTimeOut = connectionTimeOut;
      this.maxRetryTime = maxRetryTime;
      this.socketTimeOut = socketTimeOut;
      serverAddressList = new ArrayList<ServerAddress>();
      try {
        for (int i = 0; i < nodeiparray.length; i++) {
          serverAddressList.add(new ServerAddress(nodeiparray[i], Integer
              .parseInt(nodeportarray[i])));
        }
      } catch (NumberFormatException e) {
        // TODO Auto-generated catch block
        log.error("MongoManager NumberFormatException:" + e);
      } catch (UnknownHostException e) {
        // TODO Auto-generated catch block
        log.error("MongoManager UnknownHostException:" + e);
      }
      mongoInit();
    }
  }

//	public static MongoManager getInstance() {
//		return InnerHolder.INSTANCE;
//	}
//
//	private static class InnerHolder {
//		static final MongoManager INSTANCE = new MongoManager();
//	}

  private void mongoInit() {
    MongoClientOptions.Builder builder =
        MongoClientOptions.builder().connectionsPerHost(connectionsPerHost)
            .threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlock)
            .connectTimeout(connectionTimeOut).socketTimeout(socketTimeOut).autoConnectRetry(true);
    if (maxRetryTime > 0) {
      builder.maxAutoConnectRetryTime(maxRetryTime);
    }

    int nodes = serverAddressList.size();
    MongoCredential credential =
        MongoCredential.createMongoCRCredential(userName, dbName, passWord.toCharArray());
    if (nodes <= 0) {
      mongoClient = null;
      throw new MongoDaoException("Couldn't get available mongo server address!");
    } else if (nodes == 1) {
      mongoClient =
          new MongoClient(serverAddressList.get(0), Arrays.asList(credential), builder.build());
    } else {
      mongoClient = new MongoClient(serverAddressList, Arrays.asList(credential), builder.build());
    }

    DB dbpool = mongoClient.getDB(dbName);
    if (!dbpool.authenticate(userName, passWord.toCharArray())) {
      log.error("Authentication failure!userName:" + userName + ",dbName:" + dbName + ",passWord:"
          + passWord);
      throw new MongoDaoException("DB Connection failed because of authentication failure!");
    }
  }

  private ThreadLocal<DB> local = new ThreadLocal<DB>();

  public DB getDB() {
    DB db = local.get();
    if (db != null) {
      return db;
    }
    return mongoClient.getDB(dbName);
  }

  public void closeDB() {
    if (mongoClient != null) {
      mongoClient.close();
    }
  }

  @Override
  public void openTransaction() {
    DB db = mongoClient.getDB(dbName);
    local.set(db);
    // Set new PinnedRequestStatus Object into ThreadLocal
    local.get().requestStart();
  }

  @Override
  public void commitTransaction() {

  }

  @Override
  public void rollbackTransaction() {

  }

  @Override
  public void closeTransaction() {
    // Remove the specific PinnedRequestStatus Object from ThreadLocal in order to keep thread safe
    local.get().requestDone();
    local.remove();
  }

  @Override
  public void commitTransaction(Object obj) {
    // TODO Auto-generated method stub

  }

  @Override
  public void rollbackTransaction(Object obj) {
    // TODO Auto-generated method stub

  }

}
