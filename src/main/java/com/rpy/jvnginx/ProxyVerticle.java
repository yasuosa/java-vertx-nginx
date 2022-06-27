package com.rpy.jvnginx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;

import java.util.Queue;

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
    //clientOptions.setDefaultHost("127.0.0.1");
    //clientOptions.setDefaultPort(8080);
    clientOptions.setDefaultHost("www.baidu.com");
    clientOptions.setDefaultPort(443);
    clientOptions.setSsl(true);
    HttpClient client = vertx.createHttpClient(clientOptions);


    HttpServerOptions serverOptions = new HttpServerOptions();
    serverOptions.setTcpKeepAlive(true);

    vertx.createHttpServer(serverOptions)
      .requestHandler(req -> {

        HttpServerResponse resp = req.response();
        req.pause(); // 暂停
        resp.setChunked(true); // body分块
        client.request(req.method(), req.uri(), ar -> {
          // request 构造完成
          if (ar.succeeded()) {

            // 获取代理请求  - localhost:9090/hello
            HttpClientRequest req2 = ar.result();

            // 设置header
            req.headers().forEach(entry -> {
              if ("Content-Type".equals(entry.getValue())) {
                req2.putHeader(entry.getKey(), entry.getValue());
              }
            });




            req2.send(req)
              .onSuccess(resp::send)
              .onFailure(Throwable::printStackTrace);


          } else {
            resp.setStatusCode(500)
              .end(ar.cause().getMessage());
            ar.cause().printStackTrace();
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
