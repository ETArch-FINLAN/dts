package br.ufu.facom.network.dts.topology;

public class Link {
	private double usage;
	private double coast;
	
	private Port peerA;
	private Port peerB;
	
	public double getUsage() {
		return usage;
	}
	public void setUsage(double usage) {
		this.usage = usage;
	}
	public double getCoast() {
		return coast;
	}
	public void setCoast(double coast) {
		this.coast = coast;
	}
	public Port getPeerA() {
		return peerA;
	}
	public void setPeerA(Port peerA) {
		this.peerA = peerA;
	}
	public Port getPeerB() {
		return peerB;
	}
	public void setPeerB(Port peerB) {
		this.peerB = peerB;
	}
}
