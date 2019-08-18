package me.deepak.convert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

// https://github.com/flyingsaucerproject/flyingsaucer/blob/master/flying-saucer-examples/src/main/java/PDFRenderToMultiplePages.java
public class PDFWriterService {

	public void createPDFByHTML(String html, OutputStream outputStream) throws IOException {
		String[] htmls = getHTMLs(html);
		ITextRenderer textRenderer = new ITextRenderer();
		try {
			for (int i = 0; i < htmls.length; i++) {
				renderPDF(textRenderer, outputStream, htmls[i], i);
			}

			// marking PDF creation as finished as we have added all the HTMLs to one PDF
			// document
			textRenderer.finishPDF();
		} catch (Exception exception) {
		} finally {
			outputStream.close();
		}
	}

	public InputStream[] getInputStreamFromPDF(String html) {
		String[] htmls = getHTMLs(html);
		int len = htmls.length;
		InputStream[] inputStreams = new InputStream[len];
		ITextRenderer textRenderer = new ITextRenderer();
		try {
			for (int i = 0; i < len; i++) {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				textRenderer.setDocumentFromString(htmls[i]);
				textRenderer.layout();
				textRenderer.createPDF(outputStream);
				inputStreams[i] = new ByteArrayInputStream(outputStream.toByteArray());
			}
		} catch (DocumentException e) {
		}
		return inputStreams;
	}

	private String[] getHTMLs(String html) {

		// case insensitive look behind regex
		return html.split("(?<=(?i)</html>)");
	}

	private void renderPDF(ITextRenderer textRenderer, OutputStream outputStream, String html, int index)
			throws DocumentException {
		textRenderer.setDocumentFromString(html);
		textRenderer.layout();
		if (index == 0) {

			// creating PDF and set finish to false as we may have more HTMLs to be rendered
			textRenderer.createPDF(outputStream, false);
		} else {

			// writing rest of the HTMLs (html[1 ... len - 1]) as PDF
			textRenderer.writeNextDocument();
		}
	}

}
