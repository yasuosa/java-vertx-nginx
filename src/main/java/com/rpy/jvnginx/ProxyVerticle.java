package com.rpy.jvnginx;

import com.rpy.jvnginx.domain.Upstream;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
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

    Integer port = config().getInteger("port"); // 端口

    List<Upstream> upstreamList = new ArrayList<>();
    config().getJsonArray("upstream").forEach(jsonObj -> {
      upstreamList.add(new Upstream((JsonObject) jsonObj,vertx));
    });




    HttpServerOptions serverOptions = new HttpServerOptions();

    vertx.createHttpServer(serverOptions)
      .requestHandler(req -> {
        String path = req.path(); // 请求的路径
        HttpServerResponse resp = req.response();
        req.pause(); // 暂停
        for (Upstream upstream : upstreamList) {
          if(path.startsWith(upstream.getPrefix())){
            dispatchClient(upstream,req,resp);
            break;
          }
        }
      })
      .listen(port, event -> {
        if (event.succeeded()) {
          System.out.printf("服务%d - 启动成功！",port);

          vertx.deployVerticle(new ServerVerticle());
        }
      });
  }


  /**
   * 转发
   */
  private void dispatchClient(Upstream upstream,HttpServerRequest req,HttpServerResponse resp) {
    HttpClient client = upstream.getHttpClient();
    String uri = req.uri().replace(upstream.getPrefix(),upstream.getPath());
    client.request(req.method(), uri, ar -> {
      // request 构造完成
      if (ar.succeeded()) {
        // 获取代理请求  - localhost:9090/hello
        HttpClientRequest reqUpstream = ar.result();
        // 设置header
        reqUpstream.headers().setAll(req.headers());
        // 转发
        reqUpstream.send(req)
          .onSuccess(respUpstream -> {
            resp.setStatusCode(respUpstream.statusCode());
            resp.headers().setAll(respUpstream.headers());
            resp.send(respUpstream);
          })
          .onFailure(t -> {
            t.printStackTrace();
            resp.setStatusCode(500).end(t.getMessage());
          });
      } else {
        resp.setStatusCode(500).end(ar.cause().getMessage());
        ar.cause().printStackTrace();
      }
    });
  }


}
