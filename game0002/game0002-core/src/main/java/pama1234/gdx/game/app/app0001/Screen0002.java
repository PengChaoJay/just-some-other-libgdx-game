package pama1234.gdx.game.app.app0001;

import pama1234.game.app.server.server0001.particle.with2d.CellGroup2D;
import pama1234.game.app.server.server0001.particle.with2d.CellGroupGenerator2D;
import pama1234.gdx.util.app.UtilScreen2D;

/**
 * 2D 粒子系统 警告：未维护
 */
@Deprecated
public class Screen0002 extends UtilScreen2D{
  public CellGroup2D group;
  public boolean doUpdate;
  public Thread cellUpdate;
  // ServerPlayerCenter<Player2D> playerCenter;
  @Override
  public void setup() {
    backgroundColor(0);
    CellGroupGenerator2D gen=new CellGroupGenerator2D(0,0);
    group=gen.GenerateFromMiniCore();
    // playerCenter=new ServerPlayerCenter<Player2D>();
    noStroke();
    cellUpdate=createUpdateThread();
    cellUpdate.start();
  }
  public Thread createUpdateThread() {
    return new Thread("CellGroupUpdateThread") {
      @Override
      public void run() {
        while(!stop) {
          if(doUpdate) group.update();
          else try {
            sleep(1000);
          }catch(InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    };
  }
  @Override
  public void update() {
    group.update();
    // playerCenter.update();
  }
  @Override
  public void displayWithCam() {
    int circleSeg=circleSeg(CellGroup2D.SIZE*cam2d.scale.pos*(1/cam.frameScale));
    for(int i=0;i<group.size;i++) {
      float tx=group.x(i),
        ty=group.y(i);
      if(!cam2d.inbox(tx,ty)) continue;
      fillHex(group.color(i));
      circle(tx,ty,CellGroup2D.SIZE,circleSeg);
    }
  }
  @Override
  public void display() {}
  @Override
  public void frameResized() {}
}