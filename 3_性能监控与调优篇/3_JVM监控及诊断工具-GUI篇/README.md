# 第三章：JVM监控及诊断工具-GUI篇

## 1. 工具概述

使用上一章命令行工具或组合能帮您获取目标Java应用性能相关的基础信息，但它们存在下列局限:

1. 无法获取方法级别的分析数据，如方法间的调用关系、各方法的调用次数和调用时间等(这对定位应用性能瓶颈至关重要）
2. 要求用户登录到目标Java 应用所在的宿主机上，使用起来不是很方便。
3. 分析数据通过终端输出，结果展示不够直观。

为此，JDK提供了一些内存泄漏的分析工具，如jconsole，jvisualvm等，用于辅助开发人员定位问题，但是这些工具很多时候并不足以满足快速定位的需求。所以这里我们介绍的工具相对多一些、丰富一些。

**图形化综合诊断工具.**

- JDK自带的工具

  - **jconsole:**DK自带的可视化监控工具。查看Java应用程序的运行概况、监控堆信
    息、永久区（或元空间）使用情况、类加载情况等
    - 位置:jdklbin\jconsole.exe
  -  **Visual VM:**Visual VM是一个工具，它提供了一个可视界面，用于查看Java虚拟机上运行的基于Java技术的应用程序的详细信息。‘
    - 位置:jdk\binljvisualvm.exe
  - JMC :Java Mission Control，内置Java Flight Recorder。能够以极低的性能开销收集Java虚拟机的性能数据。

- 第三方工具

  - **MAT:** MAT(Memory Analyzer Tool)是基于Eclipse的内存分析工具，是一个快速、功能丰富的Java heap分析工具，它可以帮助我们查找内存泄漏和减少内存消耗
    - Eclipse的插件形式

  -  **JProfiler**:商业软件，需要付费。功能强大。
    - 与VisualVM类似
  - Arthas:Alibaba开源的Java诊断工具。深受开发者喜爱。
  - Btrace:Java运行时追踪工具。可以在不停机的情况下，跟踪指定的方法调用、构造函数调用和系统内存等信息。



## 2. jconsole

### 基本概述
**jconsole:**

- 从Java5开始，在JDK中自带的java监控和管理控制台。
- 用于对JVM中内存、线程和类等的监控，是一个基于JMX(java management extensions)的GUI性能监控工具。
- 官方教程: https://docs.oracle.com/javase/7/docs/technotes/guides/management/jconsole.html

### 启动

jdk/bin目录下，启动jconsole.exe命令即可

不需要使用jps命令来查间

### 三种连接方式   

**Local**

使用JConsole连接一个正在本地系统运行的JVM,并且执行程序的和运行IConsole的需要是同一个用户。JConsole使用文件系统的授权通过RMI连接器连接到平台的MBean服务器上。这种从本地连接的监控能力只有Sun的DK具有。

**Remote**

使用下面的URL通过RMI连接器连接到一个JMX代理，service.jmx.rmit://jindi/rmi://hostName:portNum/jmxrmi。JConsole为建立连接，需要在环境变量中设置mx.remote.credentials来指定用户名和密码，从而进行授权。

**Advanced**

使用一个特殊的URL连接JMX代理。一般情况使用自己定制的连接器而不是RMI提供的连接器来连接JMX代理，或者是一个便用JDK1.4的实现了JMX和JMX Rmote的应用。

### 主要作用

监控内存
监控线程

监控死锁
类加载与虚拟机信息

### Jconsole使用

测试代码

```java
import java.util.ArrayList;
import java.util.List;
public class HelloGC {
    byte[] a = new byte[1024 * 100]; //100kb

    public static void main(String[] args) throws InterruptedException {

        List list = new ArrayList();
        while (true) {
            list.add(new HelloGC());
            Thread.sleep(100);
        }

    }
}
```



![image-20200828221609446](.\images\image-20200828221609446.png)



双击本地HeoolGC然后选择不安全连接

![image-20200828221647059](.\images\image-20200828221647059.png)



---

**作用：** 查看Java应用程序的运行概况，监视垃圾收集器管理的虚拟机内存(堆和元空间)的变化趋势，以及监控程序内的线程。



启动JConsole，在控制台输入：jconsole即可,在弹出的界面中，选择本地进程，然后进去看界面页签信息。显示的是整个虚拟机主要运行数据的概览，其中包括堆内存使用情况，线程，类，CPU使用情况四项信息的曲线图。

进来之后有很多标签的，先看默认的第一个概览

概览是对整个Jconsole功能进行一个简述其余几个是对概览的详细解释

![image-20200828222348574](.\images\image-20200828222348574.png)



#### 内存 

相当于命令行的jstat命令，用于监视受垃圾收集器管理的虚拟机内存(堆和元空间)的变化趋势，这不仅是包括堆内存的整体信息，更细化到伊甸区、幸存区、老年代的使用情况。同时，也包括非堆区，即元空间的使用情况，单机界面右上角的“执行GC”按钮，可以强制应用程序进行一次Full GC。

![image-20200828222457240](.\images\image-20200828222457240.png)

可以自己切换查看伊甸园区 老年代 非堆（元空间）的内存使用情况

![image-20200828222743005](.\images\image-20200828222743005.png)



#### 线程：

相当于命令行的jstack命令，遇到线程停顿的时候可以使用它来进行监控分析。JConsole 显示了系统内的线程数量，并在屏幕下方，显示了程序中所有的线程。单击线程名称，便可以查看线程的栈信息。

![image-20200828223128644](.\images\image-20200828223128644.png)

#### 类：

如图所示，显示了系统以及装载的类数量。在详细信息栏中，还显示了已卸载的类数量。

![image-20200828223208143](.\images\image-20200828223208143.png)

#### VM摘要：

在VM摘要页面，JConsole 显示了当前应用程序的运行环境。包括虚拟机类型、版本、堆信息以及虚拟机参数等。相当于jinfo命令

![image-20200828223359188](.\images\image-20200828223359188.png)



#### MBean：

MBean页面允许通过JConsole访问已经在MBean服务器注册的MBean对象。

### Jconsole动态监控

上面已经讲解了Jconsole的基本使用，下面我们就通过Jconsole动态监控一下程序的运行状态。

```java
//每次回车切换一种状态
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
    JConsole演示线程监控
*/
public class Demo09 {
    public static void main(String[] args) throws IOException {
        System.in.read();
        System.out.println("开启了死循环线程");
        whileTrueThread();

        System.in.read();
        System.out.println("开启了等待线程");
        waitThread(new Object());


        System.in.read();
        System.out.println("开启了死锁线程");
        deadLock();

        System.in.read();
    }

    private static void whileTrueThread() {
        new Thread(() -> {
            while (true) ;
        }, "whileTrueThread").start();
    }

    private static void waitThread(Object o) {
        new Thread(() -> {
            synchronized (o) {
                try {
                    o.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"myWaited").start();
    }

    private static void deadLock() {
        Lock lock1 = new ReentrantLock();
        Lock lock2 = new ReentrantLock();
        new Thread(() -> {
            try {
                lock1.lock();
                Thread.sleep(100);
                lock2.lock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "myThread1").start();
        new Thread(() -> {
            try {
                lock2.lock();
                Thread.sleep(100);
                lock1.lock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "myThread2").start();
    }
}

```

**这是初始状态**

![image-20200828225818103](.\images\image-20200828225818103.png)



**当控制台输入一个回车 开启死循环时**

CPU直接飙升到25%不下降（因为我4核4线程 所以会到25% 如果是8线程应该是12%左右，当然如果虚拟机单核的 直接97%+）

![image-20200828224725515](.\images\image-20200828224725515.png)

**当再次回车时候开启等待线程**

这是未按回车的时候（未开启等待线程）

![image-20200828225000092](.\images\image-20200828225000092.png)

这是按回车的时候（开启等待线程）会多了一个myWaited线程

![image-20200828225049973](.\images\image-20200828225049973.png)

**再次会车开启死锁线程**

会再次多出两个线程 状态waiting

![image-20200828225235412](.\images\image-20200828225235412.png)

```java
名称: myThread2
状态: java.util.concurrent.locks.ReentrantLock$NonfairSync@5b103b9b上的WAITING, 拥有者: myThread1
总阻止数: 0, 总等待数: 2

堆栈跟踪: 
sun.misc.Unsafe.park(Native Method)
java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
java.util.concurrent.locks.AbstractQueuedSynchronizer.parkAndCheckInterrupt(AbstractQueuedSynchronizer.java:836)
java.util.concurrent.locks.AbstractQueuedSynchronizer.acquireQueued(AbstractQueuedSynchronizer.java:870)
java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(AbstractQueuedSynchronizer.java:1199)
java.util.concurrent.locks.ReentrantLock$NonfairSync.lock(ReentrantLock.java:209)
java.util.concurrent.locks.ReentrantLock.lock(ReentrantLock.java:285)
cn.itcast.un.Demo09.lambda$deadLock$3(Demo09.java:64)
cn.itcast.un.Demo09$$Lambda$10/1265094477.run(Unknown Source)
java.lang.Thread.run(Thread.java:748)
```

点击检测死锁时，会检测出来

![image-20200828225414413](.\images\image-20200828225414413.png)



## 3. Visual VM

### 基本概述

- Visual VM是一个功能强大的多合一故障诊断和性能监控的可视化工具。

- 它集成了多个JDK命令行工具，使用visual VM可用于显示虚拟机进程及进程的配置和环境信息(jps,jinfo)，监视应用程序的CPU、GC、堆、方法区及线程的信息(jstat、jstack)等，甚至代替JConsole。

- 在DK 6 Update 7以后，Visual vM便作为DK的一部分发布(VisualVM 在JDK/bin目录下)，即:它完全免费。

- 此外，Visual VM也可以作为独立的软件安装

首页: https://visualvm.github.io/index.html

![image-20210210090422734](.\images\image-20210210090422734.png)

### 插件的安装

- Visual VM的一大特点是支持插件扩展，并且插件安装非常方便。我们既可以通过离线下载插件文件*.nbm，然后在Plugin对话框的已下载页面下，添加已下载的插件。也可以在可用插件页面下，在线安装插件。**（这里建议安装上:VisualGC）**

插件地址: https://visualvm.github.io/pluginscenters.html

![image-20210210090610609](.\images\image-20210210090610609.png)



**IDEA中可以安装VisualVM**

![image-20210210090927031](.\images\image-20210210090927031.png)



![image-20210210091114024](.\images\image-20210210091114024.png)

### 连接方式

#### 本地连接
监控本地Java进程的CPU、类、线程等

#### 远程连接

1. 确定远程服务器的ip地址
2. 添加JMX（通过JMX技术具体监控远端服务器哪个Java进程)
3. 修改bin/catalina.sh文件，连接远程的tomcat
4. 在..../conf中添加jmxremote.access和和jimxremote.password文件
5. 将服务器地址改为公网ip地址
6. 设置阿里云安全策略和防火墙策略
7. 启动tomcat，查看tomcat启动日志和端口监听
8. JMX中输入端口号、用户名、密码登录

### 主要功能

1. 生成/读取堆内存快照
2. 查看ⅣM参数和系统属性
3. 查看运行中的虚拟机进程
4. 生成/读取线程快照
5. 程序资源的实时监控主要功能
6. 其他功能
   - JMX代理连接
   - 远程环境监控
   - CPU分析和内存分析

![image-20210210092426723](.\images\image-20210210092426723.png)
![image-20210210092812907](.\images\image-20210210092812907.png)

## 04-eclipse MAT

### 基本概述

MAT (Memory Analyzer Tool)工具是一款功能强大的Java堆内存分析器。可以用于查找内存泄漏以及查看内存消耗情况。

MAT是基于Eclipse开发的，不仅可以单独使用，还可以作为插件的形式嵌入在Eclipse中使用。是一款免费的性能分析工具，使用起来非常方便。大家可以在
https://www.eclipse.org/mat/downloads.php下载并使用MAT。

![image-20210207171554587](.\images\image-20210207171554587.png)

只要确保机器上装有JDK并配置好相关的环境变量，MAT可正常启动。

**还可以在Eclipse中以插件的方式安装:**

![image-20210207171338594](.\images\image-20210207171338594.png)







### 获取dump文件

#### dump文件内容

MAT可以分析heap dump文件。在进行内存分析时，只要获得了反映当前设备内存映像的hprof文件，通过MAT打开就可以直观地看到当前的内存信息。

一般说来，这些内存信息包含:

- 所有的对象信息，包括对象实例、成员变量、存储于栈中的基本类型值和存储于堆中的其他对象的引用值。
- 所有的类信息，包括classloader、类名称、父类、静态变量等
- GCRoot到所有的这些对象的引用路径
- 线程信息,包括线程的调用栈及此线程的线程局部变量（TLS）



#### 两点说明

**说明1:缺点:**

MAT不是一个万能工具，它并不能处理所有类型的堆存储文件。但是比较主流的厂家和格式，例如Sun，HP，SAP所采用的 HPROF 二进制堆存储文件，以及 IBM 的 PHD堆存储文件等都能被很好的解析。

**说明2:**

最吸引人的还是能够快速为开发人员生成**内存泄漏报表**，方便定位问题和分析问题。虽然MAT有如此强大的功能，但是内存分析也没有简单到一键完成的程度，很多内存问题还是需要我们从MAT展现给我们的信息当中通过经验和直觉来判断才能发现。

#### 获取堆dump文件

**方法一:**通过前一章介绍的 jmdp工具生成，可以生成任意一个java进程的dump文件;

**方法二:**通过配置JVM参数生成。

- 选项"-XX:+HeapDumpOnoutOfMemoryError”或"-XX:+HeapDumpBeforeFullGC"
- 选项"-XX:HeapDumpPath"所代表的含义就是当程序出现OutofMNemory时，将会在相应的目录下生成一份dump文件。如果不指定选项 "-XX:HeapDumpPath"则在当前目录下生成dump文件。

对比:考虑到生产环境中几乎不可能在线对其进行分析，大都是采用离线分析，因此使用jmap+MAT工具是最常见的组合。

**方法三:**使用VisualVM可以导出堆dump文件

**方法四:**使用MAT既可以打开一个已有的堆快照，也可以通过MAT直接从活动Java程序中导出堆快照。该功能将借助jps列出当前正在运行的 Java进程，以供选择并获取快照。

![image-20210207194627577](.\images\image-20210207194627577.png)



![image-20210207201227913](.\images\image-20210207201227913.png)





### 分析堆dump文件

```java
/**
 * -Xms600m -Xmx600m -XX:SurvivorRatio=8
 */
public class OOMTest {
    public static void main(String[] args) {
        ArrayList<Picture> list = new ArrayList<>();
        while(true){
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            list.add(new Picture(new Random().nextInt(100 * 50)));
        }
    }
}

class Picture{
    private byte[] pixels;

    public Picture(int length) {
        this.pixels = new byte[length];
    }
}

// jmap -dump:format=b,file=d:\mat_log\a.hprof 4568
```

**打开生成的dump文件**

![image-20210207200052651](.\images\image-20210207200052651.png)





![image-20210207200531895](.\images\image-20210207200531895.png)



```java
Leak Suspects Report 内存泄露疑点报告 哪些对象始终存活，以及为什么没有被垃圾收集器收集
     
Component Report  组件报告，会分析一系列对象的集合，找到可疑的内存空间，比如重复的字符串 空的集合，终结器，弱引用等等，这些被认为可疑的内存
    
Re-open previously run reports 重新打开之前运行过的reports

```



![image-20210208141157918](.\images\image-20210208141157918.png)





![image-20210207204735180](.\images\image-20210207204735180.png)

![image-20210207205446750](.\images\image-20210207205446750.png)







#### histogram

展示了各个类的实例数目以及这些实例的Shallowheap 或Retainedheap的总和

![image-20210207205640066](.\images\image-20210207205640066.png)

![image-20210207205702313](.\images\image-20210207205702313.png)



![image-20210208095232645](.\images\image-20210208095232645.png)





![image-20210208102329235](.\images\image-20210208102329235.png)





![image-20210208103349269](.\images\image-20210208103349269.png)



![image-20210208103930092](.\images\image-20210208103930092.png)





![image-20210208104224201](.\images\image-20210208104224201.png)



![image-20210208105025095](.\images\image-20210208105025095.png)



#### thread overview

![image-20210208105409103](.\images\image-20210208105409103.png)





##### 获得对象相互引用的关系

**with outgoing references**

![image-20210208110251032](.\images\image-20210208110251032.png)

![image-20210208110348940](.\images\image-20210208110348940.png)



**with incoming references**

![image-20210208110502333](.\images\image-20210208110502333.png)



![image-20210208111637670](.\images\image-20210208111637670.png)



#### 浅堆与深堆

##### shallow heap

浅堆(Shallow Heap)是指一个对象所消耗的内存。在32位系统中，一个对象引用会占据4个字节，一个int类型会占据4个字节，long型变量会占据8个字节，每个对象头需要占用8个字节。根据堆快照格式不同，对象的大小可能会向8字节进行对齐。

以String为例:2个int值共占8字节，对象引用占用4字节，对象头8字节，合计20字节，向8字节对齐，故占24字节。（jdk7中）

​    ![image-20210208112313927](.\images\image-20210208112313927.png)

这24字节为String对象的浅堆大小。它与String的value实际取值无关，无论字符串长度如何，浅堆大小始终是24字节。

##### retained heap 

**保留集(Retained Set):**

对象A的保留集指当对象A被垃圾回收后，可以被释放的所有的对象集合(包括对象A本身)，即对象A的保留集可以被认为是**只能通过**对象A被直接或间接访问到的所有对象的集合。通俗地说，就是指仅被对象A所持有的对象的集合。

**深堆(Retained Heap):**

深堆是指对象的保留集中所有的对象的浅堆大小之和。


**注意:**浅堆指对象本身占用的内存，不包括其内部引用对象的大小。一个对象的深堆指只能通过该对象访问到的(直接或间接)所有对象的浅堆之和，即对象被回收后，可以释放的真实空间。

​        

##### 补充:对象实际大小

另外一个常用的概念是对象的实际大小。这里，对象的实际大小定义为一个对象所能触及的所有对象的浅堆大小之和，也就是通常意义上我们说的对象大小。与深堆相比，似乎这个在日常开发中更为直观和被人接受，**但实际上，这个概念和垃圾回收无关。**

下图显示了一个简单的对象引用关系图，对象A引用了C和D，对象B引用了C和E。那么对象A的浅堆大小只是A本身，不含C和D，而A的实际大小为A、C、D三者之和。而A的深堆大小为A与D之和，由于对象C还可以通过对象B访问到，因此不在对象A的深堆范围内。

![image-20210208113614480](.\images\image-20210208113614480.png)

##### 练习

**看图理解Retained Size**

![image-20210208114018458](.\images\image-20210208114018458.png)

上图中，GC Roots直接引用了A和B两个对象。

A对象的Retained Size=A对象的ShaJlow Size

B对象的Retained Size=B对象的Shallow Size +C对象的Shallow Size

**这里不包括D对象，因D对象被GC Roots直接引用。**

​     

##### 案例分析:StudentTrace

**代码：**

```java
/**
 * 有一个学生浏览网页的记录程序，它将记录 每个学生访问过的网站地址。
 * 它由三个部分组成：Student、WebPage和StudentTrace三个类
 *
 *  -XX:+HeapDumpBeforeFullGC -XX:HeapDumpPath=d:\student.hprof
 */
public class StudentTrace {
    static List<WebPage> webpages = new ArrayList<WebPage>();


    public static void createWebPages() {
        for (int i = 0; i < 100; i++) {
            WebPage wp = new WebPage();
            wp.setUrl("http://www." + Integer.toString(i) + ".com");
            wp.setContent(Integer.toString(i));
            webpages.add(wp);
        }
    }

    public static void main(String[] args) {
        createWebPages();//创建了100个网页
        //创建3个学生对象
        Student st3 = new Student(3, "Tom");
        Student st5 = new Student(5, "Jerry");
        Student st7 = new Student(7, "Lily");

        for (int i = 0; i < webpages.size(); i++) {
            if (i % st3.getId() == 0)
                st3.visit(webpages.get(i));
            if (i % st5.getId() == 0)
                st5.visit(webpages.get(i));
            if (i % st7.getId() == 0)
                st7.visit(webpages.get(i));
        }
        webpages.clear();
        System.gc();

    }
}
@Getter
@Setter
class Student {
    private int id;
    private String name;
    private List<WebPage> history = new ArrayList<>();

    public Student(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }
    
    public void visit(WebPage wp) {
        if (wp != null) {
            history.add(wp);
        }
    }
}

@Data
class WebPage {
    private String url;
    private String content;
    
}
```



![image-20210208130120288](.\images\image-20210208130120288.png)

```shell
考虑Lily同学:15个 webpage

每个对应152个字节15* 152 = 2280字节-->即为elementData的实际大小

关心的是elementData的深堆1288是如何计算出来的?

能被7整除，且能被3整除，以及能被7整除，且能被5整除的数值有:0，21,42,63,84,35,70共7个数。7*152= 1064字节

2280 -1064 +72=1288字节!

这72个字节是什么?

15个elementData的元素*4字节=60字节
60＋8个对象头的字节数＋4字节数组长度=72字节

```





#### 支配树

**支配树（Dominator Tree）**

支配树的概念源自图论。

MAT提供了一个称为支配树（Dominator Tree）的对象图。支配树体现了对象实例间的支配关系。在对象引用图中，所有指向对象B的路径都经过对象A，则认为**对象A支配对象B。**如果对象A是离对象B最近的一个支配对象，则认为对象A为对象B的**直接支配者。**支配树是基于对象间的引用图所建立的，它有以下基本性质: I

- 对象A的子树（所有被对象A支配的对象集合）表示对象A的保留集(retained set），即深堆。·

- 如果对象A支配对象B，那么对象A的直接支配者也支配对象B。

- 支配树的边与对象引用图的边不直接对应。

如下图所示:左图表示对象引用图，右图表示左图所对应的支配树。对象A和B由根对象直接支配，由于在到对象C的路径中，可以经过A，也可以经过B，因此对象C的直接支配者也是根对象。对象F与对象D相互引用，因为到对象F的所有路径必然经过对象D，因此，对象D是对象F的直接支配者。而到对象D的所有路径中，必然经过对象c，即使是从对象F到对象D的引用，从根节点出发，也是经过对象C的，所以，对象D的直接支配者为对象C。

![image-20210208140051293](.\images\image-20210208140051293.png)

同理，对象E支配对象G。到达对象H的可以通过对象D，也可以通过对象E，因此对象D和E都不能支配对象H，而经过对象C既可以到达D也可以到达E，因此对象c为对象H的直接支配者。

**在MAT中，单击工具栏上的对象支配树按钮，可以打开对象支配树视图。**

![image-20210208140147581](.\images\image-20210208140147581.png)



下图显示了对象支配树视图的一部分。该截图显示部分Lily学生的history队列的直接支配对象。即当Lily对象被回收，也会一并回收的所有对象。显然能被3或者5整除的网页不会出现在该列表中，因为它们同时被另外两名学生对象引用。

![image-20210208140538732](.\images\image-20210208140538732.png)

### 案例:Tomcat堆溢出分析

#### 说明

Tomcat是最常用的Java Servlet容器之一，同时也可以当做单独的web服务器使用。Tomcat本身使用Java实现，并运行于Java虚拟机之上。在大规模请求时，Tomcat有可能会因为无法承受压力而发生内存溢出错误。这里根据一个被压垮的Tomcat的堆快照文件，来分析Tomcat在崩溃时的内部情况。

#### 分析过程

**图一**

![image-20210208142854271](.\images\image-20210208142854271.png)

**图二**

![image-20210208143011263](.\images\image-20210208143011263.png)



**图3**:sessions对象，它占用了约17MB空间

![image-20210208143041693](.\images\image-20210208143041693.png)

**图4:**可以看到sessions对象为ConcurrentHashMap，其内部分为16个Segment。从深堆大小看，每个Segment都比较平均,大约为1MB，合计17MB。

![image-20210208143230477](.\images\image-20210208143230477.png)

**图五**

![image-20210208143306263](.\images\image-20210208143306263.png)

**图6:**当前堆中含有9941个session，并且每一个session的深堆为1592字节，合计约15NB，达到当前堆大小的50%。

![image-20210208143404588](.\images\image-20210208143404588.png)

**图7**:选择一个Session查看他的属性情况

![image-20210208143611042](.\images\image-20210208143611042.png)

**图八**

![image-20210208143510464](.\images\image-20210208143510464.png)



根据当前的session总数，可以计算每秒的平均压力为:9941/(1403324677648-1403324645728)*L.000=311次/秒。

由此推断，在发生Tomcat堆溢出时，Tomcat在连续30秒的时间内，平均每秒接收了约311次不同客户端的请求，创建了合计9941个session。



## 补充1:再谈内存泄漏

### 内存泄漏的理解与分类

**何为内存泄漏（ memory leak）**

![image-20210208143813840](.\images\image-20210208143813840.png)

可达性分析算法来判断对象是否是不再使用的对象，本质都是判断一个对象是否还被引用。那么对于这种情况下，由于代码的实现不同就会出现很多种内存泄漏问题（让JVM误以为此对象还在引用中，无法回收，造成内存泄漏）。

**内存泄漏（memory leak）的理解**

**严格来说，只有对象不会再被程序用到了，但是Gc又不能回收他们的情况，才叫内存泄漏。**

但实际情况很多时候一些不太好的实践(或疏忽）会导致对象的生命周期变得很长甚至导致OOM，也可以叫做**宽泛意义上的“内存泄漏”。**

![image-20210208144439475](.\images\image-20210208144439475.png)



对象X引用对象Y，X的生命周期比Y的生命周期长;

那么当Y生命周期结束的时候，X依然引用着Y，这时候，垃圾回收期是不会回收对象Y的;

如果对象X还引用着生命周期比较短的A、B、C，对象A又引用着对象a、b、c，这样就可能造成大量无用的对象不能被回收，进而占据了内存资源，造成内存泄漏，直到内存溢出。

**内存泄漏与内存溢出的关系:**

1．内存泄漏（memory leak )

申请了内存用完了不释放，比如一共有1024M的内存，分配了512M 的内存一直不回收，那么可以用的内存只有512M 了，仿佛泄露掉了一部分;

通俗一点讲的话，内存泄漏就是【占着茅坑不拉shi】。

2．内存溢出（out of memory)

申请内存时，没有足够的内存可以使用;

通俗一点儿讲，一个厕所就三个坑，有两个站着茅坑不走的（内存泄漏），剩下最后一个坑，厕所表示接待压力很大，这时候一下子来了两个人，坑位（内存）就不够了，内存泄漏变成内存溢出了。

**泄漏的分类**

经常发生:发生内存泄露的代码会被多次执行，每次执行，泄露一块内存;

偶然发生:在某些特定情况下才会发生;（资源的关闭，数据库连接，IO流关闭      ）

一次性:发生内存泄露的方法只会执行一次;

隐式泄漏:一直占着内存不释放，直到执行结束;严格的说这个不算内存泄漏，因为最终释放掉了，但是如果执行时间特别长，也可能会导致内存耗尽。



### Java中内存泄漏的8种情况

#### 1-静态集合类

静态集合类，如HashMap、LinkedList等等。如果这些容器为静态的，那么它们的生命周期与JVM程序一致，则容器中的对象在程序结束之前将不能被释放，从而造成内存泄漏。简单而言，长生命周期的对象持有短生命周期对象的引用，尽管短生命周期的对象不再使用，但是因为长生命周期对象持有它的引用而导致不能被回收。

```java
public class MemoryLeak i{
    static List List = new ArrayList();
    public void oomTests(){  
        object obj = new object(); //局部变量
        list.add(obj);
    }
}
```

#### 2-单例模式

单例模式，和静态集合导致内存泄露的原因类似，因为单例的静态特性，它的生命周期和JVM 的生命周期一样长，所以如果单例对象如果持有外部对象的引用，那么这个外部对象也不会被回收，那么就会造成内存泄漏。

#### 3-内部类持有外部类

内部类持有外部类，如果一个外部类的实例对象的方法返回了一个内部类的实例对象。

这个内部类对象被长期引用了，即使那个外部类实例对象不再被使用，但由于内部类持有外部类的实例对象，这个外部类对象将不会被垃圾回收，这也会造成内存泄漏。

#### 4-各种连接,如数据库连接、网络连接和IO连接等

各种连接，如数据库连接、网络连接和Io连接等。

在对数据库进行操作的过程中，首先需要建立与数据库的连接，当不再使用时，需要调用close方法来释放与数据库的连接。只有连接被关闭后，垃圾回收器才会回收对应的对象。

否则，如果在访问数据库的过程中，对Connection、Statement或ResultSet不显性地关闭，将会造成大量的对象无法被回收，从而引起内存泄漏。

```java
public static void main( String[] args){
  try {
    Connection conn = nu1l;
    Class.forName( "com.mysql.jdbc.Driver");
    conn = DriverManager.getConnection( "ur1","","");statement stmt = conn.createStatement();
    Resultset rs = stmt.executeQuery(" ....");
  }catch (Exception e) {//异常日志
  
  }finally{
   //1.关闭结果集Statement
   //2.关闭声明的对象ResultSet1
   //3.关闭连接Connection 
  }       
}
```

#### 5-变量不合理的作用域

变量不合理的作用域。一般而言，一个变量的定义的作用范围大于其使用范围，很有可能会造成内存泄漏。另一方面，如果没有及时地把对象设置为null，很有可能导致内存泄漏的发生。

```java
public class UsingRandom {
   private String msg;
   public void receiveMsg(){
      readFromNet();//从网络中接受数据保存到msg中
      saveDB();//把msg保存到数据库中
    }
}
```



如上面这个伪代码，通过readFromNet方法把接受的消息保存在变量msg中，然后调用saveDB方法把msg的内容保存到数据库中，此时msg已经就没用了，由于msg的生命周期与对象的生命周期相同，此时msg还不能回收，因此造成了内存泄漏。 

实际上这个msg变量可以放在receiveNsg方法内部，当方法使用完，那么msg的生命周期也就结束，此时就可以回收了。还有一种方法，在使用完msg后，把msg设置为null，这样垃圾回收器也会回收msg的内存空间。

#### 6-改变哈希值

改变哈希值，当一个对象被存储进HashSet集合中以后，就不能修改这个对象中的那些参与计算哈希值的字段了。

否则，对象修改后的哈希值与最初存储进HashSet集合中时的哈希值就不同了，在这种情况下，即使在contains方法使用该对象的当前引用作为的参数去HashSet集合中检索对象，也将返回找不到对象的结果，这也会导致无法从HashSet集合中单独删除当前对象，造成内存泄漏。

这也是String为什么被设置成了不可变类型，我们可以放心地把 String存入 HashSet，或者把String当做HashMap的key 值;

当我们想把自己定义的类保存到散列表的时候，需要保证对象的hashCode不可变。

**举例1**

```java
/**
 * 演示内存泄漏
 */
public class ChangeHashCode1 {
    public static void main(String[] args) {
        HashSet<Point> hs = new HashSet<Point>();
        Point cc = new Point();
        cc.setX(10);//hashCode = 41
        hs.add(cc);

        cc.setX(20);//hashCode = 51  此行为导致了内存的泄漏

        System.out.println("hs.remove = " + hs.remove(cc));//false
        hs.add(cc);
        System.out.println("hs.size = " + hs.size());//size = 2

        System.out.println(hs);
    }

}

class Point {
    int x;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Point other = (Point) obj;
        if (x != other.x) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                '}';
    }
}
```

**举例2**

```java
public class ChangeHashCode {
    public static void main(String[] args) {
        HashSet set = new HashSet();
        Person p1 = new Person(1001, "AA");
        Person p2 = new Person(1002, "BB");

        set.add(p1);
        set.add(p2);

        p1.name = "CC";//导致了内存的泄漏
        set.remove(p1); //删除失败

        System.out.println(set);

        set.add(new Person(1001, "CC"));
        System.out.println(set);

        set.add(new Person(1001, "AA"));
        System.out.println(set);

    }
}

class Person {
    int id;
    String name;

    public Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        if (id != person.id) return false;
        return name != null ? name.equals(person.name) : person.name == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
// 结果
[Person{id=1002, name='BB'}, Person{id=1001, name='CC'}]
[Person{id=1002, name='BB'}, Person{id=1001, name='CC'}, Person{id=1001, name='CC'}]
[Person{id=1002, name='BB'}, Person{id=1001, name='CC'}, Person{id=1001, name='CC'}, Person{id=1001, name='AA'}]
```



#### 7-缓存泄漏

内存泄漏的另一个常见来源是缓存，一旦你把对象引用放入到缓存中，他就很容易遗忘。比如:之前项目在一次上线的时候，应用启动奇慢直到夯死，就是因为代码中会加载一个表中的数据到缓存（内存)中，测试环境只有几百条数据，但是生产环境有几百万的数据。

对于这个问题，可以使用WeakHashMap代表缓存，此种Map的特点是，当除了自身有对key的引用外，此key没有其他引用那么此map会自动丢弃此值。

![image-20210208154208970](.\images\image-20210208154208970.png)



上面代码和图示主演演示WeakHashMap如何自动释放缓存对象，当init函数执行完成后，局部变量宁符串引用weakd1,weakd2,d1,d2都会消失，此时只有静态map中保存中对字符串对象的引用，可以看到，调用gc之后，HashMap的没有被回收，而WeakHashMap里面的缓存被回收了。

**案例**

```java
/**
 * 演示内存泄漏
 */
public class MapTest {
    static Map wMap = new WeakHashMap();
    static Map map = new HashMap();

    public static void main(String[] args) {
        init();
        testWeakHashMap();
        testHashMap();
    }

    public static void init() {
        String ref1 = new String("obejct1");
        String ref2 = new String("obejct2");
        String ref3 = new String("obejct3");
        String ref4 = new String("obejct4");
        wMap.put(ref1, "cacheObject1");
        wMap.put(ref2, "cacheObject2");
        map.put(ref3, "cacheObject3");
        map.put(ref4, "cacheObject4");
        System.out.println("String引用ref1，ref2，ref3，ref4 消失");

    }

    public static void testWeakHashMap() {

        System.out.println("WeakHashMap GC之前");
        for (Object o : wMap.entrySet()) {
            System.out.println(o);
        }
        try {
            System.gc();
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("WeakHashMap GC之后");
        for (Object o : wMap.entrySet()) {
            System.out.println(o);
        }
    }

    public static void testHashMap() {
        System.out.println("HashMap GC之前");
        for (Object o : map.entrySet()) {
            System.out.println(o);
        }
        try {
            System.gc();
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("HashMap GC之后");
        for (Object o : map.entrySet()) {
            System.out.println(o);
        }
    }

}
/**
 * 结果
 * String引用ref1，ref2，ref3，ref4 消失
 * WeakHashMap GC之前
 * obejct2=cacheObject2
 * obejct1=cacheObject1
 * WeakHashMap GC之后
 * HashMap GC之前
 * obejct4=cacheObject4
 * obejct3=cacheObject3
 * Disconnected from the target VM, address: '127.0.0.1:51628', transport: 'socket'
 * HashMap GC之后
 * obejct4=cacheObject4
 * obejct3=cacheObject3
 **/

```

#### 8-监听器和回调

内存泄漏另一个常见来源是监听器和其他回调，如果客户端在你实现的API中注册回调，却没有显示的取消，那么就会积聚。|

需要确保回调立即被当作垃圾回收的最佳方法是只保存它的弱引用，例如将他们保存成为WeakHashMap中的键。



### 内存泄漏案例分析

#### 案例1

**案例代码**

```java
/**
 * @author shkstart
 * @create 15:05
 */
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) { //入栈
        ensureCapacity();
        elements[size++] = e;
    }
    //存在内存泄漏
//    public Object pop() { //出栈
//        if (size == 0)
//            throw new EmptyStackException();
//        return elements[--size];
//    }

    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null;
        return result;
    }

    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }
}
```





**分析**

上述程序并没有明显的错误，但是这段程序有一个内存泄漏，随着GC活动的增加，或者内存占用的不断增加，程序性能的降低就会表现出来，严重时可导致内存泄漏，但是这种失败情况相对较少。

代码的主要问题在pop函数，下面通过这张图示展现

假设这个栈一直增长，增长后如下图所示



![image-20210208154803077](.\images\image-20210208154803077.png)



当进行大量的pop操作时，由于引用未进行置空，gc是不会释放的，如下图所示

![image-20210208154637550](.\images\image-20210208154637550.png)

上图中看以看出，如果栈先增长，在收缩，那么从栈中弹出的对象将不会被当作垃圾回收，即使程序不再使用栈中的这些队象，他们也不会回收，因为栈中仍然保存这对象的引用，俗称过期引用，这个内存泄露很隐蔽。



**分析解决办法**

```java
   public Object pop() {
        if (size == 0)
            throw new EmptyStackException();
        Object result = elements[--size];
        elements[size] = null;
        return result;
    }
```



一旦引用过期，清空这些引用，将引用置空。

![image-20210208155140052](.\images\image-20210208155140052.png)



## 补充2:支持使用OQL语言查询对象信息

MAT支持一种类似于SQL的查询语言OQL (Object Query Language)。OQL使用类SQL语法，可以在堆中进行对象的查找和筛选。

### SELECT子句

**Select子句:**

在MAT中，Select子句的格式与SQL基本一致，用于指定要显示的列。Select子句中可以使用“*”，查看结果对象的引用实例（相当于outgoing references）。

SELECT * FRpM java.util.Vector v

**使用“OBJECTS”关键字，可以将返回结果集中的项以对象的形式显示。**

SELECT objects v.elementData FROM java.util.Vector v

SELECT OBECTS s.value FROM javl.lang.String s

**在Select子句中，使用“AS RETAINED SET”关键字可以得到所得对象的保留集。**

SELECT AS RETAINED SET *FROM com.atguigu.mat. Student

**“DISTINCT”关键字用于在结果集中去除重复对象。**

SELECT DISTINCT OBJECTS classof(s) FROM java.lang.String s

 

### FROM子句

From子句用于指定查询范围，它可以指定类名、正则表达式或者对象地址。

SELECT *FROM java.lang.String s

下例使用正则表达式，限定搜索范围，输出所有com.atguigu包下所有类的实例

SELECT * FROM "com\.atguigul..

也可以直接使用类的地址进行搜索。使用类的地址的好处是可以区分被不同ClassLoader加载的同一种类型。

select * from Ox37a0b4d

![image-20210208160701748](.\images\image-20210208160701748.png)



```sql
select objects v.elementData from java.util.ArrayList v

select as retained set * from com.atguigu.mat.Student
```





### WHERE子句



where子句用于指定oQL的查询条件。OQL查询将只返回满足Where子句指定条件的对象。where子句的格式与传统SQL极为相似。

**下例返回长度大于10的char数组。**

SELECT * FROM char[ ] s WHERE s.@length>10 

**下例返回包含“java”子字符串的所有字符串，使用“LIKE”操作符，“LIKE”操作符的操作参数为正则表达式。**

` SELECT * FROM java.lang.String s WHERE toString(s) LIKE ".*java."`

**下例返回所有value域不为null的字符串，使用“=”操作符。**

SELECT * FROM java.lang.String s where s.value!=null

**Where子句支持多个条件的AND、OR运算。下例返回数组长度大于15，并且深堆大于1000字节的所有Vector对象。**

SELECT * FROM java.util.Vector v WHERE v.elementData.@length>15 AND v.@retainedHeapsize>1000



### 内置对象与方法属

0QL中可以访问堆内对象的属性，也可以访问堆内代理对象的属性。访问堆内对象的属性时，格式如下:

`[ <alias>. ] <field> . <field>. <field>`其中alias为对象名称。

**访问java.io.File对象的path属性，并进一步访问path的value属性:**

SELECT toString(f.path.value)FROM java.io.File f

**下例显示了String对象的内容、objectid和objectAddress。**

SELECT s.toString(), s.@objectId, s.@objectAddress FROM java.lang.String s

**下例显示java.util.Vector内部数组的长度。**

SELECT v.elementData.@length FROM java.util.Vector v

**下例显示了所有的java.util.Vector对象及其子类型**

select * from INSTANCEOF java.util.Vector



## 05-JProfiler

### 基本概述

#### 介绍

在运行Java的时候有时候想测试运行时占用内存情况，这时候就需要使用测试工具查看了。在eclipse里面有Eclipse Memory Analyzer too1(MAT)插件可以测试，而在IDEA中也有这么一个插件，就是JProfiler。

JProfiler是由 ej-technologies公司开发的一款Java应用性能诊断工具。功能强大，但是收费。

官网下载地址: https://www.ej-technologies.com/products/jprofiler/overview.html

#### 特点

- 使用方便、界面操作友好(简单且强大)
- 对被分析的应用影响小(提供模板).
- CPU, Thread , Memory分析功能尤其强大
- 支持对jdbc , noSql, jsp, servlet，socket等进行分析·
- 支持多种模式(离线，在线)的分析
- 支持监控本地、远程的JVM
- 跨平台,拥有多种操作系统的安装版本

![image-20210208162615426](.\images\image-20210208162615426.png)

#### 主要功能

**1-方法调用**

对方法调用的分析可以帮助您了解应用程序正在做什么，并找到提高其性能的方法

**2-内存分配**

通过分析堆上对象、引用链和垃圾收集能帮您修复内存泄漏问题，优化内存使用

**3-线程和锁**

JProfiler提供多种针对线程和锁的分析视图助您发现多线程问题

**4-高级子系统**

许多性能问题都发生在更高的语义级别上。例如，对于JDBC调用，您可能希望找出执行最慢的SQL语句。JProfiler支持对这些子系统进行集成分析





### 安装与配置

#### 下载与安装

https://www.ej-technologies.com/products/jprofiler/overview.html

![image-20210208163319510](.\images\image-20210208163319510.png)



#### JProfiler中配置IDEA







#### IDEA集成JProfiler

**方式一:**

![image-20210208163808437](.\images\image-20210208163808437.png)



![image-20210208163836616](.\images\image-20210208163836616.png)

**方式二:**

从官网下载插件

官方下载地址:https://plugins.jetbrains.com/plugin/253-jprofiler

![image-20210208163729401](.\images\image-20210208163729401.png)

![image-20210208163957080](.\images\image-20210208163957080.png)

### 具体使用 

```java
/**
 * -Xms600m -Xmx600m -XX:SurvivorRatio=8
 * @author shkstart  shkstart@126.com
 * @create 2020  21:12
 */
public class OOMTest {
    public static void main(String[] args) {
        ArrayList<Picture> list = new ArrayList<>();
        while(true){
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            list.add(new Picture(new Random().nextInt(100 * 50)));
        }
    }
}

class Picture{
    private byte[] pixels;

    public Picture(int length) {
        this.pixels = new byte[length];
    }
}
```





#### 直接IDEA中打开时候

![image-20210208164539852](.\images\image-20210208164539852.png)



![image-20210208164645599](.\images\image-20210208164645599.png)

![image-20210208164551434](.\images\image-20210208164551434.png)

![image-20210208164828261](.\images\image-20210208164828261.png)





#### 直接通过双击Jprofile软件打开时

![image-20210208170203418](.\images\image-20210208170203418.png)

```java
分析一个Demo 或者之前保存过的 即之前分析完保存下来再次打开
分析正在运行的JVM进程
分析一个应用，本地或者远程的
打开一个快照，譬如堆快照，保存出去了也可以在这里离线打开
```

**根据自己需要选择一个时**

提醒选择用数据采样方式

![image-20210208170358166](.\images\image-20210208170358166.png)





#### 数据采集方式



**Instrumentation重构模式**

**Sampling抽样模式**



JProfier数据采集方式分为两种:Sampling(样本采集)和Instrumentation（重构模式). 

- Instrumentation:这是Profiler全功能模式。在class加载之前，JProfier把相关功能代码写入到需要分析的class的bytecode中，对正在运行的jvm有一定影响。
  - 优点:功能强大。在此设置中,调用堆栈信息是准确的。
  - 缺点:若要分析的class较多,则对应用的性能影响较大,CPU开销可能很高(取决于Filter的控制)。因此使用此模式一般配合Filter使用,只对特定的类或包进行分析。
- SampLing:类似于样本统计，每隔一定时间(5ms)将每个线程栈中方法栈中的信息统计出来。
  - 优点:对CPU的开销非常低，对应用影响小(即使你不配置任何Filter)·
  - 缺点:一些数据/特性不能提供(例如:方法的调用次数、执行时间)



注: JProfiler本身没有指出数据的采集类型，这里的采集类型是针对方法调用的采集类型。因为JProfiler的绝大多数核心功能都依赖方法调用采集的数据，所以可以直接认为是JProfiler的数据采集类型。

整体概述



#### 遥感监测TelemetriesL

**概述性内容**

![image-20210208172615645](.\images\image-20210208172615645.png)



![image-20210208172321162](.\images\image-20210208172321162.png)

![image-20210208172839718](.\images\image-20210208172839718.png)



![image-20210208173018067](.\images\image-20210208173018067.png)



![image-20210208173158975](.\images\image-20210208173158975.png)

![image-20210208173308777](.\images\image-20210208173308777.png)





#### 内存视图Live Memory

Live memory内存剖析:class/class instance的相关信息。例如对象的个数，大小，对象创建的方法执行栈，对象创建的热点。

- 所有对象Al1 0bjects

显示所有加载的类的列表和在堆上分配的实例数。只有Java 1.5 (JVMTI)才会显示此视图。

![image-20210208173836644](.\images\image-20210208173836644.png)

- 记录对象 Record 0bjects

查看特定时间段对象的分配，并记录分配的调用堆栈。

- 分配访问树Allocation Call Tree

显示一棵请求树或者方法、类、包或对已选择类有带注释的分配信息的J2EE组件。

- 分配热点Allocation Hot Spots

显示一个列表，包括方法、类、包或分配已选类的J2EE组件。你可以标注当前值并且显示差异值。对于每个热点都可以显示它的跟踪记录树。

- 类追踪器Class Tracker

类跟踪视图可以包含任意数量的图表，显示选定的类和包的实例与时间。



![image-20210208180309120](.\images\image-20210208180309120.png)



![image-20210208180731706](.\images\image-20210208180731706.png)



#### 堆遍历heap walker

- 类classes

显示所有类和它们的实例，可以右击具体的类"Used Selected Instance"实现进一步跟踪。

- 分配 Allocations

为所有记录对象显示分配树和分配热点。

- 索引 References

为单个对象和“显示到垃圾回收根目录的路径”提供索引图的显示功能。还能提供合并输入视图和输出视图的功能。

- 时间 Time

显示一个对已记录对象的解决时间的柱状图。

- 检查Inspections

显示了一个数量的操作，将分析当前对象集在某种条件下的子集，实质是一个筛选的过程。

- 图表Graph

你需要在references视图和biggest视图手动添加对象到图表，它可以显示对象的传入和传出引用，能方便的找到垃圾收集器根源。

Ps:在工具栏点击"Go To Start"可以使堆内存重新计数，也就是回到初始状态。





![image-20210208180921087](.\images\image-20210208180921087.png)



![image-20210208182256584](.\images\image-20210208182256584.png)

![image-20210208182328898](.\images\image-20210208182328898.png)

![image-20210208182522635](.\images\image-20210208182522635.png)

![image-20210208182855927](.\images\image-20210208182855927.png)



#### cpu视图cpu views

JProfiler提供不同的方法来记录访问树以优化性能和细节。线程或者线程组以及线程状况可以被所有的视图选择。所有的视图都可以聚集到方法、类、包或J2EE组件等不同层上。

- 访问树 Call Tree

显示一个积累的自顶向下的树，树中包含所有在JVM中已记录的访问队列。JDBC,JMS和NDI服务请求都被注释在请求树中。请求树可以根据Servlet和SP对URL的不同需要进行拆分。

- 热点 Hot Spots

显示消耗时间最多的方法的列表。对每个热点都能够显示回溯树。该热点可以按照方法请求，JDBC，JMS和JNDI服务请求以及按照URL请求来进行计算。

- 访问图 Call Graph

显示一个从已选方法、类、包或J2EE组件开始的访问队列的图。

- 方法统计Method Statistis

显示一段时间内记录的方法的调用时间细节。



![image-20210208185055115](.\images\image-20210208185055115.png)



![image-20210208184944656](.\images\image-20210208184944656.png)

#### 线程视图threads

JProfiler通过对线程历史的监控判断其运行状态，并监控是否有线程阻塞产生，还能将一个线程所管理的方法以树状形式呈现。对线程剖析。

- 线程历史Thread History

显示一个与线程活动和线程状态在一起的活动时间表。

- 线程监控Thread Monitor

显示一个列表，包括所有的活动线程以及它们目前的活动状况。

- 线程转储Thread Dumps

显示所有线程的堆栈跟踪。

线程分析主要关心三个方面:

1. web容器的线程      最大数。比如:Tomcat的线程容量应该略大于最大并发数。
2. 线程阻塞
3. 线程死锁



![image-20210208185822165](.\images\image-20210208185822165.png)



![image-20210208185758898](.\images\image-20210208185758898.png)



#### 监视器&锁Monitors&locks

**监控和锁Monitors &Locks**所有线程持有锁的情况以及锁的信息。

观察JVM的内部线程并查看状态:

- 死锁探测图表 Current Locking Graph :显示JVM中的当前死锁图表。

- 目前使用的监测器Current Monitors :显示目前使用的监测器并且包括它们的关联线程。

- 锁定历史图表 Locking History Graph :显示记录在JVM中的锁定历史。

- 历史检测记录 Monitor History :显示重大的等待事件和阻塞事件的历史记录。

- 监控器使用统计 Monitor Usage Statistics :显示分组监测，线程和监测类的统计监测数据







### 案例分析

```java
public class MemoryLeak {

    public static void main(String[] args) {
        while (true) {
            ArrayList beanList = new ArrayList();
            for (int i = 0; i < 500; i++) {
                Bean data = new Bean();
                data.list.add(new byte[1024 * 10]);//10kb
                beanList.add(data);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

class Bean {
    int size = 10;
    String info = "hello,atguigu";
 //    ArrayList list = new ArrayList();
   static ArrayList list = new ArrayList();  //使用静态集合，对象不会回收掉，
}
```

![image-20210208194651327](.\images\image-20210208194651327.png)

![image-20210208194801752](.\images\image-20210208194801752.png)



## 06-Arthas

### 基本概述

#### 背景

前面，我们介绍了jdk自带的jvgualvm等免费工具，以及商业化工具Jprofiler。

**jvisualvm界面**



![image-20210208194933377](.\images\image-20210208194933377.png)

**Jprofiler**

![image-20210208195201854](.\images\image-20210208195201854.png)



这两款工具在业界知名度也比较高，他们的优点是可以图形界面上看到各维度的性能数据，使用者根据这些数据进行综合分析，然后判断哪里出现了性能问题。

但是这两款工具也有个**缺点**，都必须在服务端项目进程中配置相关的监控参数。然后工具通过远程连接到项目进程，获取相关的数据。这样就会带来一些不便，比如线上环境的网络是隔离的，本地的监控工具根本连不上线上环境。并且类似于Jprofiler这样的商业工具，是需要付费的。

那么有没有一款工具不需要远程连接，也不需要配置监控参数，同时也提供了丰富的性能监控数据呢?

今天跟大家介绍一款**阿里巴巴开源的性能分析神器Arthas（阿尔萨斯）**


#### 概述



![image-20210208202812078](.\images\image-20210208202812078.png)



Arthas（阿尔萨斯)是Alibaba开源的Java诊断工具，深受开发者喜爱⑩在线排查问题，无需重斥;动态跟踪Java代码;实时监控JVM状态。

Arthas支持JDK 6+，支持Linux/Mac/windows，采用命令行交互模式，同时提供丰富的Tab自动补全功能，进一步方便进行问题的定位和诊断。

当你遇到以下类似问题而束手无策时，Arthas可以帮助你解决:

- 这个类从哪个jar包加载的?为什么会报各种类相关的Exception?·

- 我改的代码为什么没有执行到?难道是我没commit?分支搞错了?

- 遇到问题无法在线上 debug，难道只能通过加日志再重新发布吗?

- 线上遇到某个用户的数据处理有问题，但线上同样无法 debug，线下无法重现!·

- 是否有一个全局视角来查看系统的运行状况?

- 有什么办法可以监控到JVM的实时运行状态?

#### 基于哪些工具开发而来

- greys-anatomy: **Arthas代码基于Greys二次开发而来**，非常感谢Greys之前所有的工作，以及Greys原作者对Arthas提出的意见和建议!

- termd: **Arthas的命令行实现基于termd开发**，是一款优秀的命令行程序开发框架，感谢termd提供了优秀的框架。

- crash: **Arthas的文本渲染功能基于crash中的文本渲染功能开发，**可以从这里看到源码，感谢crash在这方面所做的优秀工作。

- cli: **Arthas的命令行界面**基于vert.x提供的cli库进行开发，感谢vert.x在这方面做的优秀工作。

- compiler Arthas里的**内存编绎器代码**来源

- Apache Commons Net Arthas里的Telnet Client代码来源

- JavaAgent:运行在 main方法之前的**拦截器，**它内定的方法名叫 premain ，也就是说先执行premain 方法然后再执行main方法

- ASM:一个通用的Java字节码操作和分析框架。它可以用于修改现有的类或直接以二进制形式动态生成类。ASM提供了一些常见的字节码转换和分析算法，可以从它们构建定制的复杂转换和代码分析工具。ASM提供了与其他Java字节码框架类似的功能，但是主要关注性能。因为它被设计和实现得尽可能小和快，所以非常适合在动态系统中使用(当然也可以以静态方式使用，例如在编译器中)



#### 官方使用文档

https://arthas.aliyun.com/zh-cn/



### 安装与使用

#### 安装

**安装方式一**:可以直接在Linux上通过命令下载

可以在官方Github 上进行下载，如果速度较慢，可以尝试国内的码云 Gitee下载。. 

- github下载

wget https://alibaba.github.io/arthas/arthas-boot.jar. 

- Gitee下载

wget https://arthas.gitee.io/arthas-boot.jar

**安装方式二:**

也可以在浏览器直接访问https://alibaba.github.io/arthas/arthas-boot.jar，等待下载成功后，上传到Linux服务器上。

![image-20210208203433863](.\images\image-20210208203433863.png)

**卸载:|**

在 LinuxlUnix/Mac 平台

删除下面文件:

```shell
rm -rf ~/ .arthas/

rm -rf ~/logs/arthas
```

windows平台直接删除user home下面的.arthas和logs/arthas目录

#### 工程目录

arthas-agent:基于JavaAgent技术的代理

bin:一些启动脚本

arthas-boot: Java版本的一键安装启动脚本

arthas-client: telnet client代码

arthas-common:一些共用的工具类和枚举类

arthas-core:核心库，各种arthas命令的交互和实现

arthas-demo:示例代码

arthas-memorycompiler:内存编绎器代码，Fork from https://github.com/skalogs/SkaETL/tree/master/compiler

arthas-packaging: maven打包相关的

arthas-site: arthas站点

arthas-spy:编织到目标类中的各个切面

static:静态资源

arthas-testcase:测试

#### 启动

Arthas只是一个java程序，所以可以直接用java -jar运行。

执行成功后，arthas提供了一种命令行方式的交互方式，arthas会检测当前服务器上的Java进程，并将进程列表展示出来，用户输入对应的编号（1、2、3、4…）进行选择，然后回车。

**比如:方式1:**



```shell
java -jar arthas-boot.jar

#选择进程(输入[]内编号(不是PID)回车)

[INFO] arthas-boot version: 3.1.4
[INFO] Found existing java process,please choose one and hit RETURN.
[1]: 11616 com.Arthas
[2]: 8676
[3]: 16200 org.jetbrains.jps.cmdline.Launcher
[4]: 21032 org.jetbrains.idea.maven.server.RemoteMavenServer
```

**方式2:运行时选择 Java 进程PID**

> java -jar arthas-boot.jar [PID]

#### 查看日志
```jaca
cat ~/logs/arthas/arthas.log
```

#### 参看帮助

```shell
java -jar arthas-boot.jar -h
```

#### web console

除了在命令行查看外，Arthas目前还支持 web Console。在成功启动连接进程之后就已经自动启动，可以直接访问http://127.0.0.1:8563/访问，页面上的操作模式和控制台完全一样。

#### 退出

- 使用quit\exit:  退出当前客户端
- 使用stop\shutdown:  关闭arthas服务端,并退出所有客户端。



### 相关诊断指令

#### 基础指令

- help――查看命令帮助信息

- cat——打印文件内容，和linux里的cat命令类似

- echo-打印参数，和linux里的ec   ho命令类似

- grep——匹配查找，和linux里的grep命令类似

- tee――复制标准输入到标准输出和指定的文件，和linux里的tee命令类似

- pwd—―返回当前的工作目录，和linux命令类似

- cls——清空当前屏幕区城

- session—―查看当前会话的信息

- reset--重置增强类，将被Arthas增强过的类全部还原，Arthas服务端关闭时会重置所有增强过的类

- version—―输出当前目标Java进程所加载的Arthas版本号

- history——打印命令历史

- quit—―退出当前Arthas客户端，其他Arthas客户端不受影响

- stop—―关闭Arthas服务端，所有Arthas客户端全部退出

- keymap——Arthas快捷键列表及自定义快捷键



#### jvm相关

```java
/**
 * -Xms600m -Xmx600m -XX:SurvivorRatio=8
 */
public class OOMTest {
    public static void main(String[] args) {
        ArrayList<Picture> list = new ArrayList<>();
        while(true){
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            list.add(new Picture(new Random().nextInt(100 * 50)));
        }
    }
}

class Picture{
    private byte[] pixels;

    public Picture(int length) {
        this.pixels = new byte[length];
    }

    public byte[] getPixels() {
        return pixels;
    }

    public void setPixels(byte[] pixels) {
        this.pixels = pixels;
    }
}
```







![image-20210209120534829](.\images\image-20210209120534829.png)



- dashboard—-当前系统的实时数据面板

```shell
# 实时数据面板每间隔一段时间就会打印信息

dashboard -i 500 # 500毫秒打印一次
dashboard -i 1000 -n 4 # 1000毫秒打印一次 打印4次
```



![image-20210209121505663](.\images\image-20210209121505663.png)

- thread—-查看当前JVM的线程堆栈信息

![image-20210209123526223](.\images\image-20210209123526223.png)

```shell
[arthas@4941]$ thread -h # 查看帮助

[arthas@4941]$ thread 1 # 查看1号线程
"main" Id=1 TIMED_WAITING
    at java.lang.Thread.sleep(Native Method)
    at com.atguigu.java.monitor03.arthas.OOMTest.main(OOMTest.java:16)

[arthas@4941]$ thread -b # 查看阻塞线程
No most blocking thread found!


[arthas@4941]$ thread -i 5000  # 5000豪秒刷新一次CPU利用率

[arthas@4941]$ thread -n 2  # 输出CPU占用率前2位线程

```

- jvm—查看当前JVM的信息

- sysprop—查看和修改JVM的系统属性

![image-20210209124108493](.\images\image-20210209124108493.png)

- sysenv—查看JVM的环境变量

![image-20210209124132582](.\images\image-20210209124132582.png)

- vmoption—查看和修改JVM里诊断相关的option

- perfcounter―查看当前JVM的Perf Counter信息

- logger―查看和修改logger

- getstatic―查看类的静态属性

- ognl―执行ognl表达式

- mbean—查看Mbean的信息

- heapdump—dump java heap,类似jmap命令的heap dump功能

```shell
[arthas@4941]$ heapdump -h 

heapdump --live
heapdump --live /tmp/dump.hprof

```







![image-20210209124248411](.\images\image-20210209124248411.png)





#### class/classloader

- sc—查看JVM已加载的类信息

```java
sc命令:查看JVM 已加载的类信息
https://arthas.aliyun.com/doc/sc
·常用参数:
sc -h 
class-pattern类名表达式匹配
-d  输出当前类的详细信息，包括这个类所加载的原始文件来源、类的声明、加载的ClassLoader等详细信息。如果一个类被多个ClassLoader所加载，则会出现多次
-E  开启正则表达式匹配，默认为通配符匹配
d -f 
-x  指定输出静态变量时属性的遍历深度，默认为0，即直接使用toString输出

·补充:

1. class-pattern支持全限定名，如com.test.AAA，也支持com/test/AAA这样的格式，这样，我们从异常堆栈里面把类名拷贝过来的时候，不需要在手动把/替换为.了。
2. sc 默认开启了子类匹配功能，也就是说所有当前类的子类也会被搜索出来，想要精确的匹配，请打开options disable-sub-class true开关
```

![image-20210209130815849](.\images\image-20210209130815849.png)

- sm—查看已加载类的方法信息

```java
sm命令:查看已加载类的方法信息
. https://arthas.aliyun.com/doc/sm
. sm命令只能看到由当前类所声明(declaring)的方法，父类则无法看到。
· 常用参数:

class-pattern  类名表达式匹配
method-pattern  方法名表达式匹配
-d  展示每个方法的详细信息
-E  开启正则表达式匹配，默认为通配符匹配

```

 ![image-20210209131513303](.\images\image-20210209131513303.png)



- jad—反编译指定已加载类的源码

```java
jad命令:反编译指定已加载类的源码
    
https://arthas.aliyun.com/doc/jad
在 Arthas Console 上，反编译出来的源码是带语法高亮的，阅读更方便
当然，反编译出来的 java 代码可能会存在语法错误，但不影响你进行阅读理解·

```

![image-20210209131834683](.\images\image-20210209131834683.png)



- mc―内存编译器，内存编译.java文件为.class文件

```java
mc命令: Memory Compiler/内存编译器，编译.java文件生成.class
https://arthas.aliyun.com/doc/mc

mc /tmp/Test.java
```

- redefine—加载外部的.class文件，redefine到JVM里

```shell
redefine命令:加载外部的.class文件，redefine jvm已加载的类。
https://arthas.aliyun.com/doc/redefine
    
# 推荐使用retransform命令    
redefine /tmp/Test.class
redefine -c 327a647b /tmp/Test.class /tmp/Test\$Inner.class

```

![image-20210209132731495](.\images\image-20210209132731495.png)

- retransform—加载外部的.class文件，retransform到JVM里

- dump—dump 已加载类的 byte code到特定目录

- classloader—查看classloader的继承树，urls，类加载信息，使用classloader去getResource

```java
classloader命令:查看classloader的继承树，urls，类加载信息 
https://larthas.aliyun.com/doc/classloader

了解当前系统中有多少类加载器，以及每个加载器加载的类数量，帮助您判断是否有类加载器泄漏。
    
·常用参数:
classloader -h
-t:查看classLoader的继承树
-l:按类加载实例查看统计信息
-c :用classloader对应的hashcode来查看对应的jar urls

```

![image-20210209133538579](.\images\image-20210209133538579.png)

#### manitor/watch/trace相关

- monitor—方法执行监控

```java
monitor命令:方法执行监控
    
对匹配 class-pattern / method-pattern的类、方法的调用进行监控。涉及方法的调用次数、执行时间、失败率等
https://arthas.aliyun.com/doc/monitor.
monitor命令是一个非实时返回命令
    
·常用参数:

class-pattern 类名表达式匹配
method-pattern 方法名表达式匹配
-c 统计周期，默认值为120秒 
```

![image-20210209134206914](.\images\image-20210209134206914.png)



- watch—方法执行数据观测

```java
watch命令:方法执行数据观测
    
 https://arthas.aliyun.com/doc/watch
·让你能方便的观察到指定方法的调用情况。能观察到的范围为:返回值、抛出异常、入参，通过编写groovy表达式进行对应变量的查看。
    
·常用参数:

class-pattern 类名表达式匹配
method-pattern 方法名表达式匹配
express 观察表达式
    
condition-express条件表达式
    -b在方法调用之前观察(默认关闭)
    -e在方法异常之后观察(默认关闭)
    -s在方法返回之后观察(默认关闭)
    -f 在方法结束之后(正常返回和异常返回)观察（默认开启)
    -x指定输出结果的属性遍历深度，默认为0
    
#cost方法执行耗时
    
·说明:这里重点要说明的是观察表达式，观察表达式的构成主要由 ognl 表达式组成，所以你可以这样写"{params , return0bj}"，只要是一个合法的 ognl表达式，都能被正常支持。
    
举例:watch全限定类名方法名 returnObj

```

![image-20210209134743212](.\images\image-20210209134743212.png)

```shell
watch com.atguigu.java.monitor03.arthas.Picture <init> "{params,returnObj}" -x 2

method=com.atguigu.java.monitor03.arthas.Picture.<init> location=AtExit
ts=2021-02-08 22:00:09; [cost=0.049544ms] result=@ArrayList[
    @Object[][
        @Integer[2243],
    ],
    null,
]
method=com.atguigu.java.monitor03.arthas.Picture.<init> location=AtExit
ts=2021-02-08 22:00:09; [cost=0.050055ms] result=@ArrayList[
    @Object[][
        @Integer[1952],
    ],
    null,
]
watch com.atguigu.java.monitor03.arthas.Picture <init> "{params,returnObj}"
method=com.atguigu.java.monitor03.arthas.Picture.<init> location=AtExit
ts=2021-02-08 22:02:23; [cost=0.024416ms] result=@ArrayList[
    @Object[][isEmpty=false;size=1],
    null,
]
method=com.atguigu.java.monitor03.arthas.Picture.<init> location=AtExit
ts=2021-02-08 22:02:23; [cost=0.030417ms] result=@ArrayList[
    @Object[][isEmpty=false;size=1],
    null,
]

```

- trace—方法内部调用路径，并输出方法路径上的每个节点上耗时

```java
trace命令:方法内部调用路径，输出方法路径上的每个节点上耗时 
https://arthas.aliyun.com/doc/trace

补充说明:

. trace命令能主动搜索class-pattern / method-pattern对应的方法调用路径，渲染和统计整个调用链路上的所有性能开销和追踪调用链路。
    
trace 能方便的帮助你定位和发现因RT高而导致的性能问题缺陷，但其每次只能跟踪一级方法的调用链路
    
.trace在执行的过程中本身是会有一定的性能开销，在统计的报告中并未像JProfiler一样预先减去其自身的统计开销。所以这统计出来有些许的不准，渲染路径上调用的类、方法越多，性能偏差越大。但还是能让你看清一些事情的。
    
·参数说明
    trace -h 
class-pattern类名表达式匹配
    method-pattern方法名表达式匹配
    condition-express条件表达式
   -n命令执行次数
    
#cost方法执行耗时

```

![image-20210209140704236](.\images\image-20210209140704236.png)

- stack―输出当前方法被调用的调用路径

```java
stack命令:输出当前方法被调用的调用路径. 
https://arthas.aliyun.com/doc/stack

·常用参数
    class-pattern 类名表达式匹配
    method-pattern 方法名表达式匹配
    condition-express 条件表达式
    -n 执行次数限制
    
#cost方法执行耗时
    ·举例:

```

![image-20210209140908738](.\images\image-20210209140908738.png)



- tt一方法执行数据的时空隧道，记录下指定方法每次调用的入参和返回信息，并能对这些不同的时间下调用进行观测

```java
tt命令:方法执行数据的时空隧道，记录下指定方法每次调用的入参和返回信息，并能对这些不同的时间下调用进行观测。
    
https://larthas.aliyun.com/doc/tt

·TimeTunnel的缩写
·常用参数:

-t 表明希望记录下类~*Test 的print方法的每次执行情况。
    
-n 3 指定你需要记录的次数，当达到记录次数时Arthas会主动中断tt命令的记录过程，避免人工操作无法停止的情况。
    
-s 筛选指定方法的调用信息
    
-i 参数后边跟着对应的 INDEX 编号查看到它的详细信息
    
-p 重做一次调用通过--replay-times指定调用次数，通过--replay-interval指定多次调用间隔(单位ms，默认1000ms)

```

![image-20210209141306388](.\images\image-20210209141306388.png)



#### 其他

使用>将结果重写到日志文件，使用&指令命令是后台运行，session断开不影响任务执行（生命周期默认为1天)

- jobs:列出所有job

- kill:强制终止任务

- fg:将暂停的任务拉到前台执行

- bg:将暂停的任务放到后台执行

- grep:搜索满足条件的结果

- plaintext:将命令的结果去除ANSI颜色

- wc:按行统计输出结果

- options:查看或设置Arthas全局开关

- profiler:使用async-profiler对应用采样，生成火焰图

- options

 

- profiler/火焰图

```shell
profiler start 
profiler getSamples 
profiler status 
profilter stop
profilter stop --format html
profilter stop --file /tmp/output.svg # 默认放在 /tmp/demo/arthas-output/202101123-123456.svg

# 默认情况下，arthas使用3658端口，则可以打开: http://localhost:3658/arthas-output/查看到arthas-output目录下面的profiler结果:

```

```shell
[arthas@6418]$ profiler start
Perf events unavailable. See stderr of the target process.
将/proc/sys/kernel/perf_event_paranoid的值设置为1。
echo 1 > /proc/sys/kernel/perf_event_paranoid
```

![image-20210209151804128](.\images\image-20210209151804128.png)



![image-20210209151832104](.\images\image-20210209151832104.png)





## 07-Java Mission Control

### 历史

在0racle 收购 Sun之前，0racle 的JRockit虚拟机提供了一款叫做JRockitMission Control 的虚拟机诊断工具。\

在Oracle收购Sun之后，Oracle公司同时拥有了Sun Hotspot和Rockit两款虚拟机。根据Oracle对于Java的战略，在今后的发展中，会将JRockit的优秀特性移植到Hotspot上。其中，一个重要的改进就是在Sun的JDK中加入了JRockit的支持。

在0racle JDK 7u40之后，Mission Control这款工具已经绑定在0racle JDK中发布。

自Java 11开始，本节介绍的JFR已经开源。但在之前的 Java 版本，JFR属于Commercial Feature(商业特性)，需要通过Java 虚拟机参数-XX:+UnlockCommercialFeatures开启

如果你有兴趣请可以查看openJDK的Mission Control项目。
https://github.com/JDKMissionControl/jmc



### 启动

![image-20210209152953654](.\images\image-20210209152953654.png)



**因为配置过环境变量，所以直接命令行 jmc即可**

![image-20210209153002300](.\images\image-20210209153002300.png)

**首页打开会有界面说明**

![image-20210209152617789](.\images\image-20210209152617789.png)

![image-20210209152549985](.\images\image-20210209152549985.png)





### 概述

Java Mission Control(简称JMC) ,Java官方提供的性能强劲的工具。是一个用于对Java应用程序进行管理、监视、概要分析和故障排除的工具套件。

它包含一个GUI 客户端，以及众多用来收集 Java 虚拟机性能数据的插件，如JMX Console(能够访问用来存放虚拟机各个子系统运行数据的MBeans)，以及虚拟机内置的高效profiling 工具Java Flight Recorder (JFR）。

JMC的另一个优点就是:采用取样，而不是传统的代码植入技术，对应用性能的影响非常非常小，完全可以开着JMC 来做压测（唯一影响可能是 full gc多了）。





### 功能:实时监控VM运行时的状态

如果是远程服务器，使用前要开JMX。

- -Dcom. sun . management.jmxremote.port=${YOUR PORT}

- -Dcom. sun .management .jmxremote

- -Dcom.sun.management .jmxremote.authenticate=false

- -Dcom.sun.management .jmxremote.ssl=false

- -Djava.rmi.server.hostname=${YOUR HOST/IP}

文件->连接->创建新连接，填入上面JMX参数的 host和 port

![image-20210209153928907](.\images\image-20210209153928907.png)



![image-20210209154011759](.\images\image-20210209154011759.png)

![image-20210209154032480](.\images\image-20210209154032480.png)



### Java Flight Recorder

Java Flight Recorder是 JMC的其中一个组件。

Java Flight Recorder能够以极低的性能开销收集Java 虚拟机的性能数据。
JFR 的性能开销很小，在默认配置下平均低于1%。与其他工具相比，JFR能够直接访问虚拟机内的数据，并且不会影响虚拟机的优化。因此，它非常适用于生产环境下满负荷运行的Java程序。

Java Flight Recorder和JDK Mission Control共同创建了一个完整的工具链。JDK Mission Control可对Java Flight Recorder连续收集低水平和详细的运行时信息进行高效，详细的分析。

#### 事件类型

当启用时，JFR将记录运行过程中发生的一系列事件。其中包括 Java层面的事件，如线程事件、锁事件，以及 Java 虚拟机内部的事件，如新建对象、垃圾回收和即时编译事件。

按照发生时机以及持续时间来划分，JFR的事件共有四种类型，它们分别为以下四种。

1. 瞬时事件（Instant Event），用户关心的是它们发生与否，例如异常、线程启动事件。
2. 持续事件（Duration Event），用户关心的是它们的持续时间，例如垃圾回收事件
3. 计时事件（ Timed Event），是时长超出指定阈值的持续事件。
4. 取样事件（ Sample Event），是周期性取样的事件。

取样事件的其中一个常见例子便是方法抽样（Method Sampling)，即每隔一段时间统计各个线程的栈轨迹。如果在这些抽样取得的栈轨迹中存在一个反复出现的方法，那么我们可以推测该方法是热点方法。



#### 启动方式

##### 方式1:使用-XX:StartFlightRecording=参数

第一种是在运行目标 Java 程序时添加-XX:StartFlightRecording=参数。

比如:下面命令中，JFR 将会在Java虚拟机启动5s后（对应delay=5s）收集数据，持续20s(对应duration=20s）。当收集完毕后，JFR 会将收集得到的数据保存至指定的文件中（对应filename=myrecording.jfr)

java -XX:StartFlightRecording=delay=5s,duration=20s,filename=myrecording.jfr,settings=profile MyApp

由于JFR将持续收集数据，如果不加以限制，那么JFR可能会填满硬盘的所有空间。因此，我们有必要对这种模式下所收集的数据进行限制。
比如: java -XX:StartFlightRecording=maxage=10m,maxsize=100m, name=SomeLabel MyApp





##### 方式2 :使用jcmd的JFR.*子命令

通过jcmd来让JFR开始收集数据、停止收集数据，或者保存所收集的数据，对应的子命令分别为JFR.start，JFR.stop，以及pFR.dump。

```java
$ jcmd <PID> JFR.start settings=profile maxage=10m maxsize=150m name=SomeLabel

上述命令运行过后，目标进程中的JFR已经开始收集数据。此时，我们可以通过下述命令来导出已经收集到的数据:
$ jcmd <PID>JFR.dump name=SomeLabel filename=myrecording.jfr
    
最后，我们可以通过下述命令关闭目标进程中的 JFR:
$ jcmd <PID>JFR.stop name=SomeLabel
```

##### 方式3:JMC的JFR插件团

直接在图形化界面操作

![image-20210209160010180](.\images\image-20210209160010180.png)





#### Java Flight Recorder 取样分析

要采用取样,必须先添加参数:

- -XX:+UnlockCommercialFeatures

- -XX:+FlightERecorder

**否则:**

![image-20210209154751830](.\images\image-20210209154751830.png)





- 加上对象数量的统计: Java Virtual Machine -> GC -Detailed -> 0bjectCount/0bject Count after GC

- 方法调用采样的间隔从10ms改为1ms(但不能低于1ms，否则会影响性能了): Java Virtual Machine -> Profiling ->Method Profiling Sample/Method Sampling Information

- Socket 与 File采样，10ms太久，但即使改为1ms也未必能抓住什么，可以干脆取消掉: Java Application->File Read/Filewrite/Socket Read/Socket write



![image-20210209160904079](.\images\image-20210209160904079.png)

![image-20210209160815888](.\images\image-20210209160815888.png)



![image-20210209161203099](.\images\image-20210209161203099.png)

![image-20210209161449325](.\images\image-20210209161449325.png)

然后就开始 Profile，到时间后Profile结束，会自动把记录下载回来，在JMC 中展示。

![image-20210209161652749](.\images\image-20210209161652749.png)

从展示信息中，我们大致可以读到内存和CPU信息、代码、线程和IO等比较重要的信息展示。



## 08-其它工具



### Flame Graphs(火焰图)

在追求极致性能的场景下,了解你的程序运行过程中cpu在干什么很重要，火焰图就是一种非常直观的展示cpu在程序整个生命周期过程中时间分配的工具。

火焰图对于现代的程序员不应该陌生，这个工具可以非常直观的显示出调用栈中的CPU消耗瓶颈。

网上的关于java火焰图的讲解大部分来自于Brendan Gregg的博客:http://www.brendangregg.com/flamegraphs.html



![image-20210209161926677](.\images\image-20210209161926677.png)

火焰图﹐简单通过x轴横条宽度来度量时间指标，y轴代表线程栈的层次。



### Tprofiler

- 案例:

使用JDK自身提供的工具进行JVM调优可以将TPS 由2.5提升到20(提升了7倍)，并准确定位系统瓶颈。

系统瓶颈有:应用里静态对象不是太多、有大量的业务线程在频繁创建一些生命周期很长的临时对象，代码里有问题。

那么，如何在海量业务代码里边准确定位这些性能代码?这里使用阿里开源工具TProfiler来定位这些性能代码，成功解决掉了GC过于频繁的性能瓶颈，并最终在上次优化的基础上将TPS 再提升了4倍，即提升到100。

- TProfiler 配置部署、远程操作、日志阅读都不太复杂，操作还是很简单的。但是其却是能够起到一针见血、立竿见影的效果，帮我们解决了GC过于频繁的性能瓶颈。

- TProfiler最重要的特性就是能够统计出你指定时间段内JVM的 top method，这些 topmethod极有可能就是造成你JVM性能瓶颈的元凶。这是其他大多数JVM 调优工具所不具备的，包括 JRockit Mission Control。JRokit首席开发者 Marcus Hirt在其私人博客《Low Overhead Method Profiling with Java Mission Control》下的评论中曾明确指出了RMC并不支持TOP方法的统计。
- TProfiler的下载: https://lgithub.com/alibaba/TProfilerl

### Btrace

**Java运行时追踪工具**

常见的动态追踪工具有BTrace、HouseMD（该项目己经停止开发）、Greys-Anatomy(国人开发，个人开发者)、Byteman (JBoss出品），注意Java运行时追踪工具并不限于这几种，但是这几个是相对比较常用的。

BTrace是SUN Kenai云计算开发平台下的一个开源项目，旨在为java提供安全可靠的动态跟踪分析工具。先看一下BTrace的官方定义:

BTrace is a safe，dynamic tracing tool for the Java platform. BTrace can beused to dynamically trace a running Java program (similar to DTrace for
OpenSolaris applications and 0S). BTrace dynamically instruments the classesof the target application to inject tracing code ("bytecode tracing”)。

简洁明了，大意是一个Java平台的安全的动态追踪工具。可以用来动态地追踪一个运行的Java程序。BTrace动态调整目标应用程序的类以注入跟踪代码（“字节码跟踪”）。

### YourKit

### JProbe

### Spring lnsight
