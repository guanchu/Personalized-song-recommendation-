package songRec_single;


import java.util.Random;

public class CDTF
{
  public static void main(String[] args)
    throws Exception
  {
    run(args, false);
  }
  
  public static void run(String[] args, boolean useBias)
    throws Exception
  {
    boolean inputError = true;
    try
    {
      System.out.println("========================");
      System.out.println("Start parameter check...");
      System.out.println("========================");
      
      String training = args[0];
      System.out.println("-training: " + training);
      String outputDir = args[1];
      System.out.println("-output: " + outputDir);
      int M = Integer.valueOf(args[2]).intValue();
      System.out.println("-M: " + M);
      int Tout = Integer.valueOf(args[3]).intValue();
      System.out.println("-Tout: " + Tout);
      int Tin = Integer.valueOf(args[4]).intValue();
      System.out.println("-Tin: " + Tin);
      int N = Integer.valueOf(args[5]).intValue();
      System.out.println("-N: " + N);
      int K = Integer.valueOf(args[6]).intValue();
      System.out.println("-K: " + K);
      float lambda = Float.valueOf(args[7]).floatValue();
      System.out.println("-lambda: " + lambda);
      boolean useWeight = Integer.valueOf(args[8]).intValue() > 0;
      System.out.println("-useWeight: " + useWeight);
      int[] modeSizes = new int[N];
      for (int n = 0; n < N; n++)
      {
        modeSizes[n] = Integer.valueOf(args[(9 + n)]).intValue();
        System.out.println("-I" + (n + 1) + ": " + modeSizes[n]);
      }
      String test = null;
      if (args.length > 9 + N)
      {
        test = args[(9 + N)];
        System.out.println("-test: " + test);
      }
      String query = null;
      if (args.length > 10 + N)
      {
        query = args[(10 + N)];
        System.out.println("-query: " + query);
      }
      inputError = false;
      
      double[][] result = null;
      



      int[] modesIdx = ArrayMethods.createSequnce(N);
      
      System.out.println("==============================");
      System.out.println("Start greedy row assignment...");
      System.out.println("==============================");
      
      int[][] permutedIdx = GreedyRowAssignment.run(training, N, modesIdx, modeSizes, M);
      
      Tensor testTensor = null;
      if (test != null) {
        testTensor = TensorMethods.importSparseTensor(test, ",", modeSizes, modesIdx, N, permutedIdx);
      }
      String name = useBias ? "Bias-CDTF" : "CDTF";
      
      System.out.println("=============");
      System.out.println("Start " + name + "...");
      System.out.println("=============");
      
      CDTF method = new CDTF();
      
      Tensor trainingTensor = TensorMethods.importSparseTensor(training, ",", modeSizes, modesIdx, N, permutedIdx);
      if (test != null) {
        method.setTest(testTensor);
      }
      result = method.run(trainingTensor, K, Tout, Tin, M, lambda, useWeight, useBias, true);
      
      System.out.println("=======================");
      System.out.println("Start writing output...");
      System.out.println("=======================");
      
      Output.writePerformance(outputDir, result, Tout);
      Output.writeFactorMatrices(outputDir, method.params, permutedIdx);
      if (useBias)
      {
        Output.writeBiases(outputDir, method.bias, permutedIdx);
        Output.writeMU(outputDir, method.mu);
      }
      if (query != null)
      {
        Tensor queryTensor = TensorMethods.importSparseTensor(query, ",", modeSizes, modesIdx, 0, permutedIdx);
        if (useBias) {
          Output.calculateEstimate(queryTensor, method.mu, method.bias, method.params, N, K);
        } else {
          Output.calculateEstimate(queryTensor, method.params, N, K);
        }
        Output.writeEstimate(outputDir, queryTensor, permutedIdx, N);
      }
      System.out.println("===========");
      System.out.println("Complete!!!");
      System.out.println("===========");
    }
    catch (Exception e)
    {
      if (inputError)
      {
        String fileName = useBias ? "run_single_bias_cdtf.sh" : "run_single_cdtf.sh";
        System.err.println("Usage: " + fileName + " [training] [output] [M] [Tout] [Tin] [N] [K] [lambda] [useWeight] [I1] [I2] ... [IN] [test] [query]");
        e.printStackTrace();
      }
      else
      {
        throw e;
      }
    }
  }
  
  private static float epsilon = 1.0E-012F;
  private Tensor test;
  private float[][][] params;
  private float[][] bias;
  private float mu;
  
  private void setTest(Tensor test)
  {
    this.test = test;
  }
  
  public double[][] run(Tensor training, int K, int Tout, int Tin, final int M, final float lambda, final boolean useWeight, boolean useBias, boolean printLog)
  {
    Random random = new Random();
    if (printLog) {
      System.out.println("iteration,elapsed_time,training_rmse,test_rmse");
    }
    final Tensor R = training.copy();
    
    final int nnzTraining = R.omega;
    boolean useTest = this.test != null;
    
    final int N = R.N;
    final int[] modeLength = R.modeLengths;
    
    final int[][] nnzFiber = TensorMethods.cardinality(R);
    



    final int[][][] division = new int[M][N][];
    int[][] divisionCount = new int[M][N];
    for (int elemIdx = 0; elemIdx < R.omega; elemIdx++) {
      for (int n = 0; n < N; n++)
      {
        int bIndex = Math.min(R.indices[n][elemIdx] * M / modeLength[n], M - 1);
        divisionCount[bIndex][n] += 1;
      }
    }
    for (int m = 0; m < M; m++) {
      for (int n = 0; n < N; n++)
      {
        division[m][n] = new int[divisionCount[m][n]];
        divisionCount[m][n] = 0;
      }
    }
    for (int elemIdx = 0; elemIdx < R.omega; elemIdx++) {
      for (int n = 0; n < N; n++)
      {
    	  int bIndex = Math.min((R.indices[n][elemIdx] * M) / modeLength[n], M - 1);
          division[bIndex][n][divisionCount[bIndex][n]++] = elemIdx;
      }
    }
    
    
    
    this.params = new float[N][][];
    for (int n = 0; n < N; n++) {
      this.params[n] = ArrayMethods.createUniformRandomMatrix(modeLength[n], K, n == 0 ? 0 : 1, random);
    }
    final float[][] updatedColumn = new float[N][];
    for (int n = 0; n < N; n++) {
      updatedColumn[n] = new float[modeLength[n]];
    }
    double[][] result = new double[Tout][4];
    long start = System.currentTimeMillis();
    for (int outIter = 0; outIter < Tout; outIter++)
    {
      for (int k = 0; k < K; k++)
      {
        final int _k = k;
        

//赋值到 updatedColumn

        new MultiThread<Object>()
        {
          public Object runJob(int blockIndex, int threadIndex)
          {
            for (int n = 0; n < N; n++)
            {
              int[] indicies = CDTF.blockIndex(modeLength[n], M, blockIndex);
              int rowStart = indicies[0];
              int rowEnd = indicies[1];
              for (int row = rowStart; row <= rowEnd; row++) {
                updatedColumn[n][row] = CDTF.this.params[n][row][_k];
              }
            }
            return null;
          }
        }.run(M, MultiThread.createJobList(M));
        


//updateR
        new MultiThread<Object>()
        {
          public Object runJob(int blockIndex, int threadIndex)
          {
            int[] indicies = CDTF.blockIndex(nnzTraining, M, blockIndex);
            int startIdx = indicies[0];
            int endIdx = indicies[1];
            
            CDTF.this.updateR(R, updatedColumn, startIdx, endIdx, true);
            
            return null;
          }
        }.run(M, MultiThread.createJobList(M));
        
        
 // 内循环开始
        for (int innerIter = 0; innerIter < Tin; innerIter++) {
          for (int n = 0; n < N; n++)
          {
            final int _n = n;
            
            new MultiThread<Object>()
            {
              public Object runJob(int m, int threadIndex)
              {
                int[] indicies = CDTF.blockIndex(modeLength[_n], M, m);
                int startIdx = indicies[0];
                int endIdx = indicies[1];
                

                CDTF.this.updateFactors(R, division[m][_n], _n, updatedColumn, lambda, useWeight, startIdx, endIdx, nnzFiber[_n]);
                
                return null;
              }
            }.run(M, MultiThread.createJobList(M));
          }
        }
    //内循环结束
        
   //updateR      
        new MultiThread<Object>()
        {
          public Object runJob(int b, int threadIndex)
          {
            int[] indicies = CDTF.blockIndex(nnzTraining, M, b);
            int rowStart = indicies[0];
            int rowEnd = indicies[1];
            CDTF.this.updateR(R, updatedColumn, rowStart, rowEnd, false);
            for (int n = 0; n < N; n++)
            {
              indicies = CDTF.blockIndex(modeLength[n], M, b);
              int startIdx = indicies[0];
              int endIdx = indicies[1];
              for (int idx = startIdx; idx <= endIdx; idx++) {
                CDTF.this.params[n][idx][_k] = updatedColumn[n][idx];
              }
            }
            return null;
          }
        }.run(M, MultiThread.createJobList(M));
      }

      //外循环结束

      
      final double[] loss = new double[1];
      


// innerLoss
      new MultiThread<Object>()
      {
        public Object runJob(int b, int threadIndex)
        {
          double innerLoss = 0.0D;
          

          int[] indicies = CDTF.blockIndex(nnzTraining, M, b);
          int rowStart = indicies[0];
          int rowEnd = indicies[1];
          for (int elemIdx = rowStart; elemIdx <= rowEnd; elemIdx++) {
            innerLoss += R.values[elemIdx] * R.values[elemIdx];
          }
          synchronized (loss)
          {
            loss[0] += innerLoss;
          }
          return null;
        }
      }.run(M, MultiThread.createJobList(M));
      double trainingRMSE = Math.sqrt(loss[0] / nnzTraining);
      double testRMSE = 0.0D;
      if (useTest) {
        testRMSE = useBias ? Performance.computeRMSE(this.test, this.params, this.bias, this.mu, N, K, M) : Performance.computeRMSE(this.test, this.params, N, K, M);
      }
      long elapsedTime = System.currentTimeMillis() - start;
      if (printLog) {
        System.out.printf("%d,%d,%f,%f\n", new Object[] { Integer.valueOf(outIter + 1), Long.valueOf(elapsedTime), Double.valueOf(trainingRMSE), Double.valueOf(testRMSE) });
      }
      result[outIter][0] = outIter + 1 ;
      result[outIter][1] = elapsedTime;
      result[outIter][2] = trainingRMSE;
      result[outIter][3] = testRMSE;
    }
    return result;
  }
  
  public void updateFactors(Tensor R, int[] Rindex, int n, float[][] updatedColumn, float lambda, boolean useWeight, int firstRow, int lastRow, int[] nnzFiber)
  {
    int dimension = updatedColumn.length;
    int numberOfRows = lastRow - firstRow + 1;
    float[] numerators = new float[numberOfRows];
    float[] denominators = new float[numberOfRows];
    for (int idx = 0; idx < Rindex.length; idx++)
    {
      int elemIdx = Rindex[idx];
      float numerator = 1.0F;
      float denominator = 1.0F;
      for (int i = 0; i < dimension; i++) {
        if (i != n) {
          numerator *= updatedColumn[i][R.indices[i][elemIdx]];
        }
      }
      denominator = numerator * numerator;
      numerator *= R.values[elemIdx];
      int resultIndex = R.indices[n][elemIdx] - firstRow;
      numerators[resultIndex] += numerator;
      denominators[resultIndex] += denominator;
    }
    for (int i = 0; i < numberOfRows; i++)
    {
      int idx = i + firstRow;
      denominators[i] += lambda * (useWeight ? nnzFiber[idx] : 1);
      if (denominators[i] != 0.0F)
      {
        float result = numerators[i] / denominators[i];
        if ((result > -epsilon) && (result < epsilon)) {
          result = 0.0F;
        }
        updatedColumn[n][idx] = result;
      }
    }
  }
  
  public void updateBiases(Tensor R, int[] Rindex, int n, float[] oldBias, float[] updatedBias, float lambda, boolean useWeight, int firstRow, int lastRow, int[] nnzFiber)
  {
    int numberOfRows = lastRow - firstRow + 1;
    float[] numerators = new float[numberOfRows];
    float[] denominators = new float[numberOfRows];
    for (int idx = 0; idx < Rindex.length; idx++)
    {
      int elemIdx = Rindex[idx];
      int rowIndex = R.indices[n][elemIdx];
      int resultIndex = rowIndex - firstRow;
      numerators[resultIndex] += R.values[elemIdx] + oldBias[rowIndex];
      denominators[resultIndex] += 1.0F;
    }
    for (int i = 0; i < numberOfRows; i++)
    {
      int idx = i + firstRow;
      denominators[i] += lambda * (useWeight ? nnzFiber[idx] : 1);
      if (denominators[i] != 0.0F)
      {
        float result = numerators[i] / denominators[i];
        if ((result > -epsilon) && (result < epsilon)) {
          result = 0.0F;
        }
        updatedBias[idx] = result;
      }
    }
  }
  
  private void updateR(Tensor R, float[][] updatedColumn, int startIdx, int endIdx, boolean add)
  {
    if (add) {
      for (int idx = startIdx; idx <= endIdx; idx++)
      {
        float product = 1.0F;
        for (int n = 0; n < updatedColumn.length; n++) {
          product *= updatedColumn[n][R.indices[n][idx]];
        }
        R.values[idx] += product;
      }
    } else {
      for (int idx = startIdx; idx <= endIdx; idx++)
      {
        float product = 1.0F;
        for (int n = 0; n < updatedColumn.length; n++) {
          product *= updatedColumn[n][R.indices[n][idx]];
        }
        R.values[idx] -= product;
      }
    }
  }
  
  private static int[] blockIndex(int n, int m, int i)
  {
    int[] result = new int[2];
    result[0] = ((int)Math.ceil((n + 0.0D) * i / m));
    result[1] = ((int)Math.ceil((n + 0.0D) * (i + 1) / m) - 1);
    return result;
  }
}

