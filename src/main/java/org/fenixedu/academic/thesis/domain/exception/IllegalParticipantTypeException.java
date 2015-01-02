package org.fenixedu.academic.thesis.domain.exception;

import org.fenixedu.bennu.core.domain.User;

public class IllegalParticipantTypeException extends Exception {

    private User user;

    public IllegalParticipantTypeException(User user) {
	this.user = user;
    }

    public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
    }

}
