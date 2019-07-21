package mapMaker;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class TileChooserTest extends JFrame implements ActionListener{

	private JLabel Message; 
	private TileSet Tiles;
	public TileChooserTest(TileSet T)
	{
		super("Testing");
		this.setLayout(new BorderLayout());
		Tiles=T;
		Message=new JLabel("Empty",
				Tiles.getTile("Empty") ,
				JLabel.CENTER);
		this.add(Message, BorderLayout.NORTH);
		TileChooser TC=new TileChooser(Tiles);
		TC.addActionListener(this);
		this.add(TC, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		this.setVisible(true);
		
	}
	public static void main(String[] args) {
		String localPath=new File("").getAbsolutePath();
		//System.out.print(localPath+"\n");//debug
		String testSave=localPath+File.separator+"src"+File.separator+"testData"+File.separator+"testSave.txt";//file of test data
		try {
			Map testMap=Map.loadMap(testSave);
			TileSet t=testMap.getTileSet();
			new TileChooserTest(t);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String tileName=e.getActionCommand();
		Message.setText(tileName);
		Message.setIcon(Tiles.getTile(tileName) );
	}

}
