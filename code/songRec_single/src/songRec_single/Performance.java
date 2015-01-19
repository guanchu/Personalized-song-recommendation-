package songRec_single;

public class Performance
{

    public Performance()
    {
    }

    public static double computeRMSE(final Tensor tensor, final float params[][][], final int N, final int K, final int M)
    {
        final double loss[] = new double[1];
        
        return Math.sqrt(loss[0] / (double)tensor.omega);
    }

    public static double computeRMSE(final Tensor tensor, final float params[][][], final float bias[][], final float mu, final int N, final int K, final int M)
    {
        final double loss[] = new double[1];
        
        return Math.sqrt(loss[0] / (double)tensor.omega);
    }

    private static int[] blockIndex(int n, int m, int i)
    {
        int result[] = new int[2];
        result[0] = (int)Math.ceil((((double)n + 0.0D) * (double)i) / (double)m);
        result[1] = (int)Math.ceil((((double)n + 0.0D) * (double)(i + 1)) / (double)m) - 1;
        return result;
    }

}
