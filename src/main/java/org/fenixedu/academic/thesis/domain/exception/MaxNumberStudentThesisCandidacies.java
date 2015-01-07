package org.fenixedu.academic.thesis.domain.exception;

import org.fenixedu.academic.domain.student.Student;

public class MaxNumberStudentThesisCandidacies extends Exception {

    private static final long serialVersionUID = 3422026637712388229L;
    private final Student student;

    public MaxNumberStudentThesisCandidacies(Student student) {
	super();
	this.student = student;
    }

    public Student getStudent() {
	return student;
    }

}
