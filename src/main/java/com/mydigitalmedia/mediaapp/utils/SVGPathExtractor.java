package com.mydigitalmedia.mediaapp.utils;

import com.mydigitalmedia.mediaapp.MainApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SVGPathExtractor {

    public static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    public static String extractSVGPath(String filePath) {

        try {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(filePath);
            NodeList pathNodes = doc.getElementsByTagName("path");
            Element pathElement = (Element) pathNodes.item(0);

            return pathElement.getAttribute("d");

        } catch (Exception e) {
            logger.error("Error extracting SVG path from file: " + filePath);
            return null;
        }
    }
}
