package pama1234.gdx.game.dimensional.tower.defense;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

import pama1234.gdx.game.dimensional.tower.defense.util.math.physics.HighMassPoint;
import pama1234.gdx.game.dimensional.tower.defense.util.math.vec.Vec12f;
import pama1234.gdx.game.dimensional.tower.defense.util.player.HighPlayer;
import pama1234.gdx.util.FileUtil;
import pama1234.gdx.util.app.UtilScreen3D;
import pama1234.gdx.util.element.Graphics;

public class DemonDefense extends UtilScreen3D{
  public TextureRegion playerImage;
  public HighPlayer yourself;
  @Override
  public void setup() {
    backgroundColor(0);
    cam3d.point.des.set(0,0,-20);
    Graphics g=new Graphics(this,16,16);
    noStroke();
    g.beginShape();
    fill(255,127);
    circle(8,8,8);
    fill(255);
    circle(8,8,4);
    g.endShape();
    playerImage=FileUtil.toTextureRegion(g.texture);
    yourself=new HighPlayer(this,new HighMassPoint(new Vec12f()),createPlayerDecal());
    centerCam.add.add(yourself);
  }
  public Decal createPlayerDecal() {
    return Decal.newDecal(16,16,playerImage,true);
  }
  @Override
  public void update() {}
  @Override
  public void display() {}
  @Override
  public void displayWithCam() {
    flushDecal();
  }
  @Override
  public void frameResized() {}
}