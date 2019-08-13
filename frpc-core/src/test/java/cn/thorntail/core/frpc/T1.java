package cn.thorntail.core.frpc;

import java.io.IOException;
import java.io.InputStream;

import org.ini4j.Ini;
import org.junit.Test;

import cn.thorntail.frpc.core.FrpcPlatformAdapter;
import cn.thorntail.frpc.core.FrpcThreadManager;

public class T1 {

  @Test
  public void test1() {
    System.setProperty("test1.com.t1", "11");
    System.setProperty("test1.com.t2", "12");
    System.setProperty("test1.com1.t3", "13");
    System.setProperty("test1.com1.t4", "14");
    FrpcThreadManager ftm = new FrpcThreadManager(getFrpcPlatformAdapter());
    Ini ini = ftm.getSystemProperty2Ini("test1.");
    
    System.out.println(ftm.toDigestHex(ini.toString()));
  }

  public FrpcPlatformAdapter getFrpcPlatformAdapter() {
      return new FrpcPlatformAdapter() {
        
        @Override
        public void init() {
          // TODO Auto-generated method stub
          
        }
        
        @Override
        public String getVersion() {
          // TODO Auto-generated method stub
          return null;
        }
        
        @Override
        public InputStream getLibraryStream() throws IOException {
          // TODO Auto-generated method stub
          return null;
        }
        
        @Override
        public String getLibraryName() {
          // TODO Auto-generated method stub
          return null;
        }
    };
    
  }

}
