package pama1234.gdx.game.state.state0001.game.player;

import com.badlogic.gdx.Input.Buttons;

import pama1234.gdx.game.app.Screen0011;
import pama1234.gdx.game.state.state0001.game.Game;
import pama1234.gdx.game.state.state0001.game.region.Block;
import pama1234.gdx.game.state.state0001.game.world.World0001;
import pama1234.gdx.util.element.CameraController2D;
import pama1234.gdx.util.info.TouchInfo;
import pama1234.math.Tools;
import pama1234.math.UtilMath;
import pama1234.math.physics.PathVar;

public class MainPlayer2D extends Player2D{
  public CameraController2D cam;
  public boolean left,right,jump;
  public boolean inAir;
  public int walkCool,jumpCool;
  public float speed=1f;
  public float floor,leftWall,rightWall,ceiling;
  //---
  public float maxLife=32;
  public PathVar life=new PathVar(maxLife);
  public MainPlayer2D(Screen0011 p,World0001 pw,float x,float y,Game pg) {
    super(p,pw,x,y,pg);
    this.cam=p.cam2d;
  }
  // @Override
  // public void mousePressed(MouseInfo info) {
  //   mouseUpdate(info);
  // }
  public void mouseUpdate(TouchInfo info) {
    if(info.state!=0) return;
    Block block=getBlock(info.x,info.y);
    if(block!=null) switch(p.isAndroid?(pg.androidRightMouseButton?Buttons.RIGHT:Buttons.LEFT):info.button) {
      case Buttons.LEFT: {
        block.type=pw.metaBlockCenter.air;
      }
        break;
      case Buttons.RIGHT: {
        block.type=pw.metaBlockCenter.dirt;
      }
        break;
    }
  }
  @Override
  public void update() {
    for(TouchInfo e:p.touches) if(e.active) mouseUpdate(e);
    testPos();
    //-------------------------------------------------------
    left=p.isKeyPressed(29)||p.isKeyPressed(21);
    right=p.isKeyPressed(32)||p.isKeyPressed(22);
    jump=p.isKeyPressed(62);
    if(walkCool>0) walkCool--;
    else if(!(left==right)) {
      float speedMult=p.shift?2:1;
      if(left) {
        point.pos.x-=speed*speedMult;
        dir=true;
      }else {
        point.pos.x+=speed*speedMult;
        dir=false;
      }
    }
    inAir=point.pos.y<floor;
    if(inAir) {
      point.vel.y+=0.7f;
    }else {
      if(point.pos.y!=floor) {
        point.vel.y=0;
        point.pos.y=floor;
      }
      if(jumpCool>0) jumpCool--;
      else if(jump) {
        point.vel.y=-pw.blockHeight;
        jumpCool=2;
      }
    }
    super.update();
    if(point.pos.y>floor) {
      point.vel.y=0;
      point.pos.y=floor;
    }
    if(point.pos.y<ceiling) {
      if(point.vel.y<0) point.vel.y=0;
      point.pos.y=ceiling;
    }
    if(point.pos.x<leftWall) point.pos.x=leftWall;
    if(point.pos.x>rightWall) point.pos.x=rightWall;
    //---
    // pointer=(p.frameCount/10)%slides.length;
    //---
    // p.cam.point.des.set(point.x()+12.5f,Tools.mag(point.y(),groundLevel)<48?groundLevel+12.5f:point.y()+12.5f,0);
    p.cam.point.des.set(point.x(),Tools.mag(point.y(),floor)<48?floor+12.5f:point.y()+12.5f,0);
    //---
    life.update();
  }
  public int //
  bx1,by1,
    bx2,by2,
    bw,bh;
  public boolean flagCache;
  public void testPos() {
    // int bx1=blockX1(),
    //   by1=blockY1(),
    //   bx2=blockX2(),
    //   by2=blockY2(),
    //   bw=bx2-bx1,
    //   bh=by2-by1;
    bx1=blockX1();
    by1=blockY1();
    bx2=blockX2();
    by2=blockY2();
    bw=bx2-bx1;
    bh=by2-by1;
    // if(inAir) {
    //   by1-=1;
    //   bh+=1;
    // }
    // if(!inAir) bh-=1;
    Block block;
    flagCache=false;
    //------------------------------------------ floor
    for(int i=0;i<=bw;i++) {
      block=getBlock(bx1+i,by2+1);
      if(!isEmpty(block)) {
        flagCache=true;
        break;
      }
    }
    if(flagCache) {
      floor=(by2+1)*pw.blockHeight;
      flagCache=false;
    }else floor=(by2+4)*pw.blockHeight;
    //------------------------------------------ left
    // for(int i=inAir?-1:0;i<=bh;i++) {
    for(int i=0;i<=bh;i++) {
      block=getBlock(bx1-1,by1+i);
      if(!isEmpty(block)) {
        flagCache=true;
        break;
      }
    }
    if(flagCache) {
      leftWall=(bx1+0.5f)*pw.blockWidth+1;
      flagCache=false;
    }else leftWall=(bx1-4)*pw.blockWidth;
    //------------------------------------------ right
    for(int i=0;i<=bh;i++) {
      block=getBlock(bx2+1,by1+i);
      if(!isEmpty(block)) {
        flagCache=true;
        break;
      }
    }
    if(flagCache) {
      rightWall=(bx2+0.5f)*pw.blockWidth-1;
      flagCache=false;
    }else rightWall=(bx2+4)*pw.blockWidth;
    //------------------------------------------ ceiling
    for(int i=0;i<=bw;i++) {
      block=getBlock(bx1+i,by1-1);
      if(!isEmpty(block)) {
        flagCache=true;
        break;
      }
    }
    if(flagCache) {
      ceiling=by1*pw.blockHeight+h;
      flagCache=false;
    }else ceiling=(by1-4)*pw.blockHeight;
  }
  public boolean isEmpty(Block block) {
    return block==null||block.type.empty;
  }
  public Block getBlock(int xIn,int yIn) {
    return pw.regions.getBlock(xIn,yIn);
    // return pw.regions.getBlock(blockX(),blockY());
  }
  public Block getBlock(float xIn,float yIn) {
    return pw.regions.getBlock(xToBlockCord(xIn),yToBlockCord(yIn));
    // return pw.regions.getBlock(blockX(),blockY());
  }
  public int blockX() {
    return xToBlockCord(x());
  }
  public int blockY() {
    return yToBlockCord(y());
  }
  public int blockX1() {
    return xToBlockCord(x()+dx);
  }
  public int blockY1() {
    return yToBlockCord(y()+dy);
  }
  public int blockX2() {
    return xToBlockCord(x()+dx+w-0.01f);//TODO
  }
  public int blockY2() {
    return yToBlockCord(y()+dy+h-0.01f);//TODO
  }
  public int xToBlockCord(float in) {
    return UtilMath.floor(in/pw.blockWidth);
  }
  public int yToBlockCord(float in) {
    return UtilMath.floor(in/pw.blockHeight);
  }
}