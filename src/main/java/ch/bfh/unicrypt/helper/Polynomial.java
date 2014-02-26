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

import ch.bfh.unicrypt.helper.array.ByteArray;
import ch.bfh.unicrypt.helper.array.ImmutableArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author philipp
 * @param <C>
 */
public class Polynomial<C extends Object>
	   extends UniCrypt {

	/** Polynomial's degree. */
	private final int degree;
	/** Holds the coefficients. It might be null if the polynomial is binary. */
	private final Map<Integer, C> coefficients;
	/** Holds the coefficients of binary polynomials. It is null if the polynomial is not binary. */
	private final ByteArray binaryCoefficients;

	/** Polynomial's zero coefficient. */
	private final C zeroCoefficient;
	/** Polynomial's one coefficient. */
	private final C oneCoefficient;

	/** Holds the indices of the non zero coefficients. */
	private ImmutableArray<Integer> indices;

	private Polynomial(Map<Integer, C> coefficients, C zeroCoefficient, C oneCoefficient) {
		this.coefficients = coefficients;
		this.zeroCoefficient = zeroCoefficient;
		this.oneCoefficient = oneCoefficient;

		int maxIndex = 0;
		boolean isBinary = true;
		for (Integer index : coefficients.keySet()) {
			maxIndex = Math.max(maxIndex, index);
			isBinary = this.oneCoefficient.equals(coefficients.get(index)) && isBinary;
		}
		this.degree = maxIndex;

		if (isBinary) {
			byte[] bytes = new byte[(int) Math.ceil((this.degree + 1) / 8.0)];
			Arrays.fill(bytes, (byte) 0x00);
			for (Integer index : coefficients.keySet()) {
				int byteIndex = index / Byte.SIZE;
				int bitIndex = index % Byte.SIZE;
				bytes[byteIndex] = (byte) (bytes[byteIndex] | (0x01 << bitIndex));
			}
			this.binaryCoefficients = ByteArray.getInstance(bytes);
		} else {
			this.binaryCoefficients = null;
		}
	}

	private Polynomial(ByteArray coefficients) {
		this.coefficients = null;
		this.binaryCoefficients = coefficients;
		this.zeroCoefficient = (C) Boolean.FALSE;
		this.oneCoefficient = (C) Boolean.TRUE;

		int byteIndex = 0;
		for (int i = 0; i < this.binaryCoefficients.getLength(); i++) {
			if (this.binaryCoefficients.getAt(i) != 0) {
				byteIndex = i;
			}
		}
		byte b = coefficients.getLength() > 0 ? coefficients.getAt(byteIndex) : 0;
		int bitIndex = Integer.SIZE - Integer.numberOfLeadingZeros(b & 0xff);
		this.degree = Math.max(0, byteIndex * Byte.SIZE + bitIndex - 1);
	}

	public int getDegree() {
		return this.degree;
	}

	public boolean isBinary() {
		return this.binaryCoefficients != null;
	}

	public C getCoefficient(int index) {
		if (index < 0) {
			throw new IllegalArgumentException();
		}
		if (this.isBinary()) {
			int byteIndex = index / Byte.SIZE;
			int bitIndex = index % Byte.SIZE;
			if (byteIndex >= this.binaryCoefficients.getLength()) {
				return this.zeroCoefficient;
			}
			return ((this.binaryCoefficients.getAt(byteIndex) >> bitIndex) & 1) == 1 ? this.oneCoefficient : this.zeroCoefficient;
		} else {
			C coefficient = this.coefficients.get(index);
			return coefficient == null ? this.zeroCoefficient : coefficient;
		}
	}

	public ByteArray getCoefficients() {
		if (!this.isBinary()) {
			throw new UnsupportedOperationException();
		}
		return this.binaryCoefficients;
	}

	public final ImmutableArray<Integer> getIndices() {

		if (this.indices == null) {
			if (this.isBinary()) {
				ArrayList<Integer> ind = new ArrayList();
				for (int i = 0; i < this.binaryCoefficients.getLength(); i++) {
					for (int j = 0; j < Byte.SIZE; j++) {
						if (((this.binaryCoefficients.getAt(i) >> j) & 1) == 1) {
							ind.add(i * Byte.SIZE + j);
						}
					}
				}
				this.indices = ImmutableArray.getInstance(ind);
			} else {
				TreeSet ind = new TreeSet(this.coefficients.keySet());
				this.indices = ImmutableArray.getInstance(ind);
			}
		}
		return this.indices;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + (this.coefficients != null ? this.coefficients.hashCode() : this.binaryCoefficients.hashCode());
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Polynomial<C> other = (Polynomial<C>) obj;

		if (this.isBinary()) {
			return this.binaryCoefficients == other.binaryCoefficients || this.binaryCoefficients.equals(other.binaryCoefficients);
		} else {
			return this.coefficients == other.coefficients || this.coefficients.equals(other.coefficients);
		}
	}

	@Override
	public String defaultToStringValue() {
		String result = "f(x)=";

		String separator = "";
		if (this.getIndices().getLength() == 0) {
			result += this.coefficientToString(this.zeroCoefficient);
		}
		for (Integer index : this.getIndices()) {
			C coefficient = this.getCoefficient(index);
			if (coefficient != this.zeroCoefficient || this.getDegree() == 0) {
				result += separator;
				if (coefficient != this.oneCoefficient || index == 0) {
					result += this.coefficientToString(coefficient);
				}
				if (index > 0) {
					result += index == 1 ? "X" : "X^" + index;
				}
				separator = "+";
			}
		}

		return result;
	}

	private String coefficientToString(C coefficient) {
		if (coefficient instanceof Boolean) {
			return coefficient == this.zeroCoefficient ? "0" : "1";
		}
		return coefficient.toString();
	}

	public static <C> Polynomial<C> getInstance(Map<Integer, C> coefficients, C zeroCoefficient, C oneCoefficient) {
		if (coefficients == null || zeroCoefficient == null || oneCoefficient == null) {
			throw new IllegalArgumentException();
		}
		Map<Integer, C> result = new HashMap<Integer, C>();
		for (Integer i : coefficients.keySet()) {
			C coeff = coefficients.get(i);
			if (coeff == null) {
				throw new IllegalArgumentException();
			}
			if (!coeff.equals(zeroCoefficient)) {
				result.put(i, coeff);
			}
		}
		return new Polynomial<C>(result, zeroCoefficient, oneCoefficient);
	}

	public static <C> Polynomial<C> getInstance(C[] coefficients, C zeroCoefficient, C oneCoefficient) {
		if (coefficients == null || zeroCoefficient == null || oneCoefficient == null) {
			throw new IllegalArgumentException();
		}

		Map<Integer, C> result = new HashMap<Integer, C>();
		for (int i = 0; i < coefficients.length; i++) {
			C coeff = coefficients[i];
			if (coeff == null) {
				throw new IllegalArgumentException();
			}
			if (!coeff.equals(zeroCoefficient)) {
				result.put(i, coeff);
			}
		}
		return new Polynomial<C>(result, zeroCoefficient, oneCoefficient);
	}

	public static Polynomial<Boolean> getInstance(ByteArray coefficients) {
		if (coefficients == null) {
			throw new IllegalArgumentException();
		}
		return new Polynomial<Boolean>(coefficients.getLength() == 0 ? ByteArray.getInstance(0x00) : coefficients);
	}

}
