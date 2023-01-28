package pama1234.game.app.server.server0002;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;

import pama1234.game.app.server.ServerCore;
import pama1234.util.net.ServerInfo;

public class Server0002 extends ServerCore{
  public Kryo kryo;
  public File mainDir=new File(System.getProperty("user.dir")+"/data/server/server0002");
  public File settingsFile=new File(mainDir,"settings.bin");
  public ServerSettings settings;
  public ScannerThread scannerThread;
  public boolean doUpdate=true;
  @Override
  public void init() {
    initKryo();
    // settingsFile.getParentFile().mkdirs();
    mainDir.mkdirs();
    loadSettings();
    serverInfo=settings.serverInfo;
    scannerThread=new ScannerThread(this);
    scannerThread.start();
  }
  public void initKryo() {
    kryo=new Kryo();
    kryo.setDefaultSerializer(TaggedFieldSerializer.class);
    kryo.register(ServerSettings.class);
    kryo.register(ServerInfo.class,new FieldSerializer<ServerInfo>(kryo,ServerInfo.class));
  }
  public void loadSettings() {
    if(!settingsFile.exists()) {
      settings=createServerSettings();
      return;
    }
    try(Input input=new Input(new FileInputStream(settingsFile))) {
      ServerSettings out=kryo.readObject(input,ServerSettings.class);
      input.close();
      settings=out;
    }catch(FileNotFoundException|KryoException e) {
      e.printStackTrace();
    }
    if(settings==null) settings=createServerSettings();
  }
  public ServerSettings createServerSettings() {
    ServerSettings out=new ServerSettings();
    out.serverInfo=new ServerInfo("127.0.0.1",12347);
    return out;
  }
  public void saveSettings() {
    try(Output output=new Output(new FileOutputStream(settingsFile))) {
      kryo.writeObject(output,settings);
      output.close();
    }catch(FileNotFoundException|KryoException e) {
      e.printStackTrace();
    }
  }
  @Override
  public void update() {}
  @Override
  public void dispose() {
    saveSettings();
  }
}
