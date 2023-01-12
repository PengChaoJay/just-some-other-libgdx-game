package pama1234.gdx.game.state.state0001.game.region.block.block0001;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import pama1234.gdx.game.asset.ImageAsset;
import pama1234.gdx.game.state.state0001.game.metainfo.MetaBlock;
import pama1234.gdx.game.state.state0001.game.metainfo.info0001.center.MetaBlockCenter0001;
import pama1234.gdx.game.state.state0001.game.region.block.Block;
import pama1234.gdx.game.state.state0001.game.world.World0001;

public class TreeBranch extends MetaBlock{
  public TreeBranch(MetaBlockCenter0001 pc,int id) {
    super(pc,"tree-branch",id,6,1,(in,type)-> {//change to branch
      in.light.set(16);
    },(in,type)-> {//change from branch
    });
    destroyTime=10;
    buildTime=5;
    fullBlock=false;
    // initFullBlockLambda();
    initTreeLogLambda();
  }
  @Override
  public void initItemDrop() {
    itemDrop=new ItemDropAttr[] {new ItemDropAttr(pc.pw.metaItems.branch,1)};
  }
  @Override
  public void init() {
    TextureRegion[][] tsrc=ImageAsset.tiles;
    //-----------------------------------------------------
    tiles[3]=tsrc[18][4];
    tiles[1]=tsrc[19][4];
    tiles[2]=tsrc[18][5];
    tiles[0]=tsrc[19][5];
    tiles[4]=tsrc[19][6];
  }
  public void initTreeLogLambda() {
    // updater=lightUpdater;
    displayUpdater=(in,x,y)-> {
      World0001 world=in.type.pc.pw;
      int typeCache=0;
      if(TreeLog.isTreeLeaf(world.getBlock(x,y-1),pc.leaf)) typeCache+=1;// up
      // if(TreeLog.isTreeLog(world.getBlock(x,y+1),pc.log)) typeCache+=2;// down
      if(!isBranchOrLog(world.getBlock(x-1,y),this,pc.log)) typeCache+=2;// left
      if(!isBranchOrLog(world.getBlock(x+1,y),this,pc.log)) typeCache=4;// right
      in.displayType[0]=typeCache;
      // typeCache=0;
      // if(Block.isNotFullBlock(world.getBlock(x-1,y-1))) typeCache+=1;
      // if(Block.isNotFullBlock(world.getBlock(x-1,y+1))) typeCache+=2;
      // if(Block.isNotFullBlock(world.getBlock(x+1,y+1))) typeCache+=4;
      // if(Block.isNotFullBlock(world.getBlock(x+1,y-1))) typeCache+=8;
      // in.displayType[1]=typeCache;
      //---
      if(in.updateLighting) lightingUpdate(in,x,y,world);
      // in.light.update();
    };
    // displayer=fullBlockDisplayer;
  }
  public boolean isBranchOrLog(Block in,TreeBranch a,TreeLog b) {
    return in!=null&&(in.type==a||in.type==b);
  }
}
