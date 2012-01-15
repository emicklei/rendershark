package org.rendershark.test;

import javax.inject.Named;

import org.junit.Test;
import org.rendershark.core.jmx.JMXAction;

public class JMXTest {
    @Test
    public void testChangeAnnotationValue() throws Exception {
        Named elm = JMXAction.class.getAnnotation(Named.class);
        System.out.println(elm.value());
    }
}
