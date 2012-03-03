package br.ufu.facom.network.dts.service;

import br.ufu.facom.network.dlontology.msg.Message;
import br.ufu.facom.network.dts.bean.core.Entity;
import br.ufu.facom.network.dts.bean.core.Workspace;
import br.ufu.facom.network.dts.core.message.Service;
import br.ufu.facom.network.dts.core.message.ServiceRequest;
import br.ufu.facom.network.dts.core.message.ServiceResponse;
import br.ufu.facom.network.dts.storage.Storage;
import br.ufu.facom.network.dts.topology.Host;
import br.ufu.facom.network.dts.util.VlanDatabase;
import br.ufu.facom.network.dts.util.WorkspaceConfigPusher;
import br.ufu.facom.network.dts.util.route.SimpleSpanningTreeCreator;

public class ServiceMessageProcessor {
	protected Storage storage;
	protected WorkspaceConfigPusher workspaceConfigPusher;
	protected VlanDatabase vlanDatabase = new VlanDatabase(100,200);
	
	//Minium Spanning-Tree graph... based on Kruskal or Prim
	protected SimpleSpanningTreeCreator routingGraphCreator;
	
	public ServiceMessageProcessor(Storage storage){
		this.storage = storage;
		this.routingGraphCreator = new  SimpleSpanningTreeCreator(storage);
	}
	
	public ServiceResponse processServiceMessage(Message dlmessage, ServiceRequest message, Host host){
		switch(message.getService()){
			case ENTITY_REGISTER: 		return entityRegister(message,host);
			case ENTITY_MODIFY: 		return entityModify(message);
			case ENTITY_UNREGISTER: 	return entityUnregister(message);
			case WORKSPACE_LOOKUP: 		return workspaceLookup(message);
			case WORKSPACE_ADVERTISE:	return workspaceAdvertise(message);
			case WORKSPACE_INVITION:	return workspaceInvitation(message);
			case WORKSPACE_ATTACH: 		return workspaceAttach(message,dlmessage.getSource());
			case WORKSPACE_DETACH: 		return workspaceDetach(message,dlmessage.getSource());
			case WORKSPACE_MODIFY: 		return workspaceModify(message);
			case WORKSPACE_DELETE: 		return workspaceDelete(message,dlmessage.getSource());
			case WORKSPACE_PUBLISH: 	return workspacePublish(message);
			case WORKSPACE_CREATE: 		return workspaceCreate(message,dlmessage.getSource());
			default: throw new RuntimeException("Unknown service");
		}
	}

	private ServiceResponse workspaceCreate(ServiceRequest message, String titleSrc) {
		ServiceResponse response = new ServiceResponse(Service.WORKSPACE_CREATE);
		
		if(titleSrc != null && !titleSrc.isEmpty()){
			//TODO verificar identidade
			if(storage.containsEntity(titleSrc)){
				String titleWs = message.getProperty("title").toString();

				Entity entity = storage.getEntity(titleSrc);
				
				Workspace workspace = null;
				if(!storage.containsWorkspace(titleWs)){
					workspace = new Workspace();
					workspace.setOwner(entity);
					workspace.setTitle(titleWs);
					storage.addWorkspace(titleWs,workspace);
					response.setSucess(true);
				}else{
					response.setMessage("The workspace is already created.");
				}
			}else{
				response.setMessage("The title passed as parameter is not registered.");
			}
		}else{
			response.setMessage("Parameter missing! Add the 'title' to your request.");
		}
		
		return response;
	}

	private ServiceResponse workspacePublish(ServiceRequest message) {
		// TODO Auto-generated method stub
		return null;
	}

	private ServiceResponse workspaceDelete(ServiceRequest message,  String titleSrc) {
		ServiceResponse response = new ServiceResponse(Service.WORKSPACE_DELETE);
		
		if(titleSrc != null && !titleSrc.isEmpty()){
			//TODO verificar identidade
			if(storage.containsEntity(titleSrc)){
				String titleWs = message.getProperty("title").toString();

				if(storage.containsWorkspace(titleWs)){
					Workspace workspace = storage.getWorkspace(titleWs);
					if(workspace.getOwner().getTitle().equalsIgnoreCase(titleSrc)){
						if(workspace.getMembers().isEmpty()){
							response.setSucess(true);
							storage.deleteWorkspace(workspace);
						}else{
							response.setMessage("The workspace is not empty.");
						}
					}else{
						response.setMessage("You don't have permission to delete this workspace.");
					}
					
				}else{
					response.setMessage("The workspace is not created.");
				}
			}else{
				response.setMessage("The title passed as parameter is not registered.");
			}
		}else{
			response.setMessage("Parameter missing! Add the 'title' to your request.");
		}
		
		return response;
	}

	private ServiceResponse workspaceModify(ServiceRequest message) {
		// TODO Auto-generated method stub
		return null;
	}

	private ServiceResponse workspaceDetach(ServiceRequest message, String titleSrc) {
		ServiceResponse response = new ServiceResponse(Service.WORKSPACE_DETACH);
		
		if(titleSrc != null && !titleSrc.isEmpty()){
			//TODO verificar identidade
			if(storage.containsEntity(titleSrc)){
				String titleWs = message.getProperty("title").toString();

				Entity entity = storage.getEntity(titleSrc);
				
				Workspace workspace = null;
				if(storage.containsWorkspace(titleWs)){
					workspace = storage.getWorkspace(titleWs);
				
					if(workspace.containsMember(entity)){
						workspace.removeMember(entity); 
		
						if(!workspace.getMembers().isEmpty()){
							if(workspace.getOwner().getTitle().equalsIgnoreCase(titleSrc)){
								workspace.setOwner(workspace.getMembers().get(0));
							}
						}	
						
						Object returnValue = reconfigureWorkspace(workspace);
						if(returnValue != null){
							response.setSucess(true);
						}
						//}else{
						//	response.setSucess(true);
						//	deleteWorkspace(workspace);
						//}
					}else{
						response.setMessage("The entity passed as parameter is not attached to the workspace.");
					}
				}else{
					response.setMessage("The workspace passed as paramenter does not exists.");
				}
			}else{
				response.setMessage("The title passed as parameter is not registered.");
			}
		}else{
			response.setMessage("Parameter missing! Add the 'title' to your request.");
		}
		
		return response;
	}

	private ServiceResponse workspaceAttach(ServiceRequest message, String titleSrc) {
		ServiceResponse response = new ServiceResponse(Service.WORKSPACE_ATTACH);
		
		if(titleSrc != null && !titleSrc.isEmpty()){
			//TODO verificar identidade
			if(storage.containsEntity(titleSrc)){
				String titleWs = message.getProperty("title").toString();

				Entity entity = storage.getEntity(titleSrc);
				
				if(storage.containsWorkspace(titleWs)){
					Workspace workspace = storage.getWorkspace(titleWs);
					
					if(!workspace.containsMember(entity)){
						workspace.addMember(entity); 
		
						Object returnValue = reconfigureWorkspace(workspace);
						if(returnValue != null){
							response.setSucess(true);
							response.setReturnValue(returnValue);
						}
					}else{
						response.setMessage("The entity has already joined in  workspace.");
					}
				}else{
					response.setMessage("The workspace doesn't exists.");
				}			
			}else{
				response.setMessage("The title passed as parameter is not registered.");
			}
		}else{
			response.setMessage("Parameter missing! Add the 'title' to your request.");
		}
		
		return response;
	}

	private ServiceResponse workspaceInvitation(ServiceRequest message) {
		// TODO Auto-generated method stub
		return null;
	}

	private ServiceResponse workspaceAdvertise(ServiceRequest message) {
		// TODO Auto-generated method stub
		return null;
	}

	private ServiceResponse workspaceLookup(ServiceRequest message) {
		// TODO Auto-generated method stub
		return null;
	}

	private ServiceResponse entityUnregister(ServiceRequest message) {
		ServiceResponse response = new ServiceResponse(Service.ENTITY_UNREGISTER);
		
		String title = message.getProperty("title").toString();
		if(title != null && !title.isEmpty()){
			//TODO verificar identidade
			if(storage.containsEntity(title)){
				storage.deleteEntity(title);
				response.setSucess(true);
			}else{
				response.setMessage("The title passed as parameter is not registered.");
			}
		}else{
			response.setMessage("Parameter missing! Add the 'title' to your request.");
		}
		
		return response;
	}

	private ServiceResponse entityModify(ServiceRequest message) {
		// TODO Auto-generated method stub
		return null;
	}

	private ServiceResponse entityRegister(ServiceRequest message, Host host) {
		ServiceResponse response = new ServiceResponse(Service.ENTITY_REGISTER);
		
		String title = message.getProperty("title").toString();
		if(title != null && !title.isEmpty()){
			//TODO verificar identidade
			if(!storage.containsEntity(title)){
				Entity entity = new Entity();
				entity.setTitle(title);
				storage.addEntity(title,entity);
				storage.addEntityToHost(entity,host);
				response.setSucess(true);
			}else{
				response.setMessage("Title unavailable. Choose other title.");
			}
		}else{
			response.setMessage("Parameter missing! Add the 'title' to your request.");
		}
		
		return response;
	}

	private Integer reconfigureWorkspace(Workspace workspace){
		storage.updateWorkspace(workspace);
		if(workspaceConfigPusher != null){
			if(workspace.getId() == -1){
				Integer vlanId = vlanDatabase.requestVlan();
				if(vlanId != null)
					workspace.setId(vlanId);
				else
					return null;
			}
			
			workspaceConfigPusher.recreateWorkspace(workspace, routingGraphCreator.findPath(workspace));
			return workspace.getId();
		}
		return null;
	}
	
	public void setWorkspaceConfigPusher(WorkspaceConfigPusher workspaceConfigPusher) {
		this.workspaceConfigPusher = workspaceConfigPusher;
	}
}
