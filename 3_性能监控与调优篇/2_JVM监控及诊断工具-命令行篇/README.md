---
title: 23-JVM监控及诊断工具-命令行篇
tags:
notebook: 宋红康-JVM
---

### 1 概述

性能诊断是软件工程师在日常工作中需要经常面对和解决的问题，在用户体验至上的今天，解决好应用的性能问题能带来非常大的收益。

Java 作为最流行的编程语言之一，其应用性能诊断一直受到业界广泛关注。可能造成 Java 应用出现性能问题的因素非常多，例如线程控制、磁盘读写、数据库访问、网络 I/O、垃圾收集等。想要定位这些问题，一款优秀的性能诊断工具必不可少。

#### 1.1 简单命令行工具

在我们刚接触 java 学习的时候，大家肯定最先了解的两个命令就是 javac，java，那么除此之外，还有没有其他的命令可以供我们使用呢?我们进入到安装 jdk 的 bin 目录，发现还有一系列辅助工具。这些辅助工具用来获取目标 JVM 不同方面、不同层次的信息，帮助开发人员很好地解决 Java 应用程序的一些疑难杂症。

**Windows 系统下**

<img src="https://tvax1.sinaimg.cn/large/006JhGcily1gytbuzfw2kj30k50oin8o.jpg" alt="image-20210206182506036" width="75%">

**源码**

https://hg.openjdk.java.net/jdk/jdk11/file/1ddf9a99e4ad/src/jdk.jcmd/share/ciassesisun/tools

### 2 jps:查看正在运行的 Java 进程

#### 2.1 基本情况

jps(Java Process Status) :

显示指定系统内所有的 HotSpot 虚拟机进程(查看虚拟机进程信息)，可用于查询正在运行的虚拟机进程。

说明:对于本地虚拟机进程来说，进程的本地虚拟机 ID 与操作系统的进程 ID 是一致的，是唯一的。

#### 2.2 测试

```java
public class ScannerTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String info = scanner.next();
    }
}
```

#### 2.3 基本语法

它的基本使用语法为:

jps [options] [hostid]

我们还可以通过追加参数，来打印额外的信息。

#### options 参数

-q: 仅仅显示 LVMID (local virtual machine id)，即本地虚拟机唯一 id。不显示主类的名称等

-l: 输出应用程序主类的全类名或如果进程执行的是 jar 包，则输出 jar 完整路径

-m: 输出虚拟机进程启动时传递给主类 main()的参数

-v: 列出虚拟机进程启动时的 JVM 参数。比如: -Xms20m -Xmx50m 是启动程序指定的 jvm 参数。

说明:以上参数可以综合使用。

<img src="https://tva4.sinaimg.cn/large/006JhGcily1gytc13qf5lj30xe0hiapc.jpg" alt="image-20210206204943078" width="80%">

<img src="https://tvax3.sinaimg.cn/large/006JhGcily1gytc1nycsoj30wy0bnx3p.jpg" alt="image-20210206205050861" width="80%">

补充:

如果某 Java 进程关闭了默认开启的 UsePerfData 参数(即使用参数-XX:-UsePerfData)，那么 jps 命令（以及下面介绍的 jstat）将无法探知该 java 进程。

#### hostid 参数

RMI 注册表中注册的主机名。

如果想要远程监控主机上的 java 程序，需要安装 jstatd。

对于具有更严格的安全实践的网络场所而言，可能使用一个自定义的策略文件来显示对特定的可信主机或网络的访问，尽管这种技术容易受到 IP 地址欺诈攻击。

如果安全问题无法使用一个定制的策略文件来处理，那么最安全的操作是不运行 jstatd 服务器，而是在本地使用 jstat 和 jps 工具。

### 3 jstat :查看 JVM 统计信息

#### 3.1 基本情况

jstat(VM Statistics Monitoring Tool):用于监视虚拟机各种运行状态信息的命令行工具。它可以显示本地或者远程虚拟机进程中的`类装载、内存、垃圾收集、JIT编译等运行数据`。

在没有 GUI 图形界面，只提供了纯文本控制台环境的服务器上，它将是运行期定位虚拟机性能问题的首选工具。`常用于检测垃圾回收问题以及内存泄漏问题`。

官方文档:

https://docs.oracle.com/javase/8/docs/technotes/tools/unix/jstat.html

#### 3.2 基本语法

它的基本使用语法为: `jstat -<option>[-t][-h<lines>] <vmid> [<interval> [<count>]]`

查看命令相关参数: jstat -h 或 jstat -help

#### option 参数

选项 option 可以由以下值构成。

- 类装载相关的:
  - -class:显示 ClassLoader 的相关信息:类的装载、卸载数量、总空间、类装载所消耗的时间等

<img src="https://tvax1.sinaimg.cn/large/006JhGcily1gytcensk7wj30cj02f3za.jpg" alt="image-20210207092558378" width="60%">

- 垃圾回收相关的:(最多也是最重要的，下面子目录讲解)

  - -gc:显示与 GC 相关的堆信息。包括 Eden 区、两个 Survivor 区、老年代、永久代等的容量、已用空间、GC 时间合计等信息。

  - -gccapacity:显示内容与-gc 基本相同，但输出主要关注 Java 堆各个区域使用到的最大、最小空间。

  - -gcutil:显示内容与-gc 基本相同，但输出主要关注已使用空间占总空间的百分比。

  - -gccause:与-gcutil 功能一样，但是会额外输出导致最后一次或当前正在发生的 GC 产生的原因。

  - gcnew:显示新生代 Gc 状况

  - -gcnewcapacity:显示内容与-gcnew 基本相同，输出主要关注使用到的最大、最小空间

  - -geold:显示老年代 GC 状况
  - -gcoldcapacity:显示内容与-gcold 基木相同,输出主要关注使用到的最大、最小空间

- JIT 相关的:
  - -compiler:显示 JIT 编译器编译过的方法、耗时等信息.
  - -printcompilation:输出已经被 JIT 编译的方法

```shell
C:\Users\zzxx>jstat -compiler 20864
Compiled Failed Invalid   Time   FailedType FailedMethod
      98      0       0     0.03          0

Compiled  Size  Type Method
      98    138    1 java/lang/StringBuffer append
```

##### -gc

**新生代相关**

```
S0C是第—个幸存者区的大小(字节)

S1C是第二个幸存者区的大小(字节)

S0U是第一个幸存者区已使用的大小(字节)

S1U是第二个幸存者区已使用的大小(字节)

EC是Eden空间的大小(字节)

EU是Eden空间已使用的大小(字节)
```

**老年代相关**

```
OC是老年代的大小(字节)

OU是老年代已使用的大小(字节)
```

**方法区(元空间)相关**

```
MC是方法区的大小

MU是方法区已使用的大小

CCSC是压缩类空间的大小

CCSU是压缩类空间已使用的大小
```

**其它**

```
YGC是指从应用程序启动到采样时young gc次数

YGCT从应用程序启动到采样时young gc消耗时间（秒）

FGC是指从应用程序启动到采样时full gc次数

FGCT是指从应用程序启动到采样时full gc消耗时间（秒）

GCT是指从应用程序启动到采样时gc的总时间
```

<img src="https://tva1.sinaimg.cn/large/006JhGcily1gytcn27kstj312702ewge.jpg" alt="image-20210207111655549" width="85%">

```java
/**
 * -Xms60m -Xmx60m -XX:SurvivorRatio=8
 */
public class GCTest {
    public static void main(String[] args) {
        ArrayList<byte[]> list = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            byte[] arr = new byte[1024 * 100];//100KB
            list.add(arr);
            try {
                Thread.sleep(120);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

**-gc:显示与 GC 相关的堆信息。包括 Eden 区、两个 Survivor 区、老年代、永久代等的容量、已用空间、GC 时间合计等信息。**

<img src="https://tva2.sinaimg.cn/large/006JhGcily1gytcv56vn3j312f09otpk.jpg" alt="image-20210207113030953" width="85%">

**-gcutil:显示内容与-gc 基本相同，但输出主要关注已使用空间占总空间的百分比。**

<img src="https://tvax1.sinaimg.cn/large/006JhGcily1gytcvst361j30n20brtlu.jpg" alt="image-20210207113924069" width="85%">

**-gccause:与-gcutil 功能一样，但是会额外输出导致最后一次或当前正在发生的 GC 产生的原因。**

Allocation Failure 新生代空间不足

<img src="https://tva2.sinaimg.cn/large/006JhGcily1gytd35m5k2j30um072k0k.jpg" alt="image-20210207114130114" width="85%">

#### interval 参数

用于指定输出统计数据的周期。单位为毫秒。即:查询间隔

**每隔 1000 毫秒打印一次**

<img src="https://tvax4.sinaimg.cn/large/006JhGcily1gytddidjrvj30do07m0wb.jpg" alt="image-20210207092704777" width="60%">

#### count 参教

用于指定查询的总次数

<img src="https://tvax2.sinaimg.cn/large/006JhGcily1gytddxo757j30el041q4j.jpg" alt="image-20210207092813974" width="60%">

#### -t 参数

可以在输出信息前加上一个 Timestamp 列，显示程序的运行时间。单位:秒

```shell
# 程序开始到现在
C:\Users\zzxx>jstat -class -t 20864
Timestamp       Loaded  Bytes  Unloaded  Bytes     Time
          330.9    687  1378.3        0     0.0       0.09

C:\Users\zzxx>jstat -class -t 20864
Timestamp       Loaded  Bytes  Unloaded  Bytes     Time
          338.3    687  1378.3        0     0.0       0.09

C:\Users\zzxx>jstat -class -t 20864 1000 4
Timestamp       Loaded  Bytes  Unloaded  Bytes     Time
         5642.6    687  1378.3        0     0.0       0.09
         5643.7    687  1378.3        0     0.0       0.09
         5644.7    687  1378.3        0     0.0       0.09
         5645.6    687  1378.3        0     0.0       0.09
```

**经验**

- `我们可以比较 Java 进程的启动时间以及总 GC 时间（GCT 列)，或者两次测量的间隔时间以及总 GC 时间的增量，来得出 GC 时间占运行时间的比例`。
- `如果该比例超过 20%，则说明目前堆的压力较大;如果该比例超过 90%，则说明堆里几乎没有可用空间，随时都可能抛出 OOM 异常`。

#### -h 参数

可以在周期性数据输出时，治出多少行数据后输出一个表头信息

```shell
# 每输出3行 会再次输出一个表头
C:\Users\zzxx>jstat -class -t -h3 20864 1000 10
Timestamp       Loaded  Bytes  Unloaded  Bytes     Time
         5812.0    687  1378.3        0     0.0       0.09
         5813.1    687  1378.3        0     0.0       0.09
         5814.1    687  1378.3        0     0.0       0.09
Timestamp       Loaded  Bytes  Unloaded  Bytes     Time
         5815.1    687  1378.3        0     0.0       0.09
         5816.1    687  1378.3        0     0.0       0.09
         5817.1    687  1378.3        0     0.0       0.09
Timestamp       Loaded  Bytes  Unloaded  Bytes     Time
         5818.1    687  1378.3        0     0.0       0.09
         5819.1    687  1378.3        0     0.0       0.09
         5820.1    687  1378.3        0     0.0       0.09
Timestamp       Loaded  Bytes  Unloaded  Bytes     Time
         5821.1    687  1378.3        0     0.0       0.09
```

### 补充

**jstat 还可以用来判断是否出现内存泄漏。**

**第 1 步:**

在长时间运行的 Java 程序中，我们可以运行 jstat 命令连续获取多行性能数据，并取这几行数据中 OU 列（即已占用的老年代内存）的最小值。

**第 2 步:**

然后，我们每隔一段较长的时间重复一次上述操作，来获得多组 OU 最小值。如果这些值呈上涨趋势，则说明该 Java 程序的老年代内存已使用量在不断上涨，这意味着无法回收的对象在不断增加，因此很有可能存在内存泄漏。

### 4 jinfo:实时查看和修改 JVM 配置参数

#### 4.1 基本情况

jinfo(Configuration Info for Java)

查看虚拟机配置参数信息，也可用于调整虚拟机的配置参数。

在很多情况下，Java 应用程序不会指定所有的 Java 虚拟机参数。而此时，开发人员可能不知道某一个具体的 Java 虚拟机参数的默认值。在这种情况下，可能需要通过查找文档获取某个参数的默认值。这个查找过程可能是非常艰难的。但有了 jinfo 工具，开发人员可以很方便地找到 Java 虚拟机参数的当前值。

<img src="https://tvax4.sinaimg.cn/large/006JhGcily1gytdzlau5zj30ri09ogrr.jpg" alt="image-20210207120020564" width="75%">

**官方帮助文档:|**
https://docs.oracle.com/en/java/javase/11/tools/jinfo.html

#### 4.2 基本语法

它的基本使用语法为:jinfo [options] pid

**说明:java 进程 ID 必须要加上**

| 选项             | 选项说明                                                                 |
| ---------------- | ------------------------------------------------------------------------ |
| no option        | 输出全部的参数和系统属性                                                 |
| -flag name       | 输出对应名称的参数                                                       |
| -flag [+-]name   | 开启或者关闭对应名称的参数只有被标记为 manageable 的参数才可以被动态修改 |
| -flag name=value | 设定对应名称的参数                                                       |
| -flags           | 输出全部的参数                                                           |
| -sysprops        | 输出系统属性                                                             |

#### 4.3 查看

**jinfo -sysprops PID**

可以查看由 System.getProperties()取得的参数(此处还是以前面 ScannerTest 为例)

<img src="https://tva2.sinaimg.cn/large/006JhGcily1gyte1s106wj30ui0matrh.jpg" alt="image-20210207120641890" width="80%">

**jinfo -flags PID**

查看曾经赋过值的一些参数

<img src="https://tva3.sinaimg.cn/large/006JhGcily1gyte3et7mij30xc07eqci.jpg" alt="image-20210207121020513" width="80%">

**jinfo -flag 具体参数 PID**

查看某个 java 进程的具体参数的值

```shell
# -XX:+表示使用
C:\Users\zzxx>jinfo -flag UseParallelGC 35316
-XX:+UseParallelGC

C:\Users\zzxx>jinfo -flag UseSerialGC 35316
-XX:-UseSerialGC
C:\Users\zzxx>jinfo -flag MaxHeapSize 35316
-XX:MaxHeapSize=3724541952
```

#### 4.4 修改

jinfo 不仅可以查看运行时某一个 Java 虚拟机参数的实际取值，甚至可以在运行时修改部分参数，并使之立即生效。

但是，并非所有参数都支持动态修改。参数只有被标记为 manageable 的 flag 可以被实时修改。其实，这个修改能力是极其有限的。

**可以查看被标记为 manageable 的参数**

```
java -XX:+PrintFlagsFinal -version | grep manageable
```

<img src="https://tvax1.sinaimg.cn/large/006JhGcily1gytec92101j30u40cntk9.jpg" alt="image-20210207122509327" width="80%">

**针对 boolean 类型**

jinfo -flag[+l-]具体参数 PID

<img src="https://tvax4.sinaimg.cn/large/006JhGcily1gyteed7aqvj30ki042765.jpg" alt="image-20210207122925481" width="80%">

**针对非 boolean 类型**

jinfo -flag 具体参数=具体参数值 PID

<img src="https://tva2.sinaimg.cn/large/006JhGcily1gytefadtohj30m004dwgo.jpg" alt="image-20210207123032942" width="80%">

#### 4.5 拓展

```
java -XX:+PrintFlagsInitial #查看所有 JVM 参数启动的初始值
```

```
java -XX:+PrintFlagsFinal #查看所有 JVM 参数的最终值
```

```
java -XX:+PrintCommandLineFlags #查看那些已经被用户或者 JVM 设置过的详细的XX参数的名
```

```shell
java -XX:+PrintFlagsInitial -version
java -XX:+PirntFlagsFinal  -version
java -XX:+PrintCommandLineFlags -version
```

### 5 jmap:导出内存映像文件&内存使用情况

#### 5.1 基本情况

jmap(JVM Memory Map):作用一方面是获取 dump 文件（堆转储快照文件，二进制文件），它还可以获取目标 Java 进程的内存相关信息，包括 Java 堆各区域的使用情况、堆中对象的统计信息、类加载信息等。

开发人员可以在控制台中输入命令“jmap -help”查阅 jmap 工具的具体使用方式和一些标准选项配置。

官方帮助文档:

https://docs.oracle.com/en/java/javase/11/tools/jmap.html

#### 5.2 基本语法

它的基本使用语法为:

- `jmap [option] <pid>`
- `jmap[option] <executable <core>`
- `jmap [option] [server_id@]<remote server IP or hostname>`

**其中 option 包括:**

| 选项           | 作用                                                                                    |
| -------------- | --------------------------------------------------------------------------------------- |
| -dump          | 生成 dump 文件                                                                          |
| -heap          | 输出整个堆空间的详细信息，包括 GC 的使用、堆配置信息，以及内存的使用信息等              |
| -histo         | 输出堆空间中对象的统计信息，包括类、实例数量和合计容量                                  |
| -permstat      | 以 ClassLoader 为统计口径输出永久代的内存状态信息                                       |
| -F             | 当虚拟机进程对-dump 选项没有任何响应时，强制执行生成 dump 文件                          |
| -finalizerinfo | 显示在 F-Queue 中等待 Finalizer 线程执行 finalize 方法的对象，仅 linux/solaris 平台有效 |

**说明:这些参数和 linux 下输入显示的命令多少会有不同，包括也受 jdk 版本的影响。**

**-dump**

生成 Java 堆转储快照: dump 文件

特别的: -dump:live 只保存堆中的存活对象(在安全点生成的对象不管)

**-heap**
输出整个堆空间的详细信息，包括 GC 的使用、堆配置信息，以及内存的使用信息等

**-histo**

输出堆中对象的统计信息，包括类．实例数量和合计容量

特别的: -histo:live 只统计堆中的存活对象

**-permstat**

以 ClassLoader 为统计口径输出永久代的内存状态信息

仅 linux/solaris 平台有效

**-finalizerinfo**

显示在 F-Queue 中等待 Finalizer 线程执行 finalize 方法的对象

仅 linux/solaris 平台有效

**-F**

当虚拟机进程对-dump 选项没有任何响应时，可使用此选项强制执行生成 dump 文件

仅 linux/solaris 平台有效

**-h/ -help**

jmap 工具使用的帮助命令

**-j\<flag>**

传递参数给 jmap 启动的 jvm

#### 5.3 使用 1:导出内存映像文件

一般来说，使用 jmap 指令生成 dump 文件的操作算得上是最常用的 jmap 命令之一,将堆中所有存活对象导出至一个文件之中。
Heap Dump 又叫做堆存储文件，指一个 Java 进程在某个时间点的内存快照。Heap Dump 在触发内存快照的时候会保存此刻的信息如下:

- All Objects
  Class,fields, primitive values and references.
- All Classes
  ClassLoader, name, super class,static fields
- Garbage collection Roots
  Objects defined to be reachable by the JVM.
- Thread stacks and Local Variables
  The call-stacks of threads at the moment of the snapshot,and per-frameinformation about local objects

**说明:**

1. 通常在写 Heap Dump 文件前会触发一次 Full Gc，所以 heap dump 文件里保存的都是 FullGC 后留下的对象信息。
2. 由于生成 dump 文件比较耗时，因此大家需要耐心等待，尤其是大内存镜像生成 dump 文件则需要耗费更长的时间来完成。

**手动的方式**

```shell
jmap -dump:format=b,file=<filename.hprof> <pid>

jmap -dump:live,format=b,file=<filename.hprof> <pid>
```

**使用前面 GCTest 测试**

<img src="https://tvax2.sinaimg.cn/large/006JhGcily1gythresg5zj30wi0eudni.jpg" alt="image-20210207133650440" width="80%">

<img src="https://tvax4.sinaimg.cn/large/006JhGcily1gyths43my0j30o204odik.jpg" alt="image-20210207133557214" width="80%">

**自动的方式**

当程序发生 OOM 退出系统时，一些瞬时信息都随着程序的终止而消失，而重现 OOM 问题往往比较困难或者耗时。此时若能在 OOM 时，自动导出 dump 文件就显得非常迫切。这里介绍一种比较常用的取得堆快照文件的方法，即使用:`-XX:+HeapDumpOnOutOfMemoryError`:在程序发生 OOM 时，导出应用程序的当前堆快照。

`-XX:HeapDumpPath`:可以指定堆快照的保存位置。

比如:

-Xms60m -Xmx60m -XX:SurvivorRatio=8 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=D:\5.hprof

```shell
-XX:+HeapDumpOnOutOfMemoryError

-XX:HeapDumpPath=<filename.hprof>
```

#### 5.4 使用 2:显示堆内存相关信息

```shell
# 运行的那一刻，堆空间的占用情况
jmap -heap pid

# 内存中有哪些对象，个数多少，占用空间多少
jmap -histo pid
```

<img src="https://tva2.sinaimg.cn/large/006JhGcily1gythxp6zwkj30jx0ny7g1.jpg" alt="image-20210207134514851" width="70%">

<img src="https://tvax3.sinaimg.cn/large/006JhGcily1gythy3te75j30sb0f77eu.jpg" alt="image-20210207134541331" width="70%">

#### 5.5 使用 3:其它作用

```shell
#仅在Linux中有效

#查看系统的ClassLoader信息
jmap -permstat pid

#查看堆积在finalizer队列中的对象
jmap -finalizerinfo

```

#### 5.6 小结

由于 jmap 将访问堆中的所有对象，为了保证在此过程中不被应用线程干扰，jmap 需要借助安全点机制，让所有线程停留在不改变堆中数据的状态。也就是说，由 jmap 导出的堆快照必定是安全点位置的。这可能导致基于该堆快照的分析结果存在偏差。

举个例子，假设在编译生成的机器码中，某些对象的生命周期在两个安全点之间，那么:live 选项将无法探知到这些对象。

另外，如果某个线程长时间无法跑到安全点，jmap 将一直等下去。而前面讲的 jstat 则不同，垃圾回收器会主动将 jstat 所需要的摘要数据保存至固定位置之中， jstat 只需直接读取即可。

### 6 jhat: JDK 自带堆分析工具

#### 6.1 基本情况

jhat (JVM Heap Analysis Tool ) :

Sun JDK 提供的 jhat 命令与 jmap 命令搭配使用，用于分析 jmap 生成的 heap dump 文件（堆转储快照）。jhat 内置了一个微型的 HTTP/HTML 服务器，生成 dump 文件的分析结果后，用户可以在浏览器中查看分析结果(分析虚拟机转储快照信息)。

使用了 jhat 命令，就启动了一个 http 服务，端口是 7000，即 http://localhost:7000/,就可以在浏览器里分析。

说明: jhat 命令在 JDK9、JDK10 中已经被删除，官方建议用 VisualVM 代替。

#### 6.2 基本语法

**option 参数: -stack false|true**

关闭|打开对象分配调用栈跟踪

**option 参数: -refs false|true**

关闭|打开对象引用跟踪

**option 参数: -port port-number**

设置 jhat HTTP

Server 的端口号，默认 7000

**option 参数: -exclude exclude-file**

执行对象查询时需要排除的数据成员列表文件

**option 参数: -baseline exclude-file**

指定一个基准堆转储

**option 参数: -debug int**

设置 debug 级别

**option 参数: -version**

启动后显示版本信息就退出

**option 参数: -J\<flag>**

传入启动参数，比如-J-Xmx512m

<img src="https://tva2.sinaimg.cn/large/006JhGcily1gyuhef8ufmj30m206241d.jpg" alt="image-20210207140005238" width="80%">

<img src="https://tvax2.sinaimg.cn/large/006JhGcily1gyuhkdqobfj318z0kcqjl.jpg" alt="image-20210207141004278" width="80%">

**堆的柱状图，查询内存中有哪些对象，个数多少，占用空间多少**

<img src="https://tvax1.sinaimg.cn/large/006JhGcily1gyuhlddh2uj31cj0ixds7.jpg" alt="image-20210207141159236" width="80%">

**OQL 查询语句，查询字符串长度大于 500 的**

<img src="https://tvax3.sinaimg.cn/large/006JhGcily1gyuhnnl4toj314q0iwgum.jpg" alt="image-20210207141027742" width="80%">

### 7 jstack:打印 JVM 中线程快照

#### 7.1 基本情况

jstack(JVM Stack Trace):用于生成虚拟机指定进程当前时刻的线程快照(虚拟机堆栈跟踪)。线程快照就是当前虚拟机内指定进程的每一条线程正在执行的方法堆栈的集合。

生成线程快照的作用:`可用于定位线程出现长时间停顿的原因`，如线程间死锁、死循环、请求外部资源导致的长时间等待等问题。这些都是导致线程长时间停顿的常见原因。当线程出现停顿时，就可以用 jstack 显示各个线程调用的堆栈情况。

官方帮助文档:

https://docs.oracle.com/en/java/javase/11/tools/jstack.html

在 thread dump 中，要留意下面几种状态

- 死锁,Deadlock（重点关注)
- 等待资源，waiting on condition（重点关注）
- 等待获取监视器，waiting on monitor entry（重点关注)阻塞，Blocked（重点关注)
- 执行中，Runnable
- 暂停，Suspended
- 对象等待中，Object.wait(）或 TIMED_WAITING
- 停止，Parked

#### 7.2 基本语法

**option 参数:-F**

当正常输出的请求不被响应时，强制输出线程堆栈

**option 参数:-l**

除堆栈外，显示关于锁的附加信息

**option 参数: -m**

如果调用到本地方法的话，可以显示 C/C++的堆栈

**option 参数:-h**

帮助操作

**测试代码**

```java
// 测试死锁
public class ThreadDeadLock {

    public static void main(String[] args) {

        StringBuilder s1 = new StringBuilder();
        StringBuilder s2 = new StringBuilder();

        new Thread(){
            @Override
            public void run() {

                synchronized (s1){

                    s1.append("a");
                    s2.append("1");

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    synchronized (s2){
                        s1.append("b");
                        s2.append("2");

                        System.out.println(s1);
                        System.out.println(s2);
                    }

                }

            }
        }.start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (s2){

                    s1.append("c");
                    s2.append("3");

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    synchronized (s1){
                        s1.append("d");
                        s2.append("4");

                        System.out.println(s1);
                        System.out.println(s2);
                    }
                }
            }
        }).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
```

<img src="https://tva3.sinaimg.cn/large/006JhGcily1gyuhwz28s9j30vj0bek4b.jpg" alt="image-20210207142525560" width="85%">

<img src="https://tva3.sinaimg.cn/large/006JhGcily1gyuhxzj8fzj30qm07n7ar.jpg" alt="image-20210207142534091" width="85%">

```java
// 测试阻塞
public class TreadSleepTest {
    public static void main(String[] args) {
        System.out.println("hello - 1");
        try {
            Thread.sleep(1000 * 60 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("hello - 2");
    }
}
```

<img src="https://tva2.sinaimg.cn/large/006JhGcily1gyui39fp5qj30ub03atbs.jpg" alt="image-20210207142706320" width="85%">

```java
public class ThreadSyncTest {
    public static void main(String[] args) {
        Number number = new Number();
        Thread t1 = new Thread(number);
        Thread t2 = new Thread(number);

        t1.setName("线程1");
        t2.setName("线程2");

        t1.start();
        t2.start();
    }
}

class Number implements Runnable {
    private int number = 1;

    @Override
    public void run() {
        while (true) {
            synchronized (this) {

                if (number <= 100) {

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println(Thread.currentThread().getName() + ":" + number);
                    number++;

                } else {
                    break;
                }
            }
        }
    }

}
```

<img src="https://tvax1.sinaimg.cn/large/006JhGcily1gyui7x04ycj30vq0777cl.jpg" alt="image-20210207142939301" width="85%">

### 8 jcmd:多功能命令行

#### 8.1 基本情况

在 JDK 1.7 以后,新增了一个命令行工具 jcmd。

它是一个多功能的工具，可以用来实现前面除了 jstat 之外所有命令的功能。比如:用它来导出堆、内存使用、查看 Java 进程、导出线程信息、执行 GC、JVM 运行时间等。

官方帮助文档:

https://docs.oracle.com/en/java/javase/11/tools/jcmd.html

jcmd 拥有 jmap 的大部分功能，并且在 Oracle 的官方网站上也推荐使用 jcmd 命令代 jmap 命令

#### 8.2 基本语法

**jcmd -l**

列出所有的 JVM 进程

**jcmd pid help**

针对指定的进程，列出支持的所有命令

**jcmd pid 具体命令**

显示指定进程的指令命令的数据

```java
JFR.stop
JFR.start
JFR.dump
JFR.check
VM.native_memory
VM.check_commercial_features
VM.unlock_commercial_features
ManagementAgent.stop
ManagementAgent.start_local
ManagementAgent.start
VM.classloader_stats
GC.rotate_log
Thread.print   // 类似于jstack 可以查看死锁等情况
GC.class_stats
GC.class_histogram   //  查看实例个数 占用大小
GC.heap_dump   //导出dump文件  jcmd GC.heap_dump d：\1.hprof
GC.finalizer_info
GC.heap_info
GC.run_finalization
GC.run
VM.uptime    //进程执行的时间
VM.dynlibs
VM.flags  // 虚拟机配置参数 Java -XX:+PrintCommandLineFlags -version  自己设定的参数
VM.system_properties // 类似于jinfo -sysprops PID 系统属性信息
VM.command_line
VM.version
help
```

### 9 jstatd:远程主机信息收集

之前的指令只涉及到监控本机的 Java 应用程序，而在这些工具中，一些监控工具也支持对远程计算机的监控（如 jps、jstat)。为了启用远程监控，则需要配合使用 jstatd 工具。

命令 jstatd 是一个 RMI 服务端程序，它的作用相当于代理服务器，建立本地计算机与远程监控工具的通信。jstatd 服务器将本机的 Java 应用程序信息传递到远程计算机。

<img src="https://tva1.sinaimg.cn/large/006JhGcily1gyuigwm6yej30lv074758.jpg" alt="image-20210207150014108" width="65%">
