package cn.thorntail.core.frpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FrpcProcess {

  public static void main(String[] args) throws IOException, InterruptedException {
    ProcessBuilder builder = new ProcessBuilder();
    builder.command("target/frpc.exe", "-c", "target/frpc.ini");
    builder.inheritIO();

    Process process = builder.start();

    // new ErrStreamRead(process).run();
    TimeUnit.SECONDS.sleep(10);
    // process.waitFor(10, TimeUnit.SECONDS);
    process.destroy();

    System.out.println("OK");
  }
}
