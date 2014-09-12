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
package ch.bfh.unicrypt.math.algebra.general.abstracts;

import ch.bfh.unicrypt.helper.UniCrypt;
import ch.bfh.unicrypt.helper.array.classes.ByteArray;
import ch.bfh.unicrypt.helper.bytetree.ByteTree;
import ch.bfh.unicrypt.helper.converter.BigIntegerConverter;
import ch.bfh.unicrypt.helper.converter.ConvertMethod;
import ch.bfh.unicrypt.helper.converter.Converter;
import ch.bfh.unicrypt.helper.hash.HashMethod;
import ch.bfh.unicrypt.helper.numerical.Numerical;
import ch.bfh.unicrypt.math.algebra.additive.interfaces.AdditiveElement;
import ch.bfh.unicrypt.math.algebra.concatenative.interfaces.ConcatenativeElement;
import ch.bfh.unicrypt.math.algebra.dualistic.interfaces.DualisticElement;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.CyclicGroup;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Group;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Monoid;
import ch.bfh.unicrypt.math.algebra.general.interfaces.SemiGroup;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Set;
import ch.bfh.unicrypt.math.algebra.multiplicative.interfaces.MultiplicativeElement;
import java.math.BigInteger;
import java.util.HashMap;

/**
 * This abstract class represents the concept an element in a mathematical group. It allows applying the group operation
 * and other methods from a {@link Group} in a convenient way. Most methods provided by {@link AbstractElement} have an
 * equivalent method in {@link Group}.
 * <p>
 * @param <S> Generic type of {@link Set} of this element
 * @param <E> Generic type of this element
 * @param <V> Generic type of value stored in this element
 * @see Group
 * <p>
 * @author R. Haenni
 * @author R. E. Koenig
 * @version 2.0
 */
public abstract class AbstractElement<S extends Set<V>, E extends Element<V>, V extends Object>
	   extends UniCrypt
	   implements Element<V> {

	private final S set;
	private final V value;

	// the following fields are needed for optimizations
	private BigInteger bigInteger;
	private final HashMap<Converter, ByteArray> byteArrays;
	private final HashMap<HashMethod, ByteArray> hashValues;

	protected AbstractElement(final S set, V value) {
		this.set = set;
		this.value = value;
		this.byteArrays = new HashMap<Converter, ByteArray>();
		this.hashValues = new HashMap<HashMethod, ByteArray>();
	}

	@Override
	public boolean isAdditive() {
		return this instanceof AdditiveElement;
	}

	@Override
	public boolean isMultiplicative() {
		return this instanceof MultiplicativeElement;
	}

	@Override
	public boolean isConcatenative() {
		return this instanceof ConcatenativeElement;
	}

	@Override
	public boolean isDualistic() {
		return this instanceof DualisticElement;
	}

	@Override
	public final boolean isTuple() {
		return this instanceof Tuple;
	}

	/**
	 * Returns the unique {@link Set} to which this element belongs
	 * <p>
	 * @return The element's set
	 */
	@Override
	public final S getSet() {
		return this.set;
	}

	/**
	 * Returns the positive BigInteger value that corresponds the element.
	 * <p>
	 * @return The corresponding BigInteger value
	 */
	@Override
	public final V getValue() {
		return this.value;
	}

	@Override
	public BigInteger getBigInteger() {
		if (this.bigInteger == null) {
			this.bigInteger = this.set.getBigIntegerFrom(this);
		}
		return this.bigInteger;
	}

	@Override
	public ByteArray getByteArray() {
		return this.getByteArray(BigIntegerConverter.getInstance());
	}

	@Override
	public ByteArray getByteArray(BigIntegerConverter converter) {
		if (converter == null) {
			throw new IllegalArgumentException();
		}
		ByteArray byteArray = this.byteArrays.get(converter);
		if (byteArray == null) {
			byteArray = converter.convertToByteArray(this.getBigInteger());
			this.byteArrays.put(converter, byteArray);
		}
		return byteArray;
	}

	@Override
	public ByteArray getByteArray(Converter<V> converter) {
		if (converter == null) {
			throw new IllegalArgumentException();
		}
		ByteArray byteArray = this.byteArrays.get(converter);
		if (byteArray == null) {
			byteArray = converter.convertToByteArray(this.value);
		}
		return byteArray;
	}

	@Override
	public ByteArray getByteArray(ConvertMethod converterMethod) {
		if (converterMethod == null) {
			throw new IllegalArgumentException();
		}
		Converter converter = converterMethod.getConverter(this.value.getClass());
		if (converter == null) {
			converter = converterMethod.getConverter(BigInteger.class);
			if (converter == null) {
				return this.getByteArray();
			}
			return this.getByteArray((BigIntegerConverter) converter);
		}
		return this.getByteArray((Converter<V>) converter);
	}

	@Override
	public ByteTree getByteTree() {
		return this.getByteTree(ConvertMethod.getInstance());
	}

	@Override
	public ByteTree getByteTree(ConvertMethod convertMethod) {
		if (convertMethod == null) {
			throw new IllegalArgumentException();
		}
		if (this.isTuple()) {
			Tuple tuple = (Tuple) this;
			ByteTree[] byteTrees = new ByteTree[tuple.getArity()];
			int i = 0;
			for (Element<V> element : tuple) {
				byteTrees[i++] = element.getByteTree(convertMethod);
			}
			return ByteTree.getInstance(byteTrees);

		}
		return ByteTree.getInstance(this.getByteArray(convertMethod));
	}

	@Override
	public final ByteArray getHashValue() {
		return this.getHashValue(HashMethod.getInstance());
	}

	@Override
	public final ByteArray getHashValue(HashMethod hashMethod) {
		if (hashMethod == null) {
			throw new IllegalArgumentException();
		}
		ByteArray hashValue = this.hashValues.get(hashMethod);
		if (hashValue == null) {
			switch (hashMethod.getMode()) {
				case BYTEARRAY:
					hashValue = this.getByteArray().getHashValue(hashMethod.getHashAlgorithm());
					break;
				case BYTETREE:
					hashValue = this.getByteTree(hashMethod.getConvertMethod()).getHashValue(hashMethod.getHashAlgorithm());
					break;
				case RECURSIVE:
					hashValue = this.getByteTree(hashMethod.getConvertMethod()).getRecursiveHashValue(hashMethod.getHashAlgorithm());
					break;
				default:
					throw new UnsupportedOperationException();
			}
			this.hashValues.put(hashMethod, hashValue);
		}
		return hashValue;
	}

	//
	// The following methods are equivalent to corresponding Set methods
	//
	/**
	 * @see Group#apply(Element, Element)
	 */
	@Override
	public final E apply(final Element element) {
		if (this.set.isSemiGroup()) {
			SemiGroup semiGroup = ((SemiGroup) this.set);
			return (E) semiGroup.apply(this, element);
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Group#applyInverse(Element, Element)
	 */
	@Override
	public final E applyInverse(final Element element) {
		if (this.set.isGroup()) {
			Group group = ((Group) this.set);
			return (E) group.applyInverse(this, element);
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Group#selfApply(Element, BigInteger)
	 */
	@Override
	public final E selfApply(final BigInteger amount) {
		if (this.set.isSemiGroup()) {
			SemiGroup semiGroup = ((SemiGroup) this.set);
			return (E) semiGroup.selfApply(this, amount);
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Group#selfApply(Element, Element)
	 */
	@Override
	public final E selfApply(final Element<Numerical> amount) {
		if (this.set.isSemiGroup()) {
			SemiGroup semiGroup = ((SemiGroup) this.set);
			return (E) semiGroup.selfApply(this, amount);
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Group#selfApply(Element, int)
	 */
	@Override
	public final E selfApply(final int amount) {
		if (this.set.isSemiGroup()) {
			SemiGroup semiGroup = ((SemiGroup) this.set);
			return (E) semiGroup.selfApply(this, amount);
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Group#selfApply(Element)
	 */
	@Override
	public final E selfApply() {
		if (this.set.isSemiGroup()) {
			SemiGroup semiGroup = ((SemiGroup) this.set);
			return (E) semiGroup.selfApply(this);
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Group#invert(Element)
	 */
	@Override
	public final E invert() {
		if (this.set.isGroup()) {
			Group group = ((Group) this.set);
			return (E) group.invert(this);
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Group#isIdentityElement(Element)
	 */
	@Override
	public final boolean isIdentity() {
		if (this.set.isMonoid()) {
			Monoid monoid = ((Monoid) this.set);
			return monoid.isIdentityElement(this);
		}
		throw new UnsupportedOperationException();
	}

	/**
	 * @see CyclicGroup#isGenerator(Element)
	 */
	@Override
	public final boolean isGenerator() {
		if (this.set.isCyclic()) {
			CyclicGroup cyclicGroup = ((CyclicGroup) this.set);
			return cyclicGroup.isGenerator(this);
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean isEquivalent(final Element other) {
		if (other == null) {
			throw new IllegalArgumentException();
		}
		if (this == other) {
			return true;
		}
		if (!this.set.isEquivalent(other.getSet())) {
			return false;
		}
		return this.value.equals(other.getValue());
	}

	@Override
	public int hashCode() {
		int hashCode = 7;
		hashCode = 13 * hashCode + this.set.hashCode();
		hashCode = 13 * hashCode + this.value.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || this.getClass() != object.getClass()) {
			return false;
		}
		final Element other = (Element) object;
		if (!this.set.equals(other.getSet())) {
			return false;
		}
		return this.value.equals(other.getValue());
	}

	@Override
	protected String defaultToStringName() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected String defaultToStringValue() {
		return this.value.toString();
	}

}
