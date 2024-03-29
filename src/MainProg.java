import MyNetwork.*;
import ilog.concert.*;
import DataStructures.BinaryHeap;
import java.util.Hashtable;
import java.io.FileOutputStream;
import java.io.PrintStream;
import SnNetwork.*;
import java.util.Vector;
import ilog.cplex.IloCplex;

public class MainProg {
	double MaxNum = 1000;
	boolean debug = false;
	PrintStream debugOut;
	public MainProg() {
	}

	public static void main(String[] args) throws Exception {
		MainProg mainProg1 = new MainProg();
		String solFile = ".\\data\\sol_whole.txt";
		String debugOutFile = ".\\data\\debugLog.txt";
		FileOutputStream outfile = new FileOutputStream(solFile, true);
		PrintStream out = new PrintStream(outfile);
		mainProg1.debugOut = new PrintStream(new FileOutputStream(debugOutFile));
//		mainProg1.debugOut.print("\n" + "index" + "\t" + "meanobj" + "\t" + "meanTime" + "\t" + "SCAMmax_sol" + "\t"
//				+ "SCAMbest_sol" + "\t" + "SCAMTime" + "\t" + "gradientmax_sol" + "\t" + "gradientbest_sol" + "\t"
//				+ "gradientTime" + "\t" + "newmax_sol" + "\t" + "newbest_sol" + "\t" + "newTime" + "\t"
//				+ "improvemax_sol" + "\t" + "improvebest_sol" + "\t" + "improveTime" + "\t" + "improve2max_sol" + "\t"
//				+ "improve2best_sol" + "\t" + "improve2Time" + "\t");
		mainProg1.debugOut.print("\n" + "index" + "\t" + "meanobj" + "\t" + "meanTime" + "\t" + "SCAMmax_sol" + "\t"
				+ "SCAMbest_sol" + "\t" + "SCAMTime" + "\t" + "gradientmax_sol" + "\t" + "gradientbest_sol" + "\t"
				+ "gradientTime" + "\t" + "newmax_sol" + "\t" + "newbest_sol" + "\t" + "newTime" + "\t"
				+ "improvemax_sol" + "\t" + "improvebest_sol" + "\t" + "improveTime");
		double obj = 0;
		out.println();
		int ITE = 5, ITE2 = 50;
		for (int n = 1; n <= 30; n++) {
			Network myNetwork = new Network();
			String netFile = ".\\netInfo.txt";
			String nodeFile = ".\\data\\nodeFile_" + n + ".txt";
			// String nodeFile = ".\\data\\node_exp" + n + ".txt";
			String arcFile = ".\\data\\arcFile_" + n + ".txt";
			// String arcFile = ".\\data\\arc_exp" + n + ".txt";
			String outFile = ".\\data\\sol_" + n + ".txt";
			System.out.println("Prob" + n);

			myNetwork.buildNetworkFrom(netFile, nodeFile, arcFile);
			out.print(n);
			mainProg1.debugOut.print("\n" + n + "\t");
			long st = System.currentTimeMillis();
			obj = mainProg1.meanMethod(myNetwork, outFile);
			long et = System.currentTimeMillis();
			double time1 = (et - st) / 1000.0;
			out.print("\t" + precision(obj));
			System.gc();
			Thread.sleep(1000);

			st = System.currentTimeMillis();
			obj = mainProg1.scapMethod(myNetwork, ITE, outFile);
			et = System.currentTimeMillis();
			double time2 = (et - st) / 1000.0;
			out.print("\t" + precision(obj));
			System.gc();
			Thread.sleep(1000);

			// mainProg1.meanMethod(myNetwork, ".\\temp.txt");
			st = System.currentTimeMillis();
			obj = mainProg1.gradientMethod(myNetwork, ITE, ITE2, outFile);
			et = System.currentTimeMillis();
			double time3 = (et - st) / 1000.0;
			out.print("\t" + precision(obj));
			System.gc();
			Thread.sleep(1000);

			// mainProg1.meanMethod(myNetwork, ".\\temp.txt");
			st = System.currentTimeMillis();
			obj = mainProg1.newMethod(myNetwork, ITE, ITE2, outFile);
			et = System.currentTimeMillis();
			double time4 = (et - st) / 1000.0;
			out.print("\t" + precision(obj));
			System.gc();
			Thread.sleep(1000);

			// mainProg1.meanMethod(myNetwork, ".\\temp.txt");
			st = System.currentTimeMillis();
			obj = mainProg1.improveMethod(myNetwork, ITE, ITE2, outFile);
			et = System.currentTimeMillis();
			double time5 = (et - st) / 1000.0;
			out.print("\t" + precision(obj));
			System.gc();
			Thread.sleep(1000);

			// mainProg1.meanMethod(myNetwork, ".\\temp.txt");
//			st = System.currentTimeMillis();
//			obj = mainProg1.improveMethod2(myNetwork, ITE, ITE2, outFile);
//			et = System.currentTimeMillis();
//			double time6 = (et - st) / 1000.0;
//			out.print("\t" + precision(obj));
//			out.println("\t" + precision(time1) + "\t" + precision(time2) + "\t" + precision(time3) + "\t"
//					+ precision(time4) + "\t" + precision(time5) + "\t" + precision(time6));

			out.println("\t" + precision(time1) + "\t" + precision(time2) + "\t" + precision(time3) + "\t"
					+ precision(time4) + "\t" + precision(time5));
			myNetwork = null;
			System.gc();
			Thread.sleep(1000);

		}
		out.close();
		outfile.close();
	}

	public double scapMethod(Network myNetwork, int ITE, String outFile) {
		if (debug)
			debugOut.println("\n\nIterations of scapMethod:");
		double obj = Double.NEGATIVE_INFINITY;
		double max_obj = Double.NEGATIVE_INFINITY;
		double best_sol = Double.MAX_VALUE;
		long start_time = System.currentTimeMillis();
		int N = myNetwork.N;
		int T = myNetwork.T;
		double[][][] q = new double[T][N][]; // slop
		double[][][] u = new double[T][N][]; // upper bound
		double[][] supply = new double[T][N + 1]; // upper bound
		double[] KStar = new double[T];
		if (debug)
			debugOut.println("initial supply");
		for (int t = 0; t < T; t++) {
			for (int i = 0; i <= N; i++) {
				supply[t][i] = ((NodeInfo) myNetwork.getNode("N" + i)).supply;
				// supply[t][i] = ( (NodeInfo) myNetwork.getNode("N" + (t*(N+1)+i))).supply;
				if (debug)
					debugOut.print("\tsupply_" + t + "_" + i + ":" + supply[t][i]);
			}
			if (debug)
				debugOut.println();
		}

		try {
			IloCplex cplex = new IloCplex();
			cplex.setParam(IloCplex.IntParam.SimDisplay, 0);
			// 0 No iteration messages until solution
			// 1 Iteration info after each refactoring
			// 2 Iteration info for each iteration

			FileOutputStream outfile = new FileOutputStream(outFile, true);
			PrintStream out = new PrintStream(outfile);
			out.println("scap");
			// obtain q and u at the last stage
			if (debug)
				debugOut.println("obtain q and u at the last stage:");
			updateLastStageQandU(myNetwork, q[T - 1], u[T - 1]);

			// end of obtaining q and u at the stage T-1
			double[][] qq = new double[N][];
			double[][] uu = new double[N][]; // store the q and u value at the last stage
			for (int i = 0; i < N; i++) {
				qq[i] = new double[q[T - 1][i].length];
				uu[i] = new double[q[T - 1][i].length];
				for (int j = 0; j < q[T - 1][i].length; j++) {
					qq[i][j] = q[T - 1][i][j];
					uu[i][j] = u[T - 1][i][j];
				}
			}
			KStar[T - 1] = 0;
			for (int ite = 1; ite <= ITE; ite++) {
				if (debug)
					debugOut.println("ite=:\t" + ite);
				// restore the q and u value at the last stage
				for (int i = 0; i < N; i++) {
					for (int j = 0; j < q[T - 1][i].length; j++) {
						q[T - 1][i][j] = qq[i][j];
						u[T - 1][i][j] = uu[i][j];
					}
				}

				for (int t = T - 2; t >= 0; t--) {
					obj = updateQandU(myNetwork, cplex, t, KStar, q[t + 1], u[t + 1], supply[t], q[t], u[t]);
				}

				// out.println("Ite:\t"+ite+"\tobj:\t"+obj);
				if (debug)
					debugOut.println("obj:\t" + obj);
				out.println(obj);
				if (max_obj < obj)
					max_obj = obj;
				// update supply
				if (debug)
					debugOut.println("update supply:");
				double temp_sol = 0;
				for (int t = 0; t <= T - 2; t++) {
					temp_sol += updateSupply(myNetwork, cplex, t, supply[t], supply[t + 1], q[t + 1], u[t + 1]);
				} // end of update supply
					// get the cost at the last stage
				for (int i = 1; i <= N; i++) {
					double u_break = 0;
					for (int j = 0; j < qq[i - 1].length; j++) {
						u_break += uu[i - 1][j];
						if (u_break <= supply[T - 1][i]) {
							temp_sol += qq[i - 1][j] * uu[i - 1][j];
						} else {
							temp_sol += (supply[T - 1][i] - u_break + uu[i - 1][j]) * qq[i - 1][j];
							break;
						}
					}
				}
				if (debug)
					debugOut.println("solution value:\t " + temp_sol);
				if (best_sol > temp_sol)
					best_sol = temp_sol;

			}
			long end_time = System.currentTimeMillis();
			out.println(max_obj + "\t" + best_sol + "\t" + (end_time - start_time) / 1000.0);
			cplex.end();
			out.close();
			outfile.close();
			debugOut.print(max_obj + "\t" + best_sol + "\t" + (end_time - start_time) / 1000.0 + "\t");
		} catch (Exception e) {
			System.err.println("scapMethod exception '" + e + "' caught");
		}

		return max_obj;
	}

// Gradient method
	public double gradientMethod(Network myNetwork, int ITE, int ITE2, String outFile) {
		if (debug)
			debugOut.println("\n\n Iterations of gradientMethod:");
		double obj = Double.NEGATIVE_INFINITY;
		double max_obj = Double.NEGATIVE_INFINITY;
		double best_sol = Double.MAX_VALUE;
		long start_time = System.currentTimeMillis();
		int N = myNetwork.N;
		int T = myNetwork.T;
		// if (T <= 3) ITE = 2;
		double[][][] q = new double[T][N][]; // slop
		double[][][] u = new double[T][N][]; // upper bound
		double[][] supply = new double[T][N + 1]; // upper bound
		double[] KStar = new double[T];
		if (debug)
			debugOut.println("initial supply");
		for (int t = 0; t < T; t++) {
			for (int i = 0; i <= N; i++) {
				supply[t][i] = ((NodeInfo) myNetwork.getNode("N" + i)).supply;
				// supply[t][i] = ( (NodeInfo) myNetwork.getNode("N" + (t*(N+1)+i))).supply;
				if (debug)
					debugOut.print("\tsupply_" + t + "_" + i + ":" + supply[t][i]);
			}
			if (debug)
				debugOut.println();
		}

		try {
			IloCplex cplex = new IloCplex();
			cplex.setParam(IloCplex.IntParam.SimDisplay, 0); // 0 No iteration messages until solution
			// 1 Iteration info after each refactoring
			// 2 Iteration info for each iteration

			FileOutputStream outfile = new FileOutputStream(outFile, true);
			PrintStream out = new PrintStream(outfile);
			out.println("Gradient");
			// obtain q and u at the last stage
			if (debug)
				debugOut.println("obtain q and u at the last stage:");
			updateLastStageQandU(myNetwork, q[T - 1], u[T - 1]);
			// end of obtaining q and u at the stage T-1
			KStar[T - 1] = 0;
			for (int ite = 1; ite <= ITE; ite++) {
				if (debug)
					debugOut.println("ite=:\t" + ite);
				for (int t = T - 2; t >= 0; t--) {
					obj = updateQandU(myNetwork, cplex, t, KStar, q[t + 1], u[t + 1], supply[t], q[t], u[t]);
				}

				// out.println("Ite:\t"+ite+"\tobj:\t"+obj);
				if (debug)
					debugOut.println("obj:\t" + obj);
				out.println(obj);
				if (max_obj < obj)
					max_obj = obj;
				// update supply
				if (debug)
					debugOut.println("update supply:");
				double temp_sol = 0;
				double temp_sol2 = 0;
				for (int t = 0; t <= T - 2; t++) {
					temp_sol2 += updateSupply(myNetwork, cplex, t, supply[t], supply[t + 1], q[t + 1], u[t + 1]);
				} // end of update supply
					// get the cost at the last stage
				for (int i = 1; i <= N; i++) {
					double u_break = 0;
					for (int j = 0; j < q[T - 1][i - 1].length; j++) {
						u_break += u[T - 1][i - 1][j];
						if (u_break <= supply[T - 1][i]) {
							temp_sol2 += q[T - 1][i - 1][j] * u[T - 1][i - 1][j];
						} else {
							temp_sol2 += (supply[T - 1][i] - u_break + u[T - 1][i - 1][j]) * q[T - 1][i - 1][j];
							break;
						}
					}
				}

				for (int t = 0; t <= T - 3; t++) {
					updateSupply(myNetwork, cplex, t, supply[t], supply[t + 1], q[t + 1], u[t + 1]);
					double temp_temp_sol = 0;
					t = t + 1;
					for (int ite2 = 1; ite2 <= ITE2; ite2++) {
						if (debug)
							debugOut.println("ite2=:\t " + ite2);
						temp_temp_sol = 0;
						// update q and u for the new supply
						if (debug)
							debugOut.println("update q and u for the new supply:");
						updateQandU(myNetwork, cplex, t, KStar, q[t + 1], u[t + 1], supply[t], q[t], u[t]);

						// find gradient
						if (debug)
							debugOut.println("find gradient:");

						temp_temp_sol = findGradient2(myNetwork, t, supply[t], q[t], u[t]);

					}
					t = t - 1;
					temp_sol += temp_temp_sol;
				} // end of update supply

				if (debug)
					debugOut.println("end of update supply.");

				// get the cost at the last two stages
				temp_sol += updateSupply(myNetwork, cplex, T - 2, supply[T - 2], supply[T - 1], q[T - 1], u[T - 1]);

// get the cost of the last stage

				for (int i = 0; i < N; i++) {
					double u_break = 0;
					for (int j = 0; j < q[T - 1][i].length; j++) {
						u_break += u[T - 1][i][j];
						if (u_break <= supply[T - 1][i + 1]) {
							temp_sol += q[T - 1][i][j] * u[T - 1][i][j];
						} else {
							temp_sol += (supply[T - 1][i + 1] - u_break + u[T - 1][i][j]) * q[T - 1][i][j];
							break;
						}
					}
				}
				if (debug)
					debugOut.println("solution value:\t " + temp_sol);
				if (best_sol > temp_sol)
					best_sol = temp_sol;
				if (best_sol > temp_sol2)
					best_sol = temp_sol2;
			}
			long end_time = System.currentTimeMillis();
			out.println(max_obj + "\t" + best_sol + "\t" + (end_time - start_time) / 1000.0);
			cplex.end();
			out.close();
			outfile.close();
			debugOut.print(max_obj + "\t" + best_sol + "\t" + (end_time - start_time) / 1000.0 + "\t");
		} catch (Exception e) {
			System.err.println("gradientMethod exception '" + e + "' caught");
		}

		return max_obj;
	}

	public double improveMethod(Network myNetwork, int ITE, int ITE2, String outFile) {
		if (debug)
			debugOut.println("\n\n Iterations of improveMethod:");
		double obj = Double.NEGATIVE_INFINITY;
		double max_obj = Double.NEGATIVE_INFINITY;
		double best_sol = Double.MAX_VALUE;
		long start_time = System.currentTimeMillis();
		int N = myNetwork.N;
		int T = myNetwork.T;
		if (T <= 3)
			ITE = 1;
		double[][][] q = new double[T][N][]; // slop
		double[][][] u = new double[T][N][]; // upper bound
		double[][] supply = new double[T][N + 1]; // upper bound
		double[] KStar = new double[T];
		if (debug)
			debugOut.println("initial supply");
		for (int t = 0; t < T; t++) {
			for (int i = 0; i <= N; i++) {
				supply[t][i] = ((NodeInfo) myNetwork.getNode("N" + i)).supply;
				// supply[t][i] = ( (NodeInfo) myNetwork.getNode("N" + (t*(N+1)+i))).supply;
				if (debug)
					debugOut.print("\tsupply_" + t + "_" + i + ":" + supply[t][i]);
			}
			if (debug)
				debugOut.println();
		}

		try {
			IloCplex cplex = new IloCplex();
			cplex.setParam(IloCplex.IntParam.SimDisplay, 0); // 0 No iteration messages until solution
			// 1 Iteration info after each refactoring
			// 2 Iteration info for each iteration

			FileOutputStream outfile = new FileOutputStream(outFile, true);
			PrintStream out = new PrintStream(outfile);
			out.println("improveMethod");
			// obtain q and u at the last stage
			if (debug)
				debugOut.println("obtain q and u at the last stage:");
			updateLastStageQandU(myNetwork, q[T - 1], u[T - 1]);
			// end of obtaining q and u at the stage T-1
			if (debug)
				debugOut.println("end of obtaining q and u at the stage.");
			KStar[T - 1] = 0;
			for (int ite = 1; ite <= ITE; ite++) {
				if (debug)
					debugOut.println("ite=:\t" + ite);
				for (int t = T - 2; t >= 0; t--) {
					obj = updateQandU(myNetwork, cplex, t, KStar, q[t + 1], u[t + 1], supply[t], q[t], u[t]);
				}
				// out.println("Ite:\t"+ite+"\tobj:\t"+obj);
				if (debug)
					debugOut.println("obj:\t" + obj);
				out.println(obj);
				if (max_obj < obj)
					max_obj = obj;
				// update supply
				if (debug)
					debugOut.println("update supply");
				double temp_sol = 0;
				double temp_sol2 = 0;
				/*
				 * for (int t = 0; t <= T - 2; t++) { temp_sol2 += updateSupply(myNetwork,
				 * cplex, t, supply[t], supply[t + 1],q[t + 1], u[t + 1]); } // end of update
				 * supply //get the cost at the last stage for (int i = 1; i <= N; i++) { double
				 * u_break = 0; for (int j = 0; j < q[T-1][i - 1].length; j++) { u_break +=
				 * u[T-1][i - 1][j]; if (u_break <= supply[T - 1][i]) { temp_sol2 += q[T-1][i -
				 * 1][j] * u[T-1][i - 1][j]; } else { temp_sol2 += (supply[T - 1][i] - u_break +
				 * u[T-1][i - 1][j]) * q[T-1][i - 1][j]; break; } } }
				 */
				for (int t = 0; t <= T - 3; t++) {
					// obtain x1
					// if(t>0) updateSupply(myNetwork, cplex, t, supply[t], supply[t + 1],q[t + 1],
					// u[t + 1]);
					updateSupply(myNetwork, cplex, t, supply[t], supply[t + 1], q[t + 1], u[t + 1]);
					updateQandU(myNetwork, cplex, t + 1, KStar, q[t + 2], u[t + 2], supply[t + 1], q[t + 1], u[t + 1]);
					for (int ite2 = 1; ite2 <= ITE2; ite2++) {
						// get G1_x1
						double G1_x1 = 0;
						for (int i = 0; i <= N; i++) {
							ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
							while (arc != null) {
								G1_x1 += arc.flow * arc.cost;
								arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
							}
							if (i > 0) {
								double u_break = 0;
								for (int j = 0; j < q[t + 1][i - 1].length; j++) {
									u_break += u[t + 1][i - 1][j];
									if (u_break <= supply[t + 1][i]) {
										G1_x1 += q[t + 1][i - 1][j] * u[t + 1][i - 1][j];
									} else {
										G1_x1 += (supply[t + 1][i] - u_break + u[t + 1][i - 1][j]) * q[t + 1][i - 1][j];
										break;
									}
								}
							}
						}
						G1_x1 -= KStar[t + 1];
						out.println(ite2 + "\t" + G1_x1);
						double alpha = 1;
						// find x2
						updateSupply(myNetwork, cplex, t, supply[t], supply[t + 1], q[t + 1], u[t + 1]);
						Hashtable indexTable = new Hashtable();
						for (int i = 0; i <= N; i++) {
							ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
							while (arc != null) {
								indexTable.put(arc.getKey(), new Double(arc.flow));
								arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
							}
						}
						double alpha_thread = 0.000001;
						while (alpha > alpha_thread) {
							if (debug)
								debugOut.println("alpha=:\t" + alpha);
							double[][] qqq = new double[N][];
							double[][] uuu = new double[N][];
							updateQandU(myNetwork, cplex, t + 1, KStar, q[t + 2], u[t + 2], supply[t + 1], qqq, uuu);
							// get G2_x2
							double G2_x2 = 0;
							for (int i = 0; i <= N; i++) {
								ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
								while (arc != null) {
									G2_x2 += arc.flow * arc.cost;
									arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
								}
								if (i > 0) {
									double u_break = 0;
									for (int j = 0; j < qqq[i - 1].length; j++) {
										u_break += uuu[i - 1][j];
										if (u_break <= supply[t + 1][i]) {
											G2_x2 += qqq[i - 1][j] * uuu[i - 1][j];
										} else {
											G2_x2 += (supply[t + 1][i] - u_break + uuu[i - 1][j]) * qqq[i - 1][j];
											break;
										}
									}
								}
							}
							G2_x2 -= KStar[t + 1];
							if (debug)
								debugOut.println("G2_x2=:\t" + G2_x2 + "\tG1_x1=:\t" + G1_x1);
							if (G2_x2 < G1_x1) {
								for (int i = 0; i < N; i++) {
									q[t + 1][i] = new double[qqq[i].length];
									u[t + 1][i] = new double[qqq[i].length];
									for (int j = 0; j < qqq[i].length; j++) {
										q[t + 1][i][j] = qqq[i][j];
										u[t + 1][i][j] = uuu[i][j];
									}
								}
								break;
							} else {
								alpha = alpha / 2;
								if (debug)
									debugOut.println("hi update solution");
								for (int i = 0; i <= N; i++) {
									ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
									supply[t + 1][i] = 0;
									while (arc != null) {
										arc.flow = (1 - alpha) * arc.pre_flow
												+ alpha * ((Double) indexTable.get(arc.getKey())).doubleValue();
										supply[t + 1][i] += arc.flow;
										arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
									}
									// if(debug) debugOut.println("N" + ((t + 1) * (N + 1) +
									// i)+"\t"+supply[t+1][i]);
								}
							}
						}
						if (alpha < alpha_thread) {
							for (int i = 0; i <= N; i++) {
								ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
								supply[t + 1][i] = 0;
								while (arc != null) {
									arc.flow = arc.pre_flow;
									supply[t + 1][i] += arc.flow;
									arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
								}
								if (debug)
									debugOut.println("N" + ((t + 1) * (N + 1) + i) + "\t" + supply[t + 1][i]);
							}
							break;
						}
					}
					// if(t==0) max_obj = updateQandU(myNetwork, cplex, t,KStar, q[t + 1], u[t + 1],
					// supply[t], q[t], u[t]);
					// if(t== 0 && debug) debugOut.println("find optimal:\t"+max_obj);
					for (int i = 0; i <= N; i++) {
						ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
						while (arc != null) {
							temp_sol += arc.flow * arc.cost;
							arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
						}
					}

				}

				// get the cost at the last two stages
				temp_sol += updateSupply(myNetwork, cplex, T - 2, supply[T - 2], supply[T - 1], q[T - 1], u[T - 1]);

// get the cost of the last stage
				for (int i = 0; i < N; i++) {
					double u_break = 0;
					for (int j = 0; j < q[T - 1][i].length; j++) {
						u_break += u[T - 1][i][j];
						if (u_break <= supply[T - 1][i + 1]) {
							temp_sol += q[T - 1][i][j] * u[T - 1][i][j];
						} else {
							temp_sol += (supply[T - 1][i + 1] - u_break + u[T - 1][i][j]) * q[T - 1][i][j];
							break;
						}
					}
				}
				if (debug)
					debugOut.println("solution value:\t " + temp_sol);
				if (best_sol > temp_sol)
					best_sol = temp_sol;
				// if (best_sol > temp_sol2) best_sol = temp_sol2;
			}

			// out.println("Ite:\t"+ite+"\tobj:\t"+obj);
			if (debug)
				debugOut.println("obj:\t" + obj);
			out.println(obj);
			if (max_obj < obj)
				max_obj = obj;

			long end_time = System.currentTimeMillis();
			out.println(max_obj + "\t" + best_sol + "\t" + (end_time - start_time) / 1000.0);
			cplex.end();
			out.close();
			outfile.close();
			debugOut.print(max_obj + "\t" + best_sol + "\t" + (end_time - start_time) / 1000.0 + "\t");
		} catch (Exception e) {
			System.err.println("improved method exception '" + e + "' caught");
		}

		return max_obj;

	}

	public double improveMethod2(Network myNetwork, int ITE, int ITE2, String outFile) {
		if (debug)
			debugOut.println("\n\n Iterations of improveMethod:");
		double obj = Double.NEGATIVE_INFINITY;
		double max_obj = Double.NEGATIVE_INFINITY;
		double best_sol = Double.MAX_VALUE;
		long start_time = System.currentTimeMillis();
		int N = myNetwork.N;
		int T = myNetwork.T;
		if (T <= 3)
			ITE = 1;
		double[][][] q = new double[T][N][]; // slop
		double[][][] u = new double[T][N][]; // upper bound
		double[][] supply = new double[T][N + 1]; // upper bound
		double[] KStar = new double[T];
		for (int t = 0; t < T; t++) {
			for (int i = 0; i <= N; i++) {
				supply[t][i] = ((NodeInfo) myNetwork.getNode("N" + i)).supply;
				// supply[t][i] = ( (NodeInfo) myNetwork.getNode("N" + (t*(N+1)+i))).supply;
			}
			// if(debug) supply[1][1]=supply[1][2]=supply[1][3]=1;
		}

		try {
			IloCplex cplex = new IloCplex();
			cplex.setParam(IloCplex.IntParam.SimDisplay, 0); // 0 No iteration messages until solution
			// 1 Iteration info after each refactoring
			// 2 Iteration info for each iteration

			FileOutputStream outfile = new FileOutputStream(outFile, true);
			PrintStream out = new PrintStream(outfile);
			out.println("improveMethod2");
			// obtain q and u at the last stage
			if (debug)
				debugOut.println("obtain q and u at the last stage:");
			updateLastStageQandU(myNetwork, q[T - 1], u[T - 1]);
			// end of obtaining q and u at the stage T-1
			if (debug)
				debugOut.println("end of obtaining q and u at the stage.");
			KStar[T - 1] = 0;
			for (int ite = 1; ite <= ITE; ite++) {
				if (debug)
					debugOut.println("ite=:\t" + ite);
				for (int t = T - 2; t >= 0; t--) {
					obj = updateQandU(myNetwork, cplex, t, KStar, q[t + 1], u[t + 1], supply[t], q[t], u[t]);
				}
				// out.println("Ite:\t"+ite+"\tobj:\t"+obj);
				if (debug)
					debugOut.println("obj:\t" + obj);
				out.println(obj);
				if (max_obj < obj)
					max_obj = obj;
				// update supply
				if (debug)
					debugOut.println("update supply");
				double temp_sol = 0;
				double temp_sol2 = 0;
				for (int t = 0; t <= T - 3; t++) {
					// obtain x1
					// if(t>0)
					updateSupply(myNetwork, cplex, t, supply[t], supply[t + 1], q[t + 1], u[t + 1]);
					updateQandU(myNetwork, cplex, t + 1, KStar, q[t + 2], u[t + 2], supply[t + 1], q[t + 1], u[t + 1]);
					boolean stopindex = false;
					for (int ite2 = 1; ite2 <= ITE2; ite2++) {
						if (debug)
							debugOut.println("ite2=:\t" + ite2);
						Vector vector = new Vector();
						// get G1_x1
						double G1_x1 = 0;
						for (int i = 0; i <= N; i++) {
							ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
							while (arc != null) {
								G1_x1 += arc.flow * arc.cost;
								arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
							}
							if (i > 0) {
								double u_break = 0;
								for (int j = 0; j < q[t + 1][i - 1].length; j++) {
									u_break += u[t + 1][i - 1][j];
									if (u_break <= supply[t + 1][i]) {
										G1_x1 += q[t + 1][i - 1][j] * u[t + 1][i - 1][j];
									} else {
										G1_x1 += (supply[t + 1][i] - u_break + u[t + 1][i - 1][j]) * q[t + 1][i - 1][j];
										break;
									}
								}
							}
						}
						G1_x1 -= KStar[t + 1];
						double kvalue = KStar[t + 1];
						out.println("ite=:\t" + ite + "ite2=:\t" + ite2 + "\t" + G1_x1);
						double alpha = 1;
						// find x2
						updateSupply(myNetwork, cplex, t, supply[t], supply[t + 1], q[t + 1], u[t + 1]);
						Hashtable indexTable = new Hashtable();
						for (int i = 0; i <= N; i++) {
							ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
							while (arc != null) {
								indexTable.put(arc.getKey(), new Double(arc.flow));
								arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
							}
						}
//						double alpha_thread = 0.00005;
						double alpha_thread = 0.0005;
						while (alpha > alpha_thread) {
							if (debug)
								debugOut.println("alpha=:\t" + alpha);
							double[][] qqq = new double[N][];
							double[][] uuu = new double[N][];
							updateQandU(myNetwork, cplex, t + 1, KStar, q[t + 2], u[t + 2], supply[t + 1], qqq, uuu);
							// get G2_x2
							double G2_x2 = 0;
							for (int i = 0; i <= N; i++) {
								ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
								while (arc != null) {
									G2_x2 += arc.flow * arc.cost;
									arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
								}
								if (i > 0) {
									double u_break = 0;
									for (int j = 0; j < qqq[i - 1].length; j++) {
										u_break += uuu[i - 1][j];
										if (u_break <= supply[t + 1][i]) {
											G2_x2 += qqq[i - 1][j] * uuu[i - 1][j];
										} else {
											G2_x2 += (supply[t + 1][i] - u_break + uuu[i - 1][j]) * qqq[i - 1][j];
											break;
										}
									}
								}
							}
							G2_x2 -= KStar[t + 1];
							if (debug)
								debugOut.println("G2_x2=:\t" + G2_x2 + "\tG1_x1=:\t" + G1_x1);
							out.println("G2_x2=:\t" + G2_x2 + "\tG1_x1=:\t" + G1_x1);
							if (G2_x2 < G1_x1 - 0.5 / (ite2 / 10 + 1)) {
								// if (G2_x2 < G1_x1) {
								for (int i = 0; i < N; i++) {
									q[t + 1][i] = new double[qqq[i].length];
									u[t + 1][i] = new double[qqq[i].length];
									for (int j = 0; j < qqq[i].length; j++) {
										q[t + 1][i][j] = qqq[i][j];
										u[t + 1][i][j] = uuu[i][j];
									}
								}
								break;
							} else {
								alpha = alpha / 2;
								if (alpha < alpha_thread) {
									vector.add(indexTable);
									indexTable = new Hashtable();
									if (debug && vector.size() > 1) {
										int debug = 1;
									}
									double temp_obj = findDirection(myNetwork, cplex, t, 1, 0.001 * vector.size(),
											supply[t], vector, indexTable, supply[t + 1], q[t + 1], u[t + 1]);
									temp_obj -= kvalue;
									out.println("find direction\tobj=" + temp_obj);
									// if( temp_obj >= G1_x1 ) {
									if (temp_obj >= G1_x1 - 0.05) {
										if (debug)
											debugOut.println("unable to find new direction" + temp_obj + "\t" + G1_x1);
										for (int i = 0; i <= N; i++) {
											ArcInfo arc = (ArcInfo) myNetwork
													.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
											supply[t + 1][i] = 0;
											while (arc != null) {
												arc.flow = arc.pre_flow;
												supply[t + 1][i] += arc.flow;
												arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
											}
											if (debug)
												debugOut.println(
														"N" + ((t + 1) * (N + 1) + i) + "\t" + supply[t + 1][i]);
										}
										if (ite2 > 20)
											stopindex = true;
										break;
									} else {
										alpha = 1;
									}
								} else {
									if (debug)
										debugOut.println("update solution");
									for (int i = 0; i <= N; i++) {
										ArcInfo arc = (ArcInfo) myNetwork
												.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
										supply[t + 1][i] = 0;
										while (arc != null) {
											arc.flow = (1 - alpha) * arc.pre_flow
													+ alpha * ((Double) indexTable.get(arc.getKey())).doubleValue();
											supply[t + 1][i] += arc.flow;
											arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
										}
										// if (debug) debugOut.println("N" + ( (t + 1) * (N + 1) + i) + "\t" + supply[t
										// + 1][i]);
									}
								}
							}
						}
						if (stopindex)
							break;
					}
					// if(t==0) max_obj = updateQandU(myNetwork, cplex, t,KStar, q[t + 1], u[t + 1],
					// supply[t], q[t], u[t]);
					// if(t== 0 && debug) debugOut.println("find optimal:\t"+max_obj);
					for (int i = 0; i <= N; i++) {
						ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
						while (arc != null) {
							temp_sol += arc.flow * arc.cost;
							arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
						}
					}

				}

				// get the cost at the last two stages
				temp_sol += updateSupply(myNetwork, cplex, T - 2, supply[T - 2], supply[T - 1], q[T - 1], u[T - 1]);

// get the cost of the last stage
				for (int i = 0; i < N; i++) {
					double u_break = 0;
					for (int j = 0; j < q[T - 1][i].length; j++) {
						u_break += u[T - 1][i][j];
						if (u_break <= supply[T - 1][i + 1]) {
							temp_sol += q[T - 1][i][j] * u[T - 1][i][j];
						} else {
							temp_sol += (supply[T - 1][i + 1] - u_break + u[T - 1][i][j]) * q[T - 1][i][j];
							break;
						}
					}
				}
				if (debug)
					debugOut.println("solution value:\t " + temp_sol);
				if (best_sol > temp_sol)
					best_sol = temp_sol;
				// if (best_sol > temp_sol2) best_sol = temp_sol2;
			}

			// out.println("Ite:\t"+ite+"\tobj:\t"+obj);
			if (debug)
				debugOut.println("obj:\t" + obj);
			out.println(obj);
			if (max_obj < obj)
				max_obj = obj;

			long end_time = System.currentTimeMillis();
			out.println(max_obj + "\t" + best_sol + "\t" + (end_time - start_time) / 1000.0);
			cplex.end();
			out.close();
			outfile.close();
			debugOut.print(max_obj + "\t" + best_sol + "\t" + (end_time - start_time) / 1000.0 + "\t");
		} catch (Exception e) {
			System.err.println("improved method2 exception '" + e + "' caught");
		}

		return max_obj;

	}

	public double meanMethod(Network myNetwork, String outFile) {
		if (debug)
			debugOut.print("\n meanMethod:");
		double obj = Double.NEGATIVE_INFINITY;
		long start_time = System.currentTimeMillis();
		int N = myNetwork.N;
		int T = myNetwork.T;
		try {
			IloCplex cplex = new IloCplex();
			cplex.setParam(IloCplex.IntParam.SimDisplay, 0); // 0 No iteration messages until solution
			// 1 Iteration info after each refactoring
			// 2 Iteration info for each iteration

			FileOutputStream outfile = new FileOutputStream(outFile);
			PrintStream out = new PrintStream(outfile);

			int numArc = myNetwork.getNumArc();
			IloNumVar[] x = new IloNumVar[numArc];
			IloNumVar[] y = new IloNumVar[T * N]; // shortage
			IloNumVar[] z = new IloNumVar[T * N]; // overage
			IloLinearNumExpr obj_exp = cplex.linearNumExpr();
			for (int i = 0; i < numArc; i++) {
				ArcInfo arc = (ArcInfo) myNetwork.getArc("A" + i);
				x[i] = cplex.numVar(arc.cap_low, arc.cap_up, "x" + i);
				obj_exp.addTerm(arc.cost, x[i]);
			}
			int k = 0;
			for (int t = 1; t <= T; t++) {
				for (int i = 1; i <= N; i++) {
					NodeInfo node = (NodeInfo) myNetwork.getNode("N" + (t * (N + 1) + i));
					y[k] = cplex.numVar(0, MaxNum, "y" + k);
					obj_exp.addTerm(node.shortage_cost, y[k]);
					k++;
				}
			}
			k = 0;
			for (int t = 1; t <= T; t++) {
				for (int i = 1; i <= N; i++) {
					NodeInfo node = (NodeInfo) myNetwork.getNode("N" + (t * (N + 1) + i));
					z[k] = cplex.numVar(0, MaxNum, "z" + k);
					obj_exp.addTerm(node.overage_cost, z[k]);
					k++;
				}
			}
			cplex.addMinimize(obj_exp);

			IloRange rng[] = new IloRange[T * N];
			IloRange stng[] = new IloRange[T * N];
			IloRange ovng[] = new IloRange[T * N];
			for (int i = 1; i <= N; i++) {
				NodeInfo node = (NodeInfo) myNetwork.getNode("N" + i);
				IloLinearNumExpr exp = cplex.linearNumExpr();
				ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf(node.getKey());
				while (arc != null) {
					String arcKey = arc.getKey();
					int j = Integer.parseInt(arcKey.substring(1));
					exp.addTerm(1, x[j]);
					arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
				}
				rng[i - 1] = cplex.addEq(exp, node.supply, node.getKey());
			}
			for (int t = 1; t < T; t++) {
				for (int i = 1; i <= N; i++) {
					NodeInfo node = (NodeInfo) myNetwork.getNode("N" + (i + t * (N + 1)));
					IloLinearNumExpr exp = cplex.linearNumExpr();
					ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf(node.getKey());
					while (arc != null) {
						String arcKey = arc.getKey();
						int j = Integer.parseInt(arcKey.substring(1));
						exp.addTerm(1, x[j]);
						arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
					}

					arc = (ArcInfo) myNetwork.getFirstOutArcOf(node.getKey());
					while (arc != null) {
						String arcKey = arc.getKey();
						int j = Integer.parseInt(arcKey.substring(1));
						exp.addTerm(-1, x[j]);
						arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
					}
					rng[t * N + i - 1] = cplex.addEq(exp, 0, node.getKey());
				}

			}

// add shortage
			for (int t = 1; t <= T; t++) {
				for (int i = 1; i <= N; i++) {
					NodeInfo node = (NodeInfo) myNetwork.getNode("N" + (i + t * (N + 1)));
					IloLinearNumExpr exp = cplex.linearNumExpr();
					ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf(node.getKey());
					while (arc != null) {
						String arcKey = arc.getKey();
						int j = Integer.parseInt(arcKey.substring(1));
						exp.addTerm(1, x[j]);
						arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
					}
					exp.addTerm(1, y[(t - 1) * N + i - 1]);
					stng[(t - 1) * N + i - 1] = cplex.addGe(exp, node.demand, node.getKey() + "_s");
				}
			}
// add overage
			for (int t = 1; t <= T; t++) {
				for (int i = 1; i <= N; i++) {
					NodeInfo node = (NodeInfo) myNetwork.getNode("N" + (i + t * (N + 1)));
					IloLinearNumExpr exp = cplex.linearNumExpr();
					ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf(node.getKey());
					while (arc != null) {
						String arcKey = arc.getKey();
						int j = Integer.parseInt(arcKey.substring(1));
						exp.addTerm(1, x[j]);
						arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
					}
					exp.addTerm(-1, z[(t - 1) * N + i - 1]);
					ovng[(t - 1) * N + i - 1] = cplex.addLe(exp, node.demand, node.getKey() + "_o");
				}
			}

			// if (debug) cplex.exportModel(".\\data\\meanMethod.lp");
// solve the model and display the solution if one was found

			if (cplex.solve()) {
				// cplex.output().println("Solution status = " + cplex.getStatus());
				// cplex.output().println("Solution value = " + cplex.getObjValue());
				int ncols = cplex.getNcols();
				obj = cplex.getObjValue();
				out.println(obj);
				out.close();
				outfile.close();
				if (debug) {
					debugOut.println("obj:\t" + obj);
				}
				for (int j = 0; j < x.length; ++j) {
					if (debug) {
						debugOut.print("x[" + j + "]\t " + cplex.getValue(x[j]) + "\t");
						if (j % 10 == 9)
							debugOut.println();
					}
					ArcInfo arc = (ArcInfo) myNetwork.getArc("A" + j);
					arc.flow = cplex.getValue(x[j]);

				}
				if (debug)
					debugOut.println();
				for (int t = 1; t <= T; t++) {
					for (int i = 0; i <= N; i++) {
						NodeInfo node = myNetwork.getNode("N" + (t * (N + 1) + i));
						node.supply = 0;
						ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf(node.getKey());
						while (arc != null) {
							node.supply += arc.flow;
							arc = myNetwork.getNextInArcFrom(arc.getKey());
						}
						if (debug)
							debugOut.print("\t" + node.getKey() + "_supply\t" + node.supply);
					}
					if (debug)
						debugOut.println();
				}

			}

			cplex.end();
			long end_time = System.currentTimeMillis();
			debugOut.print(obj + "\t" + precision((end_time - start_time) / 1000.0) + "\t");

		} catch (Exception e) {
			System.err.println("meanMethod exception '" + e + "' caught");
		}

		return obj;

	}

	public double newMethod(Network myNetwork, int ITE, int ITE2, String outFile) {
		if (debug)
			debugOut.println("\n\nIterations of newMethod:");
		double obj = Double.NEGATIVE_INFINITY;
		double max_obj = Double.NEGATIVE_INFINITY;
		double best_sol = Double.MAX_VALUE;
		long start_time = System.currentTimeMillis();
		int N = myNetwork.N;
		int T = myNetwork.T;
		// if (T <= 3) ITE = 2;
		double[][][] q = new double[T][N][]; // slop
		double[][][] u = new double[T][N][]; // upper bound
		double[][] supply = new double[T][N + 1]; // upper bound
		double[] KStar = new double[T];
		if (debug)
			debugOut.println("initial supply");
		for (int t = 0; t < T; t++) {
			for (int i = 0; i <= N; i++) {
				supply[t][i] = ((NodeInfo) myNetwork.getNode("N" + i)).supply;
				// supply[t][i] = ( (NodeInfo) myNetwork.getNode("N" + (t*(N+1)+i))).supply;
				if (debug)
					debugOut.print("\tsupply_" + t + "_" + i + ":" + supply[t][i]);
			}
			if (debug)
				debugOut.println();
		}

		try {
			IloCplex cplex = new IloCplex();
			cplex.setParam(IloCplex.IntParam.SimDisplay, 0); // 0 No iteration messages until solution
			// 1 Iteration info after each refactoring
			// 2 Iteration info for each iteration

			FileOutputStream outfile = new FileOutputStream(outFile, true);
			PrintStream out = new PrintStream(outfile);
			out.println("NewMethod");
			// obtain q and u at the last stage
			if (debug)
				debugOut.println("obtain q and u at the last stage:");
			updateLastStageQandU(myNetwork, q[T - 1], u[T - 1]);
			// end of obtaining q and u at the stage T-1
			if (debug)
				debugOut.println("end of obtaining q and u at the stage.");
			KStar[T - 1] = 0;
			for (int ite = 1; ite <= ITE; ite++) {
				if (debug)
					debugOut.println("ite=:\t" + ite);
				for (int t = T - 2; t >= 0; t--) {
					obj = updateQandU(myNetwork, cplex, t, KStar, q[t + 1], u[t + 1], supply[t], q[t], u[t]);
				}

				// out.println("Ite:\t"+ite+"\tobj:\t"+obj);
				if (debug)
					debugOut.println("obj:\t" + obj);
				out.println(obj);
				if (max_obj < obj)
					max_obj = obj;
				// update supply
				if (debug)
					debugOut.println("update supply");
				double temp_sol = 0;
				double temp_sol2 = 0;

				for (int t = 0; t <= T - 2; t++) {
					temp_sol2 += updateSupply(myNetwork, cplex, t, supply[t], supply[t + 1], q[t + 1], u[t + 1]);
				} // end of update supply
					// get the cost at the last stage
				for (int i = 1; i <= N; i++) {
					double u_break = 0;
					for (int j = 0; j < q[T - 1][i - 1].length; j++) {
						u_break += u[T - 1][i - 1][j];
						if (u_break <= supply[T - 1][i]) {
							temp_sol2 += q[T - 1][i - 1][j] * u[T - 1][i - 1][j];
						} else {
							temp_sol2 += (supply[T - 1][i] - u_break + u[T - 1][i - 1][j]) * q[T - 1][i - 1][j];
							break;
						}
					}
				}

				for (int t = 0; t <= T - 3; t++) {

					double temp_temp_sol = 0;
					t = t + 1;
					for (int ite2 = 1; ite2 <= ITE2; ite2++) {
						if (debug)
							debugOut.println("ite2=:\t " + ite2);
						temp_temp_sol = 0;
						// update q and u for the new supply

						if (debug)
							debugOut.println("t=t+1");
						updateQandU(myNetwork, cplex, t, KStar, q[t + 1], u[t + 1], supply[t], q[t], u[t]);
						double kvalue1 = KStar[t];
						t = t - 1;

						if (debug)
							debugOut.println("t=t-1");
						if (debug)
							debugOut.println("Obtain x2");
						// botian x2
						updateSupply(myNetwork, cplex, t, supply[t], supply[t + 1], q[t + 1], u[t + 1]);

						if (debug)
							debugOut.println("end of obtaining x2.");

						t = t + 1;
						// update q and u for the new supply
						if (debug)
							debugOut.println("update q and u for the new supply:");
						double[][] qqq = new double[N][];
						double[][] uuu = new double[N][];
						for (int i = 1; i <= N; i++) {
							qqq[i - 1] = new double[q[t][i - 1].length];
							uuu[i - 1] = new double[u[t][i - 1].length];
							for (int j = 0; j < q[t][i - 1].length; j++) {
								qqq[i - 1][j] = q[t][i - 1][j];
								uuu[i - 1][j] = u[t][i - 1][j];
							}
						}
						updateQandU(myNetwork, cplex, t, KStar, q[t + 1], u[t + 1], supply[t], q[t], u[t]);
						double kvalue2 = KStar[t];
						// get G2_x2
						double G2_x2 = 0;
						for (int i = 0; i <= N; i++) {
							ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) + i));
							while (arc != null) {
								G2_x2 += arc.flow * arc.cost;
								arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
							}
							if (i > 0) {
								double u_break = 0;
								for (int j = 0; j < q[t][i - 1].length; j++) {
									u_break += u[t][i - 1][j];
									if (u_break <= supply[t][i]) {
										G2_x2 += q[t][i - 1][j] * u[t][i - 1][j];
									} else {
										G2_x2 += (supply[t][i] - u_break + u[t][i - 1][j]) * q[t][i - 1][j];
										break;
									}
								}
							}
						}
						G2_x2 -= kvalue2;
						if (debug)
							debugOut.println("G2_x2 =:\t" + G2_x2);

						// bet G2_x1
						double G2_x1 = 0;
						for (int i = 0; i <= N; i++) {
							ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) + i));
							double sp = 0;
							while (arc != null) {
								G2_x1 += arc.pre_flow * arc.cost;
								sp += arc.pre_flow;
								arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
							}
							if (i > 0) {
								double u_break = 0;
								for (int j = 0; j < q[t][i - 1].length; j++) {
									u_break += u[t][i - 1][j];
									if (u_break <= sp) {
										G2_x1 += q[t][i - 1][j] * u[t][i - 1][j];
									} else {
										G2_x1 += (sp - u_break + u[t][i - 1][j]) * q[t][i - 1][j];
										break;
									}
								}
							}
						}
						// obtained G2_x1
						G2_x1 -= kvalue2;
						if (debug)
							debugOut.println("G2_x1 =:\t" + G2_x1);
						if (G2_x1 <= G2_x2) {
							int stat = binarySearch(myNetwork, t, N, kvalue1, kvalue2, q[t], u[t], qqq, uuu);
							for (int i = 0; i <= N; i++) {
								ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) + i));
								supply[t][i] = 0;
								while (arc != null) {
									supply[t][i] += arc.pre_flow;
									temp_temp_sol += arc.pre_flow * arc.cost;
									if (debug)
										debugOut.print(arc.getKey() + "\t" + arc.flow + "\t");
									arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
								}
								// if(debug) debugOut.println();
								if (debug)
									debugOut.println("supply " + (t) + "_" + i + ":\t" + supply[t][i]);
							}
							if (stat == 1)
								break;
						} else {
							// Find x3 that minimize G2(x)
							t = t - 1;
							updateSupply(myNetwork, cplex, t, supply[t], supply[t + 1], q[t + 1], u[t + 1]);
							// get G2_x3, G1_x3
							double G2_x3 = 0, G1_x3 = 0;
							t = t + 1;
							for (int i = 0; i <= N; i++) {
								ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1)));
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
										} else {
											G2_x3 += (supply[t][i] - u_break + u[t][i - 1][j]) * q[t][i - 1][j];
											break;
										}
									}
									u_break = 0;
									for (int j = 0; j < qqq[i - 1].length; j++) {
										u_break += uuu[i - 1][j];
										if (u_break <= supply[t][i]) {
											G1_x3 += qqq[i - 1][j] * uuu[i - 1][j];
										} else {
											G1_x3 += (supply[t][i] - u_break + uuu[i - 1][j]) * qqq[i - 1][j];
											break;
										}
									}

								}
							}

							if (G2_x3 <= G1_x3) {
								int stat = binarySearch(myNetwork, t, N, kvalue1, kvalue2, q[t], u[t], qqq, uuu);
								for (int i = 0; i <= N; i++) {
									ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) + i));
									supply[t][i] = 0;
									while (arc != null) {
										supply[t][i] += arc.pre_flow;
										temp_temp_sol += arc.pre_flow * arc.cost;
										if (debug)
											debugOut.print(arc.getKey() + "\t" + arc.flow + "\t");
										arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
									}
									// if(debug) debugOut.println();
									if (debug)
										debugOut.println("supply " + (t) + "_" + i + ":\t" + supply[t][i]);
								}
								if (stat == 1)
									break;
							} else {
								for (int i = 0; i <= N; i++) {
									ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) + i));
									supply[t][i] = 0;
									while (arc != null) {
										arc.pre_flow = arc.flow;
										supply[t][i] += arc.flow;
										temp_temp_sol += arc.pre_flow * arc.cost;
										arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
									}
									if (debug)
										debugOut.println("supply " + (t) + "_" + i + ":\t" + supply[t][i]);
								}
							}

						}
					}
					t = t - 1;
					temp_sol += temp_temp_sol;
				} // end of update supply

				// get the cost at the last two stages
				temp_sol += updateSupply(myNetwork, cplex, T - 2, supply[T - 2], supply[T - 1], q[T - 1], u[T - 1]);

// get the cost of the last stage
				for (int i = 0; i < N; i++) {
					double u_break = 0;
					for (int j = 0; j < q[T - 1][i].length; j++) {
						u_break += u[T - 1][i][j];
						if (u_break <= supply[T - 1][i + 1]) {
							temp_sol += q[T - 1][i][j] * u[T - 1][i][j];
						} else {
							temp_sol += (supply[T - 1][i + 1] - u_break + u[T - 1][i][j]) * q[T - 1][i][j];
							break;
						}
					}
				}
				if (debug)
					debugOut.println("solution value:\t " + temp_sol);
				if (best_sol > temp_sol)
					best_sol = temp_sol;
				if (best_sol > temp_sol2)
					best_sol = temp_sol2;
			}

			// out.println("Ite:\t"+ite+"\tobj:\t"+obj);
			if (debug)
				debugOut.println("obj:\t" + obj);
			out.println(obj);
			if (max_obj < obj)
				max_obj = obj;

			long end_time = System.currentTimeMillis();
			out.println(max_obj + "\t" + best_sol + "\t" + (end_time - start_time) / 1000.0);
			cplex.end();
			out.close();
			outfile.close();
			debugOut.print(max_obj + "\t" + best_sol + "\t" + (end_time - start_time) / 1000.0 + "\t");
		} catch (Exception e) {
			System.err.println("newMethod exception '" + e + "' caught");
		}

		return max_obj;

	}

	public int binarySearch(Network myNetwork, int t, int N, double kv1, double kv2, double[][] q, double[][] u,
			double[][] qq, double[][] uu) {
		double G1_x1 = 0, G1_x2 = 0, G2_x1 = 0, G2_x2 = 0, G1_xm = 0, G2_xm = 0;
		for (int i = 0; i <= N; i++) {
			ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) + i));
			double sp1 = 0, sp2 = 0, sp3 = 0;
			while (arc != null) {
				G1_x2 += arc.flow * arc.cost;
				G2_x2 += arc.flow * arc.cost;
				G1_x1 += arc.pre_flow * arc.cost;
				G2_x1 += arc.pre_flow * arc.cost;
				G1_xm += (arc.flow + arc.pre_flow) * arc.cost / 2;
				G2_xm += (arc.flow + arc.pre_flow) * arc.cost / 2;
				sp1 += arc.pre_flow;
				sp2 += arc.flow;
				sp3 += (arc.flow + arc.pre_flow) / 2;
				arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
			}
			if (i > 0) {
				double u_break = 0;
				for (int j = 0; j < q[i - 1].length; j++) {
					u_break += u[i - 1][j];
					if (u_break <= sp1) {
						G2_x1 += q[i - 1][j] * u[i - 1][j];
					} else {
						G2_x1 += (sp1 - u_break + u[i - 1][j]) * q[i - 1][j];
						break;
					}
				}
				u_break = 0;
				for (int j = 0; j < q[i - 1].length; j++) {
					u_break += u[i - 1][j];
					if (u_break <= sp2) {
						G2_x2 += q[i - 1][j] * u[i - 1][j];
					} else {
						G2_x2 += (sp2 - u_break + u[i - 1][j]) * q[i - 1][j];
						break;
					}
				}
				u_break = 0;
				for (int j = 0; j < q[i - 1].length; j++) {
					u_break += u[i - 1][j];
					if (u_break <= sp3) {
						G2_xm += q[i - 1][j] * u[i - 1][j];
					} else {
						G2_xm += (sp3 - u_break + u[i - 1][j]) * q[i - 1][j];
						break;
					}
				}

				u_break = 0;
				for (int j = 0; j < qq[i - 1].length; j++) {
					u_break += uu[i - 1][j];
					if (u_break <= sp1) {
						G1_x1 += qq[i - 1][j] * uu[i - 1][j];
					} else {
						G1_x1 += (sp1 - u_break + uu[i - 1][j]) * qq[i - 1][j];
						break;
					}
				}
				u_break = 0;
				for (int j = 0; j < qq[i - 1].length; j++) {
					u_break += uu[i - 1][j];
					if (u_break <= sp2) {
						G1_x2 += qq[i - 1][j] * uu[i - 1][j];
					} else {
						G1_x2 += (sp2 - u_break + uu[i - 1][j]) * qq[i - 1][j];
						break;
					}
				}
				u_break = 0;
				for (int j = 0; j < qq[i - 1].length; j++) {
					u_break += uu[i - 1][j];
					if (u_break <= sp3) {
						G1_xm += qq[i - 1][j] * uu[i - 1][j];
					} else {
						G1_xm += (sp3 - u_break + uu[i - 1][j]) * qq[i - 1][j];
						break;
					}
				}

			}
		}
		G1_x1 -= kv1;
		G1_x2 -= kv1;
		G1_xm -= kv1;
		G2_x1 -= kv2;
		G2_x2 -= kv2;
		G2_xm -= kv2;
		if (debug)
			debugOut.println("G1_x1=" + G1_x1 + ",G2_x1=" + G2_x1 + ",G1_x2=" + G1_x2 + ",G2_x2=" + G2_x2 + ",G1_xm="
					+ G1_xm + ",G2_xm=" + G2_xm);
		if (Math.abs(G1_x2 - G2_x2) <= 0.1) {
			if (debug)
				debugOut.println("G1_x2==G2_x2, find optimal solution:");
			for (int i = 0; i <= N; i++) {
				ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) + i));
				while (arc != null) {
					arc.pre_flow = arc.flow;
					if (debug)
						debugOut.print(arc.getKey() + "\t" + arc.pre_flow + "\t");
					arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
				}
				if (debug)
					debugOut.println();
			}
			return 1;
		} else if (Math.abs(G1_xm - G2_xm) <= 0.1) {
			if (debug)
				debugOut.println("G1_xm==G2_xm,find optimal solution:");
			for (int i = 0; i <= N; i++) {
				ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) + i));
				while (arc != null) {
					arc.pre_flow = (arc.flow + arc.pre_flow) / 2;
					if (debug)
						debugOut.print(arc.getKey() + "\t" + arc.pre_flow + "\t");
					arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
				}
				if (debug)
					debugOut.println();
			}
			return 1;
		} else if (Math.abs(G1_xm - G2_xm) <= 0.1 || Math.abs(G1_x1 - G1_x2 + G2_x1 - G2_x2 + G1_xm - G2_xm) <= 0.5) {
			for (int i = 0; i <= N; i++) {
				ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) + i));
				while (arc != null) {
					arc.pre_flow = (arc.flow + arc.pre_flow) / 2;
					arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
				}
			}
			return 0;
		} else {
			if ((G1_x1 - G2_x1) * (G1_xm - G2_xm) > 0 && (G2_x2 - G1_x2) * (G2_xm - G1_xm) < 0) {
				for (int i = 0; i <= N; i++) {
					ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) + i));
					while (arc != null) {
						arc.pre_flow = (arc.flow + arc.pre_flow) / 2;
						arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
					}
				}
				return binarySearch(myNetwork, t, N, kv1, kv2, q, u, qq, uu);
			} else if ((G1_x1 - G2_x1) * (G1_xm - G2_xm) < 0 && (G2_x2 - G1_x2) * (G2_xm - G1_xm) > 0) {
				for (int i = 0; i <= N; i++) {
					ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) + i));
					while (arc != null) {
						arc.flow = (arc.flow + arc.pre_flow) / 2;
						arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
					}
				}
				return binarySearch(myNetwork, t, N, kv1, kv2, q, u, qq, uu);

			} else {
				if (debug)
					debugOut.println("Error in binary search");
				return 2;
			}

		}

	}

	public static double precision(double x) {
		double p = 100;
		return ((int) (x * p + 0.5)) / p;
	}

	public double updateQandU(Network myNetwork, IloCplex cplex, int t, double KStar[], double qq[][], double uu[][],
			double sp[], double q[][], double u[][]) {
		double obj = 0;
		try {
			if (debug)
				debugOut.println("t=:\t" + t);
			int N = myNetwork.N;
			int numArc = 0;
			int numYarc = 0; // number of y
			for (int i = 0; i <= N; i++) {
				numArc += myNetwork.getNumOutArcOf("N" + (t * (N + 1) + i));
				if (i > 0)
					numYarc += qq[i - 1].length;
			}
			IloNumVar[] x = new IloNumVar[numArc];
			IloNumVar[] y = new IloNumVar[numYarc];
			IloLinearNumExpr obj_exp = cplex.linearNumExpr();
			int k = 0;
			Hashtable indexTable = new Hashtable();
			for (int i = 0; i <= N; i++) {
				ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" + (t * (N + 1) + i));
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
				for (int j = 0; j < qq[i - 1].length; j++) {
					y[k] = cplex.numVar(0, MaxNum + 1, "y" + k);
					obj_exp.addTerm(qq[i - 1][j], y[k]);
					k++;
				}
			}
			cplex.addMinimize(obj_exp);
			IloRange rng[] = new IloRange[2 * N];
			IloRange stng[] = new IloRange[numYarc];
			k = 0;
			for (int i = 1; i <= N; i++) {
				IloLinearNumExpr exp = cplex.linearNumExpr();
				ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" + (t * (N + 1) + i));
				while (arc != null) {
					int j = ((Integer) indexTable.get(arc.getKey())).intValue();
					exp.addTerm(1, x[j]);
					arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
				}
				rng[i - 1] = cplex.addEq(exp, sp[i], "N" + (t * (N + 1) + i));
			}

			k = 0;
			for (int i = 1; i <= N; i++) {
				IloLinearNumExpr exp = cplex.linearNumExpr();
				ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
				while (arc != null) {
					int j = ((Integer) indexTable.get(arc.getKey())).intValue();
					exp.addTerm(1, x[j]);
					arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
				}
				for (int j = 0; j < qq[i - 1].length; j++) {
					exp.addTerm(-1, y[k]);
					k++;
				}
				rng[N + i - 1] = cplex.addEq(exp, 0, "N" + ((t + 1) * (N + 1) + i));
			}

			// add y constraint
			k = 0;
			for (int i = 1; i <= N; i++) {
				for (int j = 0; j < qq[i - 1].length; j++) {
					IloLinearNumExpr exp = cplex.linearNumExpr();
					exp.addTerm(1, y[k]);
					stng[k] = cplex.addLe(exp, uu[i - 1][j], "Y" + t + "_" + i + "_" + j);
					k++;
				}
			}

			cplex.exportModel(".\\data\\scapMethod_" + t + ".lp");
			if (cplex.solve()) {

				obj = cplex.getObjValue() - KStar[t + 1];
				KStar[t] = KStar[t + 1];
				double[] pi = cplex.getDuals(stng);
				for (int i = 0; i < stng.length; i++)
					pi[i] = -pi[i];

				// obtain q and u

				double[][] qqq = new double[N][]; // store the q tempory
				for (int i = 0; i < N; i++) {
					qqq[i] = new double[qq[i].length];
					for (int j = 0; j < qq[i].length; j++) {
						qqq[i][j] = qq[i][j];
					}
				}

				k = 0;
				for (int i = 1; i <= N; i++) {
					for (int j = 0; j < qq[i - 1].length; j++) {
						qqq[i - 1][j] += pi[k];
						// if (pi[k] < -0.001) out.println("pi[" + k + "] is less than 0:\t" + pi[k]);
						KStar[t] += pi[k] * uu[i - 1][j];
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
						int ii = Integer.parseInt((arc.getToNodeKey()).substring(1)); // index of toNodeKey
						ii = ii - (t + 1) * (N + 1);
						if (ii > 0) {
							for (int j = 0; j < qq[ii - 1].length; j++) {
								HeapArc heaparc = new HeapArc(arc.getKey() + "_" + j, arc.cost + qqq[ii - 1][j]);
								heaparc.cap = Math.min(arc.cap_rest, uu[ii - 1][j]);
								arc.cap_rest -= heaparc.cap;
								heap.insert(heaparc);
								kk++;
								if (arc.cap_rest <= 0)
									break;
							}
						} else {
							HeapArc heaparc = new HeapArc(arc.getKey() + "_" + 0, arc.cost);
							heaparc.cap = arc.cap_up;
							heap.insert(heaparc);
							kk++;
						}
						arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
					}
					double[] temp_q = new double[kk];
					double[] temp_u = new double[kk];
					k = 0;
					for (int j = 0; j < temp_q.length; j++) {
						if (heap.isEmpty()) {
							break;
						} else {
							HeapArc heaparc = (HeapArc) heap.deleteMin();
							if (k == 0) {
								temp_q[k] = heaparc.value;
								temp_u[k] = heaparc.cap;
								k++;
							} else {
								if (temp_u[k - 1] >= MaxNum)
									break;
								if (heaparc.value <= temp_q[k - 1]) {
									temp_u[k - 1] += heaparc.cap;
								} else {
									temp_q[k] = heaparc.value;
									temp_u[k] = heaparc.cap;
									k++;
								}
							}
						}
					}
					q[i - 1] = new double[k];
					u[i - 1] = new double[k];
					// if (debug) debugOut.print(i);
					for (int j = 0; j < k; j++) {
						q[i - 1][j] = temp_q[j];
						u[i - 1][j] = temp_u[j];
						// if (debug) debugOut.print("\t" + temp_q[j] + "\t" + temp_u[j]);
					}
					// if (debug) debugOut.println();
				}
				// end of obtaining q and u
				if (debug)
					debugOut.println("end of obtaining q and u.");
			}
			cplex.clearModel();
		} catch (Exception e) {
			if (debug)
				debugOut.println("error at updating q and u" + e);
		}
		return obj;
	}

	public void updateLastStageQandU(Network myNetwork, double q[][], double u[][]) throws Exception {
		int N = myNetwork.N;
		int T = myNetwork.T;

		for (int i = 1; i <= N; i++) {
			String nodeKey = "N" + ((T - 1) * (N + 1) + i);
			ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf(nodeKey);
			// BinaryHeap heap = new BinaryHeap(myNetwork.getNumOutArcOf(nodeKey));
			BinaryHeap heap = new BinaryHeap();
			while (arc != null) {
				HeapArc heaparc = new HeapArc(arc.getKey(), arc.cost);
				heaparc.cap = arc.cap_up;
				heap.insert(heaparc);
				arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
			}
			double[] temp_q = new double[myNetwork.getNumOutArcOf(nodeKey)];
			double[] temp_u = new double[myNetwork.getNumOutArcOf(nodeKey)];
			int k = 0;
			for (int j = 0; j < temp_q.length; j++) {
				if (heap.isEmpty()) {
					break;
				} else {
					HeapArc heaparc = (HeapArc) heap.deleteMin();
					if (k == 0) {
						temp_q[k] = heaparc.value;
						temp_u[k] = heaparc.cap;
						k++;
					} else {
						if (temp_u[k - 1] >= MaxNum)
							break;
						if (heaparc.value <= temp_q[k - 1]) {
							temp_u[k - 1] += heaparc.cap;
						} else {
							temp_q[k] = heaparc.value;
							temp_u[k] = heaparc.cap;
							k++;
						}
					}
				}
			}
			q[i - 1] = new double[k];
			u[i - 1] = new double[k];
			if (debug)
				debugOut.print(i);
			for (int j = 0; j < k; j++) {
				q[i - 1][j] = temp_q[j];
				u[i - 1][j] = temp_u[j];
				if (debug)
					debugOut.print("\t" + temp_q[j] + "\t" + temp_u[j]);
			}
			if (debug)
				debugOut.println();
		}
		// end of obtaining q and u at the stage T-1

	}

	public double findGradient(Network myNetwork, int t, double sp[], double qq[][], double uu[][]) {
		int N = myNetwork.N;
		double temp_temp_sol = 0;
		SnNetwork snNetwork = new SnNetwork();
		SnNetInfo snNet = new SnNetInfo("N001");
		SnNodeInfo snNode = new SnNodeInfo("Ns", "N001");
		double total_supply = 0;
		for (int i = 0; i <= N; i++) {
			total_supply += sp[i];
		}
		snNode.supply = (int) total_supply;
		snNetwork.addNet(snNet);
		snNetwork.addNode(snNode);
		snNode = new SnNodeInfo("Nt", "N001");
		snNode.supply = (int) (-1 * total_supply);
		snNetwork.addNode(snNode);
		SnArcInfo snArc;
		for (int i = 0; i <= N; i++) {
			snNode = new SnNodeInfo("N" + (t * (N + 1) + i), "N001");
			snNode.supply = 0;
			snNetwork.addNode(snNode);
			snNode = new SnNodeInfo("N" + ((t - 1) * (N + 1) + i), "N001");
			snNode.supply = 0;
			snNetwork.addNode(snNode);
			snArc = new SnArcInfo("S" + i, "N001", "Ns", "N" + (t * (N + 1) + i));
			snArc.cost = 0;
			snArc.uBound = (int) sp[i];
			if (snArc.uBound > 0)
				snNetwork.addArc(snArc);
			snArc = new SnArcInfo("T" + i, "N001", "N" + (t * (N + 1) + i), "Nt");
			snArc.cost = 0;
			snArc.uBound = (int) MaxNum;
			if (snArc.uBound > 0)
				snNetwork.addArc(snArc);
		}
		ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1)));
		while (arc != null) {
			snArc = new SnArcInfo(arc.getKey(), "N001", arc.getFromNodeKey(), arc.getToNodeKey());
			snArc.cost = arc.cost + 0;
			snArc.uBound = (int) (arc.cap_up - arc.flow);
			if (snArc.uBound > 0)
				snNetwork.addArc(snArc);
			snArc = new SnArcInfo(arc.getKey() + "_r", "N001", arc.getToNodeKey(), arc.getFromNodeKey());
			snArc.cost = -arc.cost + 0.1;
			snArc.uBound = (int) arc.flow;
			if (snArc.uBound > 0)
				snNetwork.addArc(snArc);
			arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
		}
		for (int i = 1; i <= N; i++) {
			double q_left = 0, q_right = 0, u_break = 0;
			double u_left = 0, u_right = 0;
			for (int j = 0; j < qq[i - 1].length; j++) {
				u_break += uu[i - 1][j];
				if (u_break > sp[i]) {
					q_left = q_right = qq[i - 1][j];
					u_left = sp[i] - u_break + uu[i - 1][j];
					u_right = u_break - sp[i];
					break;
				} else if (u_break == sp[i]) {
					q_left = qq[i - 1][j];
					q_right = qq[i - 1][j + 1];
					u_left = uu[i - 1][j];
					u_right = uu[i - 1][j + 1];
					break;
				}
			}
			arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) + i));
			while (arc != null) {
				snArc = new SnArcInfo(arc.getKey(), "N001", arc.getFromNodeKey(), arc.getToNodeKey());
				snArc.cost = arc.cost + q_right;
				snArc.uBound = (int) Math.min(arc.cap_up - arc.flow, u_right);
				if (snArc.uBound > 0)
					snNetwork.addArc(snArc);
				snArc = new SnArcInfo(arc.getKey() + "_r", "N001", arc.getToNodeKey(), arc.getFromNodeKey());
				snArc.cost = -arc.cost - q_left;
				if (q_left >= q_right)
					snArc.cost += 0.1;
				snArc.uBound = (int) Math.min(arc.flow, u_left);
				if (snArc.uBound > 0)
					snNetwork.addArc(snArc);
				arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
			}
		}

		snArc = (SnArcInfo) snNetwork.getFirstArcIn("N001");
		while (snArc != null) {
			if (debug)
				System.out.println(snArc.getKey() + "\t" + snArc.getFromNodeKey() + "\t" + snArc.getToNodeKey() + "\t"
						+ snArc.cost + "\t" + snArc.uBound);
			snArc = (SnArcInfo) snNetwork.getNextArcFrom(snArc.getKey());
		}
		SnNodeInfo artNode = snNetwork.bigMStart("N001", 100);
		snNetwork.snAlgorithm("N001", 300, 200);
		if (debug)
			debugOut.println("snNetwork.getObjfcn\t:" + snNetwork.getObjfcn());
		if (snNetwork.getObjfcn() < -0.1) {
			for (int i = 0; i <= N; i++) {
				arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) + i));
				if (arc != null)
					sp[i] = 0;
				while (arc != null) {
					double f1 = 0, f2 = 0;
					if ((SnArcInfo) snNetwork.getArc(arc.getKey()) != null)
						f1 = ((SnArcInfo) snNetwork.getArc(arc.getKey())).flow;
					if ((SnArcInfo) snNetwork.getArc(arc.getKey() + "_r") != null)
						f2 = ((SnArcInfo) snNetwork.getArc(arc.getKey() + "_r")).flow;
					arc.flow += (double) (f1 - f2);
					sp[i] += arc.flow;
					temp_temp_sol += arc.flow * arc.cost;
					arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
				}
				if (debug)
					debugOut.println("supply " + (t) + "_" + i + ":\t" + sp[i]);
			}
			// go back to update q and u
			if (debug)
				debugOut.println("go back to update q and u");
		} else {
			temp_temp_sol = Double.MAX_VALUE;
		}
		return temp_temp_sol;
	}

	/**
	 * Revised on 2006-06-06
	 * 
	 * @param myNetwork
	 * @param t
	 * @param sp
	 * @param qq
	 * @param uu
	 * @return
	 */

	public double findGradient2(Network myNetwork, int t, double sp[], double qq[][], double uu[][]) {
		int N = myNetwork.N;
		double temp_temp_sol = 0;
		SnNetwork snNetwork = new SnNetwork();
		SnNetInfo snNet = new SnNetInfo("N001");
		SnNodeInfo snNode = new SnNodeInfo("Ns", "N001");
		double total_supply = 0;
		for (int i = 0; i <= N; i++) {
			total_supply += sp[i];
		}
		snNode.supply = (int) total_supply;
		snNetwork.addNet(snNet);
		snNetwork.addNode(snNode);
		snNode = new SnNodeInfo("Nt", "N001");
		snNode.supply = (int) (-1 * total_supply);
		snNetwork.addNode(snNode);
		SnArcInfo snArc;
		for (int i = 0; i <= N; i++) {
			snNode = new SnNodeInfo("N" + (t * (N + 1) + i), "N001");
			snNode.supply = 0;
			snNetwork.addNode(snNode);
			snNode = new SnNodeInfo("N" + ((t - 1) * (N + 1) + i), "N001");
			snNode.supply = 0;
			snNetwork.addNode(snNode);
		}
		// add the arcs to node 0
		snArc = new SnArcInfo("S" + 0, "N001", "Ns", "N" + (t * (N + 1)));
		snArc.cost = 0;
		snArc.uBound = (int) sp[0];
		if (snArc.uBound > 0)
			snNetwork.addArc(snArc);
		snArc = new SnArcInfo("T" + 0, "N001", "N" + (t * (N + 1)), "Nt");
		snArc.cost = 0.01;
		snArc.uBound = (int) MaxNum;
		if (snArc.uBound > 0)
			snNetwork.addArc(snArc);
		snArc = new SnArcInfo("ST", "N001", "Ns", "Nt");
		snArc.cost = 0;
		snArc.uBound = (int) MaxNum;
		snNetwork.addArc(snArc);

		ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1)));
		while (arc != null) {
			snArc = new SnArcInfo(arc.getKey(), "N001", arc.getFromNodeKey(), arc.getToNodeKey());
			snArc.cost = arc.cost;
			snArc.uBound = (int) (arc.cap_up - arc.flow);
			if (snArc.uBound > 0)
				snNetwork.addArc(snArc);
			snArc = new SnArcInfo(arc.getKey() + "_r", "N001", arc.getToNodeKey(), arc.getFromNodeKey());
			snArc.cost = -arc.cost;
			snArc.uBound = (int) arc.flow;
			if (snArc.uBound > 0)
				snNetwork.addArc(snArc);
			arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
		}
		for (int i = 1; i <= N; i++) {
			double q_left = 0, q_right = 0, u_break = 0;
			double u_left = 0, u_right = 0;
			for (int j = 0; j < qq[i - 1].length; j++) {
				u_break += uu[i - 1][j];
				if (u_break > sp[i]) {
					q_left = q_right = qq[i - 1][j];
					u_left = sp[i] - u_break + uu[i - 1][j];
					u_right = u_break - sp[i];
					break;
				} else if (u_break == sp[i]) {
					q_left = qq[i - 1][j];
					q_right = qq[i - 1][j + 1];
					u_left = uu[i - 1][j];
					u_right = uu[i - 1][j + 1];
					break;
				}
			}
			snArc = new SnArcInfo("S" + i, "N001", "Ns", "N" + (t * (N + 1) + i));
			snArc.cost = -q_left;
			snArc.uBound = (int) u_left;
			if (snArc.uBound > 0)
				snNetwork.addArc(snArc);
			snArc = new SnArcInfo("T" + i, "N001", "N" + (t * (N + 1) + i), "Nt");
			snArc.cost = q_right + 0.01;
			snArc.uBound = (int) u_right;
			if (snArc.uBound > 0)
				snNetwork.addArc(snArc);

			arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) + i));
			while (arc != null) {
				snArc = new SnArcInfo(arc.getKey(), "N001", arc.getFromNodeKey(), arc.getToNodeKey());
				snArc.cost = arc.cost;
				snArc.uBound = (int) (arc.cap_up - arc.flow);
				if (snArc.uBound > 0)
					snNetwork.addArc(snArc);
				snArc = new SnArcInfo(arc.getKey() + "_r", "N001", arc.getToNodeKey(), arc.getFromNodeKey());
				snArc.cost = -arc.cost;
				snArc.uBound = (int) arc.flow;
				if (snArc.uBound > 0)
					snNetwork.addArc(snArc);
				arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
			}
		}

		snArc = (SnArcInfo) snNetwork.getFirstArcIn("N001");
		while (snArc != null) {
			if (debug)
				System.out.println(snArc.getKey() + "\t" + snArc.getFromNodeKey() + "\t" + snArc.getToNodeKey() + "\t"
						+ snArc.cost + "\t" + snArc.uBound);
			snArc = (SnArcInfo) snNetwork.getNextArcFrom(snArc.getKey());
		}
		SnNodeInfo artNode = snNetwork.bigMStart("N001", 100);
		snNetwork.snAlgorithm("N001", 300, 200);
		if (debug)
			debugOut.println("snNetwork.getObjfcn\t:" + snNetwork.getObjfcn());
		if (snNetwork.getObjfcn() < -0.1) {
			for (int i = 0; i <= N; i++) {
				arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + (t * (N + 1) + i));
				if (arc != null)
					sp[i] = 0;
				while (arc != null) {
					double f1 = 0, f2 = 0;
					if ((SnArcInfo) snNetwork.getArc(arc.getKey()) != null)
						f1 = ((SnArcInfo) snNetwork.getArc(arc.getKey())).flow;
					if ((SnArcInfo) snNetwork.getArc(arc.getKey() + "_r") != null)
						f2 = ((SnArcInfo) snNetwork.getArc(arc.getKey() + "_r")).flow;
					arc.flow += (double) (f1 - f2);
					sp[i] += arc.flow;
					temp_temp_sol += arc.flow * arc.cost;
					arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
				}
				if (debug)
					debugOut.println("supply " + (t) + "_" + i + ":\t" + sp[i]);
			}
			// go back to update q and u
			if (debug)
				debugOut.println("go back to update q and u");
		} else {
			temp_temp_sol = Double.MAX_VALUE;
			if (debug)
				debugOut.println("find optimal gradient.");
		}

		return temp_temp_sol;
	}

	public double findDirection(Network myNetwork, IloCplex cplex, int t, double range, double rh_thread, double sp[],
			Vector vector, Hashtable indexTable2, double spp[], double q[][], double u[][]) throws Exception {
		if (debug)
			debugOut.println("t=:\t" + t);
		double temp_sol = 0;
		int numArc = 0;
		int numYarc = 0; // number of y
		int N = myNetwork.N;
		for (int i = 0; i <= N; i++) {
			numArc += myNetwork.getNumOutArcOf("N" + (t * (N + 1) + i));
			if (i > 0)
				numYarc += q[i - 1].length;
		}
		IloNumVar[] x = new IloNumVar[numArc];
		IloNumVar[] y = new IloNumVar[numYarc];
		IloLinearNumExpr obj_exp = cplex.linearNumExpr();
		int k = 0;
		Hashtable indexTable = new Hashtable();
		for (int i = 0; i <= N; i++) {
			ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" + (t * (N + 1) + i));
			while (arc != null) {
				double lw = Math.max(arc.pre_flow - range, arc.cap_low);
				double up = Math.min(arc.pre_flow + range, arc.cap_up);
				x[k] = cplex.numVar(lw, up, "x" + k);
				obj_exp.addTerm(arc.cost, x[k]);
				indexTable.put(arc.getKey(), new Integer(k));
				k++;
				arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
			}
		}
		k = 0;
		for (int i = 1; i <= N; i++) {
			for (int j = 0; j < q[i - 1].length; j++) {
				y[k] = cplex.numVar(0, MaxNum + 1, "y" + k);
				obj_exp.addTerm(q[i - 1][j], y[k]);
				k++;
			}
		}
		cplex.addMinimize(obj_exp);
		IloRange rng[] = new IloRange[2 * N];
		IloRange stng[] = new IloRange[numYarc];
		k = 0;
		for (int i = 1; i <= N; i++) {
			IloLinearNumExpr exp = cplex.linearNumExpr();
			ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" + (t * (N + 1) + i));
			while (arc != null) {
				int j = ((Integer) indexTable.get(arc.getKey())).intValue();
				exp.addTerm(1, x[j]);
				arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
			}
			rng[i - 1] = cplex.addEq(exp, sp[i], "N" + (t * (N + 1) + i));
		}

		k = 0;
		for (int i = 1; i <= N; i++) {
			IloLinearNumExpr exp = cplex.linearNumExpr();
			ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
			while (arc != null) {
				int j = ((Integer) indexTable.get(arc.getKey())).intValue();
				exp.addTerm(1, x[j]);
				arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
			}
			for (int j = 0; j < q[i - 1].length; j++) {
				exp.addTerm(-1, y[k]);
				k++;
			}
			rng[N + i - 1] = cplex.addEq(exp, 0, "N" + ((t + 1) * (N + 1) + i));
		}

		// add y constraint
		k = 0;
		for (int i = 1; i <= N; i++) {
			for (int j = 0; j < q[i - 1].length; j++) {
				IloLinearNumExpr exp = cplex.linearNumExpr();
				exp.addTerm(1, y[k]);
				stng[k] = cplex.addLe(exp, u[i - 1][j], "Y" + t + "_" + i + "_" + j);
				k++;
			}
		}
		for (int j = 0; j < vector.size(); j++) {
			IloLinearNumExpr exp = cplex.linearNumExpr();
			double rh = 0;
			Hashtable table = (Hashtable) vector.get(j);
			k = 0;
			double disqure = 0;
			for (int i = 0; i <= N; i++) {
				ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" + (t * (N + 1) + i));
				while (arc != null) {
					double di = ((Double) table.get(arc.getKey())).doubleValue() - arc.pre_flow;
					if (di > 0) {
						exp.addTerm(di, x[k]);
						rh += di * arc.pre_flow;
						disqure += Math.abs(di);
					}
					k++;
					arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
				}
			}
			rh += rh_thread * disqure;
			cplex.addLe(exp, rh);
		}

		if (debug && false) {
			debugOut.println("origin solution");
			for (int i = 0; i <= N; i++) {
				ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
				double ts = 0;
				while (arc != null) {
					debugOut.print("\t" + arc.getKey() + ":\t " + arc.pre_flow);
					ts += arc.pre_flow;
					arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
				}
				debugOut.println("\ttotal \t " + ts);
			}
			debugOut.println("origin direction");
			for (int j = 0; j < vector.size(); j++) {
				Hashtable table = (Hashtable) vector.get(j);
				debugOut.println("vector " + j);
				for (int i = 0; i <= N; i++) {
					ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
					double ts = 0;
					while (arc != null) {
						debugOut.print("\t" + arc.getKey() + ":\t " + ((Double) table.get(arc.getKey())).doubleValue());
						ts += ((Double) table.get(arc.getKey())).doubleValue();
						arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
					}
					debugOut.println("\ttotal \t " + ts);
				}
			}
		}

		// if(debug) cplex.exportModel(".\\temp.lp");
		if (cplex.solve()) {
			temp_sol = cplex.getObjValue();
			indexTable2.clear();
			// if(debug) debugOut.println("new direction");
			for (int i = 0; i <= N; i++) {
				ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
				spp[i] = 0;
				while (arc != null) {
					int j = ((Integer) indexTable.get(arc.getKey())).intValue();
					arc.flow = cplex.getValue(x[j]);
					// if(debug) debugOut.print("\t"+arc.getKey()+":\t "+arc.flow);
					spp[i] += cplex.getValue(x[j]);
					indexTable2.put(arc.getKey(), new Double(arc.flow));
					arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
				}
				// if (debug) debugOut.println("supply " + (t + 1) + "_" + i + ":\t" + spp[i]);
			}
		}
		cplex.clearModel();
		return temp_sol;
	}

	public double updateSupply(Network myNetwork, IloCplex cplex, int t, double sp[], double spp[], double q[][],
			double u[][]) throws Exception {
		if (debug)
			debugOut.println("t=:\t" + t);
		double temp_sol = 0;
		int numArc = 0;
		int numYarc = 0; // number of y
		int N = myNetwork.N;
		for (int i = 0; i <= N; i++) {
			numArc += myNetwork.getNumOutArcOf("N" + (t * (N + 1) + i));
			if (i > 0)
				numYarc += q[i - 1].length;
		}
		IloNumVar[] x = new IloNumVar[numArc];
		IloNumVar[] y = new IloNumVar[numYarc];
		IloLinearNumExpr obj_exp = cplex.linearNumExpr();
		int k = 0;
		Hashtable indexTable = new Hashtable();
		for (int i = 0; i <= N; i++) {
			ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" + (t * (N + 1) + i));
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
			for (int j = 0; j < q[i - 1].length; j++) {
				y[k] = cplex.numVar(0, MaxNum + 1, "y" + k);
				obj_exp.addTerm(q[i - 1][j], y[k]);
				k++;
			}
		}
		cplex.addMinimize(obj_exp);
		IloRange rng[] = new IloRange[2 * N];
		IloRange stng[] = new IloRange[numYarc];
		k = 0;
		for (int i = 1; i <= N; i++) {
			IloLinearNumExpr exp = cplex.linearNumExpr();
			ArcInfo arc = (ArcInfo) myNetwork.getFirstOutArcOf("N" + (t * (N + 1) + i));
			while (arc != null) {
				int j = ((Integer) indexTable.get(arc.getKey())).intValue();
				exp.addTerm(1, x[j]);
				arc = (ArcInfo) myNetwork.getNextOutArcFrom(arc.getKey());
			}
			rng[i - 1] = cplex.addEq(exp, sp[i], "N" + (t * (N + 1) + i));
		}

		k = 0;
		for (int i = 1; i <= N; i++) {
			IloLinearNumExpr exp = cplex.linearNumExpr();
			ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
			while (arc != null) {
				int j = ((Integer) indexTable.get(arc.getKey())).intValue();
				exp.addTerm(1, x[j]);
				arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
			}
			for (int j = 0; j < q[i - 1].length; j++) {
				exp.addTerm(-1, y[k]);
				k++;
			}
			rng[N + i - 1] = cplex.addEq(exp, 0, "N" + ((t + 1) * (N + 1) + i));
		}

		// add y constraint
		k = 0;
		for (int i = 1; i <= N; i++) {
			for (int j = 0; j < q[i - 1].length; j++) {
				IloLinearNumExpr exp = cplex.linearNumExpr();
				exp.addTerm(1, y[k]);
				stng[k] = cplex.addLe(exp, u[i - 1][j], "Y" + t + "_" + i + "_" + j);
				k++;
			}
		}
		if (cplex.solve()) {
			for (int i = 0; i <= N; i++) {
				ArcInfo arc = (ArcInfo) myNetwork.getFirstInArcOf("N" + ((t + 1) * (N + 1) + i));
				spp[i] = 0;
				while (arc != null) {
					int j = ((Integer) indexTable.get(arc.getKey())).intValue();
					arc.pre_flow = arc.flow;
					arc.flow = cplex.getValue(x[j]);
					spp[i] += cplex.getValue(x[j]);
					temp_sol += arc.cost * cplex.getValue(x[j]);
					arc = (ArcInfo) myNetwork.getNextInArcFrom(arc.getKey());
				}
				if (debug)
					debugOut.println("supply " + (t + 1) + "_" + i + ":\t" + spp[i]);
			}
		}
		cplex.clearModel();
		return temp_sol;
	}

}