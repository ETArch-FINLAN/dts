package br.ufu.facom.network.dts.route;

import java.util.HashSet;
import java.util.Set;

import br.ufu.facom.network.dts.bean.core.Entity;
import br.ufu.facom.network.dts.bean.core.Workspace;
import br.ufu.facom.network.dts.storage.Storage;
import br.ufu.facom.network.dts.topology.Host;
import br.ufu.facom.network.dts.topology.Port;

public abstract class SpanningTreeCreator {
	protected Storage storage;
	
	public SpanningTreeCreator(Storage storage) {
		this.storage = storage;
	}

	public abstract Set<Port> findPath(Workspace workspace);

	protected HashSet<Host> getHosts(Workspace workspace) {
		HashSet<Host> hosts = new HashSet<Host>();
		for(Entity entity : workspace.getMembers()){
			hosts.add(storage.getHostByEntity(entity));
		}
		
		return hosts;
	}
}
