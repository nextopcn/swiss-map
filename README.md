## A Google Swiss table implementation written in Java.

### Requirement

required Java version

```
Java 21
```

required VM option

```
--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED
```



### Usage

```xml

<dependency>
    <groupId>cn.nextop</groupId>
    <artifactId>swiss-map</artifactId>
    <version>1.0.0</version>
</dependency>

```

```java
SwissMap<Integer, Integer> map = new SwissMap<>(16);
map.put(1, 1);
map.get(1);
```

### SIMD optimization

The following vm options can be added to improve the performance of the Swiss map.

```
--add-modules=jdk.incubator.vector --enable-preview
```

### Benchmark

```textmate
Benchmark                                    Mode  Cnt        Score       Error  Units

SwissMapBenchmark.benchHashMapForeach       thrpt    5   538268.449 ± 11514.793  ops/s
SwissMapBenchmark.benchSwissMap64Foreach    thrpt    5  2039246.878 ± 66198.912  ops/s
SwissMapBenchmark.benchSwissMap64ExForeach  thrpt    5  1978412.382 ± 89716.763  ops/s
SwissMapBenchmark.benchSwissMap128Foreach   thrpt    5  2039027.053 ± 37183.759  ops/s
SwissMapBenchmark.benchSwissMap256Foreach   thrpt    5  2058253.928 ± 10812.746  ops/s

SwissMapBenchmark.benchHashMapGet           thrpt    5   326126.844 ±  4112.307  ops/s
SwissMapBenchmark.benchSwissMap64Get        thrpt    5   133646.872 ±  5739.705  ops/s
SwissMapBenchmark.benchSwissMap64ExGet      thrpt    5   116174.010 ±  4365.354  ops/s
SwissMapBenchmark.benchSwissMap128Get       thrpt    5   137356.028 ±  6683.400  ops/s
SwissMapBenchmark.benchSwissMap256Get       thrpt    5   145945.695 ± 13251.820  ops/s

SwissMapBenchmark.benchHashMapPut           thrpt    5    33424.117 ±  1360.542  ops/s
SwissMapBenchmark.benchSwissMap64Put        thrpt    5    32273.150 ±   605.514  ops/s
SwissMapBenchmark.benchSwissMap64ExPut      thrpt    5    30740.386 ±  1320.349  ops/s
SwissMapBenchmark.benchSwissMap128Put       thrpt    5    33234.220 ±   491.740  ops/s
SwissMapBenchmark.benchSwissMap256Put       thrpt    5    33516.981 ±  1128.439  ops/s

SwissMapBenchmark.benchHashMapValues        thrpt    5   502824.230 ± 25162.399  ops/s
SwissMapBenchmark.benchSwissMap64Values     thrpt    5   606536.142 ± 99316.343  ops/s
SwissMapBenchmark.benchSwissMap64ExValues   thrpt    5   616832.970 ± 14004.288  ops/s
SwissMapBenchmark.benchSwissMap128Values    thrpt    5   626356.301 ± 18869.597  ops/s
SwissMapBenchmark.benchSwissMap256Values    thrpt    5   612020.609 ± 23162.716  ops/s
```
