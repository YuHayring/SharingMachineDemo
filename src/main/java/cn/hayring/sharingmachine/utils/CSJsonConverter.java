package cn.hayring.sharingmachine.utils;

import cn.hayring.sharingmachine.csjson.CSJson;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class CSJsonConverter implements Converter<String, CSJson> {

    private Gson gson;

    private final Logger logger = LoggerFactory.getLogger(CSJsonConverter.class);

    @Override
    public CSJson convert(String s) {
        CSJson csJson = gson.fromJson(s, CSJson.class);
        logger.debug("Convert " + s.substring(0, 10) + "... to " + csJson.toString());
        return csJson;
    }

    @Autowired
    public void setGson(Gson gson) {
        this.gson = gson;
    }
}
