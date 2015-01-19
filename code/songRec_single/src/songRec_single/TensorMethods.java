package songRec_single;

import java.io.*;
import java.util.Random;

// Referenced classes of package sals.single:
//            Tensor

public class TensorMethods
{

    public TensorMethods()
    {
    }

    public static Tensor importSparseTensor(String path, String delim, int modeLengths[], int modesIdx[], int valIdx, int permutedIdx[][])
        throws IOException
    {
        int omega = 0;
        BufferedReader br = new BufferedReader(new FileReader(path));
        do
        {
            String line = br.readLine();
            if(line != null)
            {
                omega++;
            } else
            {
                br.close();
                return importSparseTensor(path, delim, omega, modeLengths, modesIdx, valIdx, permutedIdx);
            }
        } while(true);
    }

    public static Tensor importSparseTensor(String path, String delim, int omega, int modeLengths[], int modesIdx[], int valIdx, int permutedIdx[][])
        throws IOException
    {
        int N = modesIdx.length;
        int indices[][] = new int[N][omega];
        float values[] = new float[omega];
        BufferedReader br = new BufferedReader(new FileReader(path));
        float sum = 0.0F;
        int i = 0;
        do
        {
            String line = br.readLine();
            if(line != null)
            {
                String tokens[] = line.split(delim);
                if(permutedIdx != null)
                {
                    for(int n = 0; n < N; n++)
                        indices[n][i] = permutedIdx[n][Integer.valueOf(tokens[modesIdx[n]]).intValue()];

                } else
                {
                    for(int n = 0; n < N; n++)
                        indices[n][i] = Integer.valueOf(tokens[modesIdx[n]]).intValue();

                }
                values[i] = Float.valueOf(tokens[valIdx]).floatValue();
                sum += values[i];
                i++;
            } else
            {
                br.close();
                return new Tensor(N, modeLengths, omega, indices, values, sum);
            }
        } while(true);
    }

    public static Tensor createBinaryTensor(int omega, int modeLengths[])
    {
        int N = modeLengths.length;
        int indices[][] = new int[N][omega];
        float values[] = new float[omega];
        Random random = new Random();
        float sum = 0.0F;
        for(int i = 0; i < omega; i++)
        {
            for(int n = 0; n < N; n++)
                indices[n][i] = random.nextInt(modeLengths[n]);

            values[i] = 1.0F;
            sum++;
        }

        return new Tensor(N, modeLengths, omega, indices, values, sum);
    }

    public static int[][] cardinality(Tensor tensor)
    {
        int dimension = tensor.N;
        int modeSizes[] = tensor.modeLengths;
        int cardinality[][] = new int[dimension][];
        for(int dim = 0; dim < dimension; dim++)
            cardinality[dim] = new int[modeSizes[dim]];

        for(int i = 0; i < tensor.omega; i++)
        {
            for(int dim = 0; dim < dimension; dim++)
                cardinality[dim][tensor.indices[dim][i]]++;

        }

        return cardinality;
    }
}

