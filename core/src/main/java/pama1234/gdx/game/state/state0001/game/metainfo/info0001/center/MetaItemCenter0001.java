package pama1234.gdx.game.state.state0001.game.metainfo.info0001.center;

import pama1234.gdx.game.state.state0001.game.metainfo.MetaInfoUtil.MetaItemCenter;
import pama1234.gdx.game.state.state0001.game.metainfo.MetaIntItem;
import pama1234.gdx.game.state.state0001.game.metainfo.MetaItem;
import pama1234.gdx.game.state.state0001.game.world.IDGenerator;
import pama1234.gdx.game.state.state0001.game.world.World0001;

public class MetaItemCenter0001 extends MetaItemCenter<MetaItem<?>>{
  public World0001 pw;
  public IDGenerator idg;
  public MetaIntItem empty;
  public MetaIntItem dirt;
  public MetaIntItem stone;
  public MetaItemCenter0001(World0001 pw) {
    this.pw=pw;
    idg=new IDGenerator();
  }
  public int id() {
    return idg.get();
  }
}