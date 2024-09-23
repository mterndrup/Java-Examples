package com.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

public class XML_Channel implements Serializable {

	private Channel channel;
    private Items items;
    private Item item;

    public XML_Channel() {
        items = new Items();
    }

    public Channel parse(InputStream is) {
        RootElement root = new RootElement("rss");
        Element chanElement = root.getChild("channel");
        Element chanTitle = chanElement.getChild("title");
        Element chanLink = chanElement.getChild("link");
        Element chanDescription = chanElement.getChild("description");
        Element chanLastBuildDate = chanElement.getChild("lastBuildDate");
        Element chanDocs = chanElement.getChild("docs");
        Element chanLanguage = chanElement.getChild("language");

        Element chanItem = chanElement.getChild("item");
        Element itemTitle = chanItem.getChild("title");
        Element itemDescription = chanItem.getChild("description");
        Element itemLink = chanItem.getChild("link");

        chanElement.setStartElementListener(new StartElementListener() {
            public void start(Attributes attributes) {
                channel = new Channel();
            }
        });

        // Listen for the end of a text element and set the text as our
        // channel's title.
        chanTitle.setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                channel.setTitle(body);
            }
        });

        // Same thing happens for the other elements of channel ex.

        // On every <item> tag occurrence we create a new Item object.
        chanItem.setStartElementListener(new StartElementListener() {
            public void start(Attributes attributes) {
                item = new Item();
            }
        });

        // On every </item> tag occurrence we add the current Item object
        // to the Items container.
        chanItem.setEndElementListener(new EndElementListener() {
            public void end() {
                items.add(item);
            }
        });

        itemTitle.setEndTextElementListener(new EndTextElementListener() {
            public void end(String body) {
                item.setTitle(body);
            }
        });

        // and so on

        // here we actually parse the InputStream and return the resulting
        // Channel object.
        try {
            Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
            return channel;
        } catch (SAXException e) {
            // handle the exception
        } catch (IOException e) {
            // handle the exception
        }

        return null;
    }

}
