package pama1234.gdx.game.state.state0001.game.player;

import pama1234.gdx.game.app.Screen0011;
import pama1234.gdx.game.state.state0001.Game;
import pama1234.gdx.game.state.state0001.game.item.IntItem;
import pama1234.gdx.game.state.state0001.game.item.Inventory;
import pama1234.gdx.game.state.state0001.game.world.World0001;
import pama1234.gdx.util.element.CameraController2D;
import pama1234.gdx.util.info.TouchInfo;
import pama1234.math.Tools;

public class MainPlayer2D extends Player2D{
  public CameraController2D cam;
  public PlayerController2D ctrl;
  public Inventory<IntItem> inventory;
  public MainPlayer2D(Screen0011 p,World0001 pw,float x,float y,Game pg) {//TODO type
    super(p,pw,x,y,pw.metaEntitys.player,pg);
    this.cam=p.cam2d;
    ctrl=new PlayerController2D(p,this);
    inventory=new Inventory<>(this,32,9);
    inventory.data[0].item=pw.metaItems.dirt.createItem();//TODO
  }
  @Override
  public void keyPressed(char key,int keyCode) {
    ctrl.keyPressed(key,keyCode);
  }
  @Override
  public void keyReleased(char key,int keyCode) {
    ctrl.keyReleased(key,keyCode);
  }
  @Override
  public void touchStarted(TouchInfo info) {
    ctrl.touchStarted(info);
  }
  @Override
  public void update() {
    for(TouchInfo e:p.touches) if(e.active) ctrl.touchUpdate(e);
    ctrl.updateOuterBox();
    //-------------------------------------------------------
    ctrl.updateCtrlInfo();
    ctrl.doWalkAndJump();
    super.update();
    ctrl.constrain();
    //---
    p.cam.point.des.set(cx(),Tools.mag(point.y(),ctrl.limitBox.floor)<48?ctrl.limitBox.floor+type.dy+type.h/2f:cy(),0);
    //---
    life.update();
    inventory.update();
  }
  @Override
  public void display() {
    super.display();
    ctrl.display();
    inventory.displayHotSlot();
  }
}