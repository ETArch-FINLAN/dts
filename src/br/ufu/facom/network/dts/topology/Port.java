package br.ufu.facom.network.dts.topology;


public class Port {
	private String name;
	private int index;
	private Switch parent;
	private Link link;
	private Host accessHost;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Switch getParent() {
		return parent;
	}
	public void setParent(Switch parent) {
		this.parent = parent;
	}
	public Link getLink() {
		return link;
	}
	public void setLink(Link link) {
		this.link = link;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index){
		this.index = index;
	}
	public void setAccessHost(Host host) {
		this.accessHost = host;
	}
	public Host getAccessHost() {
		return this.accessHost;
	}
}
