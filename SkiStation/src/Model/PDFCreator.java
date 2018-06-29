package Model;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Klasa tworząca PDF z kartą do wydruku
 * @author Sebastian
 */
public class PDFCreator {
    
      /**
     * Tworzy PDF z kartą na podstawie listy zawartości podanej jako argument
     * @param content dane potrzebne do wypelnienia pól w karcie
     * @throws java.io.IOException wyjątek wyrzucany w przypadku nieudanego tworzenia dokumentu PDF
     */
    public void create(String[][] content) throws IOException{
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage( page );
            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                drawTable(page, contentStream, 700, 100, content);
            }
            doc.save("ClientCard.pdf" );
        }
    } 

     /**
     * Rysuje tabele w dokumencie PDF
     * @param content dane potrzebne do wypelnienia pól w karcie
     * @throws java.io.IOException
     */
    private void drawTable(PDPage page, PDPageContentStream contentStream,
                             float y, float margin,
                             String[][] content) throws IOException {
        final int rows = content.length;
        final int cols = content[0].length;
        final float rowHeight = 20f;
        final float tableWidth = page.getCropBox().getWidth() - margin - margin;
        final float tableHeight = rowHeight * rows;
        final float colWidth = tableWidth/(float)cols;
        final float cellMargin=5f;

        float nexty = y ;
        for (int i = 0; i <= rows; i++) {
            contentStream.drawLine(margin, nexty, margin+tableWidth, nexty);
            nexty-= rowHeight;
        }

        float nextx = margin;
        for (int i = 0; i <= cols; i++) {
            contentStream.drawLine(nextx, y, nextx, y-tableHeight);
            nextx += colWidth;
        }

        contentStream.setFont( PDType1Font.TIMES_ROMAN , 12 );

        contentStream.beginText();
        contentStream.moveTextPositionByAmount(margin,y + 10);
        contentStream.drawString("Ski Station Card");
        contentStream.endText();
        
        float textx = margin+cellMargin;
        float texty = y-15;
        for(int i = 0; i < content.length; i++){
            for(int j = 0 ; j < content[i].length; j++){
                String text = content[i][j];
                contentStream.beginText();
                contentStream.moveTextPositionByAmount(textx,texty);
                contentStream.drawString(text);
                contentStream.endText();
                textx += colWidth;
            }
            texty-=rowHeight;
            textx = margin+cellMargin;
        }
    }
    
}
