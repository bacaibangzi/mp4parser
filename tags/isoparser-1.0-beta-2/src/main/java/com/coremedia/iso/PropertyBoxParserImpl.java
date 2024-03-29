package com.coremedia.iso;

import com.coremedia.iso.boxes.AbstractBox;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.UserBox;
import com.coremedia.iso.boxes.fragment.MovieFragmentBox;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Property file based BoxFactory
 */
public class PropertyBoxParserImpl extends AbstractBoxParser {
    Properties mapping;

    public PropertyBoxParserImpl() {
        InputStream is = getClass().getResourceAsStream("/default.properties");
        mapping = new Properties();
        try {
            mapping.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PropertyBoxParserImpl(Properties mapping) {
        this.mapping = mapping;
    }

    Pattern p = Pattern.compile("(.*)\\((.*?)\\)");

    @Override
    public AbstractBox createBox(byte[] type, byte[] userType, byte[] parent, Box lastMovieFragmentBox) {

        String constructor = mapping.getProperty(IsoFile.bytesToFourCC(parent) + "-" + IsoFile.bytesToFourCC(type));
        if (constructor == null) {
            constructor = mapping.getProperty(IsoFile.bytesToFourCC(type));
        }
        if (constructor == null) {
            constructor = mapping.getProperty("default");
        }
        if (constructor == null) {
            throw new RuntimeException("No box object found for " + IsoFile.bytesToFourCC(type));
        }
        Matcher m = p.matcher(constructor);
        boolean matches = m.matches();
        if (!matches) {
            throw new RuntimeException("Cannot work with that constructor: " + constructor);
        }
        String clazzName = m.group(1);
        String[] param = m.group(2).split(",");
        try {
            if (param[0].trim().length() == 0) {
                param = new String[]{};
            }
            Class clazz = Class.forName(clazzName);

            Class[] constructorArgsClazz = new Class[param.length];
            Object[] constructorArgs = new Object[param.length];
            for (int i = 0; i < param.length; i++) {

                if ("userType".equals(param[i])) {
                    constructorArgs[i] = userType;
                    constructorArgsClazz[i] = byte[].class;
                } else if ("type".equals(param[i])) {
                    constructorArgs[i] = type;
                    constructorArgsClazz[i] = byte[].class;
                } else if ("parent".equals(param[i])) {
                    constructorArgs[i] = parent;
                    constructorArgsClazz[i] = byte[].class;
                } else if ("lastMovieFragmentBox".equals(param[i])) {
                    constructorArgs[i] = lastMovieFragmentBox;
                    constructorArgsClazz[i] = MovieFragmentBox.class;
                } else {
                    throw new InternalError("No such param: " + param[i]);
                }


            }
            Constructor<AbstractBox> constructorObject;
            try {
                if (param.length > 0) {
                    constructorObject = clazz.getConstructor(constructorArgsClazz);
                } else {
                    constructorObject = clazz.getConstructor();
                }

                return constructorObject.newInstance(constructorArgs);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }


        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
