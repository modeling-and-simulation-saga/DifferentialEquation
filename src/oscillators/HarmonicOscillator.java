package oscillators;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import myLib.utils.FileIO;

/**
 * 調和振動子
 *
 * @author tadaki
 */
public class HarmonicOscillator extends AbstractOscillator {

    private final double omega;//角速度

    /**
     * コンストラクタ
     *
     * @param x 振幅の初期値
     * @param v 速度の初期値
     * @param k k/mに相当
     */
    public HarmonicOscillator(double x, double v, double k) {
        super(x, v);//スーパークラスによる初期化
        this.omega = Math.sqrt(k);
        //微分方程式の記述
        equation = (double xx, double[] yy) -> {
            double dy[] = new double[numVar];


            return dy;
        };
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
    	double x0 = 0.;
    	double v0 = 1.;
    	double k = 1.;// k/mに相当
        HarmonicOscillator sys = new HarmonicOscillator(x0,v0,k);
        double t = 50.;
        int nstep = 10000;
        // 時間50を10000に区分して、積分を実行
        // 結果を(t,x)のリストで得る
        List<OscillatorState> points = sys.evolution(t, nstep);
        // 結果をファイルへ出力
        String filename =HarmonicOscillator.class.getSimpleName()
                +"-output.txt";
        try (BufferedWriter out = FileIO.openWriter(filename)) {
            for (OscillatorState p : points) {
                FileIO.writeSSV(out, p.t, p.x);
            }
        }
    }

}
