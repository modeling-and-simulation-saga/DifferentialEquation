package coupledOscillators;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import oscillators.OscillatorState;
import myLib.rungeKutta.DifferentialEquation;
import myLib.rungeKutta.RungeKutta;
import myLib.utils.FileIO;
import myLib.utils.Utils;

/**
 * 調和振動子
 *
 * @author tadaki
 */
public class CoupledOscillators {

    private final int numOscillators;
    private final double y[];//各振動子の変位と速度
    private final DifferentialEquation equation;
    private double t;
    private double b;

    /**
     * コンストラクタ
     *
     * @param oscillators
     * @param k ばね定数/質量
     * @param b ばねの自然長
     */
    public CoupledOscillators(OscillatorState oscillators[],
            double k, double b) {
        this.b = b;
        numOscillators = oscillators.length;//振動子の数
        y = new double[2 * numOscillators];//従属変数
        t = oscillators[0].t;//独立変数の初期値
        //偶数番目の従属変数は変位、奇数番目は速度
        //初期値設定
        for (int i = 0; i < numOscillators; i++) {
            y[2 * i] = oscillators[i].x;//変位
            y[2 * i + 1] = oscillators[i].v;//速度
        }
        //微分方程式の記述
        equation = (double xx, double[] yy) -> {
            double dy[] = new double[2 * numOscillators];
            //0 番の粒子
            {
                int i = 0;
                int j = 2 * i;
                dy[j] = yy[j + 1];
                dy[j + 1] = -k * (2 * yy[j] - yy[j + 2]);
            }
            //1番からn-2番の粒子
            for (int i = 1; i < numOscillators - 1; i++) {
                int j = 2 * i;


            }
            //n-1番の粒子
            {
                int i = numOscillators - 1;
                int j = 2 * i;
                dy[j] = yy[j + 1];
                dy[j + 1] = -k * (-yy[j - 2] + 2 * yy[j]);
            }
            return dy;
        };
    }

    /**
     * 時間発展：1ステップ
     *
     * @param dt 時間
     * @return 更新後の従属変数の値
     */
    public OscillatorState[] update(double dt) {
        //更新後の従属変数の値
        double yy[] = RungeKutta.rk4(t, y, dt, equation);
        //値の更新
        for (int i = 0; i < 2 * numOscillators; i++) {
            y[i] = yy[i];
        }
        t += dt;
        //現在の状態をOscillatorとして返す
        return getOscillators();
    }

    /**
     * 時間間隔とステップ数を指定して時間発展
     *
     * @param period 時間間隔
     * @param nstep ステップ数
     * @return
     */
    public List<OscillatorState[]> evolution(double period, int nstep) {
        List<OscillatorState[]> history = Utils.createList();
        double dt = period / nstep;
        for (int i = 0; i < nstep; i++) {
            OscillatorState[] osc = update(dt);
            history.add(osc);
        }
        return history;
    }

    /**
     * 時間間隔とステップ数を指定して時間発展
     *
     * @param period 時間間隔
     * @param nstep ステップ数
     * @param tic 値を返すステップ間隔
     * @return
     */
    public List<OscillatorState[]> evolution(double period, int nstep, int tic) {
        List<OscillatorState[]> history = Utils.createList();
        double dt = period / nstep;
        for (int i = 0; i < nstep; i++) {
            OscillatorState[] o = update(dt);
            if (i % tic == 0) {
                history.add(o);
            }
        }
        return history;
    }

    /**
     * 現在の状態をOscillatorStateとして返す
     *
     * @return
     */
    public OscillatorState[] getOscillators() {
        OscillatorState oscillators[]
                = new OscillatorState[numOscillators];
        for (int i = 0; i < numOscillators; i++) {
            oscillators[i]
                    = new OscillatorState(t, y[2 * i], y[2 * i + 1]);
        }
        return oscillators;
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        //3個の連成振動
        double t0 = 0.;
        OscillatorState[] initialOscillators = {
            new OscillatorState(t0, 1., 0.),
            new OscillatorState(t0, 2., 0.),
            new OscillatorState(t0, -1., 0.)
        };
        double b = 10.;
        double k = 1.;
        CoupledOscillators sys
                = new CoupledOscillators(initialOscillators, k, b);
        // 時間40を20000に区分して、積分を実行
        double period = 40.;
        int nstep = 20000;
        // 結果は200ステップ毎に取得
        int tic = 200;
        List<OscillatorState[]> history
                = sys.evolution(period, nstep, tic);
        //出力ファイル名の指定
        String filename = CoupledOscillators.class.getSimpleName()
                + "-output.txt";
        // 結果をファイルへ出力
        try ( BufferedWriter out = FileIO.openWriter(filename)) {
            for (OscillatorState[] oscillators : history) {
                StringBuilder sb = new StringBuilder();
                FileIO.writeSSV(out,
                        oscillators[0].t,//時刻
                        oscillators[0].x,//振動子１
                        oscillators[1].x,//振動子２
                        oscillators[2].x//振動子３
                );
            }
        }
    }

}
