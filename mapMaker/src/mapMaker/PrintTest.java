package mapMaker;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PrintTest extends JFrame implements ChangeListener {

	private SquareMapViewer ActiveViewer;
	private SpinnerNumberModel PageChooser;
	private TestPaper testPage;
	
	public PrintTest(SquareMapViewer viewer)
	{
		super("Print Test");
		//create UI
		ActiveViewer=viewer;
		
		Container content=this.getContentPane();
		content.setLayout(new BorderLayout());
		
		PageChooser=new SpinnerNumberModel(0,0,10,1);//max value is arbitrary
		JSpinner pageSpinner=new JSpinner(PageChooser);

		content.add(pageSpinner, BorderLayout.NORTH);
		//create virtual printer page
		testPage=new TestPaper();
		JPanel panel=new JPanel();
		
		panel.add(testPage);
		JScrollPane scrlPn=new JScrollPane(panel);
		scrlPn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrlPn.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.add(scrlPn);
		pageSpinner.addChangeListener(this);
		//finish window
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500, 500);
		this.setVisible(true);
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		testPage.repaint();
	}
	
	
	public static void main(String[] args) {
		//find test map using gui
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("plain text",
	            "txt");
	    chooser.setFileFilter(filter);
	    int returnValue=chooser.showOpenDialog(null);
	    SquareMap saveMap;
	    if (returnValue==JFileChooser.APPROVE_OPTION)
	    {
	    	File save=chooser.getSelectedFile();
	    	try {
				saveMap=SquareMap.createSquareMap(save.getAbsolutePath());

				
				//create print test JFramec
				SquareMapViewer viewer=new SquareMapViewer(saveMap);
				new PrintTest(viewer);
				//
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }else {
	    	return;
	    }
		

	}//end main
	
	private class TestPaper extends Canvas{
		private PageFormat PF;
		private TestPaper()
		{
			setBackground(Color.WHITE);
			//defalut page 8.5 X 11 inches, at 72 pixels per inch, 612 x 792
			PF=new PageFormat();
			setSize( (int)PF.getWidth() , (int)PF.getHeight() );
		}//end CTOR
		
		@Override
		public void paint(Graphics g)
		{
			try {
				ActiveViewer.print(g, PF, (int)PageChooser.getValue() );
			} catch (PrinterException e) {
				e.printStackTrace();
			}
		}//end paint
		
	}//end test paper
}//end print Test
