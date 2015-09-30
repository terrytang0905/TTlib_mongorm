package com.newroad.mongodb.orm.mapping;

/**
 * Mongodb operator type.
 * @author: tangzj
 * @data : 2014-7-18
 * @since : 1.5
 */
public enum Operator {
  
  gt("$gt"),
  gte("$gte"),
  in("$in"),
  lt("$lt"),
  lte("$lte"),
  ne("$ne"),
  nin("$nin"),
  or("$or"),  
  and("$and"),
  not("$not"),
  nor("$nor"),
  exists("$exists"),
  type("$type"),
  mod("$mod"),
  regex("$regex"),
  all("$all"),
  elemMatch("$elemMatch"),
  size("$size");
  
  private String opt;
  
  private Operator(String opt) {
    this.opt = opt;
  }

  public String getOpt() {
    return opt;
  }
  
}
