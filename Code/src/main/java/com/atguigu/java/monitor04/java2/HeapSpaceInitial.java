package com.atguigu.java.monitor04.java2;

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