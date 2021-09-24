---
title: 1-JVM与Java体系结构
tags:
notebook: 宋红康-JVM
---

### 1. 前言

<img src=https://p.pstatp.com/origin/pgc-image/1dd85871d94d47e4a65c3bb1b5e9ce64 width="70%">

大部分 Java 开发人员，除会在项目中使用到与 Java 平台相关的各种高精尖技术，对于 Java 技术的核心 Java 虚拟机了解甚少。

一些有一定工作经验的开发人员，打心眼儿里觉得 SSM、微服务等上层技术才是重点，基础技术并不重要，这其实是一种本末倒置的“病态”。如果我们把核心类库的 API 比做数学公式的话，那么 Java 虚拟机的知识就好比公式的推导过程。

计算机系统体系对我们来说越来越远，在不了解底层实现方式的前提下，通过高级语言很容易编写程序代码。但事实上计算机并不认识高级语言

<img src=https://p.pstatp.com/origin/pgc-image/404a8894adbd47e4bbb3c159b386a1ec width="70%">

### 2. 为什么要学习 JVM

- 面试的需要（BATJ、TMD，PKQ 等面试都爱问）
- 中高级程序员必备技能

  - 项目管理、调优的需求

- 追求极客的精神
  - 比如：垃圾回收算法、JIT（及时编译器）、底层原理

### 3. Java vs C++

垃圾收集机制为我们打理了很多繁琐的工作，大大提高了开发的效率，但是，垃圾收集也不是万能的，懂得 JVM 内部的内存结构、工作机制，是设计高扩展性应用和诊断运行时问题的基础，也是 Java 工程师进阶的必备能力。

<img src=https://p.pstatp.com/origin/pgc-image/b53d0fffce334da99b607a429da847f1 width="60%">

C 语言需要自己来分配内存和回收内存，Java 全部交给 JVM 进行分配和回收。

### 4. Java 生态圈

Java 是目前应用最为广泛的软件开发平台之一。随着 Java 以及 Java 社区的不断壮大 Java 也早已不再是简简单单的一门计算机语言了，它更是一个平台、一种文化、一个社区。

- 作为一个平台，Java 虚拟机扮演着举足轻重的作用

  - Groovy、Scala、JRuby、Kotlin 等都是 Java 平台的一部分

- 作为灯种文化，Java 几乎成为了“开源”的代名词。

  - 第三方开源软件和框架。如 Tomcat、Struts，MyBatis，Spring 等。
  - 就连 JDK 和 JVM 自身也有不少开源的实现，如 openJDK、Harmony。

- 作为一个社区，Java 拥有全世界最多的技术拥护者和开源社区支持，有数不清的论坛和资料。从桌面应用软件、嵌入式开发到企业级应用、后台服务器、中间件，都可以看到 Java 的身影。其应用形式之复杂、参与人数之众多也令人咋舌。

<img src=https://p.pstatp.com/origin/pgc-image/bbeda3a178744e6097d3e438561936bc width="70%">

每个语言都需要转换成字节码文件，最后转换的字节码文件都能通过 Java 虚拟机进行运行和处理

<img src=https://p.pstatp.com/origin/pgc-image/a22604d61d974cfdae74b2df77cb073b width="70%">

随着 Java7 的正式发布，Java 虚拟机的设计者们通过 JSR-292 规范基本实现在 Java 虚拟机平台上运行非 Java 语言编写的程序。

Java 虚拟机根本不关心运行在其内部的程序到底是使用何种编程语言编写的，它只关心“字节码”文件。也就是说 Java 虚拟机拥有语言无关性，并不会单纯地与 Java 语言“终身绑定”，只要其他编程语言的编译结果满足并包含 Java 虚拟机的内部指令集、符号表以及其他的辅助信息，它就是一个有效的字节码文件，就能够被虚拟机所识别并装载运行。

### 5. 字节码

我们平时说的 java 字节码，指的是用 java 语言编译成的字节码。准确的说任何能在 jvm 平台上执行的字节码格式都是一样的。所以应该统称为：jvm 字节码。

不同的编译器，可以编译出相同的字节码文件，字节码文件也可以在不同的 JVM 上运行。

Java 虚拟机与 Java 语言并没有必然的联系，它只与特定的二进制文件格式—Class 文件格式所关联，Class 文件中包含了 Java 虚拟机指令集（或者称为字节码、Bytecodes）和符号表，还有一些其他辅助信息。

### 6. 虚拟机与 Java 虚拟机

#### 6.1 虚拟机

所谓虚拟机（Virtual Machine），就是一台虚拟的计算机。它是一款软件，用来执行一系列虚拟计算机指令。大体上，虚拟机可以分为系统虚拟机和程序虚拟机。

- 大名鼎鼎的 Visual Box，Mware 就属于系统虚拟机，它们完全是对物理计算机的仿真，提供了一个可运行完整操作系统的软件平台。
- 程序虚拟机的典型代表就是 Java 虚拟机，它专门为执行单个计算机程序而设计，在 Java 虚拟机中执行的指令我们称为 Java 字节码指令。

无论是系统虚拟机还是程序虚拟机，在上面运行的软件都被限制于虚拟机提供的资源中。

#### 6.2 Java 虚拟机

Java 虚拟机是一台执行 Java 字节码的虚拟计算机，它拥有独立的运行机制，其运行的 Java 字节码也未必由 Java 语言编译而成。

JVM 平台的各种语言可以共享 Java 虚拟机带来的跨平台性、优秀的垃圾收集器，以及可靠的即时编译器。

Java 技术的核心就是 Java 虚拟机（JVM，Java Virtual Machine），因为所有的 Java 程序都运行在 Java 虚拟机内部。

Java 虚拟机就是二进制字节码的运行环境，负责装载字节码到其内部，解释/编译为对应平台上的机器指令执行。每一条 Java 指令，Java 虚拟机规范中都有详细定义，如怎么取操作数，怎么处理操作数，处理结果放在哪里。

**特点**：

- 一次编译，到处运行
- 自动内存管理
- 自动垃圾回收功能

#### 6.3 JVM 的位置

JVM 是运行在操作系统之上的，它与硬件没有直接的交互

<img src=https://p.pstatp.com/origin/pgc-image/0ccbe85d42524cd787ceec0f1e2bd981 width="60%">

Java 的体系结构

<img src=https://p.pstatp.com/origin/pgc-image/6ad75cc32bc7459b9249497eab27cb35 width="80%">

#### 6.4 JVM 整体结构

- HotSpot VM 是目前市面上高性能虚拟机的代表作之一。
- 它采用`解释器与即时编译器并存`的架构。
- 在今天，Java 程序的运行性能早已脱胎换骨，已经达到了可以和 C/C++程序一较高下的地步。

<img src=https://p.pstatp.com/origin/pgc-image/976457a0843843f0b890e0994359f909 width="70%">

执行引擎包含三部分：`解释器，及时编译器，垃圾回收器`

#### 6.5 Java 代码执行流程

<img src=https://p.pstatp.com/origin/pgc-image/1b40f806dbe74361baa2ec1c08e2dc69 width="80%">

只是能生成被 Java 虚拟机所能解释的字节码文件，那么理论上就可以自己设计一套代码了

#### 6.6 JVM 的架构模型

Java 编译器输入的指令流基本上是一种`基于栈`的指令集架构，另外一种指令集架构则是基于寄存器的指令集架构。具体来说：这两种架构之间的区别：

**基于栈式架构的特点**

- 设计和实现更简单，适用于资源受限的系统；
- 避开了寄存器的分配难题：使用零地址指令方式分配。
- 指令流中的指令大部分是零地址指令，其执行过程依赖于操作栈。指令集更小，编译器容易实现。
- 不需要硬件支持，可移植性更好，更好实现跨平台

- 跨平台性
- 指令集小
- 指令多
- 执行性能比寄存器差

**基于寄存器架构的特点**

- 典型的应用是 x86 的二进制指令集：比如传统的 PC 以及 Android 的 Davlik 虚拟机。
- 指令集架构则完全依赖硬件，可移植性差
- 性能优秀和执行更高效
- 花费更少的指令去完成一项操作。
- 在大部分情况下，基于寄存器架构的指令集往往都以一地址指令、二地址指令和三地址指令为主，而基于栈式架构的指令集却是以零地址指令为主

##### 6.6.1 举例

同样执行 2+3 这种逻辑操作，其指令分别如下：

基于栈的计算流程（以 Java 虚拟机为例）：

```bash
iconst_2 //常量2入栈
istore_1
iconst_3 // 常量3入栈
istore_2
iload_1
iload_2
iadd //常量2/3出栈，执行相加
istore_0 // 结果5入栈
```

而基于寄存器的计算流程

```bash
mov eax,2 //将eax寄存器的值设为1
add eax,3 //使eax寄存器的值加3
```

#### 6.7 字节码反编译

我们编写一个简单的代码，然后查看一下字节码的反编译后的结果

```java
/**
 * @author: 陌溪
 * @create: 2020-07-04-21:17
 */
public class StackStruTest {
    public static void main(String[] args) {
        int i = 2 + 3;
    }
}
```

然后我们找到编译后的 class 文件，使用下列命令进行反编译

```bash
javap -v StackStruTest.class
```

得到的文件为:

```
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=4, args_size=1
         0: iconst_2
         1: istore_1
         2: iconst_3
         3: istore_2
         4: iload_1
         5: iload_2
         6: iadd
         7: istore_3
         8: return
      LineNumberTable:
        line 9: 0
        line 10: 2
        line 11: 4
        line 12: 8
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       9     0  args   [Ljava/lang/String;
            2       7     1     i   I
            4       5     2     j   I
            8       1     3     k   I
```

#### 6.8 总结

由于跨平台性的设计，Java 的指令都是根据栈来设计的。不同平台 CPU 架构不同，所以不能设计为基于寄存器的。优点是跨平台，指令集小，编译器容易实现，缺点是性能下降，实现同样的功能需要更多的指令。

### 7 JVM 生命周期

#### 7.1 虚拟机的启动

Java 虚拟机的启动是通过引导类加载器（bootstrap class loader）创建一个初始类（initial class）来完成的，这个类是由虚拟机的具体实现指定的。

#### 7.2 虚拟机的执行

- 一个运行中的 Java 虚拟机有着一个清晰的任务：执行 Java 程序。
- 程序开始执行时他才运行，程序结束时他就停止。
- 执行一个所谓的 Java 程序的时候，真真正正在执行的是一个叫做 Java 虚拟机的进程。

#### 7.3 虚拟机的退出

有如下的几种情况：

- 程序正常执行结束
- 程序在执行过程中遇到了异常或错误而异常终止
- 由于操作系统用现错误而导致 Java 虚拟机进程终止
- 某线程调用 Runtime 类或 system 类的 exit 方法，或 Runtime 类的 halt 方法，并且 Java 安全管理器也允许这次 exit 或 halt 操作。
- 除此之外，JNI（Java Native Interface）规范描述了用 JNI Invocation API 来加载或卸载 Java 虚拟机时，Java 虚拟机的退出情况。
