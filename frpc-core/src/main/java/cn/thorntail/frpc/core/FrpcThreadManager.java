package cn.thorntail.frpc.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import com.sun.jna.Native;

import cn.thorntail.frpc.lib.FrpcLib;

/**
 * frpc线程管理器
 * 
 * @author Y13
 *
 */
public class FrpcThreadManager implements Runnable {
  private static final Logger logger = Logger.getLogger(FrpcThreadManager.class.getName());
  
  /**
   * 动态库对象,全局唯一即可
   */
  private static FrpcLib FRPC_INSTANCE = null;
  
  /**
   * 指定了动态库的位置
   * 
   * 注意该属性有点特别，理论上，也应该是全局唯一
   */
  private String libraryFile = null;
  
  /**
   * 管理器名称
   */
  private final String name;
  
  /**
   * 是否被禁用
   */
  private boolean disable = false;
  
  /**
   * frpc客户端
   */
  private Thread thread = null;
  
  /**
   * 指定了配置文件的位置
   */
  private String configFile = null;
  
  /**
   * 平台适配器
   */
  private final FrpcPlatformAdapter adapter;
  
  /**
   * bin文件夹
   */
  private String binDir;
  
  /**
   * 由System.getProperty获取配置的前缀
   */
  private final String configPrefix;
  
  public FrpcThreadManager(FrpcPlatformAdapter adapter) {
    this("thorntail", "thorntail.frpc", adapter);
  }
  
  public FrpcThreadManager(String configPrefix, FrpcPlatformAdapter adapter) {
    this("thorntail", configPrefix, adapter);
  }
  
  public FrpcThreadManager(String name, String configPrefix, FrpcPlatformAdapter adapter) {
    this.name = name;
    this.disable = Boolean.getBoolean(configPrefix + ".disable");
    this.adapter = adapter;
    this.configFile = System.getProperty(configPrefix + ".config-ref");
    this.libraryFile = System.getProperty(configPrefix +  "library-ref");
    this.configPrefix = configPrefix + ".config";
    binDir = System.getProperty(configPrefix + ".bin-dir", "target/frpc");
  }
  
  /**
   * 获取当前执行的线程
   * @return
   */
  public Thread getCurrentThread() {
    return thread;
  }
  
  public boolean isDisable() {
    return disable;
  }
  
  /**
   * 终止, 由于线程在golang上，所以这里使用stop直接杀死进程
   * 
   * 该方法无法正确杀死线程
   */
  @Deprecated
  public void stop() {
    logger.warning("stop方法无法正确关闭线程");
    if (thread != null && thread.isAlive()) {
      thread.interrupt();
      thread.stop();
      logger.info("[FRPC]" + name + ": 线程已经关闭 T" + thread.getId());
    }
  }

  @Override
  public synchronized void run() {
    if (disable) {
      throw new RuntimeException("[FRPC]" + name + "服务已经被禁用");
    }
    if (thread == null || !thread.isAlive()) {
      // 线程不存在或者已经死亡
      buildFrpcThread();
      // 启动线程
      thread.start();
      logger.info("[FRPC]" + name + ": 线程已经启动完成：T" + thread.getId());
    } else {
      // 线程已经启动，忽略重复启动
      logger.info("[FRPC]" + name + ": 线程已经启动，忽略重复启动");
    }
  }
  
  /**
   * 启动客户端
   */
  private void runClient() {
    FRPC_INSTANCE.run(configFile);
  }

  /**
   * 构建线程
   */
  private void buildFrpcThread() {
    if (thread == null) {
      adapter.init();
      
      File binDirFile = new File(binDir);
      if (!binDirFile.exists()) {
        binDirFile.mkdirs();
      }
      Path binDirPath = binDirFile.toPath();
      
      String nm = "java_" + name;
      // config file
      if (configFile == null) {
        try {
          Ini config = getSystemProperty2Ini(configPrefix);
          Path path = Files.createTempFile(binDirPath, nm, "frpc.ini");
          File file = path.toFile();
          // 系统推出时候删除
          file.deleteOnExit();
          
          config.store(file);
          
          configFile = file.getPath();
          logger.info("[FRPC]" + name + "构建临时配置文件完成：" + configFile);
        } catch (IOException e) {
          throw new RuntimeException("初始化配置文件发生异常", e);
        }
      }

      // library file
      if (FRPC_INSTANCE == null) {
        synchronized (FrpcThreadManager.class) {
          if (FRPC_INSTANCE == null) {
            if (libraryFile == null) {
              try {
                Path path = binDirPath.resolve(nm + adapter.getVersion() + adapter.getLibraryName());
                File file = path.toFile();
                if (!file.exists()) {
                  InputStream libStream = adapter.getLibraryStream();
                  Files.copy(libStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                libraryFile = file.getPath();
                logger.info("[FRPC]" + name + "构建临时配置GO库完成：" + libraryFile);
              } catch (IOException e) {
                throw new RuntimeException("初始化GO库文件发生异常", e);
              }
            }
            FRPC_INSTANCE = Native.load(libraryFile, FrpcLib.class);
          }
        }
      }
    }
    thread = new Thread(this::runClient, "[FRPC]" + name);
    // 标记为守护进程，不影响系统退出
    thread.setDaemon(true);
  }

  /**
   * <p> 通过系统环境变量指定监听的对象
   * 
   * com.suisrc.key.*
   * 
   * @param key
   */
  private Ini getSystemProperty2Ini(String key) {
    String prefix = key.endsWith(".") ? key : (key + ".");
    int prelen = prefix.length();
    
    Ini ini = new Ini();
    
    System.getProperties().forEach((k, v) -> {

      if (v != null && (k instanceof String) && k.toString().startsWith(prefix)) {
        String newKey = k.toString().substring(prelen);
        String[] keys = newKey.split("\\.");
        if (keys == null || keys.length < 1) {
          return;
        }
        Section current = ini.add(keys[0]);
        for (int i = 1; i < keys.length - 1; i++) {
          current = current.addChild(keys[i]);
        }
        current.add(keys[keys.length - 1], v);

      }
    });
    return ini;
  }
}
