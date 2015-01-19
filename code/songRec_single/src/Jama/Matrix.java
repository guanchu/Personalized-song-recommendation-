/*jadclipse*/// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.

package Jama;

import java.io.*;
import java.text.*;
import java.util.Locale;
import java.util.Vector;

// Referenced classes of package Jama:
//            CholeskyDecomposition

public class Matrix
    implements Cloneable, Serializable
{

    public Matrix(int m, int n)
    {
        this.m = m;
        this.n = n;
        A = new double[m][n];
    }

    public Matrix(int m, int n, double s)
    {
        this.m = m;
        this.n = n;
        A = new double[m][n];
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                A[i][j] = s;

        }

    }

    public Matrix(double A[][])
    {
        m = A.length;
        n = A[0].length;
        for(int i = 0; i < m; i++)
            if(A[i].length != n)
                throw new IllegalArgumentException("All rows must have the same length.");

        this.A = A;
    }

    public Matrix(double A[])
    {
        m = A.length;
        n = 1;
        this.A = new double[m][n];
        for(int i = 0; i < m; i++)
            this.A[i][0] = A[i];

    }

    public Matrix(double A[][], int m, int n)
    {
        this.A = A;
        this.m = m;
        this.n = n;
    }

    public Matrix(double vals[], int m)
    {
        this.m = m;
        n = m == 0 ? 0 : vals.length / m;
        if(m * n != vals.length)
            throw new IllegalArgumentException("Array length must be a multiple of m.");
        A = new double[m][n];
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                A[i][j] = vals[i + j * m];

        }

    }

    public static Matrix constructWithCopy(double A[][])
    {
        int m = A.length;
        int n = A[0].length;
        Matrix X = new Matrix(m, n);
        double C[][] = X.getArray();
        for(int i = 0; i < m; i++)
        {
            if(A[i].length != n)
                throw new IllegalArgumentException("All rows must have the same length.");
            for(int j = 0; j < n; j++)
                C[i][j] = A[i][j];

        }

        return X;
    }

    public Matrix copy()
    {
        Matrix X = new Matrix(m, n);
        double C[][] = X.getArray();
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                C[i][j] = A[i][j];

        }

        return X;
    }

    public Object clone()
    {
        return copy();
    }

    public double[][] getArray()
    {
        return A;
    }

    public double[][] getArrayCopy()
    {
        double C[][] = new double[m][n];
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                C[i][j] = A[i][j];

        }

        return C;
    }

    public double[] getColumnPackedCopy()
    {
        double vals[] = new double[m * n];
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                vals[i + j * m] = A[i][j];

        }

        return vals;
    }

    public double[] getRowPackedCopy()
    {
        double vals[] = new double[m * n];
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                vals[i * n + j] = A[i][j];

        }

        return vals;
    }

    public int getRowDimension()
    {
        return m;
    }

    public int getColumnDimension()
    {
        return n;
    }

    public double get(int i, int j)
    {
        return A[i][j];
    }

    public Matrix getMatrix(int i0, int i1, int j0, int j1)
    {
        Matrix X = new Matrix((i1 - i0) + 1, (j1 - j0) + 1);
        double B[][] = X.getArray();
        try
        {
            for(int i = i0; i <= i1; i++)
            {
                for(int j = j0; j <= j1; j++)
                    B[i - i0][j - j0] = A[i][j];

            }

        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    public Matrix getMatrix(int r[], int c[])
    {
        Matrix X = new Matrix(r.length, c.length);
        double B[][] = X.getArray();
        try
        {
            for(int i = 0; i < r.length; i++)
            {
                for(int j = 0; j < c.length; j++)
                    B[i][j] = A[r[i]][c[j]];

            }

        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    public Matrix getMatrix(int i0, int i1, int c[])
    {
        Matrix X = new Matrix((i1 - i0) + 1, c.length);
        double B[][] = X.getArray();
        try
        {
            for(int i = i0; i <= i1; i++)
            {
                for(int j = 0; j < c.length; j++)
                    B[i - i0][j] = A[i][c[j]];

            }

        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    public Matrix getMatrix(int r[], int j0, int j1)
    {
        Matrix X = new Matrix(r.length, (j1 - j0) + 1);
        double B[][] = X.getArray();
        try
        {
            for(int i = 0; i < r.length; i++)
            {
                for(int j = j0; j <= j1; j++)
                    B[i][j - j0] = A[r[i]][j];

            }

        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
        return X;
    }

    public void set(int i, int j, double s)
    {
        A[i][j] = s;
    }

    public void setMatrix(int i0, int i1, int j0, int j1, Matrix X)
    {
        try
        {
            for(int i = i0; i <= i1; i++)
            {
                for(int j = j0; j <= j1; j++)
                    A[i][j] = X.get(i - i0, j - j0);

            }

        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    public void setMatrix(int r[], int c[], Matrix X)
    {
        try
        {
            for(int i = 0; i < r.length; i++)
            {
                for(int j = 0; j < c.length; j++)
                    A[r[i]][c[j]] = X.get(i, j);

            }

        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    public void setMatrix(int r[], int j0, int j1, Matrix X)
    {
        try
        {
            for(int i = 0; i < r.length; i++)
            {
                for(int j = j0; j <= j1; j++)
                    A[r[i]][j] = X.get(i, j - j0);

            }

        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    public void setMatrix(int i0, int i1, int c[], Matrix X)
    {
        try
        {
            for(int i = i0; i <= i1; i++)
            {
                for(int j = 0; j < c.length; j++)
                    A[i][c[j]] = X.get(i - i0, j);

            }

        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            throw new ArrayIndexOutOfBoundsException("Submatrix indices");
        }
    }

    public Matrix transpose()
    {
        Matrix X = new Matrix(n, m);
        double C[][] = X.getArray();
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                C[j][i] = A[i][j];

        }

        return X;
    }

    public double norm1()
    {
        double f = 0.0D;
        for(int j = 0; j < n; j++)
        {
            double s = 0.0D;
            for(int i = 0; i < m; i++)
                s += Math.abs(A[i][j]);

            f = Math.max(f, s);
        }

        return f;
    }

    public double normInf()
    {
        double f = 0.0D;
        for(int i = 0; i < m; i++)
        {
            double s = 0.0D;
            for(int j = 0; j < n; j++)
                s += Math.abs(A[i][j]);

            f = Math.max(f, s);
        }

        return f;
    }

    public Matrix uminus()
    {
        Matrix X = new Matrix(m, n);
        double C[][] = X.getArray();
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                C[i][j] = -A[i][j];

        }

        return X;
    }

    public Matrix plus(Matrix B)
    {
        checkMatrixDimensions(B);
        Matrix X = new Matrix(m, n);
        double C[][] = X.getArray();
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                C[i][j] = A[i][j] + B.A[i][j];

        }

        return X;
    }

    public Matrix plusEquals(Matrix B)
    {
        checkMatrixDimensions(B);
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                A[i][j] = A[i][j] + B.A[i][j];

        }

        return this;
    }

    public Matrix minus(Matrix B)
    {
        checkMatrixDimensions(B);
        Matrix X = new Matrix(m, n);
        double C[][] = X.getArray();
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                C[i][j] = A[i][j] - B.A[i][j];

        }

        return X;
    }

    public Matrix minusEquals(Matrix B)
    {
        checkMatrixDimensions(B);
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                A[i][j] = A[i][j] - B.A[i][j];

        }

        return this;
    }

    public Matrix arrayTimes(Matrix B)
    {
        checkMatrixDimensions(B);
        Matrix X = new Matrix(m, n);
        double C[][] = X.getArray();
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                C[i][j] = A[i][j] * B.A[i][j];

        }

        return X;
    }

    public Matrix arrayTimesEquals(Matrix B)
    {
        checkMatrixDimensions(B);
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                A[i][j] = A[i][j] * B.A[i][j];

        }

        return this;
    }

    public Matrix arrayRightDivide(Matrix B)
    {
        checkMatrixDimensions(B);
        Matrix X = new Matrix(m, n);
        double C[][] = X.getArray();
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                C[i][j] = A[i][j] / B.A[i][j];

        }

        return X;
    }

    public Matrix arrayRightDivideEquals(Matrix B)
    {
        checkMatrixDimensions(B);
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                A[i][j] = A[i][j] / B.A[i][j];

        }

        return this;
    }

    public Matrix arrayLeftDivide(Matrix B)
    {
        checkMatrixDimensions(B);
        Matrix X = new Matrix(m, n);
        double C[][] = X.getArray();
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                C[i][j] = B.A[i][j] / A[i][j];

        }

        return X;
    }

    public Matrix arrayLeftDivideEquals(Matrix B)
    {
        checkMatrixDimensions(B);
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                A[i][j] = B.A[i][j] / A[i][j];

        }

        return this;
    }

    public Matrix times(double s)
    {
        Matrix X = new Matrix(m, n);
        double C[][] = X.getArray();
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                C[i][j] = s * A[i][j];

        }

        return X;
    }

    public Matrix timesEquals(double s)
    {
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                A[i][j] = s * A[i][j];

        }

        return this;
    }

    public Matrix times(Matrix B)
    {
        if(B.m != n)
            throw new IllegalArgumentException("Matrix inner dimensions must agree.");
        Matrix X = new Matrix(m, B.n);
        double C[][] = X.getArray();
        double Bcolj[] = new double[n];
        for(int j = 0; j < B.n; j++)
        {
            for(int k = 0; k < n; k++)
                Bcolj[k] = B.A[k][j];

            for(int i = 0; i < m; i++)
            {
                double Arowi[] = A[i];
                double s = 0.0D;
                for(int k = 0; k < n; k++)
                    s += Arowi[k] * Bcolj[k];

                C[i][j] = s;
            }

        }

        return X;
    }

    public CholeskyDecomposition chol()
    {
        return new CholeskyDecomposition(this);
    }

    public double trace()
    {
        double t = 0.0D;
        for(int i = 0; i < Math.min(m, n); i++)
            t += A[i][i];

        return t;
    }

    public static Matrix random(int m, int n)
    {
        Matrix A = new Matrix(m, n);
        double X[][] = A.getArray();
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                X[i][j] = Math.random();

        }

        return A;
    }

    public static Matrix identity(int m, int n)
    {
        Matrix A = new Matrix(m, n);
        double X[][] = A.getArray();
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
                X[i][j] = i != j ? 0.0D : 1.0D;

        }

        return A;
    }

    public void print(int w, int d)
    {
        print(new PrintWriter(System.out, true), w, d);
    }

    public void print(PrintWriter output, int w, int d)
    {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);
        print(output, ((NumberFormat) (format)), w + 2);
    }

    public void print(NumberFormat format, int width)
    {
        print(new PrintWriter(System.out, true), format, width);
    }

    public void print(PrintWriter output, NumberFormat format, int width)
    {
        output.println();
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
            {
                String s = format.format(A[i][j]);
                int padding = Math.max(1, width - s.length());
                for(int k = 0; k < padding; k++)
                    output.print(' ');

                output.print(s);
            }

            output.println();
        }

        output.println();
    }

    public static Matrix read(BufferedReader input)
        throws IOException
    {
        StreamTokenizer tokenizer = new StreamTokenizer(input);
        tokenizer.resetSyntax();
        tokenizer.wordChars(0, 255);
        tokenizer.whitespaceChars(0, 32);
        tokenizer.eolIsSignificant(true);
        Vector vD = new Vector();
        while(tokenizer.nextToken() == 10) ;
        if(tokenizer.ttype == -1)
            throw new IOException("Unexpected EOF on matrix read.");
        do
            vD.addElement(Double.valueOf(tokenizer.sval));
        while(tokenizer.nextToken() == -3);
        int n = vD.size();
        double row[] = new double[n];
        for(int j = 0; j < n; j++)
            row[j] = ((Double)vD.elementAt(j)).doubleValue();

        Vector v = new Vector();
        v.addElement(row);
        while(tokenizer.nextToken() == -3) 
        {
            v.addElement(row = new double[n]);
            int j = 0;
            do
            {
                if(j >= n)
                    throw new IOException((new StringBuilder("Row ")).append(v.size()).append(" is too long.").toString());
                row[j++] = Double.valueOf(tokenizer.sval).doubleValue();
            } while(tokenizer.nextToken() == -3);
            if(j < n)
                throw new IOException((new StringBuilder("Row ")).append(v.size()).append(" is too short.").toString());
        }
        int m = v.size();
        double A[][] = new double[m][];
        v.copyInto(A);
        return new Matrix(A);
    }

    private void checkMatrixDimensions(Matrix B)
    {
        if(B.m != m || B.n != n)
            throw new IllegalArgumentException("Matrix dimensions must agree.");
        else
            return;
    }

    private double A[][];
    private int m;
    private int n;
    private static final long serialVersionUID = 1L;
}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\Users\Administrator\Desktop\ÂÛÎÄ_songRec\´úÂë\SALS\SALS-1.0.jar
	Total time: 80 ms
	Jad reported messages/errors:
The class file version is 49.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/