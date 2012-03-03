package br.ufu.facom.network.dts.util;

import java.util.Vector;

public class VlanDatabase {
	private Vector<Integer> vlansInUse;
	private int intervInit;
	private int interfFinish;
	
	public VlanDatabase(int intervInit, int interfFinish){
		this.intervInit = intervInit;
		this.interfFinish = interfFinish;
		this.vlansInUse = new Vector<Integer>();
	}
	
	public Integer requestVlan(){
		for(int i=intervInit; i<interfFinish; i++){
			if(!vlansInUse.contains(i))
				return i;
		}
		return null;
	}
	
	public void releaseVlan(Integer vlanId){
		if(vlanId != null)
			vlansInUse.remove(vlanId);
	}
	
}
