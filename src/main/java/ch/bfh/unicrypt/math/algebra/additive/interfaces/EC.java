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
package ch.bfh.unicrypt.math.algebra.additive.interfaces;

import ch.bfh.unicrypt.helper.Point;
import ch.bfh.unicrypt.helper.bytetree.ByteTree;
import ch.bfh.unicrypt.math.algebra.dualistic.interfaces.DualisticElement;
import ch.bfh.unicrypt.math.algebra.dualistic.interfaces.FiniteField;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.random.classes.ReferenceRandomByteSequence;
import ch.bfh.unicrypt.random.interfaces.RandomByteSequence;
import java.math.BigInteger;

/**
 * TODO This interface represents an elliptic curve. Its set of points creates an additive group so that adding two
 * points creates another point.
 * <p>
 *
 * @author Rolf Haenni <rolf.haenni@bfh.ch>
 * @param <V> Generic type of values stored in the elements of the elliptic curve
 * @param <E>
 */
public interface EC<V extends Object, E extends DualisticElement<V>>
	   extends
	   AdditiveCyclicGroup<Point<E>> {

	/**
	 * Returns the finite Field of this elliptic curve
	 * <p>
	 *
	 * @return The finite field of this elliptic curve
	 */
	public FiniteField<V> getFiniteField();

	/**
	 * Returns the co-efficient a of this elliptic curve.
	 * <p>
	 *
	 * @return The co-efficient a
	 */
	public E getA();

	/**
	 * Returns the co-efficient b of this elliptic curve.
	 * <p>
	 *
	 * @return The co-efficient b
	 */
	public E getB();

	/**
	 * Returns the cofactor of this elliptic curve.
	 * <p>
	 *
	 * @return The cofactor
	 */
	public BigInteger getCoFactor();

	/**
	 * TODO Returns {@code true} if this elliptic curve lies on the given x co-ordinate.
	 * <p>
	 *
	 * @param xValue The given x value
	 * @return {@code true} if this elliptic curve lies on the given x co-ordinate
	 * @throws IllegalArgumentException if {@code xValue} do not belong to the finite field
	 */
	public boolean contains(E xValue);

	/**
	 * TODO Returns {@code true} if the given x and y value are the co-ordinates for a point on this elliptic curve.
	 * <p>
	 *
	 * @param xValue The given xValue
	 * @param yValue The given yValue
	 * @return {@code true} if xValue and yValue form a point on this elliptic curve.
	 * @throws IllegalArgumentException if {@code xValue} or {@code yValue} do not belong to the finite field
	 */
	public boolean contains(E xValue, E yValue);

	/**
	 * TODO Returns the corresponding point for a given x and y value.
	 * <p>
	 *
	 * @param xValue The given xValue
	 * @param yValue The given yValue
	 * @return The corresponding point for a given x and y value
	 * @throws IllegalArgumentException if {@code xValue} or {@code yValue} do not belong to this elliptic curve
	 */
	public ECElement<V, E> getElement(E xValue, E yValue);

	/**
	 * Return the two possible y-coordinates for a given valid x-coordinate
	 * <p>
	 * @param xValue x-coordinate of point
	 * @return
	 */
	public ECElement<V, E>[] getY(E xValue);

	//The following methods are overwritten from AdditiveSemiGroup with an adapted return type
	@Override
	public ECElement<V, E> add(Element element1, Element element2);

	@Override
	public ECElement<V, E> add(Element... elements);

	@Override
	public ECElement<V, E> add(Iterable<Element> elements);

	@Override
	public ECElement<V, E> times(Element element, BigInteger amount);

	@Override
	public ECElement<V, E> times(Element element, Element<BigInteger> amount);

	@Override
	public ECElement<V, E> times(Element element, int amount);

	@Override
	public ECElement<V, E> timesTwo(Element element);

	// The following methods are overridden from Set with an adapted return type
	@Override
	public ECElement<V, E> getElementFrom(int value);

	@Override
	public ECElement<V, E> getElementFrom(BigInteger value);

	@Override
	public ECElement<V, E> getElementFrom(ByteTree byteTree);

	@Override
	public ECElement<V, E> getRandomElement();

	@Override
	public ECElement<V, E> getRandomElement(
		   RandomByteSequence randomByteSequence);

	// The following methods are overridden from SemiGroup with an adapted
	// return type
	@Override
	public ECElement<V, E> apply(Element element1, Element element2);

	@Override
	public ECElement<V, E> apply(Element... elements);

	@Override
	public ECElement<V, E> selfApply(Element element, BigInteger amount);

	@Override
	public ECElement<V, E> selfApply(Element element, Element<BigInteger> amount);

	@Override
	public ECElement<V, E> selfApply(Element element, int amount);

	@Override
	public ECElement<V, E> selfApply(Element element);

	@Override
	public ECElement<V, E> multiSelfApply(Element[] elements,
		   BigInteger[] amounts);

	// The following methods are overridden from Monoid with an adapted return
	// type
	@Override
	public ECElement<V, E> invert(Element element);

	@Override
	public ECElement<V, E> applyInverse(Element element1, Element element2);

	@Override
	public ECElement<V, E> getIdentityElement();

	// The following methods are overridden from Group with an adapted return
	// type
	@Override
	public ECElement<V, E> getDefaultGenerator();

	@Override
	public ECElement<V, E> getRandomGenerator();

	@Override
	public ECElement<V, E> getRandomGenerator(
		   RandomByteSequence randomByteSequence);

	@Override
	public ECElement<V, E> getIndependentGenerator(int index);

	@Override
	public ECElement<V, E> getIndependentGenerator(int index,
		   ReferenceRandomByteSequence referenceRandomByteSequence);

}
