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
package ch.bfh.unicrypt.crypto.schemes.encryption.classes;

import ch.bfh.unicrypt.crypto.keygenerator.classes.FixedByteArrayKeyGenerator;
import ch.bfh.unicrypt.crypto.random.interfaces.RandomGenerator;
import ch.bfh.unicrypt.crypto.schemes.encryption.abstracts.AbstractSymmetricEncryptionScheme;
import ch.bfh.unicrypt.math.algebra.general.classes.FiniteByteArrayElement;
import ch.bfh.unicrypt.math.algebra.general.classes.FiniteByteArraySet;
import ch.bfh.unicrypt.math.algebra.general.classes.FixedByteArraySet;
import ch.bfh.unicrypt.math.algebra.general.classes.Pair;
import ch.bfh.unicrypt.math.algebra.general.classes.ProductSet;
import ch.bfh.unicrypt.math.function.abstracts.AbstractFunction;
import ch.bfh.unicrypt.math.function.interfaces.Function;
import ch.bfh.unicrypt.math.helper.ByteArray;

/**
 *
 * @author rolfhaenni
 */
public class OneTimePadEncryptionScheme
	   extends AbstractSymmetricEncryptionScheme<FiniteByteArraySet, FiniteByteArrayElement, FiniteByteArraySet, FiniteByteArrayElement, FixedByteArraySet, FixedByteArrayKeyGenerator> {

	private final FiniteByteArraySet finiteByteArraySet;
	private final FixedByteArraySet fixedByteArraySet;

	protected OneTimePadEncryptionScheme(int maxLength) {
		this.finiteByteArraySet = FiniteByteArraySet.getInstance(maxLength);
		this.fixedByteArraySet = FixedByteArraySet.getInstance(maxLength);
	}

	protected OneTimePadEncryptionScheme(FiniteByteArraySet finiteByteArraySet) {
		this.finiteByteArraySet = finiteByteArraySet;
		this.fixedByteArraySet = FixedByteArraySet.getInstance(finiteByteArraySet.getMaxLength());
	}

	@Override
	protected Function abstractGetEncryptionFunction() {
		return new OneTimePadFunction();
	}

	@Override
	protected Function abstractGetDecryptionFunction() {
		return new OneTimePadFunction();
	}

	@Override
	protected FixedByteArrayKeyGenerator abstractGetKeyGenerator() {
		return FixedByteArrayKeyGenerator.getInstance(this.fixedByteArraySet);
	}

	public static OneTimePadEncryptionScheme getInstance(int maxLength) {
		if (maxLength < 0) {
			throw new IllegalArgumentException();
		}
		return new OneTimePadEncryptionScheme(maxLength);
	}

	public static OneTimePadEncryptionScheme getInstance(FiniteByteArraySet finiteByteArraySet) {
		if (finiteByteArraySet == null) {
			throw new IllegalArgumentException();
		}
		return new OneTimePadEncryptionScheme(finiteByteArraySet);
	}

	private class OneTimePadFunction
		   extends AbstractFunction<ProductSet, Pair, FiniteByteArraySet, FiniteByteArrayElement> {

		protected OneTimePadFunction() {
			super(ProductSet.getInstance(fixedByteArraySet, finiteByteArraySet), finiteByteArraySet);
		}

		@Override
		protected FiniteByteArrayElement abstractApply(Pair element, RandomGenerator randomGenerator) {
			ByteArray byteArray1 = ((FiniteByteArrayElement) element.getFirst()).getValue();
			ByteArray byteArray2 = ((FiniteByteArrayElement) element.getSecond()).getValue();
			return this.getCoDomain().getElement(byteArray1.xor(byteArray2));
		}

	}

}
