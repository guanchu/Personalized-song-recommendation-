package songRec_single;

import java.io.*;
import java.util.*;

//Êý¾Ý·ÖÅä
//  

public class GreedyRowAssignment
{
 static class ArrayIndexComparator
     implements Comparator
 {

     public Integer[] createIndexArray()
     {
         Integer indexes[] = new Integer[value.length];
         for(int i = 0; i < value.length; i++)
             indexes[i] = Integer.valueOf(i);

         return indexes;
     }

     public int compare(Integer index1, Integer index2)
     {
         return value[index2.intValue()] - value[index1.intValue()];
     }

     public int compare(Object obj, Object obj1)
     {
         return compare((Integer)obj, (Integer)obj1);
     }

     private final int value[];

     public ArrayIndexComparator(int value[])
     {
         this.value = value;
     }
 }


 public GreedyRowAssignment()
 {
 }

 public static int[][] run(String inputPath, int N, int modesIdx[], int modeLengths[], int M)
 {
     Random rand;
     int cardinalities[][];
     int permutedIdx[][];
     BufferedReader br;
     rand = new Random(0L);
     cardinalities = new int[N][];
     permutedIdx = new int[N][];
     for(int dim = 0; dim < N; dim++)
     {
         cardinalities[dim] = new int[modeLengths[dim]];
         permutedIdx[dim] = new int[modeLengths[dim]];
     }

     br = null;
     try
     {
         br = new BufferedReader(new FileReader(inputPath));
         do
         {
             String line = br.readLine();
             if(line == null)
                 break;
             String tokens[] = line.split(",");
             for(int dim = 0; dim < N; dim++)
                 cardinalities[modesIdx[dim]][Integer.valueOf(tokens[dim]).intValue()]++;

         } while(true);
        
     }
     catch(IOException e)
     {
         e.printStackTrace();
     }
     try
     {
         br.close();
     }
     catch(IOException e)
     {
         e.printStackTrace();
     }
    
     Exception exception;
    
     try
     {
         br.close();
     }
     catch(IOException e)
     {
         e.printStackTrace();
     }
     
    
        
     
  
     int totalValueSumOfEachReducer[] = new int[M];
     for(int n = N - 1; n >= 0; n--)
     {
         long modeLength = cardinalities[n].length;
         int sortedValue[] = cardinalities[n];
         ArrayIndexComparator comparator = new ArrayIndexComparator(sortedValue);
         Integer sortedIndex[] = comparator.createIndexArray();
         Arrays.sort(sortedIndex, comparator);
         Arrays.sort(sortedValue);
         ArrayMethods.reverse(sortedValue);
         int currentValueSumOfEachReducer[] = new int[M];
         int currentIndexOfEachReducer[] = new int[M];
         int maxIndexOfEachReducer[] = new int[M];
         int indiciesOfEachReducer[][] = new int[M][];
         for(int r = 0; r < M; r++)
         {
             maxIndexOfEachReducer[r] = (int)((long)Math.ceil((double)((long)(r + 1) * modeLength) / (0.0D + (double)M)) - (long)Math.ceil((double)((long)r * modeLength) / (0.0D + (double)M)));
             indiciesOfEachReducer[r] = new int[maxIndexOfEachReducer[r]];
         }

         for(int i = 0; (long)i < modeLength; i++)
         {
             int smallestIndex = 0;
             int smallestValue = 0x7fffffff;
             int smallestTotalValue = 0x7fffffff;
             int smallestRemainingIndex = 0x7fffffff;
             LinkedList tieList = new LinkedList();
             for(int r = 0; r < M; r++)
             {
                 int remainingIndex = maxIndexOfEachReducer[r] - currentIndexOfEachReducer[r];
                 if(currentIndexOfEachReducer[r] < maxIndexOfEachReducer[r])
                     if(currentValueSumOfEachReducer[r] < smallestValue)
                     {
                         smallestIndex = r;
                         smallestValue = currentValueSumOfEachReducer[r];
                         smallestTotalValue = totalValueSumOfEachReducer[r];
                         smallestRemainingIndex = remainingIndex;
                         tieList.clear();
                         tieList.add(Integer.valueOf(r));
                     } else
                     if(currentValueSumOfEachReducer[r] == smallestValue)
                         if(remainingIndex < smallestRemainingIndex)
                         {
                             smallestIndex = r;
                             smallestValue = currentValueSumOfEachReducer[r];
                             smallestTotalValue = totalValueSumOfEachReducer[r];
                             smallestRemainingIndex = remainingIndex;
                             tieList.clear();
                             tieList.add(Integer.valueOf(r));
                         } else
                         if(remainingIndex == smallestRemainingIndex)
                             if(totalValueSumOfEachReducer[r] < smallestTotalValue)
                             {
                                 smallestIndex = r;
                                 smallestValue = currentValueSumOfEachReducer[r];
                                 smallestTotalValue = totalValueSumOfEachReducer[r];
                                 smallestRemainingIndex = remainingIndex;
                                 tieList.clear();
                                 tieList.add(Integer.valueOf(r));
                             } else
                             if(totalValueSumOfEachReducer[r] == smallestTotalValue)
                                 tieList.add(Integer.valueOf(r));
             }

             smallestIndex = ((Integer)tieList.get(rand.nextInt(tieList.size()))).intValue();
             currentValueSumOfEachReducer[smallestIndex] += sortedValue[i];
             totalValueSumOfEachReducer[smallestIndex] += sortedValue[i];
             indiciesOfEachReducer[smallestIndex][currentIndexOfEachReducer[smallestIndex]] = sortedIndex[i].intValue();
             currentIndexOfEachReducer[smallestIndex]++;
         }

         int index = 0;
         for(int r = 0; r < M; r++)
         {
             for(int i = 0; i < indiciesOfEachReducer[r].length; i++)
                 permutedIdx[n][indiciesOfEachReducer[r][i]] = index++;

         }

     }

     return permutedIdx;
 }
}
