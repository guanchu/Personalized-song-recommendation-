package songRec_single;

import java.util.Random;

public class ArrayMethods
{

    public ArrayMethods()
    {
    }

    public static int[][] copy(int input[][])
    {
        int result[][] = new int[input.length][];
        for(int i = 0; i < input.length; i++)
            result[i] = (int[])input[i].clone();

        return result;
    }

    public static float[][] copy(float input[][])
    {
        float result[][] = new float[input.length][];
        for(int i = 0; i < input.length; i++)
            result[i] = (float[])input[i].clone();

        return result;
    }

    public static double[][] copy(double input[][])
    {
        double result[][] = new double[input.length][];
        for(int i = 0; i < input.length; i++)
            result[i] = (double[])input[i].clone();

        return result;
    }

    public static float[][][] copy(float input[][][])
    {
        float result[][][] = new float[input.length][][];
        for(int i = 0; i < input.length; i++)
            result[i] = copy(input[i]);

        return result;
    }

    public static float[][] createUniformRandomMatrix(int m, int n, float scalarFactor, Random random)
    {
        float matrix[][] = new float[m][n];
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
            {
                matrix[i][j] = random.nextFloat() * scalarFactor;
                if(matrix[i][j] == 0.0F)
                    matrix[i][j] = 1E-005F * scalarFactor;
            }

        }

        return matrix;
    }

    public static float[] createUniformRandomVector(int n, float scalarFactor, Random random)
    {
        float vector[] = new float[n];
        for(int i = 0; i < n; i++)
            vector[i] = (random.nextFloat() + 1E-005F) * scalarFactor;

        return vector;
    }

    public static void reverse(int array[])
    {
        if(array == null)
            return;
        int i = 0;
        for(int j = array.length - 1; j > i; i++)
        {
            int tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
        }

    }

    public static int[] createSequnce(int N)
    {
        int result[] = new int[N];
        for(int i = 0; i < N; i++)
            result[i] = i;

        return result;
    }
}
