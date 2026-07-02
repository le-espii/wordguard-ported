/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  java.lang.StackWalker
 *  java.lang.StackWalker$StackFrame
 */
package com.sk89q.worldedit.internal.util;

import com.google.common.base.Joiner;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.internal.util.NonAbstractForCompatibility;
import com.sk89q.worldedit.world.block.BlockCategories;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

public class DeprecationUtil {
    private DeprecationUtil() {
    }

    public static void checkDelegatingOverride(Class<?> implementingClass) {
        Method deprecatedMethod;
        StackWalker.StackFrame caller = (StackWalker.StackFrame)((Optional)StackWalker.getInstance().walk(s -> s.skip(1L).findFirst())).orElseThrow(() -> new AssertionError((Object)"No caller found"));
        Method callingMethod = DeprecationUtil.getCallingMethod(caller);
        NonAbstractForCompatibility annotation = callingMethod.getAnnotation(NonAbstractForCompatibility.class);
        try {
            deprecatedMethod = implementingClass.getMethod(annotation.delegateName(), annotation.delegateParams());
        }
        catch (NoSuchMethodException e) {
            throw new AssertionError("Missing method referenced by " + String.valueOf(NonAbstractForCompatibility.class), e);
        }
        if (deprecatedMethod.getDeclaringClass().getName().equals(caller.getClassName())) {
            throw new IllegalStateException("Class " + implementingClass.getName() + " must override " + DeprecationUtil.methodToString(callingMethod));
        }
    }

    private static Method getCallingMethod(StackWalker.StackFrame callerInfo) {
        Method[] declaredMethods;
        try {
            declaredMethods = Class.forName(callerInfo.getClassName()).getDeclaredMethods();
        }
        catch (ClassNotFoundException e) {
            throw new AssertionError("Caller class missing?", e);
        }
        for (Method declaredMethod : declaredMethods) {
            if (!declaredMethod.isAnnotationPresent(NonAbstractForCompatibility.class) || !declaredMethod.getName().equals(callerInfo.getMethodName())) continue;
            return declaredMethod;
        }
        throw new IllegalStateException("Failed to find caller method " + callerInfo.getMethodName() + " annotated with " + String.valueOf(NonAbstractForCompatibility.class));
    }

    private static String methodToString(Method method) {
        StringBuilder builder = new StringBuilder(method.getDeclaringClass().getCanonicalName()).append('.').append(method.getName()).append('(');
        Joiner.on((String)", ").appendTo(builder, Stream.of(method.getParameterTypes()).map(Class::getSimpleName).iterator());
        builder.append(')');
        return builder.toString();
    }

    public static boolean isSign(BlockType blockType) {
        BlockType sign = BlockTypes.SIGN;
        BlockType wallSign = BlockTypes.WALL_SIGN;
        return blockType == sign || blockType == wallSign || BlockCategories.SIGNS.contains(blockType);
    }

    public static String getHeadOwnerKey() {
        int dataVersion = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getDataVersion();
        return dataVersion >= 2566 ? "SkullOwner" : "Owner";
    }
}

