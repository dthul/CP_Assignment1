package reactorexample;

import reactor.*;
import reactorapi.*;
import java.util.Random;

/**
 * Really simple Reactor usage example that creates two Handles that send
 * strings one character at a time and event handlers that collect these
 * characters and finally print them.
 * 
 * The purpose of this class is to provide a self-contained trivial test.
 * 
 * Expected output: "Reactor test completed OK."
 */
public class ReactorExample {
	Dispatcher d = new Dispatcher();
	StringHandler sh1, sh2;
	Random r = new Random();
	String expected = "Reactor test completed OK.";

	public static void main(String[] args) {
		for (int i = 0; i < 50; i++)
			new ReactorExample().execute();
		System.err
				.println("\nNo errors found; Reactor at least minimally compliant.");
	}

	public ReactorExample() {
		sh1 = new StringHandler("Reactor test ");
		d.addHandler(sh1);
		sh2 = new StringHandler("completed OK.");
		d.addHandler(sh2);
	}

	public void execute() {
		/* Main loop. */
		try {
			d.handleEvents();
		} catch (InterruptedException ie) {
			return;
		}

		String result = "" + sh1 + sh2;

		if (!result.equals(expected)) {
			System.err.println("Reactor test failed, expected:");
			System.err.println(expected);
			System.err.println("got:");
			System.err.println(result);
			System.exit(1);
		}
		System.err.print(".");
	}

	/**
	 * Handle that returns characters one at a time slowly from a
	 * creator-supplied String.
	 */
	public class StringHandle implements Handle<Character> {
		int position = 0;
		String data;

		public StringHandle(String s) {
			data = s;
		}

		public Character read() {
			try {
				Thread.sleep(r.nextInt(50));
			} catch (InterruptedException ie) {
			}

			if (position >= data.length())
				return null;
			// System.err.println("Returning: "+data.charAt(position));
			return new Character(data.charAt(position++));
		}
	}

	/**
	 * Event handler that receives characters from a (creator-supplied)
	 * StringHandle and collects them in a String.
	 */
	public class StringHandler implements EventHandler<Character> {
		StringHandle sh;
		StringBuffer sb = new StringBuffer();

		public StringHandler(String s) {
			sh = new StringHandle(s);
		}

		public StringHandle getHandle() {
			return sh;
		}

		public void handleEvent(Character s) {
			if (s == null) {
				d.removeHandler(this);
			} else
				sb.append(s);
		}

		public String toString() {
			return sb.toString();
		}
	}
}
