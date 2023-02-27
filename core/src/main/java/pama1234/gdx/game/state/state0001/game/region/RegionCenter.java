package pama1234.gdx.game.state.state0001.game.region;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;

import pama1234.gdx.game.app.Screen0011;
import pama1234.gdx.game.state.state0001.game.metainfo.MetaBlock;
import pama1234.gdx.game.state.state0001.game.net.NetMode;
import pama1234.gdx.game.state.state0001.game.player.Player;
import pama1234.gdx.game.state.state0001.game.region.Chunk.BlockData;
import pama1234.gdx.game.state.state0001.game.region.block.Block;
import pama1234.gdx.game.state.state0001.game.world.World0001;
import pama1234.gdx.util.wrapper.EntityCenter;
import pama1234.math.Tools;
import pama1234.math.UtilMath;
import pama1234.util.function.GetBoolean;

public class RegionCenter extends EntityCenter<Screen0011,Region> implements LoadAndSave{
  public static class RegionData{
    @Tag(0)
    public String name;
    @Tag(1)
    public int version;
    // public IntMap<String> idToNameMap;
    public RegionData(String name,int version) {
      this.name=name;
      this.version=version;
    }
  }
  public World0001 pw;
  public RegionData data;
  public FileHandle dataLocation;
  public int regionWidth=4,regionHeight=4;
  public int chunkWidth=64,chunkHeight=64;
  public float regionLoadDist=360;
  public int regionLoadDistInt=1;
  public float chunkRemoveDist=360,regionRemoveDist=512;
  public float chunkUpdateDisplayDist=60;
  public RegionPool pool;
  public LoopThread[] loops;
  public LoopThread updateLoop,updateDisplayLoop;
  public LoopThread priorityUpdateDisplayLoop;
  public boolean stop;
  // public LoopThread fullMapUpdateDisplayLoop;
  public TilemapRenderer0001 tilemapRenderer;
  public Region cachedRegion;
  //---
  public Block nullBlock;
  public GetBoolean stopLoop=()->p.stop||stop;
  public RegionCenter(Screen0011 p,World0001 pw) {
    this(p,pw,Gdx.files.local(pw.dir()+"regions.bin"));
  }
  public RegionCenter(Screen0011 p,World0001 pw,FileHandle metadata) {
    super(p);
    this.pw=pw;
    data=new RegionData("firstRegion",0);
    this.dataLocation=metadata;
    pool=new RegionPool(p,this,0);//TODO
    loops=new LoopThread[3];
    updateLoop=loops[0]=createUpdateLoop();
    // fullMapUpdateDisplayLoop=loops[1]=createFullMapUpdateDisplayLoop();
    updateDisplayLoop=loops[1]=createUpdateDisplayLoop();
    priorityUpdateDisplayLoop=loops[2]=createPriorityUpdateDisplayLoop();
    for(LoopThread e:loops) e.stop=stopLoop;
    // startAllLoop();
    tilemapRenderer=new TilemapRenderer0001(pw,new SpriteBatch(1000,new ShaderProgram(
      Gdx.files.internal("shader/main0002/tilemap.vert").readString(),
      Gdx.files.internal("shader/main0002/tilemap.frag").readString())));
    //---
    nullBlock=new Block(pw.metaBlocks.nullBlock);
  }
  @Override
  public void resume() {
    unlockAllLoop();
  }
  @Override
  public void pause() {
    // if(!p.stop) 
    lockAllLoop();
  }
  @Override
  public void load() {
    testAddChunk();
    super.refresh();
  }
  @Override
  public void save() {
    refresh();
    shutdownAllLoop();
    innerSave();
  }
  public void innerSave() {
    pool.saveAndClear();
    synchronized(list) {//TODO
      for(Region e:list) e.save();
      list.clear();
    }
  }
  public void dispose() {
    stop=true;
    shutdownAllLoop();
    pool.saveAndClear();
  }
  public void shutdownAllLoop() {
    unlockAllLoop();
    for(LoopThread e:loops) {
      e.sleepSize=0;
      e.interrupt();
    }
  }
  @Override
  public void refresh() {
    if(pw.netMode()!=NetMode.client) {
      removeRegionAndTestChunkUpdate();
      testAddChunk();
    }
    // synchronized(list) {
    super.refresh();
    // }
  }
  public void removeRegionAndTestChunkUpdate() {
    for(Region e:list) {
      e.keep=false;
      for(int i=0;i<e.data.length;i++) for(int j=0;j<e.data[i].length;j++) {
        e.data[i][j].priority=0;
        e.data[i][j].update=false;
      }
    }
    for(Player player:pw.entities.players.list) testChunkUpdateWithPlayer(player);
    testChunkUpdateWithPlayer(pw.yourself);//TODO
    for(Region e:list) if(!e.keep) {
      remove.add(e);
      pool.put(e);
    }
    if(cachedRegion!=null) for(Region e:remove) if(cachedRegion==e) synchronized(cachedRegion) {
      cachedRegion=null;
    }
  }
  public void testChunkUpdateWithPlayer(Player player) {
    float tcx=player.cx()/pw.settings.blockWidth,
      tcy=player.cy()/pw.settings.blockHeight;
    for(Region e:list) {
      float tx_1=(((e.x+0.5f)*regionWidth)*chunkWidth),
        ty_1=(((e.y+0.5f)*regionHeight)*chunkHeight);
      boolean tb_1=UtilMath.dist(tx_1,ty_1,tcx,tcy)<regionRemoveDist;
      if(tb_1) e.keep=true;
      for(int i=0;i<e.data.length;i++) {
        for(int j=0;j<e.data[i].length;j++) {
          Chunk chunk=e.data[i][j];
          float tx_2=((e.x*regionWidth+(i+0.5f))*chunkWidth),
            ty_2=((e.y*regionHeight+(j+0.5f))*chunkHeight);
          float dist=UtilMath.dist(tx_2,ty_2,tcx,tcy);
          if(chunk.priority<dist) chunk.priority=dist;
          boolean tb_2=dist<chunkRemoveDist;
          if(tb_2) chunk.update=true;
        }
      }
    }
  }
  public void testAddChunk() {
    for(Player player:pw.entities.players.list) testAddChunkWithPlayer(player);
    testAddChunkWithPlayer(pw.yourself);//TODO
  }
  public void testAddChunkWithPlayer(Player player) {
    int tx_1=UtilMath.round(player.cx()/(regionWidth*chunkWidth*pw.settings.blockWidth)),
      ty_1=UtilMath.round(player.cy()/(regionHeight*chunkHeight*pw.settings.blockHeight));
    float tx_2=player.cx()/pw.settings.blockWidth,
      ty_2=player.cy()/pw.settings.blockHeight;
    for(int i=-regionLoadDistInt;i<=regionLoadDistInt;i++) {
      for(int j=-regionLoadDistInt;j<=regionLoadDistInt;j++) {
        int tx_3=tx_1+i,
          ty_3=ty_1+j;
        float tx_4=(((tx_3+0.5f)*regionWidth)*chunkWidth),
          ty_4=(((ty_3+0.5f)*regionHeight)*chunkHeight);
        if(UtilMath.dist(tx_2,ty_2,tx_4,ty_4)<regionLoadDist) {
          boolean flag=testRegionPosInList(tx_3,ty_3,list)||testRegionPosInList(tx_3,ty_3,add);
          if(!flag) add.add(pool.get(tx_3,ty_3));
        }
      }
    }
  }
  public boolean testRegionPosInList(int xIn,int yIn,LinkedList<Region> list) {
    for(Region e:list) if(e.posIs(xIn,yIn)) return true;
    return false;
  }
  @Override
  public void update() {
    // super.update();
    tilemapRenderer.updateInfo();
  }
  @Override
  public void display() {
    if(stop) return;
    // super.display();
    fourPointDisplay();
  }
  public void fourPointDisplay() {
    // p.imageBatch.begin();
    tilemapRenderer.batch.begin();
    int x1=pw.xToBlockCordInt(p.cam2d.x1()),
      y1=pw.xToBlockCordInt(p.cam2d.y1()),
      x2=pw.xToBlockCordInt(p.cam2d.x2()),
      y2=pw.xToBlockCordInt(p.cam2d.y2());
    for(int i=x1;i<=x2;i++) {
      for(int j=y1;j<=y2;j++) {
        int tx=i*pw.settings.blockWidth,
          ty=j*pw.settings.blockHeight;
        Block block=getBlock(i,j);
        // if(block==null) continue;
        MetaBlock blockType=block.type;
        if(blockType==null) continue;
        // blockType.updateDisplay(block,i,j);
        if(!blockType.display) continue;
        blockType.display(tilemapRenderer,p,pw,block,tx,ty);
      }
    }
    tilemapRenderer.batch.end();
    tilemapRenderer.batch.setColor(1,1,1,1);
    // p.imageBatch.end();
    // p.noTint();
  }
  public void fourPointUpdateDisplay() {
    int x1=pw.xToBlockCordInt(p.cam2d.x1()),
      y1=pw.xToBlockCordInt(p.cam2d.y1()),
      x2=pw.xToBlockCordInt(p.cam2d.x2()),
      y2=pw.xToBlockCordInt(p.cam2d.y2());
    for(int i=x1;i<=x2;i++) {
      for(int j=y1;j<=y2;j++) {
        Block block=getBlock(i,j);
        // if(block==null) continue;
        MetaBlock blockType=block.type;
        if(blockType==null) continue;
        blockType.updateDisplay(pw,block,i,j);
      }
    }
  }
  public void startAllLoop() {
    for(LoopThread e:loops) e.start();
  }
  public void interruptAllLoop() {
    for(LoopThread e:loops) e.interrupt();
  }
  public void unlockAllLoop() {
    for(LoopThread e:loops) e.lock.unlock();
  }
  public void lockAllLoop() {
    for(LoopThread e:loops) e.lock.lock();
  }
  public LoopThread createUpdateLoop() {//刷新全世界的方块数据
    return new LoopThread("RegionsUpdateLoop",(self)-> {
      // refresh();
      // Stream<Region> stream=list.stream().parallel();
      // stream.forEach(r->r.update());
      // Iterator<Region> it=list.iterator();
      // while(it.hasNext()) it.next().update();
      RegionCenter.super.update();
      pw.data.tick+=1;
      // refresh();
      // for(Region e:list) e.update();
    });
  }
  public LoopThread createPriorityUpdateDisplayLoop() {//根据优先级和阈值刷新全世界的方块显示
    return new LoopThread("RegionsFullMapUpdateDisplayLoop",10000,(self)-> {
      // for(Region e:list) e.updateDisplay();
      // refresh();
      Stream<Region> stream=list.stream().parallel();
      stream.forEach(r-> {
        // if(p.stop) return;//TODO
        r.updateDisplay();
      });
    });
  }
  public LoopThread createUpdateDisplayLoop() {//刷新视角内的方块显示
    return new LoopThread("RegionsUpdateDisplayLoop",50,(self)-> {
      if(p.cam2d.scale.pos<1.0f) self.sleepSize=300;
      else self.sleepSize=50;
      // for(Region e:list) e.updateDisplay();
      // refresh();
      // Stream<Region> stream=list.stream().parallel();
      // stream.forEach(r->r.updateDisplay());
      // fourPointDisplay();
      fourPointUpdateDisplay();
    });
  }
  public Block getBlock(int x,int y) {
    int cx=UtilMath.floor((float)x/chunkWidth),cy=UtilMath.floor((float)y/chunkHeight);
    int tx=UtilMath.floor((float)cx/regionWidth),ty=UtilMath.floor((float)cy/regionHeight);
    int prx=Tools.moveInRange(cx,0,regionWidth),pry=Tools.moveInRange(cy,0,regionHeight);
    int px=Tools.moveInRange(x,0,chunkWidth),py=Tools.moveInRange(y,0,chunkHeight);
    Region tr=getRegions(tx,ty);
    if(tr==null) return nullBlock;
    Chunk chunk=tr.data[prx][pry];
    BlockData blockData=chunk.data[px][py];
    return blockData.block;
  }
  public void addChunk(int cx,int cy,Chunk chunk) {
    int tx=UtilMath.floor((float)cx/regionWidth),ty=UtilMath.floor((float)cy/regionHeight);
    int prx=Tools.moveInRange(cx,0,regionWidth),pry=Tools.moveInRange(cy,0,regionHeight);
    Region tr=getRegions(tx,ty);
    if(tr==null) synchronized(add) {//TODO
      for(Region r:add) if(r.x==tx&&r.y==ty) tr=r;
    }
    if(tr==null) add.add(tr=new Region(p,this,tx,ty,null));
    if(tr.data[prx][pry]==null) tr.data[prx][pry]=chunk;
  }
  public Region getRegions(int tx,int ty) {
    if(cachedRegion!=null) synchronized(cachedRegion) {
      if(cachedRegion.x==tx&&cachedRegion.y==ty) return cachedRegion;
    }
    synchronized(list) {//TODO
      for(Region r:list) if(r.x==tx&&r.y==ty) return cachedRegion=r;
    }
    return null;
  }
}