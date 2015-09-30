package com.newroad.mongodb.orm.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @info : MongoDB DataBase Operate Error
 * @author: tangzj
 * @data : 2014-1-22
 * @since : 1.5
 */
public class MongoDaoException extends RuntimeException {

	private static final long serialVersionUID = -8383893860114962233L;

	private static final Log log = LogFactory.getLog(MongoDaoException.class);

    private String sqlId;
	private String msg;

    public MongoDaoException(String sqlId, String msg){
        super(msg);
        this.sqlId = sqlId;
        this.msg = msg;
        log.error("MongoDao exception, sqlId: "+sqlId+", error message:"+msg);
    }
    
    public MongoDaoException(String msg){
        super(msg);
        this.msg = msg;
        log.error("MongoDao exception, error message:"+msg);
    }
    
    public MongoDaoException(String sqlId, String msg, Exception e){
        super(msg, e);
        this.sqlId = sqlId;
        this.msg = msg;
        log.error("MongoDao exception, sqlId: "+sqlId+", error message:"+msg, e);
    }

    @Override
    public String getLocalizedMessage(){
        return sqlId+":"+msg;
    }
}
