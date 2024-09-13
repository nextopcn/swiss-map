package cn.nextop.gadget.core.util.collection;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.reflect.Array.newInstance;
import static java.util.Arrays.fill;
import static jdk.incubator.vector.ByteVector.SPECIES_128;
import static jdk.incubator.vector.ByteVector.SPECIES_256;
import static jdk.incubator.vector.ByteVector.SPECIES_512;
import static jdk.incubator.vector.ByteVector.SPECIES_64;

import java.lang.reflect.Field;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import jdk.incubator.vector.ByteVector;
import sun.misc.Unsafe;

/**
 * @author Baoyi Chen
 */
@SuppressWarnings("unchecked")
public class SwissMap<K, V> extends AbstractMap<K, V> {
	//
	private static final byte EMPTY = -128;
	
	private static final byte TOMB_STONE = -2;
	
	private static final int H1_MASK = 0xFFFFFF80;
	
	private static final int H2_MASK = 0x0000007F;
	
	public interface Hash { int hash(Object key); }
	
	//
	private Group group;
	private float expasion;
	private Platform<?> platform;
	private int dead, limit, resident;
	
	/**
	 * 
	 */
	public SwissMap() {
		this(16, 0.5f, PLATFORM);
	}
	
	public SwissMap(int n) {
		this(n , 0.5f, PLATFORM);
	}
	
	public SwissMap(int n , float x) {
		this( n , x , PLATFORM );
	}
	
	public SwissMap( /* @see Platform */
		int n, float x, Platform<?> p) {
		final int a = p.maxAvgGroupLoad();
		this.expasion = x; this.platform = p;
		int s = p.shift(), m = p.maxGroups();
		int z = max(min(m, (n + a - 1) / a), 1);
		limit = z * a; group = new Group(z , s);
	}
	
	public SwissMap(final SwissMap<K, V> m) {
		this (m.size(), m.expasion, m.platform);
		m.forEach(( k , v ) -> this.add(k , v));
	}
	
	/**
	 * 
	 */
	@Override
	public Set<K> keySet () {
		return new XKeySet();
	}
	
	@Override public int size() {
		return (resident - dead);
	}
	
	@Override public void clear () {
		group.clear(); /* @see clear() */
		this.dead = 0; this.resident = 0;
	}
	
	public void setHash(Hash hash) {
		this.hash = hash; /* @see Hash */
	}
	
	@Override
	public Collection<V> values () {
		var r = new XValues (); return r;
	}
	
	@Override
	public Set<Entry<K , V>> entrySet() {
		var r = new XEntrySet(); return r;
	}
	
	public void setExpasion(final float v) {
		this.expasion = v;/* @see resize() */
	}
	
	@Override
	public boolean containsValue (Object v) {
		return this.group.containsValues (v);
	}
	
	/**
	 * 
	 */
	private Hash hash = (key) -> {
		if (key == null) return 0;
		final int h = key.hashCode();
		return (h) ^ (h >>>  7) ^ (h >>> 25);
	};
	
	private <T> T cast(Object o) {
		return (T) o; /* suppress warnings */
	}
	
	private int zeros(final long n) {
		return Long.numberOfTrailingZeros(n);
	}
	
	private Entry<K, V> entry(K k, V v) {
		return new SimpleEntry<K , V>(k , v);
	}
	
	private boolean equals(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}
	
	private boolean resize() {/* if necessary */
		//
		if(this.resident < this.limit) return false;
		
		//
		var g = this.group ; var p = this.platform ;
		int s = p.shift() , a = p.maxAvgGroupLoad();
		int x, y = g.length(); var z = y * expasion;
		x = min(p.maxGroups(), y + max(1, (int) z));
		if(this.dead >= (this.resident >> 1)) x = y;
		
		//
		this.dead = this.resident = 0; /* re-fill */
		this.limit = x * a; group = new Group(x, s);
		g.forEach((k, v) -> add(k, v)); return true;
	}
	
	private <T> T[] newArray(T[] out, final int n) {
		final int m = (out == null) ? 0 : out.length;
		if (m == n) return out; var c = out.getClass();
		return (T[])newInstance(c.getComponentType(), n);
	}
	
	/**
	 * 
	 */
	@Override
	public V getOrDefault(final Object k, final V v) {
		var n = node(k); return n == null ? v : n.getValue();
	}
	
	@Override
	public void forEach(BiConsumer<? super K, ? super V> x) {
		this.group.forEach(x);
	}
	
	@Override
	public void replaceAll( /* replaces each entry's value */
		BiFunction<? super K , ? super V , ? extends V> x ) {
		this.group.replaceAll(x);
	}
	
	/**
	 * 
	 */
	private V add(final K k, final V v) {
		final var h = this.hash.hash (k);
		final var hi = (h & H1_MASK) >>> 7;
		final var lo = (byte)(h & H2_MASK);
		V r = this.add(k, v, hi, lo); return r;
	}
	
	private V add (K k, V v, int hi, byte lo) {
		final var g = group; var i = g.mod(hi);
		final var p = platform; var s = p.shift();
		var m = g.meta; var d = m.data; while (true) {
			//
			var x = (i << s); var simd = p.simd(x, d);
			var t = p.empty(cast(simd)); if(t != 0L) {
				final var n = zeros(t); var y = p.next(n);
				final var j = x + y; m.add(j, lo); /* + */
				g.add(j, k, v); this.resident++; return v;
			}
			
			//
			final int n = g.length(); if(++i >= n) { i = 0; }
		}
	}
	
	/**
	 * 
	 */
	@Override
	public V get(final Object key) {
		final var h = this.hash.hash(key);
		final var hi = (h & H1_MASK) >>> 7;
		final var lo = (byte)(h & H2_MASK);
		final var g = group; var i = g.mod(hi);
		final var p = platform; var s = p.shift();
		var m = g.meta; var d = m.data; while (true) {
			//
			var x = i << s; final var simd = p.simd(x, d);
			var t = p.eq(cast(simd), lo); while(t != 0L) {
				var n = zeros(t); final var y = p.next(n);
				var j = x + y ; final var k = g.getKey(j);
				if (!equals(key , k)) { t &= ~(1L << n); }
				else { final V r = g.getValue(j); return r; }
			}
			
			//
			t = p.empty(cast(simd)); if(t != 0L) return null;
			final int n = g.length(); if(++i >= n) { i = 0; }
		}
	}
	
	@Override
	public boolean containsKey(Object key) {
		final var h = this.hash.hash(key);
		final var hi = (h & H1_MASK) >>> 7;
		final var lo = (byte)(h & H2_MASK);
		final var g = group; var i = g.mod(hi);
		final var p = platform; var s = p.shift();
		var m = g.meta; var d = m.data; while (true) {
			//
			var x = i << s; final var simd = p.simd(x, d);
			var t = p.eq(cast(simd), lo); while(t != 0L) {
				var n = zeros(t); final var y = p.next(n);
				var j = x + y ; final var k = g.getKey(j);
				if (!equals(key , k)) { t &= ~(1L << n); }
				else /** @see get & node **/ { return true; }
			}
			
			//
			t = p.empty(cast(simd)); if(t != 0) return false;
			final int n = g.length(); if(++i >= n) { i = 0; }
		}
	}
	
	private Entry<K, V> node(Object key) {
		final var h = this.hash.hash(key);
		final var hi = (h & H1_MASK) >>> 7;
		final var lo = (byte)(h & H2_MASK);
		final var g = group; var i = g.mod(hi);
		final var p = platform; var s = p.shift();
		var m = g.meta; var d = m.data; while (true) {
			//
			var x = i << s; final var simd = p.simd(x, d);
			var t = p.eq(cast(simd), lo); while(t != 0L) {
				var n = zeros(t); final var y = p.next(n);
				var j = x + y ; final var k = g.getKey(j);
				if (!equals(key , k)) { t &= ~(1L << n); }
				else { return this.entry(k, g.getValue(j)); }
			}
			
			//
			t = p.empty(cast(simd)); if(t != 0L) return null;
			final int n = g.length(); if(++i >= n) { i = 0; }
		}
	}
	
	/**
	 * 
	 */
	@Override
	public V put(K key, V value) {
		final var h = this.hash.hash(key);
		final var hi = (h & H1_MASK) >>> 7;
		final var lo = (byte)(h & H2_MASK);
		final var g = group; var i = g.mod(hi);
		final var p = platform; var s = p.shift();
		var m = g.meta; var d = m.data; while (true) {
			//
			var x = i << s; final var simd = p.simd(x, d);
			var t = p.eq(cast(simd), lo); while(t != 0L) {
				var n = zeros(t); final var y = p.next(n);
				var j = x + y ; final var k = g.getKey(j);
				if (!equals(key , k)) { t &= ~(1L << n); }
				else return g.set(j, key, value);/* set */
			}
			
			//
			t = p.empty(cast(simd)); if(t != 0) {/* add */
				
				if (this.resize()) {/* @see this.resize */
					add (key, value, hi, lo); return null;
				}
				
				var n = zeros(t); final var y = p.next(n);
				final var j = x + y; g.add(j, key, value);
				m.add(j, lo); this.resident++; return null;
			}
			final int n = g.length(); if(++i >= n) { i = 0; }
		}
	}
	
	@Override
	public V putIfAbsent(K key, V value) {
		final var h = this.hash.hash(key);
		final var hi = (h & H1_MASK) >>> 7;
		final var lo = (byte)(h & H2_MASK);
		final var g = group; var i = g.mod(hi);
		final var p = platform; var s = p.shift();
		var m = g.meta; var d = m.data; while (true) {
			//
			var x = i << s; final var simd = p.simd(x, d);
			var t = p.eq(cast(simd), lo); while(t != 0L) {
				var n = zeros(t); final var y = p.next(n);
				var j = x + y ; final var k = g.getKey(j);
				if (!equals(key , k)) { t &= ~(1L << n); }
				else { return g.getValue(j); }/* reject */
			}
			
			//
			t = p.empty(cast(simd)); if(t != 0) {/* add */
				
				if (this.resize()) {/* @see this.resize */
					add (key, value, hi, lo); return null;
				}
				
				var n = zeros(t); final var y = p.next(n);
				final var j = x + y; g.add(j, key, value);
				m.add(j, lo); this.resident++; return null;
			}
			final int n = g.length(); if(++i >= n) { i = 0; }
		}
	}
	
	/**
	 * 
	 */
	@Override
	public V replace(K key, V value) {
		final var h = this.hash.hash(key);
		final var hi = (h & H1_MASK) >>> 7;
		final var lo = (byte)(h & H2_MASK);
		final var g = group; var i = g.mod(hi);
		final var p = platform; var s = p.shift();
		var m = g.meta; var d = m.data; while (true) {
			//
			var x = i << s; final var simd = p.simd(x, d);
			var t = p.eq(cast(simd), lo); while(t != 0L) {
				var n = zeros(t); final var y = p.next(n);
				var j = x + y ; final var k = g.getKey(j);
				if (!equals(key , k)) { t &= ~(1L << n); }
				else return g.set(j, key, value);/* set */
			}
			
			//
			t = p.empty(cast(simd)); if(t != 0L) return null;
			final int n = g.length(); if(++i >= n) { i = 0; }
		}
	}
	
	@Override
	public boolean replace(K key, V ov, V nv) {
		final var h = this.hash.hash(key);
		final var hi = (h & H1_MASK) >>> 7;
		final var lo = (byte)(h & H2_MASK);
		final var g = group; var i = g.mod(hi);
		final var p = platform; var s = p.shift();
		var m = g.meta; var d = m.data; while (true) {
			//
			var x = i << s; final var simd = p.simd(x, d);
			var t = p.eq(cast(simd), lo); while(t != 0L) {
				var n = zeros(t); final var y = p.next(n);
				var j = x + y ; final var k = g.getKey(j);
				final var v = g.getValue(j);/*** prev ***/
				if (!equals(key , k)) { t &= ~(1L << n); }
				else if (!equals(ov , v)) t &= ~(1L << n);
				else { g.add(j , key , nv); return true; }
			}
			
			//
			t = p.empty(cast(simd)); if(t != 0) return false;
			final int n = g.length(); if(++i >= n) { i = 0; }
		}
	}
	
	/**
	 * 
	 */
	@Override
	public V remove(Object key) {
		final var h = this.hash.hash(key);
		final var hi = (h & H1_MASK) >>> 7;
		final var lo = (byte)(h & H2_MASK);
		final var g = group; var i = g.mod(hi);
		final var p = platform; var s = p.shift();
		var m = g.meta; var d = m.data; while (true) {
			//
			var x = i << s; final var simd = p.simd(x, d);
			var t = p.eq(cast(simd), lo); while(t != 0L) {
				var n = zeros(t); final var y = p.next(n);
				var j = x + y ; final var k = g.getKey(j);
				if (!equals(key , k)) { t &= ~(1L << n); }
				else {
					if(p.empty(cast(simd)) != 0L) {
						m.add(j, EMPTY) ; this.resident--;
					} else {
						m.add(j, TOMB_STONE); this.dead++;
					}
					V r = g.set (j, null, null); return r;
				}
			}
			
			//
			t = p.empty(cast(simd)); if(t != 0L) return null;
			final int n = g.length(); if(++i >= n) { i = 0; }
		}
	}
	
	@Override
	public boolean remove(Object key, Object value) {
		final var h = this.hash.hash(key);
		final var hi = (h & H1_MASK) >>> 7;
		final var lo = (byte)(h & H2_MASK);
		final var g = group; var i = g.mod(hi);
		final var p = platform; var s = p.shift();
		var m = g.meta; var d = m.data; while (true) {
			//
			var x = i << s; final var simd = p.simd(x, d);
			var t = p.eq(cast(simd), lo); while(t != 0L) {
				var n = zeros(t); final var y = p.next(n);
				var j = x + y ; final var k = g.getKey(j);
				final var v = g.getValue(j);/*** prev ***/
				if (!equals(key , k)) { t &= ~(1L << n); }
				else if(!equals(value,v)) t &= ~(1L << n);
				else {
					if(p.empty(cast(simd)) != 0L) {
						m.add(j, EMPTY) ; this.resident--;
					} else {
						m.add(j, TOMB_STONE); this.dead++;
					}
					g.add( j , null , null ); return true;
				}
			}
			
			//
			t = p.empty(cast(simd)); if(t != 0) return false;
			final int n = g.length(); if(++i >= n) { i = 0; }
		}
	}
	
	/**
	 * 
	 */
	@Override
	public V computeIfPresent(K key,
		BiFunction<? super K , ? super V , ? extends V> v ) {
		final var h = this.hash.hash(key);
		final var hi = (h & H1_MASK) >>> 7;
		final var lo = (byte)(h & H2_MASK);
		final var g = group; var i = g.mod(hi);
		final var p = platform; var s = p.shift();
		var m = g.meta; var d = m.data; while (true) {
			//
			var x = i << s; final var simd = p.simd(x, d);
			var t = p.eq(cast(simd), lo); while(t != 0L) {
				var n = zeros(t); final var y = p.next(n);
				var j = x + y ; final var k = g.getKey(j);
				if (!equals(key , k)) { t &= ~(1L << n); }
				else {
					V pv = g.getValue(j), nv;
					if ((nv = v.apply(key, pv)) != null) {
						g.add (j , key , nv); return (nv);
					} else {
						if(p.empty(cast(simd)) != 0L) {
							m.add(j , EMPTY) ; resident--;
						} else {
							m.add(j , TOMB_STONE); dead++;
						}
						g.add(j, null, null); return null;
					}
				}
			}
			
			//
			t = p.empty(cast(simd)); if(t != 0L) return null;
			final int n = g.length(); if(++i >= n) { i = 0; }
		}
	}
	
	@Override
	public V computeIfAbsent(
		K key , final Function<? super K , ? extends V> v ) {
		final var h = this.hash.hash(key);
		final var hi = (h & H1_MASK) >>> 7;
		final var lo = (byte)(h & H2_MASK);
		final var g = group; var i = g.mod(hi);
		final var p = platform; var s = p.shift();
		var m = g.meta; var d = m.data; while (true) {
			//
			var x = i << s; final var simd = p.simd(x, d);
			var t = p.eq(cast(simd), lo); while(t != 0L) {
				var n = zeros(t); final var y = p.next(n);
				var j = x + y ; final var k = g.getKey(j);
				if (!equals(key , k)) { t &= ~(1L << n); }
				else { var r = g.getValue (j); return r; }
			}
			
			//
			t = p.empty(cast(simd)); if(t != 0) {/* add */
				
				V nv = v.apply(key);
				if (nv == null) return null;
				if (this.resize()) {/* @see this.resize */
					V r = add (key, nv, hi, lo); return r;
				}
				
				final var n = zeros(t); var y = p.next(n);
				final var j = x + y; m.add(j, lo); /* + */
				g.add (j, key, nv); resident++; return nv;
			}
			final int n = g.length(); if(++i >= n) { i = 0; }
		}
	}
	
	@Override
	public V compute(K key,
		BiFunction<? super K , ? super V , ? extends V> v ) {
		final var h = this.hash.hash(key);
		final var hi = (h & H1_MASK) >>> 7;
		final var lo = (byte)(h & H2_MASK);
		final var g = group; var i = g.mod(hi);
		final var p = platform; var s = p.shift();
		var m = g.meta; var d = m.data; while (true) {
			//
			var x = i << s; final var simd = p.simd(x, d);
			var t = p.eq(cast(simd), lo); while(t != 0L) {
				var n = zeros(t); final var y = p.next(n);
				var j = x + y ; final var k = g.getKey(j);
				if (!equals(key , k)) { t &= ~(1L << n); }
				else {
					V pv = g.getValue(j), nv;
					if ((nv = v.apply(key, pv)) != null) {
						g.add (j , key , nv); return (nv);
					} else {
						if(p.empty(cast(simd)) != 0L) {
							m.add(j , EMPTY) ; resident--;
						} else {
							m.add(j , TOMB_STONE); dead++;
						}
						g.add(j, null, null); return null;
					}
				}
			}
			
			//
			t = p.empty(cast(simd)); if(t != 0) {/* add */
				
				V nv = v.apply(key, null);
				if (nv == null) return null;
				if (this.resize()) {/* @see this.resize */
					V r = add (key, nv, hi, lo); return r;
				}
				
				final var n = zeros(t); var y = p.next(n);
				final var j = x + y; m.add(j, lo); /* + */
				g.add (j, key, nv); resident++; return nv;
			}
			final int n = g.length(); if(++i >= n) { i = 0; }
		}
	}
	
	@Override
	public V merge(K key, V value,
		BiFunction<? super V , ? super V , ? extends V> v ) {
		final var h = this.hash.hash(key);
		final var hi = (h & H1_MASK) >>> 7;
		final var lo = (byte)(h & H2_MASK);
		final var g = group; var i = g.mod(hi);
		final var p = platform; var s = p.shift();
		var m = g.meta; var d = m.data; while (true) {
			//
			var x = i << s; final var simd = p.simd(x, d);
			var t = p.eq(cast(simd), lo); while(t != 0L) {
				var n = zeros(t); final var y = p.next(n);
				var j = x + y ; final var k = g.getKey(j);
				if (!equals(key , k)) { t &= ~(1L << n); }
				else {
					V pv = g.getValue(j), nv;
					if(pv == null) nv = value;
					else nv = v.apply(pv, value);
					if(nv != null) {/* replace */
						g.add (j , key , nv); return (nv);
					} else {
						if(p.empty(cast(simd)) != 0L) {
							m.add(j , EMPTY) ; resident--;
						} else {
							m.add(j , TOMB_STONE); dead++;
						}
						g.add(j, null, null); return null;
					}
				}
			}
			
			//
			t = p.empty(cast(simd)); if(t != 0) {/* add */
				
				if (this.resize()) {/* @see this.resize */
					return this.add( key, value, hi, lo );
				}
				
				final var n = zeros(t); var y = p.next(n);
				final var j = x + y; m.add(j, lo); /* + */
				g.add( j , key , value ); this.resident++;
				return value;
			}
			final int n = g.length(); if(++i >= n) { i = 0; }
		}
	}
	
	/**
	 * 
	 */
	private class Meta {
		
		private final byte[] data;
		
		void clear() { fill(this.data, EMPTY); }
		
		void add(int i, byte v) { data[i] = v; }
		
		private Meta(final int n, final int s) {
			fill(data = new byte[n << s], EMPTY);
		}
		
		static boolean exists(byte[] d, int i) {
			final var x = d[i];/* @see clear() */
			return x != EMPTY && x != TOMB_STONE;
		}
	}
	
	/**
	 * 
	 */
	private class Group {
		
		private final int size;
		private final Meta meta;
		private final Object[] keys;
		private final Object[] vals;
		
		public Group(int n, int s) {
			int m = (size = n) << s;
			this.keys = new Object[m];
			this.vals = new Object[m];
			this.meta = new Meta(n, s);
		}
		
		public int length() {
			return this.size;
		}
		
		public void clear() {
			fill(keys, null);
			fill(vals, null);
			this.meta.clear();
		}
		
		public K getKey( int i ) {
			return (K)this.keys[i];
		}
		
		public V getValue(int i) {
			return (V)this.vals[i];
		}
		
		public int mod( int hi ) {
			final int n = this.size;
			return hi < n ? hi : hi % n;
		}
		
		public void add(int i , K k , V v) {
			this.keys[i] = k; this.vals[i] = v;
		}
		
		public V set ( int i , K k , V v ) {
			var w = this.vals; var r = (V)w[i];
			this.keys[i] = k; w[i] = v; return r;
		}
		
		public Object[] toKeyArray() {
			final var r = new Object[size()];
			var d = meta.data; var n = d.length;
			var k = keys; for(int i = 0, j = 0; i < n; i++) {
				if (Meta.exists(d, i)) /* ! */ r[j++] = k[i];
			}
			return r;
		}
		
		public Object[] toValueArray() {
			final var r = new Object[size()];
			var d = meta.data; var n = d.length;
			var v = vals; for(int i = 0, j = 0; i < n; i++) {
				if (Meta.exists(d, i)) /* ! */ r[j++] = v[i];
			}
			return r;
		}
		
		public <T> T[] toKeyArray(T[] o) {
			final var r = newArray(o, size());
			var d = meta.data; var n = d.length;
			var k = keys; for(int i = 0, j = 0; i < n; i++) {
				if (Meta.exists(d, i)) { r[j++] = (T) k[i]; }
			}
			return r;
		}
		
		public <T> T[] toValueArray(T[] o) {
			final var r = newArray(o, size());
			var d = meta.data; var n = d.length;
			var v = vals; for(int i = 0, j = 0; i < n; i++) {
				if (Meta.exists(d, i)) { r[j++] = (T) v[i]; }
			}
			return r;
		}
		
		public boolean containsValues(Object o) {
			final var self = SwissMap.this; final var v = this.vals;
			final var d = meta.data; for (int i = 0 , n = d.length; i < n; i++) {
				if(Meta.exists(d, i)) { if (self.equals(o , v[i])) return true; }
			}
			return false;
		}
		
		public void forEach(BiConsumer< ? super K , ? super V > x) {
			final Object[] k = this.keys; final Object[] v = this.vals;
			final var d = meta.data; for (int i = 0 , n = d.length; i < n; i++) {
				if(Meta.exists(d, i)) /* accept */ x.accept((K) k[i] , (V) v[i]);
			}
		}
		
		public void replaceAll(BiFunction<? super K, ? super V, ? extends V> x) {
			final Object[] k = this.keys; final Object[] v = this.vals;
			final var d = meta.data; for (int i = 0 , n = d.length; i < n; i++) {
				if(Meta.exists(d, i)) /* apply */ v[i] = x.apply((K)k[i], (V)v[i]);
			}
		}
	}
	
	/**
	 * 
	 */
	abstract class AbstractIterator<T> implements Iterator<T> {
		
		protected int index;
		protected static Object EMPTY = new Object();
		
		@Override public boolean hasNext() {
			var r = false; final var d = group.meta.data;
			int i = index, n = d.length; for ( ; i < n; i++) {
				if (Meta.exists( d , i )) { r = true; break; }
			}
			this.index = i; /** reset the index! **/ return r;
		}
	}
	
	private class XKeyIterator extends AbstractIterator< K > {
		
		protected Object key = EMPTY;
		
		@Override public void remove() {
			final boolean illegal = this.key == EMPTY;
			if(illegal) { throw new IllegalStateException(); }
			SwissMap.this.remove (this.key); this.key = EMPTY;
		}
		
		@Override public K next() {
			if(!hasNext()) throw new NoSuchElementException();
			var g = group; var i = index; this.index = i + 1;
			var key = g.getKey(i); return (K)(this.key = key);
		}
	}
	
	private class XValueIterator extends AbstractIterator<V> {
		
		private Object key = EMPTY;
		private Object val = EMPTY;
		
		@Override public void remove() {
			boolean illegal = key == EMPTY && val == EMPTY;
			if(illegal) { throw new IllegalStateException(); }
			SwissMap.this.remove(key, val); key = val = EMPTY;
		}
		
		@Override public V next() {
			if(!hasNext()) throw new NoSuchElementException();
			var g = group; var i = index; this.index = i + 1;
			this.key = g.getKey(i) ; this.val = g.getValue(i);
			return (V)this.val;
		}
	}
	
	class XEntryIterator extends AbstractIterator<Entry<K, V>> {
		
		private Entry<K, V> next;
		
		@Override public Entry<K, V> next() {
			if(!hasNext()) throw new NoSuchElementException();
			var g = group; final var i = index; index = i + 1;
			var key = g.getKey(i) ; var value = g.getValue(i);
			var r = next = new XEntry(key, value, i); return r;
		}
		
		@Override public void remove() { var next = this.next;
			if(next == null) throw new IllegalStateException();
			var key = next.getKey(); var value = next.getValue();
			SwissMap.this.remove(key , value); this.next = (null);
		}
	}
	
	@SuppressWarnings("serial")
	private class XEntry extends SimpleEntry< K, V > {
		
		private final int index;
		
		XEntry(K k , V v , int i) { super(k, v); this.index = i; }
		
		@Override public V setValue(final V v) {
			V r = super.setValue(v); var g = group; var i = index;
			if (Meta.exists(g.meta.data , i)) { g.vals[i] = (v); }
			return r;
		}
	}
	
	/**
	 * 
	 */
	private class XKeySet extends AbstractSet<K> {
		
		@Override
		public void clear() { SwissMap.this.clear(); }
		
		@Override
		public int size() { return SwissMap.this.size(); }
		
		@Override
		public Object[] toArray() { return group.toKeyArray(); }
		
		@Override
		public boolean isEmpty() { return SwissMap.this.isEmpty(); }
		
		@Override
		public Iterator<K> iterator() { return new XKeyIterator(); }
		
		@Override
		public <T> T[] toArray(T[] o) { return group.toKeyArray(o); }
		
		@Override
		public boolean remove(Object k) { return SwissMap.this.remove(k) != null; }
		
		@Override
		public boolean contains (Object k) { return SwissMap.this.containsKey(k); }
	}
	
	private class XValues extends AbstractCollection<V> {
		
		@Override
		public void clear() { SwissMap.this.clear(); }
		
		@Override
		public int size() { return SwissMap.this.size(); }
		
		@Override
		public Object[] toArray() { return group.toValueArray(); }
		
		@Override
		public boolean isEmpty() { return SwissMap.this.isEmpty(); }
		
		@Override
		public Iterator<V> iterator() { return new XValueIterator(); }
		
		@Override
		public <T> T[] toArray(T[] o) { return group.toValueArray(o); }
		
		@Override
		public boolean contains(Object v) { return SwissMap.this.containsValue(v); }
	}
	
	private class XEntrySet extends AbstractSet<Entry<K, V>> {
		
		@Override
		public void clear() { SwissMap.this.clear(); }
		
		@Override
		public int size() { return SwissMap.this.size(); }
		
		@Override
		public boolean isEmpty() { return SwissMap.this.isEmpty(); }
		
		@Override
		public Iterator<Entry<K, V>> iterator() { return new XEntryIterator(); }
		
		@Override public boolean remove(Object o) {
			if (!(o instanceof Map.Entry)) { return false; } var e = (Entry<K, V>)o;
			final var r = SwissMap.this.remove(e.getKey() , e.getValue()); return r;
		}
		
		@Override public boolean contains(Object o) {
			if (!(o instanceof Map.Entry)) { return false; } var e = (Entry<K, V>)o;
			final var k = e.getKey (); final var v = e.getValue (); var n = node(k);
			var r = (n != null && SwissMap.this.equals(n.getValue() , v)); return r;
		}
	}
	
	/**
	 * 
	 */
	static Platform<?> PLATFORM = new Platform64Ex();
	
	static final long OFFSET; static final Unsafe UNSAFE;
	
	static {
		try {
			Field v = Unsafe.class.getDeclaredField("theUnsafe");
			v.setAccessible(true); UNSAFE = (Unsafe) v.get(null);
			OFFSET = UNSAFE.arrayBaseOffset(byte[].class);/* ! */
		} catch(Throwable t) {
			throw new RuntimeException("failed to initialize",t);
		}
	}
	
	static {
		try {
			Class.forName("jdk.incubator.vector.ByteVector");
			//--add-modules=jdk.incubator.vector --enable-preview
			switch (ByteVector.SPECIES_PREFERRED.vectorShape()) {
				case S_64_BIT  -> PLATFORM = new Platform64 ();
				case S_128_BIT -> PLATFORM = new Platform128();
				case S_256_BIT -> PLATFORM = new Platform256();
				case S_512_BIT -> PLATFORM = new Platform512();
				default -> PLATFORM = new Platform64Ex(); /* ! */
			};
		} catch (Throwable tx) { PLATFORM = new Platform64Ex(); }
	}
	
	public interface Platform<T> {
		
		T simd(int index, byte[] array);
		
		long empty(T v); long eq(T v, byte w);
		
		default int next (int n) { return n; }
		
		int shift(); int maxGroups(); int maxAvgGroupLoad();
	}
	
	public static class Platform64 implements Platform<ByteVector> {
		
		@Override public int shift() { return 3; }
		
		@Override public int maxGroups() { return 1 << 28; }
		
		@Override public int maxAvgGroupLoad() { return 7; }
		
		@Override public ByteVector simd(int i , byte[] a) {
			
			return ByteVector.fromArray(SPECIES_64 , a , i);
		}
		
		@Override
		public long empty (ByteVector v) { return v.eq(EMPTY).toLong(); }
		
		@Override
		public long eq(ByteVector v, byte w) { return v.eq(w).toLong(); }
	}
	
	public static class Platform128 implements Platform<ByteVector> {
		
		@Override public int shift() { return 4; }
		
		@Override public int maxGroups () { return 1 << 27; }
		
		@Override public int maxAvgGroupLoad() { return 14; }
		
		@Override public ByteVector simd( int i, byte[] a ) {
			
			return ByteVector.fromArray(SPECIES_128 , a , i);
		}
		
		@Override
		public long empty (ByteVector v) { return v.eq(EMPTY).toLong(); }
		
		@Override
		public long eq(ByteVector v, byte w) { return v.eq(w).toLong(); }
	}
	
	public static class Platform256 implements Platform<ByteVector> {
		
		@Override public int shift() { return 5; }
		
		@Override public int maxGroups () { return 1 << 26; }
		
		@Override public int maxAvgGroupLoad() { return 28; }
		
		@Override public ByteVector simd( int i, byte[] a ) {
			
			return ByteVector.fromArray(SPECIES_256 , a , i);
		}
		
		@Override
		public long empty (ByteVector v) { return v.eq(EMPTY).toLong(); }
		
		@Override
		public long eq(ByteVector v, byte w) { return v.eq(w).toLong(); }
	}
	
	public static class Platform512 implements Platform<ByteVector> {
		
		@Override public int shift() { return 6; }
		
		@Override public int maxGroups () { return 1 << 25; }
		
		@Override public int maxAvgGroupLoad() { return 56; }
		
		@Override public ByteVector simd( int i, byte[] a ) {
			
			return ByteVector.fromArray(SPECIES_512 , a , i);
		}
		
		@Override
		public long empty (ByteVector v) { return v.eq(EMPTY).toLong(); }
		
		@Override
		public long eq(ByteVector v, byte w) { return v.eq(w).toLong(); }
	}
	
	public static class Platform64Ex implements Platform<Long> {
		
		static final long LO = 0x0101010101010101L;
		
		static final long HI = 0x8080808080808080L;
		
		@Override public int shift () { return 3; }
		
		@Override public int next (int n) { return n >> 3; }
		
		@Override public int maxGroups() { return 1 << 28; }
		
		@Override public int maxAvgGroupLoad() { return 7; }
		
		@Override public Long simd(final int i , byte[] a) {
			
			return UNSAFE.getLong(a, OFFSET + i); /* native */
		}
		
		@Override public long empty(final Long v) {
			long r = v.longValue() ^ (  HI  ); return (r - LO) & ~r & HI;
		}
		
		@Override public long eq(final Long v, final byte w) {
			long r = v.longValue() ^ (LO * w); return (r - LO) & ~r & HI;
		}
	}
}
