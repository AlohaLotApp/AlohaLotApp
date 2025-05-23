package com.example.alohalotapp.cards;

import android.content.res.AssetManager;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.util.ArrayList;

public class CardList {
    public ArrayList<Card> cards = new ArrayList<>();

    public CardList(AssetManager assets) {
        try {
            InputStream is = assets.open("cards.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("card");

            for (int i = 0; i < nList.getLength(); i++) {
                Element e = (Element) nList.item(i);
                String holder = e.getElementsByTagName("holder").item(0).getTextContent();
                String number = e.getElementsByTagName("number").item(0).getTextContent();
                String expiry = e.getElementsByTagName("expiry").item(0).getTextContent();
                cards.add(new Card(holder, number, expiry));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
