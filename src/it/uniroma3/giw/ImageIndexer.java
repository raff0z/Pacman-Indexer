package it.uniroma3.giw;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.utils.FileUtils;
import net.semanticmetadata.lire.utils.LuceneUtils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * User: Mathias Lux, mathias@juggle.at
 * Date: 25.05.12
 * Time: 12:04
 */
public class ImageIndexer {

	public void startIndex(String imgIndexPath, String imgDocPath){

		// Getting all images from a directory and its sub directories.
		ArrayList<String> images;
		try {
			images = FileUtils.getAllImages(new File(imgDocPath), true);


			// Creating a CEDD document builder and indexing all files.
			DocumentBuilder builder = DocumentBuilderFactory.getCEDDDocumentBuilder();
			// Creating an Lucene IndexWriter
			IndexWriterConfig conf = new IndexWriterConfig(LuceneUtils.LUCENE_VERSION,
					new WhitespaceAnalyzer(LuceneUtils.LUCENE_VERSION));
			IndexWriter iw;
			try {
				iw = new IndexWriter(FSDirectory.open(new File(imgIndexPath)), conf);

				// Iterating through images building the low level features
				Iterator<String> it = images.iterator(); 
				while(it.hasNext()) {
					String imageFilePath = it.next();
					System.out.println("Indexing " + imageFilePath);
					try {
						BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
						Document document = builder.createDocument(img, imageFilePath);
						iw.addDocument(document);
					} catch (Exception e) {
						System.err.println("Error reading image or indexing it.");
						e.printStackTrace();
					}
				}
				iw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// closing the IndexWriter

		System.out.println("Finished indexing.");
	}
}
