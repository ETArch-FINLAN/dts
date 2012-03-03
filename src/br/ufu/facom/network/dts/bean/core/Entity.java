package br.ufu.facom.network.dts.bean.core;

import java.util.ArrayList;
import java.util.List;

public class Entity {
	protected String title;
	protected List<Requirement> requirements;
	
	public Entity(){
		this.requirements = new ArrayList<Requirement>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Requirement> getRequirements() {
		return requirements;
	}

	public void addRequirements(Requirement requirement) {
		this.requirements.add(requirement);
	}

	public void setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
	}
}
