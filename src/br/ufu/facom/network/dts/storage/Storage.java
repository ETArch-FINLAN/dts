package br.ufu.facom.network.dts.storage;

import java.util.Collection;

import br.ufu.facom.network.dts.bean.core.Entity;
import br.ufu.facom.network.dts.bean.core.Workspace;
import br.ufu.facom.network.dts.topology.Host;
import br.ufu.facom.network.dts.topology.Link;
import br.ufu.facom.network.dts.topology.Port;
import br.ufu.facom.network.dts.topology.Switch;

public interface Storage {

	/**
	 * Switch
	 */
	boolean containsSwitch(String title);

	void addSwitch(String title, Switch switch1);

	Switch getSwitch(String title);

	void deleteSwitch(String title);

	
	/**
	 * Host
	 */

	boolean containsHost(String title);

	void addHost(String title, Host host);

	Host getHost(String title);

	void deleteHost(String title);

	Host getHostBySwitchAndPort(String titleSw, String inPort);
	
	/**
	 * Link
	 */
	
	boolean addLink(Link link);
	
	boolean deleteLink(Link link);

	/**
	 * Entity
	 */
	boolean containsEntity(String title);
	
	void addEntity(String title, Entity host);

	Entity getEntity(String title);

	void deleteEntity(String title);

	Host getHostByEntity(Entity entity);

	void addEntityToHost(Entity entity, Host host);

	/**
	 * Workspace
	 */
	boolean containsWorkspace(String titleWs);

	void addWorkspace(String titleWs, Workspace workspace);

	Workspace getWorkspace(String titleWs);

	void addAtachmentHost(Port port, Host host);

	void updateWorkspace(Workspace workspace);

	void deleteWorkspace(Workspace workspace);

	void deleteAtachmentHost(Port port, Host host);

	Collection<Switch> getSwitches();
}
