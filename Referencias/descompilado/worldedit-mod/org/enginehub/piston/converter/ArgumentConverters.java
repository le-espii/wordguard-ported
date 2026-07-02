/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableList
 *  com.google.common.reflect.TypeToken
 */
package org.enginehub.piston.converter;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.enginehub.piston.converter.ArgumentConverter;
import org.enginehub.piston.converter.ConversionResult;
import org.enginehub.piston.converter.Converter;
import org.enginehub.piston.converter.FailedConversion;
import org.enginehub.piston.converter.SimpleArgumentConverter;
import org.enginehub.piston.converter.SuccessfulConversion;
import org.enginehub.piston.inject.InjectedValueAccess;
import org.enginehub.piston.util.CaseHelper;

public class ArgumentConverters {
    private static final ArgumentConverter<String> STRING_ARGUMENT_CONVERTER = SimpleArgumentConverter.from((s, c) -> SuccessfulConversion.fromSingle(s), "any text");
    private static final String CONVERTER_CONVERT = "convert";
    private static final MethodType HANDLE_TO_CONVERTER = MethodType.methodType(Converter.class, MethodHandle.class);
    private static final MethodType CONVERTER_SIG = MethodType.methodType(ConversionResult.class, String.class, InjectedValueAccess.class);
    private static final MethodHandle HANDLE_TO_CONVERTER_CONVERTER;
    private static final MethodHandle SUCCESSFUL_CONVERSION_FROM;
    private static final MethodHandle FAILED_CONVERSION_FROM;
    private static final List<ACProvider<Object>> PROVIDERS;

    public static ArgumentConverter<String> forString() {
        return STRING_ARGUMENT_CONVERTER;
    }

    private static <T> Optional<ArgumentConverter<T>> valueOfConverters(TypeToken<T> type) {
        MethodHandle handle;
        Class c = type.wrap().getRawType();
        try {
            handle = MethodHandles.publicLookup().findStatic(c, "valueOf", MethodType.methodType(c, String.class));
        }
        catch (IllegalAccessException | NoSuchMethodException e) {
            return Optional.empty();
        }
        handle = ArgumentConverters.noContextConverter(handle);
        return Optional.of(ArgumentConverters.converterForHandle(handle, c));
    }

    private static <T> Optional<ArgumentConverter<T>> constructorConverters(TypeToken<T> type) {
        MethodHandle handle;
        Class c = type.wrap().getRawType();
        try {
            handle = MethodHandles.publicLookup().findConstructor(c, MethodType.methodType(Void.TYPE, String.class));
        }
        catch (IllegalAccessException | NoSuchMethodException e) {
            return Optional.empty();
        }
        handle = ArgumentConverters.noContextConverter(handle);
        return Optional.of(ArgumentConverters.converterForHandle(handle, c));
    }

    private static MethodHandle noContextConverter(MethodHandle noContextHandle) {
        return MethodHandles.dropArguments(noContextHandle, 1, InjectedValueAccess.class);
    }

    private static <T> SimpleArgumentConverter<T> converterForHandle(MethodHandle handle, Class<?> type) {
        Converter converter;
        MethodType mType = handle.type();
        Preconditions.checkArgument((mType.parameterType(0).isAssignableFrom(String.class) && mType.parameterType(1).isAssignableFrom(InjectedValueAccess.class) && type.isAssignableFrom(mType.returnType()) ? 1 : 0) != 0, (String)"Incorrect signature: %s", (Object)handle);
        handle = ArgumentConverters.augmentHandleForConversionResult(handle);
        try {
            converter = HANDLE_TO_CONVERTER_CONVERTER.invokeExact(handle);
        }
        catch (Throwable throwable) {
            Throwables.throwIfUnchecked((Throwable)throwable);
            throw new RuntimeException(throwable);
        }
        return SimpleArgumentConverter.from(converter, "any " + CaseHelper.titleToSpacedLower(type.getSimpleName()));
    }

    private static MethodHandle augmentHandleForConversionResult(MethodHandle delegate) {
        MethodHandle result = MethodHandles.filterReturnValue(delegate, SUCCESSFUL_CONVERSION_FROM.asType(MethodType.methodType(ConversionResult.class, delegate.type().returnType())));
        result = MethodHandles.catchException(result, Throwable.class, FAILED_CONVERSION_FROM.asType(MethodType.methodType(ConversionResult.class, Throwable.class)));
        return result;
    }

    public static <T> ArgumentConverter<T> get(TypeToken<T> type) {
        if (type.getRawType().equals(String.class)) {
            ArgumentConverter<String> stringConv = ArgumentConverters.forString();
            return stringConv;
        }
        TypeToken raw = type;
        ArgumentConverter result = PROVIDERS.stream().map(x -> x.provideAc(raw)).filter(Optional::isPresent).map(Optional::get).findAny().orElseThrow(() -> new IllegalArgumentException("No built-in converters for " + type));
        return result;
    }

    private ArgumentConverters() {
    }

    static {
        MethodHandle handleInvoker = MethodHandles.invoker(CONVERTER_SIG);
        try {
            HANDLE_TO_CONVERTER_CONVERTER = LambdaMetafactory.metafactory(MethodHandles.lookup(), CONVERTER_CONVERT, HANDLE_TO_CONVERTER, CONVERTER_SIG, handleInvoker, CONVERTER_SIG).dynamicInvoker();
        }
        catch (LambdaConversionException e) {
            throw new IllegalStateException("Failed to load ArgumentConverter MetaFactory", e);
        }
        try {
            SUCCESSFUL_CONVERSION_FROM = MethodHandles.lookup().findStatic(SuccessfulConversion.class, "fromSingle", MethodType.methodType(SuccessfulConversion.class, Object.class));
            FAILED_CONVERSION_FROM = MethodHandles.lookup().findStatic(FailedConversion.class, "from", MethodType.methodType(FailedConversion.class, Throwable.class));
        }
        catch (IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
        PROVIDERS = ImmutableList.of(ArgumentConverters::valueOfConverters, ArgumentConverters::constructorConverters, type -> {
            if (Objects.equals(type.wrap().getRawType(), Character.class)) {
                return Optional.of(SimpleArgumentConverter.from((s, c) -> SuccessfulConversion.fromSingle(Character.valueOf(s.charAt(0))), "any character"));
            }
            return Optional.empty();
        });
    }

    private static interface ACProvider<T> {
        public Optional<ArgumentConverter<T>> provideAc(TypeToken<T> var1);
    }
}

