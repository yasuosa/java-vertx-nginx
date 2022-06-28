package com.rpy.jvnginx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * @program: java-vertx-nginx
 * @description: 服务类
 * @author: 任鹏宇
 * @create: 2022-06-27 14:47
 **/
public class ServerVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);

    // get
    router.get("/hello")
      .handler(BodyHandler.create())
      .handler(rc -> {
        rc.response().end("Hello Server! GET!");
      });

    // post
    router.post("/hello")
      .handler(BodyHandler.create())
      .handler(rc -> {
        JsonObject bodyAsJson = rc.getBodyAsJson();
        System.out.println(bodyAsJson.toString());
        rc.response().end("Hello Server! POST!");
      });


    router.get("/a").handler(rc -> {
      rc.response().end("AAA!");
    });

    router.errorHandler(500,rc->{
      rc.failure().printStackTrace();
      rc.response().end(rc.failure().getMessage());
    });

    router.errorHandler(404,rc -> {
      rc.response().end("404");
    });

    server
      .requestHandler(router)
      .listen(8080,event->{
        if(event.succeeded()){
          System.out.println("启动成功！");
        }
      });
  }
}
