package pama1234.gdx.game.state.state0001.game.net;

import com.esotericsoftware.kryo.io.Output;

import pama1234.gdx.game.state.state0001.game.net.NetState.ClientToServer;
import pama1234.gdx.game.state.state0001.game.player.PlayerControllerCore;
import pama1234.util.function.ExecuteF;

public class ClientWrite extends Thread{
  public ClientCore p;
  public Output output;
  public int sleep=-1;
  public ExecuteF[] executeFs;
  public boolean left,right,jump,jumpDown;
  public boolean dleft,dright,djump,djumpDown;
  public boolean writePlayerCtrl;
  public int state=ClientToServer.playerAuth;
  public ClientWrite(ClientCore p) {
    this.p=p;
    output=new Output(p.socketData.o);
    executeFs=new ExecuteF[] {this::writePlayerCtrl,this::writePlayerAuth};
  }
  @Override
  public void run() {
    try {
      connect();
      while(!p.stop) {
        execute();
        if(sleep>=0) sleep(sleep);
      }
    }catch(RuntimeException|InterruptedException e) {
      e.printStackTrace();
      p.stop=true;
    }finally {
      disconnect();
    }
  }
  public void connect() {
    sleep=0;
  }
  public void execute() {
    updatePlayerCtrl();
    //---
    if(state==ClientToServer.playerCtrl&&!writePlayerCtrl) return;
    output.writeByte(state);
    executeFs[state].execute();
    output.flush();
    // updatePlayerCtrl();
  }
  public void updatePlayerCtrl() {
    PlayerControllerCore ctrl=p.world.yourself.ctrl;
    dleft=left!=ctrl.left;
    dright=right!=ctrl.right;
    djump=jump!=ctrl.jump;
    djumpDown=jumpDown!=ctrl.jumpDown;
    writePlayerCtrl=dleft||dright||djump||djumpDown;
    // writePlayerCtrl();
  }
  public void writePlayerCtrl() {
    // if(!writePlayerCtrl) return;
    output.writeBoolean(dleft);
    output.writeBoolean(dright);
    output.writeBoolean(djump);
    output.writeBoolean(djumpDown);
    clearPlayerCtrlCache(p.world.yourself.ctrl);
  }
  public void writePlayerAuth() {
    output.writeString(p.world.yourself.name);
    state=ClientToServer.playerCtrl;
  }
  public void clearPlayerCtrlCache(PlayerControllerCore ctrl) {
    left=ctrl.left;
    right=ctrl.right;
    jump=ctrl.jump;
    jumpDown=ctrl.jumpDown;
  }
  public void disconnect() {}
}