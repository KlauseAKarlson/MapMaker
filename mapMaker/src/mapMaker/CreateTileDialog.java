package mapMaker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class CreateTileDialog extends JFrame implements ActionListener {

	private MapMaker Parent;
	private JButton BChooseImage, BTransparent, BOpaque;
	private JTextField TFTileName;	
	private BufferedImage ITransparent, IOpaque;
	private boolean transparentChosen=false;
	private List<ActionListener> Listeners;
	
	public CreateTileDialog(MapMaker parent)
	{
		super("Create Tile");
		Parent=parent;
		//add content
		getContentPane().setLayout(
			    new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		BChooseImage=new JButton("Choose a file\n");
		BChooseImage.setHorizontalTextPosition(AbstractButton.CENTER);
		BChooseImage.addActionListener(this);
		this.add(BChooseImage);
		//name tile
		this.add( new JLabel("Enter Tile Name:", JLabel.LEFT) );
		TFTileName=new JTextField(10);
		this.add(TFTileName);
		//finishing buttons with previews side by side
		Box b=Box.createHorizontalBox();
		BTransparent=new JButton("Create Transparent Tile");
		BTransparent.setVerticalTextPosition(AbstractButton.TOP);
		BTransparent.setHorizontalTextPosition(AbstractButton.CENTER);
		BTransparent.setIcon(parent.getMap().getTileSet().getTile("Empty").getIcon());
		BTransparent.addActionListener(this);
		BTransparent.addActionListener(parent);
		b.add(BTransparent);
		BOpaque=new JButton("Create Opaque Tile");
		BOpaque.setVerticalTextPosition(AbstractButton.TOP);
		BOpaque.setHorizontalTextPosition(AbstractButton.CENTER);
		BOpaque.setIcon(parent.getMap().getTileSet().getTile("Empty").getIcon());
		BOpaque.addActionListener(this);
		BOpaque.addActionListener(parent);
		b.add(BOpaque);
		this.add(b);
		//finish
		Listeners=new LinkedList<ActionListener>();
		this.pack();
		//this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	}//end CTOR
	public BufferedImage getTileImage()
	{
		if (this.transparentChosen)
		{
			return this.ITransparent;
		}else {
			return this.IOpaque;
		}//end if
	}//end fet tile image
	public String getTileName()
	{
		return this.TFTileName.getText();
	}
	@Override
	public void setVisible(boolean b)
	{
		/**
		 * resets fields to default and makes visible
		 * 
		 */
		if (b)
		{
			this.BChooseImage.setText("Choose a file\n");
			BTransparent.setIcon(Parent.getMap().getTileSet().getTile("Empty").getIcon());
			BOpaque.setIcon(Parent.getMap().getTileSet().getTile("Empty").getIcon());
			this.TFTileName.setText("");
			this.pack();
		}
		super.setVisible(b);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==this.BChooseImage)
		{
			chooseImage();
		}else if (	!(TFTileName.getText().equalsIgnoreCase("Empty") 
				|| TFTileName.getText().isEmpty()) )// create tile buttons locked it unacceptable tile name
		{
			if(e.getSource()==this.BTransparent )
			{
				this.transparentChosen=true;
				//Map maker should not recieve an action event unless a legal tile can be created
				this.createActionEvent("Transparent");
				this.setVisible(false);
			}else if(e.getSource()==this.BOpaque)
			{
				this.transparentChosen=false;
				this.createActionEvent("Opaque");
				this.setVisible(false);
			}
		}
	}//end action performed
	public void addActionListener(ActionListener a)
	{
		Listeners.add(a);
	}
	public void removeActionListener(ActionListener a)
	{
		Listeners.remove(a);
	}
	private void createActionEvent(String actionCommand)
	{
		/***
		 * sends and action event to all action listeners with the provided action command
		 * Map maker should not receive an action event unless a legal tile can be created
		 */
		if (!Listeners.isEmpty())
		{
			ActionEvent e2=new ActionEvent(this,ActionEvent.ACTION_FIRST ,actionCommand);
			for (ActionListener l:Listeners)
			{
				l.actionPerformed(e2);
			}
		}
	}//end  create action command
	private void chooseImage()
	{
		/**
		 * allows the user to choose an image file, and then set previews
		 */
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Images", 
	    		ImageIO.getReaderFileSuffixes() );//static method provides all image types known by ImageIo
	    chooser.setFileFilter(filter);
	    int returnValue=chooser.showOpenDialog(this);
	    if (returnValue==JFileChooser.APPROVE_OPTION)
	    {
	    	File imageFile=chooser.getSelectedFile();
	    	try {
	    		BufferedImage baseImage=ImageIO.read(imageFile);
	    		TileSet ts=Parent.getMap().getTileSet();
	    		//produce default tile name 
	    		String imagePath=imageFile.getAbsolutePath();
	    		int nameStart=imagePath.lastIndexOf(File.separator);
	    		int nameEnd=imagePath.lastIndexOf(".");
	    		this.TFTileName.setText(imagePath.substring(nameStart, nameEnd));
	    		
	    		this.IOpaque=ts.resizeImage(baseImage);
	    		this.BOpaque.setIcon(new ImageIcon(this.IOpaque));
	    		this.ITransparent=ts.ResizeAndTransparent(baseImage);
	    		this.BTransparent.setIcon(new ImageIcon(this.ITransparent));
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.getMessage(), "File Exception", JOptionPane.ERROR_MESSAGE);
			}
	    }
	}//end choose image

}//end class
