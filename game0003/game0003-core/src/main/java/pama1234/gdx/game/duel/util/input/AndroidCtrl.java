package pama1234.gdx.game.duel.util.input;

import pama1234.gdx.game.duel.Duel;
import pama1234.gdx.game.state.state0002.Game;
import pama1234.gdx.game.ui.util.TextButton;
import pama1234.gdx.util.entity.Entity;
import pama1234.gdx.util.info.TouchInfo;
import pama1234.math.UtilMath;

public class AndroidCtrl extends Entity<Duel>{
  public Game pg;
  public TextButton<?>[] buttons;
  public float maxDist;
  public float magCache;
  public float dxCache,dyCache;
  public TouchInfo moveCtrl;
  public AndroidCtrl(Duel p,Game pg) {
    super(p);
    this.pg=pg;
  }
  @Override
  public void init() {
    buttons=UiGenerator.genButtons_0001(p);
    updateMaxDist();
  }
  @Override
  public void resume() {
    for(TextButton<?> e:buttons) p.centerScreen.add.add(e);
  }
  @Override
  public void pause() {
    for(TextButton<?> e:buttons) p.centerScreen.remove.add(e);
  }
  /**
   * cam only
   */
  @Override
  public void display() {
    if(moveCtrl!=null) {
      p.doStroke();
      p.stroke(0);
      p.strokeWeight(2);
      p.cross(moveCtrl.sx,moveCtrl.sy,32,32);
      p.line(moveCtrl.x,moveCtrl.y,moveCtrl.sx,moveCtrl.sy);
      p.cross(moveCtrl.x,moveCtrl.y,16,16);
      float deg=UtilMath.deg(UtilMath.atan2(dxCache,dyCache));
      p.arc(moveCtrl.sx,moveCtrl.sy,magCache,45-deg,90);
      p.noStroke();
    }
  }
  @Override
  public void update() {
    if(!pg.paused) {
      if(moveCtrl!=null) {
        dxCache=moveCtrl.x-moveCtrl.sx;
        dyCache=moveCtrl.y-moveCtrl.sy;
        pg.currentInput.targetTouchMoved(dxCache,dyCache,magCache=UtilMath.min(UtilMath.mag(dxCache,dyCache),maxDist));
      }
    }
  }
  @Override
  public void touchStarted(TouchInfo info) {
    if(info.osx<p.width/2f) {
      if(moveCtrl==null) moveCtrl=info;
    }
  }
  @Override
  public void touchEnded(TouchInfo info) {
    if(moveCtrl==info) {
      moveCtrl=null;
      pg.currentInput.dx=0;
      pg.currentInput.dy=0;
      magCache=0;
    }
  }
  @Override
  public void frameResized(int w,int h) {
    updateMaxDist();
  }
  public void updateMaxDist() {
    maxDist=p.u;
  }
}
