package com.lexinsmart.SocketTools;
import static com.lexinsmart.util.Constants.SOCKETSTATE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.util.Log;

/**
 * 
 * @author Administrator
 * 
 */
public class Client {

	private final int STATE_OPEN = 1;// socket�� 
	private final int STATE_CLOSE = 1 << 1;// socket�ر�
	private final int STATE_CONNECT_START = 1 << 2;// ��ʼ����server
	private final int STATE_CONNECT_SUCCESS = 1 << 3;// ���ӳɹ�
	private final int STATE_CONNECT_FAILED = 1 << 4;// ����ʧ��
	private final int STATE_CONNECT_WAIT = 1 << 5;// �ȴ�����

	private String IP = "192.168.31.78";
	private int PORT = 6800;

	private int state = STATE_CONNECT_START;

	private Socket socket = null;
	private OutputStream outStream = null;
	private InputStream inStream = null;

	private Thread conn = null;
	private Thread send = null;
	private Thread rec = null;

	private Context context;
	private ISocketResponse respListener;
	private SendControlResponse sendControlResponse;
	private LinkedBlockingQueue<Packet> requestQueen = new LinkedBlockingQueue<Packet>();
	private final Object lock = new Object();
	private final String TAG = "Client";

	public int send(Packet in) {
		requestQueen.add(in);
		synchronized (lock) {
			lock.notifyAll();
		}
		return in.getId();
	}

	public void cancel(int reqId) {
		Iterator<Packet> mIterator = requestQueen.iterator();
		while (mIterator.hasNext()) {
			Packet packet = mIterator.next();
			if (packet.getId() == reqId) {
				mIterator.remove();
			}
		}
	}

	public Client(Context context, ISocketResponse respListener,SendControlResponse sendcontrolresponse) {
		this.context = context;
		this.respListener = respListener;
		this.sendControlResponse = sendcontrolresponse;
	}

	public boolean isNeedConn() {
		return !((state == STATE_CONNECT_SUCCESS)
				&& (null != send && send.isAlive()) && (null != rec && rec
				.isAlive()));
	}

	public void open() {
		reconn();
	}

	public void open(String host, int port) {
		this.IP = host;
		this.PORT = port;
		reconn();
	}

	private long lastConnTime = 0;

	public synchronized void reconn() {
		if (System.currentTimeMillis() - lastConnTime < 2000) {
			return;
		}
		lastConnTime = System.currentTimeMillis();

		close();
		state = STATE_OPEN;
		SOCKETSTATE  = "socket�� ";
		conn = new Thread(new Conn());
		conn.start();
	}

	public synchronized void close() {
		try {
			if (state != STATE_CLOSE) {
				try {
					if (null != socket) {
						socket.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					socket = null;
				}

				try {
					if (null != outStream) {
						outStream.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					outStream = null;
				}

				try {
					if (null != inStream) {
						inStream.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					inStream = null;
				}

				try {
					if (null != conn && conn.isAlive()) {
						conn.interrupt();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					conn = null;
				}
				try {
					if (null != send && send.isAlive()) {
						send.interrupt();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					send = null;
				}

				try {
					if (null != rec && rec.isAlive()) {
						rec.interrupt();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					rec = null;
				}

				state = STATE_CLOSE;
				SOCKETSTATE  = "socket�ر� ";
				
			}
			requestQueen.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class Conn implements Runnable {
		public void run() {
			Log.v(TAG, "Conn :Start");
			try {
				while (state != STATE_CLOSE) {
					try {
						SOCKETSTATE  = "��ʼ���� ";
						state = STATE_CONNECT_START;
						socket = new Socket();
						socket.connect(new InetSocketAddress(IP, PORT),
								15 * 1000);
						state = STATE_CONNECT_SUCCESS;
						SOCKETSTATE  = "���ӳɹ� ";
					} catch (Exception e) {
						e.printStackTrace();
						state = STATE_CONNECT_FAILED;
						SOCKETSTATE  = "����ʧ�� ";
					}

					if (state == STATE_CONNECT_SUCCESS) {
						try {
							outStream = socket.getOutputStream();
							inStream = socket.getInputStream();
						} catch (IOException e) {
							e.printStackTrace();
						}

						send = new Thread(new Send());
						rec = new Thread(new Rec());
						send.start();
						rec.start();
						break;
					} else {
						SOCKETSTATE  = "�ȴ�����";
						state = STATE_CONNECT_WAIT;
						// ���������û�������ϣ���ʱȡ���ӣ�û��������ֱ���˳�
						if (NetworkUtil.isNetworkAvailable(context)) {
							try {
								Thread.sleep(15 * 1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
								break;
							}
						} else {
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.v(TAG, "Conn :End");
		}
	}

	private class Send implements Runnable {
		public void run() {
			Log.v(TAG, "Send :Start");
			try {
				while (state != STATE_CLOSE && state == STATE_CONNECT_SUCCESS
						&& null != outStream) {
					Packet item;
					while (null != (item = requestQueen.poll())) {
						outStream.write(item.getPacket());
						outStream.flush();
						item = null;
					}

					Log.v(TAG, "Send :woken up AAAAAAAAA");
					synchronized (lock) {
						lock.wait();
					}
					Log.v(TAG, "Send :woken up BBBBBBBBBB");
				}
			} catch (SocketException e1) {
				e1.printStackTrace();// ���͵�ʱ������쳣��˵��socket���ر���(�������ر�)java.net.SocketException:
										// sendto failed: EPIPE (Broken pipe)
				reconn();
			} catch (Exception e) {
				Log.v(TAG, "Send ::Exception");
				e.printStackTrace();
			}

			Log.v(TAG, "Send ::End");
		}
	}

	private class Rec implements Runnable {
		public void run() {
			Log.v(TAG, "Rec :Start");

			String str = "";
			try {
				while (state != STATE_CLOSE && state == STATE_CONNECT_SUCCESS
						&& null != inStream) {

					Log.v(TAG, "Rec :---------");
					byte[] bodyBytes = new byte[30]; 
					int offset = 0;
					int length = 18;
					int read = 0;

					while ((read = inStream.read(bodyBytes, offset, length)) != -1) {
						System.out.println("read--------->" + read);
						if (read > 18 || read < 11) {

						} else if (read == 18) {
							if (length - read == 0) {
								if (null != respListener) {
									if (bodyBytes[0] == 0x7E
											&& bodyBytes[1] == 0x40) {
										respListener
												.onSocketResponse(StringUtils
														.byte2HexStr(bodyBytes));
									} else {
										respListener
												.onSocketResponse(StringUtils
														.byte2HexStr(bodyBytes));

										Log.d("error", "���ݴ���");
									}
									// String ii ="";
									// for (int i = 0; i < bodyBytes.length;
									// i++) {
									// ii =
									// StringUtils.hexStr2Str(bodyBytes[i]+"");
									// System.out.println("bodyBytes:"+ ii);
									// }
								}
								offset = 0;
								length = 18;
								read = 0;
								continue;
							}
							offset += read;
							length = 20 - offset;
						} else if (read == 11) {
							for (int i = 11; i <30; i++) {
								bodyBytes[i] = 0;
							}
							sendControlResponse.onSocketResponse(StringUtils
														.byte2HexStr(bodyBytes));
						}

					}

					reconn();// �ߵ���һ����˵��������socket����
					break;
				}
			} catch (SocketException e1) {
				e1.printStackTrace();// �ͻ�������socket.close()���������
										// java.net.SocketException: Socket
										// closed
			} catch (Exception e2) {
				Log.v(TAG, "Rec :Exception");
				e2.printStackTrace();
			}

			Log.v(TAG, "Rec :End");
		}
	}
}