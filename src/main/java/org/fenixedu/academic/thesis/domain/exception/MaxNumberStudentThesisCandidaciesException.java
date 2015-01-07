package org.fenixedu.academic.thesis.domain.exception;

import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.student.Student;

public class MaxNumberStudentThesisCandidaciesException extends DomainException {

    private static final long serialVersionUID = 3422026637712388229L;
    private final Student student;

    public MaxNumberStudentThesisCandidaciesException(Student student) {
	super();
	this.student = student;
    }

    public Student getStudent() {
	return student;
    }

}
