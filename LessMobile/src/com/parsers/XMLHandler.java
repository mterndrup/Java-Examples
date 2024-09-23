package com.parsers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
public class XMLHandler  extends DefaultHandler {
	private Channel channel;
    private Items items;
    private Item item;
    private boolean inItem = false;

    private StringBuilder content;
    
    

    public Items getItems() {
		return items;
	}

	public XMLHandler() {
        items = new Items();
        content = new StringBuilder();
    }

    public void startElement(String uri, String localName, String qName, 
            Attributes atts) throws SAXException {
        content = new StringBuilder();
        if(localName.equalsIgnoreCase("channel")) {
            channel = new Channel();
        } else if(localName.equalsIgnoreCase("item")) {
            inItem = true;
            item = new Item();
        }
    }

    public void endElement(String uri, String localName, String qName) 
            throws SAXException {
        if(localName.equalsIgnoreCase("title")) {
            if(inItem) {
                item.setTitle(content.toString());
            } else {
                channel.setTitle(content.toString());
            }
        } else if(localName.equalsIgnoreCase("link")) {
            if(inItem) {
                //item.setLink(content.toString());
            } else {
                //channel.setLink(content.toString());
            }
        } else if(localName.equalsIgnoreCase("description")) {
            if(inItem) {
                item.setDescription(content.toString());
            } else {
                //channel.setDescription(content.toString());
            }
        } else if(localName.equalsIgnoreCase("lastBuildDate")) {
            //channel.setLastBuildDate(content.toString());
        } else if(localName.equalsIgnoreCase("docs")) {
            //channel.setDocs(content.toString());
        } else if(localName.equalsIgnoreCase("language")) {
            //channel.setLanguage(content.toString());
        } else if(localName.equalsIgnoreCase("item")) {
            inItem = false;
            items.add(item);
        } else if(localName.equalsIgnoreCase("channel")) {
            channel.setItems(items);
        }
    }

    public void characters(char[] ch, int start, int length) 
            throws SAXException {
        content.append(ch, start, length);
    }

    public void endDocument() throws SAXException {
        // you can do something here for example send
        // the Channel object somewhere or whatever.
    }
}