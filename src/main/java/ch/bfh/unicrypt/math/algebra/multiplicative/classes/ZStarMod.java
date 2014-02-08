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
package ch.bfh.unicrypt.math.algebra.multiplicative.classes;

import ch.bfh.unicrypt.crypto.random.classes.HybridRandomByteSequence;
import ch.bfh.unicrypt.crypto.random.interfaces.RandomByteSequence;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Group;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Set;
import ch.bfh.unicrypt.math.algebra.multiplicative.abstracts.AbstractMultiplicativeGroup;
import ch.bfh.unicrypt.math.helper.ByteArray;
import ch.bfh.unicrypt.math.helper.bytetree.ByteTree;
import ch.bfh.unicrypt.math.helper.bytetree.ByteTreeLeaf;
import ch.bfh.unicrypt.math.helper.factorization.Factorization;
import ch.bfh.unicrypt.math.helper.numerical.NaturalNumber;
import ch.bfh.unicrypt.math.helper.numerical.ResidueClass;
import ch.bfh.unicrypt.math.utility.MathUtil;
import java.math.BigInteger;

/**
 * This class implements the group of integers Z*_n with the operation of multiplication modulo n. Its identity element
 * is 1. Every integer in Z*_n is relatively prime to n. The smallest such group is Z*_2 = {1}.
 * <p>
 * @see "Handbook of Applied Cryptography, Definition 2.124"
 * @see <a
 * href="http://en.wikipedia.org/wiki/Multiplicative_group_of_integers_modulo_n">http://en.wikipedia.org/wiki/Multiplicative_group_of_integers_modulo_n</a>
 * <p>
 * @author R. Haenni
 * @author R. E. Koenig
 * @version 2.0
 */
public class ZStarMod
	   extends AbstractMultiplicativeGroup<ZStarModElement, ResidueClass> {

	private final BigInteger modulus;
	private final Factorization moduloFactorization;

	/**
	 * This is a private constructor of this class. It is called by the static factory methods.
	 * <p>
	 * @param modulus The given modulus
	 */
	protected ZStarMod(final BigInteger modulus) {
		this(modulus, Factorization.getInstance());
	}

	/**
	 * This is a private constructor of this class. It is called by the static factory methods.
	 * <p>
	 * @param factorization The given factorization
	 */
	protected ZStarMod(final Factorization factorization) {
		this(factorization.getValue(), factorization);
	}

	/**
	 * This is a private constructor of this class. It is called by the static factory methods.
	 * <p>
	 * @param modulus       The given modulus
	 * @param factorization The given factorization
	 */
	protected ZStarMod(final BigInteger modulus, final Factorization factorization) {
		super(ResidueClass.class);
		this.modulus = modulus;
		this.moduloFactorization = factorization;
	}

	/**
	 * Returns the modulus if this group.
	 * <p>
	 * @return The modulus
	 */
	public final BigInteger getModulus() {
		return this.modulus;
	}

	/**
	 * Returns a (possibly incomplete) prime factorization the modulus if this group. An incomplete factorization
	 * implies that the group order is unknown in such a case.
	 * <p>
	 * @return The prime factorization
	 */
	public final Factorization getModuloFactorization() {
		return this.moduloFactorization;
	}

	public final boolean contains(int integerValue) {
		return this.contains(ResidueClass.getInstance(BigInteger.valueOf(integerValue), this.modulus));
	}

	public final boolean contains(BigInteger integerValue) {
		return this.contains(ResidueClass.getInstance(integerValue, this.modulus));
	}

	public final ZStarModElement getElement(int integerValue) {
		return this.getElement(ResidueClass.getInstance(BigInteger.valueOf(integerValue), this.modulus));
	}

	public final ZStarModElement getElement(BigInteger integerValue) {
		return this.getElement(ResidueClass.getInstance(integerValue, this.modulus));
	}

	//
	// The following protected methods override the default implementation from
	// various super-classes
	//
	@Override
	protected ZStarModElement defaultSelfApply(final ZStarModElement element, final BigInteger amount) {
		BigInteger newAmount = amount;
		if (this.hasKnownOrder()) {
			newAmount = amount.mod(this.getOrder());
		}
		return this.abstractGetElement(element.getValue().power(NaturalNumber.getInstance(newAmount)));
	}

	@Override
	protected BigInteger defaultGetOrderUpperBound() {
		return this.getModulus().subtract(BigInteger.ONE);
	}

	@Override
	public String defaultToStringValue() {
		return this.getModulus().toString();
	}

	//
	// The following protected methods implement the abstract methods from
	// various super-classes
	//
	@Override
	protected boolean abstractContains(final ResidueClass value) {
		return this.modulus.equals(value.getModulus()) && value.isRelativelyPrime();
	}

	@Override
	protected ZStarModElement abstractGetElement(ResidueClass value) {
		return new ZStarModElement(this, value);
	}

	@Override
	protected ZStarModElement abstractGetElementFrom(BigInteger value) {
		if (value.signum() == 0 || value.compareTo(this.getModulus()) >= 0 || !MathUtil.areRelativelyPrime(value, this.getModulus())) {
			return null; // no such element
		}
		return new ZStarModElement(this, ResidueClass.getInstance(value, this.modulus));
	}

	@Override
	protected BigInteger abstractGetBigIntegerFrom(ResidueClass value) {
		return value.getBigInteger();
	}

	@Override
	protected ZStarModElement abstractGetElementFrom(ByteTree bytTree) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected ByteTree abstractGetByteTreeFrom(ResidueClass value) {
		return ByteTreeLeaf.getInstance(ByteArray.getInstance(value.getBigInteger().toByteArray()));
	}

	@Override
	protected ZStarModElement abstractGetRandomElement(final RandomByteSequence randomByteSequence) {
		BigInteger randomValue;
		do {
			randomValue = randomByteSequence.getRandomNumberGenerator().nextBigInteger(BigInteger.ONE, this.getModulus().subtract(BigInteger.ONE));
		} while (!this.contains(randomValue));
		return this.abstractGetElement(ResidueClass.getInstance(randomValue, this.modulus));
	}

	@Override
	protected BigInteger abstractGetOrder() {
		if (!this.getModuloFactorization().getValue().equals(this.getModulus())) {
			return Group.UNKNOWN_ORDER;
		}
		return MathUtil.eulerFunction(this.getModulus(), this.getModuloFactorization().getPrimeFactors());
	}

	@Override
	protected ZStarModElement abstractGetIdentityElement() {
		return this.abstractGetElement(ResidueClass.getInstance(BigInteger.ONE, this.modulus));
	}

	@Override
	protected ZStarModElement abstractApply(final ZStarModElement element1, final ZStarModElement element2) {
		return this.abstractGetElement(element1.getValue().multiply(element2.getValue()));
	}

	@Override
	public ZStarModElement abstractInvert(final ZStarModElement element) {
		return this.abstractGetElement(ResidueClass.getInstance(element.getValue().getBigInteger().modInverse(this.modulus), this.modulus));
	}

	@Override
	protected boolean abstractEquals(final Set set) {
		final ZStarMod zStarMod = (ZStarMod) set;
		return this.getModulus().equals(zStarMod.getModulus());
	}

	@Override
	protected int abstractHashCode() {
		int hash = 7;
		hash = 47 * hash + this.getModulus().hashCode();
		return hash;
	}

	//
	// STATIC FACTORY METHODS
	//
	public static ZStarMod getInstance(final int modulus) {
		return ZStarMod.getInstance(BigInteger.valueOf(modulus));
	}

	/**
	 * This is a static factory method to construct a new instance of this class for a given {@literal modulus >= 2}. If
	 * {@literal modulus} is not prime, then a group of unknown order is returned.
	 * <p>
	 * @param modulus The modulus
	 * @return
	 * @throws IllegalArgumentException if {@literal modulus} is null or smaller than 2
	 */
	public static ZStarMod getInstance(final BigInteger modulus) {
		if (modulus == null || modulus.compareTo(BigInteger.ONE) <= 0) {
			throw new IllegalArgumentException();
		}
		if (MathUtil.isPrime(modulus)) {
			return new ZStarMod(modulus, Factorization.getInstance(new BigInteger[]{modulus}));
		}
		return new ZStarMod(modulus);
	}

	/**
	 * This is a static factory method to construct a new instance of this class, where the group's modulus is value of
	 * the given prime factorization. This always leads to a group of known order.
	 * <p>
	 * @param factorization The given prime factorization
	 * @return
	 * @throws IllegalArgumentException if {@literal primeFactorization} is null
	 * @throws IllegalArgumentException if {@literal primeFactorization.getValue()} is 1
	 */
	public static ZStarMod getInstance(final Factorization factorization) {
		if (factorization == null || factorization.getValue().compareTo(BigInteger.ONE) <= 0) {
			throw new IllegalArgumentException();
		}
		return new ZStarMod(factorization);
	}

	public static ZStarMod getRandomInstance(int bitLength, RandomByteSequence randomByteSequence) {
		if (bitLength < 1) {
			throw new IllegalArgumentException();
		}
		if (randomByteSequence == null) {
			randomByteSequence = HybridRandomByteSequence.getInstance();
		}
		return ZStarMod.getInstance(randomByteSequence.getRandomNumberGenerator().nextBigInteger(bitLength));
	}

	public static ZStarMod getRandomInstance(int bitLength) {
		return ZStarMod.getRandomInstance(bitLength, null);
	}

}
