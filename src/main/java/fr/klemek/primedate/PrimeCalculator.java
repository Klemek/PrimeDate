package fr.klemek.primedate;

import java.util.Arrays;

/**
 * Calculate and check prime numbers
 * 
 * @author Kleme
 */
public abstract class PrimeCalculator {

	// http://compoasso.free.fr/primelistweb/page/prime/eratosthene_en.php

	private static final long MAX = 1000000L; // 1 000 000
	private static final long SQRT_MAX = (long) Math.sqrt(MAX) + 1;
	private static final int MEMORY_SIZE = (int) (MAX >> 4);
	private static final int BLOCK_MAX = (int) (1 << 4);

	private static boolean computed = false;
	private static byte[] primes = new byte[MEMORY_SIZE];

	/**
	 * Get stored bit for number i
	 * 
	 * @param i
	 * @return
	 */
	private static boolean getBit(long i) {
		byte block = primes[(int) (i >> 4)];
		byte mask = (byte) (1 << ((i >> 1) & 7));
		return ((block & mask) != 0);
	}

	/**
	 * Set stored bit for number i
	 * 
	 * @param i
	 */
	private static void setBit(long i) {
		int index = (int) (i >> 4);
		byte block = primes[index];
		byte mask = (byte) (1 << ((i >> 1) & 7));
		primes[index] = (byte) (block | mask);
	}

	/**
	 * Computes first million numbers, (takes ~25 ms)
	 */
	public static void computeList() {
		Arrays.fill(primes, (byte) 0);
		long t0 = System.currentTimeMillis();
		for (long i = 3; i < SQRT_MAX; i += 2)
			if (!getBit(i)) {
				long j = (i * i);
				while (j < MAX) {
					setBit(j);
					j += (2 * i);
				}
			}
		computed = true;
		System.out.println(
				String.format("Calculated %d KB of primes in %d ms", MEMORY_SIZE / 1000, System.currentTimeMillis() - t0));
	}

	/**
	 * Next prime stored
	 * 
	 * @param p
	 * @return
	 */
	private static int nextPrime(int p) {
		p++;
		while (getBit(p))
			p++;
		return p;
	}

	/**
	 * Check if a number is prime by calculating its block
	 * 
	 * @param number
	 * @return
	 */
	private static boolean isPrimeByBlock(long number) {
		long start = number - number % BLOCK_MAX;
		long end = start + BLOCK_MAX;
		long endSqrt = (long) Math.sqrt(end) + 1;

		byte block = 0;
		int p = 2;
		while (p < endSqrt) {
			p = nextPrime(p);

			long j = p - (start % p);
			j += ((j & 1) == 0 ? p : 0);

			long p2 = p * 2;
			while (j < BLOCK_MAX) {
				block |= (1 << ((j >> 1) & 7));
				j += p2;
			}
		}
		byte mask = (byte) (1 << ((number >> 1) & 7));
		
		return !((block & mask) != 0);
	}

	/**
	 * Check if a number is prime (max 1 000 000 000 000)
	 * 
	 * @param number
	 * @return
	 * @throws Exception
	 */
	public static boolean isPrime(long number) throws Exception {
		if (number <= 0 || Math.sqrt(number) > MAX)
			throw new Exception("Number out of range");
		if (number <= 2)
			return true;
		if ((number & 1) == 0)
			return false;
		if (!computed)
			computeList();
		if (number < MAX)
			return !getBit(number);
		return isPrimeByBlock(number);
	}

}
