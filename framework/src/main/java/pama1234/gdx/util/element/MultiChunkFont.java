package pama1234.gdx.util.element;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class MultiChunkFont extends BitmapFont{
  public FileHandle[] fontFile;
  public int length;
  public boolean loadOnDemand;
  public int digitShift;
  public BitmapFont[] data;
  public MultiChunkFontData mfontData;
  public Array<TextureRegion> multiRegions;
  //---
  public SpriteBatch fontBatch;
  //---
  public float defaultSize=16,size=defaultSize;
  public Color foreground=Color.WHITE,background=Color.BLACK;
  public float scale=1;
  public MultiChunkFont(FileHandle[] fontFile,boolean loadOnDemand) {
    super(new MultiChunkFontData(fontFile[0],true),(TextureRegion)null,true);
    // usesIntegerPositions();
    mfontData=(MultiChunkFontData)getData();//TODO
    mfontData.mfont=this;
    this.fontFile=fontFile;
    length=fontFile.length;
    multiRegions=super.getRegions();
    multiRegions.setSize(length);
    this.loadOnDemand=loadOnDemand;
    digitShift=16-MathUtils.ceil(MathUtils.log2(length));
    if(digitShift>32) throw new RuntimeException("digitShift>32");
    data=new BitmapFont[length];
    data[0]=this;
    if(!loadOnDemand) for(int i=0;i<fontFile.length;i++) loadFont(i);
  }
  public void load(int in) {
    loadFont(in);
    loadOnDemand=isAllLoaded();
  }
  public boolean isAllLoaded() {
    for(int i=0;i<length;i++) if(data[i]==null) return true;
    return false;
  }
  public void loadFont(int in) {
    BitmapFont tf=createBitmapFont(fontFile[in]);
    data[in]=tf;
    for(int i=0;i<tf.getData().glyphs.length;i++) {//TODO
      Glyph[] tgs=tf.getData().glyphs[i];
      if(tgs==null) continue;
      for(int j=0;j<tgs.length;j++) {
        Glyph tg=tgs[j];
        if(tg!=null) tg.page=in;
      }
    }
    multiRegions.set(in,data[in].getRegion());
  }
  public BitmapFont createBitmapFont(FileHandle fontFile) {
    BitmapFont out=fontFile==null?new BitmapFont(true):new BitmapFont(fontFile,true);
    // BitmapFont out=fontFile==null?new BitmapFont():new BitmapFont(fontFile);
    // out.getRegion().getTexture().setFilter(TextureFilter.Nearest,TextureFilter.Nearest);
    out.getRegion().getTexture().setFilter(TextureFilter.Linear,TextureFilter.Nearest);
    out.getData().setScale(size/defaultSize);
    // Glyph glyph=data[0].getData().getGlyph(' ');
    // out.setFixedWidthGlyphs(null);
    BitmapFontData data=out.getData();
    int unit=(int)(size/2);
    for(int i=0,end=out.getData().glyphs[0].length;i<end;i++) {
      Glyph g=data.glyphs[0][i];
      if(g==null) continue;
      int tl=g.xadvance/unit;
      g.xoffset+=(unit*tl-g.xadvance)/2;
      g.xadvance=unit*tl;
      g.kerning=null;
      g.fixedWidth=true;
    }
    return out;
  }
  public float size() {
    return size;
  }
  public void size(int in) {
    if(size==in) return;
    size=in;
    for(int i=0;i<fontFile.length;i++) {
      if(data[i]!=null) data[i].getData().setScale(size/defaultSize);
    }
  }
  public void size(float in) {
    if(size==in) return;
    size=in;
    for(int i=0;i<fontFile.length;i++) if(data[i]!=null) data[i].getData().setScale(size/defaultSize);
  }
  public void text(char in,float x,float y) {
    int pos=in>>>digitShift;
    if(loadOnDemand&&data[pos]==null) load(pos);
    BitmapFont font=data[pos];
    Array<TextureRegion> regions=font.getRegions();
    Glyph glyph=font.getData().getGlyph(in);
    Texture texture=regions.get(glyph.page).getTexture();
    final float scaleX=font.getScaleX(),
      scaleY=font.getScaleY();
    fontBatch.draw(
      texture,x+glyph.xoffset*scaleX,y+glyph.yoffset*scaleY,
      glyph.width*scaleX,glyph.height*scaleY,
      glyph.u,glyph.v,
      glyph.u2,glyph.v2);
    // b.draw(texture, x, y, srcX, srcY, srcWidth, srcHeight);
  }
  // public void text(CharSequence in,float x,float y,TextStyleSupplier style) {
  public void fastText(CharSequence in,float x,float y) {
    // float ix=x;
    if(!loadOnDemand) {
      for(int i=0;i<in.length();i++) {
        char tc=in.charAt(i);
        int pos=tc>>>digitShift;
        x=drawChar(x,y,tc,pos);
      }
    }else {
      for(int i=0;i<in.length();i++) {
        char tc=in.charAt(i);
        int pos=tc>>>digitShift;
        if(loadOnDemand&&data[pos]==null) load(pos);
        x=drawChar(x,y,tc,pos);
      }
    }
    // Tools.println(in,x-ix);
  }
  public Glyph getGlyph(char ch) {
    int pos=ch>>>digitShift;
    // BitmapFont font=data[pos];
    // Glyph glyph=font.getData().getGlyph(ch);
    // return glyph;
    if(loadOnDemand&&data[pos]==null) load(pos);
    if(pos==0) {
      Glyph glyph=mfontData.getGlyphSuper(ch);
      return glyph;
    }
    Glyph glyph=data[pos].getData().getGlyph(ch);
    // System.out.println(glyph.page+" "+pos);
    // glyph.page=pos;//TODO
    return glyph;
  }
  public float drawChar(float x,float y,char tc,int pos) {
    Array<TextureRegion> regions=getRegions();//TODO
    Glyph glyph=getData().getGlyph(tc);
    if(glyph==null) {
      System.out.println(tc+" "+(int)tc+" "+pos+" "+digitShift);
      return 0;
    }
    // System.out.println(tc+" "+glyph.page);//all output 0
    Texture texture=regions.get(glyph.page).getTexture();
    fontBatch.draw(texture,
      x+glyph.xoffset*scale,
      y+glyph.yoffset*scale,
      glyph.width*scale,
      glyph.height*scale,
      glyph.u,glyph.v,
      glyph.u2,glyph.v2);
    x+=glyph.xadvance*scale;
    return x;
  }
  public void color(Color in) {
    foreground=in;
    fontBatch.setColor(foreground);
    //---
  }
  public void setColor(Color in) {
    super.setColor(in);
  }
  // @Override
  public void textScale(float in) {
    scale=in;
  }
  @Override
  public void dispose() {
    for(int i=1;i<data.length;i++) {
      final BitmapFont td=data[i];
      if(td!=null) td.dispose();
    }
    // super.dispose();
    // System.out.println(ownsTexture());
    if(ownsTexture()) getRegion().getTexture().dispose();
    for(int i=0;i<data.length;i++) {
      // System.out.println(i+" "+data[i].ownsTexture());
      if(data[i]==null||!data[i].ownsTexture()) continue;
      TextureRegion tr=data[i].getRegion();
      if(tr!=null) tr.getTexture().dispose();
    }
  }
  @Override
  public Array<TextureRegion> getRegions() {//TODO
    // return super.getRegions();
    return multiRegions;
  }
  public float textWidthNoScale(CharSequence in) {
    float out=0;
    for(int i=0;i<in.length();i++) {
      char tc=in.charAt(i);
      int pos=tc>>>digitShift;
      if(loadOnDemand&&data[pos]==null) load(pos);
      out=getAndAddCharWidth(out,tc,pos);
    }
    return out;
  }
  public float textWidth(CharSequence in) {
    // System.out.println(scale);
    return textWidthNoScale(in)*scale;
  }
  public float getAndAddCharWidth(float x,char tc,int pos) {
    BitmapFont font=data[pos];
    Glyph glyph=font.getData().getGlyph(tc);
    if(glyph==null) {
      System.err.println("MultiChunkFont.getAndAddCharWidth char=<"+tc+"> char="+(int)tc+" pos="+pos+" digitShift="+digitShift);
      return 0;
    }
    x+=glyph.xadvance;
    return x;
  }
}