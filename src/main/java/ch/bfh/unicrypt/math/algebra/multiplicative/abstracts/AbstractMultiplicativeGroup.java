/*
 * UniCrypt
 *
 *  UniCrypt(tm): Cryptographical framework allowing the implementation of cryptographic protocols e.g. e-voting
 *  Copyright (c) 2016 Bern University of Applied Sciences (BFH), Research Institute for
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
package ch.bfh.unicrypt.math.algebra.multiplicative.abstracts;

import ch.bfh.unicrypt.helper.array.interfaces.ImmutableArray;
import ch.bfh.unicrypt.helper.sequence.Sequence;
import ch.bfh.unicrypt.math.algebra.general.abstracts.AbstractGroup;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.multiplicative.interfaces.MultiplicativeElement;
import ch.bfh.unicrypt.math.algebra.multiplicative.interfaces.MultiplicativeGroup;
import java.math.BigInteger;

/**
 * This abstract class provides a basis implementation for objects of type {@link MultiplicativeGroup}.
 * <p>
 * @param <E> The generic type of the elements of this group
 * @param <V> The generic type of the values stored in the elements of this group
 * <p>
 * @author R. Haenni
 */
public abstract class AbstractMultiplicativeGroup<E extends MultiplicativeElement<V>, V>
	   extends AbstractGroup<E, V>
	   implements MultiplicativeGroup<V> {

	protected AbstractMultiplicativeGroup(Class<?> valueClass) {
		super(valueClass);
	}

	@Override
	public final E multiply(final Element element1, final Element element2) {
		return this.apply(element1, element2);
	}

	@Override
	public final E multiply(final Element... elements) {
		return this.apply(elements);
	}

	@Override
	public final E multiply(final ImmutableArray<Element> elements) {
		return this.apply(elements);
	}

	@Override
	public final E multiply(final Sequence<Element> elements) {
		return this.apply(elements);
	}

	@Override
	public final E power(final Element element, final long exponent) {
		return this.selfApply(element, exponent);
	}

	@Override
	public final E power(final Element element, final BigInteger exponent) {
		return this.selfApply(element, exponent);
	}

	@Override
	public final E power(final Element element, final Element<BigInteger> exponent) {
		return this.selfApply(element, exponent);
	}

	@Override
	public final E square(Element element) {
		return this.selfApply(element);
	}

	@Override
	public final E productOfPowers(Element[] elements, BigInteger[] exponents) {
		return this.multiSelfApply(elements, exponents);
	}

	@Override
	public final E getOneElement() {
		return this.getIdentityElement();
	}

	@Override
	public final boolean isOneElement(Element element) {
		return this.isIdentityElement(element);
	}

	@Override
	public final E oneOver(final Element element) {
		return this.invert(element);
	}

	@Override
	public final E divide(final Element element1, final Element element2) {
		return this.applyInverse(element1, element2);
	}

	@Override
	public final E nthRoot(Element element, long n) {
		return this.invertSelfApply(element, n);
	}

	@Override
	public final E nthRoot(Element element, BigInteger n) {
		return this.invertSelfApply(element, n);
	}

	@Override
	public final E nthRoot(Element element, Element<BigInteger> n) {
		return this.invertSelfApply(element, n);
	}

	@Override
	public final E squareRoot(Element element) {
		return this.invertSelfApply(element);
	}

}
