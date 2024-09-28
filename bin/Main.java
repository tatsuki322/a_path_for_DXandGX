import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
class Main {
	public static void main(String[] args) {
		ArrowDiagram mrTatsuki = new ArrowDiagram();
		mrTatsuki.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}