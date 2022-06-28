package com.rpy.jvnginx.launcher;

import io.vertx.core.Launcher;

/**
 * @program: java-vertx-nginx
 * @description: 启动类
 * @author: 任鹏宇
 * @create: 2022-06-28 09:25
 **/
public class JVNginxLauncher extends Launcher {

  /**
   * Commands:
   *     bare      Creates a bare instance of vert.x.
   *     list      List vert.x applications
   *     run       Runs a verticle called <main-verticle> in its own instance of
   *               vert.x.
   *     start     Start a vert.x application in background
   *     stop      Stop a vert.x application
   *     version   Displays the version.
   * @param args
   */
  public static void main(String[] args) {
    new JVNginxLauncher().dispatch(args);
  }
}
