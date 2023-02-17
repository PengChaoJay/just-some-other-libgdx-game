package pama1234.gdx.game.state.state0001.game.net;

import static pama1234.gdx.game.state.state0001.game.net.NetUtil.catchException;
import static pama1234.gdx.game.state.state0001.game.net.NetUtil.debug;
import static pama1234.gdx.game.state.state0001.game.net.NetUtil.protocolVersion;
import static pama1234.gdx.game.state.state0001.game.net.NetUtil.readNBytes;
import static pama1234.gdx.game.state.state0001.game.net.NetUtil.writeServerHeader;

import java.io.IOException;
import java.net.SocketException;

import pama1234.data.ByteUtil;
import pama1234.gdx.game.state.state0001.game.net.NetState0002.ClientState;
import pama1234.gdx.game.state.state0001.game.net.ServerExecute.ServerReadF;

public class ServerCore{
  public static class ServerRead extends Thread{
    public ServerReadF[] fArray;
    public ServerData p;
    public SocketData s;
    public ServerRead(ServerData pIn,SocketData dataSocket) {
      super("ServerRead "+dataSocket.s.getRemoteAddress());
      this.p=pIn;
      this.s=dataSocket;
      fArray=ServerExecute.generateReadF();
    }
    @Override
    public void run() {
      byte[] data=new byte[128];
      while(!s.stop) {
        synchronized(p.socketCenter.list) {
          try {
            int stateInt=ByteUtil.byteToInt(readNBytes(s,data,0,4),0);
            doF(s,data,
              null,stateInt,
              ByteUtil.byteToInt(readNBytes(s,data,0,4),0));
          }catch(SocketException e1) {
            catchException(e1,s);
          }catch(IOException e2) {
            catchException(e2,s);
          }
        }
      }
      // p.serverReadPool.remove.add(this);
    }
    public void doF(SocketData s,byte[] inData,ClientState state,int stateInt,int readSize) throws IOException {
      if(debug) System.out.println("ServerRead state="+state+" readSize="+readSize);
      fArray[stateInt].execute(p,s,inData,readSize);
    }
    public void dispose() {
      s.stop=true;
    }
  }
  public static class ServerWrite extends Thread{
    public ServerData p;
    public SocketData s;
    public ServerWrite(ServerData p,SocketData dataSocket) {
      super("ServerWrite "+dataSocket.s.getRemoteAddress());
      this.p=p;
      this.s=dataSocket;
    }
    @Override
    public void run() {
      byte[] data=new byte[20];
      while(!s.stop) {
        synchronized(p.group) {
          try {
            doF(s,data);
          }catch(SocketException e1) {
            catchException(e1,s);
          }catch(IOException e2) {
            catchException(e2,s);
          }
        }
      }
      // p.serverWritePool.remove.add(this);
    }
    public void doF(SocketData e,byte[] outData) throws IOException {
      if(debug) System.out.println("ServerWrite state="+e.serverState);
      // if(e.state==1)
      switch(e.serverState) {
        case ServerAuthentication: {
          writeServerHeader(e,outData,4);
          e.o.write(ByteUtil.intToByte(1234,outData,0),0,4);
          e.o.flush();
        }
          break;
        case ServerDataTransfer: {
          writeServerHeader(e,outData,p.group.size);
          // p.println(1,p.group.size);
          for(int i=0;i<p.group.size;i++) {
            ByteUtil.intToByte(i,outData,0);
            ByteUtil.intToByte(p.group.type[i],outData,4);
            ByteUtil.floatToByte(p.group.x(i),outData,8);
            ByteUtil.floatToByte(p.group.y(i),outData,12);
            ByteUtil.floatToByte(p.group.z(i),outData,16);
            e.o.write(outData);
          }
          e.o.flush();
        }
          break;
        case ServerException: {}
          break;
        case ServerFinishedProcessing: {}
          break;
        case ServerProcessing: {}
          break;
        case ServerProtocolVersion: {
          byte[] nameBytes=protocolVersion.getBytes();
          writeServerHeader(s,outData,nameBytes.length);
          s.o.write(nameBytes);
          s.o.flush();
          p.sleep(1000);
        }
          break;
        case ServerSendSceneChange: {}
          break;
        case ServerSendStringMessage: {}
          break;
        default:
          throw new RuntimeException("state err="+e.clientState);
      }
    }
    public void dispose() {
      s.stop=true;
    }
  }
}
