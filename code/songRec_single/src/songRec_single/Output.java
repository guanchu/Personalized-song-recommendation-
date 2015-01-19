package songRec_single;

import java.io.*;

//Referenced classes of package sals.single:
//         Tensor

public class Output
{

 public Output()
 {
 }

 public static void calculateEstimate(Tensor queryTensor, float params[][][], int N, int K)
 {
     for(int elemIdx = 0; elemIdx < queryTensor.omega; elemIdx++)
     {
         float predict = 0.0F;
         for(int k = 0; k < K; k++)
         {
             float product = 1.0F;
             for(int n = 0; n < N; n++)
                 product *= params[n][queryTensor.indices[n][elemIdx]][k];

             predict += product;
         }

         queryTensor.values[elemIdx] = predict;
     }

 }

 public static void calculateEstimate(Tensor queryTensor, float mu, float bias[][], float params[][][], int N, int K)
 {
     for(int elemIdx = 0; elemIdx < queryTensor.omega; elemIdx++)
     {
         float predict = mu;
         for(int n = 0; n < N; n++)
             predict += bias[n][queryTensor.indices[n][elemIdx]];

         for(int k = 0; k < K; k++)
         {
             float product = 1.0F;
             for(int n = 0; n < N; n++)
                 product *= params[n][queryTensor.indices[n][elemIdx]][k];

             predict += product;
         }

         queryTensor.values[elemIdx] = predict;
     }

 }

 public static void writeEstimate(String outputDir, Tensor query, int N)
     throws IOException
 {
     BufferedWriter bw = new BufferedWriter(new FileWriter((new StringBuilder(String.valueOf(outputDir))).append(File.separator).append("estimate.out").toString()));
     for(int elemIdx = 0; elemIdx < query.omega; elemIdx++)
     {
         for(int dim = 0; dim < N; dim++)
             bw.write((new StringBuilder(String.valueOf(query.indices[dim][elemIdx]))).append(",").toString());

         bw.write((new StringBuilder(String.valueOf(query.values[elemIdx]))).append("\n").toString());
     }

     bw.close();
 }

 public static void writeEstimate(String outputDir, Tensor query, int permutedIdx[][], int N)
     throws IOException
 {
     int invertedIdx[][] = new int[N][];
     for(int dim = 0; dim < N; dim++)
     {
         invertedIdx[dim] = new int[permutedIdx[dim].length];
         for(int i = 0; i < permutedIdx[dim].length; i++)
             invertedIdx[dim][permutedIdx[dim][i]] = i;

     }

     BufferedWriter bw = new BufferedWriter(new FileWriter((new StringBuilder(String.valueOf(outputDir))).append(File.separator).append("estimate.out").toString()));
     for(int elemIdx = 0; elemIdx < query.omega; elemIdx++)
     {
         for(int dim = 0; dim < N; dim++)
             bw.write((new StringBuilder(String.valueOf(invertedIdx[dim][query.indices[dim][elemIdx]]))).append(",").toString());

         bw.write((new StringBuilder(String.valueOf(query.values[elemIdx]))).append("\n").toString());
     }

     bw.close();
 }

 public static void writePerformance(String outputDir, double performance[][], int Tout)
     throws IOException
 {
     BufferedWriter bw = new BufferedWriter(new FileWriter((new StringBuilder(String.valueOf(outputDir))).append(File.separator).append("performance.out").toString()));
     bw.write("iteration,elapsed_time,training_rmse,test_rmse");
     for(int outIter = 0; outIter < Tout; outIter++)
         bw.write(String.format("%d,%d,%f,%f\n", new Object[] {
             Integer.valueOf((int)performance[outIter][0]), Integer.valueOf((int)performance[outIter][1]), Double.valueOf(performance[outIter][2]), Double.valueOf(performance[outIter][3])
         }));

     bw.close();
 }

 public static void writePerformanceTimeOnly(String outputDir, double performance[][], int Tout)
     throws IOException
 {
     BufferedWriter bw = new BufferedWriter(new FileWriter((new StringBuilder(String.valueOf(outputDir))).append(File.separator).append("performance.out").toString()));
     bw.write("iteration,elapsed_time");
     for(int outIter = 0; outIter < Tout; outIter++)
         bw.write(String.format("%d,%d\n", new Object[] {
             Integer.valueOf((int)performance[outIter][0]), Integer.valueOf((int)performance[outIter][1])
         }));

     bw.close();
 }

 public static void writeFactorMatrices(String outputDir, float params[][][])
     throws IOException
 {
     outputDir = (new StringBuilder(String.valueOf(outputDir))).append(File.separator).append("factor_matrices").toString();
     int N = params.length;
     File file = new File(outputDir);
     if(file.exists())
         file.delete();
     file.mkdir();
     for(int n = 0; n < N; n++)
     {
         BufferedWriter bw = new BufferedWriter(new FileWriter((new StringBuilder(String.valueOf(outputDir))).append(File.separator).append(n + 1).toString()));
         int modeLength = params[n].length;
         int K = params[n][0].length;
         for(int i = 0; i < modeLength; i++)
         {
             StringBuffer buffer = new StringBuffer();
             buffer.append(i);
             buffer.append(",");
             for(int k = 0; k < K - 1; k++)
             {
                 buffer.append(params[n][i][k]);
                 buffer.append(",");
             }

             buffer.append(params[n][i][K - 1]);
             buffer.append("\n");
             bw.write(buffer.toString());
         }

         bw.close();
     }

 }

 public static void writeFactorMatrices(String outputDir, float params[][][], int permutedIdx[][])
     throws IOException
 {
     outputDir = (new StringBuilder(String.valueOf(outputDir))).append(File.separator).append("factor_matrices").toString();
     int N = params.length;
     File file = new File(outputDir);
     if(file.exists())
         file.delete();
     file.mkdir();
     for(int n = 0; n < N; n++)
     {
         BufferedWriter bw = new BufferedWriter(new FileWriter((new StringBuilder(String.valueOf(outputDir))).append(File.separator).append(n + 1).toString()));
         int modeLength = params[n].length;
         int K = params[n][0].length;
         for(int i = 0; i < modeLength; i++)
         {
             StringBuffer buffer = new StringBuffer();
             int rowIndex = permutedIdx[n][i];
             buffer.append(i);
             buffer.append(",");
             for(int k = 0; k < K - 1; k++)
             {
                 buffer.append(params[n][rowIndex][k]);
                 buffer.append(",");
             }

             buffer.append(params[n][rowIndex][K - 1]);
             buffer.append("\n");
             bw.write(buffer.toString());
         }

         bw.close();
     }

 }

 public static void writeBiases(String outputDir, float bias[][])
     throws IOException
 {
     outputDir = (new StringBuilder(String.valueOf(outputDir))).append(File.separator).append("bias_terms").toString();
     int N = bias.length;
     File file = new File(outputDir);
     if(file.exists())
         file.delete();
     file.mkdir();
     for(int n = 0; n < N; n++)
     {
         BufferedWriter bw = new BufferedWriter(new FileWriter((new StringBuilder(String.valueOf(outputDir))).append(File.separator).append(n + 1).toString()));
         int modeLength = bias[n].length;
         for(int i = 0; i < modeLength; i++)
         {
             StringBuffer buffer = new StringBuffer();
             buffer.append(i);
             buffer.append(",");
             buffer.append(bias[n][i]);
             buffer.append("\n");
             bw.write(buffer.toString());
         }

         bw.close();
     }

 }

 public static void writeBiases(String outputDir, float bias[][], int permutedIdx[][])
     throws IOException
 {
     outputDir = (new StringBuilder(String.valueOf(outputDir))).append(File.separator).append("bias_terms").toString();
     int N = bias.length;
     File file = new File(outputDir);
     if(file.exists())
         file.delete();
     file.mkdir();
     for(int n = 0; n < N; n++)
     {
         BufferedWriter bw = new BufferedWriter(new FileWriter((new StringBuilder(String.valueOf(outputDir))).append(File.separator).append(n + 1).toString()));
         int modeLength = bias[n].length;
         for(int i = 0; i < modeLength; i++)
         {
             StringBuffer buffer = new StringBuffer();
             int rowIndex = permutedIdx[n][i];
             buffer.append(i);
             buffer.append(",");
             buffer.append(bias[n][rowIndex]);
             buffer.append("\n");
             bw.write(buffer.toString());
         }

         bw.close();
     }

 }

 public static void writeMU(String outputDir, float mu)
     throws IOException
 {
     outputDir = (new StringBuilder(String.valueOf(outputDir))).append(File.separator).append("mu").toString();
     BufferedWriter bw = new BufferedWriter(new FileWriter(outputDir));
     bw.write((new StringBuilder()).append(mu).toString());
     bw.write("\n");
     bw.close();
 }
}