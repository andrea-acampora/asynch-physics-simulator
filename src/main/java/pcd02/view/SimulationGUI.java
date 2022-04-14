package pcd02.view;

import pcd02.controller.InputListener;
import pcd02.model.Body;
import pcd02.model.Boundary;
import pcd02.model.SimulationState;
import pcd02.model.P2d;
import pcd02.model.V2d;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class SimulationGUI {

	private final VisualiserFrame frame;

	public SimulationGUI(){
		this.frame = new VisualiserFrame(620, 620);
	}

	public void addListener(final InputListener l) {
		this.frame.addListener(l);
	}

	public void display(final SimulationState state) {
		ArrayList<Body> defCopy = new ArrayList<>();
		state.getBodies().forEach(b -> defCopy.add(new Body(b.getId(), new P2d(b.getPos().getX(), b.getPos().getY()), new V2d(b.getVel().x, b.getVel().y), b.getMass())));
		frame.display(defCopy, state.getVt(), state.getSteps(), state.getBounds());
	}

	public void start() {
		SwingUtilities.invokeLater(() -> this.frame.setVisible(true));
	}

	public static class VisualiserFrame extends JFrame implements ActionListener {

		private final VisualiserPanel panel;
		private final ArrayList<InputListener> listeners;
		private final JButton startButton;
		private final JButton stopButton;


		public VisualiserFrame(int w, int h) {
			this.listeners = new ArrayList<>();
			this.startButton = new JButton("start");
			this.stopButton = new JButton("stop");
			this.panel = new VisualiserPanel(w,h);

			this.stopButton.setEnabled(false);

			setTitle("Concurrent Bodies Simulator");
			setSize(w,h);
			setResizable(false);

			startButton.addActionListener(this);
			stopButton.addActionListener(this);

			JPanel controlPanel = new JPanel();
			JPanel cp = new JPanel();

			controlPanel.add(startButton);
			controlPanel.add(stopButton);
			cp.setLayout(new BorderLayout());
			cp.add(BorderLayout.NORTH,controlPanel);
			cp.add(BorderLayout.CENTER, panel);
			cp.add(BorderLayout.SOUTH,controlPanel);

			addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent ev){
					System.exit(0);
				}
				public void windowClosed(WindowEvent ev){
					System.exit(0);
				}
			});
			setContentPane(cp);
		}

		public void display(ArrayList<Body> bodies, double vt, long iter, Boundary bounds){
			try {
				SwingUtilities.invokeLater(() -> {
					panel.display(bodies, vt, iter, bounds);
					repaint();
				});
			} catch (Exception ignored) {}
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			String cmd = actionEvent.getActionCommand();
			if (cmd.equals("start")){
				this.startButton.transferFocus();
				this.stopButton.transferFocus();
				notifyStarted();
				this.startButton.setEnabled(false);
				this.stopButton.setEnabled(true);
			} else if (cmd.equals("stop")){
				notifyStopped();
				this.startButton.setEnabled(true);
				this.stopButton.setEnabled(false);
			}
		}

		public void addListener(InputListener l) {
			listeners.add(l);
		}

		private void notifyStarted(){
			for (InputListener l: listeners){
				l.start();
			}
		}

		private void notifyStopped(){
			for (InputListener l: listeners){
				l.stop();
			}
		}
	}

	public static class VisualiserPanel extends JPanel implements KeyListener {

		private ArrayList<Body> bodies;
		private Boundary bounds;

		private long nIter;
		private double vt;
		private double scale = 1;

		private final long dx;
		private final long dy;

		public VisualiserPanel(int w, int h){
			setSize(w,h);
			dx = w/2 - 20;
			dy = h/2 - 20;
			this.addKeyListener(this);
			setFocusable(true);
			setFocusTraversalKeysEnabled(false);
			requestFocusInWindow();
		}

		public void paint(Graphics g){
			if (bodies != null) {
				Graphics2D g2 = (Graphics2D) g;

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);
				g2.clearRect(0,0,this.getWidth(),this.getHeight());


				int x0 = getXcoord(bounds.getX0());
				int y0 = getYcoord(bounds.getY0());

				int wd = getXcoord(bounds.getX1()) - x0;
				int ht = y0 - getYcoord(bounds.getY1());

				g2.drawRect(x0, y0 - ht, wd, ht);

				bodies.forEach( b -> {
					P2d p = b.getPos();
					int radius = (int) (10*scale);
					if (radius < 1) {
						radius = 1;
					}
					g2.drawOval(getXcoord(p.getX()),getYcoord(p.getY()), radius, radius);
				});
				String time = String.format("%.2f", vt);
				g2.drawString("Bodies: " + bodies.size() + " - vt: " + time + " - nIter: " + nIter + " (UP for zoom in, DOWN for zoom out)", 2, 20);
			}
		}

		private int getXcoord(double x) {
			return (int)(dx + x*dx*scale);
		}

		private int getYcoord(double y) {
			return (int)(dy - y*dy*scale);
		}

		public void display(ArrayList<Body> bodies, double vt, long iter, Boundary bounds){
			this.bodies = bodies;
			this.bounds = bounds;
			this.vt = vt;
			this.nIter = iter;
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == 38){  		/* KEY UP */
				scale *= 1.1;
			} else if (e.getKeyCode() == 40){  	/* KEY DOWN */
				scale *= 0.9;
			}
		}

		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
	}
}
