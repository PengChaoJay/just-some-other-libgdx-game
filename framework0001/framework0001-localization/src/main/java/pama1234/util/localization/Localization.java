package pama1234.util.localization;

import java.util.LinkedHashMap;

import org.yaml.snakeyaml.Yaml;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;

public class Localization{
  public Yaml yaml;
  public Kryo kryo;
  public Localization() {
    yaml=new Yaml();
    kryo=new Kryo();
    kryo.setDefaultSerializer(TaggedFieldSerializer.class);
    kryo.register(String[].class);
    kryo.register(LocalBundleCore.class);
    kryo.register(LocalBundle.class);
  }
  public Localization(Yaml yaml,Kryo kryo) {
    this.yaml=yaml;
    this.kryo=kryo;
  }
  public LocalBundle readMap(LinkedHashMap<String,String> map) {
    return readMap(new LocalBundle(),map);
  }
  public LocalBundle readMap(LocalBundle data,LinkedHashMap<String,String> map) {
    data.loadFrom(map);
    return data;
  }
  public LocalBundle readYaml(String yamlString) {
    return readYaml(new LocalBundle(),yamlString);
  }
  public LocalBundle readBytes(Input input) {
    return readBytes(new LocalBundle(),input);
  }
  public LocalBundle readYaml(LocalBundle data,String yamlString) {
    data.loadFrom(yaml,yamlString);
    return data;
  }
  public LocalBundle readBytes(LocalBundle data,Input input) {
    data.loadFrom(kryo,input);
    return data;
  }
  public <T> T load(LocalBundle localBundle,Class<T> class1) {//TODO 效率较低
    return yaml.loadAs(localBundle.getYaml(yaml),class1);
  }
  public <T> T load(LocalBundle localBundle,String[] name,Class<T> class1) {
    return yaml.loadAs(localBundle.getYaml(yaml,name),class1);
  }
}