package pama1234.util.localization;

import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;

public class LocalBundle extends LocalBundleCore{
  @Tag(1)
  public String[] name;
  public void loadFrom(Yaml yaml,String yamlData) {
    LinkedHashMap<String,String> cache=yaml.load(yamlData);//TODO map不具备稳定顺序
    name=new String[cache.size()];
    data=new String[cache.size()];
    int i=0;
    for(Map.Entry<String,String> entry:cache.entrySet()) {
      name[i]=entry.getKey();
      data[i]=entry.getValue();
      i++;
    }
  }
  public String getYaml(Yaml yaml) {
    return getYaml(yaml,name);
  }
  public String getYaml(Yaml yaml,Map<String,String> cache) {
    return getYaml(yaml,cache);
  }
  public void loadFrom(Kryo kryo,Input input) {
    name=kryo.readObject(input,String[].class);
    super.loadFrom(kryo,input);
  }
}
