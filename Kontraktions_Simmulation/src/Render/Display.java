package Render;
import java.awt.Canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import Render.Point.MyPoint;
import Render.Shapes.MyPolygon;
import Render.Shapes.Tetrahedron;

public class Display extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;
	
	private Thread thread;
	private JFrame frame;
	private static String title = "3D Render";
	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;
	private static boolean running = false;
	
	private Tetrahedron tetra;
	
	public Display() {
		this.frame = new JFrame();
		
		Dimension size = new Dimension(WIDTH, HEIGHT);
		this.setPreferredSize(size);
	}
	
	public static void main(String[] args) {
		Display display = new Display();
		display.frame.setTitle(title);
		display.frame.add(display);
		display.frame.pack();
		display.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		display.frame.setLocationRelativeTo(null);
		display.frame.setResizable(false);
		display.frame.setVisible(true);
		
		display.start();
	}
	
	public synchronized void start() {
		running = true;
		this.thread = new Thread(this, "Display");
		this.thread.start();
	}
	
	public synchronized void stop() {
		running = false;
		
		try {
			this.thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60;
		double delta = 0;
		int frames = 0;
		
		init();
		
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				update();
				delta--;
				render();
				frames++;	
				init();
			}
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				this.frame.setTitle(title + " | " + frames + "FPS");
				frames = 0;
			}
		}
		
		stop();
	}
	
	public void init() {
		double vinit = 0.01;
		double nm = doppler(vinit);
		nm = nm * 1000000000;
		int cr = 255;
		int cg = 50;
		int cb = 50;
		if (nm < 630) {
			cr = 255;
			cg = 165;
			cb = 50;		
		}
		if (nm < 590) {
			cr = 255;
			cg = 255;
			cb = 50;		
		}
		if (nm < 560) {
			cr = 50;
			cg = 255;
			cb = 50;		
		}
		if (nm < 490) {
			cr = 50;
			cg = 50;
			cb = 255;		
		}
		if (nm < 450) {
			cr = 191;
			cg = 62;
			cb = 255;		
		}
		
		int s = 200;
		int k = 150;
		int l = 0;
		int m = 0;
		double f = s/2;
		MyPoint p1 = new MyPoint(f - m, -f - l, -f - k);
		MyPoint p2 = new MyPoint(f - m, f - l, -f - k);
		MyPoint p3 = new MyPoint(f - m , f - l, f - k);
		MyPoint p4 = new MyPoint(f - m, -f - l, f - k);
		MyPoint p5 = new MyPoint(-f - m, -f - l, -f - k);
		MyPoint p6 = new MyPoint(-f - m, f - l, -f - k);
		MyPoint p7 = new MyPoint(-f - m, f - l, f - k);
		MyPoint p8 = new MyPoint(-f - m, -f - l, f - k);
		p1.x = simulation(p1.x, vinit);
		p2.x = simulation(p2.x, vinit);
		p3.x = simulation(p3.x, vinit);
		p4.x = simulation(p4.x, vinit);
		p5.x = simulation(p5.x, vinit);
		p6.x = simulation(p6.x, vinit);
		p7.x = simulation(p7.x, vinit);
		p8.x = simulation(p8.x, vinit);
		
		this.tetra = new Tetrahedron(
				new MyPolygon(new Color(cr, cg, cb), p1, p2, p3, p4),
				new MyPolygon(new Color(cr-10, cg-10, cb-10), p5, p6, p7, p8),
				new MyPolygon(new Color(cr-20, cg-20, cb-20), p1, p2, p6, p5),
				new MyPolygon(new Color(cr-30, cg-30, cb-30), p1, p5, p8, p4),
				new MyPolygon(new Color(cr-40, cg-40, cb-40), p2, p6, p7, p3),
				new MyPolygon(new Color(cr-50, cg-50, cb-50), p4, p3, p7, p8));
	}
	
	public double doppler(double v) {
		
		double c = 300000000;
		v = v*c;
		
		double nm = 0.0000006;
		
		
		double f = c/(nm);
		
		f = f/(1-(v/(c)));
		
		nm = c/f;
		
		return nm;
	}
	
	public double simulation(double px, double vinit) {
		
			double v = vinit;
			double gamma = (Math.sqrt(1 - (v*v)));
			
			double simx;
			
			simx = px*gamma;
			
			simx = simx/(1-v);
			
			
		
			return simx;
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, WIDTH, HEIGHT);

		tetra.render(g);
		
		g.dispose();
		bs.show();
		
	}
	
	private void update() {
		this.tetra.rotate(true, 5, -5, 50);	
	}

}
