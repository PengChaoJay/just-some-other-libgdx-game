package pama1234.gdx.game.duel;

import com.badlogic.gdx.Input.Buttons;

import pama1234.gdx.game.duel.util.input.KeyInput;
import pama1234.gdx.util.app.UtilScreen2D;
import pama1234.gdx.util.info.MouseInfo;

/**
 * Title: Duel
 * </p>
 * Author: FAL ( https://www.fal-works.com/ )
 * </p>
 * Made with Processing 3.3.6
 * </p>
 * </p>
 * Change log: Ver. 0.1 (30. Sep. 2017) First version. Ver. 0.2 ( 1. Oct. 2017) Bug fix
 * (unintended change of strokeWeight), minor update (enabled to hide instruction window). Ver.
 * 0.3 (10. Feb. 2018) Minor fix (lack of semicolon). Ver. 0.4 (12. Feb. 2018) Enabled scaling.
 * </p>
 * </p>
 * Modified by: Pama1234 (https://space.bilibili.com/646050693)
 * </p>
 * Altered with self-made framework v0.0.1 Altered with Processing-Libgdx v0.0.1
 * </p>
 * </p>
 *
 * The font "Unifont" https://unifoundry.com/unifont/ is part of the GNU Project.
 */
public class Duel extends UtilScreen2D{
  public static final float IDEAL_FRAME_RATE=60;
  public static final int INTERNAL_CANVAS_SIDE_LENGTH=640;
  public KeyInput currentKeyInput;
  public GameSystem system;
  public int smallFontSize=16,largeFontSize=128;
  public boolean paused;
  public int canvasSideLength=INTERNAL_CANVAS_SIDE_LENGTH;
  @Override
  public void init() {}
  @Override
  public void setup() {
    currentKeyInput=new KeyInput();
    newGame(true,true); // demo play (computer vs computer), shows instruction window
    //---
    setTextColor(0);
    //---
    cam.point.des.set(canvasSideLength/2f,canvasSideLength/2f);
    cam.point.pos.set(cam.point.des);
    cam2d.activeDrag=false;
    cam2d.activeScrollZoom=cam2d.activeTouchZoom=false;
  }
  @Override
  public void display() {}
  @Override
  public void displayWithCam() {
    system.display();
    clearMatrix();
  }
  @Override
  public void update() {
    system.update();
  }
  public void newGame(boolean demo,boolean instruction) {
    system=new GameSystem(this,demo,instruction);
  }
  @Override
  public void mousePressed(MouseInfo info) {
    if(info.button==Buttons.LEFT) system.showsInstructionWindow=!system.showsInstructionWindow;
  }
  @Override
  public void keyPressed(char key,int keyCode) {
    currentKeyInput.keyPressedEvent(this,key,keyCode);
  }
  @Deprecated
  public void doPause() {
    // if(paused) loop();
    // else noLoop();
    paused=!paused;
  }
  @Override
  public void keyReleased(char key,int keyCode) {
    currentKeyInput.keyReleased(this,key,keyCode);
  }
  @Override
  public void frameResized() {}
}