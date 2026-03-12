package com.vol.solunote.comm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SttFrameManager {
	
	private List<SttFrame> list = new ArrayList<>();
	private boolean sorted = false;

//	public SttFrameManager(List<SttFrame> collection) {
//		super();
//		this.list = collection.stream().sorted( Comparator.comparingDouble(SttFrame::getStart).thenComparing(SttFrame::getChannel) ).collect(Collectors.toList());;
//	}

	public SttFrameManager() {
		super();
	}

	public void printAll() {
		sortList();
		this.list.stream().forEach(System.out::println);
	}

	private void sortList() {
		if ( this.sorted == true ) {
			return;
		}
		
		this.list = this.list.stream().sorted( Comparator.comparingDouble(SttFrame::getStart).thenComparing(SttFrame::getChannelId) ).collect(Collectors.toList());;
		this.sorted = true;
	}

	public void add(SttFrame sf) {
		list.add(sf);		
	}

	public List<SttFrame> getFrameList() {
		sortList();
		return this.list;
	}
	
}
