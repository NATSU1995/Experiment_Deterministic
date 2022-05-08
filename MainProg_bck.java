import MyNetwork.*;
import ilog.concert.*;
import ilog.cplex.*;
import DataStructures.BinaryHeap;
import java.util.Hashtable;
import java.io.FileOutputStream;
import java.io.PrintStream;
import SnNetwork.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class MainProg {
  double MaxNum = 1000;
  boolean debug = true;
  PrintStream debugOut;
  public MainProg() {
  }
  public static void main(String[] args) throws Exception{
    MainProg mainProg1 = new MainProg();
    String solFile = ".\\data\\sol_whole.txt";
    String debugOutFile = ".\\data\\debugLog.txt";
    FileOutputStream outfile = new FileOutputStream(solFile,true);
    PrintStream out = new PrintStream(outfile);
    mainProg1.debugOut = new PrintStream( new FileOutputStream(debugOutFile));
    double obj=0;
    out.println();
    int ITE = 3;
 for(int n=1;n<=1;n++){
   Network myNetwork = new Network();
   String netFile = ".\\netInfo.txt";
   //String nodeFile = ".\\data\\nodeFile_"+n+".txt";
   String nodeFile = ".\\data\\nodeFile_exp.txt";
   //String arcFile = ".\\data\\arcFile_"+n+".txt";
   String arcFile = ".\\data\\arcFile_exp.txt";
   String outFile = ".\\data\\sol_"+n+".txt";
   System.out.println("Prob"+n);
   myNetwork.buildNetworkFrom(netFile,nodeFile,arcFile);
   out.print(n);
   long st = System.currentTimeMillis();
   //obj = mainProg1.meanMethod(myNetwork,outFile);
   long et = System.currentTimeMillis();
   double time1 = (et-st)/1000.0;
   out.print("\t"+precision(obj));
   myNetwork = null;
   System.gc();
   Thread.sleep(1000);
   myNetwork = new Network();
   myNetwork.buildNetworkFrom(netFile,nodeFile,arcFile);
   st = System.currentTimeMillis();
   //obj = mainProg1.scapMethod(myNetwork,ITE,outFile);
   et = System.currentTimeMillis();
   double time2 = (et-st)/1000.0;
   out.print("\t"+precision(obj));
   myNetwork = null;
   System.gc();
   Thread.sleep(1000);
   myNetwork = new Network();
   myNetwork.buildNetworkFrom(netFile,nodeFile,arcFile);
   st = System.currentTimeMillis();
   //obj = mainProg1.gradientMethod(myNetwork,ITE,outFile);
   et = System.currentTimeMillis();
   double time3 = (et-st)/1000.0;
   out.print("\t"+precision(obj)+"\t");
   System.gc();
   Thread.sleep(1000);
   myNetwork = new Network();
   myNetwork.buildNetworkFrom(netFile,nodeFile,arcFile);
   st = System.currentTimeMillis();
   obj = mainProg1.newMethod(myNetwork,ITE,outFile);
   et = System.currentTimeMillis();
   double time4 = (et-st)/1000.0;
   out.print("\t"+precision(obj)+"\t");
   out.println(precision(time1)+"\t"+precision(time2)+"\t"+precision(time3)+"\t"+precision(time4));
   System.gc();
   Thread.sleep(1000);

 }
 out.close();
 outfile.close();
}

 public double scapMethod(Network myNetwork, int ITE, String outFile){
   double obj =Double.NEGATIVE_INFINITY;
   double max_obj = Double.NEGATIVE_INFINITY;
   long start_time = System.currentTimeMillis();
   int N = myNetwork.N;
   int T = myNetwork.T;
   double[][][] q = new double[T][N][]; // slop
   double[][][] u = new double[T][N][]; // upper bound
   double[][] supply = new double[T][N]; // upper bound

   for(int t=0;t<T;t++){
     for(int i=1;i<=N;i++){
       supply[t][i-1] = ((NodeInfo) myNetwork.getNode("N"+i)).supply;
     }
   }

   try {
     IloCplex cplex = new IloCplex();
     cplex.setParam(IloCplex.IntParam.SimDisplay, 0);//0 No iteration messages until solution
           //1 Iteration info after each refactoring
           //2 Iteration info for each iteration

     FileOutputStream outfile = new FileOutputStream(outFile,true);
    PrintStream out = new PrintStream(outfile);
    out.println("scap");
     //obtain q and u at the last stage
     for(int i=1;i<=N;i++){
       String nodeKey = "N"+((T-1)*(N+1)+i);
       ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf(nodeKey);
       //BinaryHeap heap = new BinaryHeap(myNetwork.getNumOutArcOf(nodeKey));
       BinaryHeap heap = new BinaryHeap();
       while(arc != null){
         HeapArc heaparc = new HeapArc(arc.getKey(),arc.cost);
         heaparc.cap = arc.cap_up;
         heap.insert(heaparc);
         arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
       }
       double[] temp_q = new double[myNetwork.getNumOutArcOf(nodeKey)];
       double[] temp_u = new double[myNetwork.getNumOutArcOf(nodeKey)];
       int k=0;
       for(int j=0;j<temp_q.length;j++){
         if(heap.isEmpty()){
           break;
         }else {
           HeapArc heaparc = (HeapArc) heap.deleteMin();
           if(k==0){
             temp_q[k] = heaparc.value;
             temp_u[k] = heaparc.cap;
             k++;
           }else{
             if(temp_u[k-1] >= MaxNum) break;
             if(heaparc.value <= temp_q[k-1]){
               temp_u[k-1] += heaparc.cap;
             }else{
               temp_q[k] = heaparc.value;
               temp_u[k] = heaparc.cap;
               k++;
             }
           }
         }
       }
       q[T-1][i-1] = new double[k];
       u[T-1][i-1] = new double[k];
       for(int j=0;j<k;j++){
         q[T-1][i-1][j] = temp_q[j];
         u[T-1][i-1][j] = temp_u[j];
       }

     }
     // end of obtaining q and u at the stage T-1
     double[][] qq = new double[N][];
     double[][] uu = new double[N][]; // store the q and u value at the last stage
     for(int i=0;i<N;i++){
       qq[i] = new double[q[T-1][i].length];
       uu[i] = new double[q[T-1][i].length];
       for(int j=0;j<q[T-1][i].length;j++){
         qq[i][j] = q[T-1][i][j];
         uu[i][j] = u[T-1][i][j];
       }
     }

for(int ite =1;ite <=ITE;ite++){
       double KStar =0;
       //restore the q and u value at the last stage
       for(int i=0;i<N;i++){
         for(int j=0;j<q[T-1][i].length;j++){
           q[T-1][i][j] = qq[i][j];
           u[T-1][i][j] = uu[i][j];
         }
       }

       for (int t = T - 2; t >= 0; t--) {
         int numArc = 0;
         int numYarc = 0; // number of y
         for (int i = 0; i <= N; i++) {
           numArc += myNetwork.getNumOutArcOf("N" + (t * (N + 1) + i));
           if (i > 0)
             numYarc += q[t + 1][i - 1].length;
         }
         IloNumVar[] x = new IloNumVar[numArc];
         IloNumVar[] y = new IloNumVar[numYarc];
         IloLinearNumExpr obj_exp = cplex.linearNumExpr();
         int k = 0;
         Hashtable indexTable = new Hashtable();
         for (int i = 0; i <= N; i++) {
           ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
               (t * (N + 1) + i));
           while (arc != null) {
             x[k] = cplex.numVar(arc.cap_low, arc.cap_up, "x" + k);
             obj_exp.addTerm(arc.cost, x[k]);
             indexTable.put(arc.getKey(), new Integer(k));
             k++;
             arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
           }
         }
         k = 0;
         for (int i = 1; i <= N; i++) {
           for (int j = 0; j < q[t + 1][i - 1].length; j++) {
             y[k] = cplex.numVar(0, MaxNum + 1, "y" + k);
             obj_exp.addTerm(q[t + 1][i - 1][j], y[k]);
             k++;
           }
         }
         cplex.addMinimize(obj_exp);
         IloRange rng[] = new IloRange[2 * N];
         IloRange stng[] = new IloRange[numYarc];
         k = 0;
         for (int i = 1; i <= N; i++) {
           IloLinearNumExpr exp = cplex.linearNumExpr();
           ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
               (t * (N + 1) + i));
           while (arc != null) {
             int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
             exp.addTerm(1, x[j]);
             arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
           }
           rng[i -1] = cplex.addEq(exp, supply[t][i - 1], "N" + (t * (N + 1) + i));
         }

         k = 0;
         for (int i = 1; i <= N; i++) {
           IloLinearNumExpr exp = cplex.linearNumExpr();
           ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
               ( (t + 1) * (N + 1) + i));
           while (arc != null) {
             int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
             exp.addTerm(1, x[j]);
             arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
           }
           for (int j = 0; j < q[t + 1][i - 1].length; j++) {
             exp.addTerm( -1, y[k]);
             k++;
           }
           rng[N + i - 1] = cplex.addEq(exp, 0, "N" + ( (t + 1) * (N + 1) + i));
         }

         //add y constraint
         k = 0;
         for (int i = 1; i <= N; i++) {
           for (int j = 0; j < q[t + 1][i - 1].length; j++) {
             IloLinearNumExpr exp = cplex.linearNumExpr();
             exp.addTerm(1, y[k]);
             stng[k] = cplex.addLe(exp, u[t + 1][i - 1][j],
                                   "Y" + t + "_" + i + "_" + j);
             k++;
           }
         }

         //cplex.exportModel(".\\data\\scapMethod_" + t + ".lp");
         if (cplex.solve()) {

           obj = cplex.getObjValue() - KStar;

           double[] pi = cplex.getDuals(stng);
           for (int i = 0; i < stng.length; i++)
             pi[i] = -pi[i];

             //obtain q and u
           k = 0;
           for (int i = 1; i <= N; i++) {
             for (int j = 0; j < q[t + 1][i - 1].length; j++) {
               q[t + 1][i - 1][j] += pi[k];
               if (pi[k] < -0.001) out.println("pi[" + k + "] is less than 0:\t" + pi[k]);
               KStar += pi[k] * u[t + 1][i - 1][j];
               k++;
             }
           }

           for (int i = 1; i <= N; i++) {
             String nodeKey = "N" + (t * (N + 1) + i);
             ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf(nodeKey);
             BinaryHeap heap = new BinaryHeap();
             int kk = 0;
             while (arc != null) {
               arc.cap_rest = arc.cap_up;
               int ii = Integer.parseInt( (arc.getToNodeKey()).substring(1)); // index of toNodeKey
               ii = ii - (t + 1) * (N + 1);
               if (ii > 0) {
                 for (int j = 0; j < q[t + 1][ii - 1].length; j++) {
                   HeapArc heaparc = new HeapArc(arc.getKey() + "_" + j,
                                                 arc.cost + q[t + 1][ii - 1][j]);
                   heaparc.cap = Math.min(arc.cap_rest, u[t + 1][ii - 1][j]);
                   arc.cap_rest -= heaparc.cap;
                   heap.insert(heaparc);
                   kk++;
                   if (arc.cap_rest <= 0)
                     break;
                 }
               }
               arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
             }
             double[] temp_q = new double[kk];
             double[] temp_u = new double[kk];
             k = 0;
             for (int j = 0; j < temp_q.length; j++) {
               if (heap.isEmpty()) {
                 break;
               }
               else {
                 HeapArc heaparc = (HeapArc) heap.deleteMin();
                 if (k == 0) {
                   temp_q[k] = heaparc.value;
                   temp_u[k] = heaparc.cap;
                   k++;
                 }
                 else {
                   if (temp_u[k - 1] >= MaxNum)
                     break;
                   if (heaparc.value <= temp_q[k - 1]) {
                     temp_u[k-1] += heaparc.cap;
                   }
                   else {
                     temp_q[k] = heaparc.value;
                     temp_u[k] = heaparc.cap;
                     k++;
                   }
                 }
               }
             }
             q[t][i - 1] = new double[k];
             u[t][i - 1] = new double[k];
             for (int j = 0; j < k; j++) {
               q[t][i - 1][j] = temp_q[j];
               u[t][i - 1][j] = temp_u[j];
             }

           }
           // end of obtaining q and u

         }
         cplex.clearModel();
       }

       //out.println("Ite:\t"+ite+"\tobj:\t"+obj);
       out.println(obj);
       if(max_obj < obj) max_obj =obj;
       //update supply
       for (int t = 0; t <= T - 2; t++) {
         int numArc = 0;
         int numYarc = 0; // number of y
         for (int i = 0; i <= N; i++) {
           numArc += myNetwork.getNumOutArcOf("N" + (t * (N + 1) + i));
           if (i > 0)
             numYarc += q[t + 1][i - 1].length;
         }
         IloNumVar[] x = new IloNumVar[numArc];
         IloNumVar[] y = new IloNumVar[numYarc];
         IloLinearNumExpr obj_exp = cplex.linearNumExpr();
         int k = 0;
         Hashtable indexTable = new Hashtable();
         for (int i = 0; i <= N; i++) {
           ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
               (t * (N + 1) + i));
           while (arc != null) {
             x[k] = cplex.numVar(arc.cap_low, arc.cap_up, "x" + k);
             obj_exp.addTerm(arc.cost, x[k]);
             indexTable.put(arc.getKey(), new Integer(k));
             k++;
             arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
           }
         }
         k = 0;
         for (int i = 1; i <= N; i++) {
           for (int j = 0; j < q[t + 1][i - 1].length; j++) {
             y[k] = cplex.numVar(0, MaxNum + 1, "y" + k);
             obj_exp.addTerm(q[t + 1][i - 1][j], y[k]);
             k++;
           }
         }
         cplex.addMinimize(obj_exp);
         IloRange rng[] = new IloRange[2 * N];
         IloRange stng[] = new IloRange[numYarc];
         k = 0;
         for (int i = 1; i <= N; i++) {
           IloLinearNumExpr exp = cplex.linearNumExpr();
           ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
               (t * (N + 1) + i));
           while (arc != null) {
             int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
             exp.addTerm(1, x[j]);
             arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
           }
           rng[i -
               1] = cplex.addEq(exp, supply[t][i - 1], "N" + (t * (N + 1) + i));
         }

         k = 0;
         for (int i = 1; i <= N; i++) {
           IloLinearNumExpr exp = cplex.linearNumExpr();
           ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
               ( (t + 1) * (N + 1) + i));
           while (arc != null) {
             int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
             exp.addTerm(1, x[j]);
             arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
           }
           for (int j = 0; j < q[t + 1][i - 1].length; j++) {
             exp.addTerm( -1, y[k]);
             k++;
           }
           rng[N + i - 1] = cplex.addEq(exp, 0, "N" + ( (t + 1) * (N + 1) + i));
         }

         //add y constraint
         k = 0;
         for (int i = 1; i <= N; i++) {
           for (int j = 0; j < q[t + 1][i - 1].length; j++) {
             IloLinearNumExpr exp = cplex.linearNumExpr();
             exp.addTerm(1, y[k]);
             stng[k] = cplex.addLe(exp, u[t + 1][i - 1][j],
                                   "Y" + t + "_" + i + "_" + j);
             k++;
           }
         }
         if (cplex.solve()) {
           for (int i = 1; i <= N; i++) {
             ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
                 ( (t + 1) * (N + 1) + i));
             supply[t + 1][i - 1] = 0;
             while (arc != null) {
               int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
               supply[t + 1][i - 1] += cplex.getValue(x[j]);
               arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
             }
           }
         }
        cplex.clearModel();
       } // end of update supply
     }
     long end_time = System.currentTimeMillis();
     out.println(max_obj+"\t"+(end_time-start_time)/1000.0);
     cplex.end();
     out.close();
     outfile.close();
   }
   catch (Exception e) {
      System.err.println("Concert exception '" + e + "' caught");
   }

   return max_obj;
 }

// Gradient method
 public double gradientMethod(Network myNetwork, int ITE,String outFile){
   if(debug) debugOut.println("Iterations of gradientMethod:");
   double obj =Double.NEGATIVE_INFINITY;
   double max_obj = Double.NEGATIVE_INFINITY;
   long start_time = System.currentTimeMillis();
   int N = myNetwork.N;
   int T = myNetwork.T;
   double[][][] q = new double[T][N][]; // slop
   double[][][] u = new double[T][N][]; // upper bound
   double[][] supply = new double[T][N+1]; // upper bound

   for(int t=0;t<T;t++){
     for(int i=0;i<=N;i++){
       supply[t][i] = ((NodeInfo) myNetwork.getNode("N"+i)).supply;
     }
     if(debug) supply[1][1]=supply[1][2]=supply[1][3]=1;
   }

   try {
     IloCplex cplex = new IloCplex();
     cplex.setParam(IloCplex.IntParam.SimDisplay, 0);//0 No iteration messages until solution
           //1 Iteration info after each refactoring
           //2 Iteration info for each iteration

     FileOutputStream outfile = new FileOutputStream(outFile,true);
    PrintStream out = new PrintStream(outfile);
    out.println("Gradient");
     //obtain q and u at the last stage
     if(debug) debugOut.println("obtain q and u at the last stage:");
     for(int i=1;i<=N;i++){
       String nodeKey = "N"+((T-1)*(N+1)+i);
       ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf(nodeKey);
       //BinaryHeap heap = new BinaryHeap(myNetwork.getNumOutArcOf(nodeKey));
       BinaryHeap heap = new BinaryHeap();
       while(arc != null){
         HeapArc heaparc = new HeapArc(arc.getKey(),arc.cost);
         heaparc.cap = arc.cap_up;
         heap.insert(heaparc);
         arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
       }
       double[] temp_q = new double[myNetwork.getNumOutArcOf(nodeKey)];
       double[] temp_u = new double[myNetwork.getNumOutArcOf(nodeKey)];
       int k=0;
       for(int j=0;j<temp_q.length;j++){
         if(heap.isEmpty()){
           break;
         }else {
           HeapArc heaparc = (HeapArc) heap.deleteMin();
           if(k==0){
             temp_q[k] = heaparc.value;
             temp_u[k] = heaparc.cap;
             k++;
           }else{
             if(temp_u[k-1] >= MaxNum) break;
             if(heaparc.value <= temp_q[k-1]){
               temp_u[k-1] += heaparc.cap;
             }else{
               temp_q[k] = heaparc.value;
               temp_u[k] = heaparc.cap;
               k++;
             }
           }
         }
       }
       q[T-1][i-1] = new double[k];
       u[T-1][i-1] = new double[k];
       if(debug) debugOut.print(i);
       for(int j=0;j<k;j++){
         q[T-1][i-1][j] = temp_q[j];
         u[T-1][i-1][j] = temp_u[j];
         if(debug) debugOut.print("\t"+temp_q[j]+"\t"+temp_u[j]);
       }
       if(debug) debugOut.println();
     }
     // end of obtaining q and u at the stage T-1

for(int ite =1;ite <=ITE;ite++){
       if(debug) debugOut.println("ite=:\t"+ite);
       double KStar =0;
       for (int t = T - 2; t >= 0; t--) {
         if(debug) debugOut.println("t=:\t"+t);
         int numArc = 0;
         int numYarc = 0; // number of y
         for (int i = 0; i <= N; i++) {
           numArc += myNetwork.getNumOutArcOf("N" + (t * (N + 1) + i));
           if (i > 0)
             numYarc += q[t + 1][i - 1].length;
         }
         IloNumVar[] x = new IloNumVar[numArc];
         IloNumVar[] y = new IloNumVar[numYarc];
         IloLinearNumExpr obj_exp = cplex.linearNumExpr();
         int k = 0;
         Hashtable indexTable = new Hashtable();
         for (int i = 0; i <= N; i++) {
           ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
               (t * (N + 1) + i));
           while (arc != null) {
             x[k] = cplex.numVar(arc.cap_low, arc.cap_up, "x" + k);
             obj_exp.addTerm(arc.cost, x[k]);
             indexTable.put(arc.getKey(), new Integer(k));
             k++;
             arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
           }
         }
         k = 0;
         for (int i = 1; i <= N; i++) {
           for (int j = 0; j < q[t + 1][i - 1].length; j++) {
             y[k] = cplex.numVar(0, MaxNum + 1, "y" + k);
             obj_exp.addTerm(q[t + 1][i - 1][j], y[k]);
             k++;
           }
         }
         cplex.addMinimize(obj_exp);
         IloRange rng[] = new IloRange[2 * N];
         IloRange stng[] = new IloRange[numYarc];
         k = 0;
         for (int i = 1; i <= N; i++) {
           IloLinearNumExpr exp = cplex.linearNumExpr();
           ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
               (t * (N + 1) + i));
           while (arc != null) {
             int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
             exp.addTerm(1, x[j]);
             arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
           }
           rng[i -1] = cplex.addEq(exp, supply[t][i], "N" + (t * (N + 1) + i));
         }

         k = 0;
         for (int i = 1; i <= N; i++) {
           IloLinearNumExpr exp = cplex.linearNumExpr();
           ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
               ( (t + 1) * (N + 1) + i));
           while (arc != null) {
             int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
             exp.addTerm(1, x[j]);
             arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
           }
           for (int j = 0; j < q[t + 1][i - 1].length; j++) {
             exp.addTerm( -1, y[k]);
             k++;
           }
           rng[N + i - 1] = cplex.addEq(exp, 0, "N" + ( (t + 1) * (N + 1) + i));
         }

         //add y constraint
         k = 0;
         for (int i = 1; i <= N; i++) {
           for (int j = 0; j < q[t + 1][i - 1].length; j++) {
             IloLinearNumExpr exp = cplex.linearNumExpr();
             exp.addTerm(1, y[k]);
             stng[k] = cplex.addLe(exp, u[t + 1][i - 1][j],
                                   "Y" + t + "_" + i + "_" + j);
             k++;
           }
         }

         //cplex.exportModel(".\\data\\scapMethod_" + t + ".lp");
         if (cplex.solve()) {

           obj = cplex.getObjValue() - KStar;

           double[] pi = cplex.getDuals(stng);
           for (int i = 0; i < stng.length; i++)
             pi[i] = -pi[i];

             //obtain q and u
           double[][] qq = new double[N][]; // store the q tempory
           for(int i=0;i<N;i++){
             qq[i] = new double[q[t+1][i].length];
             for(int j=0;j<q[t+1][i].length;j++){
               qq[i][j] = q[t+1][i][j];
             }
           }

           k = 0;
           for (int i = 1; i <= N; i++) {
             for (int j = 0; j < q[t + 1][i - 1].length; j++) {
               qq[i - 1][j] += pi[k];
               if (pi[k] < -0.001) out.println("pi[" + k + "] is less than 0:\t" + pi[k]);
               KStar += pi[k] * u[t + 1][i - 1][j];
               k++;
             }
           }

           for (int i = 1; i <= N; i++) {
             String nodeKey = "N" + (t * (N + 1) + i);
             ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf(nodeKey);
             BinaryHeap heap = new BinaryHeap();
             int kk = 0;
             while (arc != null) {
               arc.cap_rest = arc.cap_up;
               int ii = Integer.parseInt( (arc.getToNodeKey()).substring(1)); // index of toNodeKey
               ii = ii - (t + 1) * (N + 1);
               if (ii > 0) {
                 for (int j = 0; j < q[t + 1][ii - 1].length; j++) {
                   HeapArc heaparc = new HeapArc(arc.getKey() + "_" + j,
                                                 arc.cost + qq[ii - 1][j]);
                   heaparc.cap = Math.min(arc.cap_rest, u[t + 1][ii - 1][j]);
                   arc.cap_rest -= heaparc.cap;
                   heap.insert(heaparc);
                   kk++;
                   if (arc.cap_rest <= 0)
                     break;
                 }
               }
               arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
             }
             double[] temp_q = new double[kk];
             double[] temp_u = new double[kk];
             k = 0;
             for (int j = 0; j < temp_q.length; j++) {
               if (heap.isEmpty()) {
                 break;
               }
               else {
                 HeapArc heaparc = (HeapArc) heap.deleteMin();
                 if (k == 0) {
                   temp_q[k] = heaparc.value;
                   temp_u[k] = heaparc.cap;
                   k++;
                 }
                 else {
                   if (temp_u[k - 1] >= MaxNum)
                     break;
                   if (heaparc.value <= temp_q[k - 1]) {
                     temp_u[k-1] += heaparc.cap;
                   }
                   else {
                     temp_q[k] = heaparc.value;
                     temp_u[k] = heaparc.cap;
                     k++;
                   }
                 }
               }
             }
             q[t][i - 1] = new double[k];
             u[t][i - 1] = new double[k];
             if(debug) debugOut.print(i);
             for (int j = 0; j < k; j++) {
               q[t][i - 1][j] = temp_q[j];
               u[t][i - 1][j] = temp_u[j];
               if(debug) debugOut.print("\t"+temp_q[j]+"\t"+temp_u[j]);
             }
             if(debug) debugOut.println();
           }
           // end of obtaining q and u
           if(debug) debugOut.println("end of obtaining q and u.");
         }
         cplex.clearModel();
       }

       //out.println("Ite:\t"+ite+"\tobj:\t"+obj);
       if(debug) debugOut.println("obj:\t"+obj);
       out.println(obj);
       if(max_obj < obj) max_obj = obj;
       //update supply
       if(debug) debugOut.println("update supply:");
       for (int t = 0; t <= T - 3; t++) {
         if(debug) debugOut.println("t=:\t"+t);
         int numArc = 0;
         int numYarc = 0; // number of y
         for (int i = 0; i <= N; i++) {
           numArc += myNetwork.getNumOutArcOf("N" + (t * (N + 1) + i));
           if (i > 0)
             numYarc += q[t + 1][i - 1].length;
         }
         IloNumVar[] x = new IloNumVar[numArc];
         IloNumVar[] y = new IloNumVar[numYarc];
         IloLinearNumExpr obj_exp = cplex.linearNumExpr();
         int k = 0;
         Hashtable indexTable = new Hashtable();
         for (int i = 0; i <= N; i++) {
           ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
               (t * (N + 1) + i));
           while (arc != null) {
             x[k] = cplex.numVar(arc.cap_low, arc.cap_up, "x" + k);
             obj_exp.addTerm(arc.cost, x[k]);
             indexTable.put(arc.getKey(), new Integer(k));
             k++;
             arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
           }
         }
         k = 0;
         for (int i = 1; i <= N; i++) {
           for (int j = 0; j < q[t + 1][i - 1].length; j++) {
             y[k] = cplex.numVar(0, MaxNum + 1, "y" + k);
             obj_exp.addTerm(q[t + 1][i - 1][j], y[k]);
             k++;
           }
         }
         cplex.addMinimize(obj_exp);
         IloRange rng[] = new IloRange[2 * N];
         IloRange stng[] = new IloRange[numYarc];
         k = 0;
         for (int i = 1; i <= N; i++) {
           IloLinearNumExpr exp = cplex.linearNumExpr();
           ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
               (t * (N + 1) + i));
           while (arc != null) {
             int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
             exp.addTerm(1, x[j]);
             arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
           }
           rng[i - 1] = cplex.addEq(exp, supply[t][i], "N" + (t * (N + 1) + i));
         }

         k = 0;
         for (int i = 1; i <= N; i++) {
           IloLinearNumExpr exp = cplex.linearNumExpr();
           ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
               ( (t + 1) * (N + 1) + i));
           while (arc != null) {
             int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
             exp.addTerm(1, x[j]);
             arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
           }
           for (int j = 0; j < q[t + 1][i - 1].length; j++) {
             exp.addTerm( -1, y[k]);
             k++;
           }
           rng[N + i -
               1] = cplex.addEq(exp, 0, "N" + ( (t + 1) * (N + 1) + i));
         }

         //add y constraint
         k = 0;
         for (int i = 1; i <= N; i++) {
           for (int j = 0; j < q[t + 1][i - 1].length; j++) {
             IloLinearNumExpr exp = cplex.linearNumExpr();
             exp.addTerm(1, y[k]);
             stng[k] = cplex.addLe(exp, u[t + 1][i - 1][j],
                                   "Y" + t + "_" + i + "_" + j);
             k++;
           }
         }
         if (cplex.solve()) {
           for (int i = 0; i <= N; i++) {
             ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
                 ( (t + 1) * (N + 1) + i));
             supply[t + 1][i ] = 0;
             while (arc != null) {
               int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
               supply[t + 1][i] += cplex.getValue(x[j]);
               arc.flow = cplex.getValue(x[j]);
               arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
             }
             if(debug) debugOut.println("supply "+i+":\t"+supply[t + 1][i ]);
           }
         }
         cplex.clearModel();

         t = t+1;
         for(int ite2=1;ite2<=5;ite2++){
           if(debug) debugOut.println("ite2=:\t "+ite2);
           //update q and u for the new supply
           if(debug) debugOut.println("update q and u for the new supply:");
           numArc = 0;
           numYarc = 0; // number of y
           for (int i = 0; i <= N; i++) {
             numArc += myNetwork.getNumOutArcOf("N" + (t * (N + 1) + i));
             if (i > 0)
               numYarc += q[t + 1][i - 1].length;
           }
           x = new IloNumVar[numArc];
           y = new IloNumVar[numYarc];
           obj_exp = cplex.linearNumExpr();
           k = 0;
           indexTable = new Hashtable();
           for (int i = 0; i <= N; i++) {
             ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
                 (t * (N + 1) + i));
             while (arc != null) {
               x[k] = cplex.numVar(arc.cap_low, arc.cap_up, "x" + k);
               obj_exp.addTerm(arc.cost, x[k]);
               indexTable.put(arc.getKey(), new Integer(k));
               k++;
               arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
             }
           }
           k = 0;
           for (int i = 1; i <= N; i++) {
             for (int j = 0; j < q[t + 1][i - 1].length; j++) {
               y[k] = cplex.numVar(0, MaxNum + 1, "y" + k);
               obj_exp.addTerm(q[t + 1][i - 1][j], y[k]);
               k++;
             }
           }
           cplex.addMinimize(obj_exp);
           rng = new IloRange[2 * N];
           stng = new IloRange[numYarc];
           k = 0;
           for (int i = 1; i <= N; i++) {
             IloLinearNumExpr exp = cplex.linearNumExpr();
             ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
                 (t * (N + 1) + i));
             while (arc != null) {
               int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
               exp.addTerm(1, x[j]);
               arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
             }
             rng[i - 1] = cplex.addEq(exp, supply[t][i ], "N" + (t * (N + 1) + i));
           }

           k = 0;
           for (int i = 1; i <= N; i++) {
             IloLinearNumExpr exp = cplex.linearNumExpr();
             ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
                 ( (t + 1) * (N + 1) + i));
             while (arc != null) {
               int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
               exp.addTerm(1, x[j]);
               arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
             }
             for (int j = 0; j < q[t + 1][i - 1].length; j++) {
               exp.addTerm( -1, y[k]);
               k++;
             }
             rng[N + i - 1] = cplex.addEq(exp, 0, "N" + ( (t + 1) * (N + 1) + i));
           }

           //add y constraint
           k = 0;
           for (int i = 1; i <= N; i++) {
             for (int j = 0; j < q[t + 1][i - 1].length; j++) {
               IloLinearNumExpr exp = cplex.linearNumExpr();
               exp.addTerm(1, y[k]);
               stng[k] = cplex.addLe(exp, u[t + 1][i - 1][j],
                                     "Y" + t + "_" + i + "_" + j);
               k++;
             }
           }

           //cplex.exportModel(".\\data\\gradientMethod_" + t + ".lp");
           if (cplex.solve()) {

             obj = cplex.getObjValue() - KStar;

             double[] pi = cplex.getDuals(stng);
             for (int i = 0; i < stng.length; i++)
               pi[i] = -pi[i];

               //obtain q and u
             double[][] qq = new double[N][]; // store the q tempory
             for(int i=0;i<N;i++){
               qq[i] = new double[q[t+1][i].length];
               for(int j=0;j<q[t+1][i].length;j++){
                 qq[i][j] = q[t+1][i][j];
               }
             }

             k = 0;
             for (int i = 1; i <= N; i++) {
               for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                 qq[i - 1][j] += pi[k];
                 if (pi[k] < -0.001)
                   out.println("pi[" + k + "] is less than 0:\t" + pi[k]);
                 KStar += pi[k] * u[t + 1][i - 1][j];
                 k++;
               }
             }

             for (int i = 1; i <= N; i++) {
               String nodeKey = "N" + (t * (N + 1) + i);
               ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf(nodeKey);
               BinaryHeap heap = new BinaryHeap();
               int kk = 0;
               while (arc != null) {
                 arc.cap_rest = arc.cap_up;
                 int ii = Integer.parseInt( (arc.getToNodeKey()).substring(1)); // index of toNodeKey
                 ii = ii - (t + 1) * (N + 1);
                 if (ii > 0) {
                   for (int j = 0; j < q[t + 1][ii - 1].length; j++) {
                     HeapArc heaparc = new HeapArc(arc.getKey() + "_" + j,
                         arc.cost + qq[ii - 1][j]);
                     heaparc.cap = Math.min(arc.cap_rest, u[t + 1][ii - 1][j]);
                     arc.cap_rest -= heaparc.cap;
                     heap.insert(heaparc);
                     kk++;
                     if (arc.cap_rest <= 0)
                       break;
                   }
                 }
                 arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
               }
               double[] temp_q = new double[kk];
               double[] temp_u = new double[kk];
               k = 0;
               for (int j = 0; j < temp_q.length; j++) {
                 if (heap.isEmpty()) {
                   break;
                 }
                 else {
                   HeapArc heaparc = (HeapArc) heap.deleteMin();
                   if (k == 0) {
                     temp_q[k] = heaparc.value;
                     temp_u[k] = heaparc.cap;
                     k++;
                   }
                   else {
                     if (temp_u[k - 1] >= MaxNum)
                       break;
                     if (heaparc.value <= temp_q[k - 1]) {
                       temp_u[k - 1] += heaparc.cap;
                     }
                     else {
                       temp_q[k] = heaparc.value;
                       temp_u[k] = heaparc.cap;
                       k++;
                     }
                   }
                 }
               }
               q[t][i - 1] = new double[k];
               u[t][i - 1] = new double[k];
               if(debug) debugOut.print(i);
               for (int j = 0; j < k; j++) {
                 q[t][i - 1][j] = temp_q[j];
                 u[t][i - 1][j] = temp_u[j];
                 if(debug) debugOut.print("\t"+temp_q[j]+"\t"+temp_u[j]);
               }
               if(debug) debugOut.println();
             }
           }
           cplex.clearModel();

           //find gradient
           if(debug) debugOut.println("find gradient:");
           SnNetwork snNetwork = new SnNetwork();
           SnNetInfo snNet = new SnNetInfo("N001");
           SnNodeInfo snNode = new SnNodeInfo("Ns","N001");
           double total_supply =0;
           for(int i=0;i<=N;i++){
             total_supply += supply[t][i];
           }
           snNode.supply = (int) total_supply;
           snNetwork.addNet(snNet);
           snNetwork.addNode(snNode);
           snNode = new SnNodeInfo("Nt","N001");
           snNode.supply = (int) (-1* total_supply);
           snNetwork.addNode(snNode);
           SnArcInfo snArc;
           for(int i=0;i<=N;i++){
             snNode = new SnNodeInfo("N"+(t*(N+1)+i),"N001");
             snNode.supply = 0;
             snNetwork.addNode(snNode);
             snNode = new SnNodeInfo("N"+((t-1)*(N+1)+i),"N001");
             snNode.supply = 0;
             snNetwork.addNode(snNode);
             snArc = new SnArcInfo("S"+i,"N001","Ns","N"+(t*(N+1)+i));
             snArc.cost =0;
             snArc.uBound = (int) supply[t][i];
             if(snArc.uBound > 0) snNetwork.addArc(snArc);
             snArc = new SnArcInfo("T"+i,"N001","N"+(t*(N+1)+i),"Nt");
             snArc.cost =0;
             snArc.uBound = (int) MaxNum;
             if(snArc.uBound > 0) snNetwork.addArc(snArc);
           }
           ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) ));
           while(arc != null){
             snArc = new SnArcInfo(arc.getKey(),"N001",arc.getFromNodeKey(),arc.getToNodeKey());
             snArc.cost = arc.cost +0;
             snArc.uBound =(int) (arc.cap_up - arc.flow);
             if(snArc.uBound > 0) snNetwork.addArc(snArc);
             snArc = new SnArcInfo(arc.getKey()+"_r","N001",arc.getToNodeKey(),arc.getFromNodeKey());
             snArc.cost = - arc.cost +0.1;
             snArc.uBound = (int) arc.flow;
             if(snArc.uBound > 0) snNetwork.addArc(snArc);
             arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
           }
           for(int i=1;i<=N;i++){
             double q_left =0,q_right=0,u_break=0;
             double u_left =0,u_right=0;
             for(int j=0;j<q[t][i-1].length;j++){
               u_break += u[t][i-1][j];
               if(u_break > supply[t][i]){
                 q_left = q_right = q[t][i-1][j];
                 u_left = supply[t][i] - u_break +u[t][i-1][j];
                 u_right = u_break - supply[t][i];
                 break;
               }
               else if(u_break == supply[t][i]){
                 q_left = q[t][i-1][j];
                 q_right = q[t][i-1][j+1];
                 u_left = u[t][i-1][j];
                 u_right = u[t][i-1][j+1];
                 break;
               }
             }
             arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1)+i ));
             while(arc != null){
               snArc = new SnArcInfo(arc.getKey(),"N001",arc.getFromNodeKey(),arc.getToNodeKey());
               snArc.cost = arc.cost + q_right;
               snArc.uBound =(int) Math.min(arc.cap_up - arc.flow,u_right);
               if(snArc.uBound > 0) snNetwork.addArc(snArc);
               snArc = new SnArcInfo(arc.getKey()+"_r","N001",arc.getToNodeKey(),arc.getFromNodeKey());
               snArc.cost = - arc.cost - q_left;
               if(q_left >= q_right) snArc.cost +=0.1;
               snArc.uBound = (int) Math.min(arc.flow,u_left);
               if(snArc.uBound > 0) snNetwork.addArc(snArc);
               arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
             }
           }

           snArc = (SnArcInfo) snNetwork.getFirstArcIn("N001");
           if (debug) System.out.println(supply[t][1]+"\t"+supply[t][2]+"\t"+supply[t][3]);
           while(snArc != null){
             if (debug) System.out.println(snArc.getKey()+"\t"+snArc.getFromNodeKey()+"\t"+snArc.getToNodeKey()+"\t"+snArc.cost+"\t"+snArc.uBound);
             snArc = (SnArcInfo) snNetwork.getNextArcFrom(snArc.getKey());
           }
           SnNodeInfo artNode = snNetwork.bigMStart("N001",100);
           snNetwork.snAlgorithm("N001",300,200);
           if(debug) debugOut.println("snNetwork.getObjfcn\t:"+snNetwork.getObjfcn());
           if(snNetwork.getObjfcn() < - 0.1){
             for(int i=0;i<=N;i++){
               arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1)+i ));
               if( arc != null) supply[t][i] =0;
               while( arc != null){
                 double f1=0,f2=0;
                 if((SnArcInfo) snNetwork.getArc(arc.getKey()) != null) f1 = ((SnArcInfo) snNetwork.getArc(arc.getKey())).flow;
                 if((SnArcInfo) snNetwork.getArc(arc.getKey()+"_r") != null) f2 =  ((SnArcInfo) snNetwork.getArc(arc.getKey()+"_r")).flow;
                 arc.flow += (double) (f1 - f2);
                 supply[t][i] += arc.flow;
                 arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
               }
               if(debug) debugOut.println("supply "+i+":\t"+supply[t][i]);
             }
             // go back to update q and u
             if(debug) debugOut.println("go back to update q and u");
           } else break;


         }
         t=t-1;
       } // end of update supply
       if(debug) debugOut.println("end of update supply.");
     }
     long end_time = System.currentTimeMillis();
     out.println(max_obj+"\t"+(end_time-start_time)/1000.0);
     cplex.end();
     out.close();
     outfile.close();
   }
   catch (Exception e) {
      System.err.println("Concert exception '" + e + "' caught");
   }

   return max_obj;
 }


  public double meanMethod(Network myNetwork,String outFile){
    double obj =Double.NEGATIVE_INFINITY;
    int N = myNetwork.N;
    int T = myNetwork.T;
    try {
      IloCplex cplex = new IloCplex();
      cplex.setParam(IloCplex.IntParam.SimDisplay, 0);//0 No iteration messages until solution
            //1 Iteration info after each refactoring
            //2 Iteration info for each iteration

      FileOutputStream outfile = new FileOutputStream(outFile);
      PrintStream out = new PrintStream(outfile);

      int numArc = myNetwork.getNumArc();
      IloNumVar[] x = new IloNumVar[numArc];
      IloNumVar[] y = new IloNumVar[T*N]; // shortage
      IloNumVar[] z = new IloNumVar[T*N]; // overage
      IloLinearNumExpr obj_exp = cplex.linearNumExpr();
      for(int i=0;i<numArc;i++){
        ArcInfo arc = (ArcInfo) myNetwork.getArc("A"+i);
        x[i] = cplex.numVar(arc.cap_low,arc.cap_up,"x"+i);
        obj_exp.addTerm(arc.cost,x[i]);
      }
      int k=0;
      for(int t=1;t<=T;t++){
        for(int i=1;i<=N;i++){
          NodeInfo node = (NodeInfo) myNetwork.getNode("N"+(t*(N+1)+i));
          y[k] = cplex.numVar(0,MaxNum, "y"+k);
          obj_exp.addTerm(node.shortage_cost,y[k]);
          k++;
        }
      }
      k=0;
      for(int t=1;t<=T;t++){
        for(int i=1;i<=N;i++){
          NodeInfo node = (NodeInfo) myNetwork.getNode("N"+(t*(N+1)+i));
          z[k] = cplex.numVar(0,MaxNum, "z"+k);
          obj_exp.addTerm(node.overage_cost,z[k]);
          k++;
        }
      }
      cplex.addMinimize(obj_exp);

      IloRange rng[] = new IloRange[T*N];
      IloRange stng[] = new IloRange[T*N];
      IloRange ovng[] = new IloRange[T*N];
      for(int i=1;i<=N;i++){
        NodeInfo node = (NodeInfo) myNetwork.getNode("N"+i);
        IloLinearNumExpr exp = cplex.linearNumExpr();
        ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf(node.getKey());
        while(arc != null){
          String arcKey = arc.getKey();
          int j = Integer.parseInt(arcKey.substring(1));
          exp.addTerm(1,x[j]);
          arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
        }
        rng[i-1] = cplex.addEq(exp,node.supply,node.getKey());
      }
      for(int t=1;t<T;t++){
        for(int i=1;i<=N;i++){
          NodeInfo node = (NodeInfo) myNetwork.getNode("N"+(i+t*(N+1)));
          IloLinearNumExpr exp = cplex.linearNumExpr();
          ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf(node.getKey());
          while(arc != null){
            String arcKey = arc.getKey();
            int j = Integer.parseInt(arcKey.substring(1));
            exp.addTerm(1,x[j]);
            arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
          }

          arc = (ArcInfo) myNetwork.getFirstOutArcOf(node.getKey());
          while(arc != null){
            String arcKey = arc.getKey();
            int j = Integer.parseInt(arcKey.substring(1));
            exp.addTerm(-1,x[j]);
            arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
          }
          rng[t*N+i-1] = cplex.addEq(exp,0,node.getKey());
        }

      }

// add shortage
      for(int t=1;t<=T;t++){
        for(int i=1;i<=N;i++){
          NodeInfo node = (NodeInfo) myNetwork.getNode("N"+(i+t*(N+1)));
          IloLinearNumExpr exp = cplex.linearNumExpr();
          ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf(node.getKey());
          while(arc != null){
            String arcKey = arc.getKey();
            int j = Integer.parseInt(arcKey.substring(1));
            exp.addTerm(1,x[j]);
            arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
          }
          exp.addTerm(1,y[(t-1)*N+i-1]);
          stng[(t-1)*N+i-1] = cplex.addGe(exp,node.demand,node.getKey()+"_s");
        }
      }
// add overage
      for(int t=1;t<=T;t++){
        for(int i=1;i<=N;i++){
          NodeInfo node = (NodeInfo) myNetwork.getNode("N"+(i+t*(N+1)));
          IloLinearNumExpr exp = cplex.linearNumExpr();
          ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf(node.getKey());
          while(arc != null){
            String arcKey = arc.getKey();
            int j = Integer.parseInt(arcKey.substring(1));
            exp.addTerm(1,x[j]);
            arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
          }
          exp.addTerm(-1,z[(t-1)*N+i-1]);
          ovng[(t-1)*N+i-1] = cplex.addLe(exp,node.demand,node.getKey()+"_o");
        }
      }

      if (debug) cplex.exportModel(".\\data\\meanMethod.lp");
// solve the model and display the solution if one was found

      if ( cplex.solve() ) {
        //cplex.output().println("Solution status = " + cplex.getStatus());
         //cplex.output().println("Solution value  = " + cplex.getObjValue());
         int ncols = cplex.getNcols();
         obj = cplex.getObjValue();
         out.println(obj);
         out.close();
         outfile.close();
         for (int j = 0; j < x.length; ++j) {
            if (debug) cplex.output().println("x[" + j + "]\t " + cplex.getValue(x[j]));
         }

         /*
         double[] dj    = cplex.getReducedCosts(x);
         double[] pi    = cplex.getDuals(rng);
         double[] slack = cplex.getSlacks(rng);
         int ncols = cplex.getNcols();
         for (int j = 0; j < ncols; ++j) {
            cplex.output().println("Column: " + j +
                                " Value = " + x[j] +
                                " Reduced cost = " + dj[j]);
         }

         int nrows = cplex.getNrows();
         for (int i = 0; i < nrows; ++i) {
            cplex.output().println("Row   : " + i +
                                " Slack = " + slack[i] +
                                " Pi = " + pi[i]);
         }
            */
      }

      cplex.end();

    }
    catch (Exception e) {
       System.err.println("Concert exception '" + e + "' caught");
    }

    return obj;
  }

  public double newMethod(Network myNetwork, int ITE, String outFile){
    if(debug) debugOut.println("Iterations of newMethod:");
    double obj =Double.NEGATIVE_INFINITY;
    double max_obj = Double.NEGATIVE_INFINITY;
    long start_time = System.currentTimeMillis();
    int N = myNetwork.N;
    int T = myNetwork.T;
    double[][][] q = new double[T][N][]; // slop
    double[][][] u = new double[T][N][]; // upper bound
    double[][] supply = new double[T][N+1]; // upper bound

    for(int t=0;t<T;t++){
      for(int i=0;i<=N;i++){
        supply[t][i] = ((NodeInfo) myNetwork.getNode("N"+i)).supply;
      }
      if(debug) supply[1][1]=supply[1][2]=supply[1][3]=1;
    }

    try {
      IloCplex cplex = new IloCplex();
      cplex.setParam(IloCplex.IntParam.SimDisplay, 0);//0 No iteration messages until solution
            //1 Iteration info after each refactoring
            //2 Iteration info for each iteration

      FileOutputStream outfile = new FileOutputStream(outFile,true);
     PrintStream out = new PrintStream(outfile);
     out.println("NewMethod");
      //obtain q and u at the last stage
      if(debug) debugOut.println("obtain q and u at the last stage:");
      for(int i=1;i<=N;i++){
        String nodeKey = "N"+((T-1)*(N+1)+i);
        ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf(nodeKey);
        //BinaryHeap heap = new BinaryHeap(myNetwork.getNumOutArcOf(nodeKey));
        BinaryHeap heap = new BinaryHeap();
        while(arc != null){
          HeapArc heaparc = new HeapArc(arc.getKey(),arc.cost);
          heaparc.cap = arc.cap_up;
          heap.insert(heaparc);
          arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
        }
        double[] temp_q = new double[myNetwork.getNumOutArcOf(nodeKey)];
        double[] temp_u = new double[myNetwork.getNumOutArcOf(nodeKey)];
        int k=0;
        for(int j=0;j<temp_q.length;j++){
          if(heap.isEmpty()){
            break;
          }else {
            HeapArc heaparc = (HeapArc) heap.deleteMin();
            if(k==0){
              temp_q[k] = heaparc.value;
              temp_u[k] = heaparc.cap;
              k++;
            }else{
              if(temp_u[k-1] >= MaxNum) break;
              if(heaparc.value <= temp_q[k-1]){
                temp_u[k-1] += heaparc.cap;
              }else{
                temp_q[k] = heaparc.value;
                temp_u[k] = heaparc.cap;
                k++;
              }
            }
          }
        }
        q[T-1][i-1] = new double[k];
        u[T-1][i-1] = new double[k];
        if(debug) debugOut.print(i);
        for(int j=0;j<k;j++){
          q[T-1][i-1][j] = temp_q[j];
          u[T-1][i-1][j] = temp_u[j];
          if(debug) debugOut.print("\t"+temp_q[j]+"\t"+temp_u[j]);
        }
        if(debug) debugOut.println();
      }
      // end of obtaining q and u at the stage T-1
      if(debug) debugOut.println("end of obtaining q and u at the stage.");
 for(int ite =1;ite <=ITE;ite++){
        double KStar =0;
        if(debug) debugOut.println("ite=:\t"+ite);
        for (int t = T - 2; t >= 0; t--) {
          if(debug) debugOut.println("t=:\t"+t);
          int numArc = 0;
          int numYarc = 0; // number of y
          for (int i = 0; i <= N; i++) {
            numArc += myNetwork.getNumOutArcOf("N" + (t * (N + 1) + i));
            if (i > 0)
              numYarc += q[t + 1][i - 1].length;
          }
          IloNumVar[] x = new IloNumVar[numArc];
          IloNumVar[] y = new IloNumVar[numYarc];
          IloLinearNumExpr obj_exp = cplex.linearNumExpr();
          int k = 0;
          Hashtable indexTable = new Hashtable();
          for (int i = 0; i <= N; i++) {
            ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
                (t * (N + 1) + i));
            while (arc != null) {
              x[k] = cplex.numVar(arc.cap_low, arc.cap_up, "x" + k);
              obj_exp.addTerm(arc.cost, x[k]);
              indexTable.put(arc.getKey(), new Integer(k));
              k++;
              arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
            }
          }
          k = 0;
          for (int i = 1; i <= N; i++) {
            for (int j = 0; j < q[t + 1][i - 1].length; j++) {
              y[k] = cplex.numVar(0, MaxNum + 1, "y" + k);
              obj_exp.addTerm(q[t + 1][i - 1][j], y[k]);
              k++;
            }
          }
          cplex.addMinimize(obj_exp);
          IloRange rng[] = new IloRange[2 * N];
          IloRange stng[] = new IloRange[numYarc];
          k = 0;
          for (int i = 1; i <= N; i++) {
            IloLinearNumExpr exp = cplex.linearNumExpr();
            ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
                (t * (N + 1) + i));
            while (arc != null) {
              int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
              exp.addTerm(1, x[j]);
              arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
            }
            rng[i -1] = cplex.addEq(exp, supply[t][i], "N" + (t * (N + 1) + i));
          }

          k = 0;
          for (int i = 1; i <= N; i++) {
            IloLinearNumExpr exp = cplex.linearNumExpr();
            ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
                ( (t + 1) * (N + 1) + i));
            while (arc != null) {
              int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
              exp.addTerm(1, x[j]);
              arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
            }
            for (int j = 0; j < q[t + 1][i - 1].length; j++) {
              exp.addTerm( -1, y[k]);
              k++;
            }
            rng[N + i - 1] = cplex.addEq(exp, 0, "N" + ( (t + 1) * (N + 1) + i));
          }

          //add y constraint
          k = 0;
          for (int i = 1; i <= N; i++) {
            for (int j = 0; j < q[t + 1][i - 1].length; j++) {
              IloLinearNumExpr exp = cplex.linearNumExpr();
              exp.addTerm(1, y[k]);
              stng[k] = cplex.addLe(exp, u[t + 1][i - 1][j],
                                    "Y" + t + "_" + i + "_" + j);
              k++;
            }
          }

          //cplex.exportModel(".\\data\\scapMethod_" + t + ".lp");
          if (cplex.solve()) {

            obj = cplex.getObjValue() - KStar;

            double[] pi = cplex.getDuals(stng);
            for (int i = 0; i < stng.length; i++)
              pi[i] = -pi[i];

              //obtain q and u
            double[][] qq = new double[N][]; // store the q tempory
            for(int i=0;i<N;i++){
              qq[i] = new double[q[t+1][i].length];
              for(int j=0;j<q[t+1][i].length;j++){
                qq[i][j] = q[t+1][i][j];
              }
            }

            k = 0;
            for (int i = 1; i <= N; i++) {
              for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                qq[i - 1][j] += pi[k];
                if (pi[k] < -0.001) out.println("pi[" + k + "] is less than 0:\t" + pi[k]);
                KStar += pi[k] * u[t + 1][i - 1][j];
                k++;
              }
            }

            for (int i = 1; i <= N; i++) {
              String nodeKey = "N" + (t * (N + 1) + i);
              ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf(nodeKey);
              BinaryHeap heap = new BinaryHeap();
              int kk = 0;
              while (arc != null) {
                arc.cap_rest = arc.cap_up;
                int ii = Integer.parseInt( (arc.getToNodeKey()).substring(1)); // index of toNodeKey
                ii = ii - (t + 1) * (N + 1);
                if (ii > 0) {
                  for (int j = 0; j < q[t + 1][ii - 1].length; j++) {
                    HeapArc heaparc = new HeapArc(arc.getKey() + "_" + j,
                                                  arc.cost + qq[ii - 1][j]);
                    heaparc.cap = Math.min(arc.cap_rest, u[t + 1][ii - 1][j]);
                    arc.cap_rest -= heaparc.cap;
                    heap.insert(heaparc);
                    kk++;
                    if (arc.cap_rest <= 0)
                      break;
                  }
                }
                arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
              }
              double[] temp_q = new double[kk];
              double[] temp_u = new double[kk];
              k = 0;
              for (int j = 0; j < temp_q.length; j++) {
                if (heap.isEmpty()) {
                  break;
                }
                else {
                  HeapArc heaparc = (HeapArc) heap.deleteMin();
                  if (k == 0) {
                    temp_q[k] = heaparc.value;
                    temp_u[k] = heaparc.cap;
                    k++;
                  }
                  else {
                    if (temp_u[k - 1] >= MaxNum)
                      break;
                    if (heaparc.value <= temp_q[k - 1]) {
                      temp_u[k-1] += heaparc.cap;
                    }
                    else {
                      temp_q[k] = heaparc.value;
                      temp_u[k] = heaparc.cap;
                      k++;
                    }
                  }
                }
              }
              q[t][i - 1] = new double[k];
              u[t][i - 1] = new double[k];
              if(debug) debugOut.print(i);
              for (int j = 0; j < k; j++) {
                q[t][i - 1][j] = temp_q[j];
                u[t][i - 1][j] = temp_u[j];
                if(debug) debugOut.print("\t"+temp_q[j]+"\t"+temp_u[j]);
              }
              if(debug) debugOut.println();
            }
            // end of obtaining q and u

          }
          cplex.clearModel();
        }

        //out.println("Ite:\t"+ite+"\tobj:\t"+obj);
        if(debug) debugOut.println("obj:\t"+obj);
        out.println(obj);
        if(max_obj < obj) max_obj = obj;
        //update supply
        if(debug) debugOut.println("update supply");
        for (int t = 0; t <= T - 3; t++) {
          if(debug) debugOut.println("t=:\t"+t);
          if(debug) debugOut.println("get flow");
          int numArc = 0;
          int numYarc = 0; // number of y
          for (int i = 0; i <= N; i++) {
            numArc += myNetwork.getNumOutArcOf("N" + (t * (N + 1) + i));
            if (i > 0)
              numYarc += q[t + 1][i - 1].length;
          }
          IloNumVar[] x = new IloNumVar[numArc];
          IloNumVar[] y = new IloNumVar[numYarc];
          IloLinearNumExpr obj_exp = cplex.linearNumExpr();
          int k = 0;
          Hashtable indexTable = new Hashtable();
          for (int i = 0; i <= N; i++) {
            ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
                (t * (N + 1) + i));
            while (arc != null) {
              x[k] = cplex.numVar(arc.cap_low, arc.cap_up, "x" + k);
              obj_exp.addTerm(arc.cost, x[k]);
              indexTable.put(arc.getKey(), new Integer(k));
              k++;
              arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
            }
          }
          k = 0;
          for (int i = 1; i <= N; i++) {
            for (int j = 0; j < q[t + 1][i - 1].length; j++) {
              y[k] = cplex.numVar(0, MaxNum + 1, "y" + k);
              obj_exp.addTerm(q[t + 1][i - 1][j], y[k]);
              k++;
            }
          }
          cplex.addMinimize(obj_exp);
          IloRange rng[] = new IloRange[2 * N];
          IloRange stng[] = new IloRange[numYarc];
          k = 0;
          for (int i = 1; i <= N; i++) {
            IloLinearNumExpr exp = cplex.linearNumExpr();
            ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
                (t * (N + 1) + i));
            while (arc != null) {
              int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
              exp.addTerm(1, x[j]);
              arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
            }
            rng[i - 1] = cplex.addEq(exp, supply[t][i], "N" + (t * (N + 1) + i));
          }

          k = 0;
          for (int i = 1; i <= N; i++) {
            IloLinearNumExpr exp = cplex.linearNumExpr();
            ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
                ( (t + 1) * (N + 1) + i));
            while (arc != null) {
              int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
              exp.addTerm(1, x[j]);
              arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
            }
            for (int j = 0; j < q[t + 1][i - 1].length; j++) {
              exp.addTerm( -1, y[k]);
              k++;
            }
            rng[N + i -
                1] = cplex.addEq(exp, 0, "N" + ( (t + 1) * (N + 1) + i));
          }

          //add y constraint
          k = 0;
          for (int i = 1; i <= N; i++) {
            for (int j = 0; j < q[t + 1][i - 1].length; j++) {
              IloLinearNumExpr exp = cplex.linearNumExpr();
              exp.addTerm(1, y[k]);
              stng[k] = cplex.addLe(exp, u[t + 1][i - 1][j],
                                    "Y" + t + "_" + i + "_" + j);
              k++;
            }
          }
          if (cplex.solve()) {
            for (int i = 0; i <= N; i++) {
              ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
                  ( (t + 1) * (N + 1) + i));
              supply[t + 1][i ] = 0;
              while (arc != null) {
                int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
                supply[t + 1][i] += cplex.getValue(x[j]);
                arc.flow = cplex.getValue(x[j]);
                if(debug) debugOut.print(arc.getKey()+"\t"+arc.flow+"\t");
                arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
              }
              //if(debug) debugOut.println();
              if(debug) debugOut.println("supply "+i+":\t"+supply[t + 1][i ]);
            }
          }
          cplex.clearModel();

          t = t+1;

          if(debug) debugOut.println("t=t+1");
            //update q and u for the new supply
            if(debug) debugOut.println("update q and u for the new supply:");
            numArc = 0;
            numYarc = 0; // number of y
            for (int i = 0; i <= N; i++) {
              numArc += myNetwork.getNumOutArcOf("N" + (t * (N + 1) + i));
              if (i > 0)
                numYarc += q[t + 1][i - 1].length;
            }
            x = new IloNumVar[numArc];
            y = new IloNumVar[numYarc];
            obj_exp = cplex.linearNumExpr();
            k = 0;
            indexTable = new Hashtable();
            for (int i = 0; i <= N; i++) {
              ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
                  (t * (N + 1) + i));
              while (arc != null) {
                x[k] = cplex.numVar(arc.cap_low, arc.cap_up, "x" + k);
                obj_exp.addTerm(arc.cost, x[k]);
                indexTable.put(arc.getKey(), new Integer(k));
                k++;
                arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
              }
            }
            k = 0;
            for (int i = 1; i <= N; i++) {
              for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                y[k] = cplex.numVar(0, MaxNum + 1, "y" + k);
                obj_exp.addTerm(q[t + 1][i - 1][j], y[k]);
                k++;
              }
            }
            cplex.addMinimize(obj_exp);
            rng = new IloRange[2 * N];
            stng = new IloRange[numYarc];
            k = 0;
            for (int i = 1; i <= N; i++) {
              IloLinearNumExpr exp = cplex.linearNumExpr();
              ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
                  (t * (N + 1) + i));
              while (arc != null) {
                int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
                exp.addTerm(1, x[j]);
                arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
              }
              rng[i - 1] = cplex.addEq(exp, supply[t][i ], "N" + (t * (N + 1) + i));
            }

            k = 0;
            for (int i = 1; i <= N; i++) {
              IloLinearNumExpr exp = cplex.linearNumExpr();
              ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
                  ( (t + 1) * (N + 1) + i));
              while (arc != null) {
                int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
                exp.addTerm(1, x[j]);
                arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
              }
              for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                exp.addTerm( -1, y[k]);
                k++;
              }
              rng[N + i - 1] = cplex.addEq(exp, 0, "N" + ( (t + 1) * (N + 1) + i));
            }

            //add y constraint
            k = 0;
            for (int i = 1; i <= N; i++) {
              for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                IloLinearNumExpr exp = cplex.linearNumExpr();
                exp.addTerm(1, y[k]);
                stng[k] = cplex.addLe(exp, u[t + 1][i - 1][j],
                                      "Y" + t + "_" + i + "_" + j);
                k++;
              }
            }

            //cplex.exportModel(".\\data\\gradientMethod_" + t + ".lp");
            if (cplex.solve()) {

              obj = cplex.getObjValue() - KStar;
              double[][] qq = new double[N][]; // store the q tempory
              double[] pi = cplex.getDuals(stng);
              for (int i = 0; i < stng.length; i++)
                pi[i] = -pi[i];

                //obtain q and u

              for(int i=0;i<N;i++){
                qq[i] = new double[q[t+1][i].length];
                for(int j=0;j<q[t+1][i].length;j++){
                  qq[i][j] = q[t+1][i][j];
                }
              }

              k = 0;
              for (int i = 1; i <= N; i++) {
                for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                  qq[i - 1][j] += pi[k];
                  if (pi[k] < -0.001)
                    out.println("pi[" + k + "] is less than 0:\t" + pi[k]);
                  KStar += pi[k] * u[t + 1][i - 1][j];
                  k++;
                }
              }

              for (int i = 1; i <= N; i++) {
                String nodeKey = "N" + (t * (N + 1) + i);
                ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf(nodeKey);
                BinaryHeap heap = new BinaryHeap();
                int kk = 0;
                while (arc != null) {
                  arc.cap_rest = arc.cap_up;
                  int ii = Integer.parseInt( (arc.getToNodeKey()).substring(1)); // index of toNodeKey
                  ii = ii - (t + 1) * (N + 1);
                  if (ii > 0) {
                    for (int j = 0; j < q[t + 1][ii - 1].length; j++) {
                      HeapArc heaparc = new HeapArc(arc.getKey() + "_" + j,
                          arc.cost + qq[ii - 1][j]);
                      heaparc.cap = Math.min(arc.cap_rest, u[t + 1][ii - 1][j]);
                      arc.cap_rest -= heaparc.cap;
                      heap.insert(heaparc);
                      kk++;
                      if (arc.cap_rest <= 0)
                        break;
                    }
                  }
                  arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
                }
                double[] temp_q = new double[kk];
                double[] temp_u = new double[kk];
                k = 0;
                for (int j = 0; j < temp_q.length; j++) {
                  if (heap.isEmpty()) {
                    break;
                  }
                  else {
                    HeapArc heaparc = (HeapArc) heap.deleteMin();
                    if (k == 0) {
                      temp_q[k] = heaparc.value;
                      temp_u[k] = heaparc.cap;
                      k++;
                    }
                    else {
                      if (temp_u[k - 1] >= MaxNum)
                        break;
                      if (heaparc.value <= temp_q[k - 1]) {
                        temp_u[k - 1] += heaparc.cap;
                      }
                      else {
                        temp_q[k] = heaparc.value;
                        temp_u[k] = heaparc.cap;
                        k++;
                      }
                    }
                  }
                }

                q[t][i - 1] = new double[k];
                u[t][i - 1] = new double[k];
                if(debug) debugOut.print(i);
                for (int j = 0; j < k; j++) {
                  q[t][i - 1][j] = temp_q[j];
                  u[t][i - 1][j] = temp_u[j];
                  if(debug) debugOut.print("\t"+temp_q[j]+"\t"+temp_u[j]);
                }
                if(debug) debugOut.println();
              }
            }
            cplex.clearModel();

            t = t-1;

            if(debug) debugOut.println("t=t-1");
            if(debug) debugOut.println("Obtain x2");
            // botian x2
          numArc = 0;
          numYarc = 0; // number of y
          for (int i = 0; i <= N; i++) {
            numArc += myNetwork.getNumOutArcOf("N" + (t * (N + 1) + i));
            if (i > 0)
              numYarc += q[t + 1][i - 1].length;
          }

            x = new IloNumVar[numArc];
            y = new IloNumVar[numYarc];
            obj_exp = cplex.linearNumExpr();
            k = 0;
            indexTable = new Hashtable();
            for (int i = 0; i <= N; i++) {
              ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
                  (t * (N + 1) + i));
              while (arc != null) {
                x[k] = cplex.numVar(arc.cap_low, arc.cap_up, "x" + k);
                obj_exp.addTerm(arc.cost, x[k]);
                indexTable.put(arc.getKey(), new Integer(k));
                k++;
                arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
              }
            }
            k = 0;
            for (int i = 1; i <= N; i++) {
              for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                y[k] = cplex.numVar(0, MaxNum + 1, "y" + k);
                obj_exp.addTerm(q[t + 1][i - 1][j], y[k]);
                k++;
              }
            }
            cplex.addMinimize(obj_exp);
            rng = new IloRange[2 * N];
            stng = new IloRange[numYarc];
            k = 0;
            for (int i = 1; i <= N; i++) {
              IloLinearNumExpr exp = cplex.linearNumExpr();
              ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
                  (t * (N + 1) + i));
              while (arc != null) {
                int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
                exp.addTerm(1, x[j]);
                arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
              }
              rng[i - 1] = cplex.addEq(exp, supply[t][i], "N" + (t * (N + 1) + i));
            }

            k = 0;
            for (int i = 1; i <= N; i++) {
              IloLinearNumExpr exp = cplex.linearNumExpr();
              ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
                  ( (t + 1) * (N + 1) + i));
              while (arc != null) {
                int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
                exp.addTerm(1, x[j]);
                arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
              }
              for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                exp.addTerm( -1, y[k]);
                k++;
              }
              rng[N + i -
                  1] = cplex.addEq(exp, 0, "N" + ( (t + 1) * (N + 1) + i));
            }

            //add y constraint
            k = 0;
            for (int i = 1; i <= N; i++) {
              for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                IloLinearNumExpr exp = cplex.linearNumExpr();
                exp.addTerm(1, y[k]);
                stng[k] = cplex.addLe(exp, u[t + 1][i - 1][j],
                                      "Y" + t + "_" + i + "_" + j);
                k++;
              }
            }
            if (cplex.solve()) {
              for (int i = 0; i <= N; i++) {
                ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
                    ( (t + 1) * (N + 1) + i));
                supply[t + 1][i ] = 0;
                while (arc != null) {
                  int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
                  supply[t + 1][i] += cplex.getValue(x[j]);
                  arc.pre_flow = arc.flow;
                  arc.flow = cplex.getValue(x[j]);
                  if(debug) debugOut.print(arc.getKey()+"\t"+arc.flow+"\t");
                  arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
                }
                //if(debug) debugOut.println();
                if(debug) debugOut.println("supply "+i+":\t"+supply[t + 1][i ]);
              }
            }
            cplex.clearModel();
            if(debug) debugOut.println("end of obtaining x2.");

            t = t+1;

            if(debug) debugOut.println("t=t+1");

          for(int ite2=1;ite2<=5;ite2++){
            if(debug) debugOut.println("ite2=:\t "+ite2);
            //update q and u for the new supply
            if(debug) debugOut.println("update q and u for the new supply:");
            numArc = 0;
            numYarc = 0; // number of y
            for (int i = 0; i <= N; i++) {
              numArc += myNetwork.getNumOutArcOf("N" + (t * (N + 1) + i));
              if (i > 0)
                numYarc += q[t + 1][i - 1].length;
            }
            x = new IloNumVar[numArc];
            y = new IloNumVar[numYarc];
            obj_exp = cplex.linearNumExpr();
            k = 0;
            indexTable = new Hashtable();
            for (int i = 0; i <= N; i++) {
              ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
                  (t * (N + 1) + i));
              while (arc != null) {
                x[k] = cplex.numVar(arc.cap_low, arc.cap_up, "x" + k);
                obj_exp.addTerm(arc.cost, x[k]);
                indexTable.put(arc.getKey(), new Integer(k));
                k++;
                arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
              }
            }
            k = 0;
            for (int i = 1; i <= N; i++) {
              for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                y[k] = cplex.numVar(0, MaxNum + 1, "y" + k);
                obj_exp.addTerm(q[t + 1][i - 1][j], y[k]);
                k++;
              }
            }
            cplex.addMinimize(obj_exp);
            rng = new IloRange[2 * N];
            stng = new IloRange[numYarc];
            k = 0;
            for (int i = 1; i <= N; i++) {
              IloLinearNumExpr exp = cplex.linearNumExpr();
              ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
                  (t * (N + 1) + i));
              while (arc != null) {
                int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
                exp.addTerm(1, x[j]);
                arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
              }
              rng[i - 1] = cplex.addEq(exp, supply[t][i ], "N" + (t * (N + 1) + i));
            }

            k = 0;
            for (int i = 1; i <= N; i++) {
              IloLinearNumExpr exp = cplex.linearNumExpr();
              ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
                  ( (t + 1) * (N + 1) + i));
              while (arc != null) {
                int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
                exp.addTerm(1, x[j]);
                arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
              }
              for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                exp.addTerm( -1, y[k]);
                k++;
              }
              rng[N + i - 1] = cplex.addEq(exp, 0, "N" + ( (t + 1) * (N + 1) + i));
            }

            //add y constraint
            k = 0;
            for (int i = 1; i <= N; i++) {
              for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                IloLinearNumExpr exp = cplex.linearNumExpr();
                exp.addTerm(1, y[k]);
                stng[k] = cplex.addLe(exp, u[t + 1][i - 1][j],
                                      "Y" + t + "_" + i + "_" + j);
                k++;
              }
            }

            //cplex.exportModel(".\\data\\gradientMethod_" + t + ".lp");
            double[][] qqq = new double[N][];
            double[][] uuu = new double[N][];
            if (cplex.solve()) {

              obj = cplex.getObjValue() - KStar;
              double[][] qq = new double[N][]; // store the q tempory
              double[] pi = cplex.getDuals(stng);
              for (int i = 0; i < stng.length; i++)
                pi[i] = -pi[i];

                //obtain q and u

              for(int i=0;i<N;i++){
                qq[i] = new double[q[t+1][i].length];
                for(int j=0;j<q[t+1][i].length;j++){
                  qq[i][j] = q[t+1][i][j];
                }
              }

              k = 0;
              for (int i = 1; i <= N; i++) {
                for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                  qq[i - 1][j] += pi[k];
                  if (pi[k] < -0.001)
                    out.println("pi[" + k + "] is less than 0:\t" + pi[k]);
                  KStar += pi[k] * u[t + 1][i - 1][j];
                  k++;
                }
              }

              for (int i = 1; i <= N; i++) {
                String nodeKey = "N" + (t * (N + 1) + i);
                ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf(nodeKey);
                BinaryHeap heap = new BinaryHeap();
                int kk = 0;
                while (arc != null) {
                  arc.cap_rest = arc.cap_up;
                  int ii = Integer.parseInt( (arc.getToNodeKey()).substring(1)); // index of toNodeKey
                  ii = ii - (t + 1) * (N + 1);
                  if (ii > 0) {
                    for (int j = 0; j < q[t + 1][ii - 1].length; j++) {
                      HeapArc heaparc = new HeapArc(arc.getKey() + "_" + j,
                          arc.cost + qq[ii - 1][j]);
                      heaparc.cap = Math.min(arc.cap_rest, u[t + 1][ii - 1][j]);
                      arc.cap_rest -= heaparc.cap;
                      heap.insert(heaparc);
                      kk++;
                      if (arc.cap_rest <= 0)
                        break;
                    }
                  }
                  arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
                }
                double[] temp_q = new double[kk];
                double[] temp_u = new double[kk];
                k = 0;
                for (int j = 0; j < temp_q.length; j++) {
                  if (heap.isEmpty()) {
                    break;
                  }
                  else {
                    HeapArc heaparc = (HeapArc) heap.deleteMin();
                    if (k == 0) {
                      temp_q[k] = heaparc.value;
                      temp_u[k] = heaparc.cap;
                      k++;
                    }
                    else {
                      if (temp_u[k - 1] >= MaxNum)
                        break;
                      if (heaparc.value <= temp_q[k - 1]) {
                        temp_u[k - 1] += heaparc.cap;
                      }
                      else {
                        temp_q[k] = heaparc.value;
                        temp_u[k] = heaparc.cap;
                        k++;
                      }
                    }
                  }
                }

                qqq[i-1] = new double[q[t][i-1].length];
                uuu[i-1] = new double[u[t][i-1].length];
                for (int j = 0; j < q[t][i-1].length; j++) {
                  qqq[i-1][j] = q[t][i-1][j];
                  uuu[i-1][j] = u[t][i-1][j];
                }

                q[t][i - 1] = new double[k];
                u[t][i - 1] = new double[k];
                if(debug) debugOut.print(i);
                for (int j = 0; j < k; j++) {
                  q[t][i - 1][j] = temp_q[j];
                  u[t][i - 1][j] = temp_u[j];
                  if(debug) debugOut.print("\t"+temp_q[j]+"\t"+temp_u[j]);
                }
                if(debug) debugOut.println();
              }
            }
            cplex.clearModel();

            // get G2_x2
            double G2_x2 =0;
            for(int i=0;i<=N;i++){
              ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) +i));
              while(arc != null){
                G2_x2 += arc.flow * arc.cost;
                arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
              }
              if(i > 0){
                double u_break=0;
                for(int j=0;j<q[t][i-1].length;j++){
                  u_break += u[t][i-1][j];
                  if(u_break <= supply[t][i]){
                    G2_x2 += q[t][i-1][j] * u[t][i-1][j];
                  }
                  else {
                    G2_x2 += (supply[t][i] - u_break + u[t][i-1][j])* q[t][i-1][j];
                    break;
                  }
                }
              }
            }
            if(debug) debugOut.println("G2_x2 =:\t"+G2_x2);
              //bet G2_x1
            double G2_x1 =0;
            for(int i=0;i<=N;i++){
              ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1)+i ));
              double sp =0;
              while(arc != null){
                G2_x1 += arc.pre_flow * arc.cost;
                sp += arc.pre_flow;
                arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
              }
              if(i > 0){
                double u_break=0;
                for(int j=0;j<q[t][i-1].length;j++){
                  u_break += u[t][i-1][j];
                  if(u_break <= sp){
                    G2_x1 += q[t][i-1][j] * u[t][i-1][j];
                  }
                  else {
                    G2_x1 += (sp - u_break + u[t][i-1][j])* q[t][i-1][j];
                    break;
                  }
                }
              }
            }
            //obtained G2_x1
            if(debug) debugOut.println("G2_x2 =:\t"+G2_x1);

            if(G2_x1 == G2_x2){
              if(debug) debugOut.println("G2_x1 == G2_x2, find optimal solution.");
                break;
            }
            else if (G2_x1 < G2_x2) {
              double alpha = binarySearch(myNetwork, t, N, q[t], u[t], qqq, uuu);

            }
            else {
              //Find x3 that minimize G2(x)
              t = t - 1;
              for (int i = 0; i <= N; i++) {
                ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
                    (t * (N + 1) + i));
                while (arc != null) {
                  x[k] = cplex.numVar(arc.cap_low, arc.cap_up, "x" + k);
                  obj_exp.addTerm(arc.cost, x[k]);
                  indexTable.put(arc.getKey(), new Integer(k));
                  k++;
                  arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
                }
              }
              k = 0;
              for (int i = 1; i <= N; i++) {
                for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                  y[k] = cplex.numVar(0, MaxNum + 1, "y" + k);
                  obj_exp.addTerm(q[t + 1][i - 1][j], y[k]);
                  k++;
                }
              }
              cplex.addMinimize(obj_exp);
              rng = new IloRange[2 * N];
              stng = new IloRange[numYarc];
              k = 0;
              for (int i = 1; i <= N; i++) {
                IloLinearNumExpr exp = cplex.linearNumExpr();
                ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" +
                    (t * (N + 1) + i));
                while (arc != null) {
                  int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
                  exp.addTerm(1, x[j]);
                  arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
                }
                rng[i -
                    1] = cplex.addEq(exp, supply[t][i], "N" + (t * (N + 1) + i));
              }

              k = 0;
              for (int i = 1; i <= N; i++) {
                IloLinearNumExpr exp = cplex.linearNumExpr();
                ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
                    ( (t + 1) * (N + 1) + i));
                while (arc != null) {
                  int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
                  exp.addTerm(1, x[j]);
                  arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
                }
                for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                  exp.addTerm( -1, y[k]);
                  k++;
                }
                rng[N + i -
                    1] = cplex.addEq(exp, 0, "N" + ( (t + 1) * (N + 1) + i));
              }

              //add y constraint
              k = 0;
              for (int i = 1; i <= N; i++) {
                for (int j = 0; j < q[t + 1][i - 1].length; j++) {
                  IloLinearNumExpr exp = cplex.linearNumExpr();
                  exp.addTerm(1, y[k]);
                  stng[k] = cplex.addLe(exp, u[t + 1][i - 1][j],
                                        "Y" + t + "_" + i + "_" + j);
                  k++;
                }
              }
              if (cplex.solve()) {
                for (int i = 0; i <= N; i++) {
                  ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
                      ( (t + 1) * (N + 1) + i));
                  supply[t + 1][i] = 0;
                  while (arc != null) {
                    int j = ( (Integer) indexTable.get(arc.getKey())).intValue();
                    supply[t + 1][i] += cplex.getValue(x[j]);
                    arc.flow = cplex.getValue(x[j]);
                    arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
                  }
                }
              }
              cplex.clearModel();

              //get G2_x3, G1_x3
              double G2_x3 = 0, G1_x3 = 0;
              t = t + 1;
              for (int i = 0; i <= N; i++) {
                ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
                    (t * (N + 1)));
                while (arc != null) {
                  G2_x3 += arc.flow * arc.cost;
                  G1_x3 += arc.flow * arc.cost;
                  arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
                }
                if (i > 0) {
                  double u_break = 0;
                  for (int j = 0; j < q[t][i - 1].length; j++) {
                    u_break += u[t][i - 1][j];
                    if (u_break <= supply[t][i]) {
                      G2_x3 += q[t][i - 1][j] * u[t][i - 1][j];
                    }
                    else {
                      G2_x3 += (supply[t][i] - u_break + u[t][i - 1][j]) *
                          q[t][i - 1][j];
                      break;
                    }
                  }
                  u_break = 0;
                  for (int j = 0; j < qqq[i - 1].length; j++) {
                    u_break += uuu[i - 1][j];
                    if (u_break <= supply[t][i]) {
                      G1_x3 += qqq[i - 1][j] * uuu[i - 1][j];
                    }
                    else {
                      G1_x3 += (supply[t][i] - u_break + uuu[i - 1][j]) *
                          qqq[i - 1][j];
                      break;
                    }
                  }

                }
              }

              if (G2_x3 <= G1_x3) {
                double alpha = binarySearch(myNetwork, t, N, q[t], u[t], qqq,
                                            uuu);
              }
              else {
                for (int i = 0; i <= N; i++) {
                  ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +
                      (t * (N + 1)));
                  supply[t][i] = 0;
                  while (arc != null) {
                    arc.pre_flow = arc.flow;
                    supply[t][i] += arc.flow;
                    arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
                  }
                }
              }

            }



          }
          t=t-1;
        } // end of update supply
      }
      long end_time = System.currentTimeMillis();
      out.println(max_obj+"\t"+(end_time-start_time)/1000.0);
      cplex.end();
      out.close();
      outfile.close();
    }
    catch (Exception e) {
       System.err.println("Concert exception '" + e + "' caught");
    }

    return max_obj;

  }

  public void binarySearch(Network myNetwork,int t, int N, double[][] q,double[][]u,double[][] qq,double[][] uu){
    double G1_x1=0,G1_x2=0,G2_x1=0,G2_x2=0,G1_xm=0,G2_xm=0;
    for(int i=0;i<=N;i++){
      ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) +i));
      double sp1=0,sp2=0,sp3=0;
      while(arc != null){
        G1_x2 += arc.flow * arc.cost;
        G2_x2 += arc.flow * arc.cost;
        G1_x1 += arc.pre_flow * arc.cost;
        G2_x1 += arc.pre_flow * arc.cost;
        G1_xm += (arc.flow + arc.pre_flow) * arc.cost /2;
        G2_xm += (arc.flow + arc.pre_flow) * arc.cost /2;
        sp1 += arc.pre_flow;
        sp2 += arc.flow;
        sp3 += (arc.flow + arc.pre_flow)/2;
        arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
      }
      if(i > 0){
        double u_break=0;
        for(int j=0;j<q[i-1].length;j++){
          u_break += u[i-1][j];
          if(u_break <= sp1){
            G2_x1 += q[i-1][j] * u[i-1][j];
          }
          else {
            G2_x1 += (sp1 - u_break + u[i-1][j])* q[i-1][j];
            break;
          }
        }
        u_break=0;
        for(int j=0;j<q[i-1].length;j++){
          u_break += u[i-1][j];
          if(u_break <= sp2){
            G2_x2 += q[i-1][j] * u[i-1][j];
          }
          else {
            G2_x2 += (sp2 - u_break + u[i-1][j])* q[i-1][j];
            break;
          }
        }
        u_break=0;
        for(int j=0;j<q[i-1].length;j++){
          u_break += u[i-1][j];
          if(u_break <= sp3){
            G2_xm += q[i-1][j] * u[i-1][j];
          }
          else {
            G2_xm += (sp3 - u_break + u[i-1][j])* q[i-1][j];
            break;
          }
        }

        u_break=0;
        for(int j=0;j<qq[i-1].length;j++){
          u_break += uu[i-1][j];
          if(u_break <= sp1){
            G1_x1 += qq[i-1][j] * uu[i-1][j];
          }
          else {
            G1_x1 += (sp1 - u_break + uu[i-1][j])* qq[i-1][j];
            break;
          }
        }
        u_break=0;
        for(int j=0;j<qq[i-1].length;j++){
          u_break += uu[i-1][j];
          if(u_break <= sp2){
            G1_x2 += qq[i-1][j] * uu[i-1][j];
          }
          else {
            G1_x2 += (sp2 - u_break + uu[i-1][j])* qq[i-1][j];
            break;
          }
        }
        u_break=0;
        for(int j=0;j<qq[i-1].length;j++){
          u_break += uu[i-1][j];
          if(u_break <= sp3){
            G1_xm += qq[i-1][j] * uu[i-1][j];
          }
          else {
            G1_xm += (sp3 - u_break + uu[i-1][j])* qq[i-1][j];
            break;
          }
        }

      }
    }
    debugOut.println("G1_x1="+G1_x1+",G1_x2="+G1_x2+",G2_x1="+G2_x1+",G2_x2="+G2_x2+",G2_xm="+G1_xm+",G2_xm="+G2_xm);
    if(Math.abs(G1_xm-G2_xm) <= 1 || Math.abs(G1_x1- G1_x2 + G2_x1- G2_x2 + G1_xm-G2_xm) <=5) {
      for(int i=0;i<=N;i++){
        ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +(t * (N + 1) + i));
        while(arc != null){
          arc.pre_flow = (arc.flow + arc.pre_flow)/2;
          arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
        }
      }
      return ;
    }
    else{
      if((G1_x1- G2_x1)*(G1_xm- G2_xm) > 0 && (G2_x2- G1_x2) *(G2_xm- G1_xm) <0){
        for(int i=0;i<=N;i++){
          ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +(t * (N + 1) + i));
          while(arc != null){
            arc.pre_flow = (arc.flow + arc.pre_flow)/2;
            arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
          }
        }
        binarySearch(myNetwork,t, N, q,u,qq,uu);
      }
      else if ((G1_x1- G2_x1)*(G1_xm- G2_xm) < 0 && (G2_x2- G1_x2) *(G2_xm- G1_xm) >0){
        for(int i=0;i<=N;i++){
          ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" +(t * (N + 1) + i));
          while(arc != null){
            arc.flow = (arc.flow + arc.pre_flow)/2;
            arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
          }
        }
        binarySearch(myNetwork,t, N, q,u,qq,uu);

      }
      else{
        debugOut.println("Error in binary search");
      }

    }

  }

  public static double precision(double x){
    double p = 100;
    return ((int) (x * p +0.5))/p;
  }


}