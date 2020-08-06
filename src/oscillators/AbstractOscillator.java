package oscillators;

import java.util.List;
import myLib.rungeKutta.*;
import myLib.utils.Utils;

/**
 * 調和振動子
 *
 * @author tadaki
 */
public class AbstractOscillator {

    protected double t;//独立変数
    protected double y[];//従属変数
    protected double yInit[];//従属変数の初期値
    protected DifferentialEquation equation;//連立微分方程式
    protected final int numVar;//従属変数の数

    /**
     * コンストラクタ
     *
     * @param x 振幅の初期値
     * @param v 速度の初期値
     */
    public AbstractOscillator(double x, double v) {
        numVar = 2;
        t = 0;
        yInit = new double[numVar];
        yInit[0] = x;
        yInit[1] = v;
        initialize();
    }

    /**
     * 全ての変数を再初期化する
     *
     * @return 初期化された従属変数の値
     */
    public final double[] initialize() {
        System.arraycopy(yInit, 0, y, 0, numVar);
        t = 0;
        return y;
    }

    /**
     * 時間発展：1ステップ
     *
     * @param dt 時間
     * @return 新しい状態での従属変数の値
     */
    public double[] update(double dt) {
        double yy[] = RungeKutta.rk4(t, y, dt, equation);
        System.arraycopy(yy, 0, y, 0, numVar);
        t += dt;
        return yy;
    }

    public List<OscillatorState> evolution(double tmax, int nstep) {
        double dt = tmax / nstep;
        List<OscillatorState> timeSequence = Utils.createList();
        for (int i = 0; i < nstep; i++) {
            update(dt);
            timeSequence.add(new OscillatorState(t, y[0], y[1]));
        }
        return timeSequence;
    }

}
