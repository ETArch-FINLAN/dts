package br.ufu.facom.network.dts.storage;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import br.ufu.facom.network.dts.bean.core.Entity;
import br.ufu.facom.network.dts.bean.core.Workspace;
import br.ufu.facom.network.dts.topology.Host;
import br.ufu.facom.network.dts.topology.Link;
import br.ufu.facom.network.dts.topology.Port;
import br.ufu.facom.network.dts.topology.Switch;

public class MemoryStorage implements Storage{
	//Attributes
	private Map<String,Entity> entities = new ConcurrentHashMap<String,Entity>();
	private Map<String,Workspace> workspaces = new ConcurrentHashMap<String,Workspace>();
	private Map<String,Switch> switches = new ConcurrentHashMap<String,Switch>();
	private Map<String,Host> hosts = new ConcurrentHashMap<String,Host>();
	private Vector<Link> links = new Vector<Link>();
	private Map<Entity,Host> entityLocale = new ConcurrentHashMap<Entity,Host>();
	private Map<Port,Host> attachmentHosts = new ConcurrentHashMap<Port,Host>();

	private Vector<StorageListener> listeners = new Vector<StorageListener>();
	
	public void addStorageListener(StorageListener sl){
		listeners.add(sl);
	}
	
	@Override
	public boolean containsSwitch(String title) {
		return switches.containsKey(title);
	}
	@Override
	public void addSwitch(String title, Switch switch1) {
		switches.put(title, switch1);
		
		for(StorageListener sl : listeners)
			sl.onAddSwitch(title, switch1);
	}
	@Override
	public Switch getSwitch(String titleSwitch) {
		return switches.get(titleSwitch);
	}
	@Override
	public void deleteSwitch(String title) {
		switches.remove(title);
		
		for(StorageListener sl : listeners)
			sl.onDeleteSwitch(title);
	}
	@Override
	public boolean containsHost(String title) {
		return hosts.containsKey(title);
	}
	@Override
	public void addHost(String title, Host host) {
		hosts.put(title, host);
		
		for(StorageListener sl : listeners)
			sl.onAddHost(title, host);
	}
	@Override
	public Host getHost(String title) {
		return hosts.get(title);
	}
	@Override
	public void deleteHost(String title) {
		hosts.remove(title);
		
		for(StorageListener sl : listeners)
			sl.onDeleteSwitch(title);
	}
	@Override
	public boolean addLink(Link link) {
		for(StorageListener sl : listeners)
			sl.onAddLink(link);
		
				System.err.println("Adding a link");
				
		return links.add(link);
	}
	@Override
	public boolean deleteLink(Link link) {
		for(StorageListener sl : listeners)
			sl.onDeleteLink(link);
				
		return links.remove(link);
	}
	@Override
	public boolean containsEntity(String title) {
		return entities.containsKey(title);
	}
	@Override
	public void addEntity(String title, Entity host) {
		entities.put(title, host);
		for(StorageListener sl : listeners)
			sl.onAddEntity(title, host);
	}
	@Override
	public Entity getEntity(String title) {
		return entities.get(title);
	}
	@Override
	public void deleteEntity(String title) {
		for(StorageListener sl : listeners)
			sl.onDeleteEntity(title);

		Entity entity = entities.get(title);
		entityLocale.get(entity).removeEntity(entity);
		entityLocale.remove(entity);
		entities.remove(title);
	}
	
	@Override
	public Host getHostByEntity(Entity entity) {
		return entityLocale.get(entity);
	}
	
	@Override
	public void addEntityToHost(Entity entity, Host host) {
		entityLocale.put(entity, host);
		host.addEntity(entity);
		
		for(StorageListener sl : listeners)
			sl.onAddEntityToHost(entity, host);
	}
	@Override
	public void addWorkspace(String title, Workspace workspace) {
		workspaces.put(title, workspace);
		
		for(StorageListener sl : listeners)
			sl.onAddWorkspace(title, workspace);
	}
	@Override
	public boolean containsWorkspace(String title) {
		return workspaces.containsKey(title);
	}
	@Override
	public Workspace getWorkspace(String title) {
		return workspaces.get(title);
	}
	@Override
	public Host getHostBySwitchAndPort(String titleSw, String inPort) {
		return switches.get(titleSw).getPort(inPort).getAccessHost();
	}

	@Override
	public void addAtachmentHost(Port port, Host host) {
		attachmentHosts.put(port, host);

		for(StorageListener sl : listeners)
			sl.onAddAtachmentHost(port, host);
	}

	@Override
	public void updateWorkspace(Workspace workspace) {
		for(StorageListener sl : listeners)
			sl.onUpdateWorkspace(workspace);
	}

	@Override
	public void deleteWorkspace(Workspace workspace) {
		for(StorageListener sl : listeners)
			sl.onDeleteWorkspace(workspace);
		
		this.workspaces.remove(workspace.getTitle());
	}

	@Override
	public void deleteAtachmentHost(Port port, Host host) {
		port.setAccessHost(null);
		host.removeAccessPort(port);
		
		for(StorageListener sl : listeners)
			sl.onDeleteAtachmentHost(port, host);
	}
}
