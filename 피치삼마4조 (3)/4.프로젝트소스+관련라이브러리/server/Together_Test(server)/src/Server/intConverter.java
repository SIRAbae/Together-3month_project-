package Server;

public class intConverter {

	public  int byteArrayToInt(byte bytes[]) {
		
		return ((((int)bytes[0] & 0xff) << 24) |
				(((int)bytes[1] & 0xff) << 16) |
				(((int)bytes[2] & 0xff) << 8) |
				(((int)bytes[3] & 0xff)));
	} 
	
	public  byte[] intToByteArray(int value) {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte)((value >> 24) & 0xFF);
        byteArray[1] = (byte)((value >> 16) & 0xFF);
        byteArray[2] = (byte)((value >> 8) & 0xFF);
        byteArray[3] = (byte)(value & 0xFF);
        return byteArray;
    }
}
