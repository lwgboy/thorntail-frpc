package cn.thorntail.frpc.windows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import cn.thorntail.frpc.core.FrpcPlatformAdapter;

/**
 * windows平台配置信息
 * 
 * @author Y13
 *
 */
public class FrpcWindowsAdapter implements FrpcPlatformAdapter {

  @Override
  public void init() {
    // do nothing
  }

  @Override
  public InputStream getLibraryStream() throws IOException {
    // 这里需要使用zip文件, 否则会发生异常
    // return getClass().getClassLoader().getResourceAsStream("libs/frpclib.dll");
    InputStream libStream = getClass().getClassLoader().getResourceAsStream("libs/frpclib.zip");
    ZipInputStream zip = new ZipInputStream(libStream);
    ZipEntry ze;
    while ((ze = zip.getNextEntry()) != null) {
      if (!ze.getName().equals("frpclib.dll")) {
        continue;
      }
      // byte[] bytes = new byte[(int) ze.getSize()];
      // zip.read(bytes);
      // libStream = new ByteArrayInputStream(bytes);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[2048];
      int len;
      while ((len = zip.read(buffer)) > 0) {
        out.write(buffer, 0, len);
      }
      libStream = new ByteArrayInputStream(out.toByteArray());
      break;
    }
    zip.closeEntry();

    return libStream;
  }

  @Override
  public String getLibraryName() {
    return "frpclib.dll";
  }

  @Override
  public String getVersion() {
    return "019081001";
  }

}
