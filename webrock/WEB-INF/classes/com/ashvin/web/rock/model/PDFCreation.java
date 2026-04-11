package com.ashvin.web.rock.model;

//For itextpdf
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.font.*;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.itextpdf.layout.borders.*;
import com.itextpdf.io.image.*;

import java.io.*;

public class PDFCreation
{
public void createPDF(String imageFilePath,String pdfFilePath)
{
System.out.println("PDF Creation called");
int totalSize=5;
System.out.println("Size: "+totalSize);
File file=new File(pdfFilePath);
try
{
if(file.exists()) file.delete();
PdfWriter pdfWriter=new PdfWriter(file.getAbsolutePath());
PdfDocument pdfDocument=new PdfDocument(pdfWriter);
Document document=new Document(pdfDocument);

PdfFont titleFont=PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
PdfFont dataFont=PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

//Header
Paragraph top=new Paragraph();
Image logo=new Image(ImageDataFactory.create(imageFilePath+"student.png"));
logo.scaleToFit(35,35);
logo.setMarginTop(15);
Image correct=new Image(ImageDataFactory.create(imageFilePath+"correct.png"));
correct.scaleToFit(20,20);
correct.setMarginTop(5);
Image incorrect=new Image(ImageDataFactory.create(imageFilePath+"incorrect.png"));
incorrect.scaleToFit(20,20);
incorrect.setMarginTop(5);

top.add(logo);
top.add(new Text(" "));
top.add("WEBROCK DOC").setFont(titleFont).setFontSize(30).setTextAlignment(TextAlignment.JUSTIFIED);
Paragraph title=new Paragraph("WEBROCK Service(s)");
title.setFont(titleFont).setFontSize(20).setTextAlignment(TextAlignment.CENTER);
Text pageNumberText;

float[] columnWidth={1,10};
float[] innerTableColumn={1,3};
Table table=new Table(UnitValue.createPercentArray(columnWidth)).useAllAvailableWidth();
Table innerTable;
Table innerMostTable;

Cell cell0;
Cell cell1;

Cell headerCell0=new Cell().add(new Paragraph("S.No.").setFont(titleFont).setFontSize(18).setBackgroundColor(ColorConstants.BLUE));
Cell headerCell1=new Cell().add(new Paragraph("Service(s)").setFont(titleFont).setFontSize(18).setBackgroundColor(ColorConstants.BLUE)); 

Paragraph creator=new Paragraph("Creator: Ashvin Parmar");
creator.setFont(titleFont).setFontSize(18).setFontColor(ColorConstants.BLACK);

int sno=0;
int pageSize=5;
boolean newPage=true;
int pageNumber=0;
for(int i=0;i<totalSize;i++)
{
if(newPage)
{
document.add(top);
pageNumberText=new Text("Page no: "+String.valueOf(++pageNumber));
pageNumberText.setTextAlignment(TextAlignment.RIGHT).setFont(dataFont).setFontSize(18);
document.add(title).add(new Paragraph(pageNumberText).setTextAlignment(TextAlignment.RIGHT));
table=new Table(UnitValue.createPercentArray(columnWidth)).useAllAvailableWidth();
table.addHeaderCell(headerCell0);
table.addHeaderCell(headerCell1);
//create Header
newPage=false;
}
//Add row to table
sno++;
cell0=new Cell().add(new Paragraph(String.valueOf(sno)));
cell0.setFont(dataFont).setFontSize(16).setTextAlignment(TextAlignment.CENTER);

//From here, the data inner table starts
innerTable=new Table(UnitValue.createPercentArray(innerTableColumn)).useAllAvailableWidth();
innerTable.addCell(new Cell().add((new Paragraph("title1")).setFont(titleFont)).setPaddingLeft(5));
innerTable.addCell(new Cell().add(new Paragraph("abcd")).setPaddingLeft(5));
innerTable.addCell(new Cell().add((new Paragraph("title1")).setFont(titleFont)));
innerTable.addCell(new Cell().add(new Paragraph("abcd")));

innerTable.addCell(new Cell().add(new Paragraph("title3").setFont(titleFont).add(correct)));
innerTable.addCell(new Cell().add(new Paragraph("[Applied]").add(incorrect)));


// inner table data insertion ends here...

cell1=new Cell().add(innerTable);
//cell1.setFont(dataFont).setFontSize(16).setTextAlignment(TextAlignment.JUSTIFIED);
table.addCell(cell0);
table.addCell(cell1);

if(sno%pageSize==0 || sno==totalSize)
{
//add table to page
//add creator name
document.add(table);
document.add(creator);
if(sno<totalSize)
{
//add new page
System.out.println("New page to add");
}
newPage=true;
}
}
document.close();
System.out.println("File created");
}catch(IOException ioException)
{
System.out.println("Exception: "+ioException.getMessage());
}
}
public static void main(String args[])
{
PDFCreation pdf=new PDFCreation();
pdf.createPDF("/media/ashvin/code/tomcat9/webapps/webrock/","/media/ashvin/code/tomcat9/webapps/webrock/doc.pdf");
}
}

