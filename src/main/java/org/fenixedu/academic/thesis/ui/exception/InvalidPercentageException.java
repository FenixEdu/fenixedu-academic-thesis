package org.fenixedu.academic.thesis.ui.exception;

public class InvalidPercentageException extends ThesisProposalException {

    private int percentage;

    public InvalidPercentageException(int percentage) {
        this.setPercentage(percentage);
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

}
