import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server
	{
		private static final int TIME_OUT = 100;

		private static int client_listener_port, user_amount = 0;

		private static User users[] = new User[100];

		public static void main(String[] args)
			{
				Scanner in = new Scanner(System.in);
				System.out.print("Client listener port: ");
				client_listener_port = in.nextInt();

				Thread client_listener = new Thread(new ClientListener());
				client_listener.start();
			}

		private static void newUser(String name, Socket socket)
			{
				users[user_amount++] = new User(name, socket);
				newMessage("New user: " + name);
				System.out.println(name + " is connected");
			}

		private static void newMessage(String text)
			{
				for (int i = 0; i < user_amount; i++)
					users[i].send(text);
				System.out.println("New message: "+text);
			}

		private static class ClientListener implements Runnable
			{
				public void run()
					{
						BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
						ServerSocket servsock;
						Socket socket;
						String s;
						while (true)
							{
								try
									{
										servsock = new ServerSocket(client_listener_port);
										socket = servsock.accept();
										in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
										s = in.readLine();
										if (s.startsWith("new#")) newUser(s.substring(4), socket);
										if (s.startsWith("msg#")) newMessage(s.substring(4));
										socket.close();
										servsock.close();
									}
								catch (IOException e) { System.out.println("connection error"); continue; }
							}
					}
			}

		private static class User
			{
				public String name;
				public InetSocketAddress address;

				User(String temp_name, Socket socket)
					{
						name = temp_name;
						address = new InetSocketAddress(socket.getInetAddress().getHostAddress(), 8860);
					}

				public void send(String message)
					{
						try {
						Socket socket = new Socket();
						socket.connect(address, TIME_OUT);
						PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
						out.println(message);
						out.flush();
						socket.close();
						} catch (Exception e) {};
					}
			}
	}