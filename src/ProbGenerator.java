import java.util.Random;
import java.util.StringTokenizer;
import java.io.*;
import java.util.Hashtable;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ProbGenerator {
  double density_low = 0.4;  double density_up = 0.6;
  double cost_low = 0;  double cost_up = 4;
  double cost_fire_low = 50;  double cost_fire_up = 100;
  double cost_hire_low = 3;  double cost_hire_up = 5;
  double cost_hold_low = 2;  double cost_hold_up = 4;
  int MaxNumber = 1000;
  int cap_low = 5;  int cap_up = 10;
  int supply_low = 10;  int supply_up = 30;
  int demand_low = 20;  int demand_up = 30;
  double shortage_cost_low = 0;  double shortage_cost_up = 0;
  double overage_cost_low = 0;  double overage_cost_up = 0;
  long seed = 232891413;//946590215;//428294713;//
  public ProbGenerator() {
  }
  public static void main(String[] args) throws Exception {
    ProbGenerator g = new ProbGenerator();
    g.seed = System.currentTimeMillis();
    Random rv = new Random(g.seed);
    int n=1;
    for(int i=1;i<=11;i++){
      for(int t=1;t<=1;t++){
        String nodeFile = ".\\data\\nodeFile_"+n+".txt";
        String arcFile = ".\\data\\arcFile_"+n+".txt";
        System.out.println("Prob"+n);
        if(t == 1) g.creatNetwork(8+i*2,3,rv,nodeFile,arcFile);
        if(t == 2) g.creatNetwork(5*i,4,rv,nodeFile,arcFile);
        if(t == 3) g.creatNetwork(5*i,5,rv,nodeFile,arcFile);
        n++;
      }
    }
    String paraFile= ".\\data\\paraFile.txt";
    FileOutputStream outfile = new FileOutputStream(paraFile);
    PrintStream out = new PrintStream(outfile);
    out.println("seed:\t"+g.seed);
    out.println("density_low:\t"+g.density_low+"\tdensity_up:\t"+g.density_up);
    out.println("cost_low:\t"+g.cost_low+"\tcost_up:\t"+g.cost_up);
    out.println("cost_fire_low:\t"+g.cost_fire_low+"\t cost_fire_up:\t"+g.cost_fire_up);
    out.println("cost_hire_low:\t"+g.cost_hire_low+"\t cost_hire_up:\t"+g.cost_hire_up);
    out.println("cost_hold_low:\t"+g.cost_hold_low+"\t cost_hold_up:\t"+g.cost_hold_up);
    out.println("cap_low:\t"+g.cap_low+"\t cap_up:\t"+g.cap_up);
    out.println("supply_low:\t"+g.supply_low+"\t supply_up:\t"+g.supply_up);
    out.println("demand_low:\t"+g.demand_low+"\t demand_up:\t"+g.demand_up);
    out.println("shortage_cost_low:\t"+g.shortage_cost_low+"\t shortage_cost_up:\t"+g.shortage_cost_up);
    out.println("overage_cost_low:\t"+g.overage_cost_low+"\t overage_cost_up:\t"+g.overage_cost_up);
    out.close();
    outfile.close();

/*
    String nodeFile = ".\\data\\nodeFile_test.txt";
    String arcFile = ".\\data\\arcFile_test.txt";
    probGenerator1.creatNetwork(10,3,rv,nodeFile,arcFile);
        */
  }

  public void creatNetwork(int N, int T, Random rv,String nodeFile,String arcFile)throws FileNotFoundException{

    try{
      FileOutputStream outfile = new FileOutputStream(nodeFile);
      PrintStream out = new PrintStream(outfile);
      out.println((N+1)*(T+1)+"\t"+N+"\t"+T);
      out.println("NodeKey\tNetKey\tSupply\tDemand\tShortage\tOverage");
      for(int t=0;t<=T;t++){
        for(int i=0;i<=N;i++){
          int s = (int)(supply_low + (supply_up - supply_low)*rv.nextDouble());
          int d = (int)(demand_low + (demand_up - demand_low)*rv.nextDouble());
          double shortage_cost = precision( shortage_cost_low + (shortage_cost_up - shortage_cost_low)*rv.nextDouble());
          double overage_cost = precision( shortage_cost_low + (shortage_cost_up - shortage_cost_low)*rv.nextDouble());
          if(t==0) out.println("N"+(i+t*(N+1))+"\t"+"N001\t"+s+"\t"+d+"\t"+shortage_cost+"\t"+overage_cost);
          else out.println("N"+(i+t*(N+1))+"\t"+"N001\t"+0+"\t"+d+"\t"+shortage_cost+"\t"+overage_cost);
        }

      }
      out.close();
      outfile.close();
      outfile = new FileOutputStream(arcFile);
      out = new PrintStream(outfile);

      out.println("Arc_ID\tNet_ID\tfrom_N\tto_N\tcost\tlow\tup");
      int numberArc = -1;
      for(int t=0;t<T;t++){
        for(int i=1;i<=N;i++){
          numberArc ++;
          double cost = cost_hire_low + (cost_hire_up - cost_hire_low)*rv.nextDouble();
          cost = precision(cost);
          out.println("A"+numberArc+"\tN001\t"+"N"+(t*(N+1))+"\t"+"N"+(i+(t+1)*(N+1))+"\t"+cost+"\t0\t"+MaxNumber);
        }
        for(int i=1;i<=N;i++){
          String from = "N"+(i+t*(N+1));
          numberArc ++;
          String to = "N"+((t+1)*(N+1));
          double cost = cost_fire_low + (cost_fire_up - cost_fire_low)*rv.nextDouble();
          cost = precision(cost);
          out.println("A"+numberArc+"\tN001\t"+from+"\t"+to+"\t"+cost+"\t0\t"+MaxNumber);

          to = "N"+((t+1)*(N+1)+i);
          numberArc ++;
          cost = cost_hold_low + (cost_hold_up - cost_hold_low)*rv.nextDouble();
          cost = precision(cost);
          int cap = (int) (cap_low +(cap_up - cap_low)*rv.nextDouble());
          out.println("A"+numberArc+"\tN001\t"+from+"\t"+to+"\t"+cost+"\t0\t"+cap);

          double density = density_low +(density_up - density_low)*rv.nextDouble();
          int K = (int) (N*density);
          Hashtable arctable = new Hashtable();
          int k=0;
          while(k<K){
            int tn = (int)((N+1)*rv.nextDouble());
            if(tn == 0 || tn == i) continue;
            if(arctable.containsKey("N"+tn)) continue;
            k++;
            to = "N"+((t+1)*(N+1)+tn);
            numberArc ++;
            cost = cost_low + (cost_up - cost_low)*rv.nextDouble();
            cost = precision(cost);
            cap = (int) (cap_low +(cap_up - cap_low)*rv.nextDouble());
            out.println("A"+numberArc+"\tN001\t"+from+"\t"+to+"\t"+cost+"\t0\t"+cap);

          }

        }
      }

    }
    catch(Exception e){
      System.out.print(e);
    }

  }
  public double precision(double x){
    double p = 100;
    return ((int) (x * p +0.5))/p;
  }

}