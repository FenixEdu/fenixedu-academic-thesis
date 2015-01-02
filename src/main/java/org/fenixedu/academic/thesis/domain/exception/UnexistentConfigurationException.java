package org.fenixedu.academic.thesis.domain.exception;

import org.fenixedu.academic.domain.ExecutionDegree;

public class UnexistentConfigurationException extends Exception {

    private ExecutionDegree executionDegree;

    public ExecutionDegree getExecutionDegree() {
	return executionDegree;
    }

    public void setExecutionDegree(ExecutionDegree executionDegree) {
	this.executionDegree = executionDegree;
    }

    public UnexistentConfigurationException(ExecutionDegree executionDegree) {
	this.executionDegree = executionDegree;
    }

}
