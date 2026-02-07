package com.example.taxone.service;

import com.example.taxone.dto.response.ProjectInvitationResponse;

public interface InvitationService {

    public ProjectInvitationResponse acceptProjectInvite(String projectId);
}
