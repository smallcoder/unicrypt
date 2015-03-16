/*
 * UniCrypt
 *
 *  UniCrypt(tm) : Cryptographical framework allowing the implementation of cryptographic protocols e.g. e-voting
 *  Copyright (C) 2014 Bern University of Applied Sciences (BFH), Research Institute for
 *  Security in the Information Society (RISIS), E-Voting Group (EVG)
 *  Quellgasse 21, CH-2501 Biel, Switzerland
 *
 *  Licensed under Dual License consisting of:
 *  1. GNU Affero General Public License (AGPL) v3
 *  and
 *  2. Commercial license
 *
 *
 *  1. This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *  2. Licensees holding valid commercial licenses for UniCrypt may use this file in
 *   accordance with the commercial license agreement provided with the
 *   Software or, alternatively, in accordance with the terms contained in
 *   a written agreement between you and Bern University of Applied Sciences (BFH), Research Institute for
 *   Security in the Information Society (RISIS), E-Voting Group (EVG)
 *   Quellgasse 21, CH-2501 Biel, Switzerland.
 *
 *
 *   For further information contact <e-mail: unicrypt@bfh.ch>
 *
 *
 * Redistributions of files must retain the above copyright notice.
 */
package ch.bfh.unicrypt.helper;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;

/**
 * This is a helper class with some static methods for various mathematical functions.
 * <p>
 * @author R. Haenni
 * @author R. E. Koenig
 * @version 2.0
 */
public final class MathUtil {

	public static final int NUMBER_OF_PRIME_TESTS = 40;

	public static final BigInteger ZERO = BigInteger.valueOf(0);
	public static final BigInteger ONE = BigInteger.valueOf(1);
	public static final BigInteger TWO = BigInteger.valueOf(2);
	public static final BigInteger THREE = BigInteger.valueOf(3);
	public static final BigInteger FOUR = BigInteger.valueOf(4);

	private static final byte BYTE_ZERO = (byte) 0;
	private static final byte BYTE_ONE = (byte) 0xFF;

	private static final byte[] BIT_MASKS = new byte[Byte.SIZE];
	private static final byte[] BIT_MASKS_INV = new byte[Byte.SIZE];

	static {
		for (int i = 0; i < Byte.SIZE; i++) {
			BIT_MASKS[i] = (byte) (1 << i);
			BIT_MASKS_INV[i] = (byte) ~(1 << i);
		}
	}

	/**
	 * Returns the value obtained from applying the Euler totient function to an integer {@literal value}.
	 * <dt><b>Preconditions:</b></dt>
	 * <dd>{@literal primeFactorSet} is the complete set of prime factors of {@literal value}.</dd>
	 * <p>
	 * @param value          The input value
	 * @param primeFactorSet The prime factors of {@literal value}
	 * @return the result of applying the Euler totient function to {@literal value}
	 * @throws IllegalArgumentException if {@literal value} is {@literal null}, {@literal 0}, or negative
	 * @throws IllegalArgumentException if {@literal primeFactorSet} is null or if {@literal primeFactorSet} contains
	 *                                  {@literal null}
	 * @see MathUtil#arePrimeFactors(BigInteger, BigInteger[])
	 * @see MathUtil#removeDuplicates(BigInteger[])
	 * @see "Handbook of Applied Cryptography, Fact 2.101 (iii)"
	 */
	public static BigInteger eulerFunction(final BigInteger value, final BigInteger... primeFactorSet) {
		if (value == null || value.signum() == 0 || value.signum() == -1 || primeFactorSet == null) {
			throw new IllegalArgumentException();
		}
		BigInteger product1 = ONE;
		BigInteger product2 = ONE;
		for (final BigInteger prime : primeFactorSet) {
			if (prime == null) {
				throw new IllegalArgumentException();
			}
			product1 = product1.multiply(prime);
			product2 = product2.multiply(prime.subtract(ONE));
		}
		return value.multiply(product2).divide(product1);
	}

	/**
	 * Tests if some given BigInteger values are all prime factors of another BigInteger value. The given list of prime
	 * factors need not be complete.
	 * <p>
	 * @param value   The given value
	 * @param factors A given array of potential prime factors
	 * @return {@literal true} if all values are prime factors, {@literal false} otherwise
	 */
	public static boolean arePrimeFactors(final BigInteger value, final BigInteger... factors) {
		if (factors == null) {
			return false;
		}
		for (BigInteger factor : factors) {
			if (!isPrimeFactor(value, factor)) {
				return false;
			}
		}
		return isPositive(value);
	}

	private static boolean isPrimeFactor(final BigInteger value, final BigInteger factor) {
		return isPositive(value) && isPrime(factor) && value.gcd(factor).equals(factor);
	}

	/**
	 * Tests if some given BigInteger values are all prime numbers.
	 * <p>
	 * @param values A given array of potential prime numbers
	 * @return {@literal true} if all values are prime numbers, {@literal false} otherwise
	 */
	public static boolean arePrime(final BigInteger... values) {
		if (values == null) {
			return false;
		}
		for (BigInteger value : values) {
			if (!isPrime(value)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests if a given BigInteger value is a positive prime number.
	 * <p>
	 * @param value A potential prime number
	 * @return {@literal true} if {@literal value} is prime, {@literal false} otherwise
	 */
	public static boolean isPrime(final BigInteger value) {
		return isPositive(value) && value.isProbablePrime(MathUtil.NUMBER_OF_PRIME_TESTS);
	}

	/**
	 * Tests if a given BigInteger value is a save prime.
	 * <p>
	 * @param value A potential save prime
	 * @return {@literal true} if {@literal value} is a save prime, {@literal false} otherwise
	 */
	public static boolean isSavePrime(final BigInteger value) {
		return isPrime(value) && isPrime(value.subtract(ONE).divide(TWO));
	}

	public static boolean areRelativelyPrime(BigInteger value1, BigInteger value2) {
		return value1.gcd(value2).equals(ONE);
	}

	public static boolean areRelativelyPrime(BigInteger... values) {
		for (int i = 0; i < values.length; i++) {
			for (int j = i + 1; j < values.length; j++) {
				if (!MathUtil.areRelativelyPrime(values[i], values[j])) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Tests if some given BigInteger values are all positive.
	 * <p>
	 * @param values A given array of potential positive numbers
	 * @return {@literal true} if all values are positive, {@literal false} otherwise
	 */
	public static boolean arePositive(final BigInteger... values) {
		if (values == null) {
			return false;
		}
		for (BigInteger value : values) {
			if (!isPositive(value)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Tests if a given BigInteger value is positive.
	 * <p>
	 * @param value A potential positive number
	 * @return {@literal true} if {@literal value} is positive, {@literal false} otherwise
	 */
	public static boolean isPositive(final BigInteger value) {
		if (value == null) {
			return false;
		}
		return value.signum() == 1;
	}

	/**
	 * Tests if some given integer values are all positive.
	 * <p>
	 * @param values A given array of potential positive numbers
	 * @return {@literal true} is all values are positive, {@literal false} otherwise
	 */
	public static boolean arePositive(final int... values) {
		for (int value : values) {
			if (value <= 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Removes duplicate values from a BigInteger array
	 * <p>
	 * @param values An array of BigInteger values
	 * @return the same array of BigInteger values without duplicates
	 * @throws IllegalArgumentException if {@literal values} is {@literal null}
	 */
	public static BigInteger[] removeDuplicates(final BigInteger... values) {
		if (values == null) {
			throw new IllegalArgumentException();
		}
		final HashSet<BigInteger> hashSet = new HashSet<BigInteger>(Arrays.asList(values));
		return hashSet.toArray(new BigInteger[hashSet.size()]);
	}

	/**
	 * Computes the factorial of some integer value. Returns 1 for input 0.
	 * <p>
	 * @param value The input value
	 * @return The factorial of {@literal value}
	 * @throws IllegalArgumentException if {@literal value} is negative.
	 */
	public static BigInteger factorial(final int value) {
		if (value < 0) {
			throw new IllegalArgumentException();
		}
		BigInteger result = ONE;
		for (int i = 1; i <= value; i++) {
			result = result.multiply(BigInteger.valueOf(i));
		}
		return result;
	}

	/**
	 * Computes the maximum value of a given BigInteger array.
	 * <p>
	 * @param values The given BigInteger array
	 * @return The maximum value
	 * @throws IllegalArgumentException if {@literal values} is null or empty, or if it contains null
	 */
	public static BigInteger maxValue(final BigInteger... values) {
		if (values == null || values.length == 0) {
			throw new IllegalArgumentException();
		}
		BigInteger maxValue = null;
		for (final BigInteger value : values) {
			if (value == null) {
				throw new IllegalArgumentException();
			}
			if (maxValue == null) {
				maxValue = value;
			} else {
				maxValue = maxValue.max(value);
			}
		}
		return maxValue;
	}

	/**
	 * Computes the elegant pairing function for two non-negative BigInteger values.
	 * <p>
	 * @see <a href="http://szudzik.com/ElegantPairing.pdf">ElegantPairing.pdf</a>
	 * @param value1 The first value
	 * @param value2 The second value
	 * @return The result of applying the elegant pairing function
	 * @throws IllegalArgumentException if {@literal value1} or {@literal value2} is null or negative
	 */
	public static BigInteger pair(BigInteger value1, BigInteger value2) {
		if (value1 == null || value1.signum() < 0 || value2 == null || value2.signum() < 0) {
			throw new IllegalArgumentException();
		}
		if (value1.compareTo(value2) < 0) {
			return value2.multiply(value2).add(value1);
		}
		return value1.multiply(value1).add(value1).add(value2);
	}

	public static BigInteger pair(int... values) {
		if (values == null) {
			throw new IllegalArgumentException();
		}
		BigInteger[] bigIntegers = new BigInteger[values.length];
		for (int i = 0; i < values.length; i++) {
			bigIntegers[i] = BigInteger.valueOf(values[i]);
		}
		return MathUtil.pair(bigIntegers);
	}

	/**
	 * Computes the elegant pairing function for a given list of non-negative BigInteger values. The order in which the
	 * binary pairing function is applied is recursively from left to right.
	 * <p>
	 * @see <a href="http://szudzik.com/ElegantPairing.pdf">ElegantPairing.pdf</a>
	 * @param values The given values
	 * @return The result of applying the elegant pairing function
	 * @throws IllegalArgumentException if {@literal values} is null
	 * @throws IllegalArgumentException if {@literal values} contains null or negative value
	 */
	public static BigInteger pair(BigInteger... values) {
		if (values == null) {
			throw new IllegalArgumentException();
		}
		int n = values.length;
		if (n == 0) {
			return ZERO;
		}
		if (n == 1) {
			return values[0];
		}
		BigInteger[] a = new BigInteger[n / 2 + n % 2];
		for (int i = 0; i < n / 2; i++) {
			a[i] = pair(values[2 * i], values[2 * i + 1]);
		}
		if (n % 2 == 1) {
			a[n / 2] = values[n - 1];
		}
		return pair(a);
	}

	/**
	 * Computes the elegant pairing function for a given list of non-negative BigInteger values. The size of the given
	 * input list is taken as an additional input value.
	 * <p>
	 * @see <a href="http://szudzik.com/ElegantPairing.pdf">ElegantPairing.pdf</a>
	 * @param values The given values
	 * @return The result of applying the elegant pairing function
	 * @throws IllegalArgumentException if {@literal values} is null
	 * @throws IllegalArgumentException if {@literal values} contains null or negative value
	 */
	public static BigInteger pairWithSize(BigInteger... values) {
		if (values == null) {
			throw new IllegalArgumentException();
		}
		return pair(pair(values), BigInteger.valueOf(values.length));
	}

	/**
	 * Computes the inverse of the binary elegant pairing function for a given non-negative BigInteger value.
	 * <p>
	 * @see <a href="http://szudzik.com/ElegantPairing.pdf">ElegantPairing.pdf</a>
	 * @param value The input value
	 * @return An array containing the two resulting values
	 * @throws IllegalArgumentException if {@literal value} is null or negative
	 */
	public static BigInteger[] unpair(BigInteger value) {
		if (value == null || value.signum() < 0) {
			throw new IllegalArgumentException();
		}
		BigInteger x1 = sqrt(value);
		BigInteger x2 = value.subtract(x1.multiply(x1));
		if (x1.compareTo(x2) > 0) {
			return new BigInteger[]{x2, x1};
		}
		return new BigInteger[]{x1, x2.subtract(x1)};
	}

	/**
	 * Computes the inverse of the n-ary elegant pairing function for a given non-negative BigInteger value.
	 * <p>
	 * @see <a href="http://szudzik.com/ElegantPairing.pdf">ElegantPairing.pdf</a>
	 * @param value The input value
	 * @param size  The number of resulting values
	 * @return An array containing the resulting values
	 * @throws IllegalArgumentException if {@literal value} is null or negative
	 * @throws IllegalArgumentException if {@literal size} is negative
	 */
	public static BigInteger[] unpair(BigInteger value, int size) {
		if (size < 0 || value.signum() < 0) {
			throw new IllegalArgumentException();
		}
		BigInteger[] result = new BigInteger[size];
		if (size == 0) {
			if (value.signum() > 0) {
				throw new IllegalArgumentException();
			}
		} else {
			unpair(value, size, 0, result);
		}
		return result;
	}

	// This is a private helper method for doing the recursion
	private static void unpair(BigInteger value, int size, int start, BigInteger[] result) {
		if (size == 1) {
			result[start] = value;
		} else {
			BigInteger[] values = unpair(value);
			int powerOfTwo = 1 << BigInteger.valueOf(size - 1).bitLength() - 1;
			unpair(values[0], powerOfTwo, start, result);
			unpair(values[1], size - powerOfTwo, start + powerOfTwo, result);
		}
	}

	/**
	 * Computes the inverse of the n-ary elegant pairing function for a given non-negative BigInteger value, where the
	 * size is included as additional input value.
	 * <p>
	 * @see <a href="http://szudzik.com/ElegantPairing.pdf">ElegantPairing.pdf</a>
	 * @param value The input value
	 * @return An array containing the resulting values
	 * @throws IllegalArgumentException if {@literal value} is null or negative
	 */
	public static BigInteger[] unpairWithSize(BigInteger value) {
		BigInteger[] values = unpair(value);
		return unpair(values[0], values[1].intValue());
	}

	public static BigInteger fold(BigInteger value) {
		if (value.signum() >= 0) {
			return value.shiftLeft(1);
		}
		return value.negate().shiftLeft(1).subtract(ONE);
	}

	public static BigInteger unfold(BigInteger value) {
		if (value.signum() == -1) {
			throw new IllegalArgumentException();
		}
		if (value.mod(TWO).equals(ZERO)) {
			return value.shiftRight(1);
		}
		return value.add(ONE).shiftRight(1).negate();
	}

	public static BigInteger foldAndPair(BigInteger... values) {
		BigInteger[] foldedValues = new BigInteger[values.length];
		for (int i = 0; i < values.length; i++) {
			foldedValues[i] = fold(values[i]);
		}
		return pair(foldedValues);
	}

	public static BigInteger[] unpairAndUnfold(BigInteger value) {
		return unpairAndUnfold(value, 2);
	}

	public static BigInteger[] unpairAndUnfold(BigInteger value, int size) {
		BigInteger[] result = new BigInteger[size];
		BigInteger[] values = unpair(value, size);
		for (int i = 0; i < size; i++) {
			result[i] = unfold(values[i]);
		}
		return result;
	}

	public static BigInteger powerOfTwo(int exponent) {
		return ONE.shiftLeft(exponent);
	}

	// This is a helper method to compute the integer square root of a positive BigInteger value.
	public static BigInteger sqrt(BigInteger n) {
		// exception if n<0
		if (n.signum() == -1) {
			throw new IllegalArgumentException();
		}
		// special case
		if (n.signum() == 0) {
			return ZERO;
		}
		// first guess
		BigInteger current = powerOfTwo(n.bitLength() / 2 + 1);
		BigInteger last;
		do {
			last = current;
			current = last.add(n.divide(last)).shiftRight(1);
		} while (last.compareTo(current) > 0);
		return last;
	}

	//Tonelli_Shanks algorithm for square root modulo prime p>2
	//Computes only one solution r, the other solution is p-r
	public static BigInteger sqrtModPrime(BigInteger x, BigInteger p) {

		if (!hasSqrtModPrime(x, p)) {
			throw new IllegalArgumentException("r has no square root");
		}

		if (p.mod(FOUR).equals(THREE)) {
			return x.modPow(p.add(ONE).divide(FOUR), p);
		}

		//z which must be a quadratic non-residue mod p.
		BigInteger z = TWO;
		while (hasSqrtModPrime(z, p)) {
			z = z.add(ONE);
		}
		BigInteger s = ONE;
		BigInteger q = p.subtract(ONE).divide(TWO);

		//Finding Q
		while (q.mod(TWO).equals(ZERO)) {
			q = q.divide(TWO);
			s = s.add(ONE);
		}

		BigInteger c = z.modPow(q, p);
		BigInteger r = x.modPow(q.add(ONE).divide(TWO), p);
		BigInteger t = x.modPow(q, p);
		BigInteger m = s;

		//Loop until t==1
		while (!t.equals(ONE)) {
			BigInteger i = ZERO;
			while (!ONE.equals(t.modPow(TWO.modPow(i, p), p))) {
				i = i.add(ONE);
			}

			BigInteger b = c.modPow(TWO.modPow(m.subtract(i).subtract(ONE), p), p);
			r = r.multiply(b).mod(p);
			t = t.multiply(b.pow(2)).mod(p);
			c = b.modPow(TWO, p);
			m = i;
		}

		if (r.modPow(TWO, p).equals(x.mod(p))) {
			return r;
		}
		throw new IllegalArgumentException();
	}

	//Check if x has a square root mod p>2
	public static boolean hasSqrtModPrime(BigInteger x, BigInteger p) {
		return x.modPow(p.subtract(ONE).divide(TWO), p).equals(ONE);
	}

	// Bit operations on byte
	public static boolean getBit(byte b, int i) {
		return and(b, BIT_MASKS[i]) != 0;
	}

	public static byte setBit(byte b, int i) {
		return or(b, BIT_MASKS[i]);
	}

	public static byte clearBit(byte b, int i) {
		return and(b, BIT_MASKS_INV[i]);
	}

	public static byte replaceBit(byte b, int i, boolean bit) {
		if (bit) {
			return setBit(b, i);
		} else {
			return clearBit(b, i);
		}
	}

	public static byte reverse(byte b) {
		return (byte) (Integer.reverse(b & 0xFF) >>> (Integer.SIZE - Byte.SIZE));
	}

	public static byte shiftLeft(byte b, int n) {
		return (byte) ((b & 0xFF) << n);
	}

	public static byte shiftRight(byte b, int n) {
		return (byte) ((b & 0xFF) >>> n);
	}

	public static byte xor(byte b1, byte b2) {
		return (byte) ((b1 & 0xFF) ^ (b2 & 0xFF));
	}

	public static byte and(byte b1, byte b2) {
		return (byte) ((b1 & 0xFF) & (b2 & 0xFF));
	}

	public static byte or(byte b1, byte b2) {
		return (byte) ((b1 & 0xFF) | (b2 & 0xFF));
	}

	public static byte not(byte b) {
		return (byte) ~(b & 0xFF);
	}

	// mathematical modulo and divide working for positive and negative values
	// Java8: use floorMod and floorDiv to handle negative values properly
	public static int modulo(int i, int n) {
		return ((i % n) + n) % n;
	}

	public static int divide(int i, int n) {
		return (i - modulo(i, n)) / n;
	}

}
