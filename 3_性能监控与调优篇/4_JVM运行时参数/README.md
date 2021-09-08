# 第四章：JVM运行时参数

## 1. JVM参数选项类型

### 类型一:标准参数选项

#### 特点

- 比较稳定,后续版本基本不会变化

- 以-开头

#### 各种选项

- 运行java或者java -help可以看到所有的标准选项
  ![image-20210209181356358](.\images\image-20210209181356358.png)

#### 补充内容:-server 与-client

Hotspot JVM有两种模式，分别是serer和client，分别通过-server和-client模式设置

1．在32位windows系统上，默认使用Client类型的JVM。要想使用Server模式，则机器配置至少有2个以上的CPU和2G以上的物理内存。client模式适用于对内存要求较小的桌面应用程序，默认使用Seria1串行垃圾收集器

2．64位机器上只支持server模式的JVM，适用于需要大内存的应用程序，默认使用并行垃圾收集器

关于server和client的官网介绍为: https://docs.oracle.com/javase/8/docs/technotes/guides/vm/server-class.html

### 类型二:-X参数选项


#### 特点

- 非标准化参数

- 功能还是比较稳定的。但官方说后续版本可能会变更

- 以-x开头

#### 各种选项

运行java -X命令可以看到所有的X选项

![image-20210209181751666](.\images\image-20210209181751666.png)

#### JVM的IT编译模式相关的选项

- -Xint   禁用JIT，所有字节码都被解释执行，这个模式的速度最慢的

- -Xcomp 所有字节码第一次使用就都被编译成本地代码，然后再执行

- -xmixed 混合模式，默认模式，让川T根据程序运行的情况，有选择地将某些代码编译成本地代码

  

#### 特别地

**-Xmx -Xms -Xss属于XX参数?**



- **`-Xms<size>`**

设置初始Java堆大小，等价于-XX:InitialHeapsize

- **`-Xmx<size>`**

设置最大Jawa堆大小，等价于-XX:MaxHeapSize

- **`-Xss<size>`**

设置Java线程堆栈大小，-XX:ThreadStackSize

### 类型三:-XX参数选项



#### 特点

- 非标准化参数

- 使用的最多的参数类型

- 类选项属于实验性，不稳定

- 以-XX开头


#### 作用

- 用于开发和调试vM

#### 分类

**Boolean类型格式**

- `-XX:+<option>`表示启用option属性

- ` -XX:-<option>`表示禁用option 属性

- 举例

```shell
-XX:+UseParalle1GC          # 选择垃圾收集器为并行收集器
-XX:+UseG1GC                 # 表示启用G1收集器
-XX:+UseAdaptiveSizePolicy  # 自动选择年轻代区大小和相应的Survivor区比例
```

- 说明:因为有的指令默认是开启的，所以可以便用-关闭

**非Boolean类型格式(key-value类型)**

- 子类型1: 数值型格式`-XX:<option> = <number>`

```shell
number表示数值，number可以带上单位，比如: 'm'、'M'表示兆，'k'、'K'表示Kb,'g'、'G'表示g（例如32k跟32768是一样的效果)

例如:

-XX:NewSize=1024m   # 表示设置新生代初始大小为1024兆
-XX:MaxGCPauseMillis=500  # 表示设置Gc停顿时间:500毫秒
-XX:GCTimeRatio=19  # 表示设置吞吐量
-XX:NewRatio=2   # 表示新生代与老年代的比例

```

- 子类型2: 非数值型格式`-XX:<name> = <string>`

```shell
例如:
-XX:HeapDumpPath=/usr/local/heapdump.hprof   # 用来指定heap转存文件的存储路径
```



#### 特别地

**-XX:+PrintFlagsFinal**

- 输出所有参数的名称和默认值

- 默认不包括Diagnostic和Experimental的参数

- 可以配合-XX:+UnlockDiagnosticvMOptions和-XXUnlockExperimentalVMOptions使用

![image-20210209183730605](.\images\image-20210209183730605.png)



## 2. 添加VM参数选项

### Eclipse

![image-20210209184123218](.\images\image-20210209184123218.png)

### IDEA

![image-20210209184156421](.\images\image-20210209184156421.png)

### 运行jar包

```shell
java -Xms50m -Xmx50m -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -jar demo.jar
```



### 通过Tomcat运行war包

```shell
#Linux系统下可以在tomcat /bin /catalina.sh中添加类似如下配置;
set "JAVA_OPTS=-Xms512M -Xmx1024M"

#windows系统下在catalina.bat中添加类似如下配置:
set "JAVA_OPTS=-Xms512M -Xmx1024M"
```



### 程序运行过程中

```shell

使用jinfo -flag <name> = <value> <pid>设置非Boolean类型参数

使用jinfo -flag [+/-]<name> <pid>设置Boolean类型参数

并非所有参数都支持动态修改。参数只有被标记为manageable的flag可以被实时修改。其实，这个修改能力是极其有限的。

#可以查看被标记为manageable的参数

java -XX:+PrintFlagsFinal -version | grep manageable

```

![image-20210209185226996](.\images\image-20210209185226996.png)



## 3. 常用的JVM参数选项

### 打印设置的XX选项及值



- -XX:+PrintCommandLineFlags

可以让在程序运行前打印出用户手动设置或者JVM自动设置的XX选项

![image-20210209191529106](.\images\image-20210209191529106.png)

- -XX:+PrintFlagslnitial

表示打印出所有XX选项的默认值

![image-20210209185755414](.\images\image-20210209185755414.png)

- -XX:+PrintFlagsFinal

表示打印出XX选项在运行程序时生效的值

- -XX:+PrintVMOptions

打印VM的参故



### 堆、栈、方法区等内存大小设置



#### 栈

- -Xss128k

设置每个线程的栈大小为128k

等价于-XX:ThreadStackSize=128k

#### 堆内存

- -Xms3550m

等价于-xX:InitialHeapsize，设置JVM初始堆内存为355OM

- -Xmx3550m

等价于-XX:MaxHleapSize，设置JVM最大堆内存为3550M

- -Xmn2g

设置年轻代大小为2G,官方推荐配置为整个堆大小的3/8

- -XX:NewSize=1024m

设置年轻代初始值为1024M

- -XX:MaxNewSize=1024m

设置年轻代最大值为1024M

- -XX:SurvivorRatio=8

设置年轻代中Eden区与一个Survivor区的比值,默认为8

- -XX:+UseAdaptiveSizePolicy

自动选挥各区大小比例(Eden区与一个Survivor区的比值,默认为8，因为开启这个会自动分配，有可能是6)

- -XX:NewRatio=4

设置老年代与年轻代(包括1个Eden和2个Survivor区）的比值（默认是2）

- -XX:PretenureSizeThreadshold=1024

设置让大于此阈值的对象直接分配在老年代，单位为字节，只对Seriall、ParNews收集器有效

- -XX:MaxTenuringThreshold=15

默认值为15，新生代每次MinorGC后，还存活的对象年龄+1，当对象的年龄大于设置的这个值时就进入老年代

-XX+PrintTenuringDistribution

让JVM在每次MinorGC后打印出当前使用的Survivor中对象的年龄分布

-XX:TargetSurvivorRatio

表示MinorGC结束后Survivor区域中占用空间的期望比例



#### 方法区 

##### 永久代



- -XX:PermSize=256m

设置永久代初始值为256M

- -XX:MaxiPermSize=256m

设置永久代最大值为256M

##### 元空间

- -XX:MetaspaceSize

初始空间大小

- -XX:MaxMetaspacesize

最大空间，默认没有限制’

- -XX:-UseCompressedOops

压缩对象指针

- -XX:-UseCompressedClassPointers

压缩类指针

- -XX:CompressedclassSpaceSize

设置Klass Metaspace的大小，默认1G

##### 直接内存

- -XX:MaxDirectMemorySize 

指定DirectMemory容量，若未指定，则默认与Java堆最大值一样



### OutofMemory相关的选项

- -XX:+HeapDumpOnOutOfMemoryError

表示在内存出现OOM的时候，把Heap转存(Dump)到文件以便后续分析

- -XX:+HeapDumpBeforeFullGc

表示在出现FullGC之前，生成Heap转储文件

- `-XX:HeapDumpPath= <path>`

指定heap转存文件的存储路径(默认是当前路径)

- -XX:OnOutOfMemoryError

指定一个可行性程序或者脚本的路径，当发生OOM的时候，去执行这个脚本

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



### 垃圾收集器相关选项

**7款经典收集器与垃圾分代之间的关系**

![image-20210209194937256](.\images\image-20210209194937256.png)

**垃圾收集器的组合关系**

![image-20210209194919283](.\images\image-20210209194919283.png)

#### 

#### 查看默认垃圾收集器

- -XX:+PrintCommandLineFlags:查看命令行相关参数（包含使用的垃圾收集器)
- 使用命令行指令:jinfo - flag     相关垃圾回收器参数进程ID

#### Serial回收器  

Seria收集器作为HotSpot中Client模式下的默认新生代垃圾收集器。Serial old是运行在Client模式下默认的老年代的垃圾回收器。

- XX:+UseSerialGC

指定年轻代和老年代都使用串行收集器。等价于新生代用Serial Gc，且老年代用Serial old GC。可以获得最高的单线程收集效率。

#### ParNew回收器

- -XX :+UseParNewGC

手动指定使用ParNew收集器执行内存回收任务。它表示年轻代使用并行收集器，不影响老年代。

- -XX:Paralle1GCThreads=N

限制线程数量，默认开启和CPU数据相同的线程数。

#### Parallel回收器

- -XX:+UsePar1le1Gc手动指定年轻代使用Parallel并行收集器执行内存回收任务。

- -XX:+UseParalle101dGC手动指定老年代都是使用并行回收收集器。
  - 分别适用于新生代和老年代。默认jdk8是开启的。
  - 上面两个参数，默认开启一个，另一个也会被开启。**（互相激活）**

- -XX:ParallelGCThreads 设置年轻代并行收集器的线程数。一般地，最好与CPU数量相等，以避免过多的线程数影响垃圾收集性能。
  - 在默认情况下，当CPU 数量小于8个，ParallelGCThreads 的值等于CPU 数量。
  - 当CPU数量大于8个，ParallelGCThreads的值等于3+[5*CPU_Count]/8]。

- -XX:MaxGCPauseMillis 设置垃圾收集器最大停顿时间(即STw的时间)。单位是毫秒。
  - 为了尽可能地把停顿时间控制在MNaxGCPauseMills以内，收集器在工作时会调整Java堆大小或者其他一些参数。
  - 对于用户来讲，停顿时间越短体验越好。但是在服务器端，我们注重高并发，整体的吞吐量。所以服务器端适合Parallel，进行控制。
  - 该参数使用需谨慎。

- -XX:GCTimeRatio垃圾收集时间占总时间的比例(= 1/ (N + 1))。用于衡量吞吐量的大小。
  - 取值范围（0,100）。默认值99，也就是垃圾回收时间不超过1%。
  - 与前一个-XX:MaxGCPauseMillis参数有一定矛盾性。暂停时间越长，Radio参数就容易超过设定的比例。

- -XX:+UseAdaptivesizePolicy 设置Parallel Scavenge收集器具有**自适应调节策略**



#### CMS回收器

- -XX:+UseConcMarkSweepGC手动指定使用CMS 收集器执行内存回收任务
  - 开启该参数后会自动将-XX:+UseParNewGC打开。即:ParNew(Young区用)+CMS(Old区用)+Serial old的组合。

- -XX:CMSInitiatingOccupanyFraction 设置堆内存使用率的阈值，一旦达到该阈值，便开始进行回收。
  - JDK5及以前版本的默认值为68,即当老年代的空间使用率达到68%时，会执行一次CMS回收。JDK6及以上版本默认值为92%
  - 如果内存增长缓慢，则可以设置一个稍大的值，大的阈值可以有效降低CNS的触发频率，减少老年代回收的次数可以较为明显地改善应用程序性能。反之，如果应用程序内存使用率增长很快，则应该降低这个阈值，以避免频繁触发老年代串行收集器。因此通过该选项便可以有效降低Full GC的执行次数。

- -XX:+UseCMSCompactAtFullCollection用于指定在执行完Full GC后对内存空间进行压缩整理，以此避免内存碎片的产生。不过由于内存压缩整理过程无法并发执行，所带来的问题就是停顿时间变得更长了。

- -XX:CMSFullGCsBeforeCompaction 设置在执行多少次Full GC后对内存空间进行压缩整理。

- -XX: ParallelCMSThreads设置CMS的线程数量。

- CMS 默认启动的线程数是(ParallelGCThreads+3)/4，ParallelGCThreads是年轻代并行收集器的线程数。当CPU 资源比较紧张时，受到CMS收集器线程的影响，应用程序的性能在



**补充参数**

- -XX:ConcGCThreads:设置并发垃圾收集的线程数，默认该值是基于ParallelGCThreads计算出来的;

- -XX:+UseCMSInitiatingOccupancyOnly:是否动态可调，用这个参数可以使CNS一直按CMSInitiatingoccupancyFraction设定的值启动

- -XX:+CMSScavengeBeforeRemark:强制hotspot虚拟机在cms remark阶段之前做一次minor gc，用于提高remark阶段的速度;

- -XX:+CMSClassUnloadingEnable:如果有的话，启用回收Perm 区(JDK8之前）

- -XX:+CMSParallelInitialEnabled:用于开启CNS initial-mark阶段采用多线程的方式进行标记，用于提高标记速度，在Java8开始已经默认开启;

- -XX:+CMSParallelRemarkEnabled:用户开启CNS remark阶段采用多线程的方式进行重新标记，默认开启;

- -XX:+ExplicitGCInvokesConcurrent 、-XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses这两个参数用户指定hotspot虚拟在执行System.gc()时使用CMS周期;

-  -XX:+CMSPrecleaningEnabled:指定CMS是否需要进行Pre cleaning这个阶段



**特别说明**

- JDK9新特性:CMS被标记为Deprecate了(EP291)
  - 如果对JDK 9及以上版本的HotSpot虚拟机使用参数-XX:+UseConcMarkSweepGC来开启CMS收集器的话，用户会收到一个警告信息，提示CMS未来将会被废弃。

- JDK14新特性:删除CMS垃圾回收器(EP363)
  - 移除了CMS垃圾收集器，如果在JDK14中使用-XX:+UseConcMarkSweepGC的话，JVM不会报错，只是给出一个warning信息，但是不会exit。JVM会自动回退以默认GC方式启动JVM



openJDK 64-Bit Server VM warning: Ignoring option UseConcMarkSweepGC;support was removed in 14.0and the VM will continue execution using the default collector.







#### G1回收器

- -XX:+UseG1GC

手动指定使用G1收集器执行内存回收任务。

- -XX:G1HeapRegionSize

设置每个Region的大小。值是2的幂，范围是1MB到32MB之间，目标是根据最小的Java堆大小划分出约2048个区域。默认是堆内存的1/2000。

- -XX:MaxGCPauseMillis

设置期望达到的最大GC停顿时间指标(JVM会尽力实现，但不保证达到)。默认值是200ms

- -XX: ParallelGCThread

设置STW时GC线程数的值。最多设置为8

- -XX:ConcGCThreads

设置并发标记的线程数。将n设置为并行垃圾回收线程数(ParallelGCThreads)的1/4左右。

- -XX:InitiatingHeapOccupancyPercent

设置触发并发GC周期的Java堆占用率阈值。超过此值，就触发Gc。默认值是45。

- -XX:G1NewSizePercent、-XX:G1MaxNewSizePercent

新生代占用整个堆内存的最小百分比（默认5%）、最大百分比（默认60%）

- -XX:G1ReservePercent=10

保留内存区域，防止 to space ( Survivor中的to区）溢出



**Mixed Gc调优参数**

注意:G1收集器主要涉及到Mixed Gc，Mixed GC会回收young区和部分old区。

G1关于Mixed GC调优常用参数:

- -XX:InitiatingHeapOccupancyPercent:设置堆占用率的百分比（0到100）达到这个数值的时候触发global concurrent marking（全局并发标记），默认为45%。值为0表示间断进行全局并发标记。

- -XX:G1MixedGCLiveThresholdPercent:设置old区的region被回收时候的对象占比，默认占用率为85%。只有old区的region中存活的对象占用达到了这个百分比，才会在Mixed GC中被回收。

- -XX:G1HeapastePercent:在global concurrent marking（全局并发标记结束之后，可以知道所有的区有多少空间要被回收，在每次young GC之后和再次发生Mixed GC之前，会检查垃圾占比是否达到此参数，只有达到了，下次才会发生Mixed GC。

-  -XX:G1MixedGCCountTarget:一次global concurrent marking(全局并发标记）之后，最多执行Mixed GC的次数，默认是8。

-  -XX:G1OldCSetRegionThresholdPercent:设置Mixed GC收集周期中要收集的old region数的上限。默认值是Java堆的10%



#### 怎么选择垃圾回收器

- 优先调整堆的大小让VM自适应完成。如果内存小于100M，使用串行收集器

- 如果是单核、单机程序，并且没有停顿时间的要求，串行收集器

- 如果是多CPU、需要高吞吐量、允许停顿时间超过1秒，选择并行或者JVM自己选择

- 如果是多CPU、追求低停顿时间，需快速响应（比如延迟不能超过1秒，如互联网应用），使用并发收集器。官方推荐G1，性能高。现在互联网的项目，基本都是使用G1。

**特别说明:**

1．没有最好的收集器，更没有万能的收集;

2．调优永远是针对特定场景、特定需求，不存在一劳永逸的收集器l



### GC日志相关选项

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

输出gc日志信息，默认输出到标准输出，可以独立使用

- -XX+PrintGC

等同于-verbose:gc，表示打开简化的GC日志，可以独立使用

![image-20210209203024263](.\images\image-20210209203024263.png)

- -XX:+PrintGCDetails

在发生垃圾回收时打印内存回收详细的日志，并在进程退出时输出当前内存各区域分配情况，可以独立使用

![image-20210209202907031](.\images\image-20210209202907031.png)

- -XX:+PrintGCTimeStamps

输出GC发生时的时间戳，不可以独立使用，需要配合-XX:+PrintGCDetails使用

![image-20210209203145923](.\images\image-20210209203145923.png)

- -XX:+PrintGCDateStamps

输出GC发生时的时间戳(以日期的形式，如2013-05-04T21:53:59.234+0800)，不可以独立使用，需要配合-XX:+PrintGCDetails使用

![image-20210209203236836](.\images\image-20210209203236836.png)

- -XX:+PrintHeapAtGC

每一次GC前和GC后，都打印堆信息，可以独立使用

![image-20210209203331316](.\images\image-20210209203331316.png)

![image-20210209204225037](.\images\image-20210209204225037.png)

- `-Xloggc:<file>`

把GC日志写入到一个文件中去，而不是打印到标准输出中

![image-20210209204451066](.\images\image-20210209204451066.png)



#### 其他参数

- -XX:+TraceClassLoading

监控类的加载

- -XX:+PrintGCApplicationStoppedTime

打印GC时线程的停顿时间

![image-20210209204853550](.\images\image-20210209204853550.png)

- -XX:+PrintGCApplicationConcurrentTime

垃圾收集之前打印出应用未中断的执行时间

- -XX:+PrintReferenceGC

记录回收了多少种不同引用类型的引用

- -XX:+PrintTenuringDistribution

让JVM在每次MinorGC后打印出当前使用的Survivor中对象的年龄分布

- -XX:+UseGCLogFileRotation

启用GC日志文件的自动转储

- -XX:NumberOfGClogFiles=1

GC日志文件的循环数目

- -XX:GCLogFileSize=1M

控制GC日志文件的大小



### 其他参数



- -XX:+DisableExplicitGC  禁止hotspot执行System.gc0，默认禁用

- `-XX:ReservedCodeCacheSize=<n>[g|m|k]、-XX:InitialCodeCacheSize=<n>[g|m|k]`  指定代码缓存的大小

- -XX:+UseCodeCacheFlushing  使用该参数让jvm放弃一些被编译的代码，避免代码缓存被占满时JVM切换到interpreted-only的情况

- -XX:+DoEscapeAnalysis  开启逃逸分析

- -XX:+UseBiasedLocking  开启偏向锁

- -XX:+UseLargePages  开启使用大页面

- -XX:+UseTLAB  使用TLAB，默认打开

- -XX:+PrintTLAB  打印TLAB的使用情况

- -XX:TLABSize  设置TLAB大小 



## 4. 通过Java代码获取JVM参数



Java提供了java.lang.management包用于监视和管理Java虚拟机和Java运行时中的其他组件，它允许本地和远程监控和管理运行的Java虚拟机。其中ManagementFactory这个类还是挺常用的另外还有Runtime类也可以获取一些内存、CPU核数等相关的数据。

通过这些api可以监控我们的应用服务器的堆内存使用情况，设置一些阈值进行报警等处理。

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





### 上篇中通过Runtime获取过

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

