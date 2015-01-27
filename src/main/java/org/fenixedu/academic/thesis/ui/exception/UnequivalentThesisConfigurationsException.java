package org.fenixedu.academic.thesis.ui.exception;

import org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration;

public class UnequivalentThesisConfigurationsException extends ThesisProposalException {

    private static final long serialVersionUID = -4270028206922579262L;
    private ThesisProposalsConfiguration configuration0;
    private ThesisProposalsConfiguration configuration1;

    public ThesisProposalsConfiguration getConfiguration0() {
        return configuration0;
    }

    public void setConfiguration0(ThesisProposalsConfiguration configuration0) {
        this.configuration0 = configuration0;
    }

    public ThesisProposalsConfiguration getConfiguration1() {
        return configuration1;
    }

    public void setConfiguration1(ThesisProposalsConfiguration configuration1) {
        this.configuration1 = configuration1;
    }

    public UnequivalentThesisConfigurationsException(ThesisProposalsConfiguration configuration0,
            ThesisProposalsConfiguration configuration1) {
        this.configuration0 = configuration0;
        this.configuration1 = configuration1;
    }

}