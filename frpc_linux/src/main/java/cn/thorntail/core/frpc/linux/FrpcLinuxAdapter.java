package cn.thorntail.core.frpc.linux;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import cn.thorntail.frpc.core.FrpcPlatformAdapter;

/**
 * linux平台配置信息
 * 
 * @author Y13
 *
 */
public class FrpcLinuxAdapter implements FrpcPlatformAdapter {

  @Override
  public void init() {
    // do nothing
  }

  @Override
  public InputStream getLibraryStream() throws IOException {
    InputStream libStream = getClass().getClassLoader().getResourceAsStream("libs/frpclib.zip");
    ZipInputStream zip = new ZipInputStream(libStream);
    ZipEntry ze;
    while ((ze = zip.getNextEntry()) != null) {
      if (!ze.getName().equals("frpclib.so")) {
        continue;
      }
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
    return "frpclib.so";
  }

  @Override
  public String getVersion() {
    return "019081301";
  }

}
