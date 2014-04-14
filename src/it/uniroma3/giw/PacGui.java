package it.uniroma3.giw;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PacGui {

	private JFrame frmPacmanIndexer;
	private JLabel lblIndexPath;
	private JLabel lblDocsPath;
	private JLabel lblNbIfUnset;
	private JButton btnBrowse;
	private JButton btnBrowse_1;
	private JCheckBox chckbxUpdate;
	private String docsPath = null;
	private String indexPath = null;
	private IndexFiles indexFiles;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PacGui window = new PacGui();
					window.frmPacmanIndexer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PacGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmPacmanIndexer = new JFrame();
		frmPacmanIndexer.setTitle("Pacman Indexer");
		frmPacmanIndexer.setBounds(100, 100, 450, 300);
		frmPacmanIndexer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frmPacmanIndexer.getContentPane().setLayout(springLayout);
		
		lblIndexPath = new JLabel("Index Path:");
		springLayout.putConstraint(SpringLayout.NORTH, lblIndexPath, 10, SpringLayout.NORTH, frmPacmanIndexer.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblIndexPath, 10, SpringLayout.WEST, frmPacmanIndexer.getContentPane());
		frmPacmanIndexer.getContentPane().add(lblIndexPath);
		
		lblDocsPath = new JLabel("Docs Path:");
		springLayout.putConstraint(SpringLayout.NORTH, lblDocsPath, 17, SpringLayout.SOUTH, lblIndexPath);
		springLayout.putConstraint(SpringLayout.WEST, lblDocsPath, 0, SpringLayout.WEST, lblIndexPath);
		frmPacmanIndexer.getContentPane().add(lblDocsPath);
		
		lblNbIfUnset = new JLabel("NB: If unset, will be used location in properties");
		springLayout.putConstraint(SpringLayout.NORTH, lblNbIfUnset, 17, SpringLayout.SOUTH, lblDocsPath);
		springLayout.putConstraint(SpringLayout.WEST, lblNbIfUnset, 0, SpringLayout.WEST, lblIndexPath);
		frmPacmanIndexer.getContentPane().add(lblNbIfUnset);
		
		btnBrowse = new JButton("Browse...");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				int result = chooser.showOpenDialog(null);
				if( result == JFileChooser.APPROVE_OPTION)
				{
					indexPath = chooser.getSelectedFile().getAbsolutePath();
					lblIndexPath.setText(indexPath);
				}
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnBrowse, 10, SpringLayout.NORTH, frmPacmanIndexer.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btnBrowse, 25, SpringLayout.NORTH, frmPacmanIndexer.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnBrowse, -10, SpringLayout.EAST, frmPacmanIndexer.getContentPane());
		frmPacmanIndexer.getContentPane().add(btnBrowse);
		
		btnBrowse_1 = new JButton("Browse...");
		btnBrowse_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = chooser.showOpenDialog(null);
				if( result == JFileChooser.APPROVE_OPTION)
				{
					docsPath = chooser.getSelectedFile().getAbsolutePath();
					lblDocsPath.setText(docsPath);
				}
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnBrowse_1, 17, SpringLayout.SOUTH, btnBrowse);
		springLayout.putConstraint(SpringLayout.SOUTH, btnBrowse_1, 32, SpringLayout.SOUTH, btnBrowse);
		springLayout.putConstraint(SpringLayout.EAST, btnBrowse_1, -10, SpringLayout.EAST, frmPacmanIndexer.getContentPane());
		frmPacmanIndexer.getContentPane().add(btnBrowse_1);
		
		chckbxUpdate = new JCheckBox("Update");
		springLayout.putConstraint(SpringLayout.NORTH, chckbxUpdate, 23, SpringLayout.SOUTH, lblNbIfUnset);
		springLayout.putConstraint(SpringLayout.WEST, chckbxUpdate, 0, SpringLayout.WEST, lblIndexPath);
		frmPacmanIndexer.getContentPane().add(chckbxUpdate);
		
		JButton btnGo = new JButton("GO!");
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if((indexPath == null ) || (docsPath == null))
					indexFiles = new IndexFiles();
				else
					indexFiles = new IndexFiles(indexPath,docsPath);
				indexFiles.index(chckbxUpdate.isSelected());
			}
		});
		springLayout.putConstraint(SpringLayout.SOUTH, btnGo, -38, SpringLayout.SOUTH, frmPacmanIndexer.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, btnGo, -178, SpringLayout.EAST, frmPacmanIndexer.getContentPane());
		frmPacmanIndexer.getContentPane().add(btnGo);
	}
}
