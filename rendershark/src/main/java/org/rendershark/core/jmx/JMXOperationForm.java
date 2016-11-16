package org.rendershark.core.jmx;

import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ObjectName;
import java.io.IOException;

import static org.rendersnake.HtmlAttributesFactory.*;

public class JMXOperationForm implements Renderable {

    private ObjectName oname;
    private MBeanOperationInfo oinfo;

    public JMXOperationForm(ObjectName oname, MBeanOperationInfo oinfo) {
        super();
        this.oname = oname;
        this.oinfo = oinfo;
    }

    @Override
    public void renderOn(HtmlCanvas html) throws IOException {
        String id = html.nextId();
        boolean hasArguments = oinfo.getSignature().length > 0;
        if (hasArguments) {
            html.form(id(id + ".form").method("post"));
            html.table();
            html.tr().td()
                    .input(class_("operation").type("submit").name("call").value(oinfo.getName()))
                    .input(type("hidden").name("oname").value(oname.getCanonicalName()))
                    ._td()._tr();
            int index = 0;
            for (MBeanParameterInfo each : oinfo.getSignature()) {
                html.tr().td(valign("top"));
                html.write(each.getName()).write("=");
                html.input(type("text").name("arg" + index));
                html.input(type("hidden").name("type" + index).value(each.getType()));
                html.write("(").write(each.getType()).write(")");
                html._td()._tr();
                index++;
            }
            html._table();
            html._form();
        } else {
            html.a(class_("operation").onClick("javascript:perform_operation("
                    + oname.getCanonicalName()
                    + ","
                    + oinfo.getName()
                    + ","
                    + id
                    + ");"))
                    .content(oinfo.getName());
        }
        // result container
        html.div(id(id))._div();

    }

}
