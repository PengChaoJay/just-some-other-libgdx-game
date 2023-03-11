package pama1234.gdx.game.duel.util.state;

import pama1234.gdx.game.duel.Duel;
import pama1234.gdx.game.duel.GameSystem;
import pama1234.math.UtilMath;

public final class GameResultState extends GameSystemState{
  public final String resultMessage;
  public final int durationFrameCount=UtilMath.floor(Duel.IDEAL_FRAME_RATE);
  public GameResultState(Duel duel,String msg) {
    super(duel);
    resultMessage=msg;
  }
  @Override
  public void updateSystem(GameSystem system) {
    system.myGroup.update();
    system.otherGroup.update();
    system.commonParticleSet.update();
  }
  @Override
  public void displaySystem(GameSystem system) {
    system.myGroup.displayPlayer();
    system.otherGroup.displayPlayer();
    system.commonParticleSet.display();
  }
  @Override
  public void displayMessage(GameSystem system) {
    if(system.demoPlay) return;
    // duel.doFill();
    // duel.fill(0);
    duel.setTextColor(0);
    duel.drawText(resultMessage,0.0f,0.0f);
    if(properFrameCount>durationFrameCount) {
      duel.pushStyle();
      // duel.textFont(duel.smallFont,duel.smallFontSize);
      duel.textSize(duel.smallFontSize);
      duel.drawText("Press X key to reset.",0.0f,80.0f);
      duel.popStyle();
    }
  }
  @Override
  public void checkStateTransition(GameSystem system) {
    if(system.demoPlay) {
      if(properFrameCount>durationFrameCount*3) {
        duel.newGame(true,system.showsInstructionWindow);
      }
    }else {
      if(properFrameCount>durationFrameCount&&duel.currentKeyInput.isXPressed) {
        duel.newGame(true,true); // back to demoplay with instruction window
      }
    }
  }
}