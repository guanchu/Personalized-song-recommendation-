/*jadclipse*/// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10) 
// Source File Name:   QRDecomposition.java

package Jama;

import java.io.Serializable;

// Referenced classes of package Jama:
//            Matrix, Maths

public class QRDecomposition
    implements Serializable
{

    public QRDecomposition(Matrix A)
    {
        QR = A.getArrayCopy();
        m = A.getRowDimension();
        n = A.getColumnDimension();
        Rdiag = new double[n];
        for(int k = 0; k < n; k++)
        {
            double nrm = 0.0D;
            for(int i = k; i < m; i++)
                nrm = Maths.hypot(nrm, QR[i][k]);

            if(nrm != 0.0D)
            {
                if(QR[k][k] < 0.0D)
                    nrm = -nrm;
                for(int i = k; i < m; i++)
                    QR[i][k] /= nrm;

                QR[k][k]++;
                for(int j = k + 1; j < n; j++)
                {
                    double s = 0.0D;
                    for(int i = k; i < m; i++)
                        s += QR[i][k] * QR[i][j];

                    s = -s / QR[k][k];
                    for(int i = k; i < m; i++)
                        QR[i][j] += s * QR[i][k];

                }

            }
            Rdiag[k] = -nrm;
        }

    }

    public boolean isFullRank()
    {
        for(int j = 0; j < n; j++)
            if(Rdiag[j] == 0.0D)
                return false;

        return true;
    }

    public Matrix getH()
    {
        Matrix X = new Matrix(m, n);
        double H[][] = X.getArray();
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                if(i >= j)
                    H[i][j] = QR[i][j];
                else
                    H[i][j] = 0.0D;

        }

        return X;
    }

    public Matrix getR()
    {
        Matrix X = new Matrix(n, n);
        double R[][] = X.getArray();
        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)
                if(i < j)
                    R[i][j] = QR[i][j];
                else
                if(i == j)
                    R[i][j] = Rdiag[i];
                else
                    R[i][j] = 0.0D;

        }

        return X;
    }

    public Matrix getQ()
    {
        Matrix X = new Matrix(m, n);
        double Q[][] = X.getArray();
        for(int k = n - 1; k >= 0; k--)
        {
            for(int i = 0; i < m; i++)
                Q[i][k] = 0.0D;

            Q[k][k] = 1.0D;
            for(int j = k; j < n; j++)
                if(QR[k][k] != 0.0D)
                {
                    double s = 0.0D;
                    for(int i = k; i < m; i++)
                        s += QR[i][k] * Q[i][j];

                    s = -s / QR[k][k];
                    for(int i = k; i < m; i++)
                        Q[i][j] += s * QR[i][k];

                }

        }

        return X;
    }

    public Matrix solve(Matrix B)
    {
        if(B.getRowDimension() != m)
            throw new IllegalArgumentException("Matrix row dimensions must agree.");
        if(!isFullRank())
            throw new RuntimeException("Matrix is rank deficient.");
        int nx = B.getColumnDimension();
        double X[][] = B.getArrayCopy();
        for(int k = 0; k < n; k++)
        {
            for(int j = 0; j < nx; j++)
            {
                double s = 0.0D;
                for(int i = k; i < m; i++)
                    s += QR[i][k] * X[i][j];

                s = -s / QR[k][k];
                for(int i = k; i < m; i++)
                    X[i][j] += s * QR[i][k];

            }

        }

        for(int k = n - 1; k >= 0; k--)
        {
            for(int j = 0; j < nx; j++)
                X[k][j] /= Rdiag[k];

            for(int i = 0; i < k; i++)
            {
                for(int j = 0; j < nx; j++)
                    X[i][j] -= X[k][j] * QR[i][k];

            }

        }

        return (new Matrix(X, n, nx)).getMatrix(0, n - 1, 0, nx - 1);
    }

    private double QR[][];
    private int m;
    private int n;
    private double Rdiag[];
    private static final long serialVersionUID = 1L;
}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\Users\Administrator\Desktop\ÂÛÎÄ_songRec\´úÂë\SALS\SALS-1.0.jar
	Total time: 83 ms
	Jad reported messages/errors:
The class file version is 49.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/