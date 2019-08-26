package coupledOscillators;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
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
    private final double y[];
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
    public CoupledOscillators(Oscillator oscillators[], double k, double b) {
        this.b = b;
        numOscillators = oscillators.length;//振動子の数
        y = new double[2 * numOscillators];//従属変数
        t = 0.;//独立変数の初期値
        //偶数番目の従属変数は変位、奇数番目は速度
        //初期値設定
        for (int i = 0; i < numOscillators; i++) {
            y[2 * i] = oscillators[i].y;//変位
            y[2 * i + 1] = oscillators[i].v;//速度
        }
        //微分方程式の記述
        equation = (double xx, double[] yy) -> {
            double dy[] = new double[2 * numOscillators];
            //0 番の粒子
            dy[0] = yy[1];
            dy[1] = -k * (2 * yy[0] - yy[2]);
            //1番からn-2番の粒子
            for (int i = 1; i < numOscillators - 1; i++) {
                int j = 2 * i;
                dy[j] = yy[j + 1];
                dy[j + 1] = -k * (-yy[j - 2] + 2 * yy[j] - yy[j + 2]);
            }
            //n-1番の粒子
            int j = 2 * (numOscillators - 1);
            dy[j] = yy[j + 1];
            dy[j + 1] = -k * (-yy[j - 2] + 2 * yy[j]);
            return dy;
        };
    }

    /**
     * 時間発展：1ステップ
     *
     * @param dt 時間
     * @return 更新後の独立変数の値
     */
    public Oscillator[] update(double dt) {
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
    public List<Oscillator[]> evolution(double period, int nstep) {
        List<Oscillator[]> history = Utils.createList();
        double dt = period / nstep;
        for (int s = 0; s < nstep; s++) {
            Oscillator[] o = update(dt);
            history.add(o);
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
    public List<Oscillator[]> evolution(double period, int nstep, int tic) {
        List<Oscillator[]> history = Utils.createList();
        double dt = period / nstep;
        for (int s = 0; s < nstep; s++) {
            Oscillator[] o = update(dt);
            if (s % tic == 0) {
                history.add(o);
            }
        }
        return history;
    }

    /**
     * 現在の状態をOscillatorとして返す
     *
     * @return
     */
    public Oscillator[] getOscillators() {
        Oscillator oscillators[] = new Oscillator[numOscillators];
        for (int i = 0; i < numOscillators; i++) {
            oscillators[i] = new Oscillator(y[2 * i], y[2 * i + 1], t);
        }
        return oscillators;
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        //3個の連成振動
        Oscillator[] initialOscillators = {
            new Oscillator(1., 0.),
            new Oscillator(2., 0.),
            new Oscillator(-1., 0.)
        };
        double b = 10.;
        double k = 1.;
        CoupledOscillators sys
                = new CoupledOscillators(initialOscillators, k, b);
        // 時間40を20000に区分して、積分を実行
        double period = 40.;
        int nstep = 20000;
        // 結果は200ステップ毎に取得
        int tic =200;
        List<Oscillator[]> history = sys.evolution(period, nstep, tic);
        //出力ファイル名の指定
        String filename = CoupledOscillators.class.getSimpleName()
                + "-output.txt";
        // 結果をファイルへ出力
        try (BufferedWriter out = FileIO.openWriter(filename)) {
            for (Oscillator[] oscillators : history) {
                StringBuilder sb = new StringBuilder();
                double t = oscillators[0].t;
                FileIO.writeSSV(out, t, 
                        oscillators[0].y,//振動子１
                        oscillators[1].y,//振動子２
                        oscillators[2].y//振動子３
                        );
            }
        }
    }

}
