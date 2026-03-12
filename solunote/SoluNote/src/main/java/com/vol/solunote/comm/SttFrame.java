package com.vol.solunote.comm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SttFrame {

	private String channelId;
	private float start;
	private float end;
	private int confidence;
	private String text;

	public SttFrame(String channel, double start, double end, int confidence, String text) {
				this(channel, (float)start, (float)end, confidence, text);
	}
	
	public SttFrame(String channelId, float start, float end, int confidence, String text) {
		this.channelId = channelId;
		this.start = start;
		this.end = end;
		this.confidence = confidence;
		this.text = text;
	}

	@Override
	public String toString() {
		return "SttFrame [channel=" + channelId + ", start=" + start + ", end=" + end + ", confidence=" + confidence
				+ ", text=" + text + "]";
	}	
}

