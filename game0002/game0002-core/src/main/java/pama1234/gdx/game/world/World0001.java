package pama1234.gdx.game.world;

import static com.badlogic.gdx.math.MathUtils.map;
import static pama1234.math.UtilMath.dist;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

import pama1234.game.app.server.server0001.game.ServerPlayer3D;
import pama1234.game.app.server.server0001.game.particle.CellGroup3D;
import pama1234.game.app.server.server0001.game.particle.CellGroupGenerator3D;
import pama1234.game.app.server.server0001.particle.Var;
import pama1234.gdx.game.app.app0001.Screen0001;
import pama1234.gdx.game.app.app0001.Screen0001.DecalData;
import pama1234.gdx.game.app.app0001.Screen0001.GraphicsData;
import pama1234.gdx.game.util.ClientPlayerCenter3D;
import pama1234.gdx.game.util.ControllerClientPlayer3D;
import pama1234.gdx.util.element.Graphics;
import pama1234.gdx.util.entity.Entity;
import pama1234.gdx.util.listener.DisplayEntityListener;
import pama1234.math.UtilMath;
import pama1234.math.vec.Vec3f;

public class World0001 extends Entity<Screen0001> implements DisplayEntityListener{
  public CellGroup3D group;
  @Deprecated
  public ClientPlayerCenter3D playerCenter;//TODO
  public ControllerClientPlayer3D yourself;
  public ArrayList<ArrayList<GraphicsData>> graphicsList;
  public ArrayList<DecalData>[] decals;
  public boolean doUpdate;
  public Thread updateCell;
  // public Vector3 posCache=new Vector3();
  public static final int layerSize=3;
  public static final int gsize=8;
  public float[][] tesselatedMat= {
    {0,0,0},{1,0,0},
    {0,1,0},{1,1,0},
    //---
    {0,0,1},{1,0,1},
    {0,1,1},{1,1,1},
  };
  public Vec3f size;
  public World0001(Screen0001 p) {
    super(p);
  }
  @Override
  public void init() {
    CellGroupGenerator3D gen=new CellGroupGenerator3D(0,0);
    if(p.random(1)>0.5f) group=gen.randomGenerate(64,p.isAndroid?1024:16384);
    else group=p.isAndroid
      ?gen.generateFromMiniCore(128,128)
      :gen.generateFromMiniCore(640,1024);
    size=new Vec3f(
      group.updater.x2-group.updater.x1,
      group.updater.y2-group.updater.y1,
      group.updater.z2-group.updater.z1);
    p.cam3d.viewDist(UtilMath.min(size.x,size.y,size.z)/2f);
    playerCenter=new ClientPlayerCenter3D(p);
    yourself=new ControllerClientPlayer3D(p,new ServerPlayer3D(
      "pama"+String.format("%04d",(int)(p.random(0,10000))),
      0,0,0));
    p.noStroke();
    graphicsList=new ArrayList<ArrayList<GraphicsData>>(layerSize);
    decals=new ArrayList[tesselatedMat.length];
    for(int i=0;i<decals.length;i++) decals[i]=new ArrayList<>(group.size);
    final int typeSize=group.colors.length;
    int tsize=group.size/typeSize;
    int[] colors=new int[typeSize];
    for(int i=0;i<colors.length;i++) colors[i]=group.colors[i];
    graphicsList.add(0,new ArrayList<GraphicsData>(typeSize));
    for(int i=0;i<typeSize;i++) {
      int tgsize=p.tgsizeF(0);
      Graphics tg=new Graphics(p,tgsize*2,tgsize*2);
      tg.beginShape();
      p.fillHex(colors[i]&0x40ffffff);
      p.circle(tgsize,tgsize,tgsize);
      p.fillHex(colors[i]);
      p.circle(tgsize,tgsize,tgsize/2);
      tg.endShape();
      TextureRegion tr=new TextureRegion(tg.texture);
      graphicsList.get(0).add(0*typeSize+i,new GraphicsData(tg,tr));
      for(int k=0;k<decals.length;k++) for(int j=0;j<tsize;j++) {
        Decal td=Decal.newDecal(Var.DIST,Var.DIST,tr,true);
        decals[k].add(i*tsize+j,new DecalData(td,0));
      }
    }
    for(int k=1;k<layerSize;k++) {
      graphicsList.add(k,new ArrayList<GraphicsData>(typeSize));
      for(int i=0;i<typeSize;i++) {
        int tgsize=p.tgsizeF(k);
        Graphics tg=new Graphics(p,tgsize*2,tgsize*2);
        tg.beginShape();
        p.fillHex(colors[i]&0x40ffffff);
        p.circle(tgsize,tgsize,tgsize);
        p.fillHex(colors[i]);
        p.circle(tgsize,tgsize,tgsize/2);
        tg.endShape();
        TextureRegion tr=new TextureRegion(tg.texture);
        graphicsList.get(k).add(i,new GraphicsData(tg,tr));
      }
    }
    updateCell=new Thread() {
      @Override
      public void run() {
        while(!p.stop) {
          if(doUpdate) group.update();
          else try {
            sleep(1000);
          }catch(InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    };
    updateCell.start();
    //---
    p.centerCam.add.add(playerCenter);
    p.centerCam.add.add(yourself);//TODO
  }
  @Override
  public void displayCam() {
    synchronized(group) {
      for(int j=0;j<tesselatedMat.length;j++) {
        // translate float array
        float[] tfa=tesselatedMat[j];
        float lx=(tfa[0]+UtilMath.floor((p.cam3d.point.pos.x)/size.x))*size.x;//TODO
        float ly=(tfa[1]+UtilMath.floor((p.cam3d.point.pos.y)/size.y))*size.y;
        float lz=(tfa[2]+UtilMath.floor((p.cam3d.point.pos.z)/size.z))*size.z;
        for(int i=0;i<group.size;i++) {
          float tx=(group.x(i)+lx)*p.multDist;
          float ty=(group.y(i)+ly)*p.multDist;
          float tz=(group.z(i)+lz)*p.multDist;
          float tdist=dist(tx,ty,tz,p.cam.x(),p.cam.y(),p.cam.z());
          final DecalData tdd=decals[j].get(i);
          final Decal td=tdd.decal;
          td.setPosition(tx,ty,tz);
          if(!p.isVisible(p.cam.camera,td,Var.DIST/2)) continue;
          // temp layer float
          final int tlf=layerF(tdist);
          if(tlf!=tdd.layer) {
            tdd.layer=tlf;
            td.setTextureRegion(graphicsList.get(tlf).get(group.type[i]).tr);
          }
          td.lookAt(p.cam.camera.position,p.cam.camera.up);
          td.setColor(1,1,1,p.colorF(tdist));
          p.decal(td);
        }
      }
    }
  }
  public int layerF(float dist) {
    int out=(int)map(dist,0,p.cam3d.viewDist(),layerSize,0);
    if(out>=layerSize) out=layerSize-1;
    return out;
  }
}