package br.ufu.facom.network.dts.bean;

import br.ufu.facom.network.dts.bean.core.Entity;
import br.ufu.facom.network.dts.topology.Host;

public class Application extends Entity{
	private Host hostContainner;

	public Host getHostContainner() {
		return hostContainner;
	}

	public void setHostContainner(Host hostContainner) {
		this.hostContainner = hostContainner;
	}
	
	
}