package mapMaker;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MapViewerTest extends JFrame implements ActionListener{

	private SquareMap activeMap;
	private SquareMapViewer activeViewer;
	private JLabel message;
	public MapViewerTest(SquareMap m)
	{
		super("Testing");
		this.setLayout(new BorderLayout());
		activeMap=m;
		message=new JLabel("Empty",
				activeMap.getTileSet().getEmpty(),
				JLabel.CENTER);
		this.add(message, BorderLayout.NORTH);
		activeViewer=new SquareMapViewer(activeMap);
		activeViewer.addActionListener(this);
		JPanel viewerPane=new JPanel();
		
		viewerPane.add(activeViewer);
		JScrollPane scrlPn=new JScrollPane(viewerPane);
		scrlPn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrlPn.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.add(scrlPn);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		this.setVisible(true);
	}
	public static void main(String[] args) {
		String localPath=new File("").getAbsolutePath();
		//System.out.print(localPath+"\n");//debug
		String testSave=localPath+File.separator+"src"+File.separator+"testData"+File.separator+"testSave.txt";//file of test data
		try {
			SquareMap testMap=SquareMap.createSquareMap(testSave);
			new  MapViewerTest(testMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		message.setText(e.getActionCommand());
		message.setIcon(activeMap.getTile(activeViewer.getSelectedColumn(),
				activeViewer.getSelectedRow(), 0) );
	}

}
