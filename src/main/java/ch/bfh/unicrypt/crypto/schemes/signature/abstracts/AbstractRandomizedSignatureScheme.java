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
package ch.bfh.unicrypt.crypto.schemes.signature.abstracts;

import ch.bfh.unicrypt.crypto.keygenerator.interfaces.KeyPairGenerator;
import ch.bfh.unicrypt.crypto.schemes.signature.interfaces.RandomizedSignatureScheme;
import ch.bfh.unicrypt.helper.hash.ElementHashMethod;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Set;
import ch.bfh.unicrypt.random.classes.HybridRandomByteSequence;
import ch.bfh.unicrypt.random.interfaces.RandomByteSequence;

/**
 *
 * @author rolfhaenni
 * @param <MS>  Message space
 * @param <ME>  Message element
 * @param <SS>  Signature space
 * @param <SE>  Signature element
 * @param <RS>  Randomization space
 * @param <SKS> Signature key space
 * @param <VKS> Verification key space
 * @param <KG>  Key generator
 */
public abstract class AbstractRandomizedSignatureScheme<MS extends Set, ME extends Element, SS extends Set,
	   SE extends Element, RS extends Set, SKS extends Set, VKS extends Set, KG extends KeyPairGenerator>
	   extends AbstractSignatureScheme<MS, ME, SS, SE, SKS, VKS, KG>
	   implements RandomizedSignatureScheme {
	private static final long serialVersionUID = 1L;

	protected final RS randomizationSpace;

	public AbstractRandomizedSignatureScheme(MS messageSpace, SS signatureSpace, RS randomizationSpace, ElementHashMethod hashMethod) {
		super(messageSpace, signatureSpace, hashMethod);
		this.randomizationSpace = randomizationSpace;
	}

	@Override
	public RS getRandomizationSpace() {
		return this.randomizationSpace;
	}

	@Override
	public SE sign(final Element privateKey, final Element message) {
		return this.sign(privateKey, message, HybridRandomByteSequence.getInstance());
	}

	@Override
	public SE sign(Element privateKey, Element message, RandomByteSequence randomByteSequence) {
		return this.sign(privateKey, message, this.randomizationSpace.getRandomElement(randomByteSequence));
	}

	@Override
	public SE sign(Element privateKey, Element message, Element randomization) {
		return (SE) this.getSignatureFunction().apply(privateKey, message, randomization);
	}

}
