package br.ufu.facom.network.dts.storage;

import br.ufu.facom.network.dts.bean.core.Entity;
import br.ufu.facom.network.dts.bean.core.Workspace;
import br.ufu.facom.network.dts.topology.Host;
import br.ufu.facom.network.dts.topology.Link;
import br.ufu.facom.network.dts.topology.Port;
import br.ufu.facom.network.dts.topology.Switch;

public interface StorageListener {

	/**
	 * Switch
	 */

	void onAddSwitch(String title, Switch switch1);

	void onDeleteSwitch(String title);

	
	/**
	 * Host
	 */
	void onAddHost(String title, Host host);

	void onDeleteHost(String title);
	
	void onAddAtachmentHost(Port port, Host host);
	
	void onDeleteAtachmentHost(Port port, Host host);

	/**
	 * Link
	 */
	
	void onAddLink(Link link);
	
	void onDeleteLink(Link link);

	/**
	 * Entity
	 */
	
	void onAddEntity(String title, Entity host);

	void onDeleteEntity(String title);

	void onAddEntityToHost(Entity entity, Host host);

	/**
	 * Workspace
	 */
	void onAddWorkspace(String titleWs, Workspace workspace);

	void onUpdateWorkspace(Workspace workspace);

	void onDeleteWorkspace(Workspace workspace);
}
