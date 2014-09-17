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
package ch.bfh.unicrypt.crypto.schemes.signature.classes;

import ch.bfh.unicrypt.crypto.keygenerator.classes.RSAKeyGenerator;
import ch.bfh.unicrypt.crypto.schemes.signature.abstracts.AbstractSignatureScheme;
import ch.bfh.unicrypt.helper.hash.HashMethod;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZMod;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZModElement;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZModPrimePair;
import ch.bfh.unicrypt.math.algebra.general.classes.ProductSet;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Set;
import ch.bfh.unicrypt.math.function.classes.AdapterFunction;
import ch.bfh.unicrypt.math.function.classes.CompositeFunction;
import ch.bfh.unicrypt.math.function.classes.ConvertFunction;
import ch.bfh.unicrypt.math.function.classes.EqualityFunction;
import ch.bfh.unicrypt.math.function.classes.HashFunction;
import ch.bfh.unicrypt.math.function.classes.MultiIdentityFunction;
import ch.bfh.unicrypt.math.function.classes.PowerFunction;
import ch.bfh.unicrypt.math.function.classes.ProductFunction;
import ch.bfh.unicrypt.math.function.classes.SelectionFunction;
import ch.bfh.unicrypt.math.function.interfaces.Function;

public class RSASignatureScheme
	   extends AbstractSignatureScheme<Set, Element, ZMod, ZModElement, ZMod, ZMod, RSAKeyGenerator> {

	private final ZMod zMod;

	protected RSASignatureScheme(Set messageSpace, ZMod zMod, HashMethod hashMethod) {
		super(messageSpace, zMod, hashMethod);
		this.zMod = zMod;
	}

	public ZMod getZMod() {
		return this.zMod;
	}

	@Override
	protected RSAKeyGenerator abstractGetKeyPairGenerator() {
		// keys can only be generated if p and q are known
		if (this.zMod instanceof ZModPrimePair) {
			return RSAKeyGenerator.getInstance((ZModPrimePair) this.zMod);
		}
		throw new UnsupportedOperationException();
	}

	@Override
	protected Function abstractGetSignatureFunction() {
		ProductSet inputSpace = ProductSet.getInstance(this.zMod, this.messageSpace);
		HashFunction hashFunction = HashFunction.getInstance(this.messageSpace, this.hashMethod);
		return CompositeFunction.getInstance(
			   MultiIdentityFunction.getInstance(inputSpace, 2),
			   ProductFunction.getInstance(
					  CompositeFunction.getInstance(
							 SelectionFunction.getInstance(inputSpace, 1),
							 hashFunction,
							 ConvertFunction.getInstance(hashFunction.getCoDomain(), this.zMod)),
					  SelectionFunction.getInstance(inputSpace, 0)),
			   //computes m^d
			   PowerFunction.getInstance(this.zMod));
	}

	@Override
	protected Function abstractGetVerificationFunction() {
		ProductSet inputSpace = ProductSet.getInstance(this.zMod, this.messageSpace, this.signatureSpace);
		HashFunction hashFunction = HashFunction.getInstance(this.messageSpace, this.hashMethod);
		return CompositeFunction.getInstance(
			   //duplicate the input for the power function (s^e) and equality function m=s^e
			   MultiIdentityFunction.getInstance(inputSpace, 2),
			   //product function: selection of m and computation of s^e
			   ProductFunction.getInstance(
					  //select parameter 1 (message) to comute hash and pass it later to equality function
					  CompositeFunction.getInstance(
							 SelectionFunction.getInstance(inputSpace, 1),
							 hashFunction,
							 ConvertFunction.getInstance(hashFunction.getCoDomain(), this.zMod)),
					  CompositeFunction.getInstance(
							 //takes parameter 2 (signature) and 0 (public key) and pass them in that order
							 AdapterFunction.getInstance(inputSpace, 2, 0),
							 //compute power function of parameter received from adapter function => s^e
							 PowerFunction.getInstance(this.zMod)
					  )
			   ),
			   //receive output of selection function (m) and composite function (s^e) and check their equality
			   EqualityFunction.getInstance(this.zMod)
		);
	}

	public static RSASignatureScheme getInstance(ZMod zMod) {
		return RSASignatureScheme.getInstance(zMod, zMod);
	}

	public static RSASignatureScheme getInstance(Set messageSpace, ZMod zMod) {
		return RSASignatureScheme.getInstance(messageSpace, zMod, HashMethod.getInstance());
	}

	public static RSASignatureScheme getInstance(Set messageSpace, ZMod zMod, HashMethod hashMethod) {
		if (messageSpace == null || zMod == null || hashMethod == null) {
			throw new IllegalArgumentException();
		}
		return new RSASignatureScheme(messageSpace, zMod, hashMethod);
	}

	public static RSASignatureScheme getInstance(ZModElement key) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
		return RSASignatureScheme.getInstance(key.getSet());
	}

	public static RSASignatureScheme getInstance(Set messageSpace, ZModElement key) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
		return RSASignatureScheme.getInstance(messageSpace, key.getSet());
	}

	public static RSASignatureScheme getInstance(Set messageSpace, ZModElement key, HashMethod hashMethod) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
		return new RSASignatureScheme(messageSpace, key.getSet(), hashMethod);
	}

}
