package cn.hayring.sharingmachine.utils;

import cn.hayring.sharingmachine.csjson.CSJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

public class CSJsonConversionService implements ConversionService {

    @Autowired
    private CSJsonConverter csJsonConverter;


    @Override
    public boolean canConvert(Class<?> source, Class<?> target) {
        if (source == null || target == null) {
            return false;
        }
        return CharSequence.class.isAssignableFrom(source) && CSJson.class.isAssignableFrom(target);
    }

    @Override
    public boolean canConvert(TypeDescriptor source, TypeDescriptor target) {
        return canConvert(source.getType(), target.getType());
    }

    @Override
    public <T> T convert(Object o, Class<T> aClass) {
        if (canConvert(o.getClass(), aClass.getClass())) {
            return (T) csJsonConverter.convert((String) o);
        } else {
            return null;
        }
    }

    @Override
    public Object convert(Object o, TypeDescriptor source, TypeDescriptor target) {
        if (canConvert(source, target)) {
            return csJsonConverter.convert((String) o);
        } else {
            return null;
        }
    }
}
