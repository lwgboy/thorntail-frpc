package cn.thorntail.core.frpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.thorntail.core.frpc.linux.FrpcLinuxAdapter;
import cn.thorntail.frpc.core.FrpcThreadManager;

public class T4 {

  public static void main(String[] args) throws IOException, InterruptedException {
    
    System.setProperty("thorntail.frpc.config-ref", "frpc.ini");

    FrpcThreadManager ftm = new FrpcThreadManager(new FrpcLinuxAdapter());
    ftm.run();
    
    TimeUnit.SECONDS.sleep(5);

  }

}
