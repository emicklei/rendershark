package org.rendershark.core.jmx;

import static org.rendersnake.HtmlAttributesFactory.NO_ESCAPE;
import static org.rendersnake.HtmlAttributesFactory.class_;
import static org.rendersnake.HtmlAttributesFactory.href;
import static org.rendersnake.HtmlAttributesFactory.id;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;
import org.rendersnake.StringResource;

public class JMXControlPage implements Renderable {

    public HashSet<String> includePrefixes = new HashSet<String>();
    public HashSet<String> excludePrefixes = new HashSet<String>();
    
    private static final String M_BEAN_DESCRIPTION = "Dynamic MBean Description";
    @Override
    public void renderOn(HtmlCanvas html) throws IOException {
        html.style().content(StringResource.get("jmx.css"));
        try {
            MBeanServer mServer = ManagementFactory.getPlatformMBeanServer();
            html.div(id("accordion"));
            for (ObjectInstance object : this.sortedMBeans(mServer.queryMBeans(new ObjectName("*:*"), null))) {
                ObjectName oname = object.getObjectName();
                if (this.canShowBean(oname.getCanonicalName())) {
                    html.h4().a(href("#")).content(oname.getCanonicalName())._h4();                   
                    html.div();
                        renderAttributesAndOperationsOn(mServer, oname, html);
                    html._div();                   
                }
            }
            html._div(); // accordion
        } catch (Exception e) {
            e.printStackTrace();
        }
        html.script().content(StringResource.get("jmx.js"),NO_ESCAPE);
    }

    private boolean canShowBean(String name) {
        for (String each : excludePrefixes) {
            if (name.startsWith(each))
                return false;
        }
        for (String each : includePrefixes) {
            if (name.startsWith(each))
                return true;
        }
        return false;
    }
    
    private List<ObjectInstance> sortedMBeans(Set<ObjectInstance> mbeans) {
        List<ObjectInstance> sorted = new ArrayList<ObjectInstance>(mbeans);
        Collections.sort(sorted, new Comparator<ObjectInstance>() {

            @Override
            public int compare(ObjectInstance o1, ObjectInstance o2) {
                return o1.getObjectName().compareTo(o2.getObjectName());
            }
        });
        return sorted;
    }
    
    private void renderAttributesAndOperationsOn(MBeanServer mServer, ObjectName oname, HtmlCanvas html) throws Exception {
        MBeanInfo minfo = mServer.getMBeanInfo(oname);
        this.renderMBeanDescriptionOn(mServer, oname, html);
        html.table();
        for (MBeanAttributeInfo ainfo : this.sortAttributes(minfo.getAttributes())) {
            this.renderAttributeOn(mServer, oname, ainfo, html);
        }
        html._table();
        
        html.table();
        int cols = 4;
        int c = 0;
        boolean tropen = false;
        for (MBeanOperationInfo oinfo : minfo.getOperations()) {
            if (c % cols == 0) {
                if (tropen) html._tr();
                html.tr();
                tropen = true;
            }
            html.td();
            html.render(new JMXOperationForm(oname, oinfo));
            html._td();
            c++;
        }        
        if (tropen) html._tr();
        html._table();
    }
    
    private MBeanAttributeInfo[] sortAttributes(MBeanAttributeInfo[] attributes) {
        Arrays.sort(attributes , new Comparator<MBeanAttributeInfo>() {@Override
            public int compare(MBeanAttributeInfo o1, MBeanAttributeInfo o2) {
                return o1.getName().compareTo(o2.getName());
            }});
        return attributes;
    }

    private void renderAttributeOn(MBeanServer mServer, ObjectName oname, MBeanAttributeInfo ainfo, HtmlCanvas html) throws IOException {
        if (M_BEAN_DESCRIPTION.equals(ainfo.getName())) return;
        html.tr();
        String value= null;
        try { 
            Object any = mServer.getAttribute(oname, ainfo.getName());
            if (any instanceof Object[]) {
                StringBuilder sb = new StringBuilder();
                Object[] array = (Object[]) any;
                for (Object each : array) {
                    if (sb.length() != 0) sb.append(",");
                    sb.append(each);
                }
                value = sb.toString();
            } else {
                value = String.valueOf(any);
            }
        } catch (Exception ex) {
            value = "** error **";
        }
        html.td(class_("aname")).content(ainfo.getName());
        if (value.length() > 100) {
            value = value.substring(0,100) + "...";
        }
        html.td(class_("avalue")).content(value);
        html._tr();
    }

    private void renderMBeanDescriptionOn(MBeanServer mServer, ObjectName oname, HtmlCanvas html) throws Exception {
        String desc = "";
        try  {
            desc = mServer.getAttribute(oname,M_BEAN_DESCRIPTION).toString();
            if (desc == null) {
                desc = oname.toString();
            }
        } catch (Exception ex) {
            
        }
        html.p(class_("mdesc")).content(desc);
    }
}
