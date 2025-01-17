package pama1234.util.localization;

import java.util.LinkedHashMap;

public class LocalBundleCenter{
  public LinkedHashMap<String,Object> data;
  public LocalBundleCenter(LinkedHashMap<String,Object> data) {
    this.data=data;
  }
  public LocalBundle get(Localization p,String path) {
    String[] sa=path.split("/");
    LinkedHashMap<String,Object> cache=data;
    for(String i:sa) cache=(LinkedHashMap<String,Object>)cache.get(i);
    return p.readMap((LinkedHashMap)cache);
  }
}
