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

import ch.bfh.unicrypt.crypto.keygenerator.classes.RSAKeyPairGenerator;
import ch.bfh.unicrypt.crypto.keygenerator.interfaces.KeyPairGenerator;
import ch.bfh.unicrypt.crypto.schemes.encryption.abstracts.AbstractAsymmetricEncryptionScheme;
import ch.bfh.unicrypt.crypto.schemes.signature.abstracts.AbstractSignatureScheme;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZMod;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZModElement;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZModPrimePair;
import ch.bfh.unicrypt.math.algebra.general.classes.ProductSet;
import ch.bfh.unicrypt.math.function.classes.AdapterFunction;
import ch.bfh.unicrypt.math.function.classes.CompositeFunction;
import ch.bfh.unicrypt.math.function.classes.EqualityFunction;
import ch.bfh.unicrypt.math.function.classes.IdentityFunction;
import ch.bfh.unicrypt.math.function.classes.MultiIdentityFunction;
import ch.bfh.unicrypt.math.function.classes.PowerFunction;
import ch.bfh.unicrypt.math.function.classes.ProductFunction;
import ch.bfh.unicrypt.math.function.classes.SelectionFunction;
import ch.bfh.unicrypt.math.function.interfaces.Function;

public class RSASignatureScheme
	   extends AbstractSignatureScheme<ZMod, ZModElement, ZMod, ZModElement, ZMod, ZMod, RSAKeyPairGenerator> {

	
	private final ZMod zMod;

	protected RSASignatureScheme(ZMod zMod) {
		this.zMod = zMod;
	}

	public ZMod getZMod() {
		return this.zMod;
	}

	@Override
	protected RSAKeyPairGenerator abstractGetKeyPairGenerator() {
		if (this.getZMod() instanceof ZModPrimePair) {
			return RSAKeyPairGenerator.getInstance((ZModPrimePair) this.getZMod());
		}
		throw new UnsupportedOperationException();
	}

	@Override
	protected Function abstractGetSignatureFunction() {
		return CompositeFunction.getInstance(
			   AdapterFunction.getInstance(ProductSet.getInstance(this.zMod, 2), 1, 0),
			   PowerFunction.getInstance(zMod));
	}

	@Override
	protected Function abstractGetVerificationFunction() {
		ProductSet ps = ProductSet.getInstance(this.zMod, 3);
		
		return CompositeFunction.getInstance(
			   MultiIdentityFunction.getInstance(ps, 2),
			   ProductFunction.getInstance(
					SelectionFunction.getInstance(ps, 1),
						CompositeFunction.getInstance(
							AdapterFunction.getInstance(ps, 2, 0),
							PowerFunction.getInstance(zMod)
					)
			   ), 
			   EqualityFunction.getInstance(this.zMod)						 
			);			   
	}
	
	public static RSASignatureScheme getInstance(ZMod zMod) {
		if (zMod == null) {
			throw new IllegalArgumentException();
		}
		return new RSASignatureScheme(zMod);
	}

	public static RSASignatureScheme getInstance(ZModElement key) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
		return new RSASignatureScheme(key.getSet());
	}


}