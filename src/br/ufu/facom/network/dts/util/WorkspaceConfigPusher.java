package br.ufu.facom.network.dts.util;

import br.ufu.facom.network.dts.bean.core.Workspace;
import br.ufu.facom.network.dts.topology.Port;

import java.util.Set;
import java.util.Vector;

public interface WorkspaceConfigPusher {
	public abstract void removePortFromWorkspace(Workspace ws, Port port);
	
	public abstract void addPortToWorkspace(Workspace ws, Port port);
	
	void recreateWorkspace(Workspace ws, Set<Port> set);

	public abstract void deleteWorkspace(Workspace workspace);
}
