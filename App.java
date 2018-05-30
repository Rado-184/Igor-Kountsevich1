package com.mycompany.FirstApp;

/**
 * Hello world!
 *
 */
/*
 * 1. Написать простое консольное приложение, которое должно “склеивать” 2 PDF файла в один. 
 * Исходные пути к файлам-источникам должны прописываться в проперти-файле, которые утилитка 
 * читает на старте. Путь к склеенному файлу тоже должен прописываться в проперти файле.
 * */
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
public class App 
{
    public static void main( String[] args )
    {
        try {
            //Prepare input pdf file list as list of input stream.
            List<InputStream> inputPdfList = new ArrayList<InputStream>();
            inputPdfList.add(new FileInputStream(new Util().getPropertyValue("PATH_ONE")));
            inputPdfList.add(new FileInputStream(new Util().getPropertyValue("PATH_TWO")));

            //Prepare output stream for merged pdf file.
            OutputStream outputStream = 
                    new FileOutputStream(new Util().getPropertyValue("PATH_RES"));

            //call method to merge pdf files.
            mergePdfFiles(inputPdfList, outputStream);     
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static class Util{
        public String getPropertyValue(String propertyName){
            String propertyValue =null;
            Properties properties = new Properties();
            
            try {

                FileReader reader=new FileReader("properties.properties");
                properties.load(reader);
                propertyValue =properties.getProperty(propertyName);
                
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
            return propertyValue;
        }
    }
    
    static void mergePdfFiles(List<InputStream> inputPdfList,
            OutputStream outputStream) throws Exception{
        //Create document and pdfReader objects.
        Document document = new Document();
        List<PdfReader> readers = 
                new ArrayList<PdfReader>();
        int totalPages = 0;

        //Create pdf Iterator object using inputPdfList.
        Iterator<InputStream> pdfIterator = 
                inputPdfList.iterator();

        // Create reader list for the input pdf files.
        while (pdfIterator.hasNext()) {
                InputStream pdf = pdfIterator.next();
                PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
                totalPages = totalPages + pdfReader.getNumberOfPages();
        }

        // Create writer for the outputStream
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        //Open document.
        document.open();

        //Contain the pdf data.
        PdfContentByte pageContentByte = writer.getDirectContent();

        PdfImportedPage pdfImportedPage;
        int currentPdfReaderPage = 1;
        Iterator<PdfReader> iteratorPDFReader = readers.iterator();

        // Iterate and process the reader list.
        while (iteratorPDFReader.hasNext()) {
                PdfReader pdfReader = iteratorPDFReader.next();
                //Create page and add content.
                while (currentPdfReaderPage <= pdfReader.getNumberOfPages()) {
                      document.newPage();
                      pdfImportedPage = writer.getImportedPage(
                              pdfReader,currentPdfReaderPage);
                      pageContentByte.addTemplate(pdfImportedPage, 0, 0);
                      currentPdfReaderPage++;
                }
                currentPdfReaderPage = 1;
        }

        //Close document and outputStream.
        outputStream.flush();
        document.close();
        outputStream.close();

        System.out.println("Pdf files merged successfully.");
    }
}
