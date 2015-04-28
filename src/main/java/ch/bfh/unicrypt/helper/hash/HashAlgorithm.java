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
package ch.bfh.unicrypt.helper.hash;

import ch.bfh.unicrypt.helper.UniCrypt;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Instances of this class represent hash algorithms such as SHA1 or SHA256. This class is a wrapper class for
 * {@link MessageDigest} with method names adjusted to the conventions of UniCrypt. A hash algorithm maps arbitrary Java
 * byte arrays into fixed-length byte arrays. The resulting byte array is called hash value of the input.
 * <p>
 * @author R. Haenni
 * @author R. E. Koenig
 * @version 2.0
 */
public class HashAlgorithm
	   extends UniCrypt {

	public static HashAlgorithm MD5 = new HashAlgorithm("MD5");
	public static HashAlgorithm SHA1 = new HashAlgorithm("SHA-1");
	public static HashAlgorithm SHA256 = new HashAlgorithm("SHA-256");
	public static HashAlgorithm SHA384 = new HashAlgorithm("SHA-384");
	public static HashAlgorithm SHA512 = new HashAlgorithm("SHA-512");

	private final MessageDigest messageDigest;

	private HashAlgorithm(String algorithmName) {
		try {
			this.messageDigest = MessageDigest.getInstance(algorithmName);
		} catch (final NoSuchAlgorithmException e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Returns the hash value of a given byte array.
	 * <p>
	 * @param bytes The given byte array
	 * @return The hash value of the given byte array
	 */
	public byte[] getHashValue(byte[] bytes) {
		if (bytes == null) {
			throw new IllegalArgumentException();
		}
		return this.messageDigest.digest(bytes);
	}

	/**
	 * Returns the hash value of some bytes extracted from a given byte array.
	 * <p>
	 * @param bytes  The given byte array
	 * @param offset The offset to start from in the given byte array
	 * @param length The number of bytes to use, starting at {@code offset}
	 * @return The hash value of the bytes extracted from the given byte array
	 */
	public byte[] getHashValue(byte[] bytes, int offset, int length) {
		if (bytes == null || offset < 0 || offset + length > bytes.length) {
			throw new IllegalArgumentException();
		}
		this.messageDigest.update(bytes, offset, length);
		return this.messageDigest.digest();
	}

	/**
	 * Returns the byte length (number of bytes) of the hash values returned by this hash algorithm.
	 * <p>
	 * @return The byte length
	 */
	public int getByteLength() {
		return this.messageDigest.getDigestLength();
	}

	/**
	 * Returns the bit length (number of bits) of the hash values returned by this hash algorithm.
	 * <p>
	 * @return The bit length
	 */
	public int getBitLength() {
		return this.getByteLength() * Byte.SIZE;
	}

	/**
	 * Returns the library's default hash algorithm SHA-256.
	 * <p>
	 * @return The default hash algorithm
	 */
	public static HashAlgorithm getInstance() {
		return HashAlgorithm.SHA256;
	}

	/**
	 * Returns the hash algorithm that corresponds to the algorithm name specified as a string. The supported algorithm
	 * names are: {@code "MD5"}, {@code "SHA1"}, {@code "SHA256"}, {@code "SHA384"}, and {@code "SHA512"}.
	 * <p>
	 * @param algorithmName The name of the hash algorithm.
	 * @return
	 */
	public static HashAlgorithm getInstance(String algorithmName) {
		switch (Arrays.binarySearch(new String[]{"MD5", "SHA1", "SHA256", "SHA384", "SHA512"}, algorithmName)) {
			case 0:
				return HashAlgorithm.MD5;
			case 1:
				return HashAlgorithm.SHA1;
			case 2:
				return HashAlgorithm.SHA256;
			case 3:
				return HashAlgorithm.SHA384;
			case 4:
				return HashAlgorithm.SHA512;
		}
		throw new IllegalArgumentException();
	}

	@Override
	protected String defaultToStringContent() {
		return this.messageDigest.getAlgorithm();
	}

}
