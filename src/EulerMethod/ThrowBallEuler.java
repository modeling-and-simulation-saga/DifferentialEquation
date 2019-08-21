package EulerMethod;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import myLib.rungeKutta.DifferentialEquation;
import myLib.utils.FileIO;
import myLib.utils.Utils;

/**
 * Euler法による重力中の投げ上げ
 *
 * @author tadaki
 */
public class ThrowBallEuler {

   private double g;//重力加速度
    protected double y[];//従属変数
    protected double yInit[];//従属変数の初期値
    private double t;
    private final DifferentialEquation equation;//連立微分方程式
    private final int numVar;//変数の数

    /**
     * コンストラクタ
     *
     * @param xi 水平方向の初期位置
     * @param yi 鉛直方向の初期位置（上が正）
     * @param vx 水平方向の初期速度
     * @param vy 鉛直方向の初期速度
     * @param g 重力加速度
     */
    public ThrowBallEuler(double xi, double yi,double vx,double vy, double g) {
        numVar = 4;
        yInit = new double[numVar];
        yInit[0] = xi;
        yInit[1] = vx;
        yInit[2] = yi;
        yInit[3] = vy;
        this.g = g;
        //微分方程式の記述
        equation = (double tt, double yy[]) -> {
            double dy[] = new double[numVar];




            return dy;
        };

        y = new double[numVar];
        for (int i = 0; i < numVar; i++) {
            y[i] = yInit[i];
        }
        t = 0;
    }

    /**
     * 時間発展：1ステップ
     *
     * @param dt 時間
     * @return 新しい状態での従属変数の値
     */
    public double[] update(double dt) {
        double yy[] = Euler.euler(t, y, dt, equation);
        for (int i = 0; i < numVar; i++) {
            y[i] = yy[i];
        }
        t += dt;
        return yy;
    }

    /**
     * 時間発展
     *
     * @param h 時間きざみ
     * @param nstep 時間hをnstepに区切って時間発展
     * @return y>0の間の時間発展 (x,y)のリスト
     */
    public List<Point2D.Double> evolution(double h) {
        List<Point2D.Double> points = Utils.createList();
        while(y[2]>=0){
            double yy[] = update(h);
            points.add(new Point2D.Double(yy[0], yy[2]));
        }
        return points;
    }

    /**
     * 独立変数の設定
     *
     * @param t
     */
    public void setT(double t) {
        this.t = t;
    }

    /**
     * 全ての変数を再初期化する
     *
     * @return 初期化された従属変数の値
     */
    public double[] initialize() {
        for (int i = 0; i < numVar; i++) {
            y[i] = yInit[i];
        }
        t = 0;
        return y;
    }

    /**
     * ThrowBallクラスを動かすメイン部分
     *
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        //初期値設定
        double vx = 10.;
        double vy = 10.;
        double g = 9.8;//パラメタ
        ThrowBallEuler sys = new ThrowBallEuler(0.,0.,vx,vy,g);//インスタンス生成
        double h = 0.01;//進める独立変数の幅
        List<Point2D.Double> points = sys.evolution(h);
        //このクラスの名前の文字列
        String className = ThrowBallEuler.class.getSimpleName();
        // 結果をファイルへ出力
        try (
                BufferedWriter out
                = FileIO.openWriter(className + "-output.txt")) {
            for (Point2D.Double p : points) {
                FileIO.writeSSV(out, p.x, p.y);//スペース区切りで出力
            }
        }
    }
}
