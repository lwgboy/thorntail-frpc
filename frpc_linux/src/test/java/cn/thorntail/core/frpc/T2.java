package cn.thorntail.core.frpc;

import com.sun.jna.Library;
import com.sun.jna.Native;


public class T2 {

  public interface FrpcLib extends Library {
    FrpcLib INSTANCE = Native.load("frpclib", FrpcLib.class);
    
    void test(String info);
    
    void run(String cfgFile);

  }



  public static void main(String[] args) {
    FrpcLib.INSTANCE.run("frpc.ini");
    // FrpcLib.INSTANCE.test("Jav call GO 测试");
  }

}
