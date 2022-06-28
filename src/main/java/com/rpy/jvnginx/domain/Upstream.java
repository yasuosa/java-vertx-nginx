package com.rpy.jvnginx.domain;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * @program: java-vertx-nginx
 * @description: 上游代理配置类
 * @author: 任鹏宇
 * @create: 2022-06-28 09:35
 **/

@Getter
@Setter
public class Upstream {

  private static final String TAG = "Upstream";

  private static Logger logger = Logger.getLogger(TAG);

  // 前缀匹配路径
  private String prefix ;

  // 真实路径
  private String path;

  // 转发的url
  private String url;

  // 配置文件
  private HttpClient httpClient;


  public Upstream(){

  }

  public Upstream(JsonObject jsonObject, Vertx vertx){
    this.prefix = jsonObject.getString("prefix");
    this.url = jsonObject.getString("url");
    initHttpClient(url,vertx);
  }

  /**
   * 初始化httpClient
   * @param url
   */
  private void initHttpClient(String url,Vertx vertx) {
    try {
      URL proxtUrl = new URL(url);
      String host = proxtUrl.getHost();
      int port = proxtUrl.getPort();
      this.path = proxtUrl.getPath();
      this.httpClient = vertx.createHttpClient(new HttpClientOptions().setDefaultHost(host).setDefaultPort(port));
    } catch (MalformedURLException e) {
      e.printStackTrace();
      logger.warning(e.getMessage());
    }
  }
}
