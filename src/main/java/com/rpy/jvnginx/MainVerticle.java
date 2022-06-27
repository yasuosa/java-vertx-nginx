package com.rpy.jvnginx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class MainVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new ServerVerticle(),ar->{
      if(ar.succeeded()){
        vertx.deployVerticle(new ProxyVerticle());
      }
    });
  }
}
