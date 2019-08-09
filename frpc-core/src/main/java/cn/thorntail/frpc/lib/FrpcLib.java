package cn.thorntail.frpc.lib;

import com.sun.jna.Library;

/**
 * 需要链接到golang动态库
 * @author Y13
 *
 */
public interface FrpcLib extends Library {

  /**
   * 测试是否连接成功
   * @param info
   */
  void test(String info);

  /**
   * 执行处理，启动frpc client
   * @param cfgf
   */
  void run(String cfgf);

}
