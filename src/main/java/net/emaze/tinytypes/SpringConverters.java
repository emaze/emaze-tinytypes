/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.emaze.tinytypes;

import java.lang.reflect.Constructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;

/**
 *
 * @author rferranti
 */
public class SpringConverters {

    public static class StringToIntTinyTypeConverterFactory implements ConverterFactory<String, IntTinyType> {

        @Override
        public <T extends IntTinyType> Converter<String, T> getConverter(Class<T> targetType) {
            final Constructor c = TinyTypesReflector.ctor(targetType);
            return (String source) -> TinyTypesReflector.create(targetType, c, Integer.parseInt(source));
        }
    }

    public static class StringToLongTinyTypeConverterFactory implements ConverterFactory<String, LongTinyType> {

        @Override
        public <T extends LongTinyType> Converter<String, T> getConverter(Class<T> targetType) {
            final Constructor c = TinyTypesReflector.ctor(targetType);
            return (String source) -> TinyTypesReflector.create(targetType, c, Long.parseLong(source));
        }

    }

    public static class StringToStringTinyTypeConverterFactory implements ConverterFactory<String, StringTinyType> {

        @Override
        public <T extends StringTinyType> Converter<String, T> getConverter(Class<T> targetType) {
            final Constructor c = TinyTypesReflector.ctor(targetType);
            return (String source) -> TinyTypesReflector.create(targetType, c, source);
        }
    }

    
    public static void register(FormatterRegistry registry){
        registry.addConverterFactory(new StringToLongTinyTypeConverterFactory());
        registry.addConverterFactory(new StringToIntTinyTypeConverterFactory());
        registry.addConverterFactory(new StringToStringTinyTypeConverterFactory());
    }
}
