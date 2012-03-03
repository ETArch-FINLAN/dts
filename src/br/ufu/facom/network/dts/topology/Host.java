package br.ufu.facom.network.dts.topology;

import java.util.ArrayList;
import java.util.List;

import br.ufu.facom.network.dts.bean.core.Entity;

public class Host extends Entity{
	private List<Entity> applications = new ArrayList<Entity>();
	private List<Port> accessPorts = new ArrayList<Port>();
	
	public Host(String title) {
		this.title = title;
	}
	
	public List<Entity> getEntities(){
		return applications;
	}
	
	public void addEntity(Entity entity){
		this.applications.add(entity);
	}
	
	public List<Port> getAccessPorts(){
		return accessPorts;
	}
	
	public void addAccessPort(Port accessPort){
		this.accessPorts.add(accessPort);
	}

	public void removeAccessPort(Port port) {
		this.accessPorts.remove(port);
	}

	public void removeEntity(Entity entity) {
		this.applications.remove(entity);
	}
}
