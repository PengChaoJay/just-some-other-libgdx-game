package pama1234.gdx.util.element;

import static pama1234.math.UtilMath.min;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;

import pama1234.gdx.util.app.UtilScreen;
import pama1234.gdx.util.entity.Entity;
import pama1234.gdx.util.info.TouchInfo;
import pama1234.math.Tools;
import pama1234.math.physics.PathPoint3D;
import pama1234.math.physics.PathVar;

public abstract class CameraController extends Entity<UtilScreen>{
  public Camera camera;
  // public PathPoint point;
  public PathPoint3D point;
  public PathVar scale,rotate;
  public float frameU,frameScale;
  public float scx,scy;
  public float asox,asoy;
  public float bsox,bsoy;
  public float bavgsox,bavgsoy;
  public float iScale,iDist;
  public TouchInfo a,b;
  public boolean active;
  public boolean activeDrag=true,activeZoom=true;
  @Deprecated
  public CameraController(UtilScreen p) {
    super(p);
  }
  public CameraController(UtilScreen p,float x,float y,float z) {
    super(p);
    point=new PathPoint3D(x,y,z);
  }
  public abstract void preResizeEvent(int w,int h);
  public boolean inbox(float x,float y) {
    return Tools.inBoxCenter(x,y,x(),y(),w(),h());
  }
  //TODO
  @Deprecated
  public boolean inbox(float x,float y,float z) {
    return false;
  }
  public float w() {
    return (p.width*frameScale)/scale.pos;
  }
  public float h() {
    return (p.height*frameScale)/scale.pos;
  }
  @Override
  public void frameResized(int w,int h) {
    frameScale=frameU/min(w,h);
  }
  // public abstract float x();
  // public abstract float y();
  // public abstract float z();
  public float x() {
    return point.x();
  }
  public float y() {
    return point.y();
  }
  public float z() {
    return point.z();
  }
  public float isx(float ftScale) {
    return MathUtils.floor(x()/ftScale)*ftScale;
  }
  public float isy(float ftScale) {
    return MathUtils.floor(y()/ftScale)*ftScale;
  }
  public int ix() {
    return MathUtils.floor(x());
  }
  public int iy() {
    return MathUtils.floor(y());
  }
  public float fx() {
    return Tools.fractionalPart(x());
  }
  public float fy() {
    return Tools.fractionalPart(y());
  }
}