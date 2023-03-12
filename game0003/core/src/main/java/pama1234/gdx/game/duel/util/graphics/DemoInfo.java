package pama1234.gdx.game.duel.util.graphics;

import com.badlogic.gdx.graphics.Texture;

import pama1234.gdx.game.duel.Duel;
import pama1234.gdx.util.element.Graphics;
import pama1234.gdx.util.entity.Entity;
import pama1234.math.UtilMath;

public class DemoInfo extends Entity<Duel>{
  public Graphics text;
  public DemoInfo(Duel p) {
    super(p);
    text=new Graphics(p,576,460);
    text.begin();
    p.beginShape();
    p.beginBlend();
    p.background(255,200);
    p.doStroke();
    p.stroke(0);
    p.strokeWeight(2);
    // p.doFill();
    // p.fill(255,220);
    p.noFill();
    p.rect(0,0,text.texture.getWidth(),text.texture.getHeight());
    p.endBlend();
    p.endShape();
    p.setTextScale(1);
    p.strokeWeight(1);
    drawText_ch(p,UtilMath.min(p.width,p.height));
    text.end();
  }
  public static void displayDemo(Duel p) {
    int fu=UtilMath.min(p.width,p.height);
    // p.beginBlend();
    // p.doStroke();
    // p.stroke(0);
    // p.strokeWeight(2);
    // p.doFill();
    // p.fill(255,200);
    // p.pushMatrix();
    // float dx=(p.width-fu)/2f;
    // float dy=(p.height-fu)/2f;
    // p.translate(dx,dy);
    // p.rect(fu*0.05f,fu*0.05f,
    //   fu*0.9f,
    //   fu*0.9f);
    // p.endBlend();
    // p.popMatrix();
    // duel.translate(-(duel.width-fu)/2f,-(duel.height-fu)/2f);
    // p.scale(UtilMath.max(1,p.pus));
    Texture img=p.demoInfo.text.texture;
    int tf=UtilMath.max(1,(int)(fu/img.getHeight()));
    // System.out.println(tf);
    int tw=img.getWidth()*tf;
    int th=img.getHeight()*tf;
    // p.endShape();
    // p.endBlend();
    // p.beginBlend();
    p.image(img,(p.width-tw)/2f,(p.height-th)/2f,tw,th);
    // p.endBlend();
    // p.beginBlend();
    // p.beginShape();
    // duel.setTextColor(0);
    // drawText_ch(duel,fu);
    // p.setTextScale(1);
    // p.strokeWeight(1);
  }
  public static void drawText_en(Duel duel,int fu) {
    duel.drawText("    Z key:",200,180);
    duel.drawText("    X key:",200,250);
    duel.drawText("Arrow key:",200,345);
    duel.drawText("Weak shot\n (auto aiming)",300,180);
    duel.drawText("Lethal shot\n (manual aiming,\n  requires charge)",300,250);
    duel.drawText("Move\n (or aim lethal shot)",300,345);
    duel.drawText("- Press Z key to start -",192,430);
    duel.drawText("(Click to hide this window)",192,475);
  }
  public static void drawText_ch(Duel duel,int fu) {
    duel.setTextScale(3);
    duel.drawText("几何决斗！",180,20);
    duel.setTextScale(1);
    duel.drawText("      Z 按键:",180,100);
    duel.drawText("      X 按键:",180,170);
    duel.drawText("左手触摸屏幕:",180,245);
    duel.drawText("普通攻击\n (自动瞄准)",300,100);
    duel.drawText("致命大招\n (手动瞄准,\n  需要蓄力)",300,170);
    duel.drawText("移动\n (或使用大招时进行瞄准)",300,245);
    duel.drawText("- 按 Z 键开始游戏 -",192,330);
    duel.drawText("(轻触显示或隐藏此界面)",192,360);
    duel.setTextColor(80);
    duel.drawText("由FAL制作！( https://www.fal-works.com/ )",20,400);
    duel.drawText("由Pama1234移植到安卓版！( https://space.bilibili.com/646050693 )",20,420);
    duel.drawText("原型版本，视觉BUG很多，敬请关注此开源项目！会更新联机版！",20,440);
  }
}