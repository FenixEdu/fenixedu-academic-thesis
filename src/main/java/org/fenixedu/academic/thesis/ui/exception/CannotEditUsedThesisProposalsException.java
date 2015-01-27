package org.fenixedu.academic.thesis.ui.exception;

import org.fenixedu.academic.thesis.domain.ThesisProposal;

public class CannotEditUsedThesisProposalsException extends ThesisProposalException {

    private static final long serialVersionUID = -4965296880371661815L;
    private ThesisProposal thesisProposal;

    public CannotEditUsedThesisProposalsException(ThesisProposal thesisProposal) {
        this.thesisProposal = thesisProposal;
    }

    public ThesisProposal getThesisProposal() {
        return thesisProposal;
    }

    public void setThesisProposal(ThesisProposal thesisProposal) {
        this.thesisProposal = thesisProposal;
    }

}