package pama1234.gdx.game.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.badlogic.gdx.net.Socket;

public class SocketData{
  // public int authCooling;//TODO server only
  //---
  public boolean stop;
  public ClientState clientState=ClientState.ClientAuthentication;
  public ServerState serverState=ServerState.ServerAuthentication;//TODO why avoiding state 0???
  public String name;//TODO replace with FullToken data class
  //---
  public Socket s;
  public InputStream i;
  public OutputStream o;
  public SocketData(String name,Socket s) {
    this.name=name;
    this.s=s;
    i=s.getInputStream();
    o=s.getOutputStream();
  }
  public SocketData(Socket s) {
    this.s=s;
    i=s.getInputStream();
    o=s.getOutputStream();
  }
  public void dispose() {
    try {
      i.close();
      o.flush();
      o.close();
    }catch(IOException e) {
      e.printStackTrace();
    }finally {
      s.dispose();
    }
  }
}