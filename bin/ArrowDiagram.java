import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.lang.Math;

class ArrowDiagram extends Frame {
	final int dh = 30, dv = 30, m = 40, n = 17;
	ArrowDiagram() {
		setSize(dh*m, dv*n);
		setVisible(true);
		setTitle("矢印工程図自動描画ソフト");
	}
	@Override public void paint(Graphics g1) {
		super.paint(g1);
		Graphics2D g = (Graphics2D)g1;
		Point2d location = new Point2d((float)dh, (float)dv*n/2);
		Font font = new Font("Arial", Font.BOLD, dh);
		FontRenderContext frc = g.getFontRenderContext();
		try {
			Scanner tasktext = new Scanner(new File("task.txt")).useDelimiter(",\\s|\\n");
			String datum;
			List<Task> diagram = new ArrayList<Task>();
			int starting_point, end_point;
			String alphabet, taskname, time;
			while (tasktext.hasNextLine()) {
				alphabet = tasktext.next();
				taskname = tasktext.next();
				starting_point = Integer.parseInt(tasktext.next());
				end_point = Integer.parseInt(tasktext.next());
				time = tasktext.next();
				diagram.add(new Task(alphabet, taskname, starting_point, 
					end_point, time));
			}
			tasktext.close();
			Scanner statetext = new Scanner(new File("state.txt"));
			int[] count = {0, 0};
			while (statetext.hasNextLine()) {
				String s = statetext.nextLine();
				count[0]++;
			}
			statetext.close();
			//Beginning of a calculation of a critical path.//
			int[] the_Earliest_Feasible_Time = new int[count[0]];
			the_Earliest_Feasible_Time[0] = 0;
			int one_more_time = 0;
			while (true) {
				one_more_time = 0;
				Loop:for (int sp = 1; sp < count[0]; sp++) {
					for (int k = 0; k < diagram.size(); k++) {
						for (int gp = 2; gp <= count[0]; gp++) {
							if ((sp!=gp)&&(diagram.get(k).get_starting_point()==sp)&&(diagram.get(k).get_end_point()==gp)) {
								int tmp = the_Earliest_Feasible_Time[sp-1]+parsing(diagram, k);
								if (tmp>the_Earliest_Feasible_Time[gp-1]) {
									the_Earliest_Feasible_Time[gp-1]=tmp;
									one_more_time = 1;
									break Loop;
								}
							}
						}
					}
				}
				if (one_more_time == 0)
					break;
			}
			int[] an_Allowable_Time = new int[count[0]];
			for (int i = 0; i < count[0]; i++)
				an_Allowable_Time[i] = the_Earliest_Feasible_Time[count[0]-1];
			while (true) {
				one_more_time = 0;
				Loop:for (int gp = count[0]; gp >= 2; gp--) {
					for (int sp = count[0]-1; sp >= 1; sp--) {
						for (int k = 0; k < diagram.size(); k++) {
							if ((sp!=gp)&&(diagram.get(k).get_starting_point()==sp)&&(diagram.get(k).get_end_point()==gp)) {
								int tmp = an_Allowable_Time[gp-1]-parsing(diagram, k);
								if (tmp<an_Allowable_Time[sp-1]) {
									an_Allowable_Time[sp-1]=tmp;
									one_more_time = 1;
									break Loop;
								}
							}
						}
					}
				}
				if (one_more_time == 0)
					break;
			}
			//--- The end of calculating a critical path.---//
			for (int i = 0; i < count[0]; i++)
				System.out.println(the_Earliest_Feasible_Time[i]+", "+an_Allowable_Time[i]);
			int[] flag = new int[count[0]];
			for (int i = 0; i < count[0]; i++)
				flag[i] = the_Earliest_Feasible_Time[i] == an_Allowable_Time[i] ? 1 : 0;
			int sa;
			for (int j = 1; j <= count[0]; j++) {
				g.setColor(flag[j-1]==1 ? Color.RED : Color.BLACK);
				sa = dh*5*(j-1);
				TextLayout layout = new TextLayout(Integer.toString(j),
							font, frc);
				FlowLayout place = new FlowLayout(FlowLayout.LEFT, 0, dv);
				setLayout(place);
				layout.draw(g, location.getX()+sa, location.getY());
				Rectangle2D bounds = layout.getBounds();
				double a = bounds.getWidth();
	   			bounds.setRect(location.getX()+sa,
	                		-1.5f*bounds.getHeight()+location.getY(),
	                		a*2f,
	                		bounds.getHeight()*2f);
	 			g.draw(bounds);
				count[1] = 0;
				for (int i = 0; i < diagram.size(); i++)
					if (diagram.get(i).get_starting_point() == j) {
						Point2d locationtmp = 
							new Point2d((float)dh*4+sa, (float)2*dv*(count[1]+1));
						int days;
						String s = diagram.get(i).get_time();
						days = Integer.parseInt(s.substring(4, i<diagram.size()-1 ? s.length()-1 : s.length()));
						g.setColor((flag[j-1]==1&&flag[diagram.get(i).get_end_point()-1]==1&&
							days==the_Earliest_Feasible_Time[diagram.get(i).get_end_point()-1]
								-the_Earliest_Feasible_Time[diagram.get(i).get_starting_point()-1]) ? Color.RED : Color.BLACK);
						g.drawLine((int)location.getX()+(int)a*2+sa, (int)location.getY(), 
							dh*4+sa, 2*dv*(count[1]+1));
						layout = new TextLayout(diagram.get(i).get_alphabet(),
									font, frc);
						layout.draw(g, locationtmp.getX(), locationtmp.getY());
						bounds = layout.getBounds();
		   				bounds.setRect(locationtmp.getX(),
        			        		-1.5f*bounds.getHeight()+locationtmp.getY(),
                					bounds.getWidth()*2f,
                					bounds.getHeight()*2f);
 						g.draw(bounds);
						count[1]++;
						for (int k = 1; k <= count[0]; k++)
							if (diagram.get(i).get_end_point() == k)
								drawArrow(locationtmp, k, g, a, location, bounds);
					}
				
			}
	    	} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	int parsing(List<Task> diagram, int k) {
		Scanner hour_days = new Scanner(diagram.get(k).get_time()).useDelimiter("×");
		double hour;
		String tmpdays;
		int days;
		hour = hour_days.nextDouble();
		tmpdays = hour_days.next();
		hour_days.close();
		days = Integer.parseInt(tmpdays.substring(0, k==diagram.size()-1 ? tmpdays.length() : tmpdays.length()-1));
		return days;
	}
	void drawArrow(Point2d locationtmp, int k, Graphics2D g, double a, Point2d location, Rectangle2D bounds) {
		int x[] = {(int)locationtmp.getX()+(int)bounds.getWidth(),
				(int)locationtmp.getY(),
				(int)location.getX()+dh*5*(k-1), 
				(int)location.getY()}; 
		g.drawLine(x[0], x[1], x[2], x[3]);
		int r = 10;
		double PI = Math.PI;
		double length = Math.sqrt(Math.pow(x[1]-x[3], 2)+Math.pow(x[2]-x[0], 2));
		g.drawLine(x[2], x[3], x[2]+(int)(r/length*(Math.cos(PI/3)*(x[1]-x[3])-Math.sin(PI/3)*(x[2]-x[0]))),
				x[3]+(int)(r/length*(Math.sin(PI/3)*(x[1]-x[3])+Math.cos(PI/3)*(x[2]-x[0]))));
		g.drawLine(x[2], x[3], x[2]+(int)(r/length*(Math.cos(2*PI/3)*(x[1]-x[3])-Math.sin(2*PI/3)*(x[2]-x[0]))),
				x[3]+(int)(r/length*(Math.sin(2*PI/3)*(x[1]-x[3])+Math.cos(2*PI/3)*(x[2]-x[0]))));
	}
}