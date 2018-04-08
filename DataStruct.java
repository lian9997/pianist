package model;

public class DataStruct {
	private int note;
	private int channel;
	
	public DataStruct(int note, int channel) {
		this.note = note;
		this.channel = channel;
	}
	
	public void setNote(int note) {
		this.note = note;
	}
	
	public void setChannel(int channel) {
		this.channel = channel;
	}
	
	public int getNote() {
		return this.note;
	}
	
	public int getChannel() {
		return channel;
	}

	@Override
	public String toString() {
		String result = "Note: " + this.note + "\nChannel: " + channel;
		
		return result;
	}
}
