package songRec_single;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class MultiThread<T>
{
  private Thread[] m_threads;
  private HashMap<Integer, T> m_results;
  private int m_numberOfJobs;
  private int m_finishingJobs;
  private Exception m_exception;
  private List<Integer> m_jobList;
  private Object m_lock;
  
  public abstract T runJob(int paramInt1, int paramInt2);
  
  public HashMap<Integer, T> run(int threadNum, List<Integer> jobList)
  {
    this.m_finishingJobs = 0;
    this.m_exception = null;
    this.m_jobList = jobList;
    this.m_numberOfJobs = jobList.size();
    this.m_threads = new Thread[threadNum];
    this.m_results = new HashMap();
    this.m_lock = new Object();
    for (int i = 0; i < threadNum; i++)
    {
    	//��ÿ���߳�ѭ��
      final int threadIndex = i;
      
      //����һ���߳�
      Thread t = new Thread()
      {
        public void run()
        {
          for (;;)
          {
            int jobIndex = 0;
            jobIndex = MultiThread.this.getJob();
            
            //�õ�һ��Job���
            if (jobIndex >= 0) {
              try
              {
                T result = MultiThread.this.runJob(jobIndex, threadIndex);
                synchronized (MultiThread.this.m_lock)
                {
                  MultiThread.this.m_finishingJobs += 1;
                  MultiThread.this.m_lock.notifyAll();
                }
                if (result != null) {
                  MultiThread.this.setResult(jobIndex, result);
                }
              }
              catch (Exception e)
              {
                synchronized (MultiThread.this.m_lock)
                {
                  MultiThread.this.m_exception = e;
                  MultiThread.this.m_lock.notifyAll();
                }
                return;
              }
              
            }
            else
                return;
          }
        }
      };
      
      //�߳̿�ʼ
      t.start();
      this.m_threads[i] = t;
    }
    int finishingJobs = 0;
    for (;;)
    {
      if (this.m_finishingJobs > finishingJobs) {
        finishingJobs = this.m_finishingJobs;
      } else {
        synchronized (this.m_lock)
        {
          if ((this.m_exception != null) || (this.m_finishingJobs != this.m_numberOfJobs)) {
            try
            {
              this.m_lock.wait();
            }
            catch (InterruptedException e)
            {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }
  
  public static List<Integer> createJobList(int n)
  {
    LinkedList<Integer> jobList = new LinkedList<Integer>();
    for (int i = 0; i < n; i++) {
      jobList.add(Integer.valueOf(i));
    }
    return jobList;
  }
  
  private int getJob()
  {
    synchronized (this.m_lock)
    {
      if (this.m_exception != null) {
        return -1;
      }
      if (this.m_jobList.isEmpty()) {
        return -1;
      }
      return ((Integer)this.m_jobList.remove(0)).intValue();
    }
  }
  
  private void setResult(int jobIndex, T result)
  {
    synchronized (this.m_results)
    {
      this.m_results.put(Integer.valueOf(jobIndex), result);
    }
  }
  
  public static void main(String[] args){
  
	  new MultiThread<Object>()
  {
    public Object runJob(int blockIndex, int threadIndex)
    {
      for (int n = 0; n < 3; n++)
      {
        
        int rowStart = 1;
        int rowEnd = 2;
        for (int row = rowStart; row <= rowEnd; row++) {
          System.out.println(row);
        }
      }
      return null;
    }
  }.run(1, MultiThread.createJobList(1));
  }
  
}
