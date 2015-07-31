/*
 * UniCrypt
 *
 *  UniCrypt(tm): Cryptographic framework allowing the implementation of cryptographic protocols, e.g. e-voting
 *  Copyright (C) 2015 Bern University of Applied Sciences (BFH), Research Institute for
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
package ch.bfh.unicrypt.helper.sequence.abstracts;

import ch.bfh.unicrypt.UniCrypt;
import ch.bfh.unicrypt.helper.array.classes.DenseArray;
import ch.bfh.unicrypt.helper.math.MathUtil;
import ch.bfh.unicrypt.helper.sequence.interfaces.Mapping;
import ch.bfh.unicrypt.helper.sequence.interfaces.Predicate;
import ch.bfh.unicrypt.helper.sequence.classes.FilteredSequence;
import ch.bfh.unicrypt.helper.sequence.classes.GroupedSequence;
import ch.bfh.unicrypt.helper.sequence.classes.MappedSequence;
import ch.bfh.unicrypt.helper.sequence.classes.ShortenedSequence;
import ch.bfh.unicrypt.helper.sequence.interfaces.Sequence;
import java.math.BigInteger;
import java.util.Collection;

/**
 *
 * @author rolfhaenni
 * @param <V>
 */
public abstract class AbstractSequence<V>
	   extends UniCrypt
	   implements Sequence<V> {

	private BigInteger length;

	@Override
	public final boolean isEmpty() {
		return this.getLength().signum() == 0;
	}

	@Override
	public final BigInteger getLength() {
		if (this.length == null) {
			this.length = this.abstractGetLength();
		}
		return this.length;
	}

	@Override
	public final int count(Predicate<? super V> predicate) {
		if (predicate == null) {
			throw new IllegalArgumentException();
		}
		if (this.getLength().equals(Sequence.INFINITE)) {
			throw new UnsupportedOperationException();
		}
		int counter = 0;
		for (V value : this) {
			if (predicate.check(value)) {
				counter++;
			}
		}
		return counter;
	}

	@Override
	public final V find(Predicate<? super V> predicate) {
		return this.find(predicate, 1);
	}

	@Override
	public final V find(Predicate<? super V> predicate, int n) {
		for (V value : this.filter(predicate)) {
			n--;
			if (n == 0) {
				return value;
			}
		}
		return null;
	}

	@Override
	public final <W> MappedSequence<V, W> map(Mapping<? super V, ? extends W> mapping) {
		if (mapping == null) {
			throw new IllegalArgumentException();
		}
		return MappedSequence.getInstance(this, mapping);
	}

	@Override
	public final FilteredSequence<V> filter(Predicate<? super V> predicate) {
		if (predicate == null) {
			throw new IllegalArgumentException();
		}
		return FilteredSequence.getInstance(this, predicate);
	}

	@Override
	public final ShortenedSequence<V> shorten(int maxLength) {
		return this.shorten(BigInteger.valueOf(maxLength));
	}

	@Override
	public final ShortenedSequence<V> shorten(BigInteger maxLength) {
		if (maxLength == null || maxLength.signum() < 0) {
			throw new IllegalArgumentException();
		}
		return ShortenedSequence.getInstance(this, maxLength);
	}

	@Override
	public final Sequence<DenseArray<V>> group(int groupLength) {
		if (groupLength < 1) {
			throw new IllegalArgumentException();
		}
		return GroupedSequence.getInstance(this, groupLength);
	}

	protected abstract BigInteger abstractGetLength();

	// helper method used to compute the length of something that is iterable
	protected static <V> BigInteger getLength(Iterable<V> values) {
		if (values instanceof Sequence) {
			return ((Sequence<V>) values).getLength();
		}
		if (values instanceof Collection) {
			return BigInteger.valueOf(((Collection<V>) values).size());
		}
		if (values.iterator().hasNext()) {
			return Sequence.UNKNOWN;
		}
		return MathUtil.ZERO;
	}

}