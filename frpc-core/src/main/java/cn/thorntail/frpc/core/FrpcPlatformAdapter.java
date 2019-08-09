package cn.thorntail.frpc.core;

import java.io.IOException;
import java.io.InputStream;

/**
 * 平台适配器
 * @author Y13
 *
 */
public interface FrpcPlatformAdapter {

  /**
   * 初始化
   */
  void init();

  /**
   * 获取动态库的数据流
   * @return
   */
  InputStream getLibraryStream() throws IOException;

  /**
   * 获取动态库名称， frpclib.dll or frpclib.so
   * @return
   */
  String getLibraryName();
  
  /**
   * 获取适配器版本
   */
  String getVersion();

}
