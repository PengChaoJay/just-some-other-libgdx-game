package pama1234.gdx.game.util;

import pama1234.util.function.GetFloat;

public class RectF{
  public GetFloat x,y,w,h;
  public RectF(GetFloat x,GetFloat y,GetFloat w,GetFloat h) {
    this.x=x;
    this.y=y;
    this.w=w;
    this.h=h;
  }
  public float x() {
    return x.get();
  }
  public float y() {
    return y.get();
  }
  public float w() {
    return w.get();
  }
  public float h() {
    return h.get();
  }
  //------------------
  //TODO
  public float x1() {
    return x.get();
  }
  public float y1() {
    return y.get();
  }
  public float x2() {
    return w.get();
  }
  public float y2() {
    return h.get();
  }
}
