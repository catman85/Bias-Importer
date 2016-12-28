
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Toolkit;

public class View extends JFrame {

	private static final long serialVersionUID = 1L;
	private static JPanel contentPane;
	private static JTextField txtPresetDirectory;
	private static JTextField txtBiasFolder;
	private static JFileChooser chooser;
	private final  ButtonGroup buttonGroup = new ButtonGroup();
	private static JRadioButton rdbtnBiasFx;
	private static JRadioButton rdbtnBiasAmp;
	private static JLabel lblPreset;
	private static JLabel lblBias;
	private static JButton btnImport;
	private static JButton btnTopFileChooser;
	private static JButton btnBottomFileChooser;
	private static View frame;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		EventQueue.invokeLater(new Runnable(){
			public void run() {
				try {
					frame = new View();
					frame.setVisible(true);
				} catch (Exception e) {
					View.error();
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame. Runs when a View object is made.
	 */
	public View(){
		setIconImage(Toolkit.getDefaultToolkit().getImage(View.class.getResource("/resourcesimg/6ed0796d5879146f7f4db90d21d6f494.png")));
		setTitle("Bias Importer");
		initComponents();
		handleEvents();
	}
	
	
	private static int popFileChooser(String type){
		
		try {
			chooser.setCurrentDirectory(new java.io.File("."));
			if(type=="pickfile"){
				chooser.setDialogTitle("Select File");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);//.Preset
			}else if(type=="pickfolder"){
				chooser.setDialogTitle("Select Folder");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//Bias Amp folder
			}else{
				chooser.setDialogTitle("Select...");
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
			}
			//
			// disable the "All files" option.
			//
			chooser.setAcceptAllFileFilterUsed(false);
			  
			
			if(chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) { 
			  chooser.getCurrentDirectory();
			  chooser.getSelectedFile();
			  return 0;
			}else {
			  //No selection 
			  //the cancel button was hit
			  return 1;
			}
		} catch (NullPointerException e) {
			View.error();
			e.printStackTrace();
		}
		return 1;
	}
	
	private void initComponents() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 570, 244);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
	    
 
		rdbtnBiasAmp = new JRadioButton("Bias Amp");
		rdbtnBiasAmp.setSelected(true);
		buttonGroup.add(rdbtnBiasAmp);
		rdbtnBiasAmp.setBounds(129, 11, 124, 36);
		contentPane.add(rdbtnBiasAmp);
	
		rdbtnBiasFx = new JRadioButton("Bias FX");
		buttonGroup.add(rdbtnBiasFx);
		rdbtnBiasFx.setBounds(257, 11, 108, 36);
		contentPane.add(rdbtnBiasFx);
		
		lblPreset = new JLabel("Preset Directory:");
		lblPreset.setBounds(18, 59, 114, 16);
		contentPane.add(lblPreset);
		
		lblBias = new JLabel("Bias Directory:");
		lblBias.setBounds(18, 113, 114, 16);
		contentPane.add(lblBias);

		txtPresetDirectory = new JTextField();
		txtPresetDirectory.setText("(Select the folder with the weird name)");
		txtPresetDirectory.setBounds(129, 56, 347, 22);
		contentPane.add(txtPresetDirectory);
		txtPresetDirectory.setColumns(10);
		
		txtBiasFolder = new JTextField();
		txtBiasFolder.setText("Usually it's: \"C:\\Users\\UserName\\Documents\\Bias\"");
		txtBiasFolder.setBounds(129, 110, 347, 22);
		contentPane.add(txtBiasFolder);
		txtBiasFolder.setColumns(10);

		btnImport = new JButton("Import");
		btnImport.setBounds(220, 161, 97, 25);
		contentPane.add(btnImport);
		
		chooser = new JFileChooser(){
			private static final long serialVersionUID = 1L;
			//setting the icon image of JFileChooser
			protected JDialog createDialog( Component parent ) throws HeadlessException {
	        	JDialog dialog = super.createDialog( parent );
	        	Image image = Toolkit.getDefaultToolkit().getImage(View.class.getResource("/resourcesimg/6ed0796d5879146f7f4db90d21d6f494.png"));
	        	dialog.setIconImage( image );
	        	return dialog;
	    	}
		};
		
		btnTopFileChooser = new JButton("...");
		btnTopFileChooser.setBounds(488, 55, 44, 25);
		contentPane.add(btnTopFileChooser);
		
		btnBottomFileChooser = new JButton("...");
		btnBottomFileChooser.setBounds(488, 109, 44, 25);
		contentPane.add(btnBottomFileChooser);
	}
	
	
	
	
	private static void handleEvents(){
		rdbtnBiasFx.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				txtPresetDirectory.setText("(Select the \".Preset\" file)");
				txtBiasFolder.setText("Usually it's: \"C:\\Users\\UserName\\Documents\\BIAS_FX\"");
				lblBias.setText("Bias FX Directory:");
			}
		});
		rdbtnBiasAmp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				txtPresetDirectory.setText("(Select the folder with the weird name)");
				txtBiasFolder.setText("Usually it's: \"C:\\Users\\UserName\\Documents\\Bias\"");
				lblBias.setText("Bias Directory:");
			}
		});
		btnTopFileChooser.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) throws NullPointerException{
				int b;
				try {
					if(rdbtnBiasFx.isSelected()){
						b=popFileChooser("pickfile");
					}else if(rdbtnBiasAmp.isSelected()){
						b=popFileChooser("pickfolder");
					}else{
						b=popFileChooser("");
					}
					if(b==0){
						txtPresetDirectory.setText(chooser.getSelectedFile().getAbsolutePath());
					}
				} catch (NullPointerException e){
					View.error();
					e.printStackTrace();
				}
			}
		});
		btnBottomFileChooser.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				try {
					int b=popFileChooser("pickfolder");
					if(b==0){
						txtBiasFolder.setText(chooser.getSelectedFile().getAbsolutePath());
					}
				} catch (NullPointerException e) {
					View.error();
					e.printStackTrace();
				}
			}
		});

		btnImport.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				String type="BiasAmp";
				if(rdbtnBiasFx.isSelected()){
					type="BiasFx";
				}
				//Hops on Bias.java and runs constructor.
				new Bias(txtPresetDirectory.getText(),txtBiasFolder.getText(),type);
			}
		});
	}
	
	//The end
	public static void success(){
		JOptionPane.showMessageDialog(new View(), "Success!");
		System.exit(0);
	}
	//THALL
	public static void error(){
		JOptionPane.showMessageDialog(new View(), "Error!");
		System.exit(1);
	}
}



