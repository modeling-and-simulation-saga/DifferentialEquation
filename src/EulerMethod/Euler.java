package EulerMethod;

import myLib.rungeKutta.DifferentialEquation;

/**
 *　Euler法による微分方程式の積分
 * @author tadaki
 */
public class Euler {

    /**
     * 
     * @param x 独立変数
     * @param y 従属変数
     * @param h 独立変数の変化幅
     * @param eq 連立微分方程式
     * @return x+hにおける従属変数の値
     */
    public static double[] euler(double x, double y[], double h,
            DifferentialEquation eq) {
        int n = y.length;//従属変数の数
        //独立変数がh進んだときの従属変数の値        
        double yt[] = new double[n];
        //独立変数の導関数
        double dy[] = eq.rhs(x, y);

        for (int i = 0; i < n; i++) {
            yt[i] = y[i] + h * dy[i];
        }
        return yt;
    }
}
