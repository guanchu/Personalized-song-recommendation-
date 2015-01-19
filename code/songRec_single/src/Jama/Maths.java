/*jadclipse*/// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.

package Jama;


public class Maths
{

    public Maths()
    {
    }

    public static double hypot(double a, double b)
    {
        double r;
        if(Math.abs(a) > Math.abs(b))
        {
            r = b / a;
            r = Math.abs(a) * Math.sqrt(1.0D + r * r);
        } else
        if(b != 0.0D)
        {
            r = a / b;
            r = Math.abs(b) * Math.sqrt(1.0D + r * r);
        } else
        {
            r = 0.0D;
        }
        return r;
    }
}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\Users\Administrator\Desktop\ÂÛÎÄ_songRec\´úÂë\SALS\SALS-1.0.jar
	Total time: 67 ms
	Jad reported messages/errors:
The class file version is 49.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/