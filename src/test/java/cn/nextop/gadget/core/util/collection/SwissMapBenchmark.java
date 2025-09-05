package cn.nextop.gadget.core.util.collection;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author Baoyi Chen
 * 
 * Benchmark                                    Mode  Cnt        Score       Error  Units
 * 
 * SwissMapBenchmark.benchHashMapForeach       thrpt    5   538268.449 ± 11514.793  ops/s
 * SwissMapBenchmark.benchSwissMap64Foreach    thrpt    5  2039246.878 ± 66198.912  ops/s
 * SwissMapBenchmark.benchSwissMap64ExForeach  thrpt    5  1978412.382 ± 89716.763  ops/s
 * SwissMapBenchmark.benchSwissMap128Foreach   thrpt    5  2039027.053 ± 37183.759  ops/s
 * SwissMapBenchmark.benchSwissMap256Foreach   thrpt    5  2058253.928 ± 10812.746  ops/s
 * 
 * SwissMapBenchmark.benchHashMapGet           thrpt    5   326126.844 ±  4112.307  ops/s
 * SwissMapBenchmark.benchSwissMap64Get        thrpt    5   133646.872 ±  5739.705  ops/s
 * SwissMapBenchmark.benchSwissMap64ExGet      thrpt    5   116174.010 ±  4365.354  ops/s
 * SwissMapBenchmark.benchSwissMap128Get       thrpt    5   137356.028 ±  6683.400  ops/s
 * SwissMapBenchmark.benchSwissMap256Get       thrpt    5   145945.695 ± 13251.820  ops/s
 * 
 * SwissMapBenchmark.benchHashMapPut           thrpt    5    33424.117 ±  1360.542  ops/s
 * SwissMapBenchmark.benchSwissMap64Put        thrpt    5    32273.150 ±   605.514  ops/s
 * SwissMapBenchmark.benchSwissMap64ExPut      thrpt    5    30740.386 ±  1320.349  ops/s
 * SwissMapBenchmark.benchSwissMap128Put       thrpt    5    33234.220 ±   491.740  ops/s
 * SwissMapBenchmark.benchSwissMap256Put       thrpt    5    33516.981 ±  1128.439  ops/s
 * 
 * SwissMapBenchmark.benchHashMapValues        thrpt    5   502824.230 ± 25162.399  ops/s
 * SwissMapBenchmark.benchSwissMap64Values     thrpt    5   606536.142 ± 99316.343  ops/s
 * SwissMapBenchmark.benchSwissMap64ExValues   thrpt    5   616832.970 ± 14004.288  ops/s
 * SwissMapBenchmark.benchSwissMap128Values    thrpt    5   626356.301 ± 18869.597  ops/s
 * SwissMapBenchmark.benchSwissMap256Values    thrpt    5   612020.609 ± 23162.716  ops/s
 */
@Fork(value = 1, jvmArgsAppend = {
		"--add-modules=jdk.incubator.vector",
		"--enable-preview"
})
@Warmup(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
public class SwissMapBenchmark {
	
	@State(Scope.Benchmark)
	public static class Get {
		private int n = 1024;
		private float ex = 0.25f;
		private SwissMap<Object, Object> map64;
		private SwissMap<Object, Object> map128;
		private SwissMap<Object, Object> map256;
		private SwissMap<Object, Object> map64ex;
		private HashMap<Object, Object> hashmap;
		private Object[] objects;
		
		@Setup
		public void setup() {
			map64 = new SwissMap<>(n, ex, new SwissMap.Platform64());
			map128 = new SwissMap<>(n, ex, new SwissMap.Platform128());
			map256 = new SwissMap<>(n, ex, new SwissMap.Platform256());
			map64ex = new SwissMap<>(n, ex, new SwissMap.Platform64Ex());
			hashmap = new HashMap<>(n * 2);
			objects = IntStream.range(0, n).mapToObj(x -> new Object()).toArray();
			for (int i = 0; i < n; i++) {
				map64.put(objects[i], objects[i]);
				map128.put(objects[i], objects[i]);
				map256.put(objects[i], objects[i]);
				map64ex.put(objects[i], objects[i]);
				hashmap.put(objects[i], objects[i]);
			}
		}
	}
	
	@State(Scope.Benchmark)
	public static class Put {
		private int n = 1024;
		private float ex = 0.25f;
		private SwissMap<Object, Object> map256;
		private SwissMap<Object, Object> map64;
		private SwissMap<Object, Object> map128;
		private SwissMap<Object, Object> map64ex;
		private HashMap<Object, Object> hashmap;
		private Object[] objects;
		
		@Setup(Level.Invocation)
		public void setup() {
			map64 = new SwissMap<>(n, ex, new SwissMap.Platform64());
			map128 = new SwissMap<>(n, ex, new SwissMap.Platform128());
			map256 = new SwissMap<>(n, ex, new SwissMap.Platform256());
			map64ex = new SwissMap<>(n, ex, new SwissMap.Platform64Ex());
			hashmap = new HashMap<>(n * 2);
			objects = IntStream.range(0, n).mapToObj(x -> new Object()).toArray();
		}
	}
	
	@Benchmark
	public void benchSwissMap64Get(Get get, Blackhole hole) {
		for (int i = 0; i < get.n; i++) {
			hole.consume(get.map64.get(get.objects[i]));
		}
	}
	
	@Benchmark
	public void benchSwissMap64Put(Put put, Blackhole hole) {
		for (int i = 0; i < put.n; i++) {
			hole.consume(put.map64.put(put.objects[i], put.objects[i]));
		}
	}
	
	@Benchmark
	public void benchSwissMap64Foreach(Get get, Blackhole hole) {
		get.map64.forEach((k, v) -> hole.consume(k));
	}
	
	@Benchmark
	public void benchSwissMap64Values(Get get, Blackhole hole) {
		for(Object v : get.map64.values()) {
			hole.consume(v);
		}
	}
	
	@Benchmark
	public void benchSwissMap64ExGet(Get get, Blackhole hole) {
		for (int i = 0; i < get.n; i++) {
			hole.consume(get.map64ex.get(get.objects[i]));
		}
	}
	
	@Benchmark
	public void benchSwissMap64ExPut(Put put, Blackhole hole) {
		for (int i = 0; i < put.n; i++) {
			hole.consume(put.map64ex.put(put.objects[i], put.objects[i]));
		}
	}
	
	@Benchmark
	public void benchSwissMap64ExForeach(Get get, Blackhole hole) {
		get.map64ex.forEach((k, v) -> hole.consume(k));
	}
	
	@Benchmark
	public void benchSwissMap64ExValues(Get get, Blackhole hole) {
		for(Object v : get.map64ex.values()) {
			hole.consume(v);
		}
	}
	
	@Benchmark
	public void benchSwissMap128Get(Get get, Blackhole hole) {
		for (int i = 0; i < get.n; i++) {
			hole.consume(get.map128.get(get.objects[i]));
		}
	}
	
	@Benchmark
	public void benchSwissMap128Put(Put put, Blackhole hole) {
		for (int i = 0; i < put.n; i++) {
			hole.consume(put.map128.put(put.objects[i], put.objects[i]));
		}
	}
	
	@Benchmark
	public void benchSwissMap128Foreach(Get get, Blackhole hole) {
		get.map128.forEach((k, v) -> hole.consume(k));
	}
	
	@Benchmark
	public void benchSwissMap128Values(Get get, Blackhole hole) {
		for(Object v : get.map128.values()) {
			hole.consume(v);
		}
	}
	
	@Benchmark
	public void benchSwissMap256Get(Get get, Blackhole hole) {
		for (int i = 0; i < get.n; i++) {
			hole.consume(get.map256.get(get.objects[i]));
		}
	}
	
	@Benchmark
	public void benchSwissMap256Put(Put put, Blackhole hole) {
		for (int i = 0; i < put.n; i++) {
			hole.consume(put.map256.put(put.objects[i], put.objects[i]));
		}
	}
	
	@Benchmark
	public void benchSwissMap256Foreach(Get get, Blackhole hole) {
		get.map256.forEach((k, v) -> hole.consume(k));
	}
	
	@Benchmark
	public void benchSwissMap256Values(Get get, Blackhole hole) {
		for(Object v : get.map256.values()) {
			hole.consume(v);
		}
	}
	
	@Benchmark
	public void benchHashMapGet(Get get, Blackhole hole) {
		for (int i = 0; i < get.n; i++) {
			hole.consume(get.hashmap.get(get.objects[i]));
		}
	}
	
	@Benchmark
	public void benchHashMapPut(Put put, Blackhole hole) {
		for (int i = 0; i < put.n; i++) {
			hole.consume(put.hashmap.put(put.objects[i], put.objects[i]));
		}
	}
	
	@Benchmark
	public void benchHashMapForeach(Get get, Blackhole hole) {
		get.hashmap.forEach((k, v) -> hole.consume(k));
	}
	
	@Benchmark
	public void benchHashMapValues(Get get, Blackhole hole) {
		for(Object v : get.hashmap.values()) {
			hole.consume(v);
		}
	}
	
	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include(SwissMapBenchmark.class.getSimpleName())
				.shouldDoGC(true)
				.build();
		new Runner(opt).run();
	}
}
