package com.github.teocci.socket.utils;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-Jan-10
 */
public class Random
{
    private static java.util.Random random;    // pseudo-random number generator
    private static long seed;        // pseudo-random number generator seed

    // static initializer
    static {
        // this is how the seed was set in Java 1.4
        seed = System.currentTimeMillis();
        random = new java.util.Random(seed);
    }

    /**
     * Sets the seed of the pseudo-random number generator.
     * This method enables you to produce the same sequence of "random"
     * number for each execution of the program.
     * Ordinarily, you should call this method at most once per program.
     *
     * @param s the seed
     */
    public static void setSeed(long s)
    {
        seed = s;
        random = new java.util.Random(seed);
    }

    /**
     * Returns the seed of the pseudo-random number generator.
     *
     * @return the seed
     */
    public static long getSeed()
    {
        return seed;
    }

    /**
     * Returns a random real number uniformly in [0, 1).
     *
     * @return a random real number uniformly in [0, 1)
     */
    public static double uniform()
    {
        return random.nextDouble();
    }


    /**
     * Returns a random long integer uniformly in [0, n).
     *
     * @param n number of possible {@code long} integers
     * @return a random long integer uniformly between 0 (inclusive) and {@code n} (exclusive)
     * @throws IllegalArgumentException if {@code n <= 0}
     */
    public static long uniform(long n)
    {
        if (n <= 0L) throw new IllegalArgumentException("argument must be positive: " + n);

        long r = random.nextLong();
        long m = n - 1;

        // power of two
        if ((n & m) == 0L) {
            return r & m;
        }

        // reject over-represented candidates
        long u = r >>> 1;
        while (u + m - (r = u % n) < 0L) {
            u = random.nextLong() >>> 1;
        }
        return r;
    }

    /**
     * Returns a random integer uniformly in [0, n).
     *
     * @param n number of possible integers
     * @return a random integer uniformly between 0 (inclusive) and {@code n} (exclusive)
     * @throws IllegalArgumentException if {@code n <= 0}
     */
    public static int uniform(int n)
    {
        if (n <= 0) throw new IllegalArgumentException("argument must be positive: " + n);
        return random.nextInt(n);
    }

    /**
     * Returns a random integer uniformly in [a, b).
     *
     * @param a the left endpoint
     * @param b the right endpoint
     * @return a random integer uniformly in [a, b)
     * @throws IllegalArgumentException if {@code b <= a}
     * @throws IllegalArgumentException if {@code b - a >= Integer.MAX_VALUE}
     */
    public static int uniform(int a, int b)
    {
        if ((b <= a) || ((long) b - a >= Integer.MAX_VALUE)) {
            throw new IllegalArgumentException("invalid range: [" + a + ", " + b + ")");
        }
        return (int) (a + uniform(b - a));
    }

    /**
     * Returns a random real number uniformly in [a, b).
     *
     * @param a the left endpoint
     * @param b the right endpoint
     * @return a random real number uniformly in [a, b)
     * @throws IllegalArgumentException unless {@code a < b}
     */
    public static double uniform(double a, double b)
    {
        if (!(a < b)) {
            throw new IllegalArgumentException("invalid range: [" + a + ", " + b + ")");
        }
        return a + uniform() * (b - a);
    }


    /**
     * Returns a random boolean from a Bernoulli distribution with success
     * probability <em>p</em>.
     *
     * @param p the probability of returning {@code true}
     * @return {@code true} with probability {@code p} and
     * {@code false} with probability {@code 1 - p}
     * @throws IllegalArgumentException unless {@code 0} &le; {@code p} &le; {@code 1.0}
     */
    public static boolean bernoulli(double p)
    {
        if (!(p >= 0.0 && p <= 1.0))
            throw new IllegalArgumentException("probability p must be between 0.0 and 1.0: " + p);
        return uniform() < p;
    }

    /**
     * Returns a random boolean from a Bernoulli distribution with success
     * probability 1/2.
     *
     * @return {@code true} with probability 1/2 and
     * {@code false} with probability 1/2
     */
    public static boolean bernoulli()
    {
        return bernoulli(0.5);
    }

    /**
     * Returns a random real number from a standard Gaussian distribution.
     *
     * @return a random real number from a standard Gaussian distribution
     * (mean 0 and standard deviation 1).
     */
    public static double gaussian()
    {
        // use the polar form of the Box-Muller transform
        double r, x, y;
        do {
            x = uniform(-1.0, 1.0);
            y = uniform(-1.0, 1.0);
            r = x * x + y * y;
        } while (r >= 1 || r == 0);
        return x * Math.sqrt(-2 * Math.log(r) / r);

        // Remark:  y * Math.sqrt(-2 * Math.log(r) / r)
        // is an independent random gaussian
    }

    /**
     * Returns a random real number from a Gaussian distribution with mean &mu;
     * and standard deviation &sigma;.
     *
     * @param mu    the mean
     * @param sigma the standard deviation
     * @return a real number distributed according to the Gaussian distribution
     * with mean {@code mu} and standard deviation {@code sigma}
     */
    public static double gaussian(double mu, double sigma)
    {
        return mu + sigma * gaussian();
    }
}
