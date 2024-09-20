package cn.nextop.gadget.core.util.collection;

import static java.lang.String.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Baoyi Chen
 */
public class SwissMapTest1 {
	
	/**
	 * 
	 */
	@Test
	public void test1() {
		//
		{
			SwissMap<Integer , Integer> m;
			m = new SwissMap<>(8); /* 8 */
			
			for (int i = 0; i < 17; i++) {
				m.put(i, i);
			}
			for (int i = 0; i < 17; i++) {
				assertEquals(i, m.get(i));
			}
		}
		
		//
		{
			SwissMap<Integer , Integer> m;
			m = new SwissMap<>(8); /* 8 */
			
			for (int i = 0; i < 14; i++) {
				m.put(i, i);
			}
			
			for (int i = 0; i <  8; i++) {
				m.remove(i);
			}
			
			m.put(14, 14);
			assertEquals (14 , m.get(14));
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void test2() {
		//
		SwissMap<Integer, String> m1;
		SwissMap<Integer, String> m2;
		final Map<Object, String> m3;
		final Map<Integer, String> m4;
		
		m1 = new SwissMap<>();
		m2 = new SwissMap<>();
		m3 = new HashMap<>(16);
		m4 = new HashMap<>(16);
		int k1 = 1, k2 = 2, k3 = 3;
		
		assertFalse(m1.equals(null));
		Assertions.assertTrue (m1.equals(m1));
		Assertions.assertTrue (m1.equals(m2));
		Assertions.assertTrue (m1.equals(m3));
		Assertions.assertTrue (m1.equals(m4));
		
		//
		m1.put(k1, "");
		m3.put(k1, ""); m4.put(k1, "");
		Assertions.assertTrue (m1.equals(m1));
		Assertions.assertFalse(m1.equals(m2));
		Assertions.assertTrue (m1.equals(m3));
		Assertions.assertTrue (m1.equals(m4));
		
		//
		m2.put(k1, "");
		m1.put(k2, "x"); m2.put(k2, "x");
		m3.put("2", "x"); m4.put(k2, "x");
		Assertions.assertTrue (m1.equals(m1));
		Assertions.assertTrue (m1.equals(m2));
		Assertions.assertFalse(m1.equals(m3));
		Assertions.assertTrue (m1.equals(m4));
		
		//
		m1.put(k3, null);
		m3.remove("2"); m3.put(k2, "x");
		Assertions.assertTrue (m1.equals(m1));
		Assertions.assertFalse(m1.equals(m2));
		Assertions.assertFalse(m1.equals(m3));
		Assertions.assertFalse(m1.equals(m4));
		
		//
		m2.put(k3, null);
		m3.put(k3, null); m4.put(k3, null);
		Assertions.assertTrue (m1.equals(m1));
		Assertions.assertTrue (m1.equals(m2));
		Assertions.assertTrue (m1.equals(m3));
		Assertions.assertTrue (m1.equals(m4));
		
		//
		m2.put(k2, "y");
		m3.put(k2, "y"); m4.put(k2, "y");
		Assertions.assertTrue (m1.equals(m1));
		Assertions.assertFalse(m1.equals(m2));
		Assertions.assertFalse(m1.equals(m3));
		Assertions.assertFalse(m1.equals(m4));
		
		//
		m1.put(k2, "y");
		Assertions.assertTrue (m1.equals(m1));
		Assertions.assertTrue (m1.equals(m2));
		Assertions.assertTrue (m1.equals(m3));
		Assertions.assertTrue (m1.equals(m4));
	}
	
	/**
	 * 
	 */
	@Test
	public void test3() {
		//
		int k0 = 0;
		List<String> out;
		SwissMap<Integer, String> m1;
		out = new ArrayList<String>();
		m1 = new SwissMap<Integer, String>(16);
		
		assertEquals(m1.size() , 0);
		assertEquals(m1.isEmpty(), true);
		assertEquals(m1.values().size(), 0);
		assertEquals(m1.keySet().size(), 0);
		assertEquals(m1.entrySet().size(), 0);
		assertEquals(m1.containsKey(k0), false);
		assertEquals(m1.containsValue("0"), false);
		m1.forEach((k, v) -> Assertions.fail()); //!
		
		//
		m1 = new SwissMap<>(m1);
		int k1 = 0; m1.put(k1, "0");
		assertEquals(m1.size() , 1);
		assertEquals(m1.get(k1), "0");
		assertEquals(m1.isEmpty(), false);
		assertEquals(m1.values().size(), 1);
		assertEquals(m1.keySet().size(), 1);
		assertEquals(m1.entrySet().size(), 1);
		assertEquals(m1.containsKey(k1), true);
		assertEquals(m1.containsValue("0"), true);
		assertEquals(m1.keySet().contains(k1), true);
		assertEquals(m1.values().contains("0"), true);
		
		m1.clear(); out.clear();
		assertEquals(m1.size(), 0);
		assertEquals(m1.get(k1), null);
		assertEquals(m1.isEmpty(), true);
		assertEquals(m1.values().size(), 0);
		assertEquals(m1.keySet().size(), 0);
		assertEquals(m1.entrySet().size(), 0);
		assertEquals(m1.containsKey(k1), false);
		assertEquals(m1.containsValue("0"), false);
		assertEquals(m1.keySet().contains(k1), false);
		assertEquals(m1.values().contains("0"), false);
		
		//
		m1 = new SwissMap<>(m1);
		int k2 = 1; m1.put(k2, "1");
		assertEquals(m1.size() , 1);
		assertEquals(m1.get(k2), "1");
		assertEquals(m1.isEmpty(), false);
		assertEquals(m1.values().size(), 1);
		assertEquals(m1.keySet().size(), 1);
		assertEquals(m1.entrySet().size(), 1);
		assertEquals(m1.containsKey(k2), true);
		assertEquals(m1.containsValue("1"), true);
		assertEquals(m1.keySet().contains(k2), true);
		assertEquals(m1.values().contains("1"), true);
		
		out.clear();
		m1 = new SwissMap<>(m1);
		int k3 = 17; m1.put(k3, "17");
		assertEquals(m1.size() , 2 );
		assertEquals(m1.get(k3), "17");
		assertEquals(m1.isEmpty(), false);
		assertEquals(m1.values().size(), 2);
		assertEquals(m1.keySet().size(), 2);
		assertEquals(m1.entrySet().size(), 2);
		assertEquals(m1.containsKey(k2), true);
		assertEquals(m1.containsKey(k3), true);
		assertEquals(m1.containsValue("1"), true);
		assertEquals(m1.containsValue("17"), true);
		assertEquals(m1.keySet().contains(k2), true);
		assertEquals(m1.keySet().contains(k3), true);
		assertEquals(m1.values().contains("1"), true);
		assertEquals(m1.values().contains("17"), true);
		
		out.clear();
		m1.remove(k2);
		assertEquals(m1.size(), 1);
		assertEquals(m1.get(k3), "17");
		assertEquals(m1.isEmpty(), false);
		assertEquals(m1.values().size(), 1);
		assertEquals(m1.keySet().size(), 1);
		assertEquals(m1.entrySet().size(), 1);
		assertEquals(m1.containsKey(k3), true);
		assertEquals(m1.containsValue("17"), true);
		assertEquals(m1.keySet().contains(k3), true);
		assertEquals(m1.values().contains("17"), true);
		
		out.clear();
		m1.remove(k3);
		assertEquals(m1.size(), 0);
		assertEquals(m1.get(k3), null);
		assertEquals(m1.isEmpty(), true);
		assertEquals(m1.values().size(), 0);
		assertEquals(m1.keySet().size(), 0);
		assertEquals(m1.entrySet().size(), 0);
		assertEquals(m1.containsKey(k2), false);
		assertEquals(m1.containsKey(k3), false);
		assertEquals(m1.containsValue("1"), false);
		assertEquals(m1.containsValue("17"), false);
		assertEquals(m1.keySet().contains(k2), false);
		assertEquals(m1.keySet().contains(k3), false);
		assertEquals(m1.values().contains("1"), false);
		assertEquals(m1.values().contains("17"), false);
		
		//
		int k4 = 33;
		m1.put(k4, "33");
		Assertions.assertEquals(m1.size(), 1);
		Assertions.assertEquals(m1.get(k4), "33");
		m1.keySet().clear(); assertEquals(m1.size(), 0);
		
		m1.put(k4, "33");
		Assertions.assertEquals(m1.size(), 1);
		Assertions.assertEquals(m1.get(k4), "33");
		m1.values().clear(); assertEquals(m1.size(), 0);
		
		m1.put(k4, "33");
		Assertions.assertEquals(m1.size(), 1);
		Assertions.assertEquals(m1.get(k4), "33");
		m1.entrySet().clear(); assertEquals(m1.size(), 0);
		
		//
		int k5 = 10;
		m1 = new SwissMap<>(m1); m1.put(k5, null);
		Assertions.assertTrue(m1.containsValue(null));
		assertEquals(m1.size(), 1); assertNull(m1.get(k5));
		
		int k6 = 11;
		m1.put(k5, null); m1.put(k6, null);
		Assertions.assertTrue(m1.containsValue(null));
		assertEquals(m1.size(), 2); assertNull(m1.get(k6));
		
		int k7 = 12;
		m1.put(k5, null); m1.put(k7, null);
		assertTrue(m1.containsValue(null));
		assertEquals(m1.size(), 3); assertNull(m1.get(k7));
		
		m1.put(k7, "12");
		assertEquals(m1.size(), 3);
		assertEquals(m1.get(k7), "12");
		for (Entry<Integer, String> me : m1.entrySet()) {
			me.setValue("xx"); /* set value by entry set */
		}
		
		Assertions.assertEquals(m1.get(k5), "xx");
		Assertions.assertEquals(m1.get(k6), "xx");
		assertEquals("xx", m1.putIfAbsent(k5, "12"));
		Assertions.assertFalse(m1.containsValue(null));
		Assertions.assertEquals("xx", m1.replace(k7, "12"));
		
		//
		var m2 = new SwissMap<>(m1);
		m2.remove(k5); m2.put(k6, "11");
		m1.putAll(new HashMap<>()); m1.putAll(m2);
		Assertions.assertEquals(m1.size(), 3);
		Assertions.assertEquals(m1.get(k5), "xx");
		Assertions.assertEquals(m1.get(k6), "11");
		Assertions.assertEquals(m1.get(k7), "12");
		Assertions.assertTrue(m1.containsKey(k5));
		Assertions.assertFalse(m1.containsKey(k1));
		Assertions.assertEquals(null, m1.replace(k1, "01"));
		
		//
		m1.clear();
		int start1 = -128, end1 = 0;
		for(int i = start1; i < end1; i++) {
			assertEquals(m1.get(i), null);
			assertEquals(m1.put(i, valueOf(i)), null);
			assertEquals(m1.get(i), String.valueOf(i));
			assertEquals(m1.put(i, valueOf(i)), valueOf(i));
		}
		assertEquals((m1.values().size()), (end1 - start1));
		assertEquals((m1.keySet().size()), (end1 - start1));
		assertEquals(m1.entrySet().size(), (end1 - start1));
		assertEquals(m1.size(), m1.size(), (end1 - start1));
		
		//
		int start2 = 1, end2 = 127;
		for(int i = start2; i < end2; i++) {
			assertEquals(m1.put(i, valueOf(i)), null);
			assertEquals(m1.remove(i), String.valueOf(i));
			Assertions.assertEquals(m1.remove(i), (null));
		}
		assertEquals((m1.values().size()), (end1 - start1));
		assertEquals((m1.keySet().size()), (end1 - start1));
		assertEquals(m1.entrySet().size(), (end1 - start1));
		assertEquals(m1.size(), m1.size(), (end1 - start1));
	}
	
	/**
	 * 
	 */
	@Test
	public void test4() {
		//
		String s1 = null, s2 = null;
		final int max = Integer.MAX_VALUE;
		Random r = ThreadLocalRandom.current();
		HashMap<Integer, String> m1 = new HashMap<>();
		SwissMap<Integer, String> m2 = new SwissMap<>();
		for(int i = 0; i < 1000000; i++) {
			int key1 = (int)(r.nextInt() % max);
			s1 = m1.put(key1, String.valueOf(key1));
			s2 = m2.put(key1, String.valueOf(key1));
			Assertions.assertEquals(m1.size(), m2.size());
			Assertions.assertEquals(s1, s2, s1 + ":" + s2);
			if (i % 2 == 0) {
				int key2 = (int)(r.nextInt() % max);
				s1 = m1.remove(key2); s2 = m2.remove(key2);
				Assertions.assertEquals(m1.size(), m2.size());
				Assertions.assertEquals(s1, s2, s1 + ":" + s2);
			}
		}
		
		m2.forEach((k, v) -> assertEquals(m1.get(k), v));
		for (Entry<Integer, String> e : m1.entrySet()) {
			final int k = e.getKey();
			s1 = e.getValue(); s2 = m2.get(k);
			assertTrue(m2.keySet().contains(k));
			assertTrue(m2.containsKey(k), String.valueOf(k));
			Assertions.assertEquals(s1, s2, (s1) + ":" + (s2));
		}
		
		for(Integer key : new HashSet<>(m2.keySet())) {
			s1 = m1.remove(key); s2 = m2.remove(key);
			Assertions.assertEquals(m1.size(), m2.size());
			Assertions.assertEquals(s1, s2, (s1) + ":" + (s2));
		}
		
		//
		for(int i = 0; i < 1000000; i++) {
			int key1 = (int) (r.nextInt() % max);
			m1.put(key1, "x"); m2.put(key1, "x");
			s1 = m1.replace(key1, String.valueOf(key1));
			s2 = m2.replace(key1, String.valueOf(key1));
			Assertions.assertEquals( m1.size(), m2.size() );
			Assertions.assertEquals (s1, s2, s1 + ":" + s2);
			if (i % 2 == 0) {
				int key2 = (int)(r.nextInt() % max);
				s1 = m1.remove(key2); s2 = m2.remove(key2);
				Assertions.assertEquals(m1.size(), m2.size());
				Assertions.assertEquals(s1, s2, s1 + ":" + s2);
			}
		}
		
		m2.forEach((k, v) -> assertEquals(m1.get(k), v));
		for (Entry<Integer, String> e : m1.entrySet()) {
			final int k = e.getKey();
			s1 = e.getValue(); s2 = m2.get(k);
			Assertions.assertEquals(s1, s2, s1 + ":" + s2);
			assertTrue( m2.containsKey(k), String.valueOf(k) );
		}
		
		for(Integer key : new HashSet<>(m2.keySet())) {
			s1 = m1.remove(key); s2 = m2.remove(key);
			Assertions.assertEquals(m1.size(), m2.size());
			Assertions.assertEquals(s1, s2, (s1) + ":" + (s2));
		}
		
		//
		for(int i = 0; i < 1000000; i++) {
			int key1 = (int)(r.nextInt() % max);
			s1 = m1.putIfAbsent(key1, String.valueOf(key1));
			s2 = m2.putIfAbsent(key1, String.valueOf(key1));
			Assertions.assertEquals( m1.size(), m2.size() );
			Assertions.assertEquals (s1, s2, s1 + ":" + s2);
			if (i % 2 == 0) {
				int key2 = (int)(r.nextInt() % max);
				s1 = m1.remove(key2); s2 = m2.remove(key2);
				Assertions.assertEquals(m1.size(), m2.size());
				Assertions.assertEquals(s1, s2, s1 + ":" + s2);
			}
		}
		
		m2.forEach((k, v) -> assertEquals(m1.get(k), v));
		for (Entry<Integer, String> e : m1.entrySet()) {
			final int k = e.getKey();
			s1 = e.getValue(); s2 = m2.get(k);
			Assertions.assertEquals(s1, s2, s1 + ":" + s2);
			assertTrue( m2.containsKey(k), String.valueOf(k) );
		}
		
		for(Integer key : new HashSet<>(m2.keySet())) {
			s1 = m1.remove(key); s2 = m2.remove(key);
			Assertions.assertEquals(m1.size(), m2.size());
			Assertions.assertEquals(s1, s2, (s1) + ":" + (s2));
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void test5() {
		//
		SwissMap<Integer, Object> m1;
		m1 = new SwissMap<>(); m1.setExpasion(1f);
		m1.setHash(k -> ((Integer) k).intValue());
		Assertions.assertEquals("{}", m1.toString());
		
		m1.put(1, "1");
		var x1 = new Integer[] {};
		var x2 = new Integer[] {0};
		var a11 = m1.keySet().toArray();
		var a12 = m1.keySet().toArray(x1);
		var a13 = m1.keySet().toArray(x2);
		Assertions.assertEquals(1, a11[0]);
		Assertions.assertEquals(1, a12[0]);
		Assertions.assertEquals(1, a13[0]);
		Assertions.assertEquals(1, a11.length);
		Assertions.assertEquals(1, a12.length);
		Assertions.assertEquals(1, a13.length);
		assertEquals("1", m1.getOrDefault(1, null));
		assertEquals("2", m1.getOrDefault(2,  "2"));
		assertEquals(null, m1.getOrDefault(2, null));
		Assertions.assertEquals("{1=1}", m1.toString());
		
		m1.replaceAll((k, v) -> String.valueOf(k + 1));
		assertEquals("2", m1.getOrDefault(1, null));
		assertEquals("2", m1.getOrDefault(2,  "2"));
		assertEquals(null, m1.getOrDefault(2, null));
		Assertions.assertEquals("{1=2}", m1.toString());
		
		//
		SwissMap<Integer, Object> m2;
		m2 = new SwissMap<>(1, 1f); m2.putAll(m1);
		
		var y1 = new String[] {};
		var y2 = new String[] {""};
		var b11 = m2.values().toArray();
		var b12 = m2.values().toArray(y1);
		var b13 = m2.values().toArray(y2);
		Assertions.assertEquals("2", b11[0]);
		Assertions.assertEquals("2", b12[0]);
		Assertions.assertEquals("2", b13[0]);
		Assertions.assertEquals(1, b11.length);
		Assertions.assertEquals(1, b12.length);
		Assertions.assertEquals(1, b13.length);
		assertEquals("2", m2.getOrDefault(1, null));
		assertEquals("2", m2.getOrDefault(2,  "2"));
		assertEquals(null, m2.getOrDefault(2, null));
		Assertions.assertEquals("{1=2}", m2.toString());
		
		//
		var i1 = m1.keySet().iterator();
		assertFalse(m1.keySet().isEmpty());
		assertFalse(m1.values().isEmpty());
		Assertions.assertTrue(i1.hasNext());
		Assertions.assertEquals(1, i1.next());
		i1.remove(); assertFalse (i1.hasNext());
		assertEquals("1", m1.getOrDefault(2, "1"));
		assertEquals(null, m1.getOrDefault(1, null));
		assertEquals(null, m1.getOrDefault(2, null));
		Assertions.assertEquals("{}", m1.toString());
		Assertions.assertTrue(m1.keySet().isEmpty());
		
		var i3 = m2.entrySet().iterator();
		Assertions.assertTrue(i3.hasNext());
		assertEquals(1, i3.next().getKey());
		assertFalse(m2.values ().isEmpty());
		assertFalse(m2.entrySet().isEmpty());
		i3.remove(); assertFalse (i3.hasNext());
		assertEquals("1", m2.getOrDefault(2, "1"));
		assertEquals(null, m2.getOrDefault(1, null));
		assertEquals(null, m2.getOrDefault(2, null));
		Assertions.assertEquals("{}", m2.toString());
		Assertions.assertTrue(m2.entrySet().isEmpty());
	}
	
	/**
	 * 
	 */
	@Test
	public void test6() {
		//
		var m1 = new SwissMap<Integer, Object>(2);
		Assertions.assertNull(m1.replace(1, "1"));
		Assertions.assertFalse(m1.replace(1, "1", "2"));
		
		//
		m1.put(1, "1");
		Assertions.assertEquals("1", m1.replace(1, "1"));
		Assertions.assertEquals("1", m1.replace(1, "2"));
		Assertions.assertEquals(null, m1.replace(2, "1"));
		Assertions.assertEquals(null, m1.replace(2, "2"));
		Assertions.assertEquals("{1=2}", m1.toString());/*!*/
		
		m1.put(2, "2");
		Assertions.assertEquals(false, m1.replace(2, "1", "2"));
		Assertions.assertEquals(true , m1.replace(2, "2", "3"));
		assertEquals("2", m1.get(1)); assertEquals("3", m1.get(2));
		
		m1.put(3, "3");
		Assertions.assertEquals("3", m1.replace(3, "x"));
		Assertions.assertEquals("x", m1.replace(3, "x"));
		Assertions.assertEquals(false, m1.replace(3, "3", "x"));
		Assertions.assertEquals(true , m1.replace(3, "x", "y"));
		assertEquals("3", m1.get(2)); assertEquals("y", m1.get(3));
		
		//
		var s1 = m1.entrySet();
		Assertions.assertFalse(s1.contains(null));
		Assertions.assertFalse(s1.contains(new Object()));
		Assertions.assertTrue(s1.contains(new SimpleEntry<>(1, "2")));
		Assertions.assertTrue(s1.contains(new SimpleEntry<>(2, "3")));
		Assertions.assertTrue(s1.contains(new SimpleEntry<>(3, "y")));
		Assertions.assertFalse(s1.contains(new SimpleEntry<>(3, "x")));
		
		Assertions.assertFalse(s1.remove(null));
		Assertions.assertFalse(s1.remove(new Object()));
		Assertions.assertTrue(s1.remove(new SimpleEntry<>(1, "2")));
		Assertions.assertTrue(s1.remove(new SimpleEntry<>(2, "3")));
		Assertions.assertTrue(s1.remove(new SimpleEntry<>(3, "y")));
		Assertions.assertFalse(s1.remove(new SimpleEntry<>(3, "x")));
		
		Assertions.assertTrue(s1.isEmpty());
		Assertions.assertFalse(s1.contains(null));
		Assertions.assertFalse(s1.contains(new Object()));
		Assertions.assertFalse(s1.contains(new SimpleEntry<>(1, "2")));
		Assertions.assertFalse(s1.contains(new SimpleEntry<>(2, "3")));
		Assertions.assertFalse(s1.contains(new SimpleEntry<>(3, "y")));
		Assertions.assertFalse(s1.contains(new SimpleEntry<>(3, "x")));
	}
	
	/**
	 * 
	 */
	@Test
	public void test7() {
		//
		var m1 = new SwissMap<Integer, Object>(2);
		assertEquals(null, m1.computeIfPresent(1, (k, v) -> "1"));
		
		m1.put(1, "1");
		assertEquals("1" , m1.computeIfPresent(1, (k, v) -> "1"));
		Assertions.assertEquals("{1=1}", m1.toString()); /***!***/
		
		assertEquals(null, m1.computeIfPresent(1, (k, v) -> null));
		assertEquals(null, m1.get(1)); assertEquals("{}", m1.toString());
		
		assertEquals("x", m1.computeIfAbsent(1, (k) -> "x"));
		assertEquals("x", m1.get(1)); assertEquals("{1=x}", m1.toString());
		
		//
		assertEquals("x", m1.computeIfAbsent(1, (k) -> "y"));
		assertEquals("x", m1.get(1)); assertEquals("{1=x}", m1.toString());
		
		assertEquals(null, m1.computeIfAbsent(3, (k) -> null));
		assertEquals("x", m1.get(1)); assertEquals(null, m1.get(3));/* ! */
		
		assertEquals("y", m1.computeIfAbsent(3, (k) -> ("y")));
		assertEquals("x", m1.get(1)); assertEquals("y" , m1.get(3));/* ! */
		
		assertEquals("y", m1.computeIfAbsent(3, (k) -> ("z")));
		assertEquals("x", m1.get(1)); assertEquals("y" , m1.get(3));/* ! */
		
		assertEquals("z", m1.computeIfAbsent(5, (k) -> ("z")));
		assertEquals("y", m1.get(3)); assertEquals("z" , m1.get(5));/* ! */
		
		assertEquals("7", m1.computeIfAbsent(7, (k) -> ("7")));
		assertEquals("z", m1.get(5)); assertEquals("7" , m1.get(7));/* ! */
		
		//
		m1.remove(7);
		assertEquals(null, m1.computeIfPresent(7, (k, v) -> "7"));
		Assertions.assertEquals("7", m1.computeIfAbsent(7 , (k) -> ("7")));
		assertEquals("z", m1.get(5)); assertEquals("7" , m1.get(7));/* ! */
		
		assertEquals("0" , m1.computeIfPresent(7, (k, v) -> "0"));
		assertEquals("z", m1.get(5)); assertEquals("0" , m1.get(7));/* ! */
		
		assertEquals(null, m1.computeIfPresent(7, (k, v) -> null));
		assertEquals("z", m1.get(5)); assertEquals(null, m1.get(7));/* ! */
		
		//
		m1 = new SwissMap<>(2);
		m1.setHash(new SwissMapTest2.ConflictHash());
		for (int i = 0; i < 20; i++) m1.computeIfAbsent(i, k -> 1);
		for (int i = 0; i < 20; i++) assertTrue(m1.containsKey(i));
		m1.computeIfPresent(2, (k, v) -> null); assertFalse(m1.containsKey(2));
	}
	
	/**
	 * 
	 */
	@Test
	public void test8() {
		//
		var m1 = new SwissMap<Integer, Object>(2);
		assertEquals(null, m1.compute(1, (k, v) -> null));
		assertEquals("1", m1.merge(1, "1", (ov, nv) -> null));
		Assertions.assertEquals("{1=1}", m1.toString()); /***!***/
		
		assertEquals("2", m1.compute(1, (k, v) -> "2"));
		Assertions.assertEquals("{1=2}", m1.toString()); /***!***/
		
		assertEquals("y", m1.merge(1, "x", (ov, nv) -> "y"));
		Assertions.assertEquals("{1=y}", m1.toString()); /***!***/
		
		//
		assertEquals(null, m1.compute(1, (k, v) -> null));
		assertTrue (m1.isEmpty()); assertEquals(null , m1.get(1));
		
		assertEquals("1", m1.compute(1, (k, v) -> "1"));
		assertFalse (m1.isEmpty()); assertEquals("1" , m1.get(1));
		
		assertEquals("2", m1.compute(2, (k, v) -> "2"));
		assertFalse (m1.isEmpty()); assertEquals("2" , m1.get(2));
		
		assertEquals("3", m1.compute(3, (k, v) -> "3"));
		assertFalse (m1.isEmpty()); assertEquals("3" , m1.get(3));
		
		assertEquals("4", m1.compute(4, (k, v) -> "4"));
		assertFalse (m1.isEmpty()); assertEquals("4" , m1.get(4));
		
		assertEquals("5", m1.compute(5, (k, v) -> "5"));
		assertFalse (m1.isEmpty()); assertEquals("5" , m1.get(5));
		
		//
		assertEquals(null, m1.merge(1, "1", (ov, nv) -> null));
		assertFalse (m1.isEmpty()); assertEquals(null, m1.get(1));
		
		assertEquals(null, m1.merge(2, "2", (ov, nv) -> null));
		assertFalse (m1.isEmpty()); assertEquals(null, m1.get(2));
		
		assertEquals(null, m1.merge(3, "3", (ov, nv) -> null));
		assertFalse (m1.isEmpty()); assertEquals(null, m1.get(3));
		
		assertEquals(null, m1.merge(4, "4", (ov, nv) -> null));
		assertFalse (m1.isEmpty()); assertEquals(null, m1.get(4));
		
		assertEquals(null, m1.merge(5, "5", (ov, nv) -> null));
		assertTrue( m1.isEmpty() ); assertEquals(null, m1.get(5));
		
		//
		assertEquals("1", m1.compute(1, (k, v) -> "1"));
		assertFalse (m1.isEmpty()); assertEquals("1" , m1.get(1));
		
		assertEquals("2", m1.merge(1, "1", (ov, nv) -> "2"));
		assertFalse (m1.isEmpty()); assertEquals("2" , m1.get(1));
		
		assertEquals("x", m1.compute(1, (k, v) -> "x"));
		assertFalse (m1.isEmpty()); assertEquals("x" , m1.get(1));
		
		assertEquals("y", m1.merge(1, "x", (ov, nv) -> "y"));
		assertFalse (m1.isEmpty()); assertEquals("y" , m1.get(1));
		
		//
		m1 = new SwissMap<>(2);
		m1.setHash(new SwissMapTest2.ConflictHash());
		for (int i = 0; i < 20; i++) m1.compute(i, (k, v) -> 1);
		for (int i = 0; i < 20; i++) assertTrue(m1.containsKey(i));
		m1.compute(2, (k, v) -> null); assertFalse(m1.containsKey(2));
		
		//
		m1 = new SwissMap<>(2);
		m1.setHash(new SwissMapTest2.ConflictHash());
		for (int i = 0; i < 20; i++) m1.merge(i, 1, (v1, v2) -> 1);
		for (int i = 0; i < 20; i++) assertTrue(m1.containsKey(i));
		m1.merge(2, 2, (v1, v2) -> null); assertFalse(m1.containsKey(2));
	}
	
	/**
	 * 
	 */
	@Test
	public void test9() {
		//
		final boolean verbose = false;
		for(int i = 64, j = 1; j <= 4096; j *= 2) {
			String progress = "=== " + i + "," + j + " ===";
			if(verbose) System.out.println(progress); test(i, j);
		}
		
		//
		for(int i = 1, j = 4096; i <= 128; i *= 2) {
			String progress = "=== " + i + "," + j + " ===";
			if(verbose) System.out.println(progress); test(i, j);
		}
	}
	
	protected void test(int n1, int n2) {
		//
		Random random = ThreadLocalRandom.current();
		SwissMap<Integer, String> a = new SwissMap<>(n1);
		final HashMap<Integer, String> b = new HashMap<>(n1);
		Mapper<Collection<?>, Set<?>> set = v -> new HashSet<>(v);
		
		//
		for(int i = 0; i < 100000; i++) {
			final var k = random.nextInt(n2);
			final String v = String.valueOf(k);
			switch(random.nextInt(20)) {/* ! */
			case 0:
				final int w0 = random.nextInt(100);
				assertEquals(a, b); assertEquals(b, a);
				if(w0 == 0) { a.clear(); b.clear(); } break;
			case 1:
				var w11 = a.getOrDefault(k, null);
				var w12 = b.getOrDefault(k, null);
				Assertions.assertEquals(w11, w12);
				Assertions.assertEquals(a.get(k), b.get(k)); break;
			case 2:
				assertEquals(a.remove(k), b.remove(k));
				Assertions.assertEquals(a.size(), b.size()); break;
			case 3:
				assertEquals(a.remove(k, v), b.remove(k, v));
				Assertions.assertEquals(a.size(), b.size()); break;
			case 4:
				assertEquals(a.replace(k, v), b.replace(k, v));
				Assertions.assertEquals(a.size(), b.size()); break;
			case 5:
				final boolean w051 = a.replace(k, v, null);
				final boolean w052 = b.replace(k, v, null);
				Assertions.assertEquals(w051, w052); /* ! */ break;
			case 6:
				final String w061 = a.put( k , v ); /* ! */
				final String w062 = b.put( k , v ); /* ! */
				Assertions.assertEquals(w061, w062); /* ! */ break;
			case 7:
				final String w071 = a.putIfAbsent( k , v );
				final String w072 = b.putIfAbsent( k , v );
				Assertions.assertEquals(w071, w072); /* ! */ break;
			case 8:
				final boolean w081 = a.keySet().remove(k);
				final boolean w082 = b.keySet().remove(k);
				Assertions.assertEquals(w081, w082); /* ! */ break;
			case 9:
				boolean w091, w092; do {
					w091 = a.values().remove(v);
					w092 = b.values().remove(v);
					Assertions.assertEquals(w091, w092);
				} while( w091 && w092 ); /** remove all ***/ break;
			case 10:
				var w100 = new SimpleEntry<>(k, v);
				final boolean w101 = a.entrySet().remove(w100);
				final boolean w102 = b.entrySet().remove(w100);
				Assertions.assertEquals(w101, w102); /* ! */ break;
			case 11:
				final boolean w110 = a.keySet().contains(k);
				assertEquals(w110 , b.keySet().contains(k)); break;
			case 12:
				final boolean w120 = a.values().contains(v);
				assertEquals(w120 , b.values().contains(v)); break;
			case 13:
				var w130 = new SimpleEntry<>(k, v);
				final boolean w131 = a.entrySet().contains(w130);
				final boolean w132 = b.entrySet().contains(w130);
				Assertions.assertEquals(w131, w132); /* ! */ break;
			case 14:
				var w140 = a.compute(k, (x, y) -> v);
				var w141 = b.compute(k, (x, y) -> v);
				Assertions.assertEquals (w140 , w141);
				w140 = a.compute(k + 1, (x, y) -> null);
				w141 = b.compute(k + 1, (x, y) -> null);
				Assertions.assertEquals(w140, w141); /* ! */
				w140 = a.compute(k + 2, (x, y) -> valueOf (k + 2));
				w141 = b.compute(k + 2, (x, y) -> valueOf (k + 2));
				Assertions.assertEquals(w140, w141); /* ! */ break;
			case 15:
				var w150 = a.merge(k, v, (x, y) -> v);
				var w151 = b.merge(k, v, (x, y) -> v);
				Assertions.assertEquals (w150 , w151);
				w150 = a.merge(k + 1, v, (x, y) -> null);
				w151 = b.merge(k + 1, v, (x, y) -> null);
				Assertions.assertEquals(w150, w151); /* ! */
				w150 = a.merge(k + 2, v, (x, y) -> valueOf(k + 2));
				w151 = b.merge(k + 2, v, (x, y) -> valueOf(k + 2));
				Assertions.assertEquals(w150, w151); /* ! */ break;
			case 16:
				var w160 = a.computeIfAbsent(k, x -> v);
				var w161 = b.computeIfAbsent(k, x -> v);
				Assertions.assertEquals(w160, w161);/* ! */
				w160 = a.computeIfAbsent(k + 1, x -> null);
				w161 = b.computeIfAbsent(k + 1, x -> null);
				Assertions.assertEquals(w160, w161); /* ! */ break;
			case 17:
				var w170 = a.computeIfPresent(k, (x, y) -> v);
				var w171 = b.computeIfPresent(k, (x, y) -> v);
				Assertions.assertEquals (w170 , w171); /* ! */
				w170 = a.computeIfPresent(k + 1, (x, y) -> null);
				w171 = b.computeIfPresent(k + 1, (x, y) -> null);
				Assertions.assertEquals(w170, w171); /* ! */ break;
			case 18:
				final Set<?> w18 = set.map(a.values());
				Assertions.assertEquals(w18, set.map(b.values()));
				var w180 = new HashSet<>(a.values());
				var w181 = new HashSet<>(b.values());
				Assertions.assertEquals(w180 , w181); /* ! */ break;
			case 19:
				Assertions.assertEquals(a.keySet(), b.keySet());
				var w190 = Set.of(a.keySet().toArray(new Integer[0]));
				var w191 = Set.of(b.keySet().toArray(new Integer[0]));
				Assertions.assertEquals (w190 , w191); /* ! */ break;
			}
		}
	}
	
	public interface Mapper<S, T> {
		
		T map(S s);
	}
}
