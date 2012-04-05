package br.ufu.facom.network.dts.route;


import java.util.HashSet;
import java.util.Set;

import br.ufu.facom.network.dts.bean.core.Workspace;
import br.ufu.facom.network.dts.storage.Storage;
import br.ufu.facom.network.dts.topology.Host;
import br.ufu.facom.network.dts.topology.Port;
import br.ufu.facom.network.dts.topology.Switch;

public class SimpleSpanningTreeCreator extends SpanningTreeCreator{

	public SimpleSpanningTreeCreator(Storage storage) {
		super(storage);
	}

	@Override
	public Set<Port> findPath(Workspace workspace) {
//		System.out.println("Workspace:");
//		
//		HashSet<Switch> switches = new HashSet<Switch>();
//		HashSet<Port> ports = new HashSet<Port>();
//		for(Host host : getHosts(workspace)){
//			for(Port access : host.getAccessPorts()){
//				System.out.println(access.getParent().getTitle()+" : "+access.getName());
//				ports.add(access);
//				switches.add(access.getParent());
//			}
//		}
//		
//		for(Switch sw : switches){
//			for(Port port : sw.getPorts()){
//				if(port.getLink() != null){
//					Port otherPort = (port.getLink().getPeerA().equals(port))? port.getLink().getPeerB() : port;
//					
//					if(otherPort != null){
//						if(switches.contains(otherPort.getParent()))
//							ports.add(port);
//					}
//				}
//			}
//		}
//		
//		System.out.println("Workspace:");
//		for(Port port : ports){
//			System.out.println(port.getParent().getTitle()+" : "+port.getName());
//		}
//		return ports;
		
		
		HashSet<Port> ports = new HashSet<Port>();
		for(Host host : getHosts(workspace)){
			for(Port access : host.getAccessPorts()){
				ports.add(access);
			}
		}
		
		for(Switch sw : storage.getSwitches()){
			for(Port port : sw.getPorts()){
				if(port.getLink() != null){
					System.out.println(port.getLink().getPeerA().getParent().getTitle()+" : "+port.getLink().getPeerA().getName());
					System.out.println(port.getLink().getPeerB().getParent().getTitle()+" : "+port.getLink().getPeerB().getName());
					ports.add(port.getLink().getPeerA());
					ports.add(port.getLink().getPeerB());
				}
			}
		}
		
		System.out.println("Workspace:");
		for(Port port : ports){
			System.out.println(port.getParent().getTitle()+" : "+port.getName());
		}
		return ports;
		
	}

}
