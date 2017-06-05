package com.lexinsmart.SocketTools;


/**
 * 
 * @author Administrator
 *
 */
public class Packet {
	
	private int id=AtomicIntegerUtil.getIncrementID();
	private byte[] data;
	
	public int getId() {
		return id;
	}

	public void pack(String txt)
	{
		data=txt.getBytes();
	}
	public void pack_byte(byte[] array_byte)
	{
		data = array_byte;
	}
	public byte[] getPacket()
	{
		return data;
	}
}
