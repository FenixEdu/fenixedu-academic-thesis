package org.fenixedu.academic.thesis.ui.bean;

import org.fenixedu.bennu.core.domain.User;

public class ThesisProposalParticipantBean {

    private User user;
    private String participantTypeExternalId;

    public String getUserExternalId() {
	return user.getExternalId();
    }

    public void setUser(User user) {
	this.user = user;
    }

    public String getParticipantTypeExternalId() {
	return participantTypeExternalId;
    }

    public void setParticipantTypeExternalId(String participantType) {
	this.participantTypeExternalId = participantType;
    }

    public ThesisProposalParticipantBean(User user, String participantType) {
	setUser(user);
	this.participantTypeExternalId = participantType;
    }

    public ThesisProposalParticipantBean() {
    }

    public User getUser() {
	return user;
    }

}
