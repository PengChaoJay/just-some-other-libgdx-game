package pama1234.gdx.game.state.state0001.game.region.block.workstation;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import pama1234.gdx.game.asset.ImageAsset;
import pama1234.gdx.game.state.state0001.game.metainfo.MetaBlock;
import pama1234.gdx.game.state.state0001.game.metainfo.info0001.center.MetaBlockCenter0001;

public class Furnace extends MetaBlock{
  public Furnace(MetaBlockCenter0001 pc,int id) {
    super(pc,"furnace",id,4,1,(in,type)-> {//change to log
      in.light.set(16);
    },(in,type)-> {//change from log
    });
    blockType=stoneType;
    workStation=true;
    fullBlock=false;
    destroyTime=120;
    buildTime=10;
    width=2;
    height=2;
    initLambda();
  }
  @Override
  public void init() {
    TextureRegion[][] tsrc=ImageAsset.tiles;
    //-----------------------------------------------------
    tiles[0]=tsrc[9][8];
    tiles[1]=tsrc[9][9];
    tiles[2]=tsrc[10][8];
    tiles[3]=tsrc[10][9];
  }
  @Override
  public void initItemDrop() {
    itemDrop=new ItemDropAttr[] {new ItemDropAttr(pc.pw.metaItems.furnace,1)};
  }
  public void initLambda() {
    displayer=(r,p,in,x,y)-> {
      in.displayType[0]=in.xOff*in.type.width+in.yOff;
      defaultBlockDisplayer.display(r,p,in,x,y);
    };
  }
}