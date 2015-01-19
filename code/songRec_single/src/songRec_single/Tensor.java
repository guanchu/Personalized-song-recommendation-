package songRec_single;

import songRec_single.ArrayMethods;
import songRec_single.Tensor;

public class Tensor
{

    public Tensor(int N, int modeLengths[], int omega, int indices[][], float values[], float sum)
    {
        this.N = N;
        this.modeLengths = modeLengths;
        this.omega = omega;
        this.indices = indices;
        this.values = values;
        mu = sum / (float)omega;
    }

    public Tensor copy()
    {
        return new Tensor(this);
    }

    private Tensor(Tensor target)
    {
        N = target.N;
        modeLengths = (int[])target.modeLengths.clone();
        omega = target.omega;
        indices = ArrayMethods.copy(target.indices);
        values = (float[])target.values.clone();
        mu = target.mu;
    }

    public int N;
    public int modeLengths[];
    public int omega;
    public int indices[][];
    public float values[];
    public float mu;
}

