package com.rbf.Document;
/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.File;
import org.dom4j.*;

import java.io.FileReader;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.htmlparser.*;
import org.htmlparser.visitors.*;
import org.htmlparser.util.*;
import org.jdom.input.SAXBuilder;

import java.io.*;

import org.apache.pdfbox.searchengine.lucene.LucenePDFDocument;
import org.apache.poi.hwpf.extractor.*;
import org.apache.poi.hslf.usermodel.*;
import org.apache.poi.hslf.*;
import org.apache.poi.hslf.model.*;
import org.apache.poi.hssf.usermodel.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.dom4j.io.SAXReader;

import com.rbf.Ui.MainFrame;

/** A utility for making Lucene Documents from a File. */

public class FileDocument {
    /** Makes a document for a File.
      <p>
      The document has three fields:  EncodeURI(http://+str)  new String(str.getBytes("ISO-8559-1"),"utf-8");
      <ul>
      <li><code>path</code>--containing the pathname of the file, as a stored,
      untokenized field;
     <li><code>modified</code>--containing the last modified date of the file as
      a field as created by <a
      href="lucene.document.DateTools.html">DateTools</a>; and
      <li><code>contents</code>--containing the full contents of the file, as a
      Reader field;
     */
    public static Document Document(File f) throws java.io.
            FileNotFoundException {

        // make a new, empty document
        Document doc = new Document();
        String[] encoding = {"UTF-8", "GBK", "GB2312", "UTF-8", "ISO8859_1"};
        // Add the path of the file as a field named "path".  Use a field that is
        // indexed (i.e. searchable), but don't tokenize the field into words.
        doc.add(new Field("path", f.getPath(), Field.Store.YES,
                          Field.Index.NOT_ANALYZED));

        // Add the last modified date of the file a field named "modified".  Use
        // a field that is indexed (i.e. searchable), but don't tokenize the field
        // into words.
        doc.add(new Field("modified",
                          DateTools.timeToString(f.lastModified(),
                                                 DateTools.Resolution.MINUTE),
                          Field.Store.YES, Field.Index.NOT_ANALYZED));

        // Add the contents of the file to a field named "contents".  Specify a Reader,
        // so that the text of the file is tokenized and indexed, but not stored.
        // Note that FileReader expects the file to be in the system's default encoding.
        // If that's not the case searching for special characters will fail.
        if (f.getPath().endsWith(".txt")) {
            doc.add(new Field("contents", new FileReader(f)));
        } else if (f.getPath().endsWith(".html") || f.getPath().endsWith(".htm")) {
            int i = 0;
            while (true) {
                try {
                    Parser myParser = new Parser(f.getPath());
                    myParser.setEncoding(encoding[i]);
                    TextExtractingVisitor visitor = new TextExtractingVisitor();
                    myParser.visitAllNodesWith(visitor);
                    doc.add(new Field("contents", visitor.getExtractedText(),
                                      Field.Store.YES, Field.Index.ANALYZED));
                    break;
                } catch (ParserException ex) {
                    System.out.println(ex.getCause() + ex.getMessage());
                    i++;
                    if (i >= 4) {
                        MainFrame.pw.println("errors occur in adding file " +
                                             f.getPath());
                        return null;
                    }
                }
            }
        }else if(f.getPath().endsWith(".xml")){       //add by rbf
        	try{
        		SAXReader reader = new SAXReader();
        		org.dom4j.Document document = reader.read(f);
        		String docXmlText=document.asXML();
        		doc.add(new Field("contents",docXmlText,Field.Store.YES,Field.Index.ANALYZED));
        	}catch( DocumentException ex7){
        		System.out.println("errors occured");
                MainFrame.pw.println("errors occur in adding file " + f.getPath());
                return null;
        	}
        }
        else if (f.getPath().endsWith(".pdf")) {
            try {
                doc = LucenePDFDocument.getDocument(f);
            } catch (IOException ex1) {
                System.out.println("errors occured");
                MainFrame.pw.println("errors occur in adding file " + f.getPath());
                return null;
            }
        } else if (f.getPath().endsWith(".doc")) {
            String bodyText = null;
            InputStream is = new FileInputStream(f.getPath());
            try {
                WordExtractor ex = new WordExtractor(is); //is是WORD文件的InputStream
                bodyText = ex.getText();
                doc.add(new Field("contents", bodyText,
                                  Field.Store.YES, Field.Index.ANALYZED));
            } catch (Exception e) {
                e.printStackTrace();
                MainFrame.pw.println("errors occur in adding file " + f.getPath());
                return null;
            }
        } else if (f.getPath().endsWith(".ppt")) {
            StringBuffer content = new StringBuffer("");
            InputStream is = new FileInputStream(f.getPath());
            try {
                SlideShow ss = new SlideShow(new HSLFSlideShow(is)); //is 为文件的InputStream，建立SlideShow
                Slide[] slides = ss.getSlides(); //获得每一张幻灯片
                for (int i = 0; i < slides.length; i++) {
                    TextRun[] t = slides[i].getTextRuns(); //为了取得幻灯片的文字内容，建立TextRun
                    for (int j = 0; j < t.length; j++) {
                        content.append(t[j].getText()); //这里会将文字内容加到content中去
                        System.out.println(t[j].getText());
                    }
                    content.append(slides[i].getTitle());
                }
                doc.add(new Field("contents", content.toString(),
                                  Field.Store.YES, Field.Index.ANALYZED));
            } catch (Exception ex) {
                System.out.println(ex.toString());
                MainFrame.pw.println("errors occur in adding file " + f.getPath());
                return null;
            }
        } else if (f.getPath().endsWith(".xls")) {
            try {
                StringBuffer content = new StringBuffer("");
                InputStream is = new FileInputStream(f.getPath());
                HSSFWorkbook workbook = new HSSFWorkbook(is); //创建对Excel工作簿文件的引用
                for (int numSheets = 0; numSheets < workbook.getNumberOfSheets(); numSheets++) {
                    if (null != workbook.getSheetAt(numSheets)) {
                        HSSFSheet aSheet = workbook.getSheetAt(numSheets); //获得一个sheet
                        for (Iterator rit = aSheet.rowIterator(); rit.hasNext(); ) {
                                HSSFRow aRow = (HSSFRow)rit.next(); //获得一个行
                                for (Iterator cit = aRow.cellIterator(); cit.hasNext(); ) {
                                        HSSFCell cell = (HSSFCell)cit.next(); //获得列值
                                        switch(cell.getCellType()) {
                                        case HSSFCell.CELL_TYPE_STRING:
                                            content.append(cell.getStringCellValue());
                                            break;
                                        case HSSFCell.CELL_TYPE_NUMERIC:
                                            if (DateUtil.isCellDateFormatted(cell)) {
                                                content.append(cell.getDateCellValue());
                                            } else {
                                                content.append(cell.getNumericCellValue());
                                            }
                                            break;
                                        case HSSFCell.CELL_TYPE_BOOLEAN:
                                            content.append(cell.getBooleanCellValue());
                                            break;
                                        case HSSFCell.CELL_TYPE_FORMULA:
                                            content.append(cell.getCellFormula());
                                            break;
                                        default:
                                            content.append(cell.getRichStringCellValue());
                                        }
                            }
                        }
                    }
                }
                doc.add(new Field("contents", content.toString(),
                                  Field.Store.YES, Field.Index.ANALYZED));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                MainFrame.pw.println("errors occur in adding file " + f.getPath());
                return null;
            }
        }else {
            System.out.println("暂时不支持该类型的文件！");
            //MainFrame.pw.println("暂时不支持该类型的文件" + f.getPath());
            return null;
        }
// return the document
        return doc;
    }

    private FileDocument() {}
}
