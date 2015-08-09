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
package ch.bfh.unicrypt.helper.array.classes;

import ch.bfh.unicrypt.helper.array.abstracts.AbstractImmutableArray;
import ch.bfh.unicrypt.helper.array.interfaces.ImmutableArray;
import ch.bfh.unicrypt.helper.sequence.functions.Predicate;
import ch.bfh.unicrypt.helper.sequence.Sequence;
import java.util.Collection;

/**
 * This class is a default implementation of the {@link ImmutableArray} interface. It is optimized for arrays for which
 * no designated default value exists, arrays with almost no equal values, or arrays with only equal values. Internally,
 * the values are either stored in an ordinary (possibly empty) array or as an array of length 1 together with the full
 * length of the immutable array (all elements are equal in that case). The implementation is optimized to provide O(1)
 * running times for most operations.
 * <p>
 * @see SparseArray
 * @author Rolf Haenni
 * @version 2.0
 * @param <V> The generic type of the values in the immutable array
 */
public class DenseArray<V>
	   extends AbstractImmutableArray<DenseArray<V>, V> {

	private static final long serialVersionUID = 1L;

	private final Object[] values;

	protected DenseArray(V value, int length) {
		this(new Object[]{value}, length, 0, false);
		this.uniform = true;
	}

	protected DenseArray(Object[] values) {
		this(values, values.length, 0, false);
	}

	protected DenseArray(Object[] values, int length, int rangeOffset, boolean reverse) {
		super(DenseArray.class, length, rangeOffset, reverse);
		this.values = values;
	}

	/**
	 * Creates a new dense array from a given Java array of values. The Java array is copied for internal storage. The
	 * length and the indices of the values of the resulting dense array correspond to the given Java array.
	 * <p>
	 * @param <V>    The generic type of the new array
	 * @param values The Java array of values
	 * @return The new sparse array
	 * @see SparseArray#getInstance(Object, Object...)
	 */
	public static <V> DenseArray<V> getInstance(V... values) {
		return DenseArray.getInstance(Sequence.getInstance(values));
	}

	public static <V> DenseArray<V> getInstance(Collection<V> values) {
		return DenseArray.getInstance(Sequence.getInstance(values));
	}

	/**
	 * Creates a new dense array from a given sequence of values. The sequence is transformed into a Java array for
	 * internal storage. Null values are eliminated.
	 * <p>
	 * @param <V>    The generic type of the new array
	 * @param values The given sequence of values
	 * @return The new dense array
	 */
	public static <V> DenseArray<V> getInstance(Sequence<V> values) {
		if (values == null || values.isInfinite()) {
			throw new IllegalArgumentException();
		}
		values = values.filter(Predicate.NOT_NULL);
		Object[] array = new Object[values.getLength().intValue()];
		int i = 0;
		for (V value : values) {
			array[i++] = value;
		}
		return new DenseArray<>(array);
	}

	/**
	 * Creates a new dense array of a given length. All its values are identical to the given value. This method is
	 * similar to {@link SparseArray#getInstance(java.lang.Object, int)}, except that a dense array is created instead
	 * of a sparse array.
	 * <p>
	 * @param <V>       The generic type of the new array
	 * @param fillValue The value included in the new array
	 * @param length    The length of the new array
	 * @return The new sparse array
	 * @see SparseArray#getInstance(java.lang.Object, int)
	 */
	public static <V> DenseArray<V> getInstance(V fillValue, int length) {
		if (fillValue == null || length < 0) {
			throw new IllegalArgumentException();
		}
		return new DenseArray(fillValue, length);
	}

	@Override
	protected V abstractGetAt(int index) {
		if (this.reverse) {
			index = this.length - index - 1;
		}
		return (V) this.values[(this.rangeOffset + index) % this.values.length];
	}

	@Override
	protected DenseArray<V> abstractExtract(int index, int length) {
		int offset = this.rangeOffset + (this.reverse ? this.length - index - length : index);
		return new DenseArray<>(this.values, length, offset, this.reverse);
	}

	@Override
	protected DenseArray<V> abstractAppend(ImmutableArray<V> other) {
		Object[] result = new Object[this.length + other.getLength()];
		for (int i : this.getAllIndices()) {
			result[i] = this.abstractGetAt(i);
		}
		for (int i : other.getAllIndices()) {
			result[this.length + i] = other.getAt(i);
		}
		return new DenseArray<>(result);
	}

	@Override
	protected DenseArray<V> abstractInsertAt(int index, V newObject) {
		Object[] result = new Object[this.length + 1];
		for (int i : this.getAllIndices()) {
			if (i < index) {
				result[i] = this.abstractGetAt(i);
			} else {
				result[i + 1] = this.abstractGetAt(i);
			}
		}
		result[index] = newObject;
		return new DenseArray<>(result);
	}

	@Override
	protected DenseArray<V> abstractReplaceAt(int index, V newObject) {
		Object[] result = new Object[this.length];
		for (int i : this.getAllIndices()) {
			result[i] = this.abstractGetAt(i);
		}
		result[index] = newObject;
		return new DenseArray<>(result);
	}

	@Override
	protected DenseArray<V> abstractReverse() {
		return new DenseArray<>(this.values, this.length, this.rangeOffset, !this.reverse);
	}

	@Override
	protected String defaultToStringContent() {
		String str = "";
		String delimiter = "";
		for (int i : this.getAllIndices()) {
			str = str + delimiter + this.abstractGetAt(i);
			delimiter = ", ";
		}
		return "[" + str + "]";
	}

}
