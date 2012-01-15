package org.rendershark.example.action;

import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.rendershark.core.HandlerResult;
import org.rendershark.core.HttpGetHandler;
import org.rendersnake.DocType;
import org.rendersnake.HtmlCanvas;

@Singleton @Named("/doc.xml")
public class XmlDocumentAction implements HttpGetHandler {

    @Override
    public HandlerResult get(HtmlCanvas xml) throws IOException {
        
        // inline page
        xml.render(DocType.XML_1_0);
        xml.tag("sample",xml.attributes().add("key", "value"));
        xml.close();
        
        return HandlerResult.ok().addHeader("Content-Type", "application/xml");
    }
}
