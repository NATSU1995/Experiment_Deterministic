\ENCODING=ISO-8859-1
\Problem name: ilog.cplex

Minimize
 obj: 1.48 x0 + 2.21 x1 + 3.81 x2 + 3.43 x3 + 4.27 y0 + 3.27 y1
Subject To
 N1:     x2  = 39
 N2:     x3  = 39
 N4:     x0 + x3 - y0  = 0
 N5:     x1 + x2 - y1  = 0
 Y0_1_0: y0 <= 16
 Y0_2_0: y1 <= 16
Bounds
 0 <= x0 <= 1000
 0 <= x1 <= 1000
 0 <= x2 <= 6
 0 <= x3 <= 6
 0 <= y0 <= 1001
 0 <= y1 <= 1001
End
