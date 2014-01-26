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
package ch.bfh.unicrypt.math.algebra.general.classes;

import ch.bfh.unicrypt.math.algebra.concatenative.classes.StringElement;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.StringMonoid;
import ch.bfh.unicrypt.math.algebra.general.abstracts.AbstractElement;
import ch.bfh.unicrypt.math.helper.ByteArray;
import ch.bfh.unicrypt.math.helper.bytetree.ByteTreeLeaf;
import java.math.BigInteger;

/**
 *
 * @author rolfhaenni
 */
public class FiniteStringElement
	   extends AbstractElement<FiniteStringSet, FiniteStringElement, String> {

	protected FiniteStringElement(final FiniteStringSet set, final String string) {
		super(set, string);
	}

	public int getLength() {
		return this.getValue().length();
	}

	public StringElement getStringElement() {
		return StringMonoid.getInstance(this.getSet().getAlphabet()).getElement(this.getValue());
	}

	@Override
	protected BigInteger abstractGetBigInteger() {
		int length = this.getLength();
		int minLength = this.getSet().getMinLength();
		BigInteger value = BigInteger.ZERO;
		BigInteger size = BigInteger.valueOf(this.getSet().getAlphabet().getSize());
		for (int i = 0; i < length; i++) {
			int charIndex = this.getSet().getAlphabet().getIndex(this.getValue().charAt(i));
			if (i < length - minLength) {
				charIndex++;
			}
			value = value.multiply(size).add(BigInteger.valueOf(charIndex));
		}
		return value;
	}

	@Override
	protected ByteTreeLeaf abstractGetByteTree() {
		return ByteTreeLeaf.getInstance(ByteArray.getInstance(this.getBigInteger().toByteArray()));
	}

	@Override
	public String standardToStringContent() {
		return "\"" + this.getValue() + "\"";
	}

}
