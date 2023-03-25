package pama1234.gdx.game.state.state0001.game.entity;

import pama1234.gdx.game.app.Screen0011;
import pama1234.gdx.game.state.state0001.game.world.WorldBase2D;
import pama1234.gdx.game.state.state0001.game.world.world0001.WorldType0001Base;
import pama1234.gdx.util.entity.PointEntity;
import pama1234.math.physics.Point;

public class GamePointEntity<T extends Point>extends PointEntity<Screen0011,T>{
  public WorldBase2D<? extends WorldType0001Base<?>> pw;
  public GamePointEntity(Screen0011 p,WorldBase2D<? extends WorldType0001Base<?>> pw,T in) {
    super(p,in);
    this.pw=pw;
  }
}