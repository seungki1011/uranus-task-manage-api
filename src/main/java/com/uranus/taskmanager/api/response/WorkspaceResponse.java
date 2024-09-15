package com.uranus.taskmanager.api.response;

import com.uranus.taskmanager.api.domain.workspace.Workspace;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WorkspaceResponse {

	private final Long id;
	private final String workspaceCode;
	private final String name;
	private final String description;

	@Builder
	public WorkspaceResponse(Long id, String workspaceCode, String name, String description) {
		this.id = id;
		this.workspaceCode = workspaceCode;
		this.name = name;
		this.description = description;
	}

	public static WorkspaceResponse fromEntity(Workspace workspace) {
		return WorkspaceResponse.builder()
			.id(workspace.getId())
			.name(workspace.getName())
			.description(workspace.getDescription())
			.workspaceCode(workspace.getWorkspaceCode())
			.build();
	}

}
