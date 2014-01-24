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
package ch.bfh.unicrypt.math.helper;

import java.util.Iterator;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Rolf Haenni <rolf.haenni@bfh.ch>
 */
public class ImmutableArrayTest {

	private static ImmutableArray<String> a1 = ImmutableArray.getInstance("s1", "s2", "s3");
	private static ImmutableArray<String> a2 = ImmutableArray.getInstance("s1", "s1", "s1");
	private static ImmutableArray<String> a3 = ImmutableArray.getInstance("s1", 3);

	public ImmutableArrayTest() {
	}

	@Test
	public void testGetLength() {
		assertEquals(3, a1.getLength());
		assertEquals(3, a2.getLength());
		assertEquals(3, a3.getLength());
	}

	@Test
	public void testGetAt() {
		assertEquals("s1", a1.getAt(0));
		assertEquals("s2", a1.getAt(1));
		assertEquals("s3", a1.getAt(2));
		assertEquals("s1", a2.getAt(0));
		assertEquals("s1", a2.getAt(1));
		assertEquals("s1", a2.getAt(2));
		assertEquals("s1", a3.getAt(0));
		assertEquals("s1", a3.getAt(1));
		assertEquals("s1", a3.getAt(2));
	}

	@Test
	public void testGetAll() {
		assertArrayEquals(new String[]{"s1", "s2", "s3"}, a1.getAll());
		assertArrayEquals(new String[]{"s1", "s1", "s1"}, a2.getAll());
		assertArrayEquals(new String[]{"s1", "s1", "s1"}, a3.getAll());
	}

	@Test
	public void testIterator() {
		Iterator<String> it2 = a2.iterator();
		Iterator<String> it3 = a3.iterator();
		while (it2.hasNext()) {
			assertEquals(it2.next(), it3.next());
		}
	}

	@Test
	public void testEquals() {
		assertTrue(a1.equals(a1));
		assertFalse(a1.equals(a2));
		assertFalse(a1.equals(a3));
		assertTrue(a2.equals(a2));
		assertTrue(a2.equals(a3));
		assertTrue(a3.equals(a3));
	}

}
