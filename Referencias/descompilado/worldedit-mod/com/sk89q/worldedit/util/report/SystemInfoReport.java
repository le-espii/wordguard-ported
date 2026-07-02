/*
 * Decompiled with CFR 0.152.
 */
package com.sk89q.worldedit.util.report;

import com.sk89q.worldedit.util.report.DataReport;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SystemInfoReport
extends DataReport {
    public SystemInfoReport() {
        super("System Information");
        Runtime runtime = Runtime.getRuntime();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        ClassLoadingMXBean classLoadingBean = ManagementFactory.getClassLoadingMXBean();
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        this.append("Java", "%s %s (%s)", System.getProperty("java.vendor"), System.getProperty("java.version"), System.getProperty("java.vendor.url"));
        this.append("Operating System", "%s %s (%s)", System.getProperty("os.name"), System.getProperty("os.version"), System.getProperty("os.arch"));
        this.append("Available Processors", runtime.availableProcessors());
        this.append("Free Memory", runtime.freeMemory() / 1024L / 1024L + " MB");
        this.append("Max Memory", runtime.maxMemory() / 1024L / 1024L + " MB");
        this.append("Total Memory", runtime.totalMemory() / 1024L / 1024L + " MB");
        this.append("System Load Average", osBean.getSystemLoadAverage());
        this.append("Java Uptime", TimeUnit.MINUTES.convert(runtimeBean.getUptime(), TimeUnit.MILLISECONDS) + " minutes");
        DataReport startup = new DataReport("Startup");
        startup.append("Input Arguments", runtimeBean.getInputArguments());
        this.append(startup.getTitle(), startup);
        DataReport vm = new DataReport("Virtual Machine");
        vm.append("Name", runtimeBean.getVmName());
        vm.append("Vendor", runtimeBean.getVmVendor());
        vm.append("Version", runtimeBean.getVmVendor());
        this.append(vm.getTitle(), vm);
        DataReport spec = new DataReport("Specification");
        spec.append("Name", runtimeBean.getSpecName());
        spec.append("Vendor", runtimeBean.getSpecVendor());
        spec.append("Version", runtimeBean.getSpecVersion());
        this.append(spec.getTitle(), spec);
        DataReport classLoader = new DataReport("Class Loader");
        classLoader.append("Loaded Class Count", classLoadingBean.getLoadedClassCount());
        classLoader.append("Total Loaded Class Count", classLoadingBean.getTotalLoadedClassCount());
        classLoader.append("Unloaded Class Count", classLoadingBean.getUnloadedClassCount());
        this.append(classLoader.getTitle(), classLoader);
        DataReport gc = new DataReport("Garbage Collectors");
        for (GarbageCollectorMXBean bean : gcBeans) {
            DataReport thisGC = new DataReport(bean.getName());
            thisGC.append("Collection Count", bean.getCollectionCount());
            thisGC.append("Collection Time", bean.getCollectionTime() + "ms");
            gc.append(thisGC.getTitle(), thisGC);
        }
        this.append(gc.getTitle(), gc);
        DataReport threads = new DataReport("Threads");
        for (ThreadInfo threadInfo : threadBean.dumpAllThreads(false, false)) {
            threads.append("#" + threadInfo.getThreadId() + " " + threadInfo.getThreadName(), (Object)threadInfo.getThreadState());
        }
        this.append(threads.getTitle(), threads);
    }
}

