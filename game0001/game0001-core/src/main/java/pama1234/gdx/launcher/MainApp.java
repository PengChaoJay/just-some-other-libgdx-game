package pama1234.gdx.launcher;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Game;

import pama1234.gdx.game.app.Screen0011;
import pama1234.gdx.util.app.UtilScreen;

public class MainApp extends Game{
  public static final int defaultType=0,taptap=1,pico=2;
  public static int type;
  public List<Class<? extends UtilScreen>> screenList;
  public int screenType=1;
  public MainApp() {
    screenList=Arrays.asList(null,
      Screen0011.class//游戏本体            <---------# √
    );
  }
  @Override
  public void create() {
    try {
      setScreen(screenList.get(screenType).getDeclaredConstructor().newInstance());
    }catch(InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException e) {
      e.printStackTrace();
    }
  }
  @Override
  public void dispose() {
    super.dispose();
    screen.dispose();
  }
}