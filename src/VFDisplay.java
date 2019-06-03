
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class VFDisplay extends JFrame {
	private static final long serialVersionUID = -6364573863974006456L;
	private int mousex;
	private int mousey;
	
	private JLabel lbl1, lbl2, lbl3, lbl4;
	
	private JTextField width;
	private JTextField height;
	private JTextField seed;
	private JTextField rng_spacing;
	private JButton enter;
	
	private DisplayPanel display;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new VFDisplay();
			}
		});
	}
	
	public VFDisplay() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setTitle("Vector Field Display - Travis Pavelka");
		this.setBounds(100, 0, 800, 600);
		this.setLayout(null);
		
		this.lbl1 = new JLabel("Width: ", SwingConstants.LEFT);
		this.lbl2 = new JLabel("Height: ", SwingConstants.LEFT);
		this.lbl3 = new JLabel("Seed: ", SwingConstants.LEFT);
		this.lbl4 = new JLabel("RNG Spacing: ", SwingConstants.LEFT);
		
		this.width = new JTextField();
		this.height = new JTextField();
		this.seed = new JTextField();
		this.rng_spacing = new JTextField();
		this.enter = new JButton("Generate");
		
		int brdr = 15;
		int cwdth = 100;
		int col1 = 0;
		int col2 = col1 + cwdth + brdr;
		int wdth = col2 + cwdth;
		int rhght = 60;
		int row1 = 0;
		int row2 = row1 + rhght + brdr;
		int row3 = row2 + rhght + brdr;
		int row4 = row3 + rhght + brdr;
		int row5 = row4 + rhght + brdr;
		int hght = row5 + rhght;
		Point ref = new Point((this.getWidth()/2)-(wdth/2), (this.getHeight()/2)-(hght/2));
		
		this.lbl1.setBounds		(ref.x + col1, ref.y + row1, cwdth, rhght);
		this.lbl2.setBounds		(ref.x + col1, ref.y + row2, cwdth, rhght);
		this.lbl3.setBounds		(ref.x + col1, ref.y + row3, cwdth, rhght);
		this.lbl4.setBounds		(ref.x + col1, ref.y + row4, cwdth, rhght);
		
		this.width.setBounds		(ref.x + col2, ref.y + row1, cwdth, rhght);
		this.height.setBounds		(ref.x + col2, ref.y + row2, cwdth, rhght);
		this.seed.setBounds			(ref.x + col2, ref.y + row3, cwdth, rhght);
		this.rng_spacing.setBounds	(ref.x + col2, ref.y + row4, cwdth, rhght);
		this.enter.setBounds		(ref.x + col2, ref.y + row5, cwdth, rhght);

		this.add(lbl1);
		this.add(lbl2);
		this.add(lbl3);
		this.add(lbl4);
		
		this.add(width);
		this.add(height);
		this.add(seed);
		this.add(rng_spacing);
		this.add(enter);
		
		enter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int w = Integer.parseInt(width.getText());
					int h = Integer.parseInt(height.getText());
					int s = Integer.parseInt(seed.getText());
					int sp = Integer.parseInt(rng_spacing.getText());
					
					lbl1.setVisible(false);
					lbl2.setVisible(false);
					lbl3.setVisible(false);
					lbl4.setVisible(false);
					
					width.setVisible(false);
					height.setVisible(false);
					seed.setVisible(false);
					rng_spacing.setVisible(false);
					enter.setVisible(false);
					
					JScrollPane scroll = new JScrollPane();
					scroll.setLocation(0, 0);
					scroll.setSize(VFDisplay.this.getContentPane().getWidth(),
							VFDisplay.this.getContentPane().getHeight());
					scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
					scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
					VFDisplay.this.add(scroll);
					
					DisplayPanel display = new DisplayPanel(VFDisplay.this, scroll.getViewport(), w, h, s, sp);
					scroll.setViewportView(display);
					scroll.addMouseMotionListener(new MouseMotionListener() {
						@Override public void mouseMoved(MouseEvent e) {}
						@Override public void mouseDragged(MouseEvent e) {
							int dx = mousex - e.getX();
							int dy = mousey - e.getY();
							
							Point pos = scroll.getViewport().getViewPosition();
							int newx = pos.x + dx;
							int newy = pos.y + dy;
							
							if(newx > 0 && newy > 0) {
								scroll.getViewport().setViewPosition(new Point(newx, newy));
							}
							
							display.repaint();
							
							mousex = e.getX();
							mousey = e.getY();
						}
					});
					scroll.addMouseListener(new MouseListener() {
						@Override public void mouseClicked(MouseEvent e) {}
						@Override public void mousePressed(MouseEvent e) {
							mousex = e.getX();
							mousey = e.getY();
						}
						@Override public void mouseReleased(MouseEvent e) {}
						@Override public void mouseEntered(MouseEvent e) {}
						@Override public void mouseExited(MouseEvent e) {}
					});
				} catch(NumberFormatException e2) {
					JOptionPane.showMessageDialog(VFDisplay.this,
							"All inputs must be integers.",
							"Number Format Exception",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		this.setVisible(true);
	}
	
	private class DisplayPanel extends JPanel {
		private static final long serialVersionUID = -933584595364049608L;
		private VFDisplay display;
		private JViewport viewport;
		private RandomLinearVF rlvf;
		// being greater than spacing causes math errors
		final int startx = 15;
		final int starty = 15;
		double spacing = 25;
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Rectangle view = viewport.getViewRect();
			int vx = ((int)Math.ceil((view.x-startx)/spacing))-1;
			int vy = ((int)Math.ceil((view.y-starty)/spacing))-1;
			double dx = Math.ceil((view.x-startx)/spacing);
			double dy = Math.ceil((view.y-starty)/spacing);
			display.setTitle("VFDisplay ("+(int)dx+" ,"+(int)dy+")");
			int svx = vx;
			int x = (int)((spacing*vx)+startx);
			int y = (int)((spacing*vy)+starty);
			int sx = x;
			while(y < view.y+view.height+spacing) {
				while(x < view.x+view.width+spacing) {
					if(vx < rlvf.getWidth() && vy < rlvf.getHeight()
						&& 0 <= vx && 0 <= vy ) {
						Vector v = rlvf.getVector(vx, vy);
						boolean rng = v.isRNG();
						if(rng) {
							g.setColor(Color.red);
						} else {
							g.setColor(Color.blue);
						}
						g.fillArc(x-2, y-2, 4, 4, 0, 360);
						g.drawLine(x, y, (int)(x + v.getX()), (int)(y + v.getY()));
					}
					vx++;
					x += spacing;
				}
				vx = svx;
				x = sx;
				vy++;
				y += spacing;
			}
		}
		
		public DisplayPanel(VFDisplay display, JViewport viewport, int width, int height, int seed, int rng_spacing) {
			this.display = display;
			this.viewport = viewport;
			this.setPreferredSize(
					new Dimension(
							((width-1)*(int)spacing) + (startx*2),
							((height-1)*(int)spacing) + (starty*2)));
			rlvf = new RandomLinearVF(seed, rng_spacing, width, height);
		}
	}
}
