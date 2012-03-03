package br.ufu.facom.network.dts.bean.core;

import java.util.Vector;

import br.ufu.facom.network.dts.topology.Host;
import br.ufu.facom.network.dts.topology.Port;

public class Workspace extends Entity{
	private Entity owner;
	private boolean publicAccess;
	
	private Vector<Entity> members = new Vector<Entity>();
	
	private int id=-1;
	
	public Vector<Entity> getMembers() {
		return members;
	}
	public void setMembers(Vector<Entity> members) {
		this.members = members;
	}
	public Entity getOwner() {
		return owner;
	}
	public void setOwner(Entity owner) {
		this.owner = owner;
	}
	public boolean isPublic() {
		return publicAccess;
	}
	public void setPublic(boolean public1) {
		publicAccess = public1;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id){
		this.id = id;
	}
	
	
	public void addMember(Entity entity) {
		members.add(entity);
	}
	public boolean containsMember(Entity entity) {
		return members.contains(entity);
	}
	public void removeMember(Entity entity) {
		members.remove(entity);
	}
}
