package com.rpy.jvnginx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.*;

/**
 * @program: java-vertx-nginx
 * @description: 代理服务器
 * @author: 任鹏宇
 * @create: 2022-06-27 14:51
 **/
public class ProxyVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {

    HttpClientOptions clientOptions = new HttpClientOptions();
    clientOptions.setDefaultHost("127.0.0.1");
    clientOptions.setDefaultPort(8080);
    HttpClient client = vertx.createHttpClient(clientOptions);


    vertx.createHttpServer()
      .requestHandler(req -> {
        HttpServerResponse resp = req.response();
        resp.setChunked(true); // body分块

        client.request(req.method(), req.uri(), ar -> {
          if (ar.succeeded()) {
            HttpClientRequest req2 = ar.result();



            req2.response(ar2 -> {
              if (ar2.succeeded()) {
                HttpClientResponse resp2 = ar2.result();

                resp.setStatusCode(resp2.statusCode());

                resp2.handler(resp::write);

                resp2.endHandler(x->{
                  resp.end();
                });
              }
            });

            req.handler(req2::write); // 持续发送


            req.endHandler(x -> {
              req2.end();
            });
          }
        });
      })
      .listen(9090, event -> {
        if (event.succeeded()) {
          System.out.println("服务9090启动成功！");
        }
      });
  }
}
