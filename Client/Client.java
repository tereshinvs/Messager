import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;

public class Client
	{
		private static final int FRAME_WIDTH = 400, FRAME_HEIGHT = 600;
		private static final int INPUT_WIDTH = 320, INPUT_HEIGHT = 40;
		private static final int TIME_OUT = 100;

		private static JFrame frame;
		private static JTextArea textarea;
		private static JScrollPane scrollpane;
		private static JTextField textfield;
		private static JButton send_button;
		private static JTextField user_name_textfield, server_address_textfield, server_port_textfield;
		private static JButton ok_button;

		private static PrintWriter out;

		private static int server_port, my_port = 8860;
		private static String server_address, user_name;

		private static Thread server_listener;

		public static void main(String[] args)
			{
				frame = new JFrame("Messager Client");
				frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
				frame.setResizable(false);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLocationRelativeTo(new JFrame().getRootPane());
				frame.setLayout(null);

				textfield = new JTextField();
				textfield.setBounds(0, FRAME_HEIGHT-2*INPUT_HEIGHT, INPUT_WIDTH, INPUT_HEIGHT);
				textfield.setVisible(false);
				frame.add(textfield);

				send_button = new JButton("Send");
				send_button.setBounds(INPUT_WIDTH, FRAME_HEIGHT-2*INPUT_HEIGHT, FRAME_WIDTH-INPUT_WIDTH, INPUT_HEIGHT);
				send_button.addActionListener(new SendButtonListener());
				send_button.setVisible(false);
				frame.add(send_button);

				textarea = new JTextArea(1, 10);
				textarea.setBounds(0, 3, FRAME_WIDTH-30, FRAME_HEIGHT-5*INPUT_HEIGHT/2);
				textarea.setLineWrap(true);
				textarea.setEditable(false);
				scrollpane = new JScrollPane(textarea);
				scrollpane.setBounds(0, 0, FRAME_WIDTH-10, FRAME_HEIGHT-2*INPUT_HEIGHT-10);
				scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				scrollpane.setVisible(false);
				frame.add(scrollpane);

				user_name_textfield = new JTextField("user");
				user_name_textfield.setBounds(20, 20, 360, 20);
				frame.add(user_name_textfield);

				server_address_textfield = new JTextField("127.0.0.1");
				server_address_textfield.setBounds(20, 60, 360, 20);
				frame.add(server_address_textfield);

				server_port_textfield = new JTextField("8850");
				server_port_textfield.setBounds(20, 100, 360, 20);
				frame.add(server_port_textfield);

				ok_button = new JButton("OK");
				ok_button.setBounds(170, 140, 60, 20);
				ok_button.addActionListener(new OKButtonListener());
				frame.add(ok_button);

				frame.setVisible(true);
			}

		private static class SendButtonListener implements ActionListener
			{
				public void actionPerformed(ActionEvent aevent)
					{
						try {
						Socket socket = new Socket();
						InetSocketAddress address = new InetSocketAddress(server_address, server_port);
						socket.connect(address, TIME_OUT);
						out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
						out.println("msg#"+user_name+": "+textfield.getText());
						out.flush();
						System.out.println("sent");
						socket.close();
						} catch (Exception e) { System.out.println("error in client"); };						
					}
			}

		private static class OKButtonListener implements ActionListener
			{
				public void actionPerformed(ActionEvent aevent)
					{
						try {
						user_name = user_name_textfield.getText();
						if (user_name.equals("")) return;
						server_address = server_address_textfield.getText();
						server_port = Integer.parseInt(server_port_textfield.getText());
						Socket socket = new Socket();
						InetSocketAddress address = new InetSocketAddress(server_address, server_port);
						socket.connect(address, TIME_OUT);
						out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
						out.println("new#"+user_name);
						out.flush();
						System.out.println("access");
						socket.close();

						ok_button.setVisible(false);
						user_name_textfield.setVisible(false);
						server_address_textfield.setVisible(false);
						server_port_textfield.setVisible(false);

						scrollpane.setVisible(true);
						send_button.setVisible(true);
						textfield.setVisible(true);

						server_listener = new Thread(new ServerListener());
						server_listener.start();

						} catch (Exception e) { System.out.println("error in client"); };						
					}
			}


		private static class ServerListener implements Runnable
			{
				public void run()
					{
						ServerSocket servsock;
						Socket socket;
						BufferedReader in;
						String s;
						while (true)
							{
								try {
								servsock = new ServerSocket(my_port);
								socket = servsock.accept();
								in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
								s = in.readLine();
								System.out.println("got a new message"+s);
								textarea.append(s+"\n"); scrollpane.doLayout();
								socket.close();
								servsock.close();
								} catch (Exception e) {};
							}
					}
			}


	}