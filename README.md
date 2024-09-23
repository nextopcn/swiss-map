## A Google Swiss table implementation written in Java.

### Requirement

required Java version

```
Java 21
```

required VM option

```
--add-opens=jdk.unsupported/sun.misc=ALL-UNNAMED
```



### Usage

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