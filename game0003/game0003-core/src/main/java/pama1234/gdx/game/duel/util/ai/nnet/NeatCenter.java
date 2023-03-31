package pama1234.gdx.game.duel.util.ai.nnet;

import java.util.ArrayList;

import pama1234.gdx.game.duel.Duel;
import pama1234.math.UtilMath;
import pama1234.util.neat.raimannma.architecture.EvolveOptions;
import pama1234.util.neat.raimannma.architecture.FloatBlock;
import pama1234.util.neat.raimannma.architecture.NEAT;
import pama1234.util.neat.raimannma.architecture.Network;
import pama1234.util.wrapper.Center;

public class NeatCenter extends Center<NetworkGroup>{
  public NetworkGroupParam param;
  public NeatModule vision,logic,behavior,world;
  //---
  public int index;
  public NeatCenter(NetworkGroupParam param) {
    this.param=param;
    // System.out.println(param.inputSize);
    // System.out.println(param.logicOptions.getTemplate().toString());
    vision=new NeatModule(param.inputSize,param.logicSize,param.visionOptions);
    logic=new NeatModule(param.logicSize,param.logicSize,param.logicOptions);
    behavior=new NeatModule(param.logicSize,param.outputSize,param.behaviorOptions);
    world=new NeatModule(param.outputSize,param.inputSize,param.worldbehavior);
  }
  public NetworkGroup getNext() {
    refresh();
    if(index==list.size()) {
      float[] data=new float[param.memorySize+param.logicSize*2];
      NetworkGroup out=createNetworkGroup(data);
      add.add(out);
      refresh();
      index++;
      return out;
    }
    return list.get(index++);
  }
  public NetworkGroup createNetworkGroup(float[] data) {
    NetworkGroup out=new NetworkGroup(
      new FloatBlock(new float[param.inputSize]),new FloatBlock(new float[param.outputSize]),
      new FloatBlock(data,param.memorySize,param.logicSize),new FloatBlock(data,param.logicSize,param.logicSize),
      new FloatBlock(data,0,param.memorySize),
      // vision.neat.evolve(),
      // logic.neat.evolve(),
      // behavior.neat.evolve(),
      // world.neat.evolve());
      vision.neat.getFittest(),
      logic.neat.getFittest(),
      behavior.neat.getFittest(),
      world.neat.getFittest());
    evolve();
    // System.out.println(out.logic.network.toString());
    return out;
  }
  public void evolve() {
    vision.neat.evolve();
    logic.neat.evolve();
    behavior.neat.evolve();
    world.neat.evolve();
  }
  public static class NeatModule{
    public NEAT neat;
    public NeatModule(int inputSize,int outputSize,EvolveOptions options) {
      neat=new NEAT(inputSize,outputSize,options);
    }
  }
  public static class Dataset{
    public ArrayList<float[]> inputs,outputs;
    public Dataset() {
      inputs=new ArrayList<>();
      outputs=new ArrayList<>();
    }
  }
  public static class NetworkGroupParam{
    public Duel duel;
    public int canvasSize=256;
    public int inputSize,logicSize,outputSize,memorySize;
    public EvolveOptions visionOptions,logicOptions,behaviorOptions,worldbehavior;
    //---
    public FloatBlock behaviorTestInput,behaviorTestOutput;
    public EvolveOptions newEvolveOptions(int tempInputSize,int tempOutputSize) {
      EvolveOptions out=new EvolveOptions();
      out.setError(0.05f);
      out.setPopulationSize(10);
      out.setTemplate(new Network(tempInputSize,tempOutputSize));
      out.setFitnessFunction(genome->genome.score);
      return out;
    }
    public NetworkGroupParam(int canvasSize) {
      this.canvasSize=canvasSize;
      inputSize=UtilMath.sq(canvasSize)*3;
      logicSize=64;
      outputSize=3;
      memorySize=1;
      //---
      behaviorTestInput=new FloatBlock(logicSize);
      behaviorTestOutput=new FloatBlock(outputSize);
      //---
      visionOptions=newEvolveOptions(inputSize,logicSize);
      logicOptions=newEvolveOptions(logicSize,logicSize);
      behaviorOptions=newEvolveOptions(logicSize,outputSize);
      behaviorOptions.setFitnessFunction(genome-> {
        float[] data=genome.activate(behaviorTestInput,behaviorTestOutput).data();
        return (data[ComputerLifeEngine.firePos]>1/3f?0.5f:0)+
          (UtilMath.abs(data[ComputerLifeEngine.magPos])>1/16f?0.5f:0);
      });
      worldbehavior=newEvolveOptions(outputSize,inputSize);
    }
  }
}