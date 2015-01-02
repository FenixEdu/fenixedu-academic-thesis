package org.fenixedu.academic.thesis.domain.exception;

import org.fenixedu.academic.thesis.domain.ThesisProposalParticipant;

public class MaxNumberThesisProposalsException extends Exception {

    private final ThesisProposalParticipant participant;

    public ThesisProposalParticipant getParticipant() {
	return participant;
    }

    public MaxNumberThesisProposalsException(ThesisProposalParticipant participant) {
	this.participant = participant;
    }

}
