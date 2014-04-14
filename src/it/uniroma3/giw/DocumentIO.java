package it.uniroma3.giw;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DocumentIO {

	private String indexPath;
	private String documentPath;

	public DocumentIO(){
		Properties conf = new Properties();
		try {
			InputStream inputStream = new FileInputStream("../pacman_configuration.properties");
			conf.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}		

		this.indexPath = conf.getProperty("index-path");
		this.documentPath = conf.getProperty("documents-path");
	}

	public String getIndexPath() {
		return indexPath;
	}

	public String getDocumentPath() {
		return documentPath;
	}



}
