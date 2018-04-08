package model;

import javax.sound.midi.*;

public class PlayThread implements Runnable{

	private int degree;
	private boolean isNote;

	public PlayThread() {

	}
	public PlayThread(int degree, boolean isNote) {
		this.isNote = isNote;
		if(isNote) {
			synchronized(Model.data) {
				Model.data.put(degree, Model.channel);
			}
			Model.lastNote = degree;
		}
		this.degree = degree;
	}

	@Override
	public void run() {
		Model.threadCount++;
		try {
			Model.synthesizer.open();
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.playNote();
	}

	public void playNote() {
		if(isNote) {
			//do nothing
		}else {
			if(this.inBetween(0, 30, degree) || this.inBetween(150, 180, degree) || degree == 360) {
				this.addToData(degree, 4);
			}else if(this.inBetween(30, 60, degree) || this.inBetween(120, 150, degree)) {
				this.addToData(degree, 3);
			}else if(this.inBetween(60, 120, degree)) {
				this.addToData(degree, 5);
			}else if(this.inBetween(180, 210, degree) || this.inBetween(330, 360, degree)) {
				this.addToData(degree, -4);
			}else if(this.inBetween(210, 240, degree) || this.inBetween(300, 330, degree)) {
				this.addToData(degree, -3);
			}else if(this.inBetween(240, 300, degree)) {
				this.addToData(degree, -5);
			}
		}

		Model.midiChannel[Model.channel].noteOn(Model.lastNote, 100);

		try {
			Thread.sleep(200); 
			Thread.getAllStackTraces();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			Model.midiChannel[Model.channel].noteOn(Model.lastNote, 0);
			Thread.currentThread().interrupt();
			if(Thread.interrupted()) {
				Model.threadCount--;
			}

		}
		System.out.println(Model.channel);

	}

	private void addToData(int data, int inc) {
		synchronized(Model.data) {
			Model.lastNote = Model.lastNote + inc;
			Model.data.put(Model.lastNote, Model.channel);
		}
	}
	private boolean inBetween(int x, int y, int c) {
		if(c >= x && c < y) {
			return true;
		}
		return false;
	}

	public void playAll() {
		synchronized(Model.data) {
			for(int i : Model.data.keySet()) {
				Model.midiChannel[Model.data.get(i)].noteOn(i, 100);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally {
					Model.midiChannel[Model.data.get(i)].noteOn(i, 0);
				}
			}
		}
	}
}