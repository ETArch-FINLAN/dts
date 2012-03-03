package br.ufu.facom.network.dts.topology;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.ufu.facom.network.dts.bean.core.Entity;

public class Switch extends Entity{
	protected Map<String,Port> ports = new HashMap<String,Port>();


	public Switch(String title) {
		this.title = title;
	}
	
	public Collection<Port> getPorts() {
		return ports.values();
	}
	public Port getPort(String name) {
		return this.ports.get(name);
	}
	public void addPort(Port port) {
		port.setParent(this);
		this.ports.put(port.getName(),port);
	}
	public void addPort(String name, int index) {
		Port port = new Port();
		port.setName(name);
		port.setIndex(index);
		
		this.addPort(port);
	}

	public boolean containsPort(String portName) {
		return this.ports.containsKey(portName);
	}

	public void removePort(String portId) {
		this.ports.remove(portId);
	}
}
