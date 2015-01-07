package org.fenixedu.academic.thesis.domain.exception;

import org.fenixedu.bennu.core.domain.User;

public class IllegalParticipantTypeException extends Exception {

    private static final long serialVersionUID = -3114050449816099494L;
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
