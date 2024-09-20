package cn.nextop.gadget.core.util.collection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * @author Baoyi Chen
 */
public class SwissMapTest2 {
	
	/**
	 * 
	 */
	@Test
	public void test1() {
		//
		SwissMap<Integer, Integer> m1;
		m1 = new SwissMap<> (8); /*!*/
		
		//
		Iterator<Integer> i1;
		i1 = m1.keySet().iterator();
		try { i1.next(); Assertions.fail();
		} catch (NoSuchElementException nop) {
		} catch (Throwable txt) { fail(txt); }
		
		//
		try { i1.remove(); fail();
		} catch (IllegalStateException nop) {
		} catch (Throwable txt) { fail(txt); }
	}
	
	/**
	 * 
	 */
	@Test
	public void test2() {
		//
		final int n = 20;
		SwissMap<Integer, Integer> m1;
		m1 = new SwissMap<> (8); /*!*/
		m1.setHash(new ConflictHash());
		var v1 = m1.computeIfPresent(1, (k, v) -> v);
		
		//
		Assertions.assertNull(v1);
		for (int i = 0; i < n; i++) { m1.put(i, i);
			m1.computeIfPresent(i , (k, v) -> v + 1);
		}
		for (int i = 0; i < n; i++) {
			Assertions.assertEquals(i + 1, m1.get(i));
		}
		
		//
		for (int i = 0; i < n; i++) {
			v1 = m1.computeIfPresent(i, (k, v) -> null);
			Assertions.assertNull(v1); /*** removed ***/
		}
		final int size = m1.size(); assertEquals(0, size);
	}
	
	@Test
	public void test3() {
		//
		final int n = 20;
		SwissMap<Integer, Integer> m1;
		m1 = new SwissMap<> (8); /*!*/
		m1.setHash(new ConflictHash());
		
		for (int i = 0; i < n; i++) { int j = i;
			assertNull(m1.compute(i, (k, v) -> null));
			assertEquals(i, m1.compute(i, (k, v) -> j));
		}
		
		//
		for (int i = 0; i < n; i++) { int j = i;
			assertEquals(i + 1, m1.compute(i, (k, v) -> j + 1));
			final Integer v = m1.get(i); assertEquals(i + 1, v);
		}
		
		//
		for (int i = 0; i < n; i++) {
			Assertions.assertNull(m1.compute(i, (k, v) -> null));
		}
		int size = m1.size(); Assertions.assertEquals( 0, size );
	}
	
	@Test
	public void test4() {
		//
		final int n = 20;
		SwissMap<Integer, Integer> m1;
		m1 = new SwissMap<> (8); /*!*/
		m1.setHash(new ConflictHash());
		
		for (int i = 0; i < n; i++) {
			assertNull(m1.putIfAbsent(i, i));
		}
		assertEquals(n, m1.size()); m1.clear();
		
		//
		for (int i = 0; i < n; i++) {
			var v = m1.merge(i, i, (v1, v2) -> null);
			Assertions.assertEquals(i, v);/*** i ***/
		}
		Assertions.assertEquals(n, m1.size()); m1.clear();
		
		//
		for (int i = 0; i < n; i++) { m1.put(i, null);
			assertEquals(i, m1.merge(i, i, (k, v) -> -1));
			final var v1 = m1.get(i); assertEquals(i, v1);
			assertNull(m1.merge(i , i , (k , v) -> null));
		}
		Assertions.assertEquals(0, m1.size()); m1.clear();
		
		//
		for (int i = 0; i < n; i++) {
			m1.put(i, i);
		}
		for (int i = 0; i < n; i++) {
			assertNull(m1.merge(i, i, (k, v) -> null));
		}
		Assertions.assertEquals(0, m1.size()); m1.clear();
	}
	
	/**
	 * 
	 */
	@Test
	public void test5() {
		//
		SwissMap<Integer, Integer> m1;
		m1 = new SwissMap<> (8); /*!*/
		Set<Integer> v = m1.keySet ();
		for (int i = 0; i < 11; i++) {
			m1.put(i, i);
		}
		
		//
		v.remove(1); // dead
		v.remove(2); // dead
		v.remove(10); // delete
		
		assertEquals(8, v.size());
		assertFalse(v.isEmpty());
		assertFalse(v.contains(1));
		assertFalse(v.contains(2));
		assertFalse(v.contains(10));
		
		//
		var a = new Integer[]{0, 3, 4, 5, 6, 7, 8, 9};
		Assertions.assertArrayEquals(a , v.toArray());
		assertArrayEquals(a, v.toArray(new Integer[]{}));
		
		Iterator<Integer> i1 = v.iterator();
		List<Integer> b = new ArrayList<>(8);
		while (i1.hasNext()) { b.add(i1.next()); }
		assertArrayEquals(a , b.toArray(Integer[]::new));
		
		i1 = v.iterator();
		while (i1.hasNext ()) { i1.next(); i1.remove(); }
		Assertions.assertEquals(0, m1.size()); v.clear();
	}
	
	@Test
	public void test6() {
		//
		SwissMap<Integer, Integer> m1;
		m1 = new SwissMap<> (8); /*!*/
		for (int i = 0; i < 11; i++) {
			m1.put(i, i);
		}
		
		//
		m1.remove(1); // dead
		m1.remove(2); // dead
		m1.remove(10); // delete
		
		final var v = m1.values();
		assertEquals(8, v.size());
		assertFalse(v.isEmpty ());
		assertFalse(v.contains(1));
		assertFalse(v.contains(2));
		assertFalse(v.contains(10));
		
		//
		var a = new Integer[]{0, 3, 4, 5, 6, 7, 8, 9};
		Assertions.assertArrayEquals(a , v.toArray());
		assertArrayEquals(a, v.toArray(new Integer[]{}));
		
		//
		Iterator<Integer> i1 = v.iterator();
		List<Integer> b = new ArrayList<>(8);
		while (i1.hasNext()) { b.add(i1.next()); }
		assertArrayEquals(a , b.toArray(Integer[]::new));
		
		i1 = v.iterator();
		while (i1.hasNext()) { i1.next(); i1.remove(); }
		Assertions.assertEquals(0, m1.size()); v.clear();
	}
	
	@Test
	public void test7() {
		//
		SwissMap<Integer, Integer> m1;
		m1 = new SwissMap<> (8); /*!*/
		for (int i = 0; i < 11; i++) {
			m1.put(i, i);
		}
		
		//
		final var a = m1.entrySet();
		a.remove(new SimpleEntry<>(1, 1));
		a.remove(new SimpleEntry<>(2, 2));
		a.remove(new SimpleEntry<>(10, 10));
		
		Assertions.assertFalse(a.isEmpty());
		Assertions.assertEquals(8, a.size());
		assertFalse(a.contains(new SimpleEntry<>(1, 1)));
		assertFalse(a.contains(new SimpleEntry<>(2, 2)));
		assertFalse(a.contains(new SimpleEntry<>(10, 10)));
		
		//
		var i1 = a.iterator();
		List<Integer> b = new ArrayList<>(8);
		List<Integer> c = new ArrayList<>(8);
		while (i1.hasNext()) { var x = i1.next();
			b.add(x.getKey()); c.add(x.getValue());
		}
		
		var arr = new Integer[]{0, 3, 4, 5, 6, 7, 8, 9};
		assertArrayEquals(arr, b.toArray(Integer[]::new));
		assertArrayEquals(arr, c.toArray(Integer[]::new));
		
		i1 = a.iterator();
		while (i1.hasNext()) { i1.next(); i1.remove(); }
		Assertions.assertEquals(0, m1.size()); a.clear();
	}
	
	/**
	 * 
	 */
	@Test
	public void test8() {
		//
		SwissMap<Integer, Integer> m1;
		m1 = new SwissMap<> (8); /*!*/
		m1.remove(11); m1.remove(123);
		for (int i = 0; i < 11; i++) {
			m1.put(i, i);
		}
		
		//
		m1.remove(1); // dead
		m1.remove(2); // dead
		m1.remove(10); // delete
		
		assertFalse(m1.containsKey(1));
		assertFalse(m1.containsKey(2));
		assertFalse(m1.containsKey(10));
		assertFalse(m1.containsValue(1));
		assertFalse(m1.containsValue(2));
		assertFalse(m1.containsValue(10));
		
		//
		{
			List<Integer> a;
			a = new ArrayList<>(8);
			m1.forEach((k, v) -> a.add(v));
			
			var b = new Integer[]{0, 3, 4, 5, 6, 7, 8, 9};
			for(var v : b) assertTrue(m1.containsValue(v));
			assertArrayEquals(b, a.toArray(Integer[]::new));
		}
		
		//
		{
			List<Integer> a;
			a = new ArrayList<>(8);
			m1.replaceAll((k, v) -> v + 1);
			m1.forEach((k, v) -> { a.add(v); });
			
			var b = new Integer[]{1, 4, 5, 6, 7, 8, 9, 10};
			for(var v : b) assertTrue(m1.containsValue(v));
			assertArrayEquals(b, a.toArray(Integer[]::new));
		}
		
		m1.put(-1, null);
		Assertions.assertNull(m1.getOrDefault(-1, +1));
		Assertions.assertEquals(+2, m1.getOrDefault(-2, +2));
		
		m1.put(null, -1);
		Assertions.assertEquals(-1, m1.get(null));
		Assertions.assertEquals(-1, m1.getOrDefault(null, -2));
	}
	
	/**
	 * 
	 */
	@RepeatedTest(value = 16)
	@Execution(ExecutionMode.CONCURRENT)
	public void test9() { test9(24); test9(32); test9(64); }
	
	private void test9(int n) {
		
		SwissMap<Integer, Integer> m;
		m = new SwissMap<> (n); /*!*/
		m.setHash(new ConflictHash());
		
		//
		{
			assertNull(m.replace(-1, -1));
			assertFalse(m.replace(-1, -1, +1));
			
			for (int i = 0; i < 9; i++) {
				m.put(i, i);
				m.replace(i, i + 1);
				m.replace(i, i + 1, i);
			}
			
			// |    group      |      group    |
			// |0|1|2|3|4|5|6|7|8| | | | | | | |
			for (int i = 0; i < 9; i++) {
				assertEquals(i, m.get(i));
				assertTrue(m.containsKey(i));
			}
		}
		
		//
		{
			m.clear();
			assertNull(m.replace(-1, -1));
			assertFalse(m.replace(-1, -1, +1));
			
			for (int i = 0; i < 9; i++) {
				m.put(i, i);
				m.replace(i, i + 1);
				m.replace(i, i + 1, i);
			}
			
			// not exist
			assertNull(m.remove(-1));
			assertFalse(m.remove(-1, -1));
			assertFalse(m.remove(-1, +1));
			
			// |    group      |      group    |
			// |0|-|-|3|4|5|6|7|8| | | | | | | |
			m.remove(1); // dead
			m.remove(2); // dead
			assertNull(m.get(1));
			assertNull(m.get(2));
			for (int i = 0; i < 9; i++) {
				if (i != 1 && i != 2) {
					assertEquals(i, m.get(i));
					assertTrue(m.containsKey(i));
				}
			}
			
			assertNull(m.putIfAbsent(9, 9));
			assertEquals(9, m.putIfAbsent(9, 9));
			Assertions.assertEquals(9, m.put(9, 9));
			
			// |    group      |      group    |
			// |0|-|-|3|4|5|6|7|8|9| | | | | | |
			int prev = m.size();
			assertEquals(9, m.get(9));
			assertNull(m.computeIfAbsent(10, k -> null));
			assertEquals(prev, m.size());
			
			assertEquals(10, m.computeIfAbsent(10, k -> 10));
			assertEquals(prev + 1, m.size());
			
			assertEquals(10, m.computeIfAbsent(10, k -> 11));
			assertEquals(prev + 1, m.size());
			
			// resize
			for (int i = 0; i < 100; i++) { final int j = i;
				assertEquals(i, m.computeIfAbsent(i, k -> j));
			}
		}
		
		//
		{
			m.clear();
			assertNull(m.replace(-1, -1));
			assertFalse(m.replace(-1, -1, +1));
			
			for (int i = 0; i < 9; i++) {
				m.put(i, i);
				m.replace(i, i + 1);
				m.replace(i, i + 1, i);
			}
			
			// |    group      |      group    |
			// |-|-|-|-|-|-|-|-|8| | | | | | | |
			for (int i = 0; i < 8; i++) {
				m.remove(i);
			}
			for (int i = 0; i < 8; i++) {
				assertFalse(m.containsKey(i));
				Assertions.assertNull(m.get(i));
			}
			
			assertEquals(8, m.get(8));
			assertNull(m.putIfAbsent(9, 9));
			assertEquals(9, m.putIfAbsent(9, 9));
			Assertions.assertEquals(9, m.put(9, 9));
			
			// |    group      |      group    |
			// |-|-|-|-|-|-|-|-|8|9| | | | | | |
			assertEquals(9, m.get(9)); m.remove(8);
			
			// |    group      |      group    |
			// |-|-|-|-|-|-|-|-| |9| | | | | | |
			int prev = m.size();
			assertNull(m.get(8));
			assertEquals(9, m.get(9));
			assertNull(m.computeIfAbsent(10, k -> null));
			Assertions.assertEquals(prev, m.size());/*!*/
			
			assertEquals(10, m.computeIfAbsent(10, k -> 10));
			Assertions.assertEquals(prev + 1, m.size());/*!*/
			
			assertEquals(10, m.computeIfAbsent(10, k -> 11));
			Assertions.assertEquals(prev + 1, m.size());/*!*/
			
			// resize
			for (int i = 0; i < 100; i++) { final int j = i;
				assertEquals(i, m.computeIfAbsent(i, k -> j));
			}
		}
		
		//
		{
			m.clear();
			assertNull(m.replace(-1, -1));
			assertFalse(m.replace(-1, -1, +1));
			
			for (int i = 0; i < 6; i++) {
				m.put(i, i);
				m.replace(i, i + 1);
				m.replace(i, i + 1, i);
			}
			
			// not exist
			assertNull(m.remove(-1));
			assertFalse(m.remove(-1, -1));
			assertFalse(m.remove(-1, +1));
			
			// |    group      |      group    |
			// |0| | |3|4|5| | | | | | | | | | |
			m.remove(1); // deleted
			m.remove(2); // deleted
			Assertions.assertNull(m.get(1));
			Assertions.assertNull(m.get(2));
			
			for (int i = 0; i < 6; i++) {
				if ( i != 1 && i != 2 ) {
					assertEquals(i, m.get(i));
					assertTrue(m.containsKey(i));
				}
			}
			
			// |    group      |      group    |
			// | | | |3|4|5| | | | | | | | | | |
			m.remove(0);
			assertNull(m.putIfAbsent(6, 6));
			assertEquals(6, m.putIfAbsent(6, 6));
			Assertions.assertEquals(6, m.put(6, 6));
			
			// |    group      |      group    |
			// |6| | |3|4|5| | | | | | | | | | |
			int prev = m.size();
			assertEquals(6, m.get(6));
			assertNull(m.computeIfAbsent(10, k -> null));
			Assertions.assertEquals(prev, m.size());/*!*/
			
			assertEquals(10, m.computeIfAbsent(10, k -> 10));
			Assertions.assertEquals(prev + 1, m.size());/*!*/
			
			assertEquals(10, m.computeIfAbsent(10, k -> 11));
			Assertions.assertEquals(prev + 1, m.size());/*!*/
			
			// resize
			for (int i = 0; i < 100; i++) { final int j = i;
				assertEquals(i, m.computeIfAbsent(i, k -> j));
			}
		}
	}
	
	/**
	 * 
	 */
	static class ConflictHash implements SwissMap.Hash {
		
		@Override public int hash(Object k) { return 3007; }
	}
}
