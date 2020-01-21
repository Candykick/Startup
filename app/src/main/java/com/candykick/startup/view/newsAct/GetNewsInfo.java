package com.candykick.startup.view.newsAct;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by candykick on 2019. 6. 9..
 */

public class GetNewsInfo extends DefaultHandler {
    private String elementValue = null;
    private Boolean elementOn = false;
    private Boolean isItem = false;
    private ArrayList<NewsData> dataList = new ArrayList<>();
    private NewsData data = null;

    public ArrayList<NewsData> getData()
    {
        return dataList;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        elementOn = true;
        if(localName.equals("item")){
            data = new NewsData();
            data.strImgUrl = null;
            isItem = true;
        } /*else if(isItem && localName.equalsIgnoreCase("content")) {
            data.strImgUrl = attributes.getValue(1);
        }*/
    }

    /**
     * This will be called when the tags of the XML end.
     **/
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if(isItem && localName.equalsIgnoreCase("title")){
            //elementValue = elementValue.replace("<![CDATA[ ","");
            //elementValue = elementValue.replace(" ]]>","");
            data.strTitle = elementValue;
        } else if(isItem && localName.equalsIgnoreCase("link")){
            //elementValue = elementValue.replace("<![CDATA[\n","");
            //elementValue = elementValue.replace("\n]]>","");
            data.strlink = elementValue;
        } else if(isItem && localName.equalsIgnoreCase("description")){
            elementValue = elementValue.replace("<![CDATA[","");
            elementValue = elementValue.replace("<p>","");
            //elementValue = elementValue.replace("![CDATA[\n","");
            //elementValue = elementValue.replace("\n]]>","");
            if(elementValue.length() > 20) {
                data.strContent = elementValue.substring(0, 20) + "...";
            }
            /*if(elementValue.contains("img src=")) {
                int findindex = elementValue.indexOf("hspace='")+12;
                data.strContent = elementValue.substring(findindex, findindex+20)+"...";
            } else {
                data.strContent = elementValue.substring(0,20)+"...";
            }*/
        } else if(isItem && localName.equalsIgnoreCase("encoded")) {
            //Log.d("Startup",elementValue);
            /*int startPosition = elementValue.indexOf("<img");
            int endPosition = elementValue.indexOf("alt=");
            if(startPosition != -1 & endPosition != -1) {
                String strTmp = elementValue.substring(startPosition, endPosition);
                String tmp[] = strTmp.split("\"");
                data.strImgUrl = tmp[3];
            }*/
            if(elementValue.contains("src=\"")) {
                int startPosition = elementValue.indexOf("src=\"") + 4;
                if (startPosition != -1)
                    elementValue = elementValue.substring(startPosition);
                int endPosition = elementValue.indexOf("\"");
                if (startPosition != -1)
                    data.strImgUrl = elementValue.substring(0, endPosition);
            }
        } else if(isItem && localName.equalsIgnoreCase("item")){
            dataList.add(data);
            data = null;
            isItem = false;
        }
    }


    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (elementOn) {
            elementValue = new String(ch, start, length);
            elementOn = false;
        }
    }
}

/*public class GetNewsInfo extends AsyncTask<String, Void, ArrayList<NewsData>> {
    @Override
    protected ArrayList<NewsData> doInBackground(String... params) {
        URL url;
        ArrayList<NewsData> arrayList = null;
        try {
            url = new URL("http://rss.donga.com/economy.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("item");
            for(int i=0; i<nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element)node;

                NodeList title = element.getElementsByTagName("title");
                NodeList link = element.getElementsByTagName("link");
                NodeList description = element.getElementsByTagName("description");
                NewsData data = new NewsData();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }
}*/
