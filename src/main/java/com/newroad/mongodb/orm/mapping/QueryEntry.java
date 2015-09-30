package com.newroad.mongodb.orm.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.newroad.util.cosure.Convertable;

/**
 * Query entry for select function.
 * @author: tangzj
 * @data : 2014-7-18
 * @since : 1.5
 */
public class QueryEntry {

  private String column;
  private Object value;
  private Operator operate;
  private QueryType type;
  
  public QueryEntry(String column, Object value) {
    this.column = column;
    this.value = value;
  }
  
  public QueryEntry(String column, Object value, QueryType type) {
    this.column = column;
    this.value = value;
    this.type = type;
  }
  
  public QueryEntry(String column, Object value, Operator operate) {
    this.column = column;
    this.value = value;
    this.operate = operate;
  }
  
  public QueryEntry(String column, Object value, Operator operate, QueryType type) {
    this.column = column;
    this.value = value;
    this.operate = operate;
    this.type = type;
  }
  
  public static final Convertable<QueryEntry, Map<String, Object>> TO_QUERY = new Convertable<QueryEntry, Map<String,Object>>() {
    @Override
    public Map<String, Object> convert(QueryEntry q) {
      Map<String, Object> t = new HashMap<String, Object>(1);
      Operator opt = q.getOperate();
      if(opt==null) {
        Object value = q.getValue();
        if(q.getType()!=null) {
          value = q.getType().getValue(value);
        }
        t.put(q.getColumn(), value);
      } else {
        Object value = q.getValue();
        if(q.getType()!=null) {
          value = q.getType().getValue(value);
        }
        Map<String, Object> t1 = new HashMap<String, Object>(1);
        t1.put(opt.getOpt(), value);
        t.put(q.getColumn(), t1);
      }
      return t;
    }
  };
  
  public static final Convertable<QueryEntry, Map<String, Object>> TO_UPDATE = new Convertable<QueryEntry, Map<String,Object>>() {
    @Override
    public Map<String, Object> convert(QueryEntry q) {
      Map<String, Object> t = new HashMap<String, Object>(1);
      Operator opt = q.getOperate();
      if(opt==null) {
        Object value = q.getValue();
        if(q.getType()!=null) {
          value = q.getType().getValue(value);
        }
        t.put(q.getColumn(), value);
      } else {
        Object value = q.getValue();
        if(q.getType()!=null) {
          value = q.getType().getValue(value);
        }
        Map<String, Object> t1 = new HashMap<String, Object>(1);
        t1.put(q.getColumn(), value);
        t.put(opt.getOpt(), t1);
      }
      return t;
    }
  };
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  public static enum QueryType {
    int_ {
      @Override public Object getValue(Object value) {
        if(value==null) {
          return null;
        }
        if(value instanceof Collection) {
          List<Integer> list = new ArrayList<Integer>();
          for(Iterator<Object> iter = ((Collection)value).iterator(); iter.hasNext();) {
            Object _v = iter.next();
            if(value instanceof Integer) {
              list.add((Integer)_v);
            } else {
              list.add(_v==null ? null : Integer.valueOf(_v.toString()));
            }
          }
          return list;
        }
        
        if(value instanceof Integer) {
          return value;
        }
        return Integer.valueOf(value.toString());
      }
    },
    long_ {
      @Override public Object getValue(Object value) {
        if(value==null) {
          return null;
        }
        if(value instanceof Collection) {
          List<Long> list = new ArrayList<Long>();
          for(Iterator<Object> iter = ((Collection)value).iterator(); iter.hasNext();) {
            Object _v = iter.next();
            if(value instanceof Long) {
              list.add((Long)_v);
            } else {
              list.add(_v==null ? null : Long.valueOf(_v.toString()));
            }
          }
          return list;
        }
        
        if(value instanceof Long) {
          return value;
        }
        return Long.valueOf(value.toString());
      }
    },
    double_ {
      @Override public Object getValue(Object value) {
        if(value==null) {
          return null;
        }
        if(value instanceof Collection) {
          List<Double> list = new ArrayList<Double>();
          for(Iterator<Object> iter = ((Collection)value).iterator(); iter.hasNext();) {
            Object _v = iter.next();
            if(value instanceof Double) {
              list.add((Double)_v);
            } else {
              list.add(_v==null ? null : Double.valueOf(_v.toString()));
            }
          }
          return list;
        }
        
        if(value instanceof Double) {
          return value;
        }
        return Double.valueOf(value.toString());
      }
    },
    boolean_ {
      @Override public Object getValue(Object value) {
        if(value==null) {
          return null;
        }
        if(value instanceof Collection) {
          List<Boolean> list = new ArrayList<Boolean>();
          for(Iterator<Object> iter = ((Collection)value).iterator(); iter.hasNext();) {
            Object _v = iter.next();
            if(value instanceof Boolean) {
              list.add((Boolean)_v);
            } else {
              list.add(_v==null ? null : Boolean.valueOf(_v.toString()));
            }
          }
          return list;
        }
        
        if(value instanceof Boolean) {
          return value;
        }
        return Boolean.valueOf(value.toString());
      }
    },
    string {
      @Override public Object getValue(Object value) {
        if(value==null) {
          return null;
        }
        if(value instanceof Collection) {
          List<String> list = new ArrayList<String>();
          for(Iterator<Object> iter = ((Collection)value).iterator(); iter.hasNext();) {
            Object _v = iter.next();
            list.add(_v==null ? null : String.valueOf(_v));
          }
          return list;
        }
        return String.valueOf(value.toString());
      }
    },
    entry {
      @Override public Object getValue(Object value) {
        if(value==null) {
          return null;
        }
        if(value instanceof Collection) {
          Map<String, Object> t1 = new HashMap<String, Object>(1);
          for(Iterator<Object> iter = ((Collection)value).iterator(); iter.hasNext();) {
            QueryEntry entry = (QueryEntry)iter.next();
            Object v = entry.getValue();
            if(entry.getType()!=null) {
              v = entry.getType().getValue(v);
            }
            t1.put(entry.getOperate().getOpt(), v);
          }
          return t1;
        }
        
        QueryEntry entry = (QueryEntry)value;
        Map<String, Object> t2 = new HashMap<String, Object>(1);
        Object v = entry.getValue();
        if(entry.getType()!=null) {
          v = entry.getType().getValue(v);
        }
        t2.put(entry.getOperate().getOpt(), v);
        
        return t2;
      }
    };
    
    abstract Object getValue(Object value);
  }
  
  public String getColumn() {
    return column;
  }
  public Operator getOperate() {
    return operate;
  }
  public Object getValue() {
    return value;
  }
  public QueryType getType() {
    return type;
  }
}