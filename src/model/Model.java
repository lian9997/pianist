package model;

import javax.sound.midi.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Model {
	private static final double VERSION = 2.1;

	static protected Synthesizer synthesizer;
	static protected MidiChannel[] midiChannel;
	static protected int threadCount = 0;
	static protected int channel;
	static protected int lastNote = 0;
	private Instrument instruments[];
	static protected LinkedList<DataStruct> data;
	static int channelIndex = 0;

	public Model() {
		data = new LinkedList<DataStruct>();


		try {
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
			midiChannel = synthesizer.getChannels();
			instruments = synthesizer.getAvailableInstruments();
		}catch(Exception e) {
			e.printStackTrace();
		}
		midiChannel[0].programChange(0);
		midiChannel[1].programChange(32);
		midiChannel[2].programChange(34);
		midiChannel[3].programChange(38);
		midiChannel[4].programChange(39);
		channel = 0;

	}

	synchronized public void setOriginNote(int midiNote) {
		PlayThread tmp = new PlayThread(midiNote, true);
		Thread threadOrg = new Thread(tmp);
		threadOrg.start();
		Thread.currentThread().interrupt();
		Thread.interrupted();
	}

	/* start a new thread to play the sound */
	synchronized public void nextStep(double degree) {

		int degreeTmp = (int)(Math.floor(degree));
		PlayThread tmp = new PlayThread(degreeTmp, false);
		Thread thread = new Thread(tmp);
		thread.start();
		Thread.currentThread().interrupt();
		Thread.interrupted();
	}

	public String getInstrumentName(int a) {
		String original = instruments[a].toString();
		String instruName = "";

		int first = original.indexOf(':');
		if(first == -1) {
			return null;
		}
		int second = original.indexOf("bank");
		if(second == -1) {
			return null;
		}

		instruName = original.substring(first + 2, second - 1);

		return instruName;
	}

	public void setInstrucment(int q) {
		switch (q) {
		case 0:
			channel = 0;
			break;
		case 32:
			channel = 1;
			break;
		case 34:
			channel = 2;
			break;
		case 38:
			channel = 3;
			break;
		case 39:
			channel = 4;
			break;
		default:
			channel = 0;

		}
	}

	public void saveAudio() {
		try {
			synthesizer.open();
		} catch (MidiUnavailableException e1) {
			e1.printStackTrace();
		}
		int i = 1;
		String name = "saved_aduio.midi";
		File a = new File(name);
		while(a.exists()) {
			name = "saved_audio" + (i++) + ".midi";
			a = new File(name);
		}
		try {
			Sequence sequence = new Sequence(Sequence.PPQ, 1);
			Track track = sequence.createTrack();
			MidiMessage message = new ShortMessage(ShortMessage.START);
			MidiEvent event1 = new MidiEvent(message, 0);
			track.add(event1);
			int time = 1;
			for(DataStruct c : data) {
				System.out.println(c);
				MidiMessage messageTmp = new ShortMessage(ShortMessage.NOTE_ON, c.getChannel(), c.getNote(), 100);
				MidiEvent eventTmp = new MidiEvent(messageTmp, time++);
				track.add(eventTmp);
			}
			MidiMessage messageEnd = new ShortMessage(ShortMessage.STOP);
			MidiEvent eventEnd = new MidiEvent(messageEnd, time);
			track.add(eventEnd);
			try {
				MidiSystem.write(sequence, 0, a);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}


	}
	//TODO fix this
	synchronized public void release() {
		System.out.println(threadCount);

		while(threadCount > 0) {
			Thread.currentThread().interrupt();
			System.out.println(threadCount);
			if(Thread.interrupted()) {
				Thread.currentThread().interrupt();
				if(Thread.interrupted()){
					threadCount--;
				}
			}
		}
		threadCount = 0;
	}
	synchronized public void playAll() {
		PlayThread tmp = new PlayThread();
		tmp.playAll();
	}
}
