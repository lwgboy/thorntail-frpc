package cn.thorntail.core.frpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.thorntail.frpc.core.FrpcThreadManager;
import cn.thorntail.frpc.windows.FrpcWindowsAdapter;

public class T4 {

  public static void main(String[] args) throws IOException, InterruptedException {
    
    System.setProperty("thorntail.frpc.config-ref", "frpc.ini");

    FrpcThreadManager ftm = new FrpcThreadManager(new FrpcWindowsAdapter());
    ftm.run();
    
    TimeUnit.SECONDS.sleep(5);

  }

}
