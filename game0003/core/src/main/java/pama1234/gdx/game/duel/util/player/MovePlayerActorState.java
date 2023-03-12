package pama1234.gdx.game.duel.util.player;

import pama1234.gdx.game.duel.util.actor.PlayerActor;
import pama1234.gdx.game.duel.util.input.AbstractInputDevice;

public final class MovePlayerActorState extends PlayerActorState{
  public PlayerActorState drawShortbowState,drawLongbowState;
  @Override
  public void act(PlayerActor parentActor) {
    final AbstractInputDevice input=parentActor.engine.controllingInputDevice;
    parentActor.addVelocity(1.0f*input.horizontalMove,1.0f*input.verticalMove);
    if(input.shotButtonPressed) {
      parentActor.state=drawShortbowState.entryState(parentActor);
      parentActor.aimAngle=getEnemyPlayerActorAngle(parentActor);
      return;
    }
    if(input.longShotButtonPressed) {
      parentActor.state=drawLongbowState.entryState(parentActor);
      parentActor.aimAngle=getEnemyPlayerActorAngle(parentActor);
      return;
    }
  }
  @Override
  public void displayEffect(PlayerActor parentActor) {}
  @Override
  public PlayerActorState entryState(PlayerActor parentActor) {
    return this;
  }
}