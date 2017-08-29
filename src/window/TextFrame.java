package window;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

@SuppressWarnings("serial")
public class TextFrame extends Frame
{
	Label label=new Label();
	public TextFrame()
	{
		this.setLayout(new GridLayout(1, 2));
		this.setMinimumSize(new Dimension(200, 20));
		this.addWindowListener(new WindowListener()
		{
			public void windowOpened(WindowEvent e){}
			public void windowIconified(WindowEvent e){}
			public void windowDeiconified(WindowEvent e){}
			public void windowDeactivated(WindowEvent e){}
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
			public void windowClosed(WindowEvent e)
			{
				System.exit(0);
			}
			public void windowActivated(WindowEvent e){}
		});
		this.add(new Label("1"));
		this.add(label=new Label());
	}
	public void setText(String text)
	{
		label.setText(text);
	}
	public String getText()
	{
		return label.getText();
	}
	public void newLine()
	{
		((GridLayout)this.getLayout()).setRows(((GridLayout)this.getLayout()).getRows()+1);
		this.add(new Label(Integer.toString(((GridLayout)this.getLayout()).getRows())));
		this.add(label=new Label());
		this.pack();
	}
}
