---
title: 25-JVM运行时参数
tags:
notebook: 宋红康-JVM
---

### 1 JVM 参数选项类型

#### 1.1 类型一:标准参数选项

#### **特点**

- 比较稳定,后续版本基本不会变化

- 以-开头

#### **各种选项**

- 运行 java 或者 java -help 可以看到所有的标准选项

<img src="https://tva1.sinaimg.cn/large/006JhGcily1gzubzk00jqj30i70ly14p.jpg" alt="image-20210209181356358" width="70%">

#### **补充内容:-server 与-client**

Hotspot JVM 有两种模式，分别是 server 和 client，分别通过-server 和-client 模式设置

1．在 32 位 windows 系统上，默认使用 Client 类型的 JVM。要想使用 server 模式，则机器配置至少有 2 个以上的 CPU 和 2G 以上的物理内存。client 模式适用于`对内存要求较小的桌面应用程序`，默认使用 Serial 串行垃圾收集器

2．64 位机器上只支持 server 模式的 JVM，适用于需要大内存的应用程序，默认使用并行垃圾收集器

关于 server 和 client 的官网介绍为: https://docs.oracle.com/javase/8/docs/technotes/guides/vm/server-class.html

#### 1.2 类型二:-X 参数选项

#### **特点**

- 非标准化参数

- 功能还是比较稳定的。但官方说后续版本可能会变更

- 以-X 开头

#### **各种选项**

运行 java -X 命令可以看到所有的 X 选项

<img src="https://tvax4.sinaimg.cn/large/006JhGcily1gzuc6j0bfbj30o20j4anx.jpg" alt="image-20210209181751666" width="70%">

#### **JVM 的 JIT 编译模式相关的选项**

- -Xint 禁用 JIT，所有字节码都被解释执行，这个模式的速度最慢的

- -Xcomp 所有字节码第一次使用就都被编译成本地代码，然后再执行

- -Xmixed 混合模式，默认模式，让 JIT 根据程序运行的情况，有选择地将某些代码编译成本地代码

#### **特别地**

**-Xmx -Xms -Xss 属于哪个 XX 参数?**

- **`-Xms<size>`**

设置初始 Java 堆大小，等价于-XX:InitialHeapSize

- **`-Xmx<size>`**

设置最大 Java 堆大小，等价于-XX:MaxHeapSize

- **`-Xss<size>`**

设置 Java 线程堆栈大小，-XX:ThreadStackSize

#### 1.3 类型三:-XX 参数选项

#### **特点**

- 非标准化参数

- 使用的最多的参数类型

- 类选项属于实验性，不稳定

- 以-XX 开头

#### **分类**

**Boolean 类型格式**

- `-XX:+<option>`表示启用 option 属性

- `-XX:-<option>`表示禁用 option 属性

- 举例

```shell
-XX:+UseParallelGC          # 选择垃圾收集器为并行收集器
-XX:+UseG1GC                 # 表示启用G1收集器
-XX:+UseAdaptiveSizePolicy  # 自动选择年轻代区大小和相应的Survivor区比例
```

- 说明:因为有的指令默认是开启的，所以可以用-关闭

**非 Boolean 类型格式(key-value 类型)**

- 子类型 1: 数值型格式`-XX:<option> = <number>`

number 表示数值，number 可以带上单位，比如: 'm'、'M'表示兆，'k'、'K'表示 Kb,'g'、'G'表示 g（例如 32k 跟 32768 是一样的效果)

例如:

```shell
-XX:NewSize=1024m   # 表示设置新生代初始大小为1024兆
-XX:MaxGCPauseMillis=500  # 表示设置Gc停顿时间:500毫秒
-XX:GCTimeRatio=19  # 表示设置吞吐量
-XX:NewRatio=2   # 表示新生代与老年代的比例
```

- 子类型 2: 非数值型格式`-XX:<name> = <string>`

例如:

```shell
-XX:HeapDumpPath=/usr/local/heapdump.hprof   # 用来指定heap转存文件的存储路径
```

#### **特别地**

**-XX:+PrintFlagsFinal**

- 输出所有参数的名称和默认值

- 默认不包括 Diagnostic 和 Experimental 的参数

- 可以配合-XX:+UnlockDiagnosticvMOptions 和-XXUnlockExperimentalVMOptions 使用

<img src="https://tva1.sinaimg.cn/large/006JhGcily1gzudbjgr4ij30tz0f0wrh.jpg" alt="image-20210209183730605" width="80%">

### 2 添加 JVM 参数选项

#### 2.1 Eclipse

<img src="https://tva3.sinaimg.cn/large/006JhGcily1gzude1gmn6j30q60pgtkw.jpg" alt="image-20210209184123218" width="77%">

#### 2.2 IDEA

<img src="https://tva1.sinaimg.cn/large/006JhGcily1gzudeki5izj31hc0smtsi.jpg" alt="image-20210209184156421" width="85%">

#### 2.3 运行 jar 包

```shell
java -Xms50m -Xmx50m -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -jar demo.jar
```

#### 2.4 通过 Tomcat 运行 war 包

```shell
#Linux系统下可以在tomcat /bin /catalina.sh中添加类似如下配置;
set "JAVA_OPTS=-Xms512M -Xmx1024M"

#windows系统下在catalina.bat中添加类似如下配置:
set "JAVA_OPTS=-Xms512M -Xmx1024M"
```

#### 2.5 程序运行过程中

```shell

使用jinfo -flag <name> = <value> <pid>设置非Boolean类型参数

使用jinfo -flag [+/-]<name> <pid>设置Boolean类型参数
```

> 并非所有参数都支持动态修改。参数只有被标记为 manageable 的 flag 可以被实时修改。其实，这个修改能力是极其有限的。

```shell
#可以查看被标记为manageable的参数
java -XX:+PrintFlagsFinal -version | grep manageable
```

<img src="https://tva2.sinaimg.cn/large/006JhGcily1gzudgwdrrbj30xa0hbwu9.jpg" alt="image-20210209185226996" width="88%">

### 3 常用的 JVM 参数选项

#### 3.1 打印设置的 XX 选项及值

- `-XX:+PrintCommandLineFlags`

可以让在程序运行前打印出用户手动设置或者 JVM 自动设置的 XX 选项

<img src="https://tvax1.sinaimg.cn/large/006JhGcily1gzudkx4djbj30xd02hwhi.jpg" alt="image-20210209191529106" width="88%">

- `-XX:+PrintFlagsInitial`

表示打印出所有 XX 选项的默认值

<img src="https://tvax2.sinaimg.cn/large/006JhGcily1gzudnvsqccj30sz0f2qga.jpg" alt="image-20210209185755414" width="88%">

- `-XX:+PrintFlagsFinal`

表示打印出 XX 选项在运行程序时生效的值

- `-XX:+PrintVMOptions`

打印 JVM 的参数

#### 3.2 堆、栈、方法区等内存大小设置

#### **栈**

- `-Xss128k`

设置每个线程的栈大小为 128k

`等价于-XX:ThreadStackSize=128k`

#### **堆内存**

- `-Xms3550m`

`等价于-xX:InitialHeapSize`，设置 JVM 初始堆内存为 355OM

- `-Xmx3550m`

`等价于-XX:MaxHeapSize`，设置 JVM 最大堆内存为 3550M

- `-Xmn2g`

设置年轻代大小为 2G,官方推荐配置为整个堆大小的 3/8

- `-XX:NewSize=1024m`

设置年轻代初始值为 1024M

- `-XX:MaxNewSize=1024m`

设置年轻代最大值为 1024M

- `-XX:SurvivorRatio=8`

设置年轻代中 Eden 区与一个 Survivor 区的比值, 默认为 8

- `-XX:+UseAdaptiveSizePolicy`

自动选挥各区大小比例

> 默认开启状态，会调整 Eden 区与一个 Survivor 区的比值为 6，如果要使默认 Eden ： Survivor = 8 ： 1 生效，需要关闭这个选项，如果不关闭的话需要显示指定-XX:SurvivorRatio=8

- `-XX:NewRatio=4`

设置老年代与年轻代(包括 1 个 Eden 和 2 个 Survivor 区）的比值（默认是 2）

- `-XX:PretenureSizeThreadshold=1024`

设置让大于此阈值的对象直接分配在老年代，单位为字节，只对 Serial、ParNews 收集器有效

- `-XX:MaxTenuringThreshold=15`

默认值为 15，新生代每次 MinorGC 后，还存活的对象年龄+1，当对象的年龄大于设置的这个值时就进入老年代

- `-XX+PrintTenuringDistribution`

让 JVM 在每次 MinorGC 后打印出当前使用的 Survivor 中对象的年龄分布

- `-XX:TargetSurvivorRatio`

表示 MinorGC 结束后 Survivor 区域中占用空间的期望比例

#### **方法区**

##### **永久代**

- -XX:PermSize=256m 设置永久代初始值为 256M

- -XX:MaxPermSize=256m 设置永久代最大值为 256M

##### **元空间**

- -XX:MetaspaceSize 初始空间大小
- -XX:MaxMetaspaceSize 最大空间，默认没有限制
- -XX:+UseCompressedOops 压缩对象指针(默认开启)
- -XX:+UseCompressedClassPointers 压缩类指针
- -XX:CompressedclassSpaceSize 设置 Class Metaspace 的大小，默认 1G

##### **直接内存**

- -XX:MaxDirectMemorySize 指定 DirectMemory 容量，若未指定，则默认与 Java 堆最大值一样

#### 3.3 OutofMemory 相关的选项

- `-XX:+HeapDumpOnOutOfMemoryError`

表示在内存出现 OOM 的时候，把 Heap 转存(Dump)到文件以便后续分析

- `-XX:+HeapDumpBeforeFullGC`

表示在出现 FullGC 之前，生成 Heap 转储文件

- `-XX:HeapDumpPath= <path>`

指定 heap 转存文件的存储路径(默认是当前路径)

- `-XX:OnOutOfMemoryError`

指定一个可行性程序或者脚本的路径，当发生 OOM 的时候，去执行这个脚本

```shell
对OnOutOfMemoryError的运维处理

以部署在linux系统/opt/Server目录下的Server.jar为例
1.在run.sh启动脚本中添加jvm参数:
-XX:OnoutOfMemoryError=/opt/Server/restart.sh

2.restart.sh脚本
linux环境:
# !/bin/bash
pid=$(ps -eflgrep Server.jar|awk '{if($8=="java") {print $2}} ')kill -9 $pid
cd /opt/Serverl
sh run.sh

windows环境:
echo off
wmic process where Name= 'java.exe' deletecd D: \Server
start run.bat

```

#### 3.4 垃圾收集器相关选项

**7 款经典收集器与垃圾分代之间的关系**

<img src="https://tva4.sinaimg.cn/large/006JhGcily1gzv83lrgjuj30p205ntau.jpg" alt="image-20210209194937256" width="75%">

**垃圾收集器的组合关系**

<img src="https://tva4.sinaimg.cn/large/006JhGcily1gzv8402x6tj30ks0axq6b.jpg" alt="image-20210209194919283" width="77%">

#### **查看默认垃圾收集器**

- -XX:+PrintCommandLineFlags:查看命令行相关参数（包含使用的垃圾收集器)
- 使用命令行指令:jinfo -flag 相关垃圾回收器参数进程 ID

#### **Serial 回收器**

Serial 收集器作为 HotSpot 中 Client 模式下的默认新生代垃圾收集器。Serial old 是运行在 Client 模式下默认的老年代的垃圾回收器。

- XX:+UseSerialGC

指定年轻代和老年代都使用串行收集器。等价于新生代用 Serial Gc，且老年代用 Serial old GC。可以获得最高的单线程收集效率。

#### **ParNew 回收器**

- -XX :+UseParNewGC

手动指定使用 ParNew 收集器执行内存回收任务。它表示年轻代使用并行收集器，不影响老年代。

- -XX:ParallelGCThreads=N

限制线程数量，默认开启和 CPU 数相同的线程数。

#### **Parallel 回收器**

- `-XX:+UseParallelGC` 手动指定年轻代使用 Parallel 并行收集器执行内存回收任务。

- `-XX:+UseParallelOldGC` 手动指定老年代都是使用并行回收收集器。

  - 分别适用于新生代和老年代。默认 jdk8 是开启的。
  - 上面两个参数，默认开启一个，另一个也会被开启。**（互相激活）**

- `-XX:ParallelGCThreads` 设置年轻代并行收集器的线程数。一般地，最好与 CPU 数量相等，以避免过多的线程数影响垃圾收集性能。

  - 在默认情况下，当 CPU 数量小于 8 个，ParallelGCThreads 的值等于 CPU 数量。
  - 当 CPU 数量大于 8 个，ParallelGCThreads 的值等于 3+[5*CPU_Count]/8]。

- `-XX:MaxGCPauseMillis` 设置垃圾收集器最大停顿时间(即 STW 的时间)。单位是毫秒。

  - 为了尽可能地把停顿时间控制在 MNaxGCPauseMills 以内，收集器在工作时会调整 Java 堆大小或者其他一些参数。
  - 对于用户来讲，停顿时间越短体验越好。但是在服务器端，我们注重高并发，整体的吞吐量。所以服务器端适合 Parallel，进行控制。
  - 该参数使用需谨慎。

- `-XX:GCTimeRatio` 垃圾收集时间占总时间的比例(= 1/ (N + 1))。用于衡量吞吐量的大小。

  - 取值范围（0,100）。默认值 99，也就是垃圾回收时间不超过 1%。
  - 与前一个-XX:MaxGCPauseMillis 参数有一定矛盾性。暂停时间越长，Radio 参数就容易超过设定的比例。

- -XX:+UseAdaptiveSizePolicy 设置 Parallel Scavenge 收集器具有**自适应调节策略**

#### **CMS 回收器**

- `-XX:+UseConcMarkSweepGC` 手动指定使用 CMS 收集器执行内存回收任务

  - 开启该参数后会自动将-XX:+UseParNewGC 打开。即:ParNew(Young 区用)+CMS(Old 区用)+Serial old 的组合。

- `-XX:CMSInitiatingOccupanyFraction` 设置堆内存使用率的阈值，一旦达到该阈值，便开始进行回收。

  - JDK5 及以前版本的默认值为 68,即当老年代的空间使用率达到 68%时，会执行一次 CMS 回收。JDK6 及以上版本默认值为 92%
  - 如果内存增长缓慢，则可以设置一个稍大的值，大的阈值可以有效降低 CNS 的触发频率，减少老年代回收的次数可以较为明显地改善应用程序性能。反之，如果应用程序内存使用率增长很快，则应该降低这个阈值，以避免频繁触发老年代串行收集器。因此通过该选项便可以有效降低 Full GC 的执行次数。

- `-XX:+UseCMSCompactAtFullCollection` 用于指定在执行完 Full GC 后对内存空间进行压缩整理，以此避免内存碎片的产生。不过由于内存压缩整理过程无法并发执行，所带来的问题就是停顿时间变得更长了。

- `-XX:CMSFullGCsBeforeCompaction` 设置在执行多少次 Full GC 后对内存空间进行压缩整理。

- `-XX:ParallelCMSThreads` 设置 CMS 的线程数量。

- CMS 默认启动的线程数是(ParallelGCThreads+3)/4，ParallelGCThreads 是年轻代并行收集器的线程数。当 CPU 资源比较紧张时，受到 CMS 收集器线程的影响，应用程序的性能在

**补充参数**

- -XX:ConcGCThreads:设置并发垃圾收集的线程数，默认该值是基于 ParallelGCThreads 计算出来的;

- -XX:+UseCMSInitiatingOccupancyOnly:是否动态可调，用这个参数可以使 CNS 一直按 CMSInitiatingoccupancyFraction 设定的值启动

- -XX:+CMSScavengeBeforeRemark:强制 hotspot 虚拟机在 cms remark 阶段之前做一次 minor gc，用于提高 remark 阶段的速度;

- -XX:+CMSClassUnloadingEnable:如果有的话，启用回收 Perm 区(JDK8 之前）

- -XX:+CMSParallelInitialEnabled:用于开启 CNS initial-mark 阶段采用多线程的方式进行标记，用于提高标记速度，在 Java8 开始已经默认开启;

- -XX:+CMSParallelRemarkEnabled:用户开启 CNS remark 阶段采用多线程的方式进行重新标记，默认开启;

- -XX:+ExplicitGCInvokesConcurrent 、-XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses 这两个参数用户指定 hotspot 虚拟在执行 System.gc()时使用 CMS 周期;

- -XX:+CMSPrecleaningEnabled:指定 CMS 是否需要进行 Pre cleaning 这个阶段

**特别说明**

- JDK9 新特性:CMS 被标记为 Deprecate 了(EP291)

  - 如果对 JDK 9 及以上版本的 HotSpot 虚拟机使用参数-XX:+UseConcMarkSweepGC 来开启 CMS 收集器的话，用户会收到一个警告信息，提示 CMS 未来将会被废弃。

- JDK14 新特性:删除 CMS 垃圾回收器(EP363)
  - 移除了 CMS 垃圾收集器，如果在 JDK14 中使用-XX:+UseConcMarkSweepGC 的话，JVM 不会报错，只是给出一个 warning 信息，但是不会 exit。JVM 会自动回退以默认 GC 方式启动 JVM

openJDK 64-Bit Server VM warning: Ignoring option UseConcMarkSweepGC;support was removed in 14.0and the VM will continue execution using the default collector.

#### G1 回收器

- -XX:+UseG1GC

手动指定使用 G1 收集器执行内存回收任务。

- -XX:G1HeapRegionSize

设置每个 Region 的大小。值是 2 的幂，范围是 1MB 到 32MB 之间，目标是根据最小的 Java 堆大小划分出约 2048 个区域。默认是堆内存的 1/2000。

- -XX:MaxGCPauseMillis

设置期望达到的最大 GC 停顿时间指标(JVM 会尽力实现，但不保证达到)。默认值是 200ms

- -XX: ParallelGCThread

设置 STW 时 GC 线程数的值。最多设置为 8

- -XX:ConcGCThreads

设置并发标记的线程数。将 n 设置为并行垃圾回收线程数(ParallelGCThreads)的 1/4 左右。

- -XX:InitiatingHeapOccupancyPercent

设置触发并发 GC 周期的 Java 堆占用率阈值。超过此值，就触发 Gc。默认值是 45。

- -XX:G1NewSizePercent、-XX:G1MaxNewSizePercent

新生代占用整个堆内存的最小百分比（默认 5%）、最大百分比（默认 60%）

- -XX:G1ReservePercent=10

保留内存区域，防止 to space ( Survivor 中的 to 区）溢出

**Mixed Gc 调优参数**

注意:G1 收集器主要涉及到 Mixed Gc，Mixed GC 会回收 young 区和部分 old 区。

G1 关于 Mixed GC 调优常用参数:

- -XX:InitiatingHeapOccupancyPercent:设置堆占用率的百分比（0 到 100）达到这个数值的时候触发 global concurrent marking（全局并发标记），默认为 45%。值为 0 表示间断进行全局并发标记。

- -XX:G1MixedGCLiveThresholdPercent:设置 old 区的 region 被回收时候的对象占比，默认占用率为 85%。只有 old 区的 region 中存活的对象占用达到了这个百分比，才会在 Mixed GC 中被回收。

- -XX:G1HeapastePercent:在 global concurrent marking（全局并发标记结束之后，可以知道所有的区有多少空间要被回收，在每次 young GC 之后和再次发生 Mixed GC 之前，会检查垃圾占比是否达到此参数，只有达到了，下次才会发生 Mixed GC。

- -XX:G1MixedGCCountTarget:一次 global concurrent marking(全局并发标记）之后，最多执行 Mixed GC 的次数，默认是 8。

- -XX:G1OldCSetRegionThresholdPercent:设置 Mixed GC 收集周期中要收集的 old region 数的上限。默认值是 Java 堆的 10%

#### 怎么选择垃圾回收器

- 优先调整堆的大小让 VM 自适应完成。如果内存小于 100M，使用串行收集器

- 如果是单核、单机程序，并且没有停顿时间的要求，串行收集器

- 如果是多 CPU、需要高吞吐量、允许停顿时间超过 1 秒，选择并行或者 JVM 自己选择

- 如果是多 CPU、追求低停顿时间，需快速响应（比如延迟不能超过 1 秒，如互联网应用），使用并发收集器。官方推荐 G1，性能高。现在互联网的项目，基本都是使用 G1。

**特别说明:**

1．没有最好的收集器，更没有万能的收集;

2．调优永远是针对特定场景、特定需求，不存在一劳永逸的收集器 l

### GC 日志相关选项

```java
/**
 * -Xms60m -Xmx60m -XX:SurvivorRatio=8
 * @author shkstart
 * @create 14:27
 */
public class GCLogTest {
    public static void main(String[] args) {
        ArrayList<byte[]> list = new ArrayList<>();

        for (int i = 0; i < 500; i++) {
            byte[] arr = new byte[1024 * 100];//100KB
            list.add(arr);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

#### 常用参数

- -verbose:gc

输出 gc 日志信息，默认输出到标准输出，可以独立使用

- -XX+PrintGC

等同于-verbose:gc，表示打开简化的 GC 日志，可以独立使用

![image-20210209203024263](.\images\image-20210209203024263.png)

- -XX:+PrintGCDetails

在发生垃圾回收时打印内存回收详细的日志，并在进程退出时输出当前内存各区域分配情况，可以独立使用

![image-20210209202907031](.\images\image-20210209202907031.png)

- -XX:+PrintGCTimeStamps

输出 GC 发生时的时间戳，不可以独立使用，需要配合-XX:+PrintGCDetails 使用

![image-20210209203145923](.\images\image-20210209203145923.png)

- -XX:+PrintGCDateStamps

输出 GC 发生时的时间戳(以日期的形式，如 2013-05-04T21:53:59.234+0800)，不可以独立使用，需要配合-XX:+PrintGCDetails 使用

![image-20210209203236836](.\images\image-20210209203236836.png)

- -XX:+PrintHeapAtGC

每一次 GC 前和 GC 后，都打印堆信息，可以独立使用

![image-20210209203331316](.\images\image-20210209203331316.png)

![image-20210209204225037](.\images\image-20210209204225037.png)

- `-Xloggc:<file>`

把 GC 日志写入到一个文件中去，而不是打印到标准输出中

![image-20210209204451066](.\images\image-20210209204451066.png)

#### 其他参数

- -XX:+TraceClassLoading

监控类的加载

- -XX:+PrintGCApplicationStoppedTime

打印 GC 时线程的停顿时间

![image-20210209204853550](.\images\image-20210209204853550.png)

- -XX:+PrintGCApplicationConcurrentTime

垃圾收集之前打印出应用未中断的执行时间

- -XX:+PrintReferenceGC

记录回收了多少种不同引用类型的引用

- -XX:+PrintTenuringDistribution

让 JVM 在每次 MinorGC 后打印出当前使用的 Survivor 中对象的年龄分布

- -XX:+UseGCLogFileRotation

启用 GC 日志文件的自动转储

- -XX:NumberOfGClogFiles=1

GC 日志文件的循环数目

- -XX:GCLogFileSize=1M

控制 GC 日志文件的大小

### 其他参数

- -XX:+DisableExplicitGC 禁止 hotspot 执行 System.gc0，默认禁用

- `-XX:ReservedCodeCacheSize=<n>[g|m|k]、-XX:InitialCodeCacheSize=<n>[g|m|k]` 指定代码缓存的大小

- -XX:+UseCodeCacheFlushing 使用该参数让 jvm 放弃一些被编译的代码，避免代码缓存被占满时 JVM 切换到 interpreted-only 的情况

- -XX:+DoEscapeAnalysis 开启逃逸分析

- -XX:+UseBiasedLocking 开启偏向锁

- -XX:+UseLargePages 开启使用大页面

- -XX:+UseTLAB 使用 TLAB，默认打开

- -XX:+PrintTLAB 打印 TLAB 的使用情况

- -XX:TLABSize 设置 TLAB 大小

## 4. 通过 Java 代码获取 JVM 参数

Java 提供了 java.lang.management 包用于监视和管理 Java 虚拟机和 Java 运行时中的其他组件，它允许本地和远程监控和管理运行的 Java 虚拟机。其中 ManagementFactory 这个类还是挺常用的另外还有 Runtime 类也可以获取一些内存、CPU 核数等相关的数据。

通过这些 api 可以监控我们的应用服务器的堆内存使用情况，设置一些阈值进行报警等处理。

```java
/**
 * 监控我们的应用服务器的堆内存使用情况，设置一些阈值进行报警等处理
 *
 * @author shkstart
 * @create 15:23
 */
public class MemoryMonitor {
    public static void main(String[] args) {
        MemoryMXBean memorymbean = ManagementFactory.getMemoryMXBean();
        MemoryUsage usage = memorymbean.getHeapMemoryUsage();
        System.out.println("INIT HEAP: " + usage.getInit() / 1024 / 1024 + "m");
        System.out.println("MAX HEAP: " + usage.getMax() / 1024 / 1024 + "m");
        System.out.println("USE HEAP: " + usage.getUsed() / 1024 / 1024 + "m");
        System.out.println("\nFull Information:");
        System.out.println("Heap Memory Usage: " + memorymbean.getHeapMemoryUsage());
        System.out.println("Non-Heap Memory Usage: " + memorymbean.getNonHeapMemoryUsage());

        System.out.println("=======================通过java来获取相关系统状态============================ ");
        System.out.println("当前堆内存大小totalMemory " + (int) Runtime.getRuntime().totalMemory() / 1024 / 1024 + "m");// 当前堆内存大小
        System.out.println("空闲堆内存大小freeMemory " + (int) Runtime.getRuntime().freeMemory() / 1024 / 1024 + "m");// 空闲堆内存大小
        System.out.println("最大可用总堆内存maxMemory " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "m");// 最大可用总堆内存大小

    }
}


// 结果
INIT HEAP: 222m
MAX HEAP: 3157m
USE HEAP: 4m

Full Information:
Heap Memory Usage: init = 232783872(227328K) used = 4698368(4588K) committed = 223346688(218112K) max = 3310878720(3233280K)
Non-Heap Memory Usage: init = 2555904(2496K) used = 6595712(6441K) committed = 8454144(8256K) max = -1(-1K)
=======================通过java来获取相关系统状态============================
当前堆内存大小totalMemory 213m
空闲堆内存大小freeMemory 208m
最大可用总堆内存maxMemory 3157m

```

### 上篇中通过 Runtime 获取过

```java

public class HeapSpaceInitial {
    public static void main(String[] args) {
        //返回Java虚拟机中的堆内存总量
        long initialMemory = Runtime.getRuntime().totalMemory() / 1024 / 1024;//返回Java虚拟机试图使用的最大堆内存量
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
        System.out.println("-xms : " + initialMemory + "M");
        System.out.println("-Xmx : " + maxMemory + "M");
        System.out.println("系统内存大小为: " + maxMemory * 4.0 / 1024 + "G");
        System.out.println("系统内存大小为: " + initialMemory * 64.0 / 1024 + "G");
    }
}
```
