# 第五章：JVM运行时参数

## 1. GC日志参数

- -verbose:gc

制出gc白志信息，默认物出到标往物出

- -XX:+PrintGc

输出GC日志。类似: -verbose:gc

- -XX:+PrintGCDetails

在发生垃圾回收时打印内存回收详细的日志，并在进程退出时输出当前内存各区域分配情况

- -XX:+PrintGCTimeStamps

输出GC发生时的时间戳

- -XX:+PrintGCDateStamps

输出GC发生时的时间戳(以日期的形式，如2013-05-04T21:53:59.234+0800)

- -XX:+PrintHeapAtGC

每一次GC前和GC后，都打印堆信息

- `-Xloggc:<file>`

表示把GC日志写入到一个文件中去，而不是打印到标准输出中



## 2. GC日志格式





### 复习:GC分类

针对HotSpot VM的实现，它里面的GC按照回收区域又分为两大种类型:一种是部分收集（Partial GC），一种是整堆收集（Full GC)

- 部分收集:不是完整收集整个Java堆的垃圾收集。其中又分为:
  - 新生代收集（Minor GC / Young GC）:只是新生代（Eden\S0,s1）的垃圾收集·
  - 老年代收集（Major Gc / O ld Gc）:只是老年代的垃圾收集。
    - 目前，只有CMS GC会有单独收集老年代的行为。
    - 注意，很多时候Major GC会和FullGC混淆使用，需要具体分辨是老年代回收还是整堆回收。
  - 混合收集（Mixed GC):收集整个新生代以及部分老年代的垃圾收集。
    - 目前,只有G1 GC会有这种行为
- 整堆收集（ Full GC):收集整个java堆和方法区的垃圾收集。

**哪些情况会触发Full GC**

- 老年代空间不足

- 方法区空间不足
- 显式调用System.gc()
- Minor GC进入老年代的数据的平均大小大于老年代的可用内存
- 大对象直接进入老年代，老年代可用空间不足



### GC日志分类

#### MinorGC

```java
MinorGC(或young GC或YGC)日志:
[GC (Allocation Failure)[PSYoungGen: 31744K->2192K(36864K)]31744K->2200K(121856K)，0.0139308 secs] [Times: user=0.05 sys=0.01,real=o.01 secs]
```





![image-20210209213120331](.\images\image-20210209213120331.png)

![image-20210209213319344](.\images\image-20210209213319344.png)

#### FullGC

**Full GC日志介绍:**

```java
[Full GC(Metadata GC Threshold)[PSYoungGen: 5104K->0K(132096K)][Par0ldGen: 416K->5453K(50176K)]5520K->5453K(182272K)，[Metaspace:20637K->20637K(1067008K)]，0.0245883 secs] [Times: user=0.06 sys=0.00,real=0.02 secs]
```





![image-20210209212428842](.\images\image-20210209212428842.png)



![image-20210209213219224](.\images\image-20210209213219224.png)

### GC日志结构剖析

#### 垃圾收集器   

- 使用serial收集器在新生代的名字是Default New Generation，因此显示的是"[DefNew"·

- 使用ParNew收集器在新生代的名字会变成"[ParNew" ,意思是"Parallel New Generation"

- 使用parallel Scavenge收集器在新生代的名字是"[PSYoungGen",这里的JDK1.7使用的就是PSYoungGen

- 使用Parallel old Generation收集器在老年代的名字是"[ParoldGen"

- 使用G1收集器的话，会显示为"garbage-first heap"



**Allocation Failure**

表明本次引起GC的原因是因为在年轻代中没有足够的空间能够存储新的数据了。

#### GC前后情况

通过图示，我们可以发现GC日志格式的规律一般都是:GC前内存占用一>GC后内存占用（该区域内存总大小)

**[PSYoungGen: 5986K->696K(8704K)] 5986K->704K(9216K)**

中括号内:GC回收前年轻代堆大小，回收后大小，（年轻代堆总大小)

括号外:GC回收前年轻代和老年代大小，回收后大小，(年轻代和老年代总大小)

#### GC时间



GC日志中有三个时间:user，sys和real

- user - 进程执行用户态代码（核心之外）所使用的时间。**这是执行此进程所使用的实际CPU时间**，其他进程和此进程阻塞的时间并不包括在内。在垃圾收集的情况下，表示GC线程执行所使用的 CPU总时间。

- sys -进程在内核态消耗的CPU 时间，即**在内核执行系统调用或等待系统事件所使用的CPU 时间**

- real - 程序从开始到结束所用的时钟时间。这个时间包括其他进程使用的时间片和进程阻差的时间（比如等待I/0完成）。对于并行gc，这个数字应该接近（用户时间+系统时间）除以垃圾收集器使用的线程数。

由于多核的原因，一般的GC事件中，real time是小于sys + user time的，因为一般是多个线程并发的去做GC，所以real time是要小于sys+user time的。如果real>sys+user的话，则你的应用可能存在下列问题:Io负载非常重或者是CPU不够用。

### Minor GC日志解析 

```java
2020-11-20T17:19:43.265-0800: 0.822:[GC(ALLOCATION FAILURE)[PSYOUNGGEN:76800K->8433K(89600K)] 76800K->8449K(294400K)，0.0088371 SECS] [TIMES:USER=0.02 SYS=0.01,REAL=O.01 SECS]
```

- 2020-11-20T17:19:43.265-0800
  - 日志打印时间日期格式如: 2013-05-04T21:53:59.234+0800

- 0.822:
  - gc发生时，Java虚拟机启动以来经过的秒数

- [GC (Allocation Failure)
  - 发生了一次垃圾回收，这是一次Minor Gc。它不区分新生代GC还是老年代GC，括号里的内容是gc发生的原因，这里的Allocation Failure的原因是新生代中没有足够区域能够存放需要分配的数据而失败。

- [PsYoungGen: 76800K->8433K(89600K)]
  - PSYoungGen:表示GC发生的区域，区域名称与使用的GC收集器是密切相关的
    - Serial收集器:Default New Generation显示DefNew
    - ParNew收集器:ParNew
    - Parallel Scanvenge收集器:PSYoung
    - 老年代和新生代同理，也是和收集器名称相关
  - 76800K->8433K(89600K): GC前该内存区域已使用容量- >GC后该区域容量(该区域总容量)
    - 如果是新生代，总容量则会显示整个新生代内存的9/10,即eden+from/to区
    - 如果是老年代，总容量则是全部内存大小，无变化



- 76800K->8449K(294400K)
  - 在显示完区域容量GC的情况之后，会接着显示整个堆内存区域的GC情况:GC前堆内存已使用容量- >GC堆内存容量（堆内存总容量)堆内存总容量= 9/10新生代+老年代<初始化的内存大小

- 0.0088371 secs]
  - 整个GC所花费的时间，单位是秒

- [Times: user=0.02 sys=0.01, real=0.01 secs]
  - user:指的是CPU工作在用户态所花费的时间
  - sys:指的是CPU工作在内核态所花费的时间
  - real:指的是在此次GC事件中所花费的总时间



### Full GC日志解析

```java
2020-11-20T17:19:43.794-0800: 1.351:[FULL GC(METADATA GC THRESHOLD)[PSYOUNGGEN: 10082K->OK(89600K)][PAROLDGEN: 32K->9638K(204800K)]10114K->9638K( 29440OK),[METASPACE: 20158K->20156K(1067008K)]，0.0285388 SECS] [TIMES: USER=0.11sYS=0.00，REAL=O.03 SECS]
```

- 2020-11-20T17:19:43.794-0800
  - 日志打印时间日期格式如: 2013-05-04T21:53:59.234+0800

- 1.351
  - gc发生时，Java虚拟机启动以来经过的秒数

- Full GC (Metadata GC Threshold)
  - 发生了一次垃圾回收，这是一次FULL GC。它不区分新生代GC还是老年代Gc
  - 括号里的内容是gc发生的原因，这里的Metadata GC Threshold的原因是Metaspace区不够用了。
    - Full GC(Ergonomics) : JVM自适应调整导致的G
    - Full Gc(System):调用了System.gc( )方法



- [PSYoungGen: 10082K->0K(89600K)]
  - PSYoungGen:表示GC发生的区域，区域名称与使用的GC收集器是密切相关的
    - Serial收集器:Default New Generation显示DefNew
    - ParNew收集器:ParNew
    - Parallel Scanvenge收集器:PSYoung
    - 老年代和新生代同理，也是和收集器名称相关
  - 10082K->0K(89600K): GC前该内存区域已使用容量->GC后该区域容量(该区域总容量)
    - 如果是新生代，总容量则会显示整个新生代内存的9/10，即eden+from/to区
    - 如果是老年代，总容量则是全部内存大小，无变化



- [ParOldGen: 32K->9638K(20480OK)]
  - 老年代区域没有发生Gc，因为本次GC是metaspace引起的

- 10114K->9638K(294400K),
  - 在显示完区域容量GC的情况之后，会接着显示整个堆内存区域的GC情况:GC前堆内存已使用容量->GC堆内存容量（堆内存总容量)堆内存总容量= 9/10新生代+老年代<初始化的内存大小

- [Metaspace:20158K->20156K(1067008K)],
  - metaspace GC回收2K空间

- 0.0285388 secs
  - 整个GC所花费的时间，单位是秒

- [Times: user=0.11 sys=0.00, real=0.03 secs]
  - user:指的是CPU工作在用户态所花费的时间
  - sys:指的是CPU工作在内核态所花费的时间
  - real:指的是在此次GC事件中所花费的总时间



## 3. GC日志分析工具

```java
/**
 * java.lang.OutOfMemoryError: Metaspace异常演示：
 * 
 * -Xms60m -Xmx60m -XX:MetaspaceSize=10m -XX:MaxMetaspaceSize=10m -XX:SurvivorRatio=8 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC -Xloggc:d:/MetaspaceOOM.log
 *
 * @author shkstart
 * @create 13:25
 */
public class MetaspaceOOM extends ClassLoader {
    public static void main(String[] args) {
        int j = 0;
        try {
            MetaspaceOOM test = new MetaspaceOOM();
            for (int i = 0; i < 10000; i++) {
                //创建ClassWriter对象，用于生成类的二进制字节码
                ClassWriter classWriter = new ClassWriter(0);
                //指明版本号，修饰符，类名，包名，父类，接口
                classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "Class" + i, null, "java/lang/Object", null);
                //返回byte[]
                byte[] code = classWriter.toByteArray();
                //类的加载
                test.defineClass("Class" + i, code, 0, code.length);//Class对象
                j++;
            }
        } finally {
            System.out.println(j);
        }
    }
}
```



```java
/**
 * 测试生成详细的日志文件
 *
 * -Xms60m -Xmx60m -XX:SurvivorRatio=8 -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC -Xloggc:d:/GCLogTest.log
 *
 * @author shkstart
 * @create 14:27
 */
public class GCLogTest {
    public static void main(String[] args) {
        ArrayList<byte[]> list = new ArrayList<>();

        for (int i = 0; i < 5000; i++) {
            byte[] arr = new byte[1024 * 50];//50KB
            list.add(arr);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
```



### GCeasy

**GCeasy  一款超好用的在线分析GC日志的网站**

官网地址: https://gceasy.io，GCeasy是一款在线的GC日志分析器，可以通过Gc日志分析进行内存泄漏检测、GC暂停原因分析、JVM配置建议优化等功能，而且是可以免费使用的（有一些服务是收费的）。

![image-20210209221536323](.\images\image-20210209221536323.png)

**将上面生成的MetaspaceOOM.log日志文件上传**

![image-20210209222712976](.\images\image-20210209222712976.png)



**将上面生成的GCLogTest.log日志文件上传**

![image-20210209223027456](.\images\image-20210209223027456.png)

![image-20210209223223598](.\images\image-20210209223223598.png)

![image-20210209223554777](.\images\image-20210209223554777.png)





### GCViewer

#### 基本概述

上面介绍了一款在线的GC日志分析器，下面介绍一个离线版的GCViewer。

GCViewer是一个免费的、开源的分析小工具，用于可视化查看由SUN/Oracle,IBM,HP和BEAJava虚拟机产生的垃圾收集器的日志。I

GCViewer用于可视化Java VM选项-verbose:gc和.NET生成的数据`-Xloggc:<file>`。它还计算与垃圾回收相关的性能指标（吞吐量，累积的暂停，最长的暂停等）。当通过更改世代大小或设置初始堆大小来调整特定应用程序的垃圾回收时，此功能非常有用。



#### 安装



1.下载GCViewer工具

源码下载:https :l/github.com/chewiebug/GCViewer

运行版本下载: https://github.com/chewiebug/GCViewer/wiki/Changelog

2.只需双击gcviewer-1.3x.jar或运行java -jar gcviewer-1.3x.jar(它需要运行java1.8 vm），即可启动GcViewer (gui)



![image-20210209224030549](.\images\image-20210209224030549.png)

![image-20210209224217243](.\images\image-20210209224217243.png)



### 其他工具



#### GChisto


GChisto是一款专业分析gc日志的工具，可以通过gc日志来分析:MinorGC、Full GC的次数、频率、持续时间等，通过列表、报表、图表等不同形式来反应gc的情况。

虽然界面略显粗糙，但是功能还是不错的。

官网上没有下载的地方，需要自己从SVN上拉下来编译

不过这个工具似乎没怎么维护了，存在不少bug





#### HPjmeter

工具很强大，但只能打开由以下参数生成的GC log,-verbose:gc,-Xloggc:gc.log。添加其他参数生成的gc.log无法打开

HPjmeter集成了以前的HPjtune功能，可以分析在HP机器上产生的垃圾回收日志文件









