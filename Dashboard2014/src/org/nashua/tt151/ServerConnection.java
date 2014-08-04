package org.nashua.tt151;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerConnection {
	public static interface ConnectionListener {
		public void onConnect( Socket s );
		
		public void onDataReceived( Socket s, String msg );
		
		public void onDisconnect( Socket s );
	}
	
	private ArrayList<Socket> clients = new ArrayList<Socket>();
	private ConnectionListener listener;
	private int port;
	private HashMap<Socket, BufferedReader> readers = new HashMap<Socket, BufferedReader>();
	private ServerSocket server;
	private HashMap<Socket, PrintWriter> writers = new HashMap<Socket, PrintWriter>();
	private HashMap<Socket, Long> lastMsg = new HashMap<Socket, Long>();
	
	public ServerConnection( int port, ConnectionListener cl ) throws IOException {
		listener = cl;
		this.port = port;
		server = new ServerSocket( port );
		new Thread() {
			public void run() {
				while ( true ) {
					try {
						Socket c = server.accept();
						readers.put( c, new BufferedReader( new InputStreamReader( c.getInputStream() ) ) );
						writers.put( c, new PrintWriter( c.getOutputStream() ) );
						clients.add( c );
						if ( listener != null ) {
							listener.onConnect( c );
						}
//						Thread.sleep( 1 );
					} catch ( Exception e ) {}
				}
			}
		}.start();
		new Thread() {
			public void run() {
				while ( true ) {
					try {
						for ( int i = 0; i < clients.size(); i++ ) {
							Socket client = clients.get( i );
							BufferedReader reader = readers.get( client );
							if ( client != null && client.isConnected() && !client.isClosed() ) {
								if ( reader.ready() ) {
									int length = reader.read();
									char[] buffer = new char[length];
									int offset = 0;
									while ( offset < length && reader.ready() ) {
										offset += reader.read( buffer, offset, length - offset );
									}
									String msg = new String( buffer );
									if ( msg != null && !msg.trim().equals( "" ) ) {
										if ( listener != null ) {
											lastMsg.put( client, System.currentTimeMillis() );
											listener.onDataReceived( client, msg );
										}
									}
								} else {
									try {
										if ( ( System.currentTimeMillis() - lastMsg.get( client ).longValue() ) > 2500 ) {
											disconnect( client, true );
											i--;
										}
									} catch ( Exception e ) {}
								}
							} else if ( client != null ) {
								disconnect( client, false );
								i--;
							}
						}
					} catch ( Exception e ) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	public void disconnect( boolean byeMessage ) {
		try {
			for ( int i = 0; i < clients.size(); i++ ) {
				disconnect( clients.get( i ), byeMessage );
				i--;
			}
		} catch ( Exception e ) {}
	}
	
	public void disconnect( Socket client, boolean byeMessage ) {
		try {
			if ( byeMessage ) {
				send( client, "[Disconnected]" );
			}
			clients.remove( client );
			readers.remove( client );
			writers.remove( client );
			if ( listener != null ) {
				listener.onDisconnect( client );
			}
		} catch ( Exception e ) {}
	}
	
	public int getPort() {
		return port;
	}
	
	public void send( Socket client, String msg ) {
		try {
			PrintWriter writer = writers.get( client );
			System.out.println( msg );
			writer.print( (char) msg.length() );
			writer.print( msg );
			writer.flush();
		} catch ( Exception e ) {}
	}
	
	public void send( String msg ) {
		try {
			for ( int i = 0; i < clients.size(); i++ ) {
				send( clients.get( i ), msg );
			}
		} catch ( Exception e ) {}
	}
}
